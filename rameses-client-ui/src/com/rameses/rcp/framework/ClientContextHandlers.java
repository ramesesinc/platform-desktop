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
final class ClientContextHandlers {
    
    private final static ClientContextHandlers instance = new ClientContextHandlers(); 
    private final static Object LOCKED = new Object(); 

    public static void load( ClassLoader loader ) { 
        synchronized(LOCKED) {
            instance.loadImpl( loader );
        }
    }
    
    public static void stop() { 
        synchronized(LOCKED) {
            instance.stopImpl();
        }
    }
    
    
    private ClassLoader loader;
    private ExecutorService exec;
    private List<ClientContextHandler> handlers;
    
    ClientContextHandlers() {
        handlers = new ArrayList();
        exec = Executors.newFixedThreadPool(100); 
    }
    
    private void loadImpl( ClassLoader loader ) {
        if ( !handlers.isEmpty()) {
            stopImpl();
            handlers.clear(); 
        }
        
        ClassLoader loader1 = getClass().getClassLoader(); 
        ClassLoader loader0 = (loader == null ? loader1 : loader); 
        List<ClassLoader> loaders = new ArrayList(); 
        if ( !loaders.contains( loader0 )) {
            loaders.add( loader0 ); 
        }
        if ( !loaders.contains( loader1 )) {
            loaders.add( loader1 ); 
        }

        List list = new ArrayList();
        while ( !loaders.isEmpty()) {
            ClassLoader cl = loaders.remove(0); 
            Iterator itr = com.rameses.util.Service.providers( ClientContextHandler.class, cl ); 
            while (itr.hasNext()) { 
                Object o = itr.next(); 
                if ( !list.contains( o.getClass())) {
                    list.add( o.getClass()); 
                    
                    ClientContextHandler h = (ClientContextHandler) o;
                    handlers.add( h ); 
                    startImpl( h ); 
                }
            }
        }
        list.clear(); 
        
//        Iterator itr = com.rameses.util.Service.providers( ClientContextHandler.class, loader ); 
//        while (itr.hasNext()) { 
//            ClientContextHandler h = (ClientContextHandler) itr.next(); 
//            System.out.println("client context handler -> " + h);
//            handlers.add( h ); 
//            startImpl( h ); 
//        } 
        
        this.loader = loader; 
    }
    
    private void stopImpl() {
        ClientContextHandler[] values = handlers.toArray(new ClientContextHandler[]{}); 
        handlers.clear();
        
        for (ClientContextHandler h : values) { 
            stopImpl( h ); 
        } 
       
        try { 
            exec.shutdown(); 
        } catch(Throwable t) {
            //do nothing 
        }
        
        exec = Executors.newFixedThreadPool(100); 
    }
    
    private void stopImpl( final ClientContextHandler cch ) {
        exec.submit( new Runnable() {
            public void run() {
                cch.stop(); 
            }
        }); 
    }
    
    private void startImpl( final ClientContextHandler cch ) {
        exec.submit( new Runnable() {
            public void run() {
                System.out.println("start client context handler -> " + cch);
                cch.start(); 
            }
        }); 
    }
}
