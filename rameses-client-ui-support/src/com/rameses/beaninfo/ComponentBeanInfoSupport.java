/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.beaninfo;

import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author wflores
 */
public abstract class ComponentBeanInfoSupport extends DefaultBeanInfo {
    
    private String _icon = "gear.png";        
    private BeanInfoHelper helper;
    
    public BeanInfoHelper getHelper() {
        if ( helper == null ) {
            helper = new BeanInfoHelper(); 
        }
        return helper;
    }
    
    public BeanDescriptor getBeanDescriptor() {
        return new BeanDescriptor( getBeanClass(), null ); 
    } 
    
    protected BeanInfo getUIControlBeanInfo() {
        return new UIControlBeanInfo( getBeanClass() ); 
    }
    protected BeanInfo getBorderBeanInfo() {
        return new BorderBeanInfo( getBeanClass() ); 
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
            BeanInfo bi = getBorderBeanInfo(); 
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

    private void showError(Throwable t) {
        KeyboardFocusManager kfm = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        Window window = kfm.getActiveWindow();
        String errmsg = t.getClass().getName() + ": " + t.getMessage();
        JOptionPane.showMessageDialog(window, errmsg, "ERROR", JOptionPane.ERROR_MESSAGE);
    }
}
