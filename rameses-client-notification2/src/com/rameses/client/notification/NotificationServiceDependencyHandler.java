/*
 * NotificationServiceDependencyHandler.java
 *
 * Created on February 8, 2014, 11:48 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.client.notification;

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
public class NotificationServiceDependencyHandler implements DependencyHandler 
{
    public Class getAnnotation() { 
        return com.rameses.rcp.annotations.NotificationService.class; 
    }

    public Object getResource(Binding binding) {
        return new RuntimeHandle(binding);
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
    
    // <editor-fold defaultstate="collapsed" desc=" RuntimeHandle "> 
    
    public class RuntimeHandle 
    {
        NotificationServiceDependencyHandler root = NotificationServiceDependencyHandler.this;
        private Binding binding;
        
        RuntimeHandle(Binding binding) {
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
    } 
    
    // </editor-fold> 
}
