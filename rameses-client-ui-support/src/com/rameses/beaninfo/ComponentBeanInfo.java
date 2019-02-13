/*
 * ComponentBeanInfo.java
 *
 * Created on May 4, 2013, 9:24 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.beaninfo;

import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.Beans;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JOptionPane;

/**
 *
 * @author wflores
 */
public class ComponentBeanInfo extends SimpleBeanInfo {
    
    private BeanInfoHelper helper = new BeanInfoHelper(); 
    
    public PropertyDescriptor[] getPropertyDescriptors() {
        try {
            return new PropertyDescriptor[] { 
                new PropertyDescriptor("background", Component.class), 
                new PropertyDescriptor("foreground", Component.class), 
                new PropertyDescriptor("font", Component.class), 
                
                createPropertyDescriptor("name", Component.class, true),
                createPropertyDescriptorBoolean("enabled", Component.class), 
                createPropertyDescriptorBoolean("opaque", JComponent.class), 
                
                new PropertyDescriptor("preferredSize", Component.class),
                new PropertyDescriptor("visible", Component.class), 
                new PropertyDescriptor("toolTipText", JComponent.class)
            };
        } 
        catch (IntrospectionException ie) { 
            ie.printStackTrace(); 
            return super.getPropertyDescriptors();
        }
    }

    protected PropertyDescriptor createPropertyDescriptor( String name, Class beanClass ) throws IntrospectionException { 
        return createPropertyDescriptor(name, beanClass, false );
    }    
    protected PropertyDescriptor createPropertyDescriptor( String name, Class beanClass, boolean preferred ) throws IntrospectionException {
        return helper.createPropertyDescriptor(name, beanClass, preferred); 
    }

    protected PropertyDescriptor createPropertyDescriptorBoolean( String name, Class beanClass ) throws IntrospectionException { 
        return createPropertyDescriptorBoolean(name, beanClass, false); 
    }    
    protected PropertyDescriptor createPropertyDescriptorBoolean( String name, Class beanClass, boolean preferred ) throws IntrospectionException { 
        return helper.createPropertyDescriptorBoolean(name, beanClass, preferred); 
    }
    
    
    public abstract static class Support extends SimpleBeanInfo {

        private BeanInfoHelper helper = new BeanInfoHelper();        
        private String _icon = "gear.png";        
        private Class beanClass;
        
        public Support() {
            this.beanClass = getBeanClass(); 
        }

        protected abstract PropertyDescriptor[] createPropertyDescriptors() throws IntrospectionException; 
        protected abstract Class getBeanClass();         

        public BeanDescriptor getBeanDescriptor() {
            return new BeanDescriptor(this.beanClass, null);
        }
        
        protected BeanInfo getUIControlBeanInfo() {
            return new UIControlBeanInfo( beanClass ); 
        }
        
        public BeanInfo[] getAdditionalBeanInfo() {
            ArrayList<BeanInfo> list = new ArrayList();
            list.add( new ComponentBeanInfo() ); 
            
            try {
                BeanInfo bi = getUIControlBeanInfo(); 
                if ( bi != null ) list.add( bi ); 
            } catch(Throwable t) { 
                t.printStackTrace(); 
            } 
                        
            try { 
                loadAdditionalBeanInfo( list ); 
            } catch(Throwable t) { 
                t.printStackTrace(); 
            } 
            
            return list.toArray(new BeanInfo[]{}); 
        }

        protected void loadAdditionalBeanInfo( List<BeanInfo> list ) {
        }
        
        public PropertyDescriptor[] getPropertyDescriptors() {
            try {
                if (Beans.isDesignTime()) 
                    return createPropertyDescriptors(); 
                else 
                    return super.getPropertyDescriptors(); 
            } catch (IntrospectionException ie) {
                ie.printStackTrace();
                showError(ie);
                return null;
            }
        }  
                
        protected PropertyDescriptor installEditor(PropertyDescriptor pd, Class editorClass) {
            return helper.installEditor(pd, editorClass); 
        } 
        
        private void showError(Throwable t) {
            KeyboardFocusManager kfm = KeyboardFocusManager.getCurrentKeyboardFocusManager();
            Window window = kfm.getActiveWindow();
            String errmsg = t.getClass().getName() + ": " + t.getMessage();
            JOptionPane.showMessageDialog(window, errmsg, "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    } 
}
