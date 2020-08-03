/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import com.rameses.io.IOStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;

/**
 *
 * @author ramesesinc
 */
public class NewEmptyJUnitTest extends TestCase {
    
    public NewEmptyJUnitTest(String testName) {
        super(testName);
    }

    public void test1() throws Exception {
        URL url = getClass().getClassLoader().getResource("test/remittance.json"); 
        byte[] bytes = IOStream.toByteArray( url ); 

        Object source = new String( bytes, "utf8");
        source = "{}";
        
        JsonConfig jc = new JsonConfig(); 
        jc.registerJsonValueProcessor(java.util.Date.class, new JsonDateValueProcessor()); 
        jc.registerJsonValueProcessor(java.sql.Date.class, new JsonDateValueProcessor()); 
        jc.registerJsonValueProcessor(java.sql.Timestamp.class, new JsonDateValueProcessor()); 

        JSON js = JSONSerializer.toJSON( source, jc ); 
        System.out.println(js);

        Object o = null; 
        if ( js.isArray()) {
            o = JSONArray.fromObject( js ); 
        } else {
            o =  JSONObject.fromObject( js );
        }
        
        System.out.println("class of o is " + o.getClass());
        System.out.println("is map -> "+ (o instanceof Map));
        System.out.println("is list -> "+ (o instanceof List));
        System.out.println( o );
    }
    
    
    private class JsonDateValueProcessor implements JsonValueProcessor {

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
