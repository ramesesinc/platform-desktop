/*
 * PropertyChangeHandler.java
 *
 * Created on May 15, 2013, 11:12 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.common;

/**
 *
 * @author wflores
 */
public interface PropertyChangeHandler 
{
    void firePropertyChange(String name, int value);   
    void firePropertyChange(String name, boolean value);
    void firePropertyChange(String name, String value);    
    void firePropertyChange(String name, Object value);
}
