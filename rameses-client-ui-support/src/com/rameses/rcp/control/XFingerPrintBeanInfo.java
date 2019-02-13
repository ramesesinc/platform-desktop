/*
 * XFingerPrintBeanInfo.java
 *
 * Created on December 8, 2013, 11:19 PM
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
public class XFingerPrintBeanInfo extends ComponentBeanInfoSupport
{
    private Class beanClass;
    
    public Class getBeanClass() {
        if (beanClass == null) { 
            beanClass = XFingerPrint.class;
        }
        return beanClass; 
    }
    
    protected void loadProperties(List<PropertyDescriptor> list) { 
        addBoolean( list, "borderPainted" ); 
        addBoolean( list, "contentAreaFilled" ); 
        addBoolean( list, "defaultCommand", true ); 
        addBoolean( list, "immediate" ); 
        addBoolean( list, "focusable" ); 
        
        add( list, "accelerator" ); 
        add( list, "margin" ); 
        add( list, "mnemonic" ); 
        add( list, "text" ); 
        add( list, "icon" ); 
        add( list, "iconResource" ); 
        add( list, "target" ); 
        add( list, "handler", true ); 
    }    
}
