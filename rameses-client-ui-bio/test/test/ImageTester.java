/*
 * ImageTester.java
 * JUnit based test
 *
 * Created on December 20, 2013, 1:12 PM
 */

package test;

import com.rameses.rcp.common.ImageModel;
import com.rameses.rcp.image.ImageViewer;
import java.io.File;
import javax.swing.UIManager;
import junit.framework.*;

/**
 *
 * @author compaq
 */
public class ImageTester extends TestCase {
    
    public ImageTester(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); 
    }

    protected void tearDown() throws Exception {
    }
    
    public void testMain() throws Exception {
        ImageModel model = new ImageModel(){
            public Object getData() {
                return new File("C:\\Users\\Public\\Pictures\\Sample Pictures\\Desert.jpg");
            }
        };
        ImageViewer.open(model); 
    }
}
