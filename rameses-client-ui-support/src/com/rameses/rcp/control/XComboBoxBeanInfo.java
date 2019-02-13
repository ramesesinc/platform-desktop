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
import java.beans.PropertyDescriptor;
import java.util.List;

/**
 *
 * @author wflores
 */
public class XComboBoxBeanInfo extends ComponentBeanInfoSupport
{
    private Class beanClass;
    
    public Class getBeanClass() {
        if (beanClass == null) {
            beanClass = XComboBox.class;
        }
        return beanClass; 
    }
    
    protected void loadProperties(List<PropertyDescriptor> list) { 
        addBoolean( list, "allowNull" );
        addBoolean( list, "autoDefaultValue" );
        addBoolean( list, "immediate" );
        
        add( list, "emptyText" );
        add( list, "fieldType" );
        add( list, "itemKey", true );
        add( list, "items", true );
        add( list, "itemsObject" );
        add( list, "varName" );
    }
}
