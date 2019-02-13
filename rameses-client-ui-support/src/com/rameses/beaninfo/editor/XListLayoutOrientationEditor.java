package com.rameses.beaninfo.editor;

import com.rameses.rcp.control.XList;
import java.beans.PropertyEditorSupport;

public class XListLayoutOrientationEditor extends PropertyEditorSupport {
    
    private String[] values = new String[]{ "VERTICAL", "VERTICAL_WRAP", "HORIZONTAL_WRAP" };
    

    public String[] getTags() { return values; }
    
    public String getJavaInitializationString() {
        StringBuffer sb = new StringBuffer();
        sb.append(XList.class.getName() + "." + getAsText());
        return sb.toString();
    }
    
    public String getAsText() {
        if( getValue() == null );
        else if( getValue().equals(XList.VERTICAL_WRAP) ) return "VERTICAL_WRAP";
        else if( getValue().equals(XList.HORIZONTAL_WRAP) ) return "HORIZONTAL_WRAP";
        
        return "VERTICAL";
    }
    
    public void setAsText(String text) throws IllegalArgumentException {
        if ( "VERTICAL_WRAP".equals(text) ) 
            setValue(XList.VERTICAL_WRAP);
        else if ( "HORIZONTAL_WRAP".equals(text) ) 
            setValue(XList.HORIZONTAL_WRAP);
        else
            setValue(XList.VERTICAL);
    }
    
}
