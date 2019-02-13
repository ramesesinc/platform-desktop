/*
 * XTreeBeanInfo.java
 *
 * Created on May 4, 2013, 9:34 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control;

import com.rameses.beaninfo.ComponentBeanInfoSupport;
import java.beans.PropertyDescriptor;
import java.util.List;

/**
 *
 * @author wflores
 */
public class XTreeBeanInfo extends ComponentBeanInfoSupport {
    
    private Class beanClass;
    
    public Class getBeanClass() {
        if (beanClass == null) {
            beanClass = XTree.class;
        } 
        return beanClass;
    }

    protected void loadProperties(List<PropertyDescriptor> list) { 
        add( list, "handler", true ); 
    }
}
