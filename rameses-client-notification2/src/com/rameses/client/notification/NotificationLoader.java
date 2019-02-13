/*
 * NotificationLoader.java
 *
 * Created on March 6, 2014, 7:24 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.client.notification;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author wflores 
 */
public final class NotificationLoader 
{
    private static NotificationLoader instance = new NotificationLoader();
    
    public static synchronized void add(Runnable runnable) {
        if (runnable != null) instance.runnables.add(runnable);
    }
    
    public static synchronized void execute() {
        while (!instance.runnables.isEmpty()) {
            Runnable runnable = instance.runnables.remove(0);
            try { 
                runnable.run(); 
            } catch(Throwable t) {
                t.printStackTrace(); 
            }
        }
    }
    
    private List<Runnable> runnables;
    
    private NotificationLoader() {
        runnables = new ArrayList();
    }
}
