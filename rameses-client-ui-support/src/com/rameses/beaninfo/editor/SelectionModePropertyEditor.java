package com.rameses.beaninfo.editor;

import com.rameses.rcp.swing.SelectionMode;
import java.beans.PropertyEditorSupport;

public class SelectionModePropertyEditor extends PropertyEditorSupport 
{
    private String[] values;
    
    public SelectionModePropertyEditor() {
        values = new String[] { 
            SelectionMode.SINGLE, 
            SelectionMode.TOGGLE, 
            SelectionMode.MULTIPLE 
        }; 
    }
    
    public String[] getTags() { return values; }
    
    public String getJavaInitializationString() {
        StringBuffer sb = new StringBuffer();
        sb.append(SelectionMode.class.getName() + "." + getAsText());
        return sb.toString();
    }
}
