/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test.window;

import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.UIManager;
import junit.framework.TestCase;

/**
 *
 * @author ramesesinc
 */
public class NewEmptyJUnitTest2 extends TestCase {
    
    public NewEmptyJUnitTest2(String testName) {
        super(testName);
    }

    public void test1() throws Exception { 
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); 

        VerticalMenuBar bar = new VerticalMenuBar();
        bar.add( new JMenu("Menu 1"));
        bar.add( new JMenu("Menu 2"));
        bar.add( new JMenu("Menu 3"));
        
        JDialog d = new JDialog();
        d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); 
        d.setTitle("Test 2"); 
        d.setModal(true); 
        d.setContentPane( new MainPanel2() ); 
        d.pack(); 
        d.setVisible(true);
        
    }
}
