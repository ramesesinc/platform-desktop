/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris2.reports;

import com.rameses.io.IOStream;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.util.Base64Cipher;
import com.rameses.util.Encoder;
import com.rameses.util.URLStreamHandlers;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author wflores
 */
public class ReportURLStreamHandlerFactory implements URLStreamHandlerFactory {
        
    private URLStreamHandlerFactory source;
    private File developerCustomFolder; 
    private boolean developerMode;
    
    public ReportURLStreamHandlerFactory() {
        this.source = URLStreamHandlers.getFactory(); 
    }
    
    public File getDeveloperCustomFolder() { return developerCustomFolder; } 
    public void setDeveloperCustomFolder( File developerCustomFolder ) {
        this.developerCustomFolder = developerCustomFolder; 
    }
    
    public boolean isDeveloperMode() { return developerMode; }
    void setDeveloperMode(boolean developerMode) {
        this.developerMode = developerMode; 
    }
        
    public ClassLoader getClassLoader() { 
        ClientContext ctx = ClientContext.getCurrentContext(); 
        ClassLoader loader = (ctx == null ? null : ctx.getClassLoader()); 
        return ( loader == null ? getClass().getClassLoader() : loader ); 
    } 
    
    public InputStream getWebResourceAsStream( String resname ) { 
        try {
            URL u = getWebResource( resname ); 
            return ( u == null ? null : u.openStream() ); 
        } catch( Exception e ) { 
            e.printStackTrace(); 
            return null; 
        } 
    }
    
    public URL getWebResource( String resname ) { 
        if ( resname == null || resname.trim().length()== 0 ) { 
            return null; 
        } 
        
        Map env = ClientContext.getCurrentContext().getAppEnv();         
        String reshost = getString( env, "res.host" );  
        if ( reshost != null && reshost.trim().length() > 0 ) { 
            try { 
                CacheResource cache = new CacheResource(); 
                URL oURL = cache.getResource( resname ); 
                if ( oURL != null ) { return oURL; } 
                
                String shost = "http://"+ reshost +"/"+ resname; 
                oURL = new URL( shost ); 
                oURL.openStream(); 
                cache.put( resname, oURL ); 
                return oURL; 
            } catch( MalformedURLException mue ) { 
                mue.printStackTrace(); 
            } catch( java.io.FileNotFoundException ffe ) { 
                //do nothing 
            } catch (Throwable t) { 
                t.printStackTrace(); 
            } 
        } 
        return null; 
    } 
    
    public InputStream getResourceAsStream( String name ) {
        try {
            URL u = getResource( name ); 
            return ( u == null ? null : u.openStream() ); 
        } catch( Exception e ) { 
            e.printStackTrace(); 
            return null; 
        } 
    }
    
    public URL getResource( String name ) { 
        if ( name == null ) return null; 
        
        try {
            return getResourceImpl( name ); 
        } catch( MalformedURLException mue ) { 
            mue.printStackTrace(); 
            return null; 
        } catch( Exception e ) { 
            return null; 
        } 
    }
    
    private URL getResourceImpl( String name ) throws Exception  {
        if ( name == null || name.trim().length()==0 ) {
            return null; 
        }
        
        File customFolder = null; 
        if ( isDeveloperMode() ) { 
            customFolder = getDeveloperCustomFolder(); 
        } 

        Map env = ClientContext.getCurrentContext().getAppEnv(); 
        String customName = getString( env, "report.custom" ); 
        if ( customName == null || customName.trim().length()==0 ) {
            customName = getString( env, "app.custom" ); 
        }

        final String preferredName = name;         
        String customReportName = null; 
        if ( customName != null && customName.trim().length() > 0 ) { 
            String sDir = name.substring(0, name.lastIndexOf("/")); 
            String sFname = name.substring(name.lastIndexOf("/")); 
            customReportName = sDir + "/" + customName + sFname; 
        } 
        
        List<String> names = new ArrayList();
        if ( customFolder != null ) {
            names.addAll( getReportNames( customReportName ) ); 
            names.addAll( getReportNames( preferredName ) );
            for ( String tmpname : names ) {
                File ofile = new File(customFolder, tmpname); 
                if ( ofile.exists() && ofile.isFile() ) { 
                    return ofile.toURI().toURL(); 
                } 
            }
        } 
        names.clear(); 
        names.addAll( getReportNames( customReportName ) ); 
        names.addAll( getReportNames( preferredName ) ); 
        ClassLoader loader = getClassLoader(); 
        for ( String tmpname : names ) {
            URL u = loader.getResource( tmpname ); 
            if ( u != null ) { return u; } 
        } 
        return null; 
    } 
    
