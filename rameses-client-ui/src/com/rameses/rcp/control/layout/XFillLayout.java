/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.control.layout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;

/**
 *
 * @author wflores 
 */
public class XFillLayout implements LayoutManager {
            
    public void addLayoutComponent(String name, Component comp) {}
    public void removeLayoutComponent(Component comp) {}

    public Dimension preferredLayoutSize(Container parent) {
        return getLayoutSize( parent ); 
    }

    public Dimension minimumLayoutSize(Container parent) {
        return getLayoutSize( parent ); 
    }

    private Component getVisibleComponent( Container parent ) {
        Component[] comps = parent.getComponents(); 
        for (int i=0; i<comps.length; i++ ) {
            Component c = comps[i];
            if ( c != null && c.isVisible() ) {
                return c; 
            } 
        } 
        return null; 
    }

    private Dimension getLayoutSize( Container parent ) {
        synchronized (parent.getTreeLock()) {
            int w=0, h=0;
            Component c = getVisibleComponent( parent );
            if ( c != null ) {
                Dimension dim = c.getPreferredSize(); 
                h = dim.height; 
                w = dim.width; 
            } 
            Insets margin = parent.getInsets();
            w += (margin.left + margin.right);
            h += (margin.top + margin.bottom);
            return new Dimension(w, h); 
        }
    }
    
    public void layoutContainer(Container parent) { 
        synchronized (parent.getTreeLock()) {
            Insets margin = parent.getInsets();
            int pw = parent.getWidth();
            int ph = parent.getHeight();
            int x = margin.left;
            int y = margin.top;
            int w = pw - (margin.left + margin.right);
            int h = ph - (margin.top + margin.bottom);

            Component c = getVisibleComponent( parent );
            if ( c != null ) { 
                c.setBounds( x, y, w, h ); 
            } 
        }
    }
} 
