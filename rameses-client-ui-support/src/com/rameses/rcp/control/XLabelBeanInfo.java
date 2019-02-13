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
import com.rameses.beaninfo.editor.SwingConstantsHAlignment;
import com.rameses.beaninfo.editor.SwingConstantsVAlignment;
import java.beans.PropertyDescriptor;
import java.util.List;

/**
 *
 * @author wflores
 */
public class XLabelBeanInfo extends ComponentBeanInfoSupport {
    
    private Class beanClass;
    
    public Class getBeanClass() {
        if (beanClass == null) {
            beanClass = XLabel.class;
        }
        return beanClass; 
    }
    
    protected void loadProperties(List<PropertyDescriptor> list) { 
        addBoolean( list, "antiAliasOn", true );
        addBoolean( list, "useHtml", true );
        
        add( list, "text" );
        add( list, "horizontalAlignment", SwingConstantsHAlignment.class );
        add( list, "verticalAlignment", SwingConstantsVAlignment.class );
        add( list, "for" );
        add( list, "format" );
        add( list, "iconResource" );
        add( list, "padding" );
        add( list, "varName" );
        
        add( list, "dateFormat" );
        add( list, "numberFormat" );
    }
}
