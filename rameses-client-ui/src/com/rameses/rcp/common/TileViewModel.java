/*
 * TileViewModel.java
 *
 * Created on February 6, 2014, 9:36 AM
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
public class TileViewModel 
{
    
    public TileViewModel() {
    }

    public int getCellCount() { return 0; } 
    public int getCellSize() { return 0; } 
    
    public List fetchList(Map params) { 
        return null; 
    } 
    
    public Object onOpenItem(Object item) { 
        return null; 
    } 
    
    
    
    private Provider provider;
    
    public void setProvider( Provider provider ) {
        this.provider = provider; 
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
    
    public static interface Provider {
        void refresh();
        void reload();
    } 
}
