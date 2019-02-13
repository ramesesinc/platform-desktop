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
import java.util.ArrayList;

/**
 *
 * @author wflores
 */
public class CenterLayout implements LayoutManager {

    public void addLayoutComponent(String name, Component comp) {
    }
    public void removeLayoutComponent(Component comp) {
    }

    public Dimension preferredLayoutSize(Container parent) {
        synchronized (parent.getTreeLock()) {
            return getPreferredSize(parent);
        }
    }

    public Dimension minimumLayoutSize(Container parent) {
        synchronized (parent.getTreeLock()) {
            return getPreferredSize(parent);
        }
    }
    
    private Dimension getPreferredSize( Container parent ) {
        int w=0, h=0;
        Component[] comps = getVisibleComponents(parent);
        if ( comps.length > 0) {
            Dimension dim = comps[0].getPreferredSize(); 
            w = dim.width;
            h = dim.height;
        }
        Insets margin = parent.getInsets(); 
        w += (margin.left + margin.right); 
        h += (margin.top + margin.bottom); 
        return new Dimension( w, h ); 
    }
    
    private Component[] getVisibleComponents(Container parent) {
        ArrayList<Component> list = new ArrayList();
        Component[] comps = parent.getComponents(); 
        for (int i=0; i<comps.length; i++) {
            if ( comps[i].isVisible() ) {
                list.add( comps[i]); 
            } 
        }
        return list.toArray(new Component[]{});  
    }

    public void layoutContainer(Container parent) {
        synchronized( parent.getTreeLock() ) {
            Component[] comps = getVisibleComponents(parent); 
            if ( comps.length == 0 ) return; 
            
            Insets margin = parent.getInsets(); 
            int pw = parent.getWidth();
            int ph = parent.getHeight(); 
            int w = pw - (margin.left + margin.right);
            int h = ph - (margin.top + margin.bottom);
            
            for (int i=0; i<comps.length; i++) {
                comps[i].setBounds(0,0,0,0);
            }
            
            Component comp = comps[0]; 
            Dimension dim = comp.getPreferredSize();
            int cx = Math.max((w/2)-(dim.width/2), 0);
            int cy = Math.max((h/2)-(dim.height/2), 0);
            
            if ( cx < margin.left ) cx = margin.left; 
            if ( cy < margin.top ) cy = margin.top; 
            comp.setBounds(cx, cy, dim.width, dim.height);
        }
    }
}
