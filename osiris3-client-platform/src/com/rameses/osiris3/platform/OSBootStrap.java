/*
 * OSBootStrap.java
 *
 * Created on October 24, 2013, 9:17 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.platform;

import java.util.Map;
import javax.swing.UIManager;

/**
 *
 * @author wflores
 */
public final class OSBootStrap 
{
    public static void main(String[] args) throws Exception {
        try {
            String plaf = UIManager.getSystemLookAndFeelClassName();
            UIManager.setLookAndFeel(plaf);
        } catch(Throwable t) {;}
                
        OSBootStrap bootstrap = new OSBootStrap();
        bootstrap.showStartup();
        bootstrap.startUpdates();
    }
    
    
    private OSBootStrap() {
    }
    
    private void showStartup() {
        try {
            OSManager osm = OSManager.getInstance();
            osm.init();
            osm.getStartupWindow().setVisible(true);
        } catch(Throwable t) {
            ErrorDialog.show(t);
            terminate();
        }
    }
   
    private void startUpdates() {
        try { 
            UpdateCenterHandler handler = new UpdateCenterHandler();
            OSPlatformLoader.DownloadResult result = OSPlatformLoader.downloadUpdates(handler);
            Map env = result.getEnv();
            prepareLookAndFeel(env); 
            
            OSManager osm = OSManager.getInstance(); 
            osm.setAppLoader(result.deriveAppLoader()); 
            osm.getMainWindow().show(); 
        } catch(Throwable t) {
            ErrorDialog.show(t);
            terminate();
        }
    }
    
    private void prepareLookAndFeel(Map env) {
        try {
            String plaf = (String) env.get("plaf");
            if (plaf != null && plaf.trim().length() > 0) {
                UIManager.setLookAndFeel(plaf);
            } else if (System.getProperty("os.name","").toLowerCase().indexOf("windows") >= 0) {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } else {
                //plaf = "com.jgoodies.plaf.plastic.PlasticXPLookAndFeel";
            }
        } catch(Throwable ign) {;} 
        
        LookAndFeelCustomizer.install(); 
    }
    
    private void terminate() {
        try {
            System.exit(0);
        } catch(Throwable t) {;}
    }
    
    private class UpdateCenterHandler implements UpdateCenter.Handler {
        public void onmessage(String message) {
            OSManager.getInstance().getStartupWindow().setTextValue(message); 
        }

        public void ondownload(ModuleEntry me) {
            String msg = "Downloading " + me.getFilename() + "...";
            onmessage(msg);
        }

        public void oncomplete() { 
            onmessage("Initializing please wait..."); 
        } 
    }
}
