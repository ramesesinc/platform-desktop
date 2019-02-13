/*
 * XSplitViewBeanInfo.java
 *
 * Created on August 25, 2013, 9:44 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control;

import com.rameses.beaninfo.ComponentBeanInfoSupport;
import com.rameses.beaninfo.editor.UIConstantsOrientationPropertyEditor;
import java.beans.PropertyDescriptor;
import java.util.List;

/**
 *
 * @author wflores
 */
public class XSplitViewBeanInfo extends ComponentBeanInfoSupport {
    
    private Class beanClass;
    
    public Class getBeanClass() {
        if (beanClass == null) {
            beanClass = XSplitView.class;
        }
        return beanClass; 
    }
    
    protected void loadProperties(List<PropertyDescriptor> list) { 
        add( list, "dividerLocation", true ); 
        add( list, "dividerLocationPercentage", true ); 
        add( list, "dividerSize", true ); 
        add( list, "orientation", true, UIConstantsOrientationPropertyEditor.class ); 
        addBoolean(list, "showDividerBorder", true );
    }
}
