/*
 * XDialog.java
 *
 * Created on October 31, 2013, 10:39 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package test;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import javax.swing.JDialog;
import javax.swing.JPanel;

/**
 *
 * @author wflores
 */
public class XDialog extends JDialog 
{
    private XGlassPane glasspane;
    private ContentPaneImpl contentpane;
    
    public XDialog() {
        super();
        setModal(true); 
        setGlassPane(glasspane = new XGlassPane()); 
        setContentPane(contentpane = new ContentPaneImpl()); 
    }

    void showGlassPane() {
        glasspane.setVisible(true); 
    }
    void hideGlassPane() {
        glasspane.setVisible(false); 
    }
    void setGlassPaneContent(Component comp) {
        glasspane.removeAll();
        if (comp == null) return;
        
        glasspane.add(comp);
    }

    public void setVisible(boolean visible) {
        if (visible) updateDimension();

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
    
    // <editor-fold defaultstate="collapsed" desc=" ContentPaneImpl ">
    
    private class ContentPaneImpl extends JPanel 
    {
        XDialog root = XDialog.this;
        private Image image;
        
        ContentPaneImpl() {
            updateBackgroundImage();
            root.addComponentListener(new ComponentListener() {
                public void componentHidden(ComponentEvent e) {
                }
                public void componentMoved(ComponentEvent e) {
                    repaint();
                }
                public void componentResized(ComponentEvent e) {
                    repaint();
                }
                public void componentShown(ComponentEvent e) {
                    repaint();
                }
            }); 
            root.addWindowFocusListener(new WindowFocusListener() {
                public void windowGainedFocus(WindowEvent e) { 
                    System.out.println("windowGainedFocus");
                    repaintImpl(); 
                }
                public void windowLostFocus(WindowEvent e) {
                    System.out.println("windowLostFocus");
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
