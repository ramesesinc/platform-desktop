package com.rameses.beaninfo.editor;

import java.beans.PropertyEditorSupport;
import javax.swing.SwingConstants;

public class SwingConstantsVTextPosition extends PropertyEditorSupport 
{
    private String[] values;
    
    public SwingConstantsVTextPosition() {
        values = new String[] { 
            "CENTER", "TOP", "BOTTOM" 
        }; 
    }
    
    public String[] getTags() { return values; }
    
    public String getJavaInitializationString() {
        StringBuffer sb = new StringBuffer();
        sb.append(SwingConstants.class.getName() + "." + getAsText());
        return sb.toString();
    }
    
    public String getAsText() {
        Object value = getValue();
        if (value instanceof Number) {
            int ival = ((Number) value).intValue();
            if (ival == SwingConstants.TOP) { return values[1]; } 
            else if (ival == SwingConstants.BOTTOM) { return values[2]; } 
        } 
        return values[0]; 
    }
    
    public void setAsText(String text) throws IllegalArgumentException {
        if (values[0].equals(text)) {
            setValue(SwingConstants.CENTER); 
        } else if (values[1].equals(text)) {
            setValue(SwingConstants.TOP); 
        } else if (values[2].equals(text)) {
            setValue(SwingConstants.BOTTOM); 
        } else {
            setValue(SwingConstants.CENTER); 
        } 
    }
}
