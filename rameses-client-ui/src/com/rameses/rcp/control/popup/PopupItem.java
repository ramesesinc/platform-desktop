/*
 * PopupItem.java
 *
 * Created on June 9, 2014, 12:44 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control.popup;

/**
 *
 * @author wflores
 */
public class PopupItem 
{
    private Object userObject;
    private String caption;
    
    public PopupItem() {
    }
    
    public PopupItem(Object userObject, String caption) {
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
}
