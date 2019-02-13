/*
 * DateColumnHandler.java
 *
 * Created on May 21, 2013, 11:46 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.common;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author wflores
 */
public class DateColumnHandler extends Column.TypeHandler implements PropertySupport.DatePropertyInfo 
{   
    private static final long serialVersionUID = 1L;
    private String inputFormat;
    private String outputFormat;
    private String valueFormat;
    
    public DateColumnHandler(){
    } 
    
    public DateColumnHandler(String inputFormat, String outputFormat, String valueFormat) 
    {
        this.inputFormat = inputFormat;
        this.outputFormat = outputFormat;
        this.valueFormat = valueFormat;
    }
    
    public String getType() { return "date"; }
    
    public String getInputFormat() 
    {
        Object value = super.get("inputFormat");
        if (value == null) value = this.inputFormat;
        
        return (value == null? null: value.toString());
    }
    
    public void setInputFormat(String inputFormat) {
        this.inputFormat = inputFormat;
    }

    public String getOutputFormat() 
    {
        Object value = super.get("outputFormat");
        if (value == null) value = this.outputFormat;
        
        return (value == null? null: value.toString()); 
    }
    
    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat; 
    }

    public String getValueFormat() 
    {
        Object value = super.get("valueFormat");
        if (value == null) value = this.valueFormat;
        
        return (value == null? null: value.toString()); 
    }
    
    public void setValueFormat(String valueFormat) { 
        this.valueFormat = valueFormat;
    }

    public Object get(Object key) {  
        if ( key == null ) { return null; } 
        
        Object val = super.get(key);
        if ( val != null ) { return val; } 
        
        if ( "format".equals( key.toString()) ) {
            return getOutputFormat(); 
        } else if ( "outputFormat".equals( key.toString()) ) {
            return getOutputFormat(); 
        } else if ( "inputFormat".equals( key.toString()) ) {
            return getInputFormat(); 
        } else if ( "valueFormat".equals( key.toString()) ) {
            return getValueFormat(); 
        } 
        return null; 
    } 
    
    private Map<String,SimpleDateFormat> formatters = new HashMap(); 
    
    private SimpleDateFormat getFormatter( String pattern ) {
        SimpleDateFormat sdf = formatters.get( pattern ); 
        if ( sdf == null ) {
            sdf = new SimpleDateFormat( pattern ); 
            formatters.put( pattern, sdf ); 
        }
        return sdf; 
    }
    
    public String format( Object value ) {
        return format( value, getOutputFormat() ); 
    }
    public String format( Object value, String pattern ) {
        if ( value == null ) { return null; }
        
        Date dtvalue = convertDate( value ); 
        if ( dtvalue == null ) { return null; } 

        if ( pattern==null || pattern.trim().length()==0 ) {
            return dtvalue.toString(); 
        } 
        return getFormatter( pattern ).format( dtvalue ); 
    }
    
    private Date convertDate( Object value ) {
        if ( value instanceof Date ) {
            return (Date) value; 
        }
        
        try {
            return java.sql.Timestamp.valueOf( value.toString() ); 
        } catch(Throwable t){;} 
        
        try { 
            return java.sql.Date.valueOf( value.toString() ); 
        } catch(Throwable t){
            return null; 
        } 
    }
}
