/*
 * NewEmptyJUnitTest.java
 * JUnit based test
 *
 * Created on January 15, 2014, 11:09 AM
 */

package test;

import com.rameses.rcp.common.WebsocketClient;
import com.rameses.rcp.common.WebsocketModel;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import junit.framework.*;

/**
 *
 * @author compaq
 */
public class TestWebsocketClient extends TestCase {
    
    public TestWebsocketClient(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }
    
    public void testMain() throws Exception {
        WebsocketClient.Handle handle = WebsocketClient.open(new WebsocketModelImpl());
        JOptionPane.showMessageDialog(null, "Wait....");
        handle.close(); 
    }
    
    private class WebsocketModelImpl extends WebsocketModel 
    {
        public String getProtocol() { return "etracs"; }
        public String getHost() { return "localhost:8060"; }

        public void onmessage(Object data) {
            System.out.println("onmessage... " + data);
        }

        public void onclose() {
            System.out.println("onclose...");
        }        
    } 
}
