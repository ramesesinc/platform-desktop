/*
 * PropertyChangeSupport.java
 *
 * Created on May 15, 2013, 11:13 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.common;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author wflores
 */
public class PropertyChangeSupport implements PropertyChangeHandler, Cloneable
{
    private List<PropertyChangeHandler> handlers = new ArrayList<PropertyChangeHandler>(); 
    
    public PropertyChangeSupport() {
    }
    
    public void removeAll() { handlers.clear(); }

    public void remove(PropertyChangeHandler handler) {
        if (handler != null) handlers.remove(handler); 
    }

    public void add(PropertyChangeHandler handler) 
    {
        if (handler != null && !handlers.contains(handler)) 
            handlers.add(handler); 
    } 
    
    public PropertyChangeHandler clone() 
    {
        PropertyChangeSupport support = new PropertyChangeSupport();
        for (PropertyChangeHandler handler : handlers) {
            support.handlers.add(handler); 
        } 
        return support; 
    }    

    public void firePropertyChange(String name, int value) {
        for (PropertyChangeHandler handler : handlers) {
            handler.firePropertyChange(name, value); 
        }        
    }

    public void firePropertyChange(String name, boolean value) {
        for (PropertyChangeHandler handler : handlers) {
            handler.firePropertyChange(name, value); 
        }        
    }

    public void firePropertyChange(String name, String value) {
        for (PropertyChangeHandler handler : handlers) {
            handler.firePropertyChange(name, value); 
        }         
    }

    public void firePropertyChange(String name, Object value) {
        for (PropertyChangeHandler handler : handlers) {
            handler.firePropertyChange(name, value); 
        }         
    }    
}
