/*
 * LabelColumnHandler.java
 *
 * Created on August 22, 2013, 5:17 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.common;

/**
 *
 * @author wflores
 */
public class LabelColumnHandler extends Column.TypeHandler implements PropertySupport.LabelPropertyInfo {
    
    private static final long serialVersionUID = 1L;
    
    public String getType() { return "label"; }

    public String getExpression() { 
        Column oColumn = getColumn();
        return (oColumn == null? null: oColumn.getExpression()); 
    }

}
