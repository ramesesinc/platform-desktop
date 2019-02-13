/*
 * XPhotoBeanInfo.java
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
public class XImageGalleryBeanInfo extends ComponentBeanInfoSupport {
    
    private Class beanClass;
    
    public Class getBeanClass() {
        if (beanClass == null) { 
            beanClass = XImageGallery.class;
        }
        return beanClass; 
    }
    
    protected void loadProperties(List<PropertyDescriptor> list) { 
        add( list, "cellBorder" );
        add( list, "cellSpacing" );
        add( list, "cellSize" );
        add( list, "enabledWhen", true );
        add( list, "handler", true );
        add( list, "selectionBorderColor" );
        add( list, "singleColumnOnly", true );
        add( list, "singleRowOnly", true );
        add( list, "rowCount", true );
    }
}
