/*
 * NotificationService.java
 *
 * Created on February 8, 2014, 12:53 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.client.notification;

import com.rameses.rcp.framework.ClientContext;
import com.rameses.rcp.framework.NotificationProvider;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author wflores
 */
public final class NotificationService 
{
    
    private NotificationService() {}
    
    public static NotificationProvider getProvider() {
        return ClientContext.getCurrentContext().getNotificationProvider();
    }

    public static synchronized void sendMessage(Object data) {
        getProvider().sendMessage(data);
    }

    public static synchronized void removeMessage(Object data) {
        if (data == null) return;

        String objid = getBeanValueAsString(data, "objid");
        String notificationid = getBeanValueAsString(data, "notificationid");
        String recipientid = getBeanValueAsString(data, "recipientid");
        String groupid = getBeanValueAsString(data, "groupid");
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
 
    private static String getBeanValueAsString(Object bean, String name) {
        Object value = getBeanValue(bean, name);
        return (value == null? null: value.toString());
    }
    
    private static Object getBeanValue(Object bean, String name) {
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
    
    private static Method findGetMethod(Class clazz, String name) {
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
}
