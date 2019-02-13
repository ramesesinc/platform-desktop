/*
 * FingerPrintTester.java
 * JUnit based test
 *
 * Created on December 20, 2013, 1:12 PM
 */

package test;

import com.rameses.rcp.common.FingerPrintModel;
import com.rameses.rcp.fingerprint.FingerPrintResultInfo;
import com.rameses.rcp.fingerprint.FingerPrintViewer;
import junit.framework.*;

/**
 *
 * @author compaq
 */
public class FingerPrintTester extends TestCase {
    
    public FingerPrintTester(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }
    
    public void testMain() throws Exception {
        FingerPrintModel model = new FingerPrintModel(){
            public void onselect(Object info) { 
                System.out.println("onselect-> " + info);
                
                FingerPrintResultInfo result = (FingerPrintResultInfo) info; 
                System.out.println("getLeftThumbData-> " + result.getLeftThumbData());
                System.out.println("getRightThumbData-> " + result.getRightThumbData());
            }

            public void onclose() {
                System.out.println("onclose");
            }            

            public int getFingerType() {
                return FingerPrintModel.RIGHT_THUMB;
            }
        };
        FingerPrintViewer.open(model); 
    }
}
