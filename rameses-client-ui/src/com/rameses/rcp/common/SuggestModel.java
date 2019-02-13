/*
 * SuggestModel.java
 *
 * Created on December 17, 2013, 10:57 AM
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
public class SuggestModel 
{
    
    public SuggestModel() {
    }
    
    public int getRows() { return 10; } 
    
    public List fetchList(Map params) {
        return null; 
    } 
    
    // <editor-fold defaultstate="collapsed" desc=" Provider ">
    
    public static interface Provider 
    {
        Object getBinding();
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
