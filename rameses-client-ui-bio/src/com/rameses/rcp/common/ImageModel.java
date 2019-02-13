/*
 * ImageModel.java
 *
 * Created on December 23, 2013, 12:41 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.common;

import java.io.File;

/**
 *
 * @author wflores
 */
public class ImageModel 
{
    private String title;
    private int width;
    private int height;
    private Object data;
    private String filterDescription; 
    
    public ImageModel() {
        setTitle("Image Viewer"); 
        setWidth(800);
        setHeight(600);
        setFilterDescription("*.jpg|*.png|*.gif");
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
    
    public Object getData() { return data; } 
    public void setData(Object data) {
        this.data = data; 
    }
    
    public String getFilterDescription() { return filterDescription; } 
    public void setFilterDescription(String filterDescription) { 
        this.filterDescription = filterDescription; 
    }  
    
    public boolean accept(File file) {
        if (file == null) return false; 
        
        return accept(file.getName(), file.isDirectory()); 
    }
    
    public boolean accept(String filename, boolean directory) {
        if (directory) return true; 
        
        String s = filename.toLowerCase();
        return (s.endsWith(".jpg") || s.endsWith(".png") || s.endsWith(".gif")); 
    }
    
    // <editor-fold defaultstate="collapsed" desc=" Provider ">
    
    public static interface Provider 
    {
        Object getBinding();
        void showDialog(ImageModel model);
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
