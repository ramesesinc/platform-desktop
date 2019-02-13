/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.common;

import java.util.List;
import java.util.Map;

/**
 *
 * @author wflores 
 */
public class CheckListModel {
    
    private final static int DEFAULT_ROW_COUNT = 2; 
    
    public int getRows() { 
        return DEFAULT_ROW_COUNT; 
    }
    
    public List fetchList( Map params ) {
        return null; 
    } 
    
    public void beforeSelect( Object o ) {
    }
    public void afterSelect( Object o ){
    }
    public void onselect( Object o ) {         
    } 
    
    
    // <editor-fold defaultstate="collapsed" desc=" UIProvider ">
    
    private UIProvider provider;    
    public final void setProvider(UIProvider provider) {
        this.provider = provider; 
    }
    
    public static interface UIProvider { 
        Object getBinding(); 
        void refresh(); 
        void reload(); 
    } 
    
    public Object getBinding() {
        return (provider == null ? null : provider.getBinding()); 
    }
    public void refresh() { 
        if ( provider != null ) {
            provider.refresh(); 
        }
    }
    public void reload() { 
        if ( provider != null ) {
            provider.reload();
        }
    }
    
    // </editor-fold>    
}
