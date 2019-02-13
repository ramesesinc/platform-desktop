/*
 * OSAppLoader.java
 *
 * Created on October 29, 2013, 5:33 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.platform;

import com.rameses.platform.interfaces.AppLoader;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author wflores
 */
class OSAppLoader 
{
    private AppLoader appLoader; 
    private Map env;
    private URL[] urls;
    
    private AppLoader activeAppLoader;
    private ClassLoader activeClassLoader;
    
    public OSAppLoader(AppLoader appLoader, Map env, URL[] urls) {
        this.appLoader = appLoader;
        this.env = env;
        this.urls = urls; 
    } 
    
    public void load(OSPlatform platform) {
        try { 
            ClassLoader newClassLoader = null; 
            if (urls != null) { 
                newClassLoader = new URLClassLoader(urls, OSManager.getOriginalClassLoader()); 
            } else { 
                newClassLoader = OSManager.getOriginalClassLoader();
            } 
            
            String loaderName = (String) env.get("app.loader"); 
            if (loaderName == null || loaderName.trim().length() == 0) 
                throw new NullPointerException("app.loader must be provided in the ENV"); 
            
            AppLoader newAppLoader = (AppLoader) newClassLoader.loadClass(loaderName).newInstance(); 
            
            Map newEnv = new HashMap();
            if (env != null) newEnv.putAll(env); 
            
            newAppLoader.load(newClassLoader, newEnv, platform); 
            activeAppLoader = newAppLoader; 
            activeClassLoader = newClassLoader; 
        } catch(RuntimeException re) {
            throw re; 
        } catch(Throwable t) {
            throw new RuntimeException(t.getMessage(), t); 
        }
    }
    
    public void destroy() {
        if (activeAppLoader == null) return;
        
        activeAppLoader = null; 
        activeClassLoader = null; 
    }
    
    public ClassLoader getClassLoader() {
        if (activeClassLoader == null) {
            return OSManager.getOriginalClassLoader(); 
        }
        return activeClassLoader; 
    }
}
