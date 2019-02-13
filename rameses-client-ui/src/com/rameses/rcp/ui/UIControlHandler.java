/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.ui;

import com.rameses.rcp.framework.UIEvent;
import com.rameses.rcp.framework.UIHandler;


/**
 *
 * @author wflores
 */
public interface UIControlHandler extends UIHandler {
    
    void valueChanged( UIEvent e ); 
    
} 
