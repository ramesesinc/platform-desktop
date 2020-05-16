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
    private String action;
    private String tag;
    
    public ButtonColumnHandler(){
    } 
    
    public ButtonColumnHandler( String visibleWhen ){
        this( visibleWhen, null, null ); 
    }     

    public ButtonColumnHandler( String visibleWhen, String action, String tag ){
        this.visibleWhen = visibleWhen; 
        this.action = action;
        this.tag = tag; 
    }         
    
    public String getType() { 
        return "button"; 
    } 
    
    public String getVisibleWhen() { return visibleWhen; } 
    public void setVisibleWhen( String visibleWhen ) {
        this.visibleWhen = visibleWhen;
    }
    
    public String getAction() { return action; } 
    public void setAction(String action) {
        this.action = action; 
    }

    public String getTag() { return tag; } 
    public void setTag(String tag) {
        this.tag = tag; 
    }
}
