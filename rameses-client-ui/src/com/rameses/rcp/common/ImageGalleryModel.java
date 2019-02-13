/*
 * ImageGalleryModel.java
 *
 * Created on April 21, 2014, 3:02 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author wflores
 */
public class ImageGalleryModel 
{
    private Map query = new HashMap(); 
    
    public Map getQuery() { return query; } 
    
    public int getCols() { return -1; } 
    
    public int getRows() { return 10; } 
    
    public List fetchList(Map params) {
        return null; 
    }
    
    public Object onselect(Object item) {
        return null; 
    }
    
    public Object onopen(Object item) {
        return null; 
    }
    
    public void reload() {
        if (provider != null) provider.reload(); 
    }
    
    public void refresh() {
        if (provider != null) provider.refresh(); 
    }    
    
    public void moveNext() {
        if (provider != null) provider.moveNext();
    }
    
    public void movePrevious() {
        if (provider != null) provider.movePrevious();
    }
    
    public void remove( int index ) {
        if ( provider != null ) provider.remove(index); 
    }
    
    // <editor-fold defaultstate="collapsed" desc=" Provider interface ">
    
    public static interface Provider {
        
        Object getBinding();
        void reload(); 
        void refresh();
        void moveNext();
        void movePrevious();
        void remove( int index);         
    } 
    
    
    private Provider provider;
    public void setProvider(Provider provider) {
        this.provider = provider; 
    }
    
    public Object getBinding() {
        return (provider == null? null: provider.getBinding()); 
    }
    
    // </editor-fold>    
}
