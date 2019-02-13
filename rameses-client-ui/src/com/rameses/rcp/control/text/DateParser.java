/*
 * DateParser.java
 *
 * Created on August 28, 2013, 11:59 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control.text;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author wflores
 */
public class DateParser {
    
    private Map<String,String> months = new HashMap();
    private int advanceYearLimit = 15;
    
    public DateParser() { 
        months.put("jan", "01"); 
        months.put("feb", "02"); 
        months.put("mar", "03"); 
        months.put("apr", "04"); 
        months.put("may", "05"); 
        months.put("jun", "06"); 
        months.put("jul", "07"); 
        months.put("aug", "08"); 
        months.put("sep", "09"); 
        months.put("oct", "10"); 
        months.put("nov", "11"); 
        months.put("dec", "12"); 
    } 
    
    public int getAdvanceYearLimit() { return advanceYearLimit; } 
    public void setAdvanceYearLimit(int advanceYearLimit) {
        this.advanceYearLimit = advanceYearLimit; 
    }
    
    private Date convert(String text) {
        try {
            Date dt = java.sql.Date.valueOf(text);
            return dt;
        } catch(Throwable t){;} 
        
        try {
            Date dt = java.sql.Timestamp.valueOf(text);
            return dt;
        } catch(Throwable t){
            return null; 
        }
    }
    
    public Date parse(String text) {
        if (text == null || text.length() == 0) return null; 
        
        Date dtc = convert(text);
        if (dtc != null) return dtc; 
        
        int _advanceYearLimit = getAdvanceYearLimit();
        _advanceYearLimit = (_advanceYearLimit < 0? 0: _advanceYearLimit+1);
        
        java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis()); 
        if (text.trim().toLowerCase().matches("now|today")) {
            return currentDate; 
        } 
        else { 
            String sval = text.trim().replaceAll("\\s{2,}"," ").replaceAll(" ","-").replaceAll("/","-");
            if (sval.matches("[\\d]{8,8}")) {
                //yyyyMMdd
                String syear = sval.substring(0, 4); 
                String smonth = sval.substring(4, 6); 
                String sday = sval.substring(6, 8); 
                return java.sql.Date.valueOf(syear +"-"+ smonth +"-"+ sday); 
                
            } else if (sval.matches("[\\d]{4,4}-[\\d]{1,2}-[\\d]{1,2}")) {
                //year-month-day
                String[] arr = sval.split("-");
                String syear = arr[0];
                String smonth = fillLeadingZeros(arr[1], 2); 
                String sday = fillLeadingZeros(arr[2], 2); 
                return java.sql.Date.valueOf(syear +"-"+ smonth +"-"+ sday); 
                
            } else if (sval.matches("[\\d]{4,4}-[\\d]{1,2}")) {
                //year-month
                String[] arr = sval.split("-");
                String syear = arr[0];
                String smonth = fillLeadingZeros(arr[1], 2);
                String sday = currentDate.toString().split("-")[2];
                return java.sql.Date.valueOf(syear + "-" + smonth + "-" + sday); 
                
            } else if (sval.matches("[\\d]{1,2}-[\\d]{1,2}-[\\d]{4,4}")) {
                //month-day-year
                String[] arr = sval.split("-");
                String syear = arr[2];
                String smonth = fillLeadingZeros(arr[0], 2); 
                String sday = fillLeadingZeros(arr[1], 2); 
                return java.sql.Date.valueOf(syear +"-"+ smonth +"-"+ sday); 
                
            } else if (sval.matches("[\\d]{1,2}-[\\d]{1,2}-[\\d]{1,2}")) {                
                //month-day-year
                String[] arr = sval.split("-");
                String smonth = fillLeadingZeros(arr[0], 2);
                String sday = fillLeadingZeros(arr[1], 2);
                String syear = arr[2];
                if (syear.length() <= 2) {
                    String curyear = currentDate.toString().split("-")[0];
                    int num1 = Integer.parseInt(curyear.substring(0,2));
                    int num2 = Integer.parseInt(curyear.substring(2,4));
                    int num = Integer.parseInt(syear);
                    if (num > num2+_advanceYearLimit) num1--; 
                    
                    syear = fillLeadingZeros(num1+"", 2) + fillLeadingZeros(syear, 2);  
                } 
                return java.sql.Date.valueOf(syear + "-" + smonth + "-" + sday); 
                
            } else if (sval.matches("[\\d]{1,2}-[\\d]{1,2}")) {
                String[] arr = sval.split("-");
                String syear = currentDate.toString().split("-")[0];
                String smonth = fillLeadingZeros(arr[0], 2);
                String sday = fillLeadingZeros(arr[1], 2);
                return java.sql.Date.valueOf(syear + "-" + smonth + "-" + sday); 
                
            } else if (sval.matches("[a-zA-Z]{3,3}-[\\d]{1,2}")) {
                //jan 1, jan 01
                String[] arr = sval.split("-");
                String smonth = months.get(arr[0].toLowerCase());
                String sday = fillLeadingZeros(arr[1], 2);
                String syear = currentDate.toString().split("-")[0];
                return java.sql.Date.valueOf(syear + "-" + smonth + "-" + sday);
                
            } else if (sval.matches("[a-zA-Z]{3,3}-[\\d]{1,2}-[\\d]{1,2}")) {
                //jan 1 98, jan 01 98
                String[] arr = sval.split("-");
                String smonth = months.get(arr[0].toLowerCase());
                String sday = fillLeadingZeros(arr[1], 2);
                String syear = arr[2];
                if (syear.length() <= 2) {
                    String curyear = currentDate.toString().split("-")[0];
                    int num1 = Integer.parseInt(curyear.substring(0,2));
                    int num2 = Integer.parseInt(curyear.substring(2,4));
                    int num = Integer.parseInt(syear);
                    if (num > num2+_advanceYearLimit) num1--; 
                    
                    syear = fillLeadingZeros(num1+"", 2) + fillLeadingZeros(syear, 2);  
                }                 
                return java.sql.Date.valueOf(syear + "-" + smonth + "-" + sday);
                
            } else if (sval.matches("[a-zA-Z]{3,3}-[\\d]{1,2}-[\\d]{4,4}")) {
                //jan 1 98, jan 01 98
                String[] arr = sval.split("-");
                String smonth = months.get(arr[0].toLowerCase());
                String sday = fillLeadingZeros(arr[1], 2);
                String syear = arr[2];
                return java.sql.Date.valueOf(syear + "-" + smonth + "-" + sday);
            }
        }
        return null; 
    } 
    
    private String fillLeadingZeros(String value, int length) {
        int rem = length - value.toString().length();
        if (rem <= 0) return value;
        
        StringBuffer sb = new StringBuffer();
        for (int i=0; i<rem; i++) sb.append("0");
        
        sb.append(value);
        return sb.toString(); 
    }
}
