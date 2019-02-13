/*
 * ImageViewPanel.java
 *
 * Created on March 13, 2014, 10:41 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control.image;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.LayoutManager2;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 *
 * @author wflores
 */
public class ImageViewPanel extends JPanel 
{
    private ImageView imageView;
    private ImageViewToolbar toolbar;
    
    private String text;
    
    public ImageViewPanel() {
        initComponent(); 
    }
    
    // <editor-fold defaultstate="collapsed" desc=" initComponent "> 
    
    private void initComponent() {
        super.setLayout(new DefaultLayout()); 
        setPreferredSize(new Dimension(120, 100));
        setOpaque(false);  
        
        toolbar = new ImageViewToolbar(this);
        toolbar.setVisible(false); 
        add(toolbar);
        
        imageView = new ImageView(); 
        imageView.addMouseListener(new MouseAdapterImpl()); 
        imageView.addHandler(toolbar.getImageViewHandler()); 
        add(imageView);         
    }
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" Getters / Setters "> 
    
    public void setLayout(LayoutManager layoutManager) {}
    
    public ImageIcon getValue() { 
        return imageView.getValue(); 
    } 
    public void setValue(ImageIcon value) {
        imageView.setValue(value);
    }
    
    public String getText() { return text; }
    public void setText(String text) {
        this.text = text; 
        if (imageView != null) imageView.setText(text); 
    }    
    
    public Color getNoImageBackground() {
        return (imageView == null? null: imageView.getNoImageBackground());
    }
    public void setNoImageBackground(Color color) {
        if (imageView != null) imageView.setNoImageBackground(color); 
    }
    
    public Color getNoImageForeground() {
        return (imageView == null? null: imageView.getNoImageForeground());
    }
    public void setNoImageForeground(Color color) {
        if (imageView != null) imageView.setNoImageForeground(color); 
    }    
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" helper and override methods "> 
    
    public final void refreshCanvas() {
        if (imageView != null) imageView.refreshCanvas(); 
    }
    
    private void showToolbar() {
        ImageIcon iicon = (imageView == null? null: imageView.getValue());
        if (iicon == null) {
            toolbar.setVisible(false); 
            toolbar.setEnabled(false);
        } else {
            toolbar.setVisible(true); 
        } 
        revalidate();
        repaint();
    }
    
    private void hideToolbar() {
        toolbar.setVisible(false); 
        revalidate();
        repaint();
    }
        
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" DefaultLayout "> 
    
    private class DefaultLayout implements LayoutManager, LayoutManager2 
    {
        ImageViewPanel root = ImageViewPanel.this;
        
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
                Insets margin = parent.getInsets();
                int width = margin.left + margin.right;
                int height = margin.top + margin.bottom;
                return new Dimension(width+120, height+100); 
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
                
                Component c = root.imageView;
                if (c != null && c.isVisible()) { 
                    c.setBounds(x, y, w, h); 
                } 
                
                c = root.toolbar;
                if (c != null && c.isVisible()) {
                    Dimension dim = c.getPreferredSize();
                    x = Math.max(pw-margin.right-dim.width-2, 2); 
                    y = Math.max(ph-margin.bottom-dim.height-2, 2);
                    c.setBounds(x, y, dim.width, dim.height); 
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
    
    // <editor-fold defaultstate="collapsed" desc=" MouseAdapterImpl "> 
    
    private class MouseAdapterImpl implements MouseListener 
    {
        ImageViewPanel root = ImageViewPanel.this;
        
        public void mouseClicked(MouseEvent e) {}
        public void mousePressed(MouseEvent e) {}
        public void mouseReleased(MouseEvent e) {}

        public void mouseEntered(MouseEvent e) {            
            root.showToolbar();
        }

        public void mouseExited(MouseEvent e) {
            Rectangle bounds = root.getBounds();
            if (bounds.contains(e.getPoint())) return;
            
            root.hideToolbar(); 
        }
    }
    
    // </editor-fold>
}
