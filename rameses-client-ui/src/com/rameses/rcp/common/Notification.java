/*
 * Notification.java
 *
 * Created on January 20, 2014, 10:03 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.common;

import com.rameses.rcp.framework.ClientContext;
import com.rameses.rcp.framework.NotificationHandler;
import com.rameses.rcp.framework.NotificationProvider;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author wflores
 */
public final class Notification 
{
    public static NotificationProvider getProvider() {
        return ClientContext.getCurrentContext().getNotificationProvider(); 
    }
    
    public static void sendMessage(Object data) {
        getProvider().sendMessage(data);
    }
    
    public static void removeMessage(Object data) {
        getProvider().removeMessage(data); 
    }    
    
    public static RuntimeHandle register(Object callback) {
        Map options = new HashMap();
        options.put("onmessage", callback); 
        return register(options); 
    }
    
    public static RuntimeHandle register(Map options) {
        if (options == null) options = new HashMap();

        return new RuntimeHandle(options);
    }
        
    private Notification() {
    }
    
    // <editor-fold defaultstate="collapsed" desc=" RuntimeHandle "> 
    
    public static class RuntimeHandle 
    {
        private HandlerProxy proxy;
        
        RuntimeHandle(Map options) { 
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
    }
    
    // </editor-fold>
    
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
