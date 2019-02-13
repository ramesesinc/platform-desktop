/*
 * FingerPrintDataModel.java
 *
 * Created on December 17, 2013, 3:29 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.fingerprint;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author wflores 
 */
class FingerPrintDataModel 
{
    private List<FingerPrintDataModel.Item> items;
    private FingerPrintDataModel.Item selectedItem; 
    
    public FingerPrintDataModel() {
        this.items = new ArrayList(); 
    }
    
    FingerPrintDataModel.Item getItem(int index) {
        if (index >= 0 && index < items.size()) {
            return items.get(index);  
        } else {
            return null; 
        }
    }
    
    int indexOf(FingerPrintDataModel.Item item) {
        return items.indexOf(item); 
    }
    
    void removeAll() {
        items.clear(); 
        selectedItem = null; 
    }
    
    void remove(FingerPrintDataModel.Item item) {
        if (item == null) return; 
        
        items.remove(item); 
        if (selectedItem != null && selectedItem.equals(item)) { 
            selectedItem = null; 
        } 
        
        item.setModel(null); 
    }
    
    void add(FingerPrintDataModel.Item item) { 
        if (item == null || items.contains(item)) return; 

        item.setModel(this); 
        items.add(item); 
    } 
    
    FingerPrintDataModel.Item getSelectedItem() {
        return selectedItem;
    }
    
    void setSelectedItem(FingerPrintDataModel.Item selectedItem) {
        this.selectedItem = selectedItem;
    }
    
    void setSelectedItem(int index) {
        this.selectedItem = getItem(index); 
    }
    
    boolean isSelected(FingerPrintDataModel.Item item) {
        if (item == null) return false; 
        
        return (selectedItem != null && selectedItem.equals(item));
    }
    
    void refresh() {
        for (FingerPrintDataModel.Item item : items) {
            item.refresh();
        }
    }
    
    
    
    // <editor-fold defaultstate="collapsed" desc=" Item "> 
    
    public static interface Item 
    {
        void setModel(FingerPrintDataModel model); 
        void setImageContext(ImageContext imageContext);
        void refresh();        
    }
    
    // </editor-fold>    
}
