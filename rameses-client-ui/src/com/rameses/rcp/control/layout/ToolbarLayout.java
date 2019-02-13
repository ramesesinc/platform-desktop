/*
 * ActionBarLayout.java
 *
 * Created on April 28, 2013, 10:59 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control.layout;

import com.rameses.rcp.constant.UIConstants;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author wflores
 */
public class ToolbarLayout implements LayoutManager
{
    private Map<String,LayoutManager> layouts = new HashMap<String,LayoutManager>();
    private LayoutManager layout;
    private String orientation;
    private String alignment;
    private Insets padding;  
    private int spacing;
    
    public ToolbarLayout() 
    {
        layouts.put(UIConstants.HORIZONTAL, new HorizontalLayout()); 
        layouts.put(UIConstants.VERTICAL, new VerticalLayout());
        layouts.put(UIConstants.FLOW, new FlowLayout());
        layouts.put(UIConstants.XFLOW, new XFlowLayout());
        
        setOrientation(UIConstants.HORIZONTAL);
        setAlignment(UIConstants.LEFT);
        setPadding(null); 
    }
    
    public String getOrientation() { return this.orientation; }    
    public void setOrientation(String orientation) 
    {
        this.orientation = orientation;
        this.layout = layouts.get(orientation); 
        if (this.layout == null) 
        {
            this.orientation = UIConstants.HORIZONTAL;
            this.layout = layouts.get(orientation); 
        }
    }
    
    public String getAlignment() { return this.alignment; }
    public void setAlignment(String alignment) {  
        this.alignment = alignment; 
    }
    
    public Insets getPadding() { return this.padding; }
    public void setPadding(Insets padding) { 
        this.padding = (padding == null? new Insets(1,1,1,1): padding); 
    }
    
    public int getSpacing() { return spacing; }
    public void setSpacing(int spacing) { this.spacing = spacing; }

    
    public void addLayoutComponent(String name, Component comp) 
    {
        if (layout != null) layout.addLayoutComponent(name, comp); 
    }
    
    public void removeLayoutComponent(Component comp) 
    {
        if (layout != null) layout.removeLayoutComponent(comp);     
    }

    public Dimension preferredLayoutSize(Container parent) 
    {       
        if (layout != null) 
            return layout.preferredLayoutSize(parent);
        else
            return new Dimension(0, 0); 
    }

    public Dimension minimumLayoutSize(Container parent) 
    {
        if (layout != null) 
            return layout.minimumLayoutSize(parent);        
        else
            return new Dimension(0, 0);
    }

    public void layoutContainer(Container parent) 
    {
        if (layout != null) layout.layoutContainer(parent);
    }
    
    
    // <editor-fold defaultstate="collapsed" desc=" HorizontalLayout (Class) ">
    private class HorizontalLayout implements LayoutManager 
    {
        public void addLayoutComponent(String name, Component comp) {;}
        public void removeLayoutComponent(Component comp) {;}
        
