/*
 * FingerPrintImage.java
 *
 * Created on December 17, 2013, 2:17 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.fingerprint;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 *
 * @author wflores
 */
class FingerPrintImage extends JPanel implements FingerPrintDataModel.Item 
{
    private static final long serialVersionUID = 1L;

    private ImageContext imageContext;     
    private ImageRenderer renderer; 
    private JLabel label;
    private int fingerType;
    
    private FingerPrintDataModel model; 
    private boolean selected;
    
    public FingerPrintImage(String caption, int fingerType) { 
        this.fingerType = fingerType; 
        initComponent(); 
        setCaption(caption); 
    } 
    
    private void initComponent() {
        super.setLayout(new DefaultLayout());
        
        renderer = new ImageRenderer(); 
        add(renderer);
        
        label = new JLabel("");
        label.setHorizontalAlignment(SwingConstants.CENTER); 
        label.setBorder(BorderFactory.createEmptyBorder(3,2,3,2)); 
        add(label); 
    }
    
    // <editor-fold defaultstate="collapsed" desc=" Getters/Setters "> 
    
    public void setCaption(String caption) { 
        label.setText(caption == null? "": caption); 
    }
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" FingerPrintDataModel.Item implementation "> 
    
    public void setModel(FingerPrintDataModel model) {
        this.model = model; 
    }
    
    public ImageContext getImageContext() { return imageContext; } 
    public void setImageContext(ImageContext imageContext) {
        this.imageContext = imageContext; 
        if (imageContext != null) {
            imageContext.setFingerType(this.fingerType); 
        }
        renderer.repaint();
    }

    public void refresh() {
        selected = model.isSelected(this); 
        renderer.repaint();
    }
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" DefaultLayout "> 
    
    private class DefaultLayout implements LayoutManager 
    {
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
                int w=0, h=0;
                
                Component[] comps = parent.getComponents();
                for (int i=0; i<comps.length; i++) {
                    Component c = comps[i];
                    if (!c.isVisible()) continue;
                    
                    Dimension dim = c.getPreferredSize(); 
                    w = Math.max(dim.width, w); 
                    h += dim.height;
                }
                
                Insets margin = parent.getInsets();
                w += margin.left + margin.right;
                h += margin.top + margin.bottom;
                return new Dimension(w, h); 
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
                
                Component[] comps = parent.getComponents();
                for (int i=0; i<comps.length; i++) {
                    Component c = comps[i];
                    if (!c.isVisible()) continue;
                    
                    Dimension dim = c.getPreferredSize(); 
                    c.setBounds(x, y, w, dim.height);
                    y += dim.height; 
                }
            } 
        }
    }
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" ImageRenderer "> 
    
    private class ImageRenderer extends JLabel 
    {
        FingerPrintImage root = FingerPrintImage.this;
        
        private Color shadow;
        private Color darkShadow;
        
        ImageRenderer() {            
            setPreferredSize(new Dimension(178, 196));
            setHorizontalAlignment(SwingConstants.CENTER); 

            shadow = getBackground().darker(); 
            darkShadow = shadow.darker();
            setBackground(Color.WHITE); 
            setOpaque(true); 
            
            addMouseListener(new MouseListener() {
                public void mouseClicked(MouseEvent e) {}
                public void mouseEntered(MouseEvent e) {}
                public void mouseExited(MouseEvent e) {}
                public void mouseReleased(MouseEvent e) {}
                
                public void mousePressed(MouseEvent e) {
                    onmousePressed(e);
                }                
            });
            addKeyListener(new KeyListener() {
                public void keyPressed(KeyEvent e) {
                }
                public void keyReleased(KeyEvent e) {
                }
                public void keyTyped(KeyEvent e) {
                    onkeyTyped(e);
                }
            }); 
        }
        
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            int width = getWidth();
            int height = getHeight();
            Color oldColor = g.getColor();            
            Graphics2D g2 = (Graphics2D)g;
            g2.setColor(Color.WHITE); 
            
            // gets the current clipping area
            Rectangle clip = g2.getClipBounds();
            g2.clearRect(0, 0, clip.width, clip.height); 
            
            if (imageContext != null) { 
                int px = Math.max((width - imageContext.getWidth())/2, 0);
                int py = Math.max((height - imageContext.getHeight())/2, 0);
                int pw = Math.min(width, imageContext.getWidth());
                int ph = Math.min(height, imageContext.getHeight());
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.drawImage(imageContext.getImage(), px, py, pw, ph, null); 
            } 
            
            g2.setColor(shadow); 
            g2.drawRect(0, 0, width-1, height-1); 
            if (selected) {
                //g2.setColor(darkShadow);
                g2.setColor(new Color(51, 153, 255)); 
                g2.drawRect(0, 0, width-1, height-1); 
                g2.setColor(shadow); 
                g2.drawRect(1, 1, width-3, height-3); 
            }
            g2.setColor(oldColor);
        } 
        
        private void onmousePressed(MouseEvent e) {
            root.model.setSelectedItem(root); 
            root.model.refresh();
        }
        
        private void onkeyTyped(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                int index = root.model.indexOf(root); 
                FingerPrintDataModel.Item item = root.model.getItem(index+1); 
                if (item == null) return;
                
                root.model.setSelectedItem(item);
                root.model.refresh();
            } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                int index = root.model.indexOf(root); 
                FingerPrintDataModel.Item item = root.model.getItem(index-1); 
                if (item == null) return; 
                
                root.model.setSelectedItem(item);
                root.model.refresh();                
            }
        }
    }
    
    // </editor-fold>    
}