    private List<String> getReportNames( String name ) { 
        List<String> names = new ArrayList();
        if ( name == null || name.trim().length()==0 ) {
            return names; 
        }
        
        Map env = ClientContext.getCurrentContext().getAppEnv(); 
        String printerName = getString( env, "printer.name" ); 
        if ( printerName != null && printerName.length() > 0 ) {
            int idx = name.lastIndexOf("."); 
            String str = name.substring(0, idx); 
            names.add( str + "." + printerName + name.substring(idx));  
        }
        names.add( name ); 
        return names; 
    }
    
    public URLStreamHandler createURLStreamHandler(String protocol) { 
        return getHandler( protocol );  
    } 
    
    public com.rameses.util.URLStreamHandler getHandler( String protocol ) { 
        if ( protocol == null || protocol.length() == 0 ) {
            return null; 
        } else if ("classpath".equalsIgnoreCase( protocol )) { 
            return new ClasspathURLStreamHandler(); 
        } else if ("webresource".equalsIgnoreCase( protocol )) { 
            return new WebResURLStreamHandler(); 
        } else if ("base64".equalsIgnoreCase( protocol)) { 
            return new Base64URLStreamHandler(); 
        } else { 
            return null; 
        } 
    }
    
    private String getString( Map source, Object key ) { 
        Object value = (source == null? null: source.get(key)); 
        return (value == null? null: value.toString()); 
    } 
    
    
    // <editor-fold defaultstate="collapsed" desc=" static methods ">  
    
    public static String CACHE_NAME = "rameses_cache_resource";
    
    public static File getCacheDir() { 
        File dir = new File( System.getProperty("java.io.tmpdir"), CACHE_NAME ); 
        if ( !dir.isDirectory() ) { 
            dir.mkdir(); 
        } 
        return dir; 
    }  
    
    // </editor-fold> 
        
    // <editor-fold defaultstate="collapsed" desc=" URLStreamHandler facility ">  
    
    private class ClasspathURLStreamHandler extends com.rameses.util.URLStreamHandler {
        
        ReportURLStreamHandlerFactory root = ReportURLStreamHandlerFactory.this; 
        
        public String getProtocol() { 
            return "classpath"; 
        }

        public URL getResource(String spath) { 
            URL result = null;             
            if ( spath.startsWith("images/") ) { 
               result = getWebResource( spath ); 
            }
            
            if ( result == null ) { 
                result = root.getResource(spath); 
            } 
            return result; 
        }
    }

    private class WebResURLStreamHandler extends com.rameses.util.URLStreamHandler { 
        
        ReportURLStreamHandlerFactory root = ReportURLStreamHandlerFactory.this; 

        public String getProtocol() { 
            return "webresource"; 
        }

        public URL getResource(String spath) { 
            return getWebResource( spath ); 
        } 
    }
    
    // </editor-fold> 
    
    // <editor-fold defaultstate="collapsed" desc=" CacheResource facility ">  
    
    public static class CacheResource { 
        
        final Base64Cipher cipher = new Base64Cipher(); 
        
        public boolean contains( String name ) { 
            File file = get( name ); 
            return ( file != null ); 
        } 
        
        public File get( String name ) {
            File dir = getCacheDir(); 
            String enckey = Encoder.MD5.encode( name ).toLowerCase(); 
            File file = new File( dir, enckey );
            return ( file.isFile()? file : null );  
        }

        public URL getResource( String name ) { 
            File file = get( name ); 
            if ( file == null ) { return null; } 
            
            try { 
                return file.toURI().toURL();
            } catch (MalformedURLException ex) {
                throw new RuntimeException( ex.getMessage(), ex ); 
            }
        }
        
        public InputStream getResourceAsStream( String name ) {
            URL url = getResource( name ); 
            try { 
                return ( url == null? null: url.openStream() );
            } catch (IOException ex) {
                throw new RuntimeException( ex.getMessage(), ex ); 
            } 
        } 
        
        public void put( String name, File file ) {
            try { 
                put( name, file.toURI().toURL() );
            } catch (MalformedURLException ex) { 
                throw new RuntimeException( ex.getMessage(), ex ); 
            } 
        }

        public void put( String name, URL url ) {
            put( name, IOStream.toByteArray(url) ); 
        }

        public void put( String name, byte[] bytes ) {
            String encdata = cipher.encode( bytes ); 
            String enckey = Encoder.MD5.encode( name ).toLowerCase();    
            File dir = getCacheDir();             
            File file = new File( dir, enckey ); 
            FileOutputStream fos = null; 
            try {
                fos = new FileOutputStream( file ); 
                IOStream.write( new ByteArrayInputStream( bytes ), fos );  
            } catch( FileNotFoundException ffe ) { 
                throw new RuntimeException( ffe.getMessage(), ffe ); 
            } finally { 
                try { fos.close(); } catch(Throwable t){;} 
            } 
        } 
    }

    // </editor-fold> 
    
} 
