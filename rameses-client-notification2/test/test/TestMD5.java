/*
 * NewEmptyJUnitTest.java
 * JUnit based test
 *
 * Created on January 15, 2014, 11:09 AM
 */

package test;

import com.rameses.util.Encoder;
import junit.framework.*;

/**
 *
 * @author compaq
 */
public class TestMD5 extends TestCase {
    
    public TestMD5(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }
    
    public void testMain() throws Exception {
        String str = Encoder.MD5.encode("iligan", "etracs");
        System.out.println(str);
    } 
}
