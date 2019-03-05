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
        
        Iterator itr = com.rameses.util.Service.providers( ClientContextHandler.class, loader ); 
        while (itr.hasNext()) { 
            ClientContextHandler h = (ClientContextHandler) itr.next(); 
            handlers.add( h ); 
            startImpl( h ); 
        } 
        
        this.loader = loader; 
    }
    
    private void stopImpl() {
        ClientContextHandler[] values = handlers.toArray(new ClientContextHandler[]{}); 
        handlers.clear();
        
        for (ClientContextHandler h : values) { 
            stopImpl( h ); 
        } 
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
                cch.start(); 
            }
        }); 
    }
}
