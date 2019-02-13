/*
 * OSScreenLock.java
 *
 * Created on October 31, 2013, 10:39 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.platform;

import com.rameses.platform.interfaces.SubWindow;
import com.rameses.platform.interfaces.SubWindowListener;
import com.rameses.platform.interfaces.ViewContext;
import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.RootPaneContainer;
import javax.swing.plaf.basic.BasicInternalFrameUI;

/**
 *
 * @author wflores
 */
class OSScreenLock extends JDialog implements SubWindow, WindowListener  
{
    private OSGlassPane glasspane;
    private ContentPaneImpl contentpane;
    private ViewContext viewContext; 
    private OSView osview;
    private ImageIcon frameIcon;
    
    public OSScreenLock() {
        super();
        setGlassPane(glasspane = new OSGlassPane()); 
        setContentPane(contentpane = new ContentPaneImpl()); 
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(this);        
    }

    void showGlassPane() {
        glasspane.setVisible(true); 
    }
    void hideGlassPane() {
        glasspane.setVisible(false); 
    }
    void setContent(Component comp) {
        glasspane.removeAll();
        if (comp != null) {
            glasspane.add(createComponentHolder(comp));
            if (comp instanceof ViewContext) {
                viewContext = (ViewContext) comp;
            }
        } 
        glasspane.revalidate();
        glasspane.repaint(); 
    }
    
    Component getContent() {
        return glasspane.getContent(); 
    }

    public void setVisible(boolean visible) {
        if (visible) {
            setModal(true); 
            setUndecorated(true); 
            glasspane.setVisible(true); 
            updateDimension();
        }
        super.setVisible(visible);
    }
    
    private void updateDimension() {
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension dim = tk.getScreenSize(); 
        Insets margin = tk.getScreenInsets(getGraphicsConfiguration()); 
        dim.width -= (margin.left + margin.right);
        dim.height -= (margin.top + margin.bottom);
        setBounds(0, 0, dim.width, dim.height); 
    }
    
    void updateBackgroundImage() {
        contentpane.updateBackgroundImage(); 
    }
    
    Component createComponentHolder(Component comp) {
        JInternalFrame iframe = new JInternalFrame();
        iframe.setFrameIcon(getFrameIcon()); 
        
        try { 
            JComponent northpane = ((BasicInternalFrameUI) iframe.getUI()).getNorthPane(); 
            MouseMotionListener[] listeners = northpane.getMouseMotionListeners(); 
            for (MouseMotionListener mml: listeners) {
                northpane.removeMouseMotionListener(mml); 
            } 
        } catch(Throwable t) {;} 
            
        iframe.setContentPane((Container) comp);
        iframe.setVisible(true); 
        return iframe; 
    } 
    
    private ImageIcon getFrameIcon() {
        if (frameIcon == null) {
            BufferedImage bi = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = (Graphics2D) bi.createGraphics(); 
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR));
            g2.fillRect(0, 0, 16, 16); 
            g2.dispose(); 
            frameIcon = new ImageIcon(bi); 
        }
        return frameIcon;
    }
    
    
    // <editor-fold defaultstate="collapsed" desc=" SubWindow implementation ">

    public String getName() {  
        return super.getName(); 
    } 
        
    public void setListener(SubWindowListener listener) {}
    
    public void closeWindow() {
        if (viewContext != null) viewContext.close(); 
        
        dispose();
        OSManager osm = OSManager.getInstance();
        osm.unregisterView(getName());   
        osm.setScreenLock(null); 
        glasspane.removeAll(); 
        contentpane.image = null; 
    }    
    
    public void update(Map windowAttributes) {} 
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" WindowListener implementation ">
    
    public void windowClosed(WindowEvent e) {}
    public void windowIconified(WindowEvent e) {}
    public void windowDeiconified(WindowEvent e) {}
    public void windowActivated(WindowEvent e) {}
    public void windowDeactivated(WindowEvent e) {}
    
    public void windowClosing(WindowEvent e) {
        //closeWindow();
    }
    
    public void windowOpened(WindowEvent e) { 
        OSManager osm = OSManager.getInstance();
        osm.setScreenLock(this);
        
        if (viewContext != null) { 
            viewContext.display(); 
            viewContext.setSubWindow(this); 
        }
        if (osview == null) {
            osview = new OSViewImpl();
            osm.registerView(getName(), osview); 
        }
    }
    
    // </editor-fold> 
    
    // <editor-fold defaultstate="collapsed" desc=" OSView support ">
    
    private class OSViewImpl implements OSView 
    {
        OSScreenLock root = OSScreenLock.this;
        
        public String getId() {
            return root.getName();
        }
        
        public String getType() {
            return "screenlock"; 
        }
        
        public void requestFocus() { 
            Component comp = root.getContent(); 
            if (comp instanceof RootPaneContainer) {
                Container con = (Container) comp;
                con.requestFocus(); 
                con.setFocusCycleRoot(true); 
                con.transferFocus();                 
            } else { 
                root.requestFocus(); 
            } 
        } 

        public void closeView() {
            root.closeWindow(); 
        } 
        
        public WindowContainer getWindowContainer() {
            return null; 
        }        
    }
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" ContentPaneImpl ">
    
    private class ContentPaneImpl extends JPanel 
    {
        OSScreenLock root = OSScreenLock.this;
        private Image image;
        
        ContentPaneImpl() {
            root.addComponentListener(new ComponentListener() {
                public void componentHidden(ComponentEvent e) {
                }
                public void componentMoved(ComponentEvent e) {
                    root.repaint();
                }
                public void componentResized(ComponentEvent e) {
                    root.repaint();
                }
                public void componentShown(ComponentEvent e) {
                    root.repaint();
                }
            }); 
            root.addWindowFocusListener(new WindowFocusListener() {
                public void windowGainedFocus(WindowEvent e) { 
                    repaintImpl(); 
                }
                public void windowLostFocus(WindowEvent e) {
                    repaintImpl(); 
                }
                void repaintImpl() { 
                    if (root.isVisible()) root.repaint(); 
                }
            });
        }
        
        void updateBackgroundImage() {
            try {
                Robot rbt = new Robot();
                Toolkit tk = Toolkit.getDefaultToolkit();
                Dimension dim = tk.getScreenSize();
                image = rbt.createScreenCapture(new Rectangle(0, 0, dim.width, dim.height));
            } catch (Throwable ex) { 
                image = null;
                ex.printStackTrace();
            }
        }    
        
        public void paintComponent(Graphics g) {
            Point pos = getLocationOnScreen();
            Point offset = new Point(-pos.x,-pos.y);
            g.drawImage(image,offset.x,offset.y,null);
        }        
    }
    
    // </editor-fold>         
}
