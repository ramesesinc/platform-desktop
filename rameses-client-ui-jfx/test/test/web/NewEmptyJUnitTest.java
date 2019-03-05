/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test.web;

import java.io.InputStream;
import java.net.URL;
import javax.swing.JDialog;
import javax.swing.UIManager;
import junit.framework.TestCase;

/**
 *
 * @author ramesesinc
 */
public class NewEmptyJUnitTest extends TestCase {
    
    public NewEmptyJUnitTest(String testName) {
        super(testName);
    }

    public void test1() throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); 
        
        JDialog d = new JDialog(); 
        d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); 
        d.setModal(true); 
        d.setTitle("Test WebView"); 
        d.setContentPane( new TestPage() );
        //d.pack();
        d.setVisible(true);
    }
    
    private String getContent( String name ) throws Exception {
        InputStream inp = null; 
        StringBuilder buff = new StringBuilder();
        try {
            URL url = getClass().getResource( name ); 
            inp = url.openStream(); 
            
            int read = -1;
            byte[] bytes = new byte[250]; 
            while ((read = inp.read(bytes)) != -1) {
                buff.append( new String(bytes, 0, read)); 
            }
            return buff.toString(); 
        } finally {
            try { inp.close(); }catch(Throwable t){;}
        }
    }
}
