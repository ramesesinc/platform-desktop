/*
 * ValueChangeSupport.java
 *
 * Created on July 8, 2013, 11:17 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.framework;

import com.rameses.rcp.common.CallbackHandlerProxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author wflores
 */
public class ValueChangeSupport 
{
    private Map<String,List> handlers = new HashMap(); 
    private List<Map> extendedHandlers = new ArrayList(); 
    private List<Handler> vhandlers = new ArrayList();
            
    ValueChangeSupport() {}

    public void add( Handler handler ) {
        if ( handler != null && !vhandlers.contains(handler) ) {
            vhandlers.add( handler ); 
        }
    }
    public void remove( Handler handler ) {
        if ( handler != null ) {
            vhandlers.remove( handler ); 
        }
    }    
    
    public void addExtendedHandler(Map extended) { 
        if (extended != null) extendedHandlers.add(extended); 
    } 
    
    public void add(String property, Object callbackListener) {
        if (property == null || callbackListener == null) return;
        
        List list = handlers.get(property); 
        if (list == null) {
            list = new ArrayList();
            handlers.put(property, list); 
        }
        
        list.add(callbackListener); 
    }
    
    public void remove(String property, Object callbackListener) 
    {
        if (property == null || callbackListener == null) return;
        
        List list = handlers.get(property); 
        if (list != null) {
            list.remove(callbackListener);
            if (list.isEmpty()) handlers.remove(property);
        }         
    }
    
    public void removeAll() {
        for (List list : handlers.values()) list.clear(); 
                
        handlers.clear(); 
        extendedHandlers.clear();
    }
    
    public void notify(String property, Object value) {
        if (property == null) return;
        
        for (Handler vh : vhandlers) {
            vh.valueChange( property, value ); 
        } 
        
        Set<Map.Entry<String,List>> entries = handlers.entrySet(); 
        for (Map.Entry<String,List> entry: entries) {
            String regex = entry.getKey();
            if (!match(property, regex)) continue;
            
            List list = entry.getValue(); 
            if (list == null) continue;
            
            for (Object callback: list) {
                invokeCallback(callback, value); 
            }
        }
        
        if (extendedHandlers.isEmpty()) return;
        
        for (Map map : extendedHandlers) {
            if (map.isEmpty()) continue; 
            
            Set<Map.Entry> sets = map.entrySet(); 
            for (Map.Entry entry: sets) 
            {
                String regex = (entry.getKey() == null? null: entry.getKey().toString()); 
                if (!match(property, regex)) continue;

                invokeCallback(entry.getValue(), value);
            }            
        }        
    }
    
    private boolean match(String name, String regex) 
    {
        if (name == null || regex == null) return false; 
        
        try {
            if ("*".equals(regex)) regex = ".*";

            return name.matches(regex);            
        } catch(Exception ex) {
            return false; 
        }
    }
    
    private void invokeCallback(Object callback, Object value) 
    {
        try 
        {
            if (callback == null) return;
            
            new CallbackHandlerProxy(callback).call(value);
        } 
        catch(Exception ex) 
        {
            System.out.println("[ValueChangeSupport_notify] failed caused by " + ex.getMessage());
            if (ClientContext.getCurrentContext().isDebugMode()) ex.printStackTrace(); 
        } 
    } 
    
    
    public static interface Handler {
        void valueChange( String name, Object value ); 
    }
}
