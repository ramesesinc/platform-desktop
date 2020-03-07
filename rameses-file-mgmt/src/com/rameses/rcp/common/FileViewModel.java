/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.common;

import com.rameses.filemgmt.FileManager;
import com.rameses.filemgmt.FileManager.DbProvider;
import com.rameses.rcp.framework.Binding;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author wflores
 */
public class FileViewModel {

    private boolean editable = true; 
    private boolean allowAdd = true; 
    private boolean allowRemove = true; 
    private boolean multiSelect;
    
    private Number cellWidth;
    private Number cellHeight;
    private Number cellSpacing;
    
    public final Map eventMap = new HashMap();

    public boolean isEditable() { return editable; } 
    public void setEditable( boolean editable ) {
        this.editable = editable; 
    }
    
    public boolean isAllowAdd() { return allowAdd; } 
    public void setAllowAdd( boolean allowAdd ) {
        this.allowAdd = allowAdd; 
    }
    
    public boolean isAllowRemove() { return allowRemove; }
    public void setAllowRemove( boolean allowRemove ) {
        this.allowRemove = allowRemove; 
    }
    
    public boolean isMultiSelect() { return multiSelect; } 
    public void setMultiSelect( boolean multiSelect ) {
        this.multiSelect = multiSelect; 
    } 
    
    public Number getCellSpacing() { return cellSpacing; } 
    public void setCellSpacing( Number cellSpacing ) {
        this.cellSpacing = cellSpacing; 
    }
    
    public Number getCellWidth() { return cellWidth; } 
    public void setCellWidth( Number cellWidth ) {
        this.cellWidth = cellWidth; 
    }

    public Number getCellHeight() { return cellHeight; } 
    public void setCellHeight( Number cellHeight ) {
        this.cellHeight = cellHeight; 
    }
        
    public List fetchList( Map params ) { 
        return null; 
    } 
    
    public Object getSelectedItem() { 
        return workspace.thumbnail; 
    }
        
    public void afterAddItem( Object item ) {
    }
    
    public Object getItem( Map params ) { 
        DbProvider dbp = FileManager.getInstance().getDbProvider(); 
        return ( dbp == null ? null : dbp.read(params)); 
    } 
    
    public Object createItem() {
        return null; 
    }
    
    public Object openItem( Object item ) {
        return null; 
    }

    public boolean removeItem( Object item ) {
        return true; 
    }

    public void reload() {
        if ( workspace.albumHandler != null ) {
            workspace.albumHandler.reload(); 
        }
    }
    

    public Binding getBinding() {
        return (provider == null ? null : provider.getBinding()); 
    }
    public Binding getInnerBinding() {
        return (provider == null ? null : provider.getInnerBinding()); 
    }
    public void addItem( Object item ) throws Exception {
        if ( provider == null ) return; 
        provider.addItem( item ); 
    }
    

    private Provider provider; 
    public void setProvider( Provider provider ) { 
        this.provider = provider; 
    } 
    public static interface Provider { 
        Binding getBinding(); 
        Binding getInnerBinding(); 
        void addItem( Object item ) throws Exception;        
        void updateBeanValue();
    }  

    
    public final boolean fireRemoveItem( Object item ) { 
        boolean b = removeItem( item ); 
        if ( !b ) return false; 
        
        DbProvider dbp = FileManager.getInstance().getDbProvider(); 
        if ( dbp != null ) dbp.remove((Map) item); 
        
        return true; 
    }
    
    
    private final Workspace workspace = new Workspace(); 
    public Workspace getWorkspace() { return workspace; } 
    
    public class Workspace { 
        private Object thumbnail; 

        public void thumbnailSelectionChanged( Object thumbnail ) {
            this.thumbnail = thumbnail; 
        } 
        
        private ListPaneModel albumHandler;
        public void setAlbumHandler( Object model ) {
            albumHandler = null;
            if ( model instanceof ListPaneModel ) {
                albumHandler = (ListPaneModel) model; 
            } 
        }
    }
}
