/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.rameses.util.Base64Cipher;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 *
 * @author elmonazareno
 */
public class NewEmptyJUnitTest1 {
    
    public NewEmptyJUnitTest1() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    public void hello() {
        String s = "Elmo Nazareno";
        Base64Cipher cp = new Base64Cipher();
        System.out.println( "" + cp.isEncoded(s));
    }
}
