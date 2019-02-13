/*
 * DoubleColumnHandler.java
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
public class DoubleColumnHandler extends DecimalColumnHandler implements PropertySupport.DoublePropertyInfo
{      
    private static final long serialVersionUID = 1L;
    
    public DoubleColumnHandler(){
        this("#,##0.00");
    } 
    
    public DoubleColumnHandler(String format) {
        this(format, -1.0, -1.0); 
    }
    
    public DoubleColumnHandler(String format, double minValue, double maxValue) {
        super(format, minValue, maxValue, true); 
    }    
    
    public String getType() { return "double"; } 

    public final boolean isUsePrimitiveValue() { return true; }
    public final void setUsePrimitiveValue(boolean usePrimitiveValue) {}    
}
