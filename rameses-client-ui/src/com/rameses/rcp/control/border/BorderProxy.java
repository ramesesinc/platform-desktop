/*
 * BorderProxy.java
 *
 * Created on May 2, 2013, 2:13 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control.border;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;

/**
 *
 * @author wflores
 */
public class BorderProxy extends AbstractBorder 
{
    private Border border;
    
    public BorderProxy() {
    }
    
    public BorderProxy(Border border) {
        this.border = border; 
    }    
    
    public Border getBorder() { return this.border; }
    public void setBorder(Border border) { this.border = border; }
    
    public Insets getBorderInsets(Component c) {
        return getBorderInsets(c, new Insets(0, 0, 0, 0)); 
    }
    
    public Insets getBorderInsets(Component c, Insets insets) {
        if (border == null)
            return insets;
        else if (border instanceof AbstractBorder) 
            return ((AbstractBorder) border).getBorderInsets(c, insets); 
        else 
            return border.getBorderInsets(c); 
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) 
    {
        if (border != null) border.paintBorder(c, g, x, y, w, h); 
    } 
}
