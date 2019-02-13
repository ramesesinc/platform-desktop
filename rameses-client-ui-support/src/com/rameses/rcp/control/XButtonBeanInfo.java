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
import com.rameses.beaninfo.editor.SwingConstantsHTextPosition;
import com.rameses.beaninfo.editor.SwingConstantsVAlignment;
import com.rameses.beaninfo.editor.SwingConstantsVTextPosition;
import java.beans.PropertyDescriptor;
import java.util.List;

/**
 *
 * @author wflores
 */
public class XButtonBeanInfo extends ComponentBeanInfoSupport
{
    private Class beanClass;
    
    public Class getBeanClass() {
        if (beanClass == null) { 
            beanClass = XButton.class;
        }
        return beanClass; 
    }
        
    protected void loadProperties(List<PropertyDescriptor> list) { 
        add( list, "accelerator" );
        add( list, "margin" );
        add( list, "mnemonic" );
        add( list, "text" );
        add( list, "icon" );
        add( list, "iconResource" );
        add( list, "iconTextGap" );
        add( list, "horizontalTextPosition", SwingConstantsHTextPosition.class );
        add( list, "verticalTextPosition", SwingConstantsVTextPosition.class );
        add( list, "verticalAlignment", SwingConstantsVAlignment.class );
        add( list, "target" );
        add( list, "params" );
        
        addBoolean( list, "autoRefresh" );
        addBoolean( list, "borderPainted" );
        addBoolean( list, "contentAreaFilled" );
        addBoolean( list, "defaultCommand" );
        addBoolean( list, "immediate" );
        addBoolean( list, "focusable" );
    }
}
