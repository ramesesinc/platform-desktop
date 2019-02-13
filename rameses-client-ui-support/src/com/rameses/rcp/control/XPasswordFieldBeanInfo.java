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
import com.rameses.beaninfo.UITextFieldBeanInfo;
import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;
import java.util.List;

/**
 *
 * @author wflores
 */
public class XPasswordFieldBeanInfo extends ComponentBeanInfoSupport {
    
    private Class beanClass;
    
    public Class getBeanClass() {
        if (beanClass == null) { 
            beanClass = XPasswordField.class;
        }
        return beanClass; 
    }

    protected void loadAdditionalBeanInfo(List<BeanInfo> list) {
        list.add( new UITextFieldBeanInfo(getBeanClass())); 
    }
    
    protected void loadProperties(List<PropertyDescriptor> list) { 
        add( list, "echoChar" );
    }
}
