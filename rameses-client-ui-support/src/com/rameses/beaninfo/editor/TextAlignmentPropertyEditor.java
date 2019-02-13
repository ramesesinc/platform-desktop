package com.rameses.beaninfo.editor;

import java.beans.PropertyEditorSupport;

public class TextAlignmentPropertyEditor extends PropertyEditorSupport 
{    
    private String[] values = new String[] { 
        "CENTER", "LEFT", "RIGHT", 
        "TOP_LEFT", "TOP_CENTER", "TOP_RIGHT",
        "BOTTOM_LEFT", "BOTTOM_CENTER", "BOTTOM_RIGHT"
    };
    
    public String[] getTags() { return values; }
    
    public String getAsText() 
    {
        Object value = getValue();
        return (value == null? "LEFT": value.toString()); 
    }
    
    public void setAsText(String text) throws IllegalArgumentException 
    {
        String value = (text == null? "LEFT": text.toUpperCase()); 
        if (isValid(value)) 
            setValue(value);
        else
            setValue("LEFT");
    }
    
    public String getJavaInitializationString() { 
        return ("\"" +  getAsText() + "\""); 
    } 
    
    private boolean isValid(String value) 
    {
        for (int i=0; i<values.length; i++) {
            if (values[i].equalsIgnoreCase(value)) return true; 
        }
        return false; 
    } 
}
