/*
 * LogoffPanel.java
 *
 * Created on November 5, 2013, 2:24 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.platform;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

/**
 *
 * @author wflores
 */
class LogoffPanel extends WaitPanel 
{
    private boolean processing;

    public LogoffPanel() {
        super();
        
        final Runnable runnable = new Runnable() {
            public void run() {
                OSManager osm = OSManager.getInstance();
                osm.reinitialize(); 
                osm.getMainWindow().hideGlassPane(); 
            }
        };
        
        addComponentListener(new ComponentListener() {
            public void componentHidden(ComponentEvent e) {}
            public void componentMoved(ComponentEvent e) {}
            public void componentShown(ComponentEvent e) {}
            public void componentResized(ComponentEvent e) { 
                if (processing) return;
                
                processing = true; 
                OSManager.getInstance().scheduleTask(runnable, 500); 
            }       
        });         
    }
    
}
