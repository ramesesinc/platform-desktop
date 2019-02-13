/*
 * XLayout.java
 *
 * Created on December 12, 2013, 4:23 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control.layout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingConstants;

/** 
 *
 * @author wflores
 */
public class XLayout implements LayoutManager
{
    private int alignment = SwingConstants.LEFT;
    private int spacing = 2; 
    
    public int getSpacing() { return spacing; }
    public void setSpacing(int spacing) {
        this.spacing = spacing;
    }
    
    public int getAlignment() { return alignment; } 
    public void setAlignment( int alignment ) {
        this.alignment = alignment; 
    }
    
    public void addLayoutComponent(String name, Component comp) {}
    public void removeLayoutComponent(Component comp) {}

    public Dimension preferredLayoutSize(Container parent) {
        return getLayoutSize(parent);
    }

    public Dimension minimumLayoutSize(Container parent) {
        return getLayoutSize(parent);
    }
    
    private Component[] getVisibleComponents(Container parent) {
        List<Component> list = new ArrayList();
        Component[] comps = parent.getComponents(); 
        for (int i=0; i<comps.length; i++) {
            if ( comps[i].isVisible()) {
                list.add( comps[i]); 
            } 
        } 
        return list.toArray(new Component[]{});
    }
    
    private Dimension getLayoutSize(Container parent) {
        synchronized (parent.getTreeLock()) { 
            int w=0, h=0;
            Component[] comps = getVisibleComponents( parent ); 
            for (int i=0; i<comps.length; i++) {
                if ( i > 0 ) w += getSpacing();
                    
                Dimension dim = comps[i].getPreferredSize();
                h = Math.max(dim.height, h); 
                w += dim.width;
            }
            
            Insets margin = parent.getInsets();
            w += (margin.left + margin.right);
            h += (margin.top + margin.bottom);
            return new Dimension(w, h); 
        }
    }

    public void layoutContainer(Container parent) {
        synchronized (parent.getTreeLock()) { 
            int alignment = getAlignment(); 
            if ( alignment == SwingConstants.RIGHT ) {
                layoutRight( parent );
            } else if ( alignment == SwingConstants.CENTER ) {
                layoutLeft( parent ); 
            } else {
                layoutLeft( parent ); 
            }
            
            Insets margin = parent.getInsets();
            int pw = parent.getWidth();
            int ph = parent.getHeight();
            int x = margin.left;
            int y = margin.top;
            int h = ph - (margin.top + margin.bottom);
            
            boolean has_visible_components = false; 
            Component[] comps = parent.getComponents(); 
            for (int i=0; i<comps.length; i++) {
                Component c = comps[i]; 
                if (!c.isVisible()) continue;
                if (has_visible_components) x += getSpacing();
                    
                Dimension dim = c.getPreferredSize();
                c.setBounds(x, y, dim.width, h); 
                x += dim.width; 
                has_visible_components = true; 
            }
        }        
    }
    
    public void layoutLeft(Container parent) {
        Insets margin = parent.getInsets();
        int pw = parent.getWidth();
        int ph = parent.getHeight();
        int x = margin.left;
        int y = margin.top;
        int h = ph - (margin.top + margin.bottom);

        int spacing = getSpacing();
        Component[] comps = getVisibleComponents(parent); 
        for (int i=0; i<comps.length; i++) { 
            if ( i>0 ) x += spacing;

            Dimension dim = comps[i].getPreferredSize();
            comps[i].setBounds(x, y, dim.width, h); 
            x += dim.width; 
        }
    }
    public void layoutRight(Container parent) {
        Insets margin = parent.getInsets();
        int pw = parent.getWidth();
        int ph = parent.getHeight();
        int x = margin.left;
        int y = margin.top;
        int h = ph - (margin.top + margin.bottom);
        int w = pw - (margin.left + margin.right);

        int spacing = getSpacing();         
        Component[] comps = getVisibleComponents(parent); 
        for (int i=0; i<comps.length; i++) { 
            if ( i>0 && spacing>0 ) { 
                w -= spacing;
            } 
            Component c = comps[ comps.length-1-i ];
            Dimension dim = c.getPreferredSize();
            comps[i].setBounds(w-dim.width, y, dim.width, h); 
            w -= dim.width; 
        }
    }
    public void layoutCenter(Container parent) {
        Insets margin = parent.getInsets();
        int pw = parent.getWidth();
        int ph = parent.getHeight();
        int x = margin.left;
        int y = margin.top;
        int h = ph - (margin.top + margin.bottom);
        int w = pw - (margin.left + margin.right); 
        
        Component[] comps = getVisibleComponents(parent); 
        int totalWidth = getWidth( comps ); 
        int spacing = getSpacing(); 
        for (int i=0; i<comps.length; i++) { 
            if ( i>0 && spacing>0 ) { 
                w -= spacing;
            } 
            Component c = comps[ comps.length-1-i ];
            Dimension dim = c.getPreferredSize();
            comps[i].setBounds(w-dim.width, y, dim.width, h); 
            w -= dim.width; 
        }
    } 
    private int getWidth( Component[] comps ) { 
        int width = 0;
        int spacing = getSpacing();         
        for (int i=0; i<comps.length; i++) { 
            if ( i>0 && spacing>0 ) { 
                width += spacing;
            } 
            Dimension dim = comps[i].getPreferredSize();
            width += dim.width; 
        }
        return width; 
    }
}
