/*
 * EditorListSupport.java
 *
 * Created on November 4, 2013, 9:58 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.common;

import java.util.HashMap;

/**
 *
 * @author wflores
 */
public final class EditorListSupport 
{
    public static EditorListSupport create(AbstractListDataProvider dataProvider) {
        if (dataProvider == null) return null;        
        if (dataProvider instanceof TableEditor) { 
            EditorListSupport els = new EditorListSupport(dataProvider); 
            return (els.handler == null? null: els); 
        } else {
            return null; 
        }
    }
    
    
    private AbstractListDataProvider dataProvider;
    private TableEditor tableEditor;
    private TableEditorHandler handler; 
    
    private EditorListSupport(AbstractListDataProvider dataProvider) {
        this.tableEditor = (TableEditor) dataProvider;
        this.handler = tableEditor.getTableEditorHandler(); 
        this.dataProvider = dataProvider; 
        
        tableEditor.setEditorListSupport(this); 
    }
    
    public final AbstractListDataProvider getSource() {
        return dataProvider; 
    }    
    
    public boolean isTemporaryItem(ListItem li) {
        return (li.getState() == ListItem.STATE_DRAFT); 
    }
    
    public void flushTemporaryItem(ListItem li) {
        if (!isTemporaryItem(li)) return;
        
        validate(li);
        onAddItem(li.getItem()); 
        li.setState(ListItem.STATE_SYNC);         
    }
    
    public void removeTemporaryItem(ListItem li) {
        if (!isTemporaryItem(li)) return;

        li.loadItem(null, ListItem.STATE_EMPTY);
        //remove last row only 
        //let us assume that temporary item is second to the last row
        dataProvider.removeListItem(li.getIndex()+1);
    }
    
    public Object loadTemporaryItem(ListItem li, String columnName) {
        Object item = handler.createItem( columnName );
        if (item == null) item = new HashMap();
        
        li.loadItem(item, ListItem.STATE_DRAFT); 
        return item; 
    }
    
    public boolean isAllowedForEditing(ListItem li) 
    {
        if (li == null) return false; 

        int index = li.getIndex();        
        int size = dataProvider.getListItems().size();
        if (index == size) return true; 
        
        return (index >= 0 && index < size);
    } 
    
    public boolean isColumnEditable(Object item, String columnName) { 
        return handler.isColumnEditable(item, columnName); 
    }
    
    public boolean isLastItem(ListItem li) 
    {
        int index = li.getIndex();
        return (index+1 == dataProvider.getListItems().size()); 
    } 
    
    public boolean isAllowAdd() { 
        return handler.isAllowAdd();
    } 
    
    public final void addEmptyItem() {
        dataProvider.addEmptyItem(); 
    }
   
    public void fireUpdateItem(ListItem li) {
        if (li == null) return;
        
        onUpdateItem(li.getItem()); 
    }

    public void fireValidateItem(ListItem li) {
        if (li != null) validate(li); 
    }
    
    public void fireCommitItem(ListItem li) {
        if (li == null) return;
        
        onCommitItem(li.getItem()); 
        li.setState(ListItem.STATE_SYNC); 
    } 
    
    public void fireRemoveItem(ListItem li) {
        if (li == null) return;
        if (li.getState() == ListItem.STATE_EMPTY) return;
                
        int index = li.getIndex();
        dataProvider.getMessageSupport().removeErrorMessage(index); 
                
        TableModelSupport tms = tableEditor.getTableModelSupport();
        if (li.getState() == ListItem.STATE_DRAFT) {
            if (dataProvider.getListItem(index+1) == null) {
                li.loadItem(null, ListItem.STATE_EMPTY); 
                tms.fireTableRowsUpdated(index, index); 
            } else {
                dataProvider.getDataList().remove(index); 
                dataProvider.getListItems().remove(index); 
                tms.fireTableRowsDeleted(index, index); 
            }
        } else {
            if (!onRemoveItem(li.getItem())) return;

            int itemIndex = dataProvider.getListItems().indexOf(li); 
            int dataIndex = dataProvider.getDataList().indexOf(li.getItem());             
            if (dataIndex < 0 && itemIndex == index) dataProvider.refresh(false); 
        } 
    } 
    
    public final void fireBeforeColumnUpdate(ListItem li, Object newValue) { 
        if (li == null) return;
        
        try { 
            String columnName = dataProvider.getSelectedColumn(); 
            boolean success = handler.beforeColumnUpdate(li.getItem(), columnName, newValue); 
            if (success == true) return; 
        } catch(Throwable t) {
            throw new BeforeColumnUpdateException(t); 
        } 
        
        throw new BeforeColumnUpdateException(); 
    }
    
    public final void fireColumnUpdate(ListItem li) {    
        if (li == null) return;
        
        try { 
            String columnName = dataProvider.getSelectedColumn(); 
            handler.onColumnUpdate(li.getItem(), columnName); 
        } catch(Throwable t) {
            throw new AfterColumnUpdateException(t); 
        }
    }
    
    
    // <editor-fold defaultstate="collapsed" desc=" callback events ">
    
    protected void validate(ListItem li) {
        handler.validate(li); 
    } 
    
    protected void onAddItem(Object item) {
        handler.onAddItem(item);
    } 

    protected void onUpdateItem(Object item) {
        handler.onUpdateItem(item);
    } 
    
    protected void onCommitItem(Object item) {
        handler.onCommitItem(item); 
    }    
    
    protected boolean onRemoveItem(Object item) { 
        return handler.onRemoveItem(item); 
    } 
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Custom Exceptions ">  
    
    public static class BeforeColumnUpdateException extends RuntimeException { 
        
        BeforeColumnUpdateException() {
            super(); 
        }         
        
        BeforeColumnUpdateException(Throwable caused) {
            super(caused); 
        } 
    }
    
    public static class AfterColumnUpdateException extends RuntimeException { 
        
        AfterColumnUpdateException() {
            super(); 
        }         
        
        AfterColumnUpdateException(Throwable caused) {
            super(caused); 
        } 
    }    
    
    // </editor-fold>    
        
    // <editor-fold defaultstate="collapsed" desc=" TableEditor ">
    
    public static interface TableEditor 
    {
        TableEditorHandler getTableEditorHandler();
        TableModelSupport getTableModelSupport(); 
        
        void setEditorListSupport(EditorListSupport editorSupport); 
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" TableEditorProvider support ">
    
    public static interface TableEditorProvider 
    {
        void refreshCurrentEditor(ListItem li); 
        boolean hasUncommittedData();
    } 
    
    private TableEditorProvider editorProvider; 
    
    public void setTableEditorProvider(TableEditorProvider editorProvider) { 
        this.editorProvider = editorProvider; 
    }
    
    public final void refreshCurrentEditor() {
        if (editorProvider == null) return;
        
        ListItem li = dataProvider.getSelectedItem(); 
        if (li != null) editorProvider.refreshCurrentEditor(li); 
    } 
    
    public final boolean hasUncommittedData() {
        return (editorProvider == null? false: editorProvider.hasUncommittedData()); 
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" TableEditorHandler ">
    
    public static interface TableEditorHandler 
    {
        boolean isAllowAdd();
        Object createItem(String columnName); 
        
        void validate(ListItem li);
        void onAddItem(Object item);
        void onUpdateItem(Object item);
        void onCommitItem(Object item); 
        boolean onRemoveItem(Object item);
        
        boolean isColumnEditable(Object item, String columnName);
        boolean beforeColumnUpdate(Object item, String columnName, Object newValue);
        void onColumnUpdate(Object item, String columnName); 
    }
    
    // </editor-fold>
    
}
