/*
 * CheckBoxColumnHandler.java
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
public class CheckBoxColumnHandler extends Column.TypeHandler implements PropertySupport.CheckBoxPropertyInfo 
{
    private static final long serialVersionUID = 1L;
    private Class valueType;
    private Object checkValue;
    private Object uncheckValue;
    
    public CheckBoxColumnHandler(){
        this(Boolean.class, true, false);
    } 
    
    public CheckBoxColumnHandler(Class valueType, Object checkValue, Object uncheckValue) 
    {
        this.valueType = valueType;
        this.checkValue = checkValue;
        this.uncheckValue = uncheckValue;
    }

    public String getType() { return "checkbox"; }
    
    public Class getValueType() 
    {
        Object value = super.get("valueType");
        if (value == null) value = this.valueType;
        
        return (value == null? Boolean.class: (Class)value); 
    }    
    
    public void setValueType(Class valueType) { 
        this.valueType = valueType; 
    }
    
    public Object getCheckValue() 
    {
        Object value = super.get("checkValue");
        if (value == null) value = this.checkValue;
        
        return value;
    }
    
    public void setCheckValue(Object checkValue) {
        this.checkValue = checkValue;
    }

    public Object getUncheckValue() 
    {
        Object value = super.get("uncheckValue");
        if (value == null) value = this.uncheckValue;
        
        return value; 
    }
    
    public void setUncheckValue(Object uncheckValue) {
        this.uncheckValue = uncheckValue; 
    }    
}
