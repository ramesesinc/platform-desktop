/*
 * ActionFieldModel.java
 *
 * Created on December 7, 2013, 10:43 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.common;

/**
 *
 * @author wflores
 */
public class ActionFieldModel 
{
    
    public ActionFieldModel() {
    }
    
    public Object invoke(Object info) {
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
