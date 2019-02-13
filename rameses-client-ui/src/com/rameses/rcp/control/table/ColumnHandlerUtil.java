/*
 * ColumnHandlerUtil.java
 *
 * Created on June 5, 2013, 10:48 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control.table;

import com.rameses.rcp.common.ButtonColumnHandler;
import com.rameses.rcp.common.CheckBoxColumnHandler;
import com.rameses.rcp.common.Column;
import com.rameses.rcp.common.ComboBoxColumnHandler;
import com.rameses.rcp.common.DateColumnHandler;
import com.rameses.rcp.common.DecimalColumnHandler;
import com.rameses.rcp.common.DoubleColumnHandler;
import com.rameses.rcp.common.IconColumnHandler;
import com.rameses.rcp.common.IntegerColumnHandler;
import com.rameses.rcp.common.LabelColumnHandler;
import com.rameses.rcp.common.LookupColumnHandler;
import com.rameses.rcp.common.TextColumnHandler;

/**
 *
 * @author wflores
 */
class ColumnHandlerUtil 
{
    public static ColumnHandlerUtil newInstance() {
        return new ColumnHandlerUtil();
    }
    
    private ColumnHandlerUtil() {}
    
    public Column.TypeHandler createTypeHandler(Column oColumn)
    {
        if (oColumn == null) return null;
        
        Column.TypeHandler handler = oColumn.getTypeHandler(); 
        if (handler != null) {
            if (handler instanceof LabelColumnHandler) {
                oColumn.setEditable(false); 
                oColumn.setEditableWhen(null); 
            } 
            return handler;
        }
        
        String stype = oColumn.getType();
        if ("boolean".equals(stype) || "checkbox".equals(stype)) {
            CheckBoxColumnHandler oHandler = new CheckBoxColumnHandler(); 
            oHandler.setValueType(oColumn.getFieldType()); 
            oHandler.setCheckValue(oColumn.getCheckValue());
            oHandler.setUncheckValue(oColumn.getUncheckValue());
            if (oHandler.getValueType() == null) 
                oHandler.setValueType(Boolean.class); 
            
            return oHandler;
        }
        else if ("combo".equals(stype) || "combobox".equals(stype)) {
            ComboBoxColumnHandler oHandler = new ComboBoxColumnHandler(); 
            oHandler.setItems(oColumn.getItems()); 
            oHandler.setExpression(oColumn.getExpression()); 
            
            Object itemKey = oColumn.getProperties().get("itemKey");
            oHandler.setItemKey((itemKey == null? null: itemKey.toString()));
            return oHandler;
        }
        else if ("date".equals(stype)) {
            DateColumnHandler oHandler = new DateColumnHandler();
            Object oFormat = oColumn.getProperties().get("inputFormat");
            if (oFormat != null) oHandler.setInputFormat(oFormat.toString()); 
            
            oFormat = oColumn.getProperties().get("outputFormat");
            if (oFormat != null) oHandler.setOutputFormat(oFormat.toString()); 
            
            oFormat = oColumn.getProperties().get("valueFormat");
            if (oFormat != null) oHandler.setValueFormat(oFormat.toString()); 
            
            return oHandler;
        }
        else if ("double".equals(stype)) {
            DoubleColumnHandler oHandler = new DoubleColumnHandler(); 
            if (oColumn.getFormat() != null) oHandler.setFormat(oColumn.getFormat());
            
            oHandler.setUsePrimitiveValue(true);
            return oHandler;
        }
        else if ("decimal".equals(stype)) {
            DecimalColumnHandler oHandler = new DecimalColumnHandler(); 
            if (oColumn.getFormat() != null) oHandler.setFormat(oColumn.getFormat());

            oHandler.putAll(oColumn.getProperties());             
            return oHandler;
        } 
        else if ("integer".equals(stype)) {
            IntegerColumnHandler oHandler = new IntegerColumnHandler(); 
            if (oColumn.getFormat() != null) oHandler.setFormat(oColumn.getFormat());            

            return oHandler;
        } 
        else if ("label".equals(stype)) {
            LabelColumnHandler oHandler = new LabelColumnHandler(); 
            oColumn.setEditable(false);
            oColumn.setEditableWhen(null); 
            return oHandler;
        } 
        else if ("lookup".equals(stype)) {
            LookupColumnHandler oHandler = new LookupColumnHandler(); 
            oHandler.setHandler(oColumn.getHandler());
            oHandler.setExpression(oColumn.getExpression()); 
            return oHandler;
        } 
        else if ("icon".equals(stype)) {
            IconColumnHandler oHandler = new IconColumnHandler();
            oColumn.setEditable(false);
            oColumn.setEditableWhen(null); 
            return oHandler;
        }   
        else if ("button".equals(stype)) {
            ButtonColumnHandler oHandler = new ButtonColumnHandler();
            oColumn.setEditable(false);
            oColumn.setEditableWhen(null); 
            return oHandler;
        }   
        else {
            TextColumnHandler oHandler = new TextColumnHandler(); 
            return oHandler; 
        }
    }
    
    public void prepare(Column oColumn) 
    {
        Column.TypeHandler handler = oColumn.getTypeHandler();
        if (handler instanceof SelectionColumnHandler) 
        {
            oColumn.setWidth(30);
            oColumn.setMinWidth(30);
            oColumn.setMaxWidth(30);
            oColumn.setEditable(true);
        }
        else if (handler instanceof IconColumnHandler) {
            oColumn.setEditable(false);
            oColumn.setEditableWhen(null);
        }
        
        if (!oColumn.isResizable()) {
            int width = oColumn.getWidth();
            if (width > 0) {
                oColumn.setMinWidth(width);
                oColumn.setMaxWidth(width); 
            }
        }
    }
    
    public Object resolveValue(Column oColumn, Object cellValue, Object exprBean) 
    {
        Column.TypeHandler handler = oColumn.getTypeHandler();
        if (handler == null) handler = createTypeHandler(oColumn); 
        
        if (handler instanceof CheckBoxColumnHandler)
        {
            CheckBoxColumnHandler h = (CheckBoxColumnHandler) handler;
            
        }
        
        return null;
    }
    
}
