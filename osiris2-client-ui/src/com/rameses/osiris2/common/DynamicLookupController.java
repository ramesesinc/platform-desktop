/*
 * DynamicLookupController.java
 *
 * Created on June 27, 2013, 1:48 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris2.common;

import com.rameses.rcp.common.AbstractListDataProvider;

/**
 *
 * @author wflores
 */
public class DynamicLookupController 
{
    private AbstractListDataProvider listHandler;
    private Object selectedEntity;
    
    public DynamicLookupController() {
    }
    
    public AbstractListDataProvider getListHandler() { return listHandler; } 
    public void setListHandler(AbstractListDataProvider listHandler) {
        this.listHandler = listHandler; 
    }
    
    public Object getSelectedEntity() { return selectedEntity; } 
    public void setSelectedEntity(Object selectedEntity) {
        this.selectedEntity = selectedEntity; 
    }
    
    public Object getSelectedValue() 
    {
        AbstractListDataProvider dp = getListHandler();
        return (dp == null? null: dp.getSelectedValue()); 
    }
    
    public Object doSelect() 
    {
        return "_close";
    }
    
    public Object doCancel() 
    {
        return "_close";
    }
    
}
