/*
 * TableModelSupport.java
 *
 * Created on May 15, 2013, 10:36 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.common;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author wflores
 */
public class TableModelSupport implements TableModelHandler, Cloneable
{
    private List<TableModelHandler> handlers = new ArrayList<TableModelHandler>(); 
    
    public TableModelSupport() {
    }
    
    public void removeAll() { handlers.clear(); }

    public void remove(TableModelHandler handler) {
        if (handler != null) handlers.remove(handler); 
    }

    public void add(TableModelHandler handler) 
    {
        if (handler != null && !handlers.contains(handler)) 
            handlers.add(handler); 
    } 
    
    public TableModelSupport clone() 
    {
        TableModelSupport tms = new TableModelSupport();
        for (TableModelHandler handler : handlers) {
            tms.handlers.add(handler); 
        } 
        return tms; 
    }
    
    public void fireTableCellUpdated(int row, int column) {
        TableModelHandler[] arrs = handlers.toArray(new TableModelHandler[]{}); 
        for (int i=0; i<arrs.length; i++) {
            arrs[i].fireTableCellUpdated(row, column); 
        }
    }

    public void fireTableDataChanged() {
        TableModelHandler[] arrs = handlers.toArray(new TableModelHandler[]{}); 
        for (int i=0; i<arrs.length; i++) {
            arrs[i].fireTableDataChanged(); 
        }
    }

    public void fireTableRowsDeleted(int firstRow, int lastRow) {
        TableModelHandler[] arrs = handlers.toArray(new TableModelHandler[]{}); 
        for (int i=0; i<arrs.length; i++) {
            arrs[i].fireTableRowsDeleted(firstRow, lastRow);
        } 
    }

    public void fireTableRowsInserted(int firstRow, int lastRow) {
        TableModelHandler[] arrs = handlers.toArray(new TableModelHandler[]{}); 
        for (int i=0; i<arrs.length; i++) {
            arrs[i].fireTableRowsInserted(firstRow, lastRow);
        } 
    }

    public void fireTableRowsUpdated(int firstRow, int lastRow) {
        TableModelHandler[] arrs = handlers.toArray(new TableModelHandler[]{}); 
        for (int i=0; i<arrs.length; i++) {
            arrs[i].fireTableRowsUpdated(firstRow, lastRow);
        } 
    }

    public void fireTableStructureChanged() {
        TableModelHandler[] arrs = handlers.toArray(new TableModelHandler[]{}); 
        for (int i=0; i<arrs.length; i++) {
            arrs[i].fireTableStructureChanged();
        } 
    }

    public void fireTableRowSelected(int row, boolean focusOnItemDataOnly) {
        TableModelHandler[] arrs = handlers.toArray(new TableModelHandler[]{}); 
        for (int i=0; i<arrs.length; i++) {
            arrs[i].fireTableRowSelected(row, focusOnItemDataOnly);
        } 
    }    

    public void fireTableDataProviderChanged() {
        TableModelHandler[] arrs = handlers.toArray(new TableModelHandler[]{}); 
        for (int i=0; i<arrs.length; i++) {
            arrs[i].fireTableDataProviderChanged();
        }         
    }
}
