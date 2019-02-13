package com.rameses.beaninfo.editor;

import java.beans.PropertyEditorSupport;
import javax.swing.JTabbedPane;

public class TabPlacementEditor extends PropertyEditorSupport 
{
    private String[] values;
    private int[] keys; 
    
    public TabPlacementEditor() { 
        values = new String[] { 
            "TOP", "LEFT", "BOTTOM", "RIGHT"  
        }; 
        keys = new int[]{
            JTabbedPane.TOP, JTabbedPane.LEFT,
            JTabbedPane.BOTTOM, JTabbedPane.RIGHT  
        }; 
    }
    
    public String[] getTags() { return values; }
    
    public String getJavaInitializationString() {
        StringBuffer sb = new StringBuffer();
        sb.append( JTabbedPane.class.getName() + "." + getAsText());
        return sb.toString();
    }
    
    public String getAsText() {
        Object value = getValue();
        if (value instanceof Integer) {
            int ival = ((Integer) value).intValue();
            for (int i=0; i<keys.length; i++) {
                if ( keys[i] == ival ) {
                    return values[i]; 
                } 
            }
        }
        return values[0]; 
    }
    
    public void setAsText(String text) throws IllegalArgumentException { 
        int value = keys[0]; 
        for ( int i=0; i<values.length; i++ ) {
            if ( values[i].equals(text)) {
                value = keys[i]; 
            }
        }
        setValue( value ); 
    }
}
