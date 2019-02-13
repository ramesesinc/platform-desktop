/*
 * UIProperty.java
 *
 * Created on May 2, 2013, 3:45 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.ui;

/**
 *
 * @author wflores
 */
public abstract class UIProperty 
{
    public abstract Object getValue(String name);
    
    public String getStringValue(String name) {
        Object o = getValue(name);
        return (o == null? null: o.toString()); 
    }
    
    public boolean getBooleanValue(String name) {
        Object o = getValue(name);
        if (o == null) 
            return false;
        else if (o instanceof Boolean) 
            return ((Boolean) o).booleanValue(); 
        else 
            return "true".equals(o.toString());
    }
    
    public int getIntValue(String name) 
    {
        Object o = getValue(name); 
        if (o == null) 
            return 0;
        else if (o instanceof Number)
            return ((Number) o).intValue(); 
        else 
            return Integer.parseInt(o.toString()); 
    }
}
