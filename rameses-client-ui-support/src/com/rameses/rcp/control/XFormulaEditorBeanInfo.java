/*
 * XTextFieldBeanInfo.java
 *
 * Created on May 4, 2013, 9:34 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control;

import com.rameses.beaninfo.ComponentBeanInfoSupport;
import com.rameses.beaninfo.editor.TextCasePropertyEditor;
import com.rameses.beaninfo.editor.TrimSpaceOptionPropertyEditor;
import java.beans.PropertyDescriptor;
import java.util.List;

/**
 *
 * @author wflores
 */
public class XFormulaEditorBeanInfo extends ComponentBeanInfoSupport {
    
    private Class beanClass;
    
    public Class getBeanClass() {
        if (beanClass == null) { 
            beanClass = XFormulaEditor.class;
        } 
        return beanClass;
    }

    protected void loadProperties(List<PropertyDescriptor> list) { 
        addBoolean( list, "nullWhenEmpty", true ); 
        addBoolean( list, "readonly", true ); 
        addBoolean( list, "editable", true ); 
        
        add( list, "handler", true );
        add( list, "handlerObject" );
        add( list, "keywordItems", true );
        add( list, "textCase", TextCasePropertyEditor.class );
        add( list, "trimSpaceOption", TrimSpaceOptionPropertyEditor.class );
    }
}
