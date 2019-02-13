/*
 * RuntimeNotificationHandle.java
 *
 * Created on January 16, 2014, 12:35 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.framework;

import com.rameses.rcp.common.CallbackHandlerProxy;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author compaq
 */
public final class RuntimeNotificationHandle 
{
    private Binding binding;
    private HandlerProxy proxy;
    
    RuntimeNotificationHandle(Binding binding) {
        this.binding = binding; 
    } 

    public Binding getBinding() { return binding; } 
    
    public NotificationProvider getProvider() {
        return ClientContext.getCurrentContext().getNotificationProvider();
    }
    
    public void publish(Object data) {
        getProvider().sendMessage(data);
    }
    
    public void markAsRead(Object data) {
        getProvider().removeMessage(data); 
    }     
    
    public void register(Object callback) {
        Map options = new HashMap();
        options.put("onmessage", callback); 
        register(options); 
    }
    
    public void register(Map options) {
        if (options == null) options = new HashMap();
        
        proxy = new HandlerProxy(options); 
        getProvider().add(proxy); 
    }
    
    public void unregister() {
        if (proxy == null) return;
        
        proxy.cancelled = true;
        getProvider().remove(proxy);
        proxy.onClose(); 
        proxy = null; 
    }
    
    // <editor-fold defaultstate="collapsed" desc=" HandlerProxy "> 
    
    private static class HandlerProxy implements NotificationHandler 
    {
        private Map options;
        private CallbackHandlerProxy onmessageHandler;
        private CallbackHandlerProxy onreadHandler;
        private CallbackHandlerProxy oncloseHandler;
        private boolean cancelled;
        
        HandlerProxy(Map options) {
            this.options = options;
            
            Object source = get(options, "onmessage"); 
            if (source != null) onmessageHandler = new CallbackHandlerProxy(source); 
            
            source = get(options, "onread"); 
            if (source != null) onreadHandler = new CallbackHandlerProxy(source);             
            
            source = get(options, "onclose"); 
            if (source != null) oncloseHandler = new CallbackHandlerProxy(source); 
        }
        
        public void onMessage(Object data) {
            if (cancelled || onmessageHandler == null) return;
            onmessageHandler.call(data);
        }

        public void onRead(Object data) {
            if (cancelled || onreadHandler == null) return;
            onreadHandler.call(data);            
        }        

        public void onClose() {
            if (cancelled || oncloseHandler == null) return;
            oncloseHandler.call(); 
        } 
        
        private Integer getInt(Map map, String name) {
            try {
                return (Integer) map.get(name);
            } catch(Throwable t) { 
                return null; 
            }
        }

        private String getString(Map map, String name) {
            try {
                Object o = map.get(name);
                return (o == null? null: o.toString()); 
            } catch(Throwable t) { 
                return null; 
            }
        } 
        
        private Boolean getBool(Map map, String name) {
            try {
                return (Boolean) map.get(name);
            } catch(Throwable t) { 
                return null; 
            }
        } 
        
        private Object get(Map map, String name) {
            return (map == null? null: map.get(name)); 
        }
    }
    
    // </editor-fold>            
}
