/*
 * TableHeaderBorder.java
 *
 * Created on April 27, 2013, 12:12 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.rameses.rcp.control.table;

import com.rameses.rcp.support.ColorUtil;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;

class TableHeaderBorder extends AbstractBorder
{
    private Insets margin = new Insets(0,0,0,0);
    
    TableHeaderBorder() {;}
    
    TableHeaderBorder(Insets margin) 
    {
        if (margin != null) this.margin = margin;
    }
    
    public Insets getBorderInsets(Component c) {
        return getBorderInsets(c, new Insets(0,0,0,0));
    }

    public Insets getBorderInsets(Component c, Insets insets) 
    {
        insets.top = margin.top;
        insets.left = margin.left;
        insets.bottom = margin.bottom;
        insets.right = margin.right;
        return insets;
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) 
    {
        Color shadow = UIManager.getColor("controlDkShadow");
        Color oldColor = g.getColor();

        g.setColor(ColorUtil.brighter(shadow,20));
        g.drawRect(0,0,w-1,h-1);
        g.setColor(ColorUtil.brighter(shadow,40));
        g.drawLine(1,h-2,w-2,h-2);
        g.drawLine(w-2,1,w-2,h-2);

        g.setColor(ColorUtil.brighter(shadow,55));
        g.drawRect(0,0,w,h);
        g.setColor(oldColor);
    }

}
