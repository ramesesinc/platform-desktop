package com.rameses.beaninfo.editor;

import com.rameses.rcp.constant.UIConstants;
import java.beans.PropertyEditorSupport;

public class UIConstantsOrientationPropertyEditor extends PropertyEditorSupport {
    
    private String[] values = new String[] { 
        
        UIConstants.VERTICAL, UIConstants.HORIZONTAL, 
        UIConstants.FLOW, UIConstants.XFLOW         
    };
       
    
    public String[] getTags() { return values; }
    
    public String getJavaInitializationString() { 
        StringBuffer sb = new StringBuffer(); 
        sb.append(UIConstants.class.getName() + "." + getValue()); 
        return sb.toString(); 
    }
    
}
