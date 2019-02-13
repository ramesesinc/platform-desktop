package com.rameses.beaninfo.editor;

import java.beans.PropertyEditorSupport;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

public class TabLayoutPolicyEditor extends PropertyEditorSupport 
{
    private String[] values;
    
    public TabLayoutPolicyEditor() { 
        values = new String[] { 
            "WRAP_TAB_LAYOUT", "SCROLL_TAB_LAYOUT" 
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
            if (ival == JTabbedPane.SCROLL_TAB_LAYOUT) {
                return values[1]; 
            } else {
                return values[0]; 
            } 
        } else {
            return values[0]; 
        }
    }
    
    public void setAsText(String text) throws IllegalArgumentException {
        if (values[1].equals(text)) {
            setValue( JTabbedPane.SCROLL_TAB_LAYOUT ); 
        } else {
            setValue( JTabbedPane.WRAP_TAB_LAYOUT ); 
        }
    }
}
