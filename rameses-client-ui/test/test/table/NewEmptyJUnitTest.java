/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test.table;

import com.rameses.rcp.control.table.DataTableComponent;
import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import junit.framework.TestCase;

/**
 *
 * @author compaq
 */
public class NewEmptyJUnitTest extends TestCase {


    public NewEmptyJUnitTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    public void testMain() throws Exception { 
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); 
        
        Object[] columnNames = new Object[]{
            "Column#1", "Column#2", 
            "Column#3", "Column#4"
        }; 
        
        DataTableComponent tbl = new DataTableComponent(); 
        tbl.setModel( new DefaultTableModel( columnNames, 10 ) ); 
        
        JScrollPane jsp = new JScrollPane( tbl ); 
                
        JPanel panel = new JPanel( new BorderLayout() ); 
        panel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        panel.add( jsp ); 
       
        JDialog d = new JDialog((JFrame) null);
        d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); 
        d.setTitle("Test Panel"); 
        d.setModal( true );
        d.setContentPane( panel ); 
        d.setSize(500, 400); 
        d.setVisible(true); 
    }
}
