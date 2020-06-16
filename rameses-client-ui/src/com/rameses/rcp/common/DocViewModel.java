/*
 * DocViewModel.java
 *
 * Created on August 27, 2013, 9:25 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.common;

/**
 *
 * @author wflores
 */
public class DocViewModel {
    
    private DocViewModel.Provider provider; 
    private Object value;
    
    public DocViewModel() {
    }
    
    public void setProvider(DocViewModel.Provider provider) { 
        this.provider = provider; 
    } 
    public DocViewModel.Provider getProvider() {
        return provider; 
    }
    
    public final boolean hasProvider() { 
        return (provider != null); 
    }
    
    public String getText() { 
        return (provider == null? null: provider.getText()); 
    }     
    public void setText(String text) {
        if (provider != null) provider.setText(text); 
    }
        
    public void insertText(String text) {
        if (provider != null) provider.insertText(text); 
    }
    
    public void appendText(String text) {
        if (provider != null) provider.appendText(text); 
    }

    public void requestFocus() { 
        if (provider != null) provider.requestFocus();
    }
    
    public void load() { 
        if (provider != null) provider.load(); 
    } 
    
    public void refresh() {
        if (provider != null) provider.refresh(); 
    }  
    
    public Object getValue() { return value; } 
    public void setValue(Object value) {
        this.value = value; 
    }
    
    public boolean isContextMenuEnabled() {
        return false; 
    }
    
    public int getWidth() {
        return (provider == null ? 0 : provider.getWidth()); 
    }

    public int getHeight() {
        return (provider == null ? 0 : provider.getHeight()); 
    }
    
    
    // <editor-fold defaultstate="collapsed" desc=" Provider interface ">
    
    public static interface Provider 
    {
        void insertText(String text); 
        void appendText(String text); 
        
        String getText(); 
        void setText(String text);
        
        void load();
        void refresh();
        void requestFocus(); 
        
        int getWidth();
        int getHeight();
    } 
    
    // </editor-fold>
    
    
    public void onresize( ResizeEvent re ) {
    }

    public static class ResizeEvent {
        
        private int width;
        private int height;
        
        public ResizeEvent(int width, int height) {
            this.width = width;
            this.height = height; 
        }
        
        public int getWidth() { return width; } 
        public int getHeight() { return height; } 
    }
}
