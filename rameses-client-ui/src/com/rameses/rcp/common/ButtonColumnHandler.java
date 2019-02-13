/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.common;

/**
 *
 * @author wflores 
 */
public class ButtonColumnHandler extends Column.TypeHandler implements PropertySupport.TextPropertyInfo {

    private static final long serialVersionUID = 1L;
    
    private String visibleWhen;
    
    public ButtonColumnHandler(){
    } 
    
    public ButtonColumnHandler( String visibleWhen ){
        this.visibleWhen = visibleWhen; 
    }     
    
    public String getType() { 
        return "button"; 
    } 
    
    public String getVisibleWhen() { return visibleWhen; } 
    public void setVisibleWhen( String visibleWhen ) {
        this.visibleWhen = visibleWhen;
    }
}
