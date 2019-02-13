/*
 * TestLoad.java
 * JUnit based test
 *
 * Created on February 21, 2013, 10:23 AM
 */

package test;

import com.rameses.clientupdate.AppConfBuilder;
import junit.framework.*;

/**
 *
 * @author Elmo
 */
public class TestLoad extends TestCase {
    
    public TestLoad(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }
    
    // TODO add test methods here. The name must begin with 'test'. For example:
    public void testHello() throws Exception {
        AppConfBuilder b = new AppConfBuilder("file:///c:/client-updates");
        System.out.println( b.buildConf( "app1" ) );
       
    }

}
