/*
 * SubFormPanelModel.java
 *
 * Created on September 12, 2013, 12:56 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.common;

import java.util.Map;

/**
 *
 * @author wflores
 */
public class SubFormPanelModel 
{
    
    public SubFormPanelModel() {
    }
    
    public Object getOpener() { return null; }
    public Map getOpenerParams(Object o) { return null; } 
    
    public Object getBinding() {
        return (provider == null? null: provider.getBinding()); 
    }
    
    public void refresh() {
        if (provider != null) provider.refresh();
    }
    
    public void reload() {
        if (provider != null) provider.reload();
    } 
    
    
    // <editor-fold defaultstate="collapsed" desc=" Provider ">
    
    private Provider provider;
    public final void setProvider(Provider provider) {
        this.provider = provider; 
    }
    
    public static interface Provider 
    {
        Object getBinding();
        
        void refresh();
        void reload(); 
    }
    
    // </editor-fold>
}
