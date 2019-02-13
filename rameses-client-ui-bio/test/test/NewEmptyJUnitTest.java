/*
 * NewEmptyJUnitTest.java
 * JUnit based test
 *
 * Created on December 5, 2013, 10:11 AM
 */

package test;

import com.rameses.rcp.fingerprint.FingerPrintViewer;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import javax.swing.UIManager;
import junit.framework.*;

/**
 *
 * @author compaq
 */
public class NewEmptyJUnitTest extends TestCase {
    
    public NewEmptyJUnitTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); 
    }

    protected void tearDown() throws Exception {
    }
    
    public void testMain() throws Exception {
        Map options = new HashMap();
        //WebcamViewer.open(options); 
        FingerPrintViewer.open(options); 
    }

    public void xtest2() throws Exception {
        byte[] data = toByteArray(new File("right_thumb.jpg"));
        System.out.println(data);
    } 
    
    private byte[] toByteArray(File file) 
    {
        FileInputStream fis = null; 
        ByteArrayOutputStream baos = null; 
        try {
            fis = new FileInputStream(file); 
            baos = new ByteArrayOutputStream();
            int read = -1;
            while ((read=fis.read()) != -1) {
                baos.write(read); 
            }
            return baos.toByteArray(); 
        } catch(RuntimeException re) {
            throw re; 
        } catch(Exception x) {
            throw new RuntimeException(x.getMessage(), x); 
        } finally {
            try { fis.close(); } catch(Throwable t){;} 
            try { baos.close(); } catch(Throwable t){;} 
        }
    }
}
