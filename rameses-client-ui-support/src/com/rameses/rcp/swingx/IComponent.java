/*
 * IComponent.java
 *
 * Created on May 28, 2013, 10:41 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.swingx;

/**
 *
 * @author wflores
 */
public interface IComponent 
{
    String getName();
    
    Object getClientProperty(Object key);
    void putClientProperty(Object key, Object value); 
    
    Object getValue();
    void setValue(Object value);
    
    boolean isUpdateable();
}
