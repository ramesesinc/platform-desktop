/*
 * IntegerColumnHandler.java
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
public class IntegerColumnHandler extends Column.TypeHandler implements PropertySupport.IntegerPropertyInfo
{   
    private static final long serialVersionUID = 1L;
    private String format;
    private int minValue;
    private int maxValue;
    
    public IntegerColumnHandler(){
        this(null);
    } 
    
    public IntegerColumnHandler(String format) {
        this(format, -1, -1); 
    }
    
    public IntegerColumnHandler(String format, int minValue, int maxValue) {
        this.format = format;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }
    
    public String getType() { return "integer"; }
        
    public String getFormat() 
    {
        Object value = super.get("format");
        if (value == null) value = this.format;
        
        return (value == null? null: value.toString());
    }
    
    public void setFormat(String format) {
        this.format = format;
    }

    public int getMinValue() 
    {
        Object value = super.get("minValue");
        if (value == null) value = this.minValue;
        
        Number num = convertInteger(value);
        return (num == null? -1: num.intValue());
    }
    
    public void setMinValue(int minValue) { 
        this.minValue = minValue;
    }
    
    public int getMaxValue() 
    {
        Object value = super.get("maxValue");
        if (value == null) value = this.maxValue;
        
        Number num = convertInteger(value);
        return (num == null? -1: num.intValue());
    }
    
    public void setMaxValue(int maxValue) { 
        this.maxValue = maxValue;
    } 
    
    protected Number convertInteger(Object value) 
    {
        if (value instanceof Number)
            return (Number) value;

        try {
            return Integer.parseInt(value.toString()); 
        } catch(Exception ex) {
            return null; 
        }
    }     
}
