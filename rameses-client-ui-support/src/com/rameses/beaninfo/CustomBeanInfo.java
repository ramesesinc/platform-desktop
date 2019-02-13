/*
 * ComponentBeanInfo.java
 *
 * Created on May 4, 2013, 9:24 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.beaninfo;

import com.rameses.rcp.common.MsgBox;
import java.awt.Image;
import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.Beans;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

/**
 *
 * @author wflores
 */
public abstract class CustomBeanInfo extends SimpleBeanInfo 
{
    private String iconName;
    private Class beanClass;

    public CustomBeanInfo() {
        this.beanClass = getBeanClass(); 
    }

    protected abstract PropertyDescriptor[] createPropertyDescriptors() throws IntrospectionException; 
    protected abstract Class getBeanClass();         

    public BeanDescriptor getBeanDescriptor() {
        return new BeanDescriptor(this.beanClass);
    }

    public Image getIcon(int paramInt)
    {
        return null; 
        //if (this.iconName == null) return null;
        //return Utilities.loadImage("org/netbeans/modules/form/beaninfo/awt/" + this.iconName + ".gif");
    }

    public BeanInfo[] getAdditionalBeanInfo() {
        return new BeanInfo[] {};
    }

    public PropertyDescriptor[] getPropertyDescriptors()
    {
        try 
        {
            if (Beans.isDesignTime()) 
                return createPropertyDescriptors(); 
            else 
                return super.getPropertyDescriptors(); 
        } 
        catch (IntrospectionException ie) 
        {
            MsgBox.err(ie); 
            return null;
        }
    }  

    protected PropertyDescriptor installEditor(PropertyDescriptor pd, Class editorClass) 
    {
        pd.setPropertyEditorClass(editorClass); 
        return pd; 
    } 
}
