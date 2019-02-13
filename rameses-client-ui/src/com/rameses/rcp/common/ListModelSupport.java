/*
 * ListModelSupport.java
 *
 * Created on May 9, 2013, 9:20 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.common;

import javax.swing.table.AbstractTableModel;

/**
 *
 * @author wflores
 */
public class ListModelSupport 
{
    private AbstractListModel listModel;
    private AbstractTableModel tableModel;
    
    ListModelSupport() {} 
    
    ListModelSupport(AbstractListModel listModel, AbstractTableModel tableModel) 
    {
        this.listModel = listModel;
        this.tableModel = tableModel;
    }
    
    public AbstractTableModel getTableModel() { return tableModel; } 

    public void createListItemAt(int rowIndex) 
    {
        
    }
    
}
