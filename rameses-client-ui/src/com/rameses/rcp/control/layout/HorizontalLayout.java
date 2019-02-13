/*
 * LineLayout.java
 *
 * Created on April 28, 2013, 10:40 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control.layout;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;

/**
 *
 * @author wflores
 */
public class HorizontalLayout implements LayoutManager 
{
    private Border separator;
    private boolean showLeftSeparator;
    
    public HorizontalLayout() {
        setSeparator(new SeparatorBorder()); 
    }

    public Border getSeparator() { return this.separator; } 
    public void setSeparator(Border separator) { 
        this.separator = separator; 
    } 
    
    public void setShowLeftSeparator(boolean showLeftSeparator) {
        this.showLeftSeparator = showLeftSeparator; 
    }    
    
    public void addLayoutComponent(String name, Component comp) {}
    public void removeLayoutComponent(Component comp) {}

    private List<Component> getVisibleComponents(Container parent) 
    {
        List<Component> list = new ArrayList<Component>(); 
        Component[] comps = parent.getComponents(); 
        for (int i=0; i<comps.length; i++) 
        {
            if (!comps[i].isVisible()) continue;

            if (comps[i] instanceof SeparatorComponent) {
                parent.remove(comps[i]); 
                
            } else {
                if (comps[i] instanceof Container) {
                    Component[] innerComps = ((Container) comps[i]).getComponents();
                    if (innerComps.length == 0) {
                        list.add(comps[i]); 
                    } else { 
                        for (int ii=0; ii<innerComps.length; ii++) {
                            if (innerComps[ii].isVisible()) {
                                list.add(comps[i]); 
                                break; 
                            } 
                        } 
                    }
                } else { 
                    list.add(comps[i]);
                } 
            } 
        } 
        return list;
    }
    
    private Dimension getLayoutSize(Container parent) 
    {
        synchronized (parent.getTreeLock()) 
        {
            int w = 0, h = 0;
            Component sep = createSeparatorComponent(); 
            Dimension sepdim = (sep == null? new Dimension(0,0): sep.getPreferredSize());
            
            List<Component> list = getVisibleComponents(parent); 
            if (showLeftSeparator && !list.isEmpty()) {
                w += sepdim.width; 
                h = Math.max(h, sepdim.height); 
            } 
            
            boolean found = false;                        
            while (!list.isEmpty()) {                
                Component comp = list.remove(0); 
                Dimension dim = comp.getPreferredSize(); 
                
                if (found) { 
                    w += sepdim.width; 
                    h = Math.max(h, sepdim.height); 
                }
                
                w += dim.width;
                h = Math.max(h, dim.height); 
                found = true;
            }            
            Insets margin = parent.getInsets(); 
            return new Dimension(w+margin.left+margin.right, h+margin.top+margin.bottom);
        }
    }

    public Dimension preferredLayoutSize(Container parent) { 
        return getLayoutSize(parent);
    }

    public Dimension minimumLayoutSize(Container parent) {
        return getLayoutSize(parent);
    }

    public void layoutContainer(Container parent) 
    {
        synchronized (parent.getTreeLock()) 
        {        
            Insets margin = parent.getInsets();
            int x = margin.left;
            int y = margin.top;
            int w = parent.getWidth() - (margin.left + margin.right);
            int h = parent.getHeight() - (margin.top + margin.bottom); 
            
            boolean found = false;            
            List<Component> list = getVisibleComponents(parent); 
            if (showLeftSeparator && !list.isEmpty()) {
                Component sep = createSeparatorComponent(); 
                if (sep != null) { 
                    Dimension sepDim = sep.getPreferredSize();
                    parent.add(sep); 
                    sep.setBounds(x, y, sepDim.width, h); 
                    x += sepDim.width; 
                }                
            }
            
            while (!list.isEmpty()) { 
                Component comp = list.remove(0); 
                Dimension dim = comp.getPreferredSize();
                int pwidth = dim.width;
                if (list.isEmpty()) 
                    pwidth = Math.max(dim.width, w-x); 
                
                if (found) {
                    Component sep = createSeparatorComponent(); 
                    if (sep != null){
                        Dimension sepDim = sep.getPreferredSize();
                        parent.add(sep); 
                        sep.setBounds(x, y, sepDim.width, h); 
                        x += sepDim.width; 
                    } 
                }
                
                comp.setBounds(x, y, pwidth, h); 
                x += dim.width;
                found = true;
            }
        }
    }
    
    private Component createSeparatorComponent() 
    {
        Border border = getSeparator();
        if (border == null) return null; 
        
        SeparatorComponent c = new SeparatorComponent();
        c.setBorder(border); 
        return c;
    }
    
    
    private class SeparatorBorder extends AbstractBorder 
    {
        public Insets getBorderInsets(Component c) {
            return getBorderInsets(c, new Insets(0,0,0,0));
        }

        public Insets getBorderInsets(Component c, Insets insets) {
            insets.top = 5;
            insets.bottom = 5;
            insets.left = 2;
            insets.right = 2;
            return insets;
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) 
        {
            Color shadow = c.getBackground().darker();
            Color hilite = c.getBackground().brighter();

            Color oldColor = g.getColor(); 
            g.setColor(shadow);
            g.drawLine(x, y+5, x, h-6); 
            g.setColor(hilite);
            g.drawLine(x+1, y+5, x+1, h-6); 
            g.setColor(oldColor); 
        }
    }
    
    private class SeparatorComponent extends JLabel {} 
    
}
