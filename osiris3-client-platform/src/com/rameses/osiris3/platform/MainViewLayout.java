/*
 * ExplorerViewLayout.java
 *
 * Created on October 24, 2013, 12:36 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.platform;


import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

/**
 *
 * @author wflores
 */
class MainViewLayout implements LayoutManager, PropertyChangeListener
{
    public final static String EXPLORER_SECTION = "EXPLORER";
    public final static String CONTENT_SECTION = "CONTENT";
    
    private MainViewLayout.Provider provider;    
    private LayoutManager layout;
    private int dividerSize;
    private int dividerLocation;
    private int locationIndex;

    private Component explorer;
    private Component content;    
    private Component divider;
    
    private Point sourcePoint;
    private Point targetPoint;
    private Rectangle viewRect; 
    
    private HorizontalMouseSupport mouseSupport;
        
    public MainViewLayout(MainViewLayout.Provider provider)
    {
        this.provider = provider;
        layout = new HorizontalLayout(); 
        setDividerLocation(100);
        setDividerSize(5); 
    }
        
    public int getDividerSize() { return dividerSize; } 
    public void setDividerSize(int dividerSize) {
        this.dividerSize = dividerSize; 
    }
    
    public int getDividerLocation() { return dividerLocation; } 
    public void setDividerLocation(int dividerLocation) {
        this.dividerLocation = dividerLocation;
        this.locationIndex = dividerLocation;         
    }
    
    int getLocationIndex() { return locationIndex; } 
    void setLocationIndex(int x) { this.locationIndex = x; }
    
    private Component getDivider() {
        if (divider == null) {
            JLabel lbl = new JLabel();
            lbl.setName("splitview.divider"); 
            lbl.setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR)); 

