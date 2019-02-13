/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.control.layout;

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
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

/**
 *
 * @author wflores 
 */
public class SplitterLayout implements LayoutManager {
    
    public final static String VW_SIDE    = "sideview"; 
    public final static String VW_CONTENT = "contentview"; 
    
    private LayoutManager layout;
    
    private int dividerSize; 
    private int dividerLocation;
    private int locationIndex; 
    
    private Component vwside;
    private Component vwcontent;    
    private Component divider;
    
    private Point sourcePoint;
    private Point targetPoint;
    private Rectangle viewRect; 
    
    private HorizontalMouseSupport mouseSupport;    
    
    public SplitterLayout() {
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
        setLocationIndex( this.dividerLocation ); 
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

    public void addLayoutComponent(String name, Component comp) { 
        if ( name == null || comp == null) {
            //do nothing 
        } else if ( VW_SIDE.equals(name)) {
            vwside = comp;
        } else if ( VW_CONTENT.equals(name)) {
            vwcontent = comp;
        } 
    }

    public void removeLayoutComponent(Component comp) {
        if ( comp == null) {
            //do nothing 
        } else if ( vwside != null && vwside.equals(comp)) {
            vwside = null; 
        } else if ( vwcontent != null && vwcontent.equals(comp)) {
            vwcontent = null;
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

    // <editor-fold defaultstate="collapsed" desc=" Canvas ">
    public static interface Canvas { 
        void paintDividerHandle(Rectangle viewRect, Rectangle dividerRect, Point targetPoint); 
        void revalidate();
        void repaint();
    } 
    // </editor-fold> 
    
    // <editor-fold defaultstate="collapsed" desc=" HorizontalMouseSupport ">
    private class HorizontalMouseSupport implements MouseListener, MouseMotionListener { 
        
        SplitterLayout root = SplitterLayout.this;  
        
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
            root.sourcePoint = e.getPoint(); 
        }
        
        public void mouseReleased(MouseEvent e) {
            if (root.targetPoint != null) {
                Rectangle rect = divider.getBounds();
                int nx = rect.x + root.targetPoint.x; 
                if (nx < 0) { 
                    root.targetPoint.x = root.locationIndex * -1;
                }
                
                root.locationIndex = nx;
            }
            root.sourcePoint = null;
            root.targetPoint = null;
            
            if ( e.getSource() instanceof Component ) {
                Container parent = ((Component) e.getSource()).getParent(); 
                if ( parent instanceof Canvas ) {
                    Canvas canvas = (Canvas) parent; 
                    canvas.revalidate(); 
                    canvas.repaint();
                } 
            } 
        }
        
        public void mouseEntered(MouseEvent e) {}
        public void mouseExited(MouseEvent e) {}

        public void mouseMoved(MouseEvent e) {}        
        public void mouseDragged(MouseEvent e) {
            root.targetPoint = e.getPoint(); 
            Rectangle divRect = divider.getBounds();
            int nx = divRect.x + root.targetPoint.x; 
            if (nx < 10) { 
                root.targetPoint.x = (divRect.x * -1)+10; 
            } else {
                int w = root.viewRect.width-divRect.x;
                int limit = w - getDividerSize() - 10;
                if (root.targetPoint.x > limit) root.targetPoint.x = limit;
            }
            
            if ( e.getSource() instanceof Component ) {
                Container parent = ((Component) e.getSource()).getParent(); 
                if ( parent instanceof Canvas ) {
                    Canvas canvas = (Canvas) parent; 
                    canvas.paintDividerHandle(root.viewRect, divRect, root.targetPoint); 
                } 
            } 
        } 
        
        private int prevLocationIndex;
        private void toggleLeftView() {
//            if (root.getLocationIndex() == 0) {
//                root.setLocationIndex(prevLocationIndex); 
//                if (root.explorer != null) root.explorer.setVisible(true); 
//            } else { 
//                prevLocationIndex = root.getLocationIndex(); 
//                root.setLocationIndex(0); 
//                if (root.explorer != null) root.explorer.setVisible(false);
//            }             
            //provider.revalidate();
            //provider.repaint(); 
        }         
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" HorizontalLayout (Class) ">

    private class HorizontalLayout implements LayoutManager {
        
        SplitterLayout root = SplitterLayout.this; 
        
        public void addLayoutComponent(String name, Component comp) {;}
        public void removeLayoutComponent(Component comp) {;}
        
        public Dimension preferredLayoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                return getLayoutSize(parent);
            }
        }
        
        public Dimension minimumLayoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                return getLayoutSize(parent);
            }
        }
        
        public Dimension getLayoutSize(Container parent) {
            int w = getDividerSize();
            int h = getDividerSize();
            Insets margin = parent.getInsets();
            w += (margin.left + margin.right);
            h += (margin.top + margin.bottom);
            return new Dimension( w, h ); 
        }
        
        public void layoutContainer(Container parent) {
            synchronized (parent.getTreeLock()) {
                Component divider = root.getDivider();
                parent.remove(divider);
                parent.add(divider); 

                Insets margin = parent.getInsets();
                int pw = parent.getWidth();
                int ph = parent.getHeight();
                int x = margin.left, y = margin.top; 
                int w = pw - (margin.left + margin.right);
                int h = ph - (margin.top + margin.bottom);
                viewRect = new Rectangle(x, y, w, h); 

                int dividerSize = root.getDividerSize();                 
                int dividerMinPos = margin.left;
                int dividerMaxPos = pw - margin.right - dividerSize; 
                
                int locationIndex = root.getLocationIndex(); 
                if ( locationIndex <= dividerMinPos ) { 
                    locationIndex = dividerMinPos;  
                } else if ( locationIndex >= dividerMaxPos ) { 
                    locationIndex = dividerMaxPos;  
                } 

                int dw = locationIndex - x; 
                if ( dw > 0 && root.vwside != null ) {
                    root.vwside.setBounds(x, y, dw, h); 
                }
                
                divider.setBounds(locationIndex, y, dividerSize, h);
                
                dw = pw - margin.right - (locationIndex + dividerSize); 
                if ( dw > 0 && root.vwcontent != null ) { 
                    x = locationIndex + dividerSize; 
                    root.vwcontent.setBounds(x, y, dw, h); 
                } 
            } 
        } 
    } 

    // </editor-fold>            
}
