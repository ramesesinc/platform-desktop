/*
 * XTabbedPaneBeanInfo.java
 *
 * Created on September 2, 10:00 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control;

import com.rameses.beaninfo.ComponentBeanInfoSupport;
import com.rameses.beaninfo.editor.TabLayoutPolicyEditor;
import com.rameses.beaninfo.editor.TabPlacementEditor;
import java.beans.PropertyDescriptor;
import java.util.List;

/**
 *
 * @author wflores
 */
public class XTabbedPaneBeanInfo extends ComponentBeanInfoSupport {
    
    private Class beanClass;
    
    public Class getBeanClass() {
        if (beanClass == null) { 
            beanClass = XTabbedPane.class;
        }
        return beanClass; 
    }

    protected void loadProperties(List<PropertyDescriptor> list) { 
        add( list, "handler", true); 
        add( list, "items", true ); 
        add( list, "tabLayoutPolicy", TabLayoutPolicyEditor.class ); 
        add( list, "tabPlacement", TabPlacementEditor.class ); 
        
        addBoolean( list, "nameAutoLookupAsOpener" ); 
    }
}
