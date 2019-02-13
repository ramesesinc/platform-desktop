/*
 * ContentPane.java
 *
 * Created on October 17, 2013, 1:52 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.platform.interfaces;

import java.util.Map;

/**
 *
 * @author wflores
 */
public interface ContentPane 
{
    Object getClientProperty(Object key);
    void putClientProperty(Object key, Object value); 

    ContentPane.View getView();     
    boolean isCanClose();    
    void close();
    
    void activate();
    
    
    public static interface View { 
        Object getClientProperty(Object key);
        void putClientProperty(Object key, Object value); 
        
        Map getInfo();
        void showInfo(); 
    } 
}
