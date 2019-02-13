/*
 * FingerPrintModel.java
 *
 * Created on December 8, 2013, 11:19 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.common;

/**
 *
 * @author wflores
 */
public class FingerPrintModel 
{
    public final static int RIGHT_THUMB  = 1;
    public final static int RIGHT_INDEX  = 2;
    public final static int RIGHT_MIDDLE = 4;
    public final static int RIGHT_RING   = 8;
    public final static int RIGHT_LITTLE = 16;

    public final static int LEFT_THUMB   = 32;
    public final static int LEFT_INDEX   = 64;
    public final static int LEFT_MIDDLE  = 128;
    public final static int LEFT_RING    = 256;
    public final static int LEFT_LITTLE  = 512;
    
    public FingerPrintModel() {
    }
    
    public String getTitle() { return "FingerPrint Capture"; }  
    
    public int getFingerType() { 
        return LEFT_THUMB + RIGHT_THUMB; 
    } 
    
    public void onselect(Object resultInfo) {
    }
    
    public void onclose() {
    }
    
    
    // <editor-fold defaultstate="collapsed" desc=" Provider ">
    
    public static interface Provider 
    {
        Object getBinding();
        void showDialog(FingerPrintModel model);
    }

    
    private Provider provider;
    
    public void setProvider(Provider provider) { 
        this.provider = provider;  
    } 
    
    public Object getBinding() { 
        return (provider == null? null: provider.getBinding()); 
    } 
    
    public void showDialog() { 
        if (provider != null) provider.showDialog(this); 
    } 
    
    // </editor-fold>
}
