
import com.rameses.osiris2.client.InvokerProxy;
import groovy.lang.GroovyShell;
import junit.framework.*;
/*
 * NewEmptyJUnitTest.java
 * JUnit based test
 *
 * Created on January 22, 2011, 4:01 PM
 */

/**
 *
 * @author ms
 */
public class NewEmptyJUnitTest extends TestCase {
    
    public NewEmptyJUnitTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }
    
    /*
    class MyHandler implements AsyncHandler {
        public void onMessage(AsyncResult o) {
            System.out.println("finished handled " + o.getValue());
        }
    }
     */
    
    // TODO add test methods here. The name must begin with 'test'. For example:
    public void testHello() throws Exception {
        Object o = InvokerProxy.getInstance().create( "MyFirstService" );
        GroovyShell sh = new GroovyShell();
        sh.setProperty("o",o);
        //sh.setProperty("handler",new MyHandler());
        
        sh.evaluate( "o.sayTest( [firstname:'elmo',lastname:'nazareno'], handler )" );
        Thread.sleep(3000);
    }

}
