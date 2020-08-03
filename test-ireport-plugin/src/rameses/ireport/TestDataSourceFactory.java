/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rameses.ireport;

import com.rameses.osiris2.reports.ReportDataSource;
import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;

/**
 *
 * @author elmonazareno
 */
public class TestDataSourceFactory {

    public static JRDataSource createDatasource() throws Exception { 
       
        Object p = JOptionPane.showInputDialog(null, "Enter name", null);
        if ( p == null ) return null;

        String filename = p.toString(); 
        if ( !filename.endsWith(".json")) {
            filename += ".json";
        }

        File[] dirs = new File[]{
            new File( System.getProperty("user.dir")), 
            new File( ".." )
        }; 
        
        File datafile = null; 
        for ( int i=0; i < dirs.length; i++ ) {
            System.out.println("user-directory -> " + dirs[i]); 
            datafile = new File( dirs[i], "data/"+ filename ); 
            if ( datafile.exists() && !datafile.isDirectory()) {
                break; 
            } 
            else { 
                datafile = null; 
            }
        }
        
        if ( datafile == null ) {
            throw new Exception("File does not exist"); 
        }
                
        int read = 0; 
        FileInputStream fis = null;
        StringBuilder buff = new StringBuilder();
        try {
            byte[] bytes = new byte[1024];
            fis = new FileInputStream( datafile );             
            while ((read = fis.read( bytes )) != -1 ) {
                buff.append( new String( bytes, 0, read)); 
            }
            
            JOptionPane.showMessageDialog(null, "data is " + buff.toString());
        }
        catch(Exception e) {
            e.printStackTrace();
            throw e;
        }
        catch(Throwable t) {
            t.printStackTrace();
            throw new RuntimeException(t); 
        }
        finally {
            try { fis.close(); }catch(Throwable t){;}
        }
        
        try {
            // JSON Conversion starts here...
            JsonConfig jc = new JsonConfig(); 
            jc.registerJsonValueProcessor(java.util.Date.class, new JsonDateValueProcessor()); 
            jc.registerJsonValueProcessor(java.sql.Date.class, new JsonDateValueProcessor()); 
            jc.registerJsonValueProcessor(java.sql.Timestamp.class, new JsonDateValueProcessor()); 
            JSON js = JSONSerializer.toJSON( buff.toString(), jc ); 
                        
            Object obj = null ; 
            if ( js.isArray()) {
                obj = JSONArray.fromObject( js );
            } else {
                obj = JSONObject.fromObject( js ); 
            }
            
            if ( obj instanceof Map || obj instanceof List ) {
                return new ReportDataSource( obj );
            }

            throw new RuntimeException("JSON format must be in Map or List object"); 
        }
        catch(Exception e) {
            e.printStackTrace();
            throw e;
        }
        catch(Throwable t) {
            t.printStackTrace();
            throw new RuntimeException(t); 
        }
    }
    
    
    
    private static class JsonDateValueProcessor implements JsonValueProcessor {

        private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd mm:hh:ss"); 
        
        public Object processArrayValue(Object value, JsonConfig jc) { 
            if ( value instanceof java.util.Date ) {
                return sdf.format((java.util.Date) value); 
            }
            return "";
        }

        public Object processObjectValue(String name, Object value, JsonConfig jc) { 
            if ( value instanceof java.util.Date ) {
                return sdf.format((java.util.Date) value); 
            }
            return "";
        }
    }    
        
}
