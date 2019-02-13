/*
 * Notifier.java
 *
 * Created on November 28, 2013, 1:12 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.platform.interfaces;

import java.beans.PropertyChangeListener;

/**
 *
 * @author wflores
 */
public interface Notifier 
{
    void add(Object key, PropertyChangeListener listener);     
    void remove(Object key); 
    
    void notify(Object key, String name, Object value); 
}
