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
public class XLayeredLayout implements LayoutManager {
    
    public void addLayoutComponent(String name, Component comp) {
    }

    public void removeLayoutComponent(Component comp) {
    }

    public Dimension minimumLayoutSize(Container parent) {
        synchronized( parent.getTreeLock() ) {
            Insets margin = parent.getInsets();
            int w = (margin.left + margin.right);
            int h = (margin.top + margin.bottom);
            return new Dimension( w, h ); 
        } 
    } 

    public Dimension preferredLayoutSize(Container parent) {
        synchronized( parent.getTreeLock() ) {
            int w=0; int h=0; 
            Component[] comps = getVisibleComponents(parent);
            for (int i=0; i<comps.length; i++) {
                Dimension dim = comps[i].getPreferredSize(); 
                w = Math.max(w, dim.width); 
                h = Math.max(h, dim.height); 
            }
            Insets margin = parent.getInsets();
            w += (margin.left + margin.right);
            h += (margin.top + margin.bottom);            
            return new Dimension( w, h ); 
        }
    }
    
    private Component[] getVisibleComponents(Container parent) {
        ArrayList<Component> list = new ArrayList();
        Component[] comps = parent.getComponents(); 
        for (int i=0; i<comps.length; i++) {
            if ( comps[i].isVisible()) {
                list.add( comps[i]); 
            } 
        } 
        return list.toArray(new Component[]{});
    }
    
    public void layoutContainer(Container parent) {
        synchronized( parent.getTreeLock() ) {
            Insets margin = parent.getInsets();
            int pw = parent.getWidth(); 
            int ph = parent.getHeight(); 
            int x = margin.left;
            int y = margin.top;
            int w = pw - (margin.left + margin.right);
            int h = ph - (margin.top + margin.bottom);

            Component[] comps = getVisibleComponents(parent);
            for (int i=0; i<comps.length; i++) {
                Dimension dim = comps[i].getPreferredSize(); 
                comps[i].setBounds(x, y, w, h); 
            } 
        }        
    }
}
