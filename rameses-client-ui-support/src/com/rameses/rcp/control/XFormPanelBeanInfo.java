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
import com.rameses.beaninfo.editor.CaptionOrientationPropertyEditor;
import com.rameses.beaninfo.editor.OrientationPropertyEditor;
import com.rameses.beaninfo.editor.UIConstantsHAlignmentPropertyEditor;
import com.rameses.beaninfo.editor.UIConstantsVAlignmentPropertyEditor;
import java.beans.PropertyDescriptor;
import java.util.List;

/**
 *
 * @author wflores
 */
public class XFormPanelBeanInfo extends ComponentBeanInfoSupport
{
    private Class beanClass;
    
    public Class getBeanClass() {
        if (beanClass == null) { 
            beanClass = XFormPanel.class;
        }
        return beanClass; 
    }
    
    protected void loadProperties(List<PropertyDescriptor> list) { 
        addBoolean( list, "addCaptionColon" );
        addBoolean( list, "emptyText" );
        addBoolean( list, "emptyWhen" );
        addBoolean( list, "showCategory" );
        
        add( list, "captionOrientation", CaptionOrientationPropertyEditor.class );
        add( list, "captionHAlignment", UIConstantsHAlignmentPropertyEditor.class );
        add( list, "captionVAlignment", UIConstantsVAlignmentPropertyEditor.class );
        add( list, "orientation", OrientationPropertyEditor.class );
        
        add( list, "cellpadding" ); 
        add( list, "cellspacing" ); 
        add( list, "padding" ); 
        add( list, "viewType" ); 
    }
}
