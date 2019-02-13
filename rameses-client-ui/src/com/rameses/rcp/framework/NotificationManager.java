/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.framework;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author wflores 
 */
public final class NotificationManager {
    
    private final static ExecutorService THREAD_POOL = Executors.newFixedThreadPool(100); 
    private final static NotificationManager instance = new NotificationManager(); 
    private final static Object HANDLER_LOCKED = new Object(); 
    private final static Object RENDERER_LOCKED = new Object(); 
    
    static synchronized void reset() { 
        try { 
            instance.resetImpl(); 
        } catch(Throwable t) {
            t.printStackTrace(); 
        }
    } 
    
    public static NotificationProvider getDefaultProvider() { 
        try { 
            return instance.getProviders().get(0); 
        } catch(ArrayIndexOutOfBoundsException aie) { 
            //do nothing 
        } catch(IndexOutOfBoundsException ie) { 
            //do nothing 
        } catch(Throwable t) {
            t.printStackTrace(); 
        } 
        return instance.getEmptyProvider();  
    } 
    
    public static void addHandler( NotificationHandler handler ) { 
        synchronized ( HANDLER_LOCKED ) {
            if ( handler == null ) return; 
            
            instance.handlers.remove( handler ); 
            instance.handlers.add( handler ); 
        }
    }
    public static void removeHandler( NotificationHandler handler ) { 
        synchronized ( HANDLER_LOCKED ) {
            if ( handler != null ) {
                instance.handlers.remove( handler ); 
            }
        } 
    }
    public static List<NotificationHandler> getHandlers() {
        return instance.handlers; 
    }
    
    public static void addRenderer( NotificationRenderer renderer ) { 
        synchronized ( RENDERER_LOCKED ) {
            if ( renderer == null ) return; 
            
            instance.renderers.remove( renderer ); 
            instance.renderers.add( renderer ); 
        }
    }
    public static void removeHandler( NotificationRenderer renderer ) { 
        synchronized ( RENDERER_LOCKED ) { 
            if ( renderer != null ) { 
                instance.renderers.remove( renderer ); 
            } 
        } 
    } 
    public static void updateRenderers() { 
        for ( int i=0; i<instance.renderers.size(); i++ ) {
            try {
                THREAD_POOL.submit(new RendererRefreshProc( instance.renderers.get(i) )); 
            } catch(Throwable t) {
                continue; 
            }
        }
    }
    
    
    private final List<NotificationProvider> providers = new ArrayList();
    private final List<NotificationRenderer> renderers = new ArrayList();
    private final List<NotificationHandler> handlers = new ArrayList();
    
    private NotificationProvider emptyProvider;
    private boolean allow_fetch_providers = true; 
    
    private void resetImpl() { 
        allow_fetch_providers = true; 
        renderers.clear();
        handlers.clear();
    }
    
    private synchronized List<NotificationProvider> getProviders() { 
        if ( allow_fetch_providers ) {  
            Iterator itr = com.rameses.util.Service.providers(NotificationProvider.class, ClientContext.getCurrentContext().getClassLoader()); 
            allow_fetch_providers = false; 
            providers.clear(); 

            while ( itr.hasNext() ) { 
                Object o = itr.next(); 
                if ( o instanceof NotificationProvider ) {
                    providers.add((NotificationProvider) o); 
                } 
            } 
        } 
        return providers; 
    }
    
    private NotificationProvider getEmptyProvider() {
        if ( emptyProvider == null ) { 
            emptyProvider = new EmptyNotificationProvider(); 
        } 
        return emptyProvider; 
    }
    
    // <editor-fold defaultstate="collapsed" desc=" RendererRefreshProc "> 
    
    private static class RendererRefreshProc implements Runnable {
        
        private NotificationRenderer handler; 
        
        RendererRefreshProc( NotificationRenderer handler ) {
            this.handler = handler; 
        }

        public void run() {
            if ( handler != null ) {
                handler.refresh(); 
            } 
        } 
    } 
    
    // </editor-fold>
}
