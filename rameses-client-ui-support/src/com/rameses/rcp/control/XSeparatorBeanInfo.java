/*
 * XButtonBeanInfo.java
 *
 * Created on May 4, 2013, 11:00 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control;

import com.rameses.beaninfo.ComponentBeanInfoSupport;
import com.rameses.beaninfo.editor.UIConstantsHAlignmentPropertyEditor;
import com.rameses.beaninfo.editor.UIConstantsOrientationPropertyEditor;
import com.rameses.beaninfo.editor.UIConstantsVAlignmentPropertyEditor;
import java.beans.PropertyDescriptor;
import java.util.List;

/**
 *
 * @author wflores
 */
public class XSeparatorBeanInfo extends ComponentBeanInfoSupport {
    
    private Class beanClass;
    
    public Class getBeanClass() {
        if (beanClass == null) {
            beanClass = XSeparator.class;
        }
        return beanClass; 
    }
    
    protected void loadProperties(List<PropertyDescriptor> list) { 
        add( list, "lineColor", true ); 
        add( list, "lineShadow", true ); 
        add( list, "orientation", UIConstantsOrientationPropertyEditor.class ); 
        add( list, "orientationHPosition", UIConstantsHAlignmentPropertyEditor.class ); 
        add( list, "orientationVPosition", UIConstantsVAlignmentPropertyEditor.class ); 
        add( list, "padding" ); 
    }
}
