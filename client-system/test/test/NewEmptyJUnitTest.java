/*
 * NewEmptyJUnitTest.java
 * JUnit based test
 *
 * Created on May 5, 2014, 6:04 PM
 */

package test;

import com.rameses.io.IOStream;
import com.rameses.osiris2.Folder;
import java.awt.Dimension;
import java.net.URL;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
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
        Folder folder = null; 
    }

    protected void tearDown() throws Exception {
    }
    
    public void testMain() throws Exception {
        URL url = getClass().getResource("content");
        byte[] bytes = IOStream.toByteArray(url); 
        
        JTextPane txt = new JTextPane(); 
        txt.setText(new String(bytes)); 
                
        JScrollPane jsp = new JScrollPane(txt); 
        jsp.setPreferredSize(new Dimension(300, 300));
        JOptionPane.showMessageDialog(null, jsp); 
       
    }
}
