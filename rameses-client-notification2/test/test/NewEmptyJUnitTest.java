/*
 * NewEmptyJUnitTest.java
 * JUnit based test
 *
 * Created on January 15, 2014, 11:09 AM
 */

package test;

import java.lang.reflect.Method;
import junit.framework.*;

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
//        Method method = findGetMethod(getClass(), "objid"); 
//        System.out.println(method);
//        System.out.println(method.invoke(this, null));
        
        int len = 170;
        System.out.println("quotient : " + (len / 160));
        System.out.println("remainder: " + (len % 160));
        String str = "";
    }
    
    public String getObjid() {
        if (true) throw new RuntimeException("error");
        return "123";
    }

    private Method findGetMethod(Class clazz, String name) {
        try { 
            if (name == null || name.length() <= 3) return null; 

            String methodName = "get" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
            for (Method method : clazz.getMethods()) {
                if (!method.getName().equals(methodName)) continue;
                if ("void".equals(method.getReturnType().toString())) continue;
                
                Class[] paramTypes = method.getParameterTypes();
                if (paramTypes != null && paramTypes.length > 1) continue;
                
                return method;
            }
            return null;
        } catch(Throwable t) {
            return null; 
        }
    }    
}
