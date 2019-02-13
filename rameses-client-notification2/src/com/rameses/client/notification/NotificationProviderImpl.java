/*
 * NotificationProviderImpl.java
 *
 * Created on January 14, 2014, 9:39 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.client.notification;

import com.rameses.rcp.framework.NotificationHandler;
import com.rameses.rcp.framework.NotificationProvider;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author wflores
 */
public final class NotificationProviderImpl implements NotificationProvider 
{
    private static Object LOCK = new Object();
    private List<NotificationHandler> handlers;
    
    public NotificationProviderImpl() {
        handlers = new ArrayList(); 
    }
    
    public void close() {
        synchronized (LOCK) {
            handlers.clear();
        }
    }
    
    public void add(NotificationHandler handler) {
        synchronized (LOCK) {
            if (handler == null) return;
            if (!handlers.contains(handler)) {
                handlers.add(handler);
            } 
        }
    }
    
    public boolean remove(NotificationHandler handler) {
        synchronized (LOCK) {
            if (handler == null) {
                return false; 
            } else { 
                return handlers.remove(handler); 
            } 
        } 
    } 
    
    public void sendMessage(Object data) {
        new Thread(new NotifyProcess(handlers, data)).start(); 
    } 
    
    public void removeMessage(Object data) {
        new Thread(new ReadProcess(handlers, data)).start(); 
    }
    
    // <editor-fold defaultstate="collapsed" desc=" NotifyProcess ">
    
    private class NotifyProcess implements Runnable
    {
        private List<NotificationHandler> handlers;
        private Object data;
        
        NotifyProcess(List<NotificationHandler> handlers, Object data) {
            this.handlers = handlers; 
            this.data = data; 
        }

        public void run() {
            synchronized (LOCK) { 
                if (handlers == null) return;

                for (int i=0; i<handlers.size(); i++) {
                    NotificationHandler handler = handlers.get(i);
                    if (handler == null) continue;
                    
                    try { 
                        handler.onMessage(data); 
                    } catch(Throwable t) {
                        System.out.println("handler error onMessage caused by " + t.getMessage());
                    } 
                }
                handlers = null;
                data = null; 
            } 
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ReadProcess ">
    
    private class ReadProcess implements Runnable
    {
        private List<NotificationHandler> handlers;
        private Object data;
        
        ReadProcess(List<NotificationHandler> handlers, Object data) {
            this.handlers = handlers; 
            this.data = data; 
        }

        public void run() {
            synchronized (LOCK) { 
                if (handlers == null) return;

                for (int i=0; i<handlers.size(); i++) {
                    NotificationHandler handler = handlers.get(i);
                    if (handler == null) continue;
                    
                    try { 
                        handler.onRead(data); 
                    } catch(Throwable t) {
                        System.out.println("handler error onMessage caused by " + t.getMessage());
                    } 
                }
                handlers = null;
                data = null; 
            }             
        }
    }
    
    // </editor-fold>
}
