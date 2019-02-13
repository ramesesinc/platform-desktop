package com.rameses.rcp.constant;

public enum TextCase 
{  
    NONE(0),
    LOWER(1),
    UPPER(2);

    private int type;
    
    TextCase(int type) { 
        this.type = type; 
    }
    
    public String convert(String value)
    {
        if (value == null) return value;
        else if (type == 1) return toLower(value);
        else if (type == 2) return toUpper(value); 
        else return value;
    }
    
    private String toUpper(String value) {
        StringBuffer sb = new StringBuffer(value); 
        for (int i=0; i<sb.length(); i++) {
            char ch = sb.charAt(i);
            int num = (int)ch; 
            if ((num >= 65 && num <= 90)  || (num >= 97 && num <= 122) || 
                (num >= 225 && num <= 255) || (num >= 193 && num <= 376)) {
                sb.replace(i, i+1, Character.toUpperCase(ch)+""); 
            } 
        }
        return sb.toString(); 
    }
    
    private String toLower(String value) {
        StringBuffer sb = new StringBuffer(value); 
        for (int i=0; i<sb.length(); i++) {
            char ch = sb.charAt(i);
            int num = (int)ch; 
            if ((num >= 65 && num <= 90)  || (num >= 97 && num <= 122) || 
                (num >= 225 && num <= 255) || (num >= 193 && num <= 376)) {
                sb.replace(i, i+1, Character.toLowerCase(ch)+""); 
            } 
        }
        return sb.toString(); 
    }    
}
