/*
 * SplitVewLayout.java
 *
 * Created on April 28, 2013, 10:59 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control.layout;


import com.rameses.rcp.constant.UIConstants;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

/**
 *
 * @author wflores
 */
public class SplitViewLayout implements LayoutManager
{
    private Map<String,LayoutManager> layouts = new HashMap<String,LayoutManager>();
    private LayoutManager layout;
    private String orientation;
    private int dividerSize;
    private int dividerLocation;
    private int dividerLocationPercentage;
    private int locationIndex;
    private Component divider;
    private SplitViewLayout.Provider provider;

    private Component horizontalDivider;
    private Component verticalDivider;
    private Point sourcePoint;
    private Point targetPoint;
    private Rectangle viewRect; 
    
    private boolean startedMoving;
    private boolean showDividerBorder; 
    
    public SplitViewLayout(SplitViewLayout.Provider provider)
    {
        this.provider = provider;
        layouts.put(UIConstants.HORIZONTAL, new HorizontalLayout()); 
        layouts.put(UIConstants.VERTICAL, new VerticalLayout());        
        setOrientation(UIConstants.HORIZONTAL);
        setDividerLocation(100);
        setDividerSize(5); 
    }
    
    public String getOrientation() { return this.orientation; }    
    public void setOrientation(String orientation) 
    {
        this.orientation = orientation;
        this.layout = layouts.get(orientation); 
        if (this.layout == null) {
            this.orientation = UIConstants.HORIZONTAL;
            this.layout = layouts.get(orientation); 
        }
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
    
    public int getDividerLocationPercentage() { return dividerLocationPercentage; } 
    public void setDividerLocationPercentage( int dividerLocationPercentage ) {
        this.dividerLocationPercentage = dividerLocationPercentage; 
    }
    
    public boolean isShowDividerBorder() { return showDividerBorder; } 
    public void setShowDividerBorder( boolean showDividerBorder ) {
        this.showDividerBorder = showDividerBorder; 
    }

    void setLocationIndex(int x) {
        this.locationIndex = x; 
    }
    
    private Component getDivider() {
        if ("vertical".equalsIgnoreCase(getOrientation()+"")) { 
            if (verticalDivider == null) {
                JDivider lbl = new JDivider( SwingConstants.VERTICAL );
                lbl.setName("splitview.divider"); 
                lbl.setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR)); 
                
                new VerticalMouseSupport().setDivider(lbl); 
                verticalDivider = lbl;
            }
            return verticalDivider; 
        } else {
            if (horizontalDivider == null) {
                JDivider lbl = new JDivider( SwingConstants.HORIZONTAL );
                lbl.setName("splitview.divider"); 
                lbl.setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR)); 
                