            mouseSupport = new HorizontalMouseSupport();
            mouseSupport.setDivider(lbl);                
            divider = lbl;
        } 
        return divider;
    } 
    
    public Component getLayoutComponent(String name) {
        if (name == null) return null;        
        if (EXPLORER_SECTION.equalsIgnoreCase(name)) {
            return explorer; 
        } else if (CONTENT_SECTION.equalsIgnoreCase(name)) {
            return content; 
        } else {
            return null; 
        }
    }
        
    public void addLayoutComponent(String name, Component comp) {
        if (comp == null) return;
        
        synchronized (comp.getTreeLock()) {
            if (name == null) name = CONTENT_SECTION;

            if (EXPLORER_SECTION.equalsIgnoreCase(name)) {
                explorer = comp;
            } else if (CONTENT_SECTION.equalsIgnoreCase(name)) {
                content = comp;
            } 
        }     
    }
    
    public void removeLayoutComponent(Component comp) {
        if (comp == null) return;
        
        synchronized (comp.getTreeLock()) {
            if (explorer != null && explorer.equals(comp)) {
                explorer = null;
            } else if (content != null && content.equals(comp)) {
                content = null;
            } 
        } 
    } 

    public Dimension preferredLayoutSize(Container parent) {
        return layout.preferredLayoutSize(parent);
    }

    public Dimension minimumLayoutSize(Container parent) {
        return layout.minimumLayoutSize(parent); 
    }
    
    public void layoutContainer(Container parent) {
        layout.layoutContainer(parent);
    }     

    public void propertyChange(PropertyChangeEvent evt) {
        String name = evt.getPropertyName();
        if ("toggleLeftView".equals(name)) {
            if (mouseSupport != null) mouseSupport.toggleLeftView(); 
        }
    }

    // <editor-fold defaultstate="collapsed" desc=" Provider ">
    
    public static interface Provider 
    {        
        void revalidate();
        void repaint();
        void paintDividerHandle(Rectangle viewRect, Rectangle divRect, Point targetPoint);         
    }
    
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" HorizontalMouseSupport ">
    
    private class HorizontalMouseSupport implements MouseListener, MouseMotionListener
    {
        MainViewLayout root = MainViewLayout.this;  
        Component divider;
        
        void setDivider(Component divider) {
            this.divider = divider;
            divider.addMouseListener(this);
            divider.addMouseMotionListener(this); 
        }
        
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() != 2) {
                if (!SwingUtilities.isLeftMouseButton(e)) return; 
                
                EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        toggleLeftView(); 
                    }
                });                
            }
        }
        
        public void mousePressed(MouseEvent e) {
            sourcePoint = e.getPoint(); 
        }
        
        public void mouseReleased(MouseEvent e) {
            if (targetPoint != null) {
                Rectangle rect = divider.getBounds();
                int nx = rect.x + targetPoint.x; 
                if (nx < 0) targetPoint.x = locationIndex * -1;
                
                locationIndex = nx;
            }
            sourcePoint = null;
            targetPoint = null;
            if (locationIndex > 0 && root.explorer != null && !root.explorer.isVisible()) {
                root.explorer.setVisible(true); 
            }
            provider.revalidate();
            provider.repaint(); 
        }
        
        public void mouseEntered(MouseEvent e) {}
        public void mouseExited(MouseEvent e) {}

        public void mouseMoved(MouseEvent e) {}        
        public void mouseDragged(MouseEvent e) {
            targetPoint = e.getPoint(); 
            Rectangle divRect = divider.getBounds();
            int nx = divRect.x + targetPoint.x; 
            if (nx < 10) { 
                targetPoint.x = (divRect.x * -1)+10; 
            } else {
                int w = viewRect.width-divRect.x;
                int limit = w - getDividerSize() - 10;
                if (targetPoint.x > limit) targetPoint.x = limit;
            }
            
            provider.paintDividerHandle(viewRect, divRect, targetPoint); 
        } 
        
        private int prevLocationIndex;
        private void toggleLeftView() {
            if (root.getLocationIndex() == 0) {
                root.setLocationIndex(prevLocationIndex); 
                if (root.explorer != null) root.explorer.setVisible(true); 
            } else { 
                prevLocationIndex = root.getLocationIndex(); 
                root.setLocationIndex(0); 
                if (root.explorer != null) root.explorer.setVisible(false);
            }             
            provider.revalidate();
            provider.repaint(); 
        }         
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" HorizontalLayout (Class) ">

    private class HorizontalLayout implements LayoutManager 
    {
        MainViewLayout root = MainViewLayout.this; 
        
        public void addLayoutComponent(String name, Component comp) {;}
        public void removeLayoutComponent(Component comp) {;}
        
        public Dimension preferredLayoutSize(Container parent) {
            return getLayoutSize(parent);
        }
        
        public Dimension minimumLayoutSize(Container parent) {
            return getLayoutSize(parent);
        }
        
        public Dimension getLayoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                int w=0, h=0;

                w += getDividerSize();
                h += getDividerSize();

                Insets margin = parent.getInsets();
                w += (margin.left + margin.right);
                h += (margin.top + margin.bottom);
                return new Dimension(w,h); 
            }
        }
        
        public void layoutContainer(Container parent) {
            synchronized (parent.getTreeLock()) {
                Component divider = getDivider();
                parent.remove(divider);
                parent.add(divider); 

                Insets margin = parent.getInsets();
                int pw = parent.getWidth();
                int ph = parent.getHeight();
                int x = margin.left, y = margin.top; 
                int w = pw - (margin.left + margin.right);
                int h = ph - (margin.top + margin.bottom);
                viewRect = new Rectangle(x, y, w, h); 

                if (locationIndex < 0) 
                    locationIndex = 0; 
                else if (locationIndex >= (pw-margin.right)) 
                    locationIndex = (pw-margin.right)-getDividerSize();

                int divX = locationIndex;     
                if (root.explorer == null || !root.explorer.isVisible()) {
                    divX = 0;
                } else {
                    Dimension dim = root.explorer.getPreferredSize();
                    root.explorer.setBounds(x, y, divX, h);
                }
                
                if (root.explorer != null) { 
                    x += divX; 
                    divider.setBounds(x, y, getDividerSize(), h); 
                    x += getDividerSize(); 
                } 

                int rw = (pw-margin.right)-x;
                if (rw < 0) rw = 0;
                if (root.content != null) {
                    root.content.setBounds(x, y, rw, h); 
                }
            }
        }
    }

    // </editor-fold>    
    
}
