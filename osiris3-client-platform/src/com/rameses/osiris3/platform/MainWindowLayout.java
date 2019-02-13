/*
 * MainLayout.java
 *
 * Created on October 24, 2013, 11:17 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.platform;


import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.net.URL;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

/**
 *
 * @author wflores
 */
class MainWindowLayout implements LayoutManager 
{
    public final static String MENUBAR_SECTION = "MENUBAR";
    public final static String TOOLBAR_SECTION = "TOOLBAR";
    public final static String CONTENT_SECTION = "CONTENT";
    public final static String STATUSBAR_SECTION = "STATUSBAR";
    
    private Component menubar;
    private Component toolbar;
    private Component content;
    private Component statusbar;
    
    private Logo logo;
    
    public MainWindowLayout() {
        logo = new Logo(); 
    }
    
    public void addLayoutComponent(String name, Component comp) { 
        if (comp == null) return;
        
        synchronized (comp.getTreeLock()) {
            if (name == null) name = CONTENT_SECTION;

            if (MENUBAR_SECTION.equalsIgnoreCase(name)) {
                menubar = comp;
            } else if (TOOLBAR_SECTION.equalsIgnoreCase(name)) {
                toolbar = comp;
            } else if (STATUSBAR_SECTION.equalsIgnoreCase(name)) {
                statusbar = comp;
            } else if (CONTENT_SECTION.equalsIgnoreCase(name)) {
                content = comp; 
            } 
        } 
    }    
    public void removeLayoutComponent(Component comp) {
        if (comp == null) return;
        
        synchronized (comp.getTreeLock()) {
            if (menubar != null && menubar.equals(comp)) {
                menubar = null; 
            } else if (toolbar != null && toolbar.equals(comp)) {
                toolbar = null; 
            } else if (statusbar != null && statusbar.equals(comp)) {
                statusbar = null; 
            } else if (content != null && content.equals(comp)) {
                content = null; 
            } 
        } 
    } 
    
    public Component getLayoutComponent(Object constraints) {
        String name = (constraints == null? null: constraints.toString());
        if (name == null || name.length() == 0) return null; 
        
        if (MENUBAR_SECTION.equalsIgnoreCase(name)) {
            return menubar; 
        } else if (TOOLBAR_SECTION.equalsIgnoreCase(name)) {
            return toolbar; 
        } else if (STATUSBAR_SECTION.equalsIgnoreCase(name)) {
            return statusbar; 
        } else if (CONTENT_SECTION.equalsIgnoreCase(name)) {
            return content; 
        } else {
            return null; 
        }
    }

    public Dimension minimumLayoutSize(Container parent) {
        synchronized (parent.getTreeLock()) {
            int width=0, height=0;

            if (menubar != null && menubar.isVisible()) {
                Dimension dim = menubar.getMinimumSize();
                width = Math.max(dim.width, width); 
                height += dim.height;
            }
            if (toolbar != null && toolbar.isVisible()) {
                Dimension dim = toolbar.getMinimumSize();
                width = Math.max(dim.width, width); 
                height += dim.height;
            } 
            if (statusbar != null && statusbar.isVisible()) {
                Dimension dim = statusbar.getMinimumSize();
                width = Math.max(dim.width, width); 
                height += dim.height;
            } 
            if (content != null && content.isVisible()) {
                Dimension dim = content.getMinimumSize();
                if (width == 0) width = dim.width;
                
                height += dim.height;
            } 
            
            Insets insets = parent.getInsets();
            width += insets.left + insets.right;
            height += insets.top + insets.bottom;
            return new Dimension(width, height); 
        }
    }

