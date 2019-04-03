/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.common;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.scene.web.WebEngine;
import netscape.javascript.JSObject;

/**
 *
 * @author wflores 
 */
public class WebEngineProxy {
    
    private WebEngine we; 
    
    public WebEngineProxy( WebEngine we ) {
        this.we = we; 
    }
    
    public void call( final String name, final Object ... args ) {
        final Runnable run = new Runnable() {
            public void run() {
                Object result = null; 
                try {
                    Object o = we.executeScript("window"); 
                    if( o instanceof JSObject) {
                        JSObject js = (JSObject) o ;
                        result = js.call(name, args); 
                    }
                }
                catch(Throwable t) {
                    t.printStackTrace(); 
                }
            } 
        }; 
        Platform.runLater( run );
    }
    
}
