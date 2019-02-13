/*
 * Services.java
 *
 * Created on January 15, 2014, 10:15 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.framework;

import com.rameses.rcp.framework.ClientContext.DesktopService;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author wflores
 */
public class Services 
{
    private static Object LOCK = new Object();
    private List<DesktopService> services; 
    
    Services() {
        services = new ArrayList(); 
    }

    public void add(ClientContext.DesktopService svc) {
        synchronized (LOCK) {
            if (svc == null) return;
            if (services.contains(svc)) return; 
            
            services.add(svc); 
        }
    }
    
    public void remove(ClientContext.DesktopService svc) {
        synchronized (LOCK) {
            if (svc == null) return;
            if (!services.contains(svc)) return; 
            
            services.remove(svc);
            svc.stop(); 
        }
    }    
    
    public void stop() {
        synchronized (LOCK) {
            while (!services.isEmpty()) {
                ClientContext.DesktopService svc = services.remove(0);
                try { svc.stop(); } catch(Throwable t) {;} 
            }
        } 
    }
}
