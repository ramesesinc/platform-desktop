/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test.window;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import javax.swing.JDialog;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import javax.swing.plaf.PopupMenuUI;
import junit.framework.TestCase;

/**
 *
 * @author ramesesinc
 */
public class NewEmptyJUnitTest1 extends TestCase {
    
    public NewEmptyJUnitTest1(String testName) {
        super(testName);
    }

    public void test1() throws Exception { 
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); 

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment(); 
        GraphicsDevice gdmain = ge.getDefaultScreenDevice(); 
        System.out.println("default device : " + gdmain); 
        
        for ( GraphicsDevice gd : ge.getScreenDevices() ) {
            System.out.println(gd);
            if ( gdmain.equals(gd)) {
                System.out.println("this is the default");
            }
        }
        
        
        
        JDialog d = new JDialog();
        d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); 
        d.setTitle("Test"); 
        d.setModal(true); 
        d.setContentPane( new MainPanel()); 
        d.pack(); 
        d.setVisible(true);
        
    }
}
