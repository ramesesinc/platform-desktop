/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test.web;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import junit.framework.TestCase;

/**
 *
 * @author ramesesinc
 */
public class NewEmptyJUnitTest3 extends TestCase {
    
    public NewEmptyJUnitTest3(String testName) {
        super(testName);
    }

    public void test1() throws Exception { 
        Thread t = new Thread(new Runnable() {
            public void run() {
                JFXApp.main(null); 
            }
        });
        t.start(); 
        
        new LinkedBlockingQueue().poll(5, TimeUnit.SECONDS); 
        Platform.exit(); 
    }
}
