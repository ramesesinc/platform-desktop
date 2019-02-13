/*
 * AbstractNotificationService.java
 *
 * Created on February 6, 2014, 11:06 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.client.notification;

import com.rameses.rcp.framework.ClientContext;
import com.rameses.service.ScriptServiceContext;
import com.rameses.service.ServiceProxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author wflores
 */
public abstract class AbstractNotificationService 
{
    private final static Object LOCK = new Object();
    
    private ServiceProxy serviceProxy;
    
    public AbstractNotificationService() {
    }

    public abstract String getName();
    
    protected ServiceProxy getProxy() {
        if (serviceProxy == null) {
            serviceProxy = create(getName()); 
        }
        return serviceProxy;
    }
    
    public void getNotified(Map params) {
        invoke("getNotified", params);
    }
    
    public void removeMessage(Map params) {
        invoke("removeMessage", params);
    }
    
    public List<Map> getList(Map params) {
        return (List<Map>) invoke("getList", params);
    }
    
    public Object invoke(String action) {
        return invoke(action, null); 
    }

    public Object invoke(String action, Object arg) {
        try { 
            Object[] params = new Object[]{};
            if (arg != null) params = new Object[]{arg}; 
            
            return getProxy().invoke(action, params); 
        } catch(RuntimeException re) {
            throw re;
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage(), e); 
        }    
    }    
    
    private ServiceProxy create(String name) {
        synchronized (LOCK) { 
            ClientContext cctx = ClientContext.getCurrentContext(); 
            Map appenv = cctx.getAppEnv();
            Map newenv = new HashMap();
            newenv.put("app.host",    get(appenv, "notification.host", appenv.get("app.host")));
            newenv.put("app.context", get(appenv, "notification.context", appenv.get("app.context")));
            newenv.put("app.cluster", get(appenv, "app.cluster", appenv.get("app.cluster")));
            newenv.put("readTimeout", get(appenv, "readTimeout", "20000"));

            ScriptServiceContext ssc = new ScriptServiceContext(newenv); 
            return ssc.create(name, cctx.getHeaders()); 
        } 
    }
    
    private String get(Map map, String name, Object defaultValue) {
        Object value = (map == null? null: map.get(name));
        if (value == null) {
            return (defaultValue == null? null: defaultValue.toString()); 
        } else {
            return value.toString(); 
        }
    }
    
}
