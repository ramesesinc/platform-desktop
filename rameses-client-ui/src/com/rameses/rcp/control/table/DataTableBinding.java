/*
 * DataTableBinding.java
 *
 * Created on June 10, 2013, 11:40 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control.table;

import com.rameses.rcp.framework.Binding;

/**
 *
 * @author wflores
 */
public class DataTableBinding extends Binding
{
    private DataTableModel tableModel;
    private Binding root;    
    private int rowIndex;
    private int columnIndex;

    public Binding getRoot() { return root; }
    public void setRoot(Binding root) { this.root = root; }

    public int getRowIndex() { return rowIndex; }
    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    public int getColumnIndex() { return columnIndex; }
    public void setColumnIndex(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    public DataTableModel getTableModel() { return tableModel; }
    public void setTableModel(DataTableModel tableModel) {
        this.tableModel = tableModel;
    }    
    
    public Object createExpressionBean() { 
        return getTableModel().createExpressionBean(getRowIndex()); 
    } 
} 
