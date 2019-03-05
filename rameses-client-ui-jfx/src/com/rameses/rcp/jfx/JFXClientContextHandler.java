/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.jfx;

import com.rameses.rcp.framework.ClientContextHandler;
import javafx.application.Platform;

/**
 *
 * @author wflores 
 */
public class JFXClientContextHandler implements ClientContextHandler {

    public void start() {
        JFXUtilityApp.main(null); 
    }
 
    public void stop() { 
        Platform.exit(); 
    }
    
}
