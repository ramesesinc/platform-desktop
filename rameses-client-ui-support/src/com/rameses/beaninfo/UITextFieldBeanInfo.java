/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.beaninfo;

import com.rameses.beaninfo.editor.SwingConstantsHAlignment;
import com.rameses.beaninfo.editor.TextCasePropertyEditor;
import com.rameses.beaninfo.editor.TrimSpaceOptionPropertyEditor;
import java.beans.PropertyDescriptor;
import java.util.List;

/**
 *
 * @author wflores
 */
public class UITextFieldBeanInfo extends DefaultBeanInfo {

    private Class beanClass; 

    public UITextFieldBeanInfo( Class beanClass ) {
        this.beanClass = beanClass; 
    }
    
    public Class getBeanClass() {
        return beanClass; 
    }

    protected void loadProperties(List<PropertyDescriptor> list) {
        addBoolean(list, "readonly");
        addBoolean(list, "editable", false);
        addBoolean(list, "focusable" );
        addBoolean(list, "nullWhenEmpty");
        
        add(list, "text");
        add(list, "margin");
        add(list, "disabledTextColor");
        add(list, "actionCommand");
        add(list, "horizontalAlignment", SwingConstantsHAlignment.class);
        add(list, "filter");
        add(list, "focusAccelerator");
        add(list, "focusKeyStroke");
        add(list, "hint");
        add(list, "inputFormat");
        add(list, "inputFormatErrorMsg");
        add(list, "maxLength");
        add(list, "spaceChar");
        add(list, "textCase", TextCasePropertyEditor.class);
        add(list, "trimSpaceOption", TrimSpaceOptionPropertyEditor.class);
    }
}
