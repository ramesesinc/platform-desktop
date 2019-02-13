/*
 * NewEmptyJUnitTest.java
 * JUnit based test
 *
 * Created on October 23, 2013, 4:41 PM
 */

package epms;

import com.rameses.osiris3.platform.CipherUtil;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import junit.framework.*;

/**
 *
 * @author compaq
 */
public class TestProfile extends TestCase 
{
    
    public TestProfile(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }
    
    public void testMain() throws Exception {
        byte[] icon = getImageBytes("icon.gif");
        byte[] splash = getImageBytes("splash.png");
        Map map = new HashMap(); 
        map.put("icon", icon);
        map.put("splash", splash);
        writeToFile(".identity", map); 
    }

    private byte[] getImageBytes(String name) throws Exception {
        URL url = getClass().getResource(name);
        InputStream inp = url.openStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            int read = -1;
            while ((read=inp.read()) != -1) {
                baos.write(read); 
            } 
            baos.flush();
            return baos.toByteArray();
        } catch(Exception e) {
            throw e; 
        } finally {
            try { baos.close(); }catch(Exception ign){;} 
            try { inp.close(); }catch(Exception ign){;} 
        }
    } 
    
    private void writeToFile(String filename, Map data) throws Exception {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            File f = new File(filename);
            Object o = CipherUtil.encode((Serializable) data);
            fos = new FileOutputStream(f);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(o);
            oos.flush();
        } catch(Exception e) {
            throw e;
        } finally {
            try { oos.close(); } catch(Exception ign){;}
            try { fos.close(); } catch(Exception ign){;}
        } 
    }
}
