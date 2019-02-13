
import com.rameses.beaninfo.editor.table.ColumnEditorPage2;
import com.rameses.rcp.common.TextColumnHandler;
import com.rameses.rcp.swingx.ComboItem;
import javax.swing.JDialog;
import javax.swing.UIManager;
import junit.framework.*;


/*
 * Test1.java
 * JUnit based test
 *
 * Created on January 10, 2011, 2:08 PM
 * @author jaycverg
 */

public class Test1 extends TestCase {
    
    public Test1(String testName) {
        super(testName);
    }
    
    public void test1() throws Exception 
    {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); 
        JDialog d = new JDialog();
        d.setTitle("Test");
        d.setModal(true);
        d.setContentPane(new ColumnEditorPage2());
        d.pack();
        d.setVisible(true); 
    }
    
    public void xtest2() throws Exception 
    {
        Object o1 = new ComboItem("text");
        Object o2 = new TextColumnHandler();
        
        System.out.println( o1.equals(o2) );
    }
    
}
