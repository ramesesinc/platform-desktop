/*
 * XDropDownListBeanInfo.java
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
public class XDropDownListBeanInfo extends ComponentBeanInfoSupport
{
    private Class beanClass;
    
    public Class getBeanClass() {
        if (beanClass == null) { 
            beanClass = XDropDownList.class;
        }
        return beanClass; 
    }
    
    protected void loadProperties(List<PropertyDescriptor> list) { 
        addBoolean( list, "borderPainted" );
        addBoolean( list, "contentAreaFilled" );
        addBoolean( list, "focusable" );
        addBoolean( list, "hideOnEmptyResult" );
        
        add( list, "accelerator" );
        add( list, "margin" );
        add( list, "mnemonic" );
        add( list, "text" );
        add( list, "icon" );
        add( list, "iconResource" );
        add( list, "cellHeight" );
        add( list, "popupSize" );
        add( list, "handler", true );
        add( list, "handlerObject" );
        add( list, "itemExpression", true );
        add( list, "title" );
        add( list, "varName" );
    }    
}
