/*
 * CameraModel.java
 *
 * Created on December 5, 2013, 12:13 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.common;

/**
 *
 * @author wflores
 */
public class CameraModel 
{
    private String title;
    private int width;
    private int height;
    private boolean autoCloseOnSelect;    
    private boolean autoOpenMode;
    private boolean alwaysOnTop;
    private boolean modal;
    
    public CameraModel() {
        setAutoCloseOnSelect(true);
        setModal(true);        
        setTitle("Camera"); 
        setWidth(320);
        setHeight(240);         
    }
    
    public String getTitle() { return title; } 
    public void setTitle(String title) {
        this.title = title; 
    }
    
    public int getWidth() { return width; } 
    public void setWidth(int width) {
        this.width = width;
    }
    
    public int getHeight() { return height; } 
    public void setHeight(int height) {
        this.height = height;
    }
    
    public boolean isAutoCloseOnSelect() { return autoCloseOnSelect; } 
    public void setAutoCloseOnSelect(boolean autoCloseOnSelect) {
        this.autoCloseOnSelect = autoCloseOnSelect; 
    }
    
    public boolean isAutoOpenMode() { return autoOpenMode; } 
    public void setAutoOpenMode(boolean autoOpenMode) {
        this.autoOpenMode = autoOpenMode; 
    }
    
    public boolean isAlwaysOnTop() { return alwaysOnTop; } 
    public void setAlwaysOnTop(boolean alwaysOnTop) {
        this.alwaysOnTop = alwaysOnTop;
    }
    
    public boolean isModal() { return modal; } 
    public void setModal(boolean modal) {
        this.modal = modal;
    }
    
    public void onselect(byte[] bytes) {
    }
    
    public void onclose() {
    }
        
    
    // <editor-fold defaultstate="collapsed" desc=" Provider ">
    
    public static interface Provider 
    {
        Object getBinding();
        void showDialog(CameraModel model);
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
    
    
    public static interface ViewerProvider 
    {
        void close();
    }
    
    private ViewerProvider viewerProvider;
    public void setViewerProvider(ViewerProvider viewerProvider) {
        this.viewerProvider = viewerProvider;
    }
    
    public void close() {
        if (viewerProvider != null) viewerProvider.close(); 
    } 
    
    // </editor-fold>
}
