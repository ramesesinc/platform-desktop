/*
 * TextComponentSupport.java
 *
 * Created on October 9, 2013, 10:22 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control.text;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JTextArea;
import javax.swing.text.JTextComponent;

/**
 *
 * @author wflores
 */
public final class TextComponentSupport 
{
    public static TextComponentSupport getInstance() {
        return new TextComponentSupport(); 
    }
    
    
    private TextComponentSupport() {
    }
    
    public void installUIDefaults(JTextComponent jtxt) 
    {
        if (jtxt == null) return;
        
        jtxt.setMargin(new Insets(1,3,1,2));
        jtxt.setDisabledTextColor(new Color(60,60,60));
        
        FocusListener focusL = (FocusListener) jtxt.getClientProperty(FocusListenerImpl.class); 
        if (focusL == null) {
            focusL = new FocusListenerImpl(jtxt);
            jtxt.addFocusListener(focusL); 
            jtxt.putClientProperty(FocusListenerImpl.class, focusL); 
        }
        
        if ( jtxt instanceof JTextArea ) {
            DefaultTextField tmp = new DefaultTextField(); 
            Font font = tmp.getFont(); 
            if ( font != null ) {
                jtxt.setFont(new Font( font.getFontName(), font.getStyle(), font.getSize())); 
            }
        }
    }
    
    public void uninstallUIDefaults(JTextComponent jtxt) 
    {
        if (jtxt == null) return;
        
        FocusListener focusL = (FocusListener) jtxt.getClientProperty(FocusListenerImpl.class); 
        if (focusL != null) jtxt.removeFocusListener(focusL);
    }    
    
    
    // <editor-fold defaultstate="collapsed" desc=" Focus supporting methods ">
    
    private class FocusListenerImpl implements FocusListener 
    {
        private JTextComponent jtxt;
        
        FocusListenerImpl(JTextComponent jtxt) {
            this.jtxt = jtxt; 
        }
        
        public void focusGained(FocusEvent focusEvent) { 
            jtxt.selectAll(); 
        } 
        
        public void focusLost(FocusEvent focusEvent) {
        }
    }    
    
    // </editor-fold>
}
