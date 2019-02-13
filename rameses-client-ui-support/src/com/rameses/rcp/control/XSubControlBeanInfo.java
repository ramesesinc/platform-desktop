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
import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;
import java.util.List;

/**
 *
 * @author wflores
 */
public class XSubControlBeanInfo extends ComponentBeanInfoSupport {
    
    private Class beanClass;
    
    public Class getBeanClass() {
        if (beanClass == null) { 
            beanClass = XSubControl.class;
        }
        return beanClass; 
    }

    protected void loadAdditionalBeanInfo(List<BeanInfo> list) {
        list.add( new XSubFormPanelBeanInfo() ); 
    }
    
    protected void loadProperties(List<PropertyDescriptor> list) { 
        addBoolean( list, "editable" ); 
        addBoolean( list, "handlerAutoLookup" ); 
    }
}
