/*
 * DynamicColumn.java
 *
 * Created on June 13, 2013, 11:57 AM
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
public class DynamicColumn extends Column 
{
    private static final long serialVersionUID = 1L;
    private List<Column> columns = new ArrayList();
    
    public DynamicColumn() {
        super();
    }

    public DynamicColumn(String name, String caption) {
        super(name, caption);
    }

    public String getType() { return "dynamic"; }
    
    public List<Column> getColumns() { return columns; } 
    public void setColumns(List<Column> columns) { 
        this.columns = (columns == null? new ArrayList(): columns); 
    } 
    
    public void setColumns(Column[] columns) 
    { 
        this.columns.clear();
        if (columns != null) 
        {
            for (int i=0; i<columns.length; i++) 
            {
                if (columns[i] != null) this.columns.add(columns[i]); 
            }
        }
    } 
    
}
