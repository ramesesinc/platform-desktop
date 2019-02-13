/*
 * SuggestItem.java
 *
 * Created on December 18, 2013, 1:50 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control.suggest;

/**
 *
 * @author wflores
 */
public class SuggestItem 
{
    private Object userObject;
    private String caption;
    private String icon;
    
    public SuggestItem() {
    }
    
    public SuggestItem(Object userObject, String caption) {
        this.userObject = userObject;
        this.caption = caption; 
    }
    
    public Object getUserObject() { return userObject; } 
    public void setUserObject(Object userObject) {
        this.userObject = userObject; 
    }
    
    public String getCaption() { return caption; }
    public void setCaption(String caption) {
        this.caption = caption;
    }
    
    public String getIcon() { return icon; } 
    public void setIcon( String icon ) {
        this.icon = icon; 
    }
}
