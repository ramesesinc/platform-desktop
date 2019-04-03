/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.common;

import com.rameses.rcp.framework.ClientContext;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 *
 * @author wflores
 */
public final class NotifierManager {
    
    private final static Object SERVICE_LOCK = new Object();
    private final static Object REGISTRY_LOCK = new Object(); 
    private final static NotifierManager instance = new NotifierManager(); 
    
    public static NotifierSession register( String id ) { 
        return instance.registerImpl( id, null, null ); 
    }
    
    public static NotifierSession register( String id, Object handler ) { 
        return instance.registerImpl( id, handler, null ); 
    }
    
    public static NotifierSession register( String id, Object handler, Map options ) { 
        return instance.registerImpl( id, handler, options ); 
    }

    public static String unregister( String id ) { 
        return instance.unregisterImpl( id ); 
    }
    
    public static void notify( String id ) {
        instance.notifyImpl( id ); 
    }
    
    private String uriString;
    private HashMap<String,NotifierHandlerProxy> handlers; 
    
    ExecutorService executor;
    ScheduledExecutorService scheduler; 
    
    NotifierManager() { 
        handlers = new HashMap(); 
        executor = Executors.newFixedThreadPool(10); 
        scheduler = Executors.newScheduledThreadPool(10); 
        
        ClientContext cctx = ClientContext.getCurrentContext();
        Map appenv = cctx.getAppEnv();

        String host = getConf(appenv, "notifier.host", null);
        if ( host == null || host.length() == 0 ) {
            System.out.println("notifier.host is not set in app env"); 
        }
        else {
            String action = getConf(appenv, "notifier.action", "notifier/subscribe");
            uriString = "ws://"+ host + "/" + action; 
        } 
    }

    String getUriString() {
        return uriString; 
    }
    
    private String getConf(Map map, String name, Object defaultValue) {
        Object value = (map == null ? null : map.get(name)); 
        if (value == null) {
            return (defaultValue == null ? null : defaultValue.toString()); 
        } else { 
            return value.toString(); 
        }
    }
    
    private NotifierSession registerImpl( String id, Object handler, Map options ) {
        synchronized( REGISTRY_LOCK ) { 
            if ( id == null ) 
                throw new RuntimeException("NotifierManager.register requires an 'id' parameter"); 
            
            NotifierHandlerProxy proxy = handlers.get( id ); 
            if ( proxy == null ) { 
                proxy = new NotifierHandlerProxy( id, options, this ); 
                handlers.put( id, proxy ); 
                proxy.subscribe(); 
            }
            return proxy.add( handler ); 
        } 
    } 
    
    private String unregisterImpl( String id ) {
        synchronized( REGISTRY_LOCK ) { 
            if ( id == null ) 
                throw new RuntimeException("NotifierManager.unregister requires an 'id' parameter"); 
            
            NotifierHandlerProxy proxy = handlers.get( id ); 
            if ( proxy == null ) return null; 
            
            proxy.removeAll(); 
            return id; 
        }
    }
    
    private void notifyImpl( String id ) {
        NotifierHandlerProxy proxy = handlers.get( id ); 
        if ( proxy == null ) return; 
        
        proxy.invokeHandlers(); 
    }
    
    void detach( String id ) {
        synchronized( REGISTRY_LOCK ) { 
            handlers.remove( id ); 
        }
    }    
} 
