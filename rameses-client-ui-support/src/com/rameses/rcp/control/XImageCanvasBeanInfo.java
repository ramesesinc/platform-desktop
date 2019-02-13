/*
 * XImageCanvasBeanInfo.java
 *
 * Created on December 4, 2013, 11:10 AM
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
public class XImageCanvasBeanInfo extends ComponentBeanInfoSupport {
    
    private Class beanClass;
    
    public Class getBeanClass() {
        if (beanClass == null) {
            beanClass = XImageCanvas.class;
        }
        return beanClass; 
    }
    
    protected void loadProperties(List<PropertyDescriptor> list) { 
        add( list, "text" );
        add( list, "noImageBackground" );
        add( list, "noImageForeground" );
    }
}
