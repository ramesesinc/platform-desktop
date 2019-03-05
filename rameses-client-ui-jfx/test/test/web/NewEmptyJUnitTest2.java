/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test.web;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JPanel;
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
        
        JFXPanelImpl fxp = new JFXPanelImpl(); 
        fxp.setBorder(BorderFactory.createLineBorder(new Color(180,180,180), 1));
        fxp.setOpaque(false); 
        
        JPanel p = new JPanel(); 
        p.setLayout(new BorderLayout());
        p.setBackground( Color.WHITE);
        p.add( fxp ); 
        
        JDialog d = new JDialog(); 
        d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); 
        d.setModal(true); 
        d.setTitle("Test WebView"); 
        d.setContentPane( p );
        d.setSize(500, 400);
        d.setVisible(true);
    }    
}
