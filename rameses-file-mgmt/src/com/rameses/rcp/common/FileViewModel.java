/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.common;

import com.rameses.filemgmt.FileManager;
import com.rameses.filemgmt.FileManager.DbProvider;
import com.rameses.rcp.framework.Binding;
import java.awt.Dimension;
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
    
    public boolean removeItem( Object item ) {
        return true; 
    }
    
    public void afterAddItem( Object item ) {
    }
    
    public Object getItem( Map params ) { 
        DbProvider dbp = FileManager.getInstance().getDbProvider(); 
        return ( dbp == null ? null : dbp.read(params)); 
    } 
    
    public Object openItem( Object item ) {
        return null; 
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
    
    public Object getSelectedItem() { 
        return (provider == null ? null : provider.getSelectedItem()); 
    }
    
    private Provider provider; 
    public void setProvider( Provider provider ) { 
        this.provider = provider; 
    } 
    public static interface Provider { 
        Binding getBinding(); 
        Binding getInnerBinding(); 
        void addItem( Object item ) throws Exception;
        
        Object getSelectedItem();
        void updateBeanValue();
    }  
    
    
    
    private final Workspace workspace = new Workspace(); 
    public Workspace getWorkspace() { return workspace; } 
    
    public class Workspace { 
        
        private Object selectedThumbnail; 
        
        public void setSelectedThumbnail( Object selectedThumbnail ) {
            this.selectedThumbnail = selectedThumbnail; 
            if ( provider != null ) provider.updateBeanValue();
        } 
    }
}
