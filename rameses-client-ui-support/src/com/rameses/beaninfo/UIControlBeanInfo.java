/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.beaninfo;

import com.rameses.beaninfo.editor.CaptionOrientationPropertyEditor;
import com.rameses.beaninfo.editor.UIConstantsHAlignmentPropertyEditor;
import com.rameses.beaninfo.editor.UIConstantsVAlignmentPropertyEditor;
import java.beans.PropertyDescriptor;
import java.util.List;

/**
 *
 * @author wflores
 */
public class UIControlBeanInfo extends DefaultBeanInfo {

    private Class beanClass; 

    public UIControlBeanInfo( Class beanClass ) {
        this.beanClass = beanClass; 
    }
    
    public Class getBeanClass() {
        return beanClass; 
    }

    protected void loadProperties(List<PropertyDescriptor> list) {
        add( list, "depends", true );
        add( list, "index" );
        add( list, "stretchWidth" );
        add( list, "stretchHeight" );
        add( list, "visibleWhen", true );
        add( list, "disableWhen", true );
        add( list, "fontStyle" );
        add( list, "expression", true );

        add( list, "caption", true );
        add( list, "captionBorder" );
        add( list, "captionMnemonic" );
        add( list, "index" );
        add( list, "captionWidth" );
        add( list, "captionFont" );
        add( list, "captionFontStyle" );
        add( list, "captionForeground" );
        add( list, "captionPadding" );
        add( list, "cellPadding" );
        
        addBoolean( list, "dynamic" );
        addBoolean( list, "required", true );
        addBoolean( list, "showCaption" );
    }
}
