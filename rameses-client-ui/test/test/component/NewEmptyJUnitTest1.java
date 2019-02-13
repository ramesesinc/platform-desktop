/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test.component;

import com.rameses.rcp.constant.UIConstants;
import com.rameses.rcp.control.XSplitView;
import javax.swing.JButton;
import test.window.*;
import javax.swing.JDialog;
import javax.swing.UIManager;
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

        XSplitView v = new XSplitView(); 
        v.setDividerLocation(200); 
        v.setOrientation( UIConstants.VERTICAL ); 
        v.add( new JButton("Button 1"));
        v.add( new JButton("Button 2"));
        
        JDialog d = new JDialog();
        d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); 
        d.setTitle("Test"); 
        d.setModal(true); 
        d.setContentPane( v ); 
        d.setSize( 500, 300 );
        d.setVisible(true);
        
    }
}
