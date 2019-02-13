/*
 * ScrollbarVPolicyPropertyEditor.java
 *
 * Created on April 23, 2014, 4:17 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.beaninfo.editor;

import java.beans.PropertyEditorSupport;
import javax.swing.ScrollPaneConstants;

/**
 *
 * @author wflores 
 */
public class ScrollbarVPolicyPropertyEditor extends PropertyEditorSupport  
{
    private String[] values = new String[]{ 
        "AS_NEEDED", "NEVER", "ALWAYS" 
    };
    
    public String[] getTags() { return values; }
    
    public String getJavaInitializationString() {
        StringBuffer sb = new StringBuffer();
        sb.append(ScrollPaneConstants.class.getName() + ".VERTICAL_SCROLLBAR_" + getAsText());
        return sb.toString();
    }
    
    public String getAsText() {
        Object value = getValue();
        if ( value == null ) {
            return "AS_NEEDED";
        } else if (value.equals(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER)) {
            return "NEVER"; 
        } else if (value.equals(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS)) { 
            return "ALWAYS";
        } else {
            return "AS_NEEDED";
        }
    }
    
    public void setAsText(String text) throws IllegalArgumentException {
        if (text == null) {
            setValue(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED); 
        } else if ("NEVER".equals(text) ) {
            setValue(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER); 
        } else if ("ALWAYS".equals(text) ) {
            setValue(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS); 
        } else {
            setValue(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED); 
        } 
    } 
}
