/*
 * ComboItem.java
 *
 * Created on May 28, 2013, 11:31 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.swingx;

/**
 *
 * @author wflores
 */
public class ComboItem 
{
    private Object key;
    private Object value;
    
    public ComboItem(Object key) {
        this(key, key); 
    }
    
    public ComboItem(Object key, Object value) {
        this.key = key;
        this.value = value;
    }
    
    public Object getKey() { return key; }
    public Object getValue() { return value; }     
    public String toString() { return getKey()+""; }

    public boolean equals(Object obj) 
    {
        if (super.equals(obj)) return true;
        else if (key == obj) return true; 
        else if (value == obj) return true;
        else if (key != null && obj != null && (key.equals(obj) || obj.equals(key))) return true;
        else if (value != null && obj != null && (value.equals(obj) || obj.equals(value))) return true; 
        else return false; 
    }
}
