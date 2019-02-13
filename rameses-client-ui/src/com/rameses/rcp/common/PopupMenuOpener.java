/*
 * PopupMenuOpener.java
 *
 * Created on August 5, 2013, 12:25 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.common;

import java.rmi.server.UID;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author wflores
 */
public class PopupMenuOpener extends Opener {
    
    private List items = new ArrayList(); 
    private boolean executeOnSingleResult;
    
    public PopupMenuOpener() {
        super();
        setId("PopupMenu" + new UID()); 
    }
    
    public final String getTarget() { return "popupmenu"; }   

    public boolean isExecuteOnSingleResult() { return executeOnSingleResult; } 
    public void setExecuteOnSingleResult(boolean executeOnSingleResult) {
        this.executeOnSingleResult = executeOnSingleResult; 
    }
    
    public List getItems() { return items; } 
    
    public Object getFirst() { 
        return (items.isEmpty()? null: items.get(0)); 
    } 
    
    public void removeAll() { items.clear(); }
    
    public void add(Object item) {
        if (item instanceof Opener || item instanceof Action) {
            if (!items.contains(item)) items.add(item); 
        }
    }
    
    public void addAll(List collection) {
        if (collection == null) return;
        
        for (Object o: collection) {
            add(o); 
        }
    }
    
}
