package com.rameses.rcp.control;

import com.rameses.beaninfo.ComponentBeanInfoSupport;
import java.beans.PropertyDescriptor;
import java.util.List;

public class XHtmlEditorBeanInfo extends ComponentBeanInfoSupport 
{
    private Class beanClass; 
    
    public Class getBeanClass() { 
        if (beanClass == null)  {
            beanClass = XHtmlEditor.class;
        } 
        return beanClass; 
    }
    
    protected void loadProperties(List<PropertyDescriptor> list) { 
        add( list, "handler", true );
        add( list, "itemExpression", true );
        add( list, "varName" );
    }
}
