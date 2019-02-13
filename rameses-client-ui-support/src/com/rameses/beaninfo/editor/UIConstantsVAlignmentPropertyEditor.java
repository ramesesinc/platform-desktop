package com.rameses.beaninfo.editor;

import com.rameses.rcp.constant.UIConstants;
import java.beans.PropertyEditorSupport;

public class UIConstantsVAlignmentPropertyEditor extends PropertyEditorSupport {
    
    private String[] values = new String[] { 
        UIConstants.TOP, 
        UIConstants.CENTER, 
        UIConstants.BOTTOM  
    };
       
    
    public String[] getTags() { return values; }
    
    public String getJavaInitializationString() { 
        StringBuilder sb = new StringBuilder(); 
        sb.append(UIConstants.class.getName());
        sb.append("."); 
        
        String str = (String) getValue();
        if (str == null) {
            sb.append( UIConstants.TOP ); 
        } else {
            sb.append( str ); 
        }
        return sb.toString(); 
    }
    
}
