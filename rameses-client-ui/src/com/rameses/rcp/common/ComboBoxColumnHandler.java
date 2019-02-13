/*
 * ComboBoxColumnHandler.java
 *
 * Created on May 21, 2013, 11:46 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.common;

/**
 *
 * @author wflores
 */
public class ComboBoxColumnHandler extends Column.TypeHandler implements PropertySupport.ComboBoxPropertyInfo
{   
    private static final long serialVersionUID = 1L;
    private Object items;
    private String itemKey;
    private String expression;
    
    public ComboBoxColumnHandler(){
    } 
    
    public ComboBoxColumnHandler(Object items, String itemKey, String expression) 
    {
        this.items = items;
        this.itemKey = itemKey;
        this.expression = expression; 
    }
    
    public String getType() { return "combobox"; }
    
    public String getExpression() 
    {
        Object value = super.get("expression");
        if (value == null) value = this.expression;
        
        return (value == null? null: value.toString()); 
    }
    
    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getItemKey() 
    {
        Object value = super.get("itemKey");
        if (value == null) value = this.itemKey;
        
        return (value == null? null: value.toString()); 
    }
    
    public void setItemKey(String itemKey) {
        this.itemKey = itemKey;
    }

    public Object getItems() 
    {
        Object value = super.get("items");
        if (value == null) value = this.items;
        
        return value; 
    }
    
    public void setItems(Object items) { 
        this.items = items;
    }
}
