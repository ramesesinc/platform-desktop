/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test.web;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.net.URL;
import junit.framework.TestCase;

/**
 *
 * @author ramesesinc
 */
public class TestFont extends TestCase {
    
    public TestFont(String testName) {
        super(testName);
    }

    public void test1() throws Exception {
        ClassLoader loader = TestFont.class.getClassLoader();
        URL url = loader.getResource("fonts/OpenSans-Regular-webfont.ttf");
        if ( url != null ) {
            Font.createFont(Font.TRUETYPE_FONT, new File( url.toURI()));
        }
        
        String fonts[] = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        for (String str : fonts) {
            System.out.println( str );
        }
    }
}
