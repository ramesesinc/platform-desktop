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
import com.rameses.beaninfo.editor.XListLayoutOrientationEditor;
import java.beans.PropertyDescriptor;
import java.util.List;

/**
 *
 * @author wflores
 */
public class XListBeanInfo extends ComponentBeanInfoSupport {
    
    private Class beanClass;
    
    public Class getBeanClass() {
        if (beanClass == null) {
            beanClass = XList.class;
        }
        return beanClass; 
    }
    
    protected void loadProperties(List<PropertyDescriptor> list) { 
        add( list, "cellHorizontalAlignment", SwingConstantsHAlignment.class );
        add( list, "cellVerticalAlignment", SwingConstantsVAlignment.class );
        add( list, "cellHorizontalTextPosition", SwingConstantsHAlignment.class );
        add( list, "cellVerticalTextPosition", SwingConstantsVAlignment.class );
        add( list, "fixedCellHeight" );
        add( list, "fixedCellWidth" );
        add( list, "handler", true );
        add( list, "items", true );
        add( list, "openAction" );
        add( list, "padding" );
        add( list, "varName" );
        add( list, "varStatus" );
        add( list, "visibleRowCount" );
        add( list, "layoutOrientation", XListLayoutOrientationEditor.class );
        
        addBoolean( list, "multiselect", true ); 
    }
}
