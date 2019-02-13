/*
 * TimerManager.java
 *
 * Created on December 18, 2013, 10:26 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.util;

import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author wflores 
 */
public class TimerManager 
{
    private static Object LOCKED = new Object();
    private static TimerManager instance;
    
    public static TimerManager getInstance() {
        if (instance == null) {
            instance = new TimerManager();
        }
        return instance;
    }
    
    
    private Timer timer;
    
    private TimerManager() {
        timer = new Timer(); 
    }

    public void cancel() {
        try { 
            timer.cancel(); 
        } catch(Throwable t) {
            System.out.println("[WARN] TimerManager.cancel error caused by " + t.getClass().getName() + ": " + t.getMessage());
        } 
    }
    
    public void purge() { 
        try { 
            timer.purge(); 
        } catch(Throwable t) {
            System.out.println("[WARN] TimerManager.purge error caused by " + t.getClass().getName() + ": " + t.getMessage());
        }
    }
    
    public void schedule(Runnable runnable) { 
        schedule(runnable, 100);
    }
    
    public void schedule(Runnable runnable, long delay) {
        schedule(new TimerTaskImpl(runnable), delay);
    }
    
    public void schedule(TimerTask task, long delay) {
        timer.schedule(task, delay); 
    }
    
    private class TimerTaskImpl extends TimerTask 
    {
        private Runnable runnable;
        
        TimerTaskImpl(Runnable runnable) {
            this.runnable = runnable;
        }
        
        public void run() {
            if (runnable == null) return;
         
            try { 
                runnable.run(); 
            } catch(Throwable t) {
                ErrorDialog.show(t); 
            }
        }
    }
}

