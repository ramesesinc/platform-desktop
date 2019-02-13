package com.rameses.rcp.control;

import com.rameses.beaninfo.ComponentBeanInfoSupport;
import java.beans.PropertyDescriptor;
import java.util.List;

public class XHtmlViewBeanInfo extends ComponentBeanInfoSupport {
    
    private Class beanClass; 
    
    public Class getBeanClass() { 
        if (beanClass == null) {
            beanClass = XHtmlView.class;
        } 
        return beanClass; 
    }

    protected void loadProperties(List<PropertyDescriptor> list) { 
    }
}