                new HorizontalMouseSupport().setDivider(lbl);                
                horizontalDivider = lbl;
            } 
            return horizontalDivider;
        }
    }
        
    public void addLayoutComponent(String name, Component comp) {}    
    public void removeLayoutComponent(Component comp) {}

    public Dimension preferredLayoutSize(Container parent) {
        if (layout == null) 
            return new Dimension(getDividerSize(), getDividerSize());
        else 
            return layout.preferredLayoutSize(parent);
    }

    public Dimension minimumLayoutSize(Container parent) {
        if (layout == null) 
            return new Dimension(getDividerSize(), getDividerSize());
        else 
            return layout.minimumLayoutSize(parent); 
    }
    
    public void layoutContainer(Container parent) {
        layout.layoutContainer(parent);
    }    
    
    private Component[] getLayoutComponents(Component[] comps) {
        if (comps == null) return new Component[]{};
        
        List<Component> list = new ArrayList();
        for (int i=0; i<comps.length; i++) {
            Component c = comps[i];            
            if (horizontalDivider != null && horizontalDivider.equals(c)) continue;
            if (verticalDivider != null && verticalDivider.equals(c)) continue;
            if (list.size() >= 2) break;
                
            list.add(c); 
        }
        return list.toArray(new Component[]{}); 
    }
    
    private Component getLayoutComponent(Component[] comps, String name) {
        if (comps == null || name == null) return null;
        
        for (int i=0; i<comps.length; i++) {
            String cname = comps[i].getName();
            if (name.equals(cname)) return comps[i]; 
        } 
        return null; 
    } 
    
    private Component lookupComponent(Component[] comps) {
        if (comps == null || comps.length == 0) return null;
        
        for (int i=0; i<comps.length; i++) {
            Component c = comps[i];
            if (c.getName() == null) return c;
        }
        return null; 
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
        SplitViewLayout root = SplitViewLayout.this;  
        Component divider;
        
        void setDivider(Component divider) {
            this.divider = divider;
            divider.addMouseListener(this);
            divider.addMouseMotionListener(this); 
        }
        
        public void mouseClicked(MouseEvent e) {}
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
            provider.revalidate();
            provider.repaint(); 
        }
        
        public void mouseEntered(MouseEvent e) {}
        public void mouseExited(MouseEvent e) {}

        public void mouseMoved(MouseEvent e) {}        
        public void mouseDragged(MouseEvent e) {
            startedMoving = true;
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
    }
    
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" VerticalMouseSupport ">
    
    private class VerticalMouseSupport implements MouseListener, MouseMotionListener
    {
        SplitViewLayout root = SplitViewLayout.this;  
        Component divider;
        
        void setDivider(Component divider) {
            this.divider = divider;
            divider.addMouseListener(this);
            divider.addMouseMotionListener(this); 
        }
        
        public void mouseClicked(MouseEvent e) {}
        public void mousePressed(MouseEvent e) {
            sourcePoint = e.getPoint(); 
        }
        
        public void mouseReleased(MouseEvent e) {
            if (targetPoint != null) {
                Rectangle rect = divider.getBounds();
                int ny = rect.y + targetPoint.y; 
                if (ny < 0) targetPoint.y = locationIndex * -1;
                
                locationIndex = ny;
            }
            sourcePoint = null;
            targetPoint = null;
            provider.revalidate();
            provider.repaint(); 
        }
        
        public void mouseEntered(MouseEvent e) {}
        public void mouseExited(MouseEvent e) {}

        public void mouseMoved(MouseEvent e) {}        
        public void mouseDragged(MouseEvent e) {
            root.startedMoving = true;             
            targetPoint = e.getPoint(); 
            Rectangle divRect = divider.getBounds();
            int ny = divRect.y + targetPoint.y; 
            if (ny < 10) { 
                targetPoint.y = (divRect.y * -1)+10; 
            } else {
                int h = viewRect.height-divRect.y;
                int limit = h - getDividerSize() - 10;
                if (targetPoint.y > limit) targetPoint.y = limit;
            }             
            provider.paintDividerHandle(viewRect, divRect, targetPoint); 
        } 
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" JDivider ">
    
    private class JDivider extends JLabel {
        
        SplitViewLayout root = SplitViewLayout.this; 
        
        private Color hilite; 
        private Color shadow; 
        private int orientation; 
        
        public JDivider( int orientation ) {
            super(); 
            this.orientation = orientation; 
            this.hilite = SystemColor.controlLtHighlight;
            this.shadow = SystemColor.controlDkShadow; 
        }
        
        protected void paintBorder(Graphics g) {
            super.paintBorder(g);
            
            if ( !root.isShowDividerBorder()) return; 
            
            int dsize = root.getDividerSize();
            if ( dsize < 3 ) return;
            
            Rectangle rect = getBounds(); 
            if ( orientation == SwingConstants.VERTICAL ) {
                g.setColor(hilite); 
                g.drawLine(0, 1, rect.width, 1);
                g.setColor(shadow); 
                g.drawLine(0, rect.height-2, rect.width, rect.height-2); 
                
            } else {
                g.setColor(hilite); 
                g.drawLine(1, 0, 1, rect.height);
                g.setColor(shadow); 
                g.drawLine(rect.width-2, 0, rect.width-2, rect.height); 
                
            }
        }
    }
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" HorizontalLayout (Class) ">

    private class HorizontalLayout implements LayoutManager {
        
        SplitViewLayout root = SplitViewLayout.this; 
        
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
                int dsize = root.getDividerSize(); 
                int w=dsize, h=dsize;

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
                int pw = parent.getWidth(), ph = parent.getHeight();
                int lbound = margin.left; 
                int rbound = pw - margin.right; 
                int x = margin.left, y = margin.top; 
                int w = pw - (margin.left + margin.right);
                int h = ph - (margin.top + margin.bottom);
                viewRect = new Rectangle(x, y, w, h); 

                int divsize = root.getDividerSize();
                int locindex = 0; 
                if ( root.startedMoving ) {
                    locindex = root.locationIndex;  
                } else {
                    double locper = (double) root.getDividerLocationPercentage(); 
                    if ( locper > 0 ) {
                        locper = Math.min( locper, 100.0 );
                        locindex = margin.left + ((int) ((locper / 100.0) * w)); 
                    } else {
                        locindex = root.getDividerLocation(); 
                    }
                }

                if ( locindex < lbound ) {
                    locindex = lbound + divsize;
                }
                if ( locindex > rbound ) {
                    locindex = rbound - divsize; 
                }

                Component[] comps = getLayoutComponents(parent.getComponents());
                if (comps.length >= 1) {
                    comps[0].setBounds(x, y, locindex, h); 
                }
                
                x += locindex;
                divider.setBounds(x, y, divsize, h); 
                x += getDividerSize();

                int rw = (pw-margin.right)-x;
                if (rw < 0) rw = 0;
                if (comps.length >= 2) {
                    comps[1].setBounds(x, y, rw, h); 
                }                
            }
        }
    }

    //</editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" VerticalLayout (Class) ">

    private class VerticalLayout implements LayoutManager {
        
        SplitViewLayout root = SplitViewLayout.this; 
        
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
                int dsize = root.getDividerSize(); 
                int w=dsize, h=dsize;

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
                int pw = parent.getWidth(), ph = parent.getHeight();
                int tMax = margin.top; 
                int bMax = ph - margin.bottom;                 
                int x = margin.left, y = margin.top; 
                int w = pw - (margin.left + margin.right);
                int h = ph - (margin.top + margin.bottom);
                viewRect = new Rectangle(x, y, w, h); 

                int divsize = root.getDividerSize();
                int locindex = 0; 
                if ( root.startedMoving ) {
                    locindex = root.locationIndex;  
                } else {
                    double locper = (double) root.getDividerLocationPercentage(); 
                    if ( locper > 0 ) {
                        locper = Math.min( locper, 100.0 );
                        locindex = margin.top + ((int) ((locper / 100.0) * h)); 
                    } else {
                        locindex = root.getDividerLocation(); 
                    }
                }
                
                if ( locindex < tMax ) {
                    locindex = tMax + divsize;
                }
                if ( locindex > bMax ) {
                    locindex = bMax - divsize; 
                }

                Component[] comps = getLayoutComponents(parent.getComponents());
                if (comps.length >= 1) {
                    comps[0].setBounds(x, y, w, locindex); 
                }

                y += locindex;
                divider.setBounds(x, y, w, divsize); 
                y += divsize;

                int nh = (ph-margin.bottom)-y;
                if (nh < 0) nh = 0;
                if (comps.length >= 2) {
                    comps[1].setBounds(x, y, w, nh); 
                }
            }
        }
    }

    //</editor-fold>        
}
