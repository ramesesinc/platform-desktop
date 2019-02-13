/*
 * NotifierImpl.java
 *
 * Created on November 28, 2013, 1:21 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.impl;

import com.rameses.platform.interfaces.Notifier;
import com.rameses.rcp.common.MsgBox;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author wflores
 */
public class NotifierImpl implements Notifier 
{
    private Map<Object,ProxyHandler> registry; 
    
    public NotifierImpl() {
        registry = new HashMap(); 
    }

    public void add(Object key, PropertyChangeListener listener) {
        if (key == null || listener == null) return;
        
        ProxyHandler handler = registry.get(key); 
        if (handler == null) {
            handler = new ProxyHandler(); 
            registry.put(key, handler); 
        }
        handler.add(listener); 
    }

    public void remove(Object key) {
        if (key == null) return;
        
        ProxyHandler handler = registry.remove(key); 
        if (handler != null) handler.clear(); 
    }

    public void notify(Object key, String name, Object value) {
        if (key == null) return;
        
        ProxyHandler handler = registry.get(key); 
        handler.notify(key, name, value); 
    }
    
    
    private class ProxyHandler 
    {
        NotifierImpl root = NotifierImpl.this;
        
        private List<PropertyChangeListener> listeners;
        
        ProxyHandler() {
            listeners = new ArrayList(); 
        } 
        
        void add(PropertyChangeListener listener) {
            if (listener != null && !listeners.contains(listener)) {
                listeners.add(listener); 
            }
        }
        
        void remove(PropertyChangeListener listener) {
            if (listener != null) listeners.remove(listener); 
        }
        
        void clear() {
            listeners.clear(); 
        }
        
        void notify(Object key, String name, Object value) {
            PropertyChangeEvent pce = new PropertyChangeEvent(root, name, null, value);            
            for (PropertyChangeListener pcl : listeners) {
                try {
                    pcl.propertyChange(pce); 
                } catch(Throwable t) {
                    MsgBox.err(t); 
                }
            }
        }
    }
}
