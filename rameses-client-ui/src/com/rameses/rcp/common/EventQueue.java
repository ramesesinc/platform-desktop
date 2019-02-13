/*
 * EventQueue.java
 *
 * Created on April 29, 2014, 2:29 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.common;

/**
 *
 * @author wflores
 */
public final class EventQueue 
{
    public static void invokeLater(Object callback) {
        invokeLater(callback, null);
    }
    
    public static void invokeLater(Object callback, Object value) {
        if (callback == null) return;
        
        invokeLater(new CallbackRunnable(callback, value));
    }
    
    public static void invokeLater(Runnable runnable) {
        if (runnable == null) return;
        
        java.awt.EventQueue.invokeLater(runnable); 
    }
    
    
    private EventQueue() {
    }
    
    private static class CallbackRunnable implements Runnable 
    {
        private Object callback;
        private Object value;
        
        CallbackRunnable(Object callback, Object value) {
            this.callback = callback; 
            this.value = value; 
        }
        
        public void run() {
            try {
                if (callback == null) return;
                
                new CallbackHandlerProxy(callback).call(value); 
            } catch(Throwable t) {
                MsgBox.err(t); 
            }
        }
    }
}
