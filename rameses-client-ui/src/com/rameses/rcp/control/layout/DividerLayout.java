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
import javax.swing.SwingConstants;

/**
 *
 * @author wflores
 */
public class DividerLayout implements LayoutManager {
    
    private int orientation; 
    private int spacing;
    
    private int minWidth;
    private int minHeight;
    
    public DividerLayout() {
        orientation = SwingConstants.VERTICAL; 
        spacing = 5; 
        minWidth = 100;  
        minHeight = 60; 
    } 
    
    public int getOrientation() { return orientation; } 
    public void setOrientation( int orientation ) {
        this.orientation = orientation;
    }
    
    public int getSpacing() { return spacing; } 
    public void setSpacing( int spacing ) {
        this.spacing = spacing;
    }
    
    public int getMinWidth() { return minWidth; } 
    public void setMinWidth( int minWidth ) {
        this.minWidth = minWidth;
    }    

    public int getMinHeight() { return minHeight; } 
    public void setMinHeight( int minHeight ) {
        this.minHeight = minHeight;
    }    
    
    public void addLayoutComponent(String name, Component comp) {
    }
    public void removeLayoutComponent(Component comp) {
    }

    public Dimension preferredLayoutSize(Container parent) { 
        synchronized( parent.getTreeLock()) {
            Dimension dim = getMinLayoutSize( parent ); 
            Insets margin = parent.getInsets(); 
            int w = (margin.left + margin.right) + dim.width; 
            int h = (margin.top + margin.bottom) + dim.height;
            return new Dimension(w, h); 
        }
    }

    public Dimension minimumLayoutSize(Container parent) {
        synchronized( parent.getTreeLock()) {
            Dimension dim = getMinLayoutSize( parent ); 
            Insets margin = parent.getInsets(); 
            int w = (margin.left + margin.right) + dim.width; 
            int h = (margin.top + margin.bottom) + dim.height;
            return new Dimension(w, h); 
        }
    }    
    
    private Dimension getMinLayoutSize(Container parent) {
        Component[] comps = getVisibleComponents(parent); 
        Component comp1 = (comps.length > 0 ? comps[0] : null);
        Component comp2 = (comps.length > 1 ? comps[1] : null);
        int w1 = (comp1 == null ? 0 : comp1.getMinimumSize().width); 
        int h1 = (comp1 == null ? 0 : comp1.getMinimumSize().height); 
        int w2 = (comp2 == null ? 0 : comp2.getMinimumSize().width); 
        int h2 = (comp2 == null ? 0 : comp2.getMinimumSize().height); 
        int minW = Math.max(getMinWidth(), 0);
        int minH = Math.max(getMinHeight(), 0);
        int w = Math.max(Math.max(w1, w2), minW);
        int h = Math.max(Math.max(h1, h2), minH);
        return new Dimension(w, h); 
    }

    private Component[] getVisibleComponents( Container parent ) { 
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
        synchronized( parent.getTreeLock()) {
            Component[] comps = getVisibleComponents(parent); 
            Component comp1 = (comps.length > 0 ? comps[0] : null);
            Component comp2 = (comps.length > 1 ? comps[1] : null);
            if ( getOrientation() == SwingConstants.VERTICAL ) {
                layoutVertical( parent, comp1, comp2 ); 
            } else if ( getOrientation() == SwingConstants.HORIZONTAL ) {
                layoutHorizontal( parent, comp1, comp2 ); 
            } else {
                layoutVertical( parent, comp1, comp2 ); 
            }
        }        
    }
    
    private void layoutVertical( Container parent, Component comp1, Component comp2 ) {
        Insets margin = parent.getInsets(); 
        int minW = Math.max(getMinWidth(), 0);
        int minH = Math.max(getMinHeight(), 0);
        int pw = Math.max(parent.getWidth(), minW); 
        int ph = Math.max(parent.getHeight(), minH); 
        int x = margin.left; 
        int y = margin.top; 
        int w = pw - (margin.left + margin.right); 
        int h = ph - (margin.top + margin.bottom);
        
        int compH = h; 
        if ( comp2 != null ) {
            int spacing = getSpacing();
            int totalH = Math.max(h - spacing, 0);  
            compH = Math.max(totalH / 2, 0);
            y = (ph - margin.bottom) - compH;
            comp2.setBounds(x, y, w, compH);
            y = margin.top;
        }
        if ( comp1 != null ) {
            comp1.setBounds(x, y, w, compH);
        }
    }
    
    private void layoutHorizontal( Container parent, Component comp1, Component comp2 ) {
        Insets margin = parent.getInsets(); 
        int minW = Math.max(getMinWidth(), 0);
        int minH = Math.max(getMinHeight(), 0);
        int pw = Math.max(parent.getWidth(), minW); 
        int ph = Math.max(parent.getHeight(), minH); 
        int x = margin.left; 
        int y = margin.top; 
        int w = pw - (margin.left + margin.right); 
        int h = ph - (margin.top + margin.bottom);
        
        int compW = w; 
        if ( comp2 != null ) {
            int spacing = getSpacing();
            int totalW = Math.max(w - spacing, 0);  
            compW = Math.max(totalW / 2, 0);
            x = (pw - margin.right) - compW;
            comp2.setBounds(x, y, compW, h);
            x = margin.left; 
        }
        if ( comp1 != null ) {
            comp1.setBounds(x, y, compW, h);
        }
    }    
}
