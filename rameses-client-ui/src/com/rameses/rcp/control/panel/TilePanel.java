/*
 * TilePanel.java
 *
 * Created on July 26, 2014, 6:06 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control.panel;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

/**
 *
 * @author wflores
 */
public class TilePanel extends WrapPanel  
{
    private boolean showCaptions;
    private String textAlignment;
    private String alignment;
    private Object selectedItem; 
    
    public TilePanel() {
        super();
        //super.setLayout(new ContainerLayout()); 
         
        showCaptions = true;
        addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {}
            public void mouseEntered(MouseEvent e) {}
            public void mouseExited(MouseEvent e) {}
            public void mouseReleased(MouseEvent e) {}
            public void mousePressed(MouseEvent e) {
                try {
                    clearSelection(); 
                } catch(Throwable t) {
                    t.printStackTrace(); 
                }
            }
        });
    }
    
    public void setLayout(LayoutManager mgr) {;}
        
    public String getTextAlignment() { return this.textAlignment; }
    public void setTextAlignment(String textAlignment) {
        this.textAlignment = textAlignment;
    }
    
    public String getAlignment() { return this.alignment; }
    public void setAlignment(String alignment) {
        this.alignment = alignment;
    }    
    
    public boolean isShowCaptions() { return showCaptions; }
    public void setShowCaptions(boolean showCaptions) { 
        this.showCaptions = showCaptions; 
    } 
    
    public void addItem(String caption, Object userObject) {
        addItem(caption , userObject); 
    }
    
    public void addItem(String caption, Object userObject, ImageIcon icon) {
        TileItem ti = new TileItem(caption, userObject, icon);
        super.add(ti); 
    }    
    
    public Object getSelectedItem() { 
        return selectedItem; 
    } 

    public TileItem getItem(Object userObject) {
        int idx = indexOf(userObject); 
        return getItem(idx); 
    }
    
    public TileItem getItem(int index) {
        try {
            return (TileItem) getComponent(index); 
        } catch(Throwable t) {
            return null; 
        }
    }
    
    public void removeItem(Object userObject) {
        int idx = indexOf(userObject); 
        removeItem(idx); 
    }
    
    public void removeItem(int index) {
        if (index >= 0 && index < getComponentCount()) {
            remove(index); 
        }
    }
    
    public int indexOf(Object userObject) {
        Component[] comps = getComponents(); 
        for (int i=0; i<comps.length; i++) {
            if (!(comps[i] instanceof TileItem)) continue; 
            
            TileItem item = (TileItem)comps[i]; 
            Object itemobj = item.getUserObject(); 
            if (userObject == null && itemobj == null) {
                return i; 
            } else if (userObject != null && userObject.equals(itemobj)) {
                return i; 
            } else if (itemobj != null && itemobj.equals(userObject)) {
                return i;
            }
        }
        return -1; 
    }

    protected void addImpl(Component comp, Object constraints, int index) {
        if (comp instanceof TileItem) {
            TileItem ti = (TileItem) comp;
            ti.setAlignment(getAlignment());
            ti.setTextAlignment(getTextAlignment()); 
            super.addImpl(comp, constraints, index); 
        } else {
            throw new IllegalStateException("This container only supports TileItem component. Please use addItem to correct this.");
        }
    }
    
    protected void onselect(Object item) {
    }
    
    private void clearSelection() {
        Component[] comps = getComponents(); 
        for (int i=0; i<comps.length; i++) {
            if (comps[i] instanceof TileItem) {
                TileItem ti = (TileItem)comps[i]; 
                ti.setSelected(false); 
                ti.repaint();
            } 
        } 
    } 
    
    // <editor-fold defaultstate="collapsed" desc=" TileItem ">
    
    class TileItem extends TileLabel  
    {
        private Color selBackground;
        private Color selBorderBackground;
        private Object userObject;
        
        private boolean mouse_entered;
        private boolean selected;
        
        public TileItem(String text, Object userObject) {
            this(text, userObject, null); 
        }
        
        public TileItem(String text, Object userObject, ImageIcon icon) {
            super();
            
            this.userObject = userObject;  
            setText(text);
            setImageIcon(icon); 
            
            selBackground = Color.decode("#c1dcfc");
            selBorderBackground = Color.decode("#7da2ce"); 
//            setBorder(BorderFactory.createEmptyBorder(3,3,0,3));
//            setVerticalAlignment(SwingConstants.TOP); 
//            setHorizontalAlignment(SwingConstants.CENTER);      
//            setVerticalTextPosition(SwingConstants.TOP);
//            setHorizontalTextPosition(SwingConstants.CENTER);             
            new TileItemMouseAdapter(this); 
        } 
        
        public Object getUserObject() { return userObject; } 
        public void setUserObject(Object userObject) {
            this.userObject = userObject; 
        }
                
        public void setTextAlignment(String alignment) {
            if (alignment == null) return;
            if (alignment.equalsIgnoreCase("TOP")) {
                setVerticalTextPosition(SwingConstants.TOP);
                setHorizontalTextPosition(SwingConstants.CENTER); 
            } else if (alignment.equalsIgnoreCase("BOTTOM")) {
                setVerticalTextPosition(SwingConstants.BOTTOM);
                setHorizontalTextPosition(SwingConstants.CENTER); 
            } else if (alignment.equalsIgnoreCase("TOP_LEFT")) {
                setVerticalTextPosition(SwingConstants.TOP);
                setHorizontalTextPosition(SwingConstants.LEFT); 
            } else if (alignment.equalsIgnoreCase("TOP_CENTER")) {
                setVerticalTextPosition(SwingConstants.TOP);
                setHorizontalTextPosition(SwingConstants.CENTER); 
            } else if (alignment.equalsIgnoreCase("TOP_RIGHT")) {
                setVerticalTextPosition(SwingConstants.TOP);
                setHorizontalTextPosition(SwingConstants.RIGHT); 
            } else if (alignment.equalsIgnoreCase("BOTTOM_LEFT")) {
                setVerticalTextPosition(SwingConstants.BOTTOM);
                setHorizontalTextPosition(SwingConstants.LEFT); 
            } else if (alignment.equalsIgnoreCase("BOTTOM_CENTER")) {
                setVerticalTextPosition(SwingConstants.BOTTOM);
                setHorizontalTextPosition(SwingConstants.CENTER); 
            } else if (alignment.equalsIgnoreCase("BOTTOM_RIGHT")) {
                setVerticalTextPosition(SwingConstants.BOTTOM);
                setHorizontalTextPosition(SwingConstants.RIGHT); 
            } else {
                setVerticalTextPosition(SwingConstants.CENTER);
                setHorizontalTextPosition(SwingConstants.CENTER); 
            } 
        }

        public void setAlignment(String alignment) {
            if (alignment == null) return;
            if (alignment.equalsIgnoreCase("TOP")) {
                setVerticalAlignment(SwingConstants.TOP);
                setHorizontalAlignment(SwingConstants.CENTER); 
            } else if (alignment.equalsIgnoreCase("BOTTOM")) {
                setVerticalAlignment(SwingConstants.BOTTOM);
                setHorizontalAlignment(SwingConstants.CENTER); 
            } else if (alignment.equalsIgnoreCase("TOP_LEFT")) {
                setVerticalAlignment(SwingConstants.TOP);
                setHorizontalAlignment(SwingConstants.LEFT); 
            } else if (alignment.equalsIgnoreCase("TOP_CENTER")) {
                setVerticalAlignment(SwingConstants.TOP);
                setHorizontalAlignment(SwingConstants.CENTER); 
            } else if (alignment.equalsIgnoreCase("TOP_RIGHT")) {
                setVerticalAlignment(SwingConstants.TOP);
                setHorizontalAlignment(SwingConstants.RIGHT); 
            } else if (alignment.equalsIgnoreCase("BOTTOM_LEFT")) {
                setVerticalAlignment(SwingConstants.BOTTOM);
                setHorizontalAlignment(SwingConstants.LEFT); 
            } else if (alignment.equalsIgnoreCase("BOTTOM_CENTER")) {
                setVerticalAlignment(SwingConstants.BOTTOM);
                setHorizontalAlignment(SwingConstants.CENTER); 
            } else if (alignment.equalsIgnoreCase("BOTTOM_RIGHT")) {
                setVerticalAlignment(SwingConstants.BOTTOM);
                setHorizontalAlignment(SwingConstants.RIGHT); 
            } else {
                setVerticalAlignment(SwingConstants.CENTER);
                setHorizontalAlignment(SwingConstants.CENTER); 
            } 
        }          
        
        boolean isMouseEntered() { return mouse_entered; } 
        void setMouseEntered(boolean mouse_entered) {
            this.mouse_entered = mouse_entered; 
        }
        
        boolean isSelected() { return selected; } 
        void setSelected(boolean selected) { 
            this.selected = selected; 
        } 

        protected void paintComponent(Graphics g) {
            int width = getWidth();
            int height = getHeight();
            Graphics2D g2 = (Graphics2D) g.create();
            if (isSelected()) {
                g2.setColor(selBackground);
                g2.fillRoundRect(0, 0, width-1, height-1, 3, 3); 
            } else if (isMouseEntered()) {
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.4f)); 
                g2.setColor(selBackground);
                g2.fillRoundRect(0, 0, width-1, height-1, 3, 3); 
            }
            g2.dispose(); 
            
            super.paintComponent(g); 
        }

        public void paint(Graphics g) {
            super.paint(g); 
            
            int width = getWidth();
            int height = getHeight();
            Graphics2D g2 = (Graphics2D) g.create();
            if (isSelected()) {
                g2.setColor(selBorderBackground);
                g2.drawRoundRect(0, 0, width-1, height-1, 3, 3); 
            } else if (isMouseEntered()) {
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.4f)); 
                g2.setColor(selBorderBackground);
                g2.drawRoundRect(0, 0, width-1, height-1, 3, 3);
            } 
            g2.dispose(); 
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" XTileItem ">
    
    class XTileItem extends JLabel 
    {
        private Color selBackground;
        private Color selBorderBackground;
        private String text;
        private Object userObject;
        private ImageIcon icon;
        
        private boolean mouse_entered;
        private boolean selected;
        
        public XTileItem(String text, Object userObject) {
            this(text, userObject, null); 
        }
        
        public XTileItem(String text, Object userObject, ImageIcon icon) {
            super();
            this.text = text; 
            this.icon = icon; 
            this.userObject = userObject;  
            
            if (isShowCaptions()) { 
                setText("<html><center>"+ text +"</center></html>"); 
            }
            selBackground = Color.decode("#c1dcfc");
            selBorderBackground = Color.decode("#7da2ce"); 
            setBorder(BorderFactory.createEmptyBorder(3,3,0,3));
            setVerticalAlignment(SwingConstants.TOP); 
            setHorizontalAlignment(SwingConstants.CENTER);      
            setVerticalTextPosition(SwingConstants.TOP);
            setHorizontalTextPosition(SwingConstants.CENTER);             
            //new TileItemMouseAdapter(this); 
        } 
        
        public Object getUserObject() { return userObject; } 
        public void setUserObject(Object userObject) {
            this.userObject = userObject; 
        }
        
        public Icon getIcon() { return icon; }
        public void setIcon(ImageIcon icon) {
            this.icon = icon; 
        }
        
        public void setTextAlignment(String alignment) {
            if (alignment == null) return;
            if (alignment.equalsIgnoreCase("TOP")) {
                setVerticalTextPosition(SwingConstants.TOP);
                setHorizontalTextPosition(SwingConstants.CENTER); 
            } else if (alignment.equalsIgnoreCase("BOTTOM")) {
                setVerticalTextPosition(SwingConstants.BOTTOM);
                setHorizontalTextPosition(SwingConstants.CENTER); 
            } else if (alignment.equalsIgnoreCase("TOP_LEFT")) {
                setVerticalTextPosition(SwingConstants.TOP);
                setHorizontalTextPosition(SwingConstants.LEFT); 
            } else if (alignment.equalsIgnoreCase("TOP_CENTER")) {
                setVerticalTextPosition(SwingConstants.TOP);
                setHorizontalTextPosition(SwingConstants.CENTER); 
            } else if (alignment.equalsIgnoreCase("TOP_RIGHT")) {
                setVerticalTextPosition(SwingConstants.TOP);
                setHorizontalTextPosition(SwingConstants.RIGHT); 
            } else if (alignment.equalsIgnoreCase("BOTTOM_LEFT")) {
                setVerticalTextPosition(SwingConstants.BOTTOM);
                setHorizontalTextPosition(SwingConstants.LEFT); 
            } else if (alignment.equalsIgnoreCase("BOTTOM_CENTER")) {
                setVerticalTextPosition(SwingConstants.BOTTOM);
                setHorizontalTextPosition(SwingConstants.CENTER); 
            } else if (alignment.equalsIgnoreCase("BOTTOM_RIGHT")) {
                setVerticalTextPosition(SwingConstants.BOTTOM);
                setHorizontalTextPosition(SwingConstants.RIGHT); 
            } else {
                setVerticalTextPosition(SwingConstants.CENTER);
                setHorizontalTextPosition(SwingConstants.CENTER); 
            } 
        }

        public void setAlignment(String alignment) {
            if (alignment == null) return;
            if (alignment.equalsIgnoreCase("TOP")) {
                setVerticalAlignment(SwingConstants.TOP);
                setHorizontalAlignment(SwingConstants.CENTER); 
            } else if (alignment.equalsIgnoreCase("BOTTOM")) {
                setVerticalAlignment(SwingConstants.BOTTOM);
                setHorizontalAlignment(SwingConstants.CENTER); 
            } else if (alignment.equalsIgnoreCase("TOP_LEFT")) {
                setVerticalAlignment(SwingConstants.TOP);
                setHorizontalAlignment(SwingConstants.LEFT); 
            } else if (alignment.equalsIgnoreCase("TOP_CENTER")) {
                setVerticalAlignment(SwingConstants.TOP);
                setHorizontalAlignment(SwingConstants.CENTER); 
            } else if (alignment.equalsIgnoreCase("TOP_RIGHT")) {
                setVerticalAlignment(SwingConstants.TOP);
                setHorizontalAlignment(SwingConstants.RIGHT); 
            } else if (alignment.equalsIgnoreCase("BOTTOM_LEFT")) {
                setVerticalAlignment(SwingConstants.BOTTOM);
                setHorizontalAlignment(SwingConstants.LEFT); 
            } else if (alignment.equalsIgnoreCase("BOTTOM_CENTER")) {
                setVerticalAlignment(SwingConstants.BOTTOM);
                setHorizontalAlignment(SwingConstants.CENTER); 
            } else if (alignment.equalsIgnoreCase("BOTTOM_RIGHT")) {
                setVerticalAlignment(SwingConstants.BOTTOM);
                setHorizontalAlignment(SwingConstants.RIGHT); 
            } else {
                setVerticalAlignment(SwingConstants.CENTER);
                setHorizontalAlignment(SwingConstants.CENTER); 
            } 
        }          
        
        boolean isMouseEntered() { return mouse_entered; } 
        void setMouseEntered(boolean mouse_entered) {
            this.mouse_entered = mouse_entered; 
        }
        
        boolean isSelected() { return selected; } 
        void setSelected(boolean selected) { 
            this.selected = selected; 
        } 

        protected void paintComponent(Graphics g) {
            int width = getWidth();
            int height = getHeight();
            Graphics2D g2 = (Graphics2D) g.create();
            if (isSelected()) {
                g2.setColor(selBackground);
                g2.fillRoundRect(0, 0, width-1, height-1, 3, 3); 
            } else if (isMouseEntered()) {
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.4f)); 
                g2.setColor(selBackground);
                g2.fillRoundRect(0, 0, width-1, height-1, 3, 3); 
            }
            g2.dispose(); 
            
            super.paintComponent(g); 
        }

        public void paint(Graphics g) {
            super.paint(g); 
            
            int width = getWidth();
            int height = getHeight();
            Graphics2D g2 = (Graphics2D) g.create();
            if (isSelected()) {
                g2.setColor(selBorderBackground);
                g2.drawRoundRect(0, 0, width-1, height-1, 3, 3); 
            } else if (isMouseEntered()) {
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.4f)); 
                g2.setColor(selBorderBackground);
                g2.drawRoundRect(0, 0, width-1, height-1, 3, 3);
            } 
            g2.dispose(); 
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" TileItemMouseAdapter ">
    
    private class TileItemMouseAdapter implements MouseListener
    {
        private TileItem source; 
        private boolean pressed;
        private boolean processing;
        
        TileItemMouseAdapter(TileItem source) {
            this.source = source;
            source.addMouseListener(this); 
        }

        public void mouseClicked(MouseEvent e) {
        }

        public void mousePressed(MouseEvent e) {
            pressed = true; 
            clearSelection();
            source.setSelected(true);
            source.repaint();
        }

        public void mouseReleased(MouseEvent e) {
            if (pressed) {
                pressed = false; 
                clearSelection();
                source.setSelected(true);
                source.repaint();
                
                try {
                    Thread.currentThread().sleep(68);
                } catch(InterruptedException ie) {;}
                
                if (e.getClickCount() == 2) {
                    //do not process when double-click 
                    return; 
                }
                
                EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        doClick();
                    }
                });
            }
        }

        public void mouseEntered(MouseEvent e) {
            Component[] comps = getComponents(); 
            for (int i=0; i<comps.length; i++) {
                if (comps[i] instanceof TileItem) {
                    TileItem ti = (TileItem)comps[i]; 
                    ti.setMouseEntered(false); 
                    ti.repaint();
                }
            }
            source.setMouseEntered(true); 
            source.repaint(); 
        }

        public void mouseExited(MouseEvent e) {
            if (pressed) {
                pressed = false; 
                source.setSelected(false); 
            }
            source.setMouseEntered(false); 
            source.repaint(); 
        }
        
        void doClick() {
            selectedItem = source.getUserObject(); 
            onselect(selectedItem); 
        }
    }
    
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" ContainerLayout (Class) ">
    
    private class ContainerLayout implements LayoutManager {
        public void addLayoutComponent(String name, Component comp) {}
        public void removeLayoutComponent(Component comp) {}
        
        private TileItem[] getVisibleComponents(Container parent) {
            ArrayList<TileItem> list = new ArrayList();
            Component[] comps = parent.getComponents();
            for (int i=0; i<comps.length; i++) {
                if (comps[i] instanceof TileItem) {
                    list.add((TileItem) comps[i]); 
                } 
            }
            return list.toArray(new TileItem[]{}); 
        }
        
        public Dimension getLayoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                Dimension newdim = new Dimension(0, 0);
                Dimension celldim = getCellSize();
                Insets margin = parent.getInsets();
                TileItem[] items = getVisibleComponents(parent); 
                for (int i=0; i<items.length; i++) {
                    if ( i > 0 ) newdim.width += Math.max(getCellSpacing(), 0); 
                    
                    TileItem c = items[i];
                    newdim.width += celldim.width;
                    newdim.height = celldim.height;
                }

                Insets pads = getPadding();
                if (pads != null) {
                    newdim.width += (margin.left + margin.right + pads.left + pads.right);
                    newdim.height += (margin.top + margin.bottom + pads.top + pads.bottom);
                }
                return newdim; 
            }
        }
        
        public Dimension preferredLayoutSize(Container parent) {
            return getLayoutSize(parent);
        }
        
        public Dimension minimumLayoutSize(Container parent) {
            return getLayoutSize(parent);
        }
        
        public void layoutContainer(Container parent) {
            synchronized (parent.getTreeLock()) {
                Insets pads = getPadding();
                if (pads == null) pads = new Insets(0,0,0,0); 
                
                Dimension celldim = getCellSize();
                Insets margin = parent.getInsets();
                int x = margin.left + pads.left;
                int y = margin.top + pads.top;
                int w = parent.getWidth() - (margin.left + margin.right + pads.left + pads.right);
                int h = parent.getHeight() - (margin.top + margin.bottom + pads.top + pads.bottom);
                int rb = parent.getWidth() - (margin.right + pads.right);
                
                boolean firstItemInRow = true; 
                TileItem[] items = getVisibleComponents(parent);
                for (int i=0; i<items.length; i++) {
                    if (i > 0) {
                        x += Math.max(getCellSpacing(), 0); 
                    }
                    
                    TileItem c = items[i];                    
                    if (firstItemInRow) {
                        firstItemInRow = false;                         
                        c.setSize(celldim.width, celldim.height); 
                        c.setLocation(x, y); 
                    } else if (x + celldim.width > rb) {
                        y += (celldim.height + Math.max(getCellSpacing(), 0));
                        x = margin.left + pads.left;
                        firstItemInRow = false;    
                        c.setSize(celldim.width, celldim.height); 
                        c.setLocation(x, y); 
                    } else {
                        c.setSize(celldim.width, celldim.height); 
                        c.setLocation(x, y); 
                    }
                    x += celldim.width; 
                }
            }
        }
    }
    
    //</editor-fold>        
}
