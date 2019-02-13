/*
 * NewEmptyJUnitTest.java
 * JUnit based test
 *
 * Created on October 23, 2013, 4:41 PM
 */

package test;

import javax.swing.ImageIcon;
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
    
    // TODO add test methods here. The name must begin with 'test'. For example:
    public void testMain() throws Exception {
//        JDialog d = new JDialog();
//        d.setModal(true); 
//        d.setContentPane(new NewJPanel());
//        d.pack();
//        d.setVisible(true);
        
        String s = "1111b-a:0-_\\";
        System.out.println(s.matches("[0-9a-zA-Z]{5,5}-[0-9a-zA-Z:_\\-]{1,}"));
    }

}
