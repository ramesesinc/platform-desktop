/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.beaninfo;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

/**
 *
 * @author wflores 
 */
public class BeanInfoHelper {
    
    public PropertyDescriptor createPropertyDescriptor( String name, Class beanClass, boolean preferred ) throws IntrospectionException {
        PropertyDescriptor pd = new PropertyDescriptor( name, beanClass); 
        if ( preferred ) pd.setPreferred( preferred ); 
        return pd; 
    }

    public PropertyDescriptor createPropertyDescriptorBoolean( String name, Class beanClass, boolean preferred ) throws IntrospectionException { 
        StringBuilder sb = new StringBuilder();
        sb.append( Character.toUpperCase( name.charAt(0))); 
        if ( name.length() > 1 ) sb.append( name.substring(1) ); 

        PropertyDescriptor pd = new PropertyDescriptor( name, beanClass, "is"+sb, "set"+sb); 
        if ( preferred ) pd.setPreferred( preferred ); 
        return pd; 
    }

    public PropertyDescriptor installEditor(PropertyDescriptor pd, Class editorClass) {
        pd.setPropertyEditorClass(editorClass); 
        return pd; 
    } 
}
