/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.ui;

import com.rameses.rcp.framework.UIEvent;

/**
 *
 * @author wflores 
 */
public class UIControlEvent extends UIEvent {
    
    private UIControl uic; 
    
    public UIControlEvent( UIControl uic ) {
        super( uic, uic.getBinding(), uic.getName() ); 
        
        this.uic = uic; 
    } 
    
    public UIControlEvent( UIInput uic, Object value ) { 
        super( uic, uic.getBinding(), uic.getName(), value ); 
        
        this.uic = uic; 
    } 
    
    public UIControl getControl() {
        return uic; 
    }
}
