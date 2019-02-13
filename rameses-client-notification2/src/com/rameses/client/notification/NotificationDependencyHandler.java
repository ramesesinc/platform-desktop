/*
 * NotificationDependencyHandler.java
 *
 * Created on February 8, 2014, 11:48 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.client.notification;

import com.rameses.rcp.common.CallbackHandler;
import com.rameses.rcp.common.CallbackHandlerProxy;
import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.rcp.framework.DependencyHandler;
import com.rameses.rcp.framework.NotificationHandler;
import com.rameses.rcp.framework.NotificationProvider;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author wflores
 */
public class NotificationDependencyHandler implements DependencyHandler 
{
    public Class getAnnotation() { 
        return com.rameses.rcp.annotations.Notification.class; 
    }

    public Object getResource(Binding binding) {
        return new RuntimeNotificationHandle(binding);
    }
    
    
    private String getBeanValueAsString(Object bean, String name) {
        Object value = getBeanValue(bean, name);
        return (value == null? null: value.toString());
    }
    
    private Object getBeanValue(Object bean, String name) {
        if (bean == null) return null;
        if (bean instanceof Map) { 
            return ((Map) bean).get(name); 
        }
        
        Class beanClass = bean.getClass();
        Method method = findGetMethod(beanClass, "notificationid"); 
        if (method == null) return null;
        
        try { 
            return method.invoke(bean, new Object[]{}); 
        } catch(Throwable t) {
            System.out.println("failed to get value from method ["+method+"] caused by " + t.getMessage());
            return null; 
        }
    }
    
    private Method findGetMethod(Class clazz, String name) {
        try { 
            if (name == null || name.length() <= 3) return null; 

            String methodName = "get" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
            for (Method method : clazz.getMethods()) {
                if (!method.getName().equals(methodName)) continue;
                if ("void".equals(method.getReturnType().toString())) continue;
                
                Class[] paramTypes = method.getParameterTypes();
                if (paramTypes != null && paramTypes.length > 1) continue;
                
                return method;
            }
            return null;
        } catch(Throwable t) {
            return null; 
        }
    }  
       
    private NotificationProvider getProvider() {
        return ClientContext.getCurrentContext().getNotificationProvider();
    }    
    
    // <editor-fold defaultstate="collapsed" desc=" RuntimeNotificationHandle "> 
    
    public class RuntimeNotificationHandle 
    {
        NotificationDependencyHandler root = NotificationDependencyHandler.this;
        private Binding binding;
        
        RuntimeNotificationHandle(Binding binding) {
            this.binding = binding; 
        }
        
        public Binding getBinding() { return binding; } 

        public NotificationProvider getProvider() {
            return root.getProvider();
        }

        public void sendMessage(Object data) {
            getProvider().sendMessage(data);
        }

        public void removeMessage(Object data) {
            if (data == null) return;

            String type = root.getBeanValueAsString(data, "type");            
            String objid = root.getBeanValueAsString(data, "objid");
            String notificationid = root.getBeanValueAsString(data, "notificationid");
            String groupid = root.getBeanValueAsString(data, "groupid");            
            String recipientid = root.getBeanValueAsString(data, "recipientid");
            
            Map params = new HashMap();
            params.put("objid", objid);
            params.put("notificationid", notificationid);
            if (recipientid != null && recipientid.trim().length() > 0) { 
                new UserNotificationService().removeMessage(params);
            } else if (groupid != null && groupid.trim().length() > 0) { 
                new GroupNotificationService().removeMessage(params); 
            } 
            getProvider().removeMessage(data); 
        }
        
        public RuntimeEventHandle register(Object callback) {
            Map options = new HashMap();
            options.put("onmessage", callback); 
            return register(options); 
        }

        public RuntimeEventHandle register(Map options) {
            if (options == null) options = new HashMap();

            return new RuntimeEventHandle(options);
        }        
    } 
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" RuntimeEventHandle "> 
    
    public class RuntimeEventHandle 
    {
        NotificationDependencyHandler root = NotificationDependencyHandler.this;
        private HandlerProxy proxy;
        
        RuntimeEventHandle(Map options) { 
            proxy = new HandlerProxy(options); 
            root.getProvider().add(proxy); 
            options.put("close", new CloseHandler(this));
        }
        
        public void unregister() {
            if (proxy == null) return;

            proxy.cancelled = true;
            root.getProvider().remove(proxy);
            proxy.onClose(); 
            proxy = null;             
        }
        
        public void close() {
            unregister(); 
        }
    }
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" CloseHandler "> 
    
    public class CloseHandler implements CallbackHandler
    {
        private RuntimeEventHandle event;
        
        CloseHandler(RuntimeEventHandle event) {
            this.event = event;
        }
        
        public Object call() {
            event.unregister(); 
            return null; 
        }

        public Object call(Object arg) {
            event.unregister(); 
            return null; 
        }

        public Object call(Object[] args) {
            event.unregister(); 
            return null; 
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" HandlerProxy "> 
    
    private class HandlerProxy implements NotificationHandler 
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