    public Dimension preferredLayoutSize(Container parent) {
        synchronized (parent.getTreeLock()) {
            int width=0, height=0;

            if (menubar != null && menubar.isVisible()) {
                Dimension dim = menubar.getPreferredSize();
                width = Math.max(dim.width, width); 
                height += dim.height;
            }
            if (toolbar != null && toolbar.isVisible()) {
                Dimension dim = toolbar.getPreferredSize();
                width = Math.max(dim.width, width); 
                height += dim.height;
            } 
            if (statusbar != null && statusbar.isVisible()) {
                Dimension dim = statusbar.getPreferredSize();
                width = Math.max(dim.width, width); 
                height += dim.height;
            } 
            if (content != null && content.isVisible()) {
                Dimension dim = content.getPreferredSize();
                if (width == 0) width = dim.width;
                
                height += dim.height;
            } 
            
            Insets insets = parent.getInsets();
            width += insets.left + insets.right;
            height += insets.top + insets.bottom;
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
           
            if (menubar != null && menubar.isVisible()) {
                Dimension dim = menubar.getPreferredSize();
                menubar.setBounds(x, y, w, dim.height); 
                y += dim.height;
            }                        
            if (toolbar != null && toolbar.isVisible()) {
                Dimension dim = toolbar.getPreferredSize();
                
                //logo component
                if (logo.getParent() == null) parent.add(logo); 
                if (toolbar instanceof JComponent) {
                    Border border = ((JComponent)toolbar).getBorder(); 
                    if (border == null) border = BorderFactory.createEmptyBorder();

                    logo.setBorder(border); 
                }
                logo.setPreferredSize(new Dimension(100, dim.height));                
                Dimension ldim = logo.getPreferredSize(); 
                int lx = (pw - margin.right) - ldim.width; 
                int lw = lx - margin.left;
                toolbar.setBounds(x, y, lx-margin.right, dim.height); 
                logo.setBounds(lx, y, ldim.width, dim.height); 
                y += dim.height;
            } 
            
            int cy = y;
            int ch = (ph-margin.bottom)-y;            
            if (ch <= 0) return;
            
            if (statusbar != null && statusbar.isVisible()) {
                Dimension dim = statusbar.getPreferredSize();
                int y0 = (ch - dim.height)+cy;
                if (y0 <= 0) return;
                
                statusbar.setBounds(x, y0, w, dim.height); 
                ch = y0 - y; 
            } 
            
            if (ch <= 0) return; 
            if (content != null && content.isVisible()) {
                Dimension dim = content.getPreferredSize();
                content.setBounds(x, cy, w, ch); 
            }
        }
    } 

    // <editor-fold defaultstate="collapsed" desc=" Logo ">
    
    private class Logo extends JLabel 
    { 
        MainWindowLayout root = MainWindowLayout.this;
        ImageIcon iicon;
        Image scaledImage;
        Dimension scale;
        
        Logo() {
            super();
            try {
                URL url = root.getClass().getResource("icon/rameses.png"); 
                iicon = new ImageIcon(url);
            } catch(Throwable e){;}
            
            setOpaque(true); 
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); 
            setToolTipText("www.ramesesinc.com"); 
            addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent me) {
                    if (!SwingUtilities.isLeftMouseButton(me)) return;
                    if (me.getClickCount() != 1) return;
                    
                    try { 
                        Desktop.getDesktop().browse(new URI("www.ramesesinc.com"));
                    } catch(Throwable t){;} 
                }
            });
            
        }
        
        public void paint(Graphics g) {
            super.paint(g); 
            if (iicon == null) return;

            Insets margin = getInsets();  
            int w = getWidth();
            int h = getHeight();
            int x = margin.left;
            int y = margin.top;            
            Image image = iicon.getImage();
            int imgw = iicon.getIconWidth();
            int imgh = iicon.getIconHeight();
            if (imgw < (w-margin.left-margin.right)) x = w - (margin.left + margin.right + imgw);
            if (imgh < (h-margin.top-margin.bottom)) {
                int rh = h - (margin.top + margin.bottom); 
                y = Math.max((h/2)-(rh/2), 0)+margin.top;
            }
            if (x < 0) x = margin.left; 
            if (y < 0) y = margin.top; 
            
            Graphics2D g2 = (Graphics2D) g.create(); 
            g2.drawImage(image, x, y, imgw, imgh, null);             
            g2.dispose(); 
        }
    }
    
    // </editor-fold>
    
}
