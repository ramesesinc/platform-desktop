/*
 * OSGlassPane.java
 *
 * Created on October 30, 2013, 10:48 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.platform;

import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets; 
import java.awt.KeyboardFocusManager;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;

/**
 *
 * @author wflores
 */
public class OSGlassPane extends JPanel 
{
    public OSGlassPane() {
        initComponent();
    }

    private void initComponent() {
        setOpaque(false);
        setLayout(new Layout()); 
        addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                e.consume();
            }
            public void mouseEntered(MouseEvent e) {
            }
            public void mouseExited(MouseEvent e) {
            }
            public void mousePressed(MouseEvent e) {
                e.consume();
            }
            public void mouseReleased(MouseEvent e) {
                e.consume();
            }
        });
        addMouseMotionListener(new MouseMotionListener() {
            public void mouseDragged(MouseEvent e) {
            }
            public void mouseMoved(MouseEvent e) {
            }
        });
        addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent e) { 
                e.consume(); 
            }
            public void keyReleased(KeyEvent e) {
                e.consume();
            }
            public void keyTyped(KeyEvent e) {
            }
        });        
    }
    
    protected void paintComponent(Graphics g) {
        // enables anti-aliasing
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // gets the current clipping area
        Rectangle clip = g.getClipBounds();
        
        // sets a 65% translucent composite
        AlphaComposite alpha = AlphaComposite.SrcOver.derive(0.85f);
        Composite oldComposite = g2.getComposite();
        g2.setComposite(alpha);
        
        // fills the background
        g2.setColor(getBackground());
        g2.fillRect(clip.x, clip.y, clip.width, clip.height);                
        g2.setComposite(oldComposite);
    }    

    public void setVisible(boolean visible) {
        setFocusable(visible);         
        super.setVisible(visible);
        if (visible) {
            requestFocusInWindow();            
            Component comp = getContent();
            if (!(comp instanceof JInternalFrame)) {
                transferFocus(); 
            } 
            
            if (comp instanceof Container) {
                Container con = (Container) comp;
                con.setFocusCycleRoot(true); 
            } else {
                setFocusCycleRoot(true); 
            }
        } 
    } 
    
    public Component getContent() {
        Component[] comps = getComponents(); 
        if (comps.length > 0) return comps[0]; 
        
        return null; 
    }
    
    // <editor-fold defaultstate="collapsed" desc=" Layout ">
    
    private class Layout implements LayoutManager
    {
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
                int w=0, h=0;
                
                Component comp = getFirstVisible(parent); 
                if (comp != null) {
                    Dimension dim = comp.getPreferredSize();
                    w = dim.width;
                    h = dim.height; 
                }
                
                Insets margin = parent.getInsets();
                w += (margin.left + margin.right);
                h += (margin.top + margin.bottom); 
                return new Dimension(w, h); 
            }
        }

        private Component getFirstVisible(Container parent) {
            Component[] comps = parent.getComponents();
            for (int i=0; i<comps.length; i++) {
                if (comps[i].isVisible()) return comps[i]; 
            }
            return null; 
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
                
                Component comp = getFirstVisible(parent); 
                if (comp != null) {
                    Dimension dim = comp.getPreferredSize();
                    if (dim.width < pw) x = ((w - dim.width) / 2) + margin.left;
                    if (dim.height < ph) y = ((h - dim.height) / 2) + margin.top;
                    x = Math.max(x, margin.left);
                    y = Math.max(y, margin.top);
                    comp.setBounds(x, y, dim.width, dim.height); 
                }
            } 
        }        
    }
    
    // </editor-fold>
}
