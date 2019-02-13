package com.rameses.beaninfo.editor;

import java.beans.PropertyEditorSupport;
import javax.swing.SwingConstants;

public class SwingConstantsHTextPosition extends PropertyEditorSupport 
{
    private String[] values;
    
    public SwingConstantsHTextPosition() {
        values = new String[] { 
            "TRAILING", "RIGHT", "LEFT", "CENTER", "LEADING" 
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
            if (ival == SwingConstants.RIGHT) { return values[1]; } 
            else if (ival == SwingConstants.LEFT) { return values[2]; } 
            else if (ival == SwingConstants.CENTER) { return values[3]; } 
            else if (ival == SwingConstants.LEADING) { return values[4]; } 
        } 
        return values[0]; 
    }
    
    public void setAsText(String text) throws IllegalArgumentException {
        if (values[0].equals(text)) {
            setValue(SwingConstants.TRAILING); 
        } else if (values[1].equals(text)) {
            setValue(SwingConstants.RIGHT); 
        } else if (values[2].equals(text)) {
            setValue(SwingConstants.LEFT); 
        } else if (values[3].equals(text)) {
            setValue(SwingConstants.CENTER); 
        } else if (values[4].equals(text)) {
            setValue(SwingConstants.LEADING); 
        } else {
            setValue(SwingConstants.TRAILING); 
        } 
    }
}
