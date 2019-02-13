/*
 * XRadioListBeanInfo.java
 *
 * Created on May 4, 2013, 11:00 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control;

import com.rameses.beaninfo.ComponentBeanInfoSupport;
import com.rameses.beaninfo.editor.SelectionModePropertyEditor;
import com.rameses.beaninfo.editor.SwingConstantsOrientation;
import java.beans.PropertyDescriptor;
import java.util.List;

/**
 *
 * @author wflores
 */
public class XRadioListBeanInfo extends ComponentBeanInfoSupport {
    
    private Class beanClass;
    
    public Class getBeanClass() {
        if (beanClass == null) {
            beanClass = XRadioList.class;
        }
        return beanClass; 
    }
    
    protected void loadProperties(List<PropertyDescriptor> list) { 
        add( list, "handler", true ); 
        add( list, "itemExpression", true ); 
        add( list, "itemKey", true ); 
        add( list, "itemGap", true ); 
        add( list, "itemCount", true ); 
        add( list, "orientation", SwingConstantsOrientation.class ); 
        add( list, "selectionMode", SelectionModePropertyEditor.class ); 
        add( list, "padding" ); 
        add( list, "varName" ); 
    }
}
