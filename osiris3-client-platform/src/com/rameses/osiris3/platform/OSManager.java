/*
 * OSManager.java
 *
 * Created on October 24, 2013, 9:20 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.platform;

import java.awt.Container;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JLabel;
import javax.swing.JMenuBar;

/**
 *
 * @author wflores
 */
final class OSManager 
{
    // <editor-fold defaultstate="collapsed" desc=" static methods "> 
    
    private static ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
    
    public static ClassLoader getOriginalClassLoader() {
        return originalClassLoader;
    } 

    private static OSManager instance;        
    public static OSManager getInstance() {
        if (instance == null) instance = new OSManager();

        return instance;
    } 
    
    static final Object VIEW_LOCK = new OSViewLock();
    static class OSViewLock {}    

    // </editor-fold>
    
    private Object treeLock = new Object();
    private OSMainWindow osMainWindow;
    private OSPlatform osPlatform;
    private JMenuBar menubar;
    private Container toolbarView;
    private Container desktopView;
    private Container statusView;
    private Map<Object,Object> properties = new Hashtable<Object,Object>();    
    private Map<String,OSView> views = new LinkedHashMap(); 
    
    private OSManager() {
    }
    
    void init() {
        osMainWindow = new OSMainWindow();
        osPlatform = new OSPlatform(this);
    } 
    
    OSMainWindow getMainWindow() { return osMainWindow; }
    OSPlatform getPlatform() { return osPlatform; } 
    
    void reinitialize() {
        osMainWindow.reinitialize();
        osPlatform = new OSPlatform(this); 
        if (osAppLoader == null) {
            osMainWindow.setContent(new JLabel("")); 
        } else {
            WaitPanel wp = WaitPanel.create("Loading please wait...");
            osMainWindow.showInGlassPane(wp, null); 
            osAppLoader.load(osPlatform); 
        } 
        osMainWindow.hideGlassPane();
    }
    
    void startUpdate() {
        startUpdateImpl(false);
    }
    
    void retryUpdate() { 
        startUpdateImpl(true); 
    }
    
    private void startUpdateImpl(final boolean retry) {
        DownloadPanel pnl = null; 
        try { 
            pnl = new DownloadPanel(); 
            osMainWindow.setContent(pnl); 
            osMainWindow.show();
        } catch(Throwable t) {
            ErrorDialog.show(t); 
            t.printStackTrace();
        } 
        
        final DownloadPanel dpnl = pnl;
        Runnable runnable = new Runnable() {
            public void run() {
                if (retry) { 
                    try { Thread.sleep(1000); }catch(Throwable t){;} 
                } 
                
                dpnl.startDownload(); 
            } 
        }; 
        new Thread(runnable).start(); 
    }   
        
    boolean registerView(String id, OSView view) {
        synchronized (VIEW_LOCK) {
            if (id == null || id.trim().length() == 0 || view == null) return false;
            if (views.containsKey(id)) return false;

            views.put(id, view); 
            return true; 
        }
    }
    
    OSView unregisterView(String id) {
        synchronized (VIEW_LOCK) {
            if (id == null || id.trim().length() == 0) return null;

            return views.remove(id); 
        } 
    }
    
    OSView lookupView(String id) {
        if (id == null) return null;
        
        return views.get(id); 
    }
    
    boolean containsView(String id) {
        return (lookupView(id) != null); 
    }
    
    void unregisterAllViews() {
        synchronized (VIEW_LOCK) { 
            views.clear(); 
        } 
    }
    
    List<OSView> findViews(String type) {
        List<OSView> list = new ArrayList();
        if (type == null) return list;
        
        Iterator<OSView> itr = views.values().iterator();
        while (itr.hasNext()) {
            OSView vw = itr.next();
            if (!type.equals(vw.getType())) continue; 
            
            list.add(vw);
        } 
        return list;
    }
    
    Iterator<OSView> findAll() {
        return views.values().iterator(); 
    } 
        
    private OSStartupWindow startupWindow;    
    OSStartupWindow getStartupWindow() {
        if (startupWindow == null) {
            startupWindow = new OSStartupWindow();
        }
        return startupWindow; 
    } 
    
    void closeStartupWindow() {
        if (startupWindow == null) return;

        startupWindow.dispose();
        startupWindow = null; 
    } 
    
    private OSAppLoader osAppLoader;
    OSAppLoader getAppLoader() { return osAppLoader; } 
    void setAppLoader(OSAppLoader osAppLoader) {
        OSAppLoader old = this.osAppLoader;
        if (old != null) old.destroy();
        
        this.osAppLoader = osAppLoader; 
        if (osAppLoader != null) osAppLoader.load(osPlatform); 
    }    
    
    // <editor-fold defaultstate="collapsed" desc=" screen lock support ">
 
    private OSScreenLock osScreenLock; 
    OSScreenLock getScreenLock() { return osScreenLock; } 
    void setScreenLock(OSScreenLock osScreenLock) {
        OSScreenLock old = this.osScreenLock;
        if (old != null) unregisterView(old.getName()); 

        this.osScreenLock = osScreenLock; 
    }
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" Scheduler support ">
    
    private Scheduler scheduler;
    
    void scheduleTask(Runnable runnable, long delay) {
        if (scheduler == null) scheduler = new Scheduler(); 
        
        scheduler.addTask(runnable, delay);
    }
    
    void stopScheduledTasks() {
        if (scheduler != null) {
            scheduler.stopAllTasks();
        }
    }
    
    private class Scheduler 
    {
        private Timer timer;
        private boolean enabled; 
        
        Scheduler() {
            timer = new Timer();
            enabled = true; 
        }
        
        void setEnabled(boolean enabled) {
            this.enabled = enabled; 
        }
                
        void addTask(Runnable runnable, long delay) {
            if (!enabled) return;
            
            ScheduledTask task = new ScheduledTask(runnable);
            timer.schedule(task, delay); 
        } 
        
        void stopAllTasks() {
            try { timer.cancel(); } catch(Throwable t) { t.printStackTrace(); }
            try { timer.purge(); } catch(Throwable t) {;} 

            timer = new Timer(); 
        }
    } 
    
    private class ScheduledTask extends TimerTask 
    {
        private Runnable target;
        
        ScheduledTask(Runnable target) {
            this.target = target; 
        }
        
        public void run() {
            if (target != null) target.run(); 
        }        
    }
    
    // </editor-fold>

}
