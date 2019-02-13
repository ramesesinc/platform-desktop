/*
 * SuggestTypePropertyEditor.java
 *
 * Created on April 22, 2014, 12:45 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.beaninfo.editor;

import com.rameses.rcp.constant.UIConstants;
import java.beans.PropertyEditorSupport;

/**
 *
 * @author wflores
 */
public class SuggestTypePropertyEditor extends PropertyEditorSupport
{
    private String[] values;
    
    public SuggestTypePropertyEditor() {
        values = new String[] { 
            UIConstants.SuggestTypes.BASIC,  
            UIConstants.SuggestTypes.LOOKUP 
        };
    }
    
    public String[] getTags() { return values; }

    public String getJavaInitializationString() {
        StringBuffer sb = new StringBuffer();
        sb.append(UIConstants.class.getName() + ".SuggestTypes." + getValue());
        return sb.toString(); 
    } 
}
