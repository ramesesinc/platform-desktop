/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package system.module;

import com.rameses.io.IOStream;
import com.rameses.rcp.framework.ClientContext;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.crypto.Cipher;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author wflores
 */
public class ModuleManager {
    
    private List<Map> modules;
    private Map<String,Properties> confs;
    
    public List getModules() {
        if ( modules == null ) {
            loadModules(); 
        }
        return modules; 
    }
        
    private void loadConfs() {
        try { 
            confs = new HashMap();
            
            ClassLoader classLoader = ClientContext.getCurrentContext().getClassLoader();
            Enumeration en = classLoader.getResources("META-INF/module.conf"); 
            while (en.hasMoreElements()) {
                URL url = (URL) en.nextElement();
                String path = url.toString();
                if ( !path.endsWith("!/META-INF/module.conf" )) continue; 
                
                Properties props = new Properties();
                props.load( url.openStream() ); 
                
                int idx = path.lastIndexOf("/modules");
                if ( idx < 0 ) continue; 
                
                path = path.substring( idx );
                path = path.substring( 0, path.lastIndexOf('!') ); 
                confs.put(path, props); 
            } 
        } catch(Throwable t) {
            t.printStackTrace();
        }
    }
    
    private void loadModules() {
        boolean securedfile = true; 
        File basedir = new File(System.getProperty("user.dir")); 
        File xmlfile = new File(basedir, "osiris2/modules/updates.sxml");
        if ( !xmlfile.exists() ) { 
            xmlfile = new File(basedir, "osiris2/modules/updates.xml");
            securedfile = false; 
        } 
        
        byte[] bytes = getContent( xmlfile, securedfile ); 
        if ( bytes == null || bytes.length == 0 ) {
            modules = new ArrayList(); 
        } else {
            Conf conf = new Conf();
            modules = conf.parse( bytes ); 
        }
                
        loadConfs(); 
        
        for ( Map map : modules ) {
            String filename = (String) map.get("file"); 
            if ( filename == null || filename.length() == 0 ) continue; 
            
            String version = (String) map.get("version"); 
            if ( version == null || version.length() == 0 ) { 
                version = "1.0"; 
            }
        
            version = version.replaceAll("\\.","_"); 
            if ( filename.endsWith(".jar") ) {
                int idx = filename.lastIndexOf(".jar"); 
                filename = filename.substring(0, idx); 
            }
            
            String actualfile = filename + "-" + version + ".jar"; 
            map.put("actualfile", actualfile); 
            
            Properties props = (Properties) confs.get("/modules/" + actualfile);
            if (props == null) props = new Properties();

            map.put("conf", props); 
        }
    }
    
    private byte[] getContent( File file, boolean secured ) {
        if ( !file.exists() ) return null; 
        
        if ( secured ) {
            Object serializeObj = null; 
            FileInputStream fis = null; 
            ObjectInputStream ois = null; 
            try {
                fis = new FileInputStream( file ); 
                ois = new ObjectInputStream( fis ); 
                serializeObj = ois.readObject();
            } catch (RuntimeException re) {
                throw re; 
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e); 
            } finally {
                try { ois.close(); }catch(Throwable t) {;} 
                try { fis.close(); }catch(Throwable t) {;} 
            }
            
            if ( serializeObj == null) return null; 
            
            try { 
                Object[] o = (Object[])serializeObj;
                if ( o.length != 2  ) throw new RuntimeException( "Error secured parameter count");

                SecretKey sk = (SecretKey) o[0];
                SealedObject so = (SealedObject) o[1];
                Cipher dec = Cipher.getInstance("DES");
                dec.init(Cipher.DECRYPT_MODE, sk);
                Object obj = so.getObject(dec); 
                return (obj == null? null: obj.toString().getBytes());  
            } catch (RuntimeException re) {
                throw re; 
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e); 
            } 
        } else {
            return IOStream.toByteArray( file ); 
        }
    }
    
    private class Conf extends DefaultHandler {
        
        final static String FIND_PATH = "app/modules/module";
        
        StringBuilder pathbuilder = new StringBuilder(); 
        List<Map> modules = new ArrayList();
        
        List<Map> parse( byte[] bytes ) { 
            ByteArrayInputStream bis = null;
            try { 
                bis = new ByteArrayInputStream( bytes );
                
                SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
                parser.parse(bis, this);
                return modules; 
            } catch (RuntimeException re) {
                throw re; 
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e); 
            } finally {
                try { bis.close(); }catch(Throwable t){;} 
            }
        }
        
        String getPath() {
            return pathbuilder.toString(); 
        }
        String pushNode( String node ) {
            if ( pathbuilder.length() > 0 ) { 
                pathbuilder.append("/");
            } 
            pathbuilder.append( node ); 
            return getPath(); 
        }
        String popNode() {
            int idx = pathbuilder.lastIndexOf("/"); 
            if (idx > 0) {
                pathbuilder = pathbuilder.delete(idx, pathbuilder.length()); 
            } else { 
                pathbuilder.delete(0, pathbuilder.length()); 
            } 
            return getPath(); 
        }
               
        public void startDocument() throws SAXException { 
            pathbuilder = new StringBuilder(); 
        }

        public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
            String path = pushNode( qName );
            if ( FIND_PATH.equalsIgnoreCase(path) ) {
                Map map = new HashMap();
                for (int i=0; i<attrs.getLength(); i++) {
                    String key = attrs.getQName(i); 
                    String val = attrs.getValue(i); 
                    map.put(key, val); 
                }
                
                String filename = (String) map.get("file");
                map.put("name", filename);
                modules.add( map ); 
            } 
        } 

        public void endElement(String uri, String localName, String qName) throws SAXException {
            popNode(); 
        }
    }
}
