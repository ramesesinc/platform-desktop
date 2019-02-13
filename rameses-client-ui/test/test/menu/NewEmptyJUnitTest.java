/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test.menu;

import com.rameses.rcp.control.menu.VMenu;
import com.rameses.rcp.control.menu.VMenuBar;
import java.awt.FlowLayout;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
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

        VMenuBar jmb = new VMenuBar();
        VMenu vm = jmb.addMenu("File");
        buildMenuItems( vm );
        
        VMenu jm = new VMenu("Folder");
        vm.add( jm ); 
        buildMenuItems( jm ); 

        JMenuItem mi = jmb.addMenuItem( "Source" );
        
        vm = jmb.addMenu("Navigate");
        buildMenuItems( vm );
        
        JPanel p = new JPanel(new FlowLayout());
        p.add(jmb); 
        
        JDialog d = new JDialog();
        d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); 
        d.setTitle("Test 3"); 
        d.setModal(true); 
        d.setContentPane( p ); 
        d.pack(); 
        d.setVisible(true);
    }
    
    private void buildMenuItems( JMenu jm ) {
        jm.add( createMenuItem("Item 1"));
        jm.add( createMenuItem("Item 2"));
        jm.add( createMenuItem("Item 3"));
        jm.add( createMenuItem("Item 4"));
    }
    
    private JMenuItem createMenuItem( String text ) {
        final JMenuItem mi = new JMenuItem(text);
        return mi;
    }
}
