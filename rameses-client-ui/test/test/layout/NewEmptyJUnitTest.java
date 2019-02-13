/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test.layout;

import com.rameses.rcp.control.layout.SplitterLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
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
        
        JPanel panel = new JPanel();
        panel.setLayout(new SplitterLayout());

        JButton b1 = new JButton("View 1"); 
        panel.add( b1, "sideview" );
        
        JButton b2 = new JButton("View 2"); 
        panel.add( b2, "contentview" );

        JDialog d = new JDialog();
        d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); 
        d.setTitle("Test"); 
        d.setModal(true); 
        d.setContentPane( panel ); 
        d.pack(); 
        d.setVisible(true);        
    }
}
