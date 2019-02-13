/*
 * ListItem.java
 *
 * This class is re-used by GridComponent and SubItemController
 */

package com.rameses.rcp.common;

import java.util.ArrayList;
import java.util.List;

public class ListItem implements Cloneable 
{
    public final static int STATE_EMPTY = 0;
    public final static int STATE_SYNC  = 1;
    public final static int STATE_DRAFT = 2;
    public final static int STATE_EDIT  = 3;
    
    private HandlerSupport handlerSupport;
    private boolean selected; 
    private Object item;
    
    private int state = STATE_EMPTY;
    private int index;
    private int rownum;
    
    //new addition. this refers to the calling code
    private Object root;
    
    public ListItem() {
        handlerSupport = new HandlerSupport(); 
    }
    
    public void removeHandler(ListItem.Handler handler) {
        handlerSupport.remove(handler); 
    }
    public void addHandler(ListItem.Handler handler) {
        handlerSupport.add(handler); 
    }
    
    public ListItem clone() {
        ListItem item = new ListItem();
        item.item = this.item;
        item.state = this.state;
        item.index = this.index;
        item.rownum = this.rownum;
        item.selected = this.selected;
        item.root = this.root;
        return item;
    }

    public boolean equals(Object obj) {
        return obj != null && this.hashCode() == obj.hashCode();
    }
    
    public int hashCode() { 
        return this.getClass().getName().hashCode() + handlerSupport.getClass().getName().hashCode() + rownum; 
    } 
    
    //this method is called only by the AbstractListDataProvider ONLY.
    //during reload
    public final void loadItem(Object item) {
        this.item = item;
    } 
    
    public final void loadItem(Object item, int state) { 
        setState(state); 
        this.item = item;
        this.state = state;
    }     
    
    public final Object getItem() { return item; }    
    public final void setItem(Object newItem) {
        if (item == null && newItem == null) return;
        if (item != null && item.equals(newItem)) return;
        
        try {
            //fire only replace if the previous item is not null.
            handlerSupport.replaceSelectedItem(this, newItem);
            this.item = newItem;             
            this.state = STATE_SYNC; 
            handlerSupport.refreshItemUpdated(this); 
        } catch(Exception e) {
            MsgBox.err(e);
        }
    }
        
    public int getState() { return state; }    
    public void setState(int state) {
        stateCheck(state); 
        this.state = state;
    }
    
    public int getIndex() { return index; }    
    public void setIndex(int index) {
        this.index = index;
    }
    
    public int getRownum() { return rownum; }    
    public void setRownum(int rowindex) {
        this.rownum = rowindex;
    }
    
    public boolean isSelected() { return selected; }    
    public void setSelected(boolean selected) {
        this.selected = (item == null)? false: selected;
        handlerSupport.setSelected(this, this.selected); 
    }
        
    public final Object getRoot() { return root; }    
    public final void setRoot(Object root) {
        this.root = root;
    }

    protected void finalize() throws Throwable {
        handlerSupport.removeAll(); 
        item = null;
    }
    
    private void stateCheck(int state) {
        switch (state) {
            case STATE_DRAFT: break;
            case STATE_EDIT: break;
            case STATE_EMPTY: break;
            case STATE_SYNC: break;
            default: throw new IllegalStateException("Invalid state value for ListItem");
        }
    }
    
    public boolean isDirty() {
        if ( getState()==STATE_DRAFT || getState()==STATE_EDIT ) {
            return true;  
        } else {
            return false; 
        }
    }
    
    
    // <editor-fold defaultstate="collapsed" desc=" HandlerSupport (class) "> 
    
    private class HandlerSupport implements Handler, Cloneable  
    {
        private List<ListItem.Handler> handlers = new ArrayList<ListItem.Handler>(); 

        void removeAll() {
            handlers.clear();
        }
        
        void remove(ListItem.Handler handler) {
            if (handler != null) handlers.remove(handler); 
        }

        void add(ListItem.Handler handler) 
        {
            if (handler != null && !handlers.contains(handler)) 
                handlers.add(handler); 
        }

        public HandlerSupport clone() 
        {
            ListItem.HandlerSupport hs = new ListItem.HandlerSupport();
            for (ListItem.Handler handler : handlers) {
                hs.handlers.add(handler); 
            } 
            return hs; 
        }
        
        public void setSelected(ListItem li, boolean selected) { 
            for (ListItem.Handler handler : handlers) {
                handler.setSelected(li, selected); 
            }
        }

        public void replaceSelectedItem(ListItem li, Object data) {
            for (ListItem.Handler handler : handlers) {
                handler.replaceSelectedItem(li, data); 
            }   
        }

        public void refreshItemUpdated(ListItem li) {
            for (ListItem.Handler handler : handlers) {
                handler.refreshItemUpdated(li); 
            }              
        }
    }
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" Handler (class) "> 
    
    public static interface Handler 
    {
        void setSelected(ListItem li, boolean selected); 
        void replaceSelectedItem(ListItem li, Object newData);
        
        void refreshItemUpdated(ListItem li); 
    }
    
    // </editor-fold>
}
