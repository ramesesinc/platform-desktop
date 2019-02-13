/*
 * XScrollPane.java
 *
 * Created on September 16, 2013, 2:50 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control;

import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

/**
 *
 * @author wflores
 */
public class XScrollPane extends JScrollPane 
{    
    private FocusChangeListener focusChangeListener;
    
    public XScrollPane() {
        focusChangeListener = new FocusChangeListener(); 
    }
        
    public void setViewportView(Component view) {
        super.setViewportView(view); 
        
        KeyboardFocusManager kfm = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        kfm.removePropertyChangeListener("focusOwner", focusChangeListener);
        if (view != null) {
            kfm.addPropertyChangeListener("focusOwner", focusChangeListener);
        }         
    }
    
    // <editor-fold defaultstate="collapsed" desc=" FocusChangeListener ">
    
    private class FocusChangeListener implements PropertyChangeListener
    {
        XScrollPane root = XScrollPane.this;
        
        public void propertyChange(PropertyChangeEvent evt) {
            Object newValue = evt.getNewValue();
            if (!(newValue instanceof JComponent)) return;

            JComponent view = (JComponent) root.getViewport().getView();
            if (view == null) return;
            
            JComponent focused = (JComponent) newValue;
            if (view.isAncestorOf(focused)) {
                JComponent jparent = (JComponent) focused.getParent(); 
                if (jparent instanceof JViewport) {
                    jparent = (JComponent) ((JViewport)jparent).getParent().getParent();
                }
                if (jparent != null) 
                    jparent.scrollRectToVisible(focused.getBounds());
            }
        } 
    }
    
    // </editor-fold>
    
}
