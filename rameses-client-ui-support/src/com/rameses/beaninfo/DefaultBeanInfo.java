/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.beaninfo;

import java.beans.Beans;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author wflores
 */
public abstract class DefaultBeanInfo extends SimpleBeanInfo {
    
    private BeanInfoHelper helper;

    public abstract Class getBeanClass();
    protected abstract void loadProperties( List<PropertyDescriptor> list );
    
    public BeanInfoHelper getHelper() {
        if ( helper == null ) {
            helper = new BeanInfoHelper(); 
        }
        return helper;
    }
    
    public final PropertyDescriptor[] getPropertyDescriptors() {
        if (Beans.isDesignTime()) {
            ArrayList<PropertyDescriptor> list = new ArrayList();
            loadProperties( list ); 
            return list.toArray(new PropertyDescriptor[]{}); 
        } else {
            return super.getPropertyDescriptors(); 
        }
    } 
    
    protected void add( List list, String name ) {
        add( list, name, false ); 
    }
    protected void add( List list, String name, boolean preferred ) {
        add( list, name, preferred, null ); 
    }
    protected void add( List list, String name, Class editorClass ) {
        add( list, name, false, editorClass ); 
    }
    protected void add( List list, String name, boolean preferred, Class editorClass ) {
        try {
            PropertyDescriptor pd = getHelper().createPropertyDescriptor(name, getBeanClass(), preferred); 
            if ( pd == null ) return; 

            if ( editorClass != null ) {
                pd.setPropertyEditorClass(editorClass); 
            }
            list.add( pd ); 
        } catch(Throwable t) {
            t.printStackTrace(); 
        }
    }
    
    protected void addBoolean( List list, String name ) {
        addBoolean(list, name, false);
    }
    protected void addBoolean( List list, String name, boolean preferred ) {
        try { 
            PropertyDescriptor pd = getHelper().createPropertyDescriptorBoolean(name, getBeanClass(), false); 
            if ( pd != null ) list.add( pd ); 
        } catch(Throwable t) { 
            t.printStackTrace(); 
        } 
    } 
    
}
