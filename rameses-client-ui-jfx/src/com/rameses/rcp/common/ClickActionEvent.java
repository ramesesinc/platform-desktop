/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.common;

import java.util.Map;

/**
 *
 * @author wflores
 */
public class ClickActionEvent {
    
    private String tagName;
    private String location; 
    private Map properties;

    private boolean consumed;
    
    public ClickActionEvent( String tagName, String location, Map properties ) {
        this.tagName = tagName;
        this.location = location; 
        this.properties = properties;
    }

    public String getTagName() { return tagName; }
    public String getLocation() { return location; } 
    public Map getProperties() { return properties; } 
    
    public boolean isConsumed() {
        return consumed; 
    }
    
    public void consume() { 
        this.consumed = true; 
    }    
}
