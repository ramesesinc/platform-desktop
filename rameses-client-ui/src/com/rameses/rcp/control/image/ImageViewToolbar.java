/*
 * ImageViewToolbar.java
 *
 * Created on March 13, 2014, 11:11 AM
 *
 * To change this template, choose Tools | Template Manager
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author wflores
 */
public class ImageViewToolbar extends JPanel implements ImageView.Handler
{
    private ImageViewPanel parent; 
    private ImageView imageView; 
    
    private JLabel label;
    private JSlider slider;
    private JButton btnFitWindow;
    private JButton btnFitActual;
    
    public ImageViewToolbar(ImageViewPanel parent) {
        this.parent = parent; 
        initComponent();
    }
    
    // <editor-fold defaultstate="collapsed" desc=" initComponent "> 
    
    private void initComponent() {
        super.setLayout(new DefaultLayout()); 
        setBorder(BorderFactory.createEmptyBorder(2,5,2,5)); 
        setOpaque(false); 
        
        label = new JLabel("%");
        label.setForeground(Color.WHITE); 
        add(label);
        add(Box.createHorizontalStrut(5));
        
        slider = new JSlider(6, 800); 
        slider.setOpaque(false); 
        slider.setToolTipText("Zoom");
        add(slider); 
        
        btnFitWindow = new JButton();
        btnFitWindow.setOpaque(false);
        btnFitWindow.setBorderPainted(false);
        btnFitWindow.setMargin(new Insets(1,3,1,3));
        btnFitWindow.setToolTipText("Fit to window");
        btnFitWindow.setIcon(getIcon("com/rameses/rcp/icons/fit-to-screen.png"));
        add(btnFitWindow);
        
        btnFitActual = new JButton("1:1");
        btnFitActual.setOpaque(false);
        btnFitActual.setBorderPainted(false);
        btnFitActual.setMargin(new Insets(1,2,1,2)); 
        btnFitActual.setToolTipText("Actual size");
        add(btnFitActual); 
        
        SliderHandler handler = new SliderHandler();
        slider.addMouseWheelListener(handler);        
        slider.addMouseListener(handler);
        slider.addChangeListener(handler); 
        
        btnFitWindow.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fitToWindow();
            }
        }); 
        btnFitActual.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fitToActualSize();
            }
        });         
    }
    
    private ImageIcon getIcon(String name) {
        return ImageIconSupport.getInstance().getIcon(name);
    }
        
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" Getters / Setters "> 
    
    public ImageView.Handler getImageViewHandler() { 
        return this; 
    }

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" helper and override methods "> 
    
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); 
        
        AlphaComposite alpha = createAlphaComposite(0.2f); 
        if (alpha == null) return;
        
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setComposite(alpha);
        g2.fillRect(0, 0, getWidth(), getHeight()); 
        g2.dispose(); 
    } 
    
    private AlphaComposite createAlphaComposite(float alpha) {
//        try {
//            return AlphaComposite.SrcOver.derive(alpha); 
//        } catch (Throwable t) {;} 
        
        try {
            return AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha); 
        } catch (Throwable t) {
            return null; 
        } 
    }
    
    private void fitToWindow() {
        if (imageView == null) return;
        
        imageView.fitToWindow();
    }
    
    private void fitToActualSize() {
        if (imageView == null) return;
        
        imageView.zoom(100);
    }
        
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" DefaultLayout "> 
    
    private class DefaultLayout implements LayoutManager, LayoutManager2 
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
                int w = pw - (margin.left + margin.right);
                int h = ph - (margin.top + margin.bottom);
                
                Component[] comps = parent.getComponents(); 
                for (int i=0; i<comps.length; i++) {
                    Component c = comps[i];
                    if (!c.isVisible()) continue; 
                    
                    Dimension dim = c.getPreferredSize();
                    c.setBounds(x, y, dim.width, h);
                    x += dim.width; 
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
    
    // <editor-fold defaultstate="collapsed" desc=" ImageView.Handler implementation "> 

    public void refresh(ImageView imageView) {
        this.imageView = imageView; 
        
        double maxScale = (double) slider.getMaximum();
        double newCurScale = imageView.getCurrentScale() * 100.0; 
        double newMinScale = imageView.getMinScale() * 100.0;
        if (newMinScale < 0.0) newMinScale = 0.0;

        if (newCurScale < newMinScale) newCurScale = newMinScale;
        if (newCurScale > maxScale) newCurScale = maxScale;
        
        newMinScale = resolveValue(newMinScale);
        newCurScale = resolveValue(newCurScale);
        slider.setMinimum((int) resolveValue(newMinScale));
        slider.setValue((int) resolveValue(newCurScale)); 
    }
    
    private double resolveValue(double value) {
        return new BigDecimal(value).setScale(0, RoundingMode.HALF_UP).doubleValue();        
    } 
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" SliderHandler "> 
    
    private class SliderHandler implements ChangeListener, MouseListener, MouseWheelListener
    {
        ImageViewToolbar root = ImageViewToolbar.this;
        
        private boolean enableStateListener;
        
        public void mouseClicked(MouseEvent e) {}
        public void mousePressed(MouseEvent e) {}
        public void mouseReleased(MouseEvent e) {}

        public void mouseEntered(MouseEvent e) {
            enableStateListener = true;
        }
        public void mouseExited(MouseEvent e) {
            enableStateListener = false;
        }
        
        public void stateChanged(ChangeEvent e) {
            updateDependencies();
            
            boolean allowed = enableStateListener;
            if (!allowed && root.slider.hasFocus()) allowed = true; 
            if (!allowed || root.imageView == null) return;

            root.imageView.zoom(root.slider.getValue()); 
        }

        public void mouseWheelMoved(MouseWheelEvent e) {
            if (!enableStateListener) return;
            int value = root.slider.getValue();
            if (e.getWheelRotation() <= -1) {
                value += e.getScrollAmount();
            } else {
                value -= e.getScrollAmount();
            }
            root.slider.setValue(value); 
        }
        
        private void updateDependencies() {
            int value = root.slider.getValue();
            ImageIcon iicon = root.parent.getValue();
            StringBuffer sb = new StringBuffer();
            sb.append(" " + value + "%"); 
            if (iicon != null) {
                sb.append("  |  ");                
                sb.append(iicon.getIconWidth());
                sb.append(" x ");
                sb.append(iicon.getIconHeight());
                sb.append(" px");
            }
            root.label.setText(sb.toString());
        }
    }
    
    // </editor-fold>
}
