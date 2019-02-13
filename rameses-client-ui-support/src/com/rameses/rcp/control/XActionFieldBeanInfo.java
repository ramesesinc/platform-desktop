/*
 * XActionFieldBeanInfo.java
 *
 * Created on December 7, 2013, 10:43 AM
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
public class XActionFieldBeanInfo extends ComponentBeanInfoSupport 
{
    private Class beanClass;
    
    public Class getBeanClass() {
        if (beanClass == null) {
            beanClass = XActionField.class;
        } 
        return beanClass;
    }
    
    protected void loadAdditionalBeanInfo( List<BeanInfo> list ) { 
        list.add( new UITextFieldBeanInfo( getBeanClass())); 
    }    
    
    protected void loadProperties(List<PropertyDescriptor> list) { 
        add( list, "expression", true );
        add( list, "handler", true );
        add( list, "spacing" );
        add( list, "actionFont" );
        add( list, "actionFontStyle" );
        add( list, "actionIcon" );
        add( list, "actionText" );
        add( list, "actionTextMargin" );
    }     
}
