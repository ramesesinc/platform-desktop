
package com.rameses.osiris2.reports;

import com.rameses.common.PropertyResolver;
import com.rameses.util.Base64Cipher;
import com.rameses.util.QRCodeUtil;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRRewindableDataSource;

public class ReportDataSource implements JRRewindableDataSource {
    
    protected Iterator iterator;
    protected Object currentObject;
    
    private Object source;
    private PropertyResolver propertyResolver;
    private ReportDataSourceHelper helper;
    
    public ReportDataSource(Object source) {    
        setSource( source ); 
        propertyResolver = PropertyResolver.getInstance();
    }
    
    public ReportDataSourceHelper getHelper() {
        return helper; 
    }
       
    public Object getSource() { return source; } 
    public void setSource(Object src) { 
        this.source = src; 
        this.helper = new ReportDataSourceHelper( this.source );
        reset();
    }
    
    private void reset() { 
        Object src = getSource(); 
        if ( src == null ) { 
            iterator = (new ArrayList()).iterator();
        } else if( src instanceof Collection ) {
            iterator = ((Collection)src).iterator();
        } else {
            List l = new ArrayList();
            l.add( src );
            iterator = l.iterator();
        } 
    } 
    
    public void moveFirst() throws JRException {
        reset(); 
    }

    public boolean next() throws JRException {
        if( iterator.hasNext() ) {
            currentObject = iterator.next();
            return true;
        } else {
            return false;
        }
    }
    
    //added in getFieldValue. If value is byte[] then we need to convert this to InputStream.
    //This is very common in images.
    public Object getFieldValue(JRField jRField) throws JRException {
        Object value = null;
        String fieldName = null;
        try {
            fieldName = jRField.getName();
            if ( "_source".equals( fieldName )) {
                return new ReportDataSource( getSource());
            }
            
            value = propertyResolver.getProperty(currentObject, fieldName);
            if ( value == null ) {
                return null;
            }
            if (value.getClass() == byte[].class ) {
                return new ByteArrayInputStream((byte[]) value);
            }
            else if( value.getClass() == String.class ) {
                
                if ( java.util.Date.class.isAssignableFrom( jRField.getValueClass())) {
                    return convertDateObject( jRField.getValueClass(), value.toString() ); 
                }
                
                String str = value.toString(); 
                if ( str.startsWith("qrcode:")) {
                    String newstr = str.substring(7);
                    byte[] bytes =  QRCodeUtil.generateQRCode( newstr );
                    return new ByteArrayInputStream( bytes );
                }
                else if ( str.startsWith("image:")) {
                    String[] arr = str.substring(6).split(",");
                    String encstr = (arr.length > 1 ? arr[1] : arr[0]); 
                    String type = (arr.length > 1 ? arr[0] : ""); 
                    
                    Object obj = new Base64Cipher().decode( encstr, false );
                    byte[] bytes = (byte[]) obj;
                    if ( "url".equalsIgnoreCase( type )) { 
                        BytesHandler handler = new BytesHandler( bytes ); 
                        return new URL( null, "bytes:///", handler );
                    } 
                    else {
                        return new ByteArrayInputStream( bytes );
                    }
                }
                else  {
                    return str;
                }
            }
            else if( jRField.getValueClass().isAssignableFrom( Collection.class) ) {
                return new ReportDataSource( value );
            } 
            else { 
                return value;
            }
        } 
        catch(Throwable ex) {
            System.out.println("Error on field [" + fieldName  + "] caused by " + ex.getMessage());
            ex.printStackTrace();
            return null; 
        } 
    }  
    
    
    private SimpleDateFormat YMD = new SimpleDateFormat("yyyy-MM-dd"); 
    private SimpleDateFormat YMD_HMS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
    
    private Object convertDateObject( Class targetClass, String value ) { 
        try {
            return new java.sql.Timestamp( YMD_HMS.parse( value ).getTime());
        } catch (ParseException ex) {
            //do nothing 
        }
        try {
            return new java.sql.Date( YMD.parse(value).getTime() ); 
        } catch (ParseException ex) {
            ex.printStackTrace(); 
            return null; 
        }
    }
    
    private class BytesHandler extends java.net.URLStreamHandler {

        private byte[] bytes; 
        
        public BytesHandler( byte[] bytes ) {
            this.bytes = bytes; 
        }
        
        protected URLConnection openConnection(URL u) throws IOException {
            BytesConnection bc = new BytesConnection(u); 
            bc.handler = this; 
            return bc; 
        }

        public String getProtocol() {
            return "bytes";
        }

        public URL getResource(String spath) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
    
    private class BytesConnection extends URLConnection {
        
        private BytesHandler handler;
        
        public BytesConnection(URL url) {
            super(url);
        }

        public void connect() throws IOException {
        }

        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream( handler.bytes ); 
        }
    }    
}
