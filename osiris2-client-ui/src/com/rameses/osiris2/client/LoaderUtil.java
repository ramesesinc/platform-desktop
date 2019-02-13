/*
 * LoaderUtil.java
 *
 * Created on January 16, 2014, 9:15 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris2.client;

import com.rameses.osiris2.Invoker;
import com.rameses.osiris2.SessionContext;
import java.util.List;

/**
 *
 * @author wflores
 */
class LoaderUtil 
{
    private static Object LOCK = new Object();
    
    public static void runLoaderAfter() {
        Runnable runnable = new Runnable() {
            public void run() {
                synchronized (LOCK) { 
                    SessionContext app = OsirisContext.getSession();
                    List<Invoker> invokers = app.getInvokers("loader:after");
                    for (Invoker inv : invokers) {
                        inv.getProperties().put("target", "process");
                        try {
                            InvokerUtil.invoke(inv, null);
                        } catch(RuntimeException re) {
                            throw re;
                        } catch(Exception ex) {
                            throw new IllegalStateException(ex.getMessage(), ex);
                        } 
                    } 
                } 
            } 
        };
        new Thread(runnable).start();         
    }
    
    private LoaderUtil() {
    }    
}
