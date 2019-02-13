/*
 * SigIdModel.java
 *
 * Created on December 19, 2013, 8:10 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.common;

/**
 *
 * @author wflores
 */
public class SigIdModel 
{
    private String title;
    private int width;
    private int height;
    
    public SigIdModel() {
        setTitle("Signature Capture"); 
        setWidth(640);
        setHeight(300);
    }
    
    public String getTitle() { return title; } 
    public void setTitle(String title) {
        this.title = title;
    }
    
    public int getPenWidth() { return 8; } 
    public int getImageXSize() { return 0; } 
    public int getImageYSize() { return 0; } 
    public String getKey() { return null; } 
    
    public int getWidth() { return width; } 
    public void setWidth(int width) {
        this.width = width;
    }
    
    public int getHeight() { return height; } 
    public void setHeight(int height) {
        this.height = height;
    }
        
    public void onselect(Object result) {
    }
    
    public void onclose() {
    }
    
    // <editor-fold defaultstate="collapsed" desc=" Provider ">
    
    public static interface SigInfo {
        String getSigString();
        String getKeyString();
        int getKeyReceipt();
        byte[] getImageData();
    }
    
    public static interface Provider {
        Object getBinding();
        void showDialog(SigIdModel model);
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
