/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.rameses.io.StreamUtil;
import com.rameses.util.Base64Coder;
import java.io.File;
import java.io.FileInputStream;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author elmonazareno
 */
public class NewEmptyJUnitTest {
    
    public NewEmptyJUnitTest() {
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
    @Test
    public void testReport() throws Exception {
        File f = new File("/Users/elmonazareno/Desktop/sample1.txt");
        FileInputStream fis = new FileInputStream(f);
        String s = StreamUtil.toString(fis);
        byte[] bytes = Base64Coder.decodeLines(s);
        System.out.println( bytes );
        /*
        File file = new File("sample_test.pdf");
        FileOutputStream fos = new FileOutputStream(file);
        fos.write( bytes );
        fos.flush();
        fos.close();
        */
    }
}
