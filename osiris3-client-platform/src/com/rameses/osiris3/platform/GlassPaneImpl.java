/*
 * GlassPaneImpl.java
 *
 * Created on October 30, 2013, 10:48 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.platform;

import com.rameses.platform.interfaces.SubWindow;
import com.rameses.platform.interfaces.SubWindowListener;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
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
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.basic.BasicInternalFrameUI;

/**
 *
 * @author wflores
 */
class GlassPaneImpl extends JPanel implements SubWindow, OSView 
{
    private OSMainWindow mainWindow;
    
    public GlassPaneImpl(OSMainWindow mainWindow) {
        this.mainWindow = mainWindow;
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
        //AlphaComposite alpha = AlphaComposite.SrcOver.derive(0.75f);
        Composite oldComposite = g2.getComposite();
        AlphaComposite acomposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.75f);         
        g2.setComposite(acomposite);
        
        // fills the background
        g2.setColor(getBackground());
        g2.fillRect(clip.x, clip.y, clip.width, clip.height);                
        g2.setComposite(oldComposite);
    }    

    public void setVisible(boolean visible) {
        setFocusable(visible);         
        super.setVisible(visible);
        if (visible) {
            requestFocus(); 
            
            Component comp = getContent();
            if (comp != null && comp.isFocusable()) { 
                comp.requestFocus();
            } 
            
            if (comp instanceof Container) {
                Container con = (Container) comp;
                con.setFocusCycleRoot(true); 
                con.transferFocusDownCycle();
            } else {
                setFocusCycleRoot(true); 
            }
        } else {
            Component comp = getContent();
            if (comp instanceof Container) {
                Container con = (Container) comp;
                con.setFocusCycleRoot(false); 
            }
        }
    } 
    
    public Component getContent() {
        Component[] comps = getComponents(); 
        if (comps.length > 0) return comps[0]; 
        
        return null; 
    }

    protected void addImpl(Component comp, Object constraints, int index) { 
        if (comp instanceof InnerView) {
            super.addImpl(comp, constraints, index); 
        } else { 
            JComponent jcomp = (JComponent) comp; 
            Map props = (Map) jcomp.getClientProperty("Window.properties");
            if (props == null) props = new HashMap();
            
            Object id = props.get("id");
            if (id == null || id.toString().trim().length() == 0) {
                super.addImpl(comp, constraints, index); 
                return; 
            }
            
            Object title = props.get("title");
            Object target = props.get("target");
            
            InnerView iw = null;
            if ("popup".equals(target+"")) { 
                iw = new InnerWindow(); 
            } else {
                iw = new InnerPanel();
            }
            
            iw.setTitle(title == null? "": title.toString()); 
            iw.setContentPane(jcomp); 
            iw.setVisible(true); 
            super.addImpl((Component)iw, constraints, index); 
        } 
    }
    
    
    // <editor-fold defaultstate="collapsed" desc=" SubWindow implementation ">
    
    private String title;
    private SubWindowListener subWindowListener;
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public void setListener(SubWindowListener listener) {
        this.subWindowListener = listener; 
    }

    public void update(Map attrs) {
    }
    
    public void closeWindow() { 
        setVisible(false); 
        removeAll();
        OSManager.getInstance().unregisterView(getName()); 
        
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                KeyboardFocusManager kfm = KeyboardFocusManager.getCurrentKeyboardFocusManager(); 
                kfm.clearGlobalFocusOwner();
                mainWindow.requestFocus(); 
            }
        });
    }
    
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" OSView implementation ">
    
    public String getId() {
        return getName(); 
    }
    
    public String getType() {
        return "glasspane"; 
    }
    
    public void requestFocus() {
        super.requestFocus(); 
    }
    
    public void closeView() {
        closeWindow(); 
    }

    public WindowContainer getWindowContainer() {
        return null; 
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" InnerView ">
    
    private interface InnerView 
    {
        void setTitle(String title); 
        void setContentPane(Container con); 
        void setVisible(boolean visible); 
        
        Object getClientProperty(Object key); 
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" InnerWindow ">
    
    private class InnerWindow extends JInternalFrame implements InnerView 
    {
        InnerWindow() {
            setFrameIcon(createDefaultIcon()); 
            try { 
                JComponent northpane = ((BasicInternalFrameUI) getUI()).getNorthPane(); 
                MouseMotionListener[] listeners = northpane.getMouseMotionListeners(); 
                for (MouseMotionListener mml: listeners) {
                    northpane.removeMouseMotionListener(mml); 
                } 
            } catch(Throwable t) {;} 
        }
        
        private ImageIcon createDefaultIcon() {
            BufferedImage bi = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = (Graphics2D) bi.createGraphics(); 
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR));
            g2.fillRect(0, 0, 1, 1); 
            g2.dispose(); 
            return new ImageIcon(bi); 
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" InnerPanel ">
    
    private class InnerPanel extends JPanel implements InnerView 
    {
        InnerPanel() {
            setLayout(new InnerPanelLayout()); 
            setBorder(new InnerPanelBorder()); 
        }
        
        public void setTitle(String title) {}
        
        public void setContentPane(Container con) {
            if (con != null) add(con); 
        }

        protected void addImpl(Component comp, Object constraints, int index) {
            removeAll();
            super.addImpl(comp, constraints, -1); 
        }
    }

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" InnerPanelBorder ">
    
    private class InnerPanelBorder extends AbstractBorder 
    {
        public boolean isBorderOpaque() { return true; }
        
        public Insets getBorderInsets(Component c)       {
            return new Insets(2, 2, 2, 2);
        }

        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.top = insets.right = insets.bottom = 2;
            return insets;
        } 
        
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Color oldColor = g.getColor();
            g.setColor(getShadowOuterColor(c));
            g.drawRect(0, 0, w-1, h-1); 
            g.setColor(getHightlightInnerColor(c)); 
            g.drawLine(1, 1, 1, h-2); 
            g.drawLine(1, 1, w-2, 1); 
            g.setColor(getShadowInnerColor(c)); 
            g.drawLine(w-2, 1, w-2, h-2); 
            g.drawLine(1, h-2, w-2, h-2); 
            g.setColor(oldColor);
        }  

        private Color getShadowOuterColor(Component c) {
            return c.getBackground().darker().darker();
        }   
        
        private Color getShadowInnerColor(Component c) {
            return c.getBackground().darker();
        }           
        
        private Color getHightlightInnerColor(Component c) {
            return c.getBackground().brighter(); 
        }          
    }
    
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" InnerPanelLayout ">
    
    private class InnerPanelLayout implements LayoutManager
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
                if (comp == null) return;
                
                Dimension dim = comp.getPreferredSize();
                comp.setBounds(x, y, w, h); 
            } 
        }        
    }
    
    // </editor-fold>
    
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
