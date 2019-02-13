/*
 * ReportDataUtil.java
 *
 * Created on October 3, 2013, 12:54 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris2.reports;

import com.rameses.common.PropertyResolver;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 *
 * @author wflores
 */
public final class ReportDataUtil 
{
    private static ReportDataUtil instance; 
    
    public final static ReportDataUtil getInstance() {
        if (instance == null) {
            instance = new ReportDataUtil(); 
        }
        return instance; 
    }
    
    private Object resolveObject(Object obj) {
        if (obj instanceof ReportDataSource) {
            return ((ReportDataSource)obj).getSource();
        } else { 
            return obj; 
        } 
    }
    
    public BigDecimal toBigDecimal(Object value) {
        Object obj = resolveObject(value);
        if (obj == null) return null;
        
        if (obj instanceof BigDecimal) {
            return (BigDecimal)obj;
        } else { 
            return new BigDecimal(obj.toString());
        } 
    }
    
    public Integer toInteger(Object value) {
        Object obj = resolveObject(value);
        if (obj == null) return null;
        
        if (obj instanceof Integer) {
            return (Integer)obj;
        } else { 
            return new Integer(obj.toString());
        }
    }
    
    public Double toDouble(Object value) {
        Object obj = resolveObject(value);
        if (obj == null) return null;
        
        if (obj instanceof Double) {
            return (Double)obj;
        } else { 
            return new Double(obj.toString()); 
        } 
    }
    
    public boolean isEmpty(Object value) {
        Object obj = resolveObject(value);
        if (obj == null) return true; 
        
        if (obj instanceof Map) {
            return ((Map)obj).isEmpty(); 
        } else if (obj instanceof List) {
            return ((List)obj).isEmpty();
        } else { 
            return false; 
        } 
    }
    
    public Object ifNull(Object value, Object defaultValue) {
        Object obj = resolveObject(value);
        return (obj == null) ? defaultValue : obj;
    }
    
    public Object getValue(Object bean, String name) {
        try {
            if (bean == null) return null;
            
            Object obj = resolveObject(bean);
            if (obj == null) return null;
            
            return PropertyResolver.getInstance().getProperty(obj, name);
        } catch (Throwable ex) {
            System.out.println("ReportDataUtil.getValue: [ERROR_" + ex.getClass().getName() + "] " + ex.getMessage());
            return null;
        }
    }
    
    public BigDecimal getBigDecimal(Object bean, String name) {
        Object value = getValue(bean, name);
        if (value == null) return null;
        
        if (value instanceof BigDecimal) {
            return (BigDecimal)value;
        } else { 
            return new BigDecimal(value.toString()); 
        } 
    }
    
    public Integer getInteger(Object bean, String name) {
        Object value = getValue(bean, name);
        if (value == null) return null;
        
        if (value instanceof Integer) {
            return (Integer)value;
        } else { 
            return new Integer(value.toString()); 
        }
    }
    
    public String getString(Object bean, String name) {
        Object value = getValue(bean, name);
        return (value == null) ? null : value.toString();
    }
    
    public java.util.Date getDate(Object bean, String name) {
        Object value = getValue(bean, name);
        if (value == null) return null;
        
        if (value instanceof java.util.Date) {
            return (java.util.Date)value;
        } else { 
            return java.sql.Date.valueOf(value.toString()); 
        } 
    }
    
    public Timestamp getTimestamp(Object bean, String name) {
        Object value = getValue(bean, name);
        if (value == null) return null;
        
        if (value instanceof Timestamp) {
            return (Timestamp)value;
        } else { 
            return Timestamp.valueOf(value.toString()); 
        } 
    }
    
    private java.util.Date convertDate(Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof java.util.Date) {
            return (java.util.Date)value;
        } 
        
        java.util.Date dt = null;
        try {
            dt = java.sql.Date.valueOf(value.toString());
        } catch (Throwable ign) {;}
        
        try {
            if (dt == null) dt = Timestamp.valueOf(value.toString());
        } catch (Throwable ign) {;}
        
        return dt;
    }
    
    public int getDaysDiff( Object dtfrom, Object dtto ) {
        java.util.Date startDate = convertDate( dtfrom );
        java.util.Date endDate = convertDate( dtto );
        
        Calendar startCal = Calendar.getInstance();
        Calendar endCal = Calendar.getInstance();
        
        startCal.setTime(startDate);
        endCal.setTime(endDate );
        
        long startMillis = startCal.getTimeInMillis();
        long endMillis = endCal.getTimeInMillis();
        
        // Calculate no. of days using diff in milliseconds
        long diff = endMillis - startMillis;
        return (int)(diff / (24 * 60 * 60 * 1000) + 1); 
    }
    
    public int getYearsDiff(Object dtfrom, Object dtto) {
        java.util.Date startDate = convertDate( dtfrom );
        java.util.Date endDate = convertDate( dtto );
        
        Calendar startCal = Calendar.getInstance();
        Calendar endCal = Calendar.getInstance();
        
        startCal.setTime(startDate);
        endCal.setTime(endDate );
        
        int years = 0;         
        if (startCal.get(Calendar.YEAR) != endCal.get(Calendar.YEAR)) {
            while ( startCal.before(endCal) ) {
                startCal.add(Calendar.YEAR, 1); 
                years++; 
            } 
        }
        return years; 
    }
    
    public String padLeft( Object value, String padstr, Number numlen ) { 
        if ( numlen != null && numlen.intValue() > 0 ) { 
            return padLeft( value, padstr, numlen.intValue() ); 
        } else {
            return null; 
        } 
    } 
    
    public String padLeft( Object value, String padstr, int length ) {
        if ( value == null ) { return null; } 
        if ( padstr == null || padstr.length() == 0 ) { return null; } 
        
        String str = value.toString(); 
        if ( str.length() >= length ) { 
            return str; 
        } 
        
        StringBuilder sb = new StringBuilder();
        int diff = length-str.length();
        for ( int i=0; i<diff; i++ ) {
            sb.append( padstr ); 
        }
        sb.append( str );
        return sb.toString(); 
    }
    
    public String padRight( Object value, String padstr, Number numlen ) {
        if ( numlen != null && numlen.intValue() > 0 ) { 
            return padRight( value, padstr, numlen.intValue() ); 
        } else {
            return null; 
        } 
    }
    
    public String padRight( Object value, String padstr, int length ) {
        if ( value == null ) { return null; } 
        
        String str = value.toString(); 
        if ( str.length() >= length ) { 
            return str; 
        } 
        
        StringBuilder sb = new StringBuilder();
        sb.append( str );
        
        int diff = length-str.length();
        for ( int i=0; i<diff; i++ ) {
            sb.append( padstr ); 
        }
        return sb.toString(); 
    }    
}
