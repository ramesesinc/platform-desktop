package com.rameses.rcp.common;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author wflores
 */
public class MultiSelectionSupport implements MultiSelectionHandler, MultiSelectionMode 
{
    
    private List<MultiSelectionHandler> handlers; 
    private int selectionMode; 
    
    public MultiSelectionSupport() {
        handlers = new ArrayList<MultiSelectionHandler>(); 
        selectionMode = INTERVAL; 
    }
    
    public void removeAll() { handlers.clear(); }

    public void remove(MultiSelectionHandler handler) {
        if (handler != null) {
            handlers.remove(handler);
        }
    }

    public void add(MultiSelectionHandler handler) {
        if (handler != null && !handlers.contains(handler)) { 
            handlers.add(handler); 
        } 
    }     
    
    public int getSelectionMode() { return selectionMode; }
    public void setSelectionMode( int selectionMode ) {
        this.selectionMode = selectionMode; 
    }

    public void selectAll() {
        for (MultiSelectionHandler handler : handlers) {
            handler.selectAll(); 
        }
    }

    public void deselectAll() {
        for (MultiSelectionHandler handler : handlers) {
            handler.deselectAll(); 
        }
    }
}
