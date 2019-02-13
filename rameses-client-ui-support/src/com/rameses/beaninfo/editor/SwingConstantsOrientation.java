package com.rameses.beaninfo.editor;

import java.beans.PropertyEditorSupport;
import javax.swing.SwingConstants;

public class SwingConstantsOrientation extends PropertyEditorSupport 
{
    private String[] values;
    
    public SwingConstantsOrientation() {
        values = new String[] { 
            "HORIZONTAL", "VERTICAL" 
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
        if (value instanceof Integer) {
            int ival = ((Integer) value).intValue();
            if (ival == SwingConstants.VERTICAL) {
                return values[1]; 
            } else {
                return values[0]; 
            }
        } else {
            return values[0]; 
        }
    }
    
    public void setAsText(String text) throws IllegalArgumentException {
        if (values[0].equals(text)) {
            setValue(SwingConstants.HORIZONTAL); 
        } else if (values[1].equals(text)) {
            setValue(SwingConstants.VERTICAL); 
        } 
    }
}
