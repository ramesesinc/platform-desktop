/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.control.image;

import com.rameses.rcp.support.ImageIconSupport;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.LayoutManager2;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 *
 * @author wflores 
 */
class ImageCropToolbar extends JPanel {

    public final static String ACTION_ZOOM_IN = "zoom-in";
    public final static String ACTION_ZOOM_OUT = "zoom-out";
    public final static String ACTION_FIT_SCREEN = "fit-screen";
    
    private final static String IMG_ZOOM_IN    = "com/rameses/rcp/icons/zoom-in-16.png"; 
    private final static String IMG_ZOOM_OUT   = "com/rameses/rcp/icons/zoom-out-16.png"; 
    private final static String IMG_FIT_SCREEN = "com/rameses/rcp/icons/fit-to-screen.png"; 
    
    private JButton btnFitWindow; 
    private JButton btnZoomIn; 
    private JButton btnZoomOut; 
    
    ImageCropToolbar() {
        initComponent();
    }
    
    // <editor-fold defaultstate="collapsed" desc=" initComponent "> 
    
    private void initComponent() {
        super.setLayout(new DefaultLayout()); 
        setOpaque( false );
        
        btnZoomIn = createButton("Zoom In", IMG_ZOOM_IN);
        btnZoomOut = createButton("Zoom Out", IMG_ZOOM_OUT);
        btnFitWindow = createButton("Fit to window", IMG_FIT_SCREEN);
        
        add( btnZoomIn ); 
        add( btnFitWindow ); 
        add( btnZoomOut );    
    } 
    
    private JButton createButton( String tooltiptext, String iconpath ) {
        JButton btn = new JButton();
        btn.setOpaque(false);
        btn.setFocusable(false);
        btn.setBorderPainted(false);
        btn.setMargin(new Insets(2,4,2,4));
        if ( tooltiptext != null ) {
            btn.setToolTipText( tooltiptext );
        }
        try {
            btn.setIcon(getIcon( iconpath)); 
        } catch(Throwable t){;} 
        
        return btn; 
    }
    
    // </editor-fold>        
    
    // <editor-fold defaultstate="collapsed" desc=" Helper methods "> 

    public void setAction( String name, ActionListener handler ) {
        if ( name == null || handler == null ) return; 
        
        JButton btn = null; 
        if ( ACTION_ZOOM_IN.equals( name)) btn = btnZoomIn; 
        else if ( ACTION_ZOOM_OUT.equals( name)) btn = btnZoomOut;
        else if ( ACTION_FIT_SCREEN.equals( name)) btn = btnFitWindow; 
        
        if ( btn == null ) return;  
        
        ActionListener[] items = btn.getActionListeners(); 
        if ( items == null ) items = new ActionListener[0];
        
        for ( ActionListener o : items ) { 
            btn.removeActionListener( o ); 
        }
        btn.addActionListener( handler ); 
    } 
    
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = null; 
        try {
            AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f); 
            g2 = (Graphics2D) g.create(); 
            g2.setColor(Color.WHITE ); 
            g2.setComposite( alpha ); 
            g2.fillRect( 0, 0, getWidth(), getHeight());  
        } catch (Throwable t) {
            //do nothing 
        } finally {
            try { g2.dispose(); }catch(Throwable t){;} 
        } 
    }
    
    private ImageIcon getIcon(String name) {
        return ImageIconSupport.getInstance().getIcon(name);
    }    
        
    // </editor-fold>        
    
    // <editor-fold defaultstate="collapsed" desc=" DefaultLayout "> 
    
    private class DefaultLayout implements LayoutManager, LayoutManager2 {
        
        private Insets DEFAULT_PADDING = new Insets(3,5,3,5); 
        
        public void addLayoutComponent(String name, Component comp) {}
        public void removeLayoutComponent(Component comp) {}

        public Dimension preferredLayoutSize(Container parent) {
            return layoutSize(parent);
        }

        public Dimension minimumLayoutSize(Container parent) {
            return layoutSize(parent);
        }

        private Dimension layoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                int width=0, height=0, count=0;
                Component[] comps = parent.getComponents(); 
                for (int i=0; i<comps.length; i++) {
                    Component c = comps[i];
                    if (!c.isVisible()) continue; 
                    
                    Dimension dim = c.getPreferredSize();
                    width += dim.width;
                    height = Math.max(dim.height, height); 
                    count += 1; 
                }
                Insets margin = DEFAULT_PADDING;
                width += (margin.left + margin.right)+( count*2 );
                height += (margin.top + margin.bottom);
                return new Dimension(width, height); 
            }
        }
        
        public void layoutContainer(Container parent) { 
            synchronized (parent.getTreeLock()) { 
                Insets margin = DEFAULT_PADDING;
                int pw = parent.getWidth();
                int ph = parent.getHeight();
                int x = margin.left;
                int y = margin.top;
                int w = pw - (margin.left + margin.right);
                int h = ph - (margin.top + margin.bottom);
                
                Component[] comps = parent.getComponents(); 
                for (int i=0; i<comps.length; i++) {
                    Component c = comps[i];
                    if (!c.isVisible()) continue; 
                    
                    Dimension dim = c.getPreferredSize();
                    c.setBounds(x, y, dim.width, h);
                    x += dim.width + 2; 
                }                
            } 
        }

        public void addLayoutComponent(Component comp, Object constraints) {}

        public Dimension maximumLayoutSize(Container target) { 
            return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE); 
        }

        public float getLayoutAlignmentX(Container target) { return 0.5f; }
        public float getLayoutAlignmentY(Container target) { return 0.5f; }

        public void invalidateLayout(Container target) {
            layoutContainer(target); 
        }
    }
    
    // </editor-fold>            
}