        public Dimension getLayoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                int w=0, h=0;
                boolean found = false;
                Component[] comps = parent.getComponents();
                for (int i=0; i<comps.length; i++) {
                    Component c = comps[i];
                    if (!c.isVisible()) continue;
                    if (found) w += getSpacing();
                    Dimension dim = c.getPreferredSize();
                    w += (dim.width + padding.left+padding.right);
                    h = Math.max(h, dim.height+padding.top+padding.bottom);
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
                int endPoint = parent.getWidth() - margin.right;

                boolean found = false;                
                Component[] comps = parent.getComponents();
                
                if (UIConstants.RIGHT.equals(getAlignment()))
                {
                    x = parent.getWidth() - margin.right;
                    for (int i=comps.length; i>0; i--) 
                    {
                        if (!comps[i-1].isVisible()) continue;

                        Component comp = comps[i-1];                    
                        Dimension dim = comp.getPreferredSize();
                        if (found) x -= getSpacing();

                        x -= (dim.width + padding.left + padding.right); 
                        found = true;
                    }  
                    
                    if (x < margin.left) x = margin.left;
                }
                
                found = false; 
                for (int i=0; i<comps.length; i++) 
                {
                    if (!comps[i].isVisible()) continue;

                    Component comp = comps[i];                    
                    Dimension dim = comp.getPreferredSize();
                    int cwidth = dim.width + padding.left + padding.right;
                    if (found) x += getSpacing();
                    
                    if (x+cwidth > endPoint) 
                    {
                        int nw = w - x; 
                        if (nw < 18)
                            comp.setBounds(0, 0, 0, 0);
                        else 
                            comp.setBounds(x, y, nw, h);
                    }
                    else 
                        comp.setBounds(x, y, cwidth, h);
                    
                    x += cwidth; 
                    found = true;
                }
            }
        }
    }
    //</editor-fold>    
        
    // <editor-fold defaultstate="collapsed" desc=" VerticalLayout (Class) ">
    private class VerticalLayout implements LayoutManager 
    {
        public void addLayoutComponent(String name, Component comp) {;}
        public void removeLayoutComponent(Component comp) {;}
        
        public Dimension getLayoutSize(Container parent) 
        {
            synchronized (parent.getTreeLock()) 
            {
                int w=0, h=0;
                boolean found = false;
                Component[] comps = parent.getComponents();
                for (int i=0; i<comps.length; i++) 
                {
                    if (!comps[i].isVisible()) continue;
                    if (found) h += getSpacing();

                    Dimension dim = comps[i].getPreferredSize();
                    h += (dim.height + padding.top + padding.bottom);
                    w = Math.max(w, dim.width + padding.left + padding.right);
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
                int endPoint = parent.getHeight() - margin.bottom;                
                
                boolean found = false;                
                Component[] comps = parent.getComponents();
                
                if (UIConstants.BOTTOM.equals(getAlignment()))
                {
                    y = endPoint;
                    for (int i=comps.length; i>0; i--) 
                    {
                        if (!comps[i-1].isVisible()) continue;

                        Component comp = comps[i-1];                    
                        Dimension dim = comp.getPreferredSize();
                        if (found) y -= getSpacing();

                        y -= (dim.height + padding.top + padding.bottom); 
                        found = true;
                    }  
                    
                    if (y < margin.top) y = margin.top;
                }
                
                found = false; 
                for (int i=0; i<comps.length; i++) 
                {
                    if (!comps[i].isVisible()) continue;

                    Component comp = comps[i];                    
                    Dimension dim = comp.getPreferredSize();
                    int cheight = dim.height + padding.top + padding.bottom; 
                    if (found) y += getSpacing();
                    
                    if (y+cheight > endPoint) 
                    {
                        int nh = h - y;
                        if (nh < 18)
                            comp.setBounds(0, 0, 0, 0); 
                        else 
                            comp.setBounds(x, y, w, nh); 
                    }
                    else 
                        comp.setBounds(x, y, w, cheight);
                    
                    y += cheight; 
                    found = true;
                }
            }
        }
    }
    //</editor-fold>     
        
    // <editor-fold defaultstate="collapsed" desc=" FlowLayout (Class) ">
    
    private class FlowLayout implements LayoutManager 
    {
        private int CELL_SIZE = 100;
        
        public void addLayoutComponent(String name, Component comp) {;}
        public void removeLayoutComponent(Component comp) {;}
        
        public Dimension getLayoutSize(Container parent) 
        {
            synchronized (parent.getTreeLock()) 
            {
                int w = CELL_SIZE + padding.left + padding.right;
                int h = CELL_SIZE + padding.top + padding.bottom;
                Insets margin = parent.getInsets(); 
                return new Dimension(w+margin.left+margin.right, w+margin.top+margin.bottom);
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
                int x = margin.left, y=margin.top, pw=parent.getWidth(), ph=parent.getHeight();
                int w = pw - (margin.left + margin.right);
                int h = ph - (margin.top + margin.bottom);

                Component[] comps = parent.getComponents();
                if (comps.length == 0) return;

                int colWidth = CELL_SIZE + padding.left + padding.right;
                int colHeight = CELL_SIZE + padding.top + padding.bottom;
                int colCount = w / colWidth; 
                
                for (int i=0; i<comps.length; i++)
                {
                    int colIndex = 0, rowHeight = 0;
                    boolean found = false;
                    for (int c=i; c<comps.length; c++) 
                    {
                        if (colIndex >= colCount) break;
                        
                        i = c;                        
                        if (!comps[c].isVisible()) continue;
                        
                        Component comp = comps[c];
                        Dimension dim = comp.getPreferredSize();
                        if (found) x += getSpacing();

                        if (dim.width > colWidth)
                            colHeight = (((colWidth/dim.width)*dim.height)/3)+dim.height;
                        else
                            colHeight = dim.height;
                        
                        comp.setBounds(x, y, colWidth, colHeight); 
                        rowHeight = Math.max(rowHeight, colHeight);                         
                        x += colWidth;
                        colIndex += 1; 
                        found = true;                          
                    }
                    
                    x = margin.left;
                    y += rowHeight;
                    if (found) y += getSpacing();                    
                }
            }
        }        
        
        public void layoutContainer2(Container parent) 
        {
            synchronized (parent.getTreeLock()) 
            {
                Insets margin = parent.getInsets();                
                int x = margin.left, y=margin.top, pw=parent.getWidth(), ph=parent.getHeight();
                int w = pw - (margin.left + margin.right);
                int h = ph - (margin.top + margin.bottom);

                Component[] comps = parent.getComponents();
                if (comps.length == 0) return;

                int colWidth = CELL_SIZE + padding.left + padding.right;
                int colHeight = CELL_SIZE + padding.top + padding.bottom;
                int colCount = w / colWidth; 
                
                for (int i=0; i<comps.length; i++)
                {
                    int colIndex = 0;
                    boolean found = false;
                    for (int c=i; c<comps.length; c++) 
                    {
                        if (colIndex >= colCount) break;
                        
                        i = c;                        
                        if (!comps[c].isVisible()) continue;
                        
                        Component comp = comps[c];
                        Dimension dim = comp.getPreferredSize();
                        if (found) x += getSpacing();

                        comp.setBounds(x, y, colWidth, colHeight); 
                        x += colWidth;
                        colIndex += 1; 
                        found = true;                          
                    }
                    
                    x = margin.left;
                    y += colHeight;
                    if (found) y += getSpacing();                    
                }
            }
        }
    }
    
    //</editor-fold>        
    
    // <editor-fold defaultstate="collapsed" desc=" XFlowLayout (Class) ">

    private class XFlowLayout implements LayoutManager 
    {
        private int CELL_SIZE = 100;

        public void addLayoutComponent(String name, Component comp) {;}
        public void removeLayoutComponent(Component comp) {;}

        public Dimension getLayoutSize(Container parent) 
        {
            synchronized (parent.getTreeLock()) 
            {
                int w = (CELL_SIZE+20) + padding.left + padding.right;
                int h = (CELL_SIZE-20) + padding.top + padding.bottom;
                Insets margin = parent.getInsets(); 
                return new Dimension(w+margin.left+margin.right, w+margin.top+margin.bottom);
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
                int x = margin.left, y=margin.top, pw=parent.getWidth(), ph=parent.getHeight();
                int w = pw - (margin.left + margin.right);
                int h = ph - (margin.top + margin.bottom);

                Component[] comps = parent.getComponents();
                if (comps.length == 0) return;

                int colWidth = (CELL_SIZE+20) + padding.left + padding.right;
                int colHeight = (CELL_SIZE-20) + padding.top + padding.bottom;
                int colCount = w / colWidth; 

                for (int i=0; i<comps.length; i++)
                {
                    int colIndex = 0;
                    boolean found = false;
                    for (int c=i; c<comps.length; c++) 
                    {
                        if (colIndex >= colCount) break;

                        i = c;                        
                        if (!comps[c].isVisible()) continue;

                        Component comp = comps[c];
                        Dimension dim = comp.getPreferredSize();
                        if (found) x += getSpacing();

                        comp.setBounds(x, y, colWidth, colHeight); 
                        x += colWidth;
                        colIndex += 1; 
                        found = true;                          
                    }

                    x = margin.left;
                    y += colHeight;
                    if (found) y += getSpacing();                    
                }
            }
        }
    }

    //</editor-fold>        
}
