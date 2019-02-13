/*
 * XActionBarBeanInfo.java
 *
 * Created on October 8, 2010, 1:37 PM
 * @author jaycverg
 */

package com.rameses.rcp.control;

import com.rameses.beaninfo.ComponentBeanInfoSupport;
import com.rameses.beaninfo.editor.TextAlignmentPropertyEditor;
import com.rameses.beaninfo.editor.UIConstantsOrientationPropertyEditor;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.List;

public class XActionBarBeanInfo extends ComponentBeanInfoSupport {
    
    private Class beanClass; 
    
    public Class getBeanClass() { 
        if (beanClass == null) {
            beanClass = XActionBar.class;
        } 
        return beanClass; 
    }

    protected void loadProperties(List<PropertyDescriptor> list) { 
        addBoolean( list, "buttonAsHyperlink" );
        addBoolean( list, "buttonBorderPainted" );
        addBoolean( list, "buttonContentAreaFilled" );
        addBoolean( list, "buttonTextInHtml" );
        addBoolean( list, "showCaptions" );
        addBoolean( list, "useToolBar" );
        
        add( list, "buttonCaptionOrientation" );
        add( list, "buttonFont" );
        add( list, "buttonForeground" );
        add( list, "buttonPreferredSize" );
        add( list, "formName", true );
        add( list, "textAlignment", TextAlignmentPropertyEditor.class );
        add( list, "textPosition", TextAlignmentPropertyEditor.class );
        add( list, "orientation", UIConstantsOrientationPropertyEditor.class );
        add( list, "horizontalAlignment" );
        add( list, "orientationHAlignment" );
        add( list, "orientationVAlignment" );
        add( list, "padding" );
        add( list, "spacing" );
        add( list, "target" );
    }
}
