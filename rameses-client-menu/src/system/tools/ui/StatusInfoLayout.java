/*
 * StatusInfoLayout.java
 *
 * Created on October 25, 2013, 9:48 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package system.tools.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;

/**
 *
 * @author wflores
 */
class StatusInfoLayout implements LayoutManager 
{
    private int gap;
    
    public StatusInfoLayout() {
        this(3);
    }
    
    public StatusInfoLayout(int gap) {
        this.gap = gap;
    }

    public void addLayoutComponent(String name, Component comp) {}
    public void removeLayoutComponent(Component comp) {}

    public Dimension preferredLayoutSize(Container parent) {
        return getLayoutSize(parent);
    }

    public Dimension minimumLayoutSize(Container parent) {
        return getLayoutSize(parent);
    }
    
    private Dimension getLayoutSize(Container parent) {
        synchronized (parent.getTreeLock()) {
            int width=0, height=0;
            
            Component[] comps = parent.getComponents();
            for (int i=0; i<comps.length; i++) {
                Component c = comps[i];
                if (!c.isVisible()) continue;
                
                Dimension dim = c.getPreferredSize();
                width += dim.width;
                height = Math.max(dim.height, height); 
            }
            
            Insets margin = parent.getInsets(); 
            width += (margin.left + margin.right);
            height += (margin.top + margin.bottom); 
            return new Dimension(width, height); 
        }
    }
    

    public void layoutContainer(Container parent) {
        synchronized (parent.getTreeLock()) {
            Insets margin = parent.getInsets();             
            int pw = parent.getWidth();
            int ph = parent.getHeight();
            int x = margin.left;
            int y = margin.top;
            int h = ph - (margin.top + margin.bottom);
            int w = pw - margin.right;
            
            int visibleCount = 0;
            Component[] comps = parent.getComponents();
            for (int i=0; i<comps.length; i++) {
                Component c = comps[i];
                if (!c.isVisible()) continue;
                if (visibleCount > 0) x += gap;
                
                Dimension dim = c.getPreferredSize();       
                int rw = dim.width;
                if (i+1 >= comps.length) {
                    rw = Math.max(w-x, 0);  
                    rw = Math.max(rw, 60);
                }
                
                c.setBounds(x, y, rw, h); 
                x += dim.width; 
                visibleCount++;
            }
        }        
    }
    
}
