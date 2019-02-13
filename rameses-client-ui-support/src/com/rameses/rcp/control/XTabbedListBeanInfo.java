/*
 * XTabbedListBeanInfo.java
 *
 * Created on September 2, 10:00 PM
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
public class XTabbedListBeanInfo extends ComponentBeanInfoSupport {
    
    private Class beanClass;
    
    public Class getBeanClass() {
        if (beanClass == null) { 
            beanClass = XTabbedList.class;
        }
        return beanClass; 
    }

    protected void loadProperties(List<PropertyDescriptor> list) { 
    }
}
