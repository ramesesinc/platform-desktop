/*
 * TabbedListModel.java
 *
 * Created on September 2, 2013, 11:05 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.common;

import java.util.List;
import java.util.Map;

/**
 *
 * @author wflores
 */
public class TabbedListModel {
    
    public TabbedListModel() {
    }
    
    public List<Map> getList() { return null; } 
    
    public boolean beforeSelect(Object item) { return true; } 
    public boolean beforeSelect(Object item, int index) {
        return beforeSelect(item); 
    } 
    
    public void onselect(Object item) {}
        
    public Object getBinding() {
        return (provider == null? null: provider.getBinding());
    }
        
    public void reload() {
        if (provider != null) provider.reload(); 
    }
    
    public void refresh() {
        if (provider != null) provider.refresh(); 
    }
    
    // <editor-fold defaultstate="collapsed" desc=" Provider interface ">

    private TabbedListModel.Provider provider; 

    public final void setProvider(TabbedListModel.Provider provider) {
        this.provider = provider; 
    } 
    
    public static interface Provider {
        Object getBinding();         
        void reload();
        void refresh();
    } 
    
    // </editor-fold>     
}
