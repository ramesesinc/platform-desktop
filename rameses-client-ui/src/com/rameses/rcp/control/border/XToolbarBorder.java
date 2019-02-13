/*
 * XToolbarBorder.java
 *
 * Created on April 28, 2013, 9:58 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control.border;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.border.AbstractBorder;

/**
 *
 * @author wflores
 */
public class XToolbarBorder extends AbstractBorder 
{
    private Map compProperties;
    private boolean showTopBorder;
    private boolean showBottomBorder;
    
    public XToolbarBorder() {
        this(true, true); 
    } 
    
    public XToolbarBorder(boolean showTopBorder, boolean showBottomBorder) 
    {
        this.showTopBorder = showTopBorder; 
        this.showBottomBorder = showBottomBorder;
    }    

    public boolean isShowTopBorder() { return showTopBorder; } 
    public void setShowTopBorder(boolean showTopBorder) {
        this.showTopBorder = showTopBorder; 
    }
    
    public boolean isShowBottomBorder() { return showBottomBorder; } 
    public void setShowBottomBorder(boolean showBottomBorder) {
        this.showBottomBorder = showBottomBorder; 
    }

    public Insets getBorderInsets(Component c) {
        return getBorderInsets(c, new Insets(0,0,0,0));
    }
    
    public Insets getBorderInsets(Component c, Insets insets) 
    {
        if (c instanceof JComponent)
        {
            compProperties = (Map) ((JComponent) c).getClientProperty("Border.properties"); 
            if (compProperties == null) compProperties = new HashMap(); 
        }
        else {
            compProperties = null; 
        }
        
        insets.top = 2;
        insets.bottom = 2;
        insets.left = 1;
        insets.right = 1;
        return insets;
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) 
    {
        boolean btop = isShowTopBorder();
        boolean bbottom = isShowBottomBorder(); 
        if (compProperties != null) 
        {
            if (compProperties.get("showTopBorder") != null) 
                btop = "true".equals(compProperties.get("showTopBorder").toString()); 
            if (compProperties.get("showBottomBorder") != null) 
                bbottom = "true".equals(compProperties.get("showBottomBorder").toString()); 
        }
        
        Color shadow = c.getBackground().darker();
        Color hilite = c.getBackground().brighter();

        Color oldColor = g.getColor(); 
        g.setColor(shadow);
        
        if (btop) g.drawLine(x, y, w, y); 
        if (bbottom) g.drawLine(x, h-2, w, h-2); 
        
        g.setColor(hilite);
        if (btop) g.drawLine(x, y+1, w, y+1); 
        if (bbottom) g.drawLine(x, h-1, w, h-1);         
        
        g.setColor(oldColor); 
    }

}
