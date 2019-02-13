/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test.list;

import javax.swing.JDialog;
import junit.framework.TestCase;

/**
 *
 * @author rameses
 */
public class NewEmptyJUnitTest extends TestCase {
    
    public NewEmptyJUnitTest(String testName) {
        super(testName);
    }

    public void test1() throws Exception {
        JDialog d = new JDialog();
        d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        d.setTitle("Test List Panel");
        d.setModal(true); 
        d.pack();
        d.setVisible(true); 
    }
}
