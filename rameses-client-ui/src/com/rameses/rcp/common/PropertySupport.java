/*
 * PropertyInfo.java
 *
 * Created on May 29, 2013, 2:52 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.common;

/**
 *
 * @author wflores
 */
public abstract class PropertySupport 
{
    private static final long serialVersionUID = 1L;
    
    public static interface PropertyInfo {}
    
    public static interface CheckBoxPropertyInfo extends PropertyInfo 
    {
        Class getValueType();
        void setValueType(Class valueType);
        
        Object getCheckValue();
        void setCheckValue(Object checkValue);
        
        Object getUncheckValue();
        void setUncheckValue(Object uncheckValue);
    }
    
    public static interface ComboBoxPropertyInfo extends PropertyInfo 
    {
        Object getItems();
        void setItems(Object items);
        
        String getItemKey();
        void setItemKey(String itemKey);
        
        String getExpression();
        void setExpression(String expression);
    }  
    
    public static interface DatePropertyInfo extends PropertyInfo 
    {
        String getInputFormat();
        void setInputFormat(String inputFormat);
        
        String getOutputFormat();
        void setOutputFormat(String outputFormat);

        String getValueFormat();
        void setValueFormat(String valueFormat);
    }  
    
    public static interface DecimalPropertyInfo extends PropertyInfo 
    {
        String getFormat();
        void setFormat(String format);
        
        double getMinValue();
        void setMinValue(double minValue);
        
        double getMaxValue();
        void setMaxValue(double maxValue);
        
        boolean isUsePrimitiveValue();
        void setUsePrimitiveValue(boolean usePrimitiveValue);
        
        int getScale();
        void setScale(int scale); 
    }    
    
    public static interface DoublePropertyInfo extends PropertyInfo 
    {
        String getFormat();
        void setFormat(String format);
        
        double getMinValue();
        void setMinValue(double minValue);
        
        double getMaxValue();
        void setMaxValue(double maxValue);
    }  
    
    public static interface IntegerPropertyInfo extends PropertyInfo 
    {
        String getFormat();
        void setFormat(String format);
        
        int getMinValue();
        void setMinValue(int minValue);
        
        int getMaxValue();
        void setMaxValue(int maxValue);
    }   
    
    public static interface LookupPropertyInfo extends PropertyInfo 
    {
        Object getHandler();
        void setHandler(Object handler); 
        
        String getExpression();
        void setExpression(String expression); 
    }    
    
    public static interface OpenerPropertyInfo extends PropertyInfo 
    {
        String getExpression();
        void setExpression(String expression); 
        
        Object getHandler();
        void setHandler(Object handler); 
    }      
    
    public static interface LabelPropertyInfo extends PropertyInfo 
    {
        String getExpression();
    } 
    
    public static interface TextPropertyInfo extends PropertyInfo 
    {
    }       
}
