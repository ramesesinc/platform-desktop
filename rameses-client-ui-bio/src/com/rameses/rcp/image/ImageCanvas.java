/*
 * ImageCanvas.java
 *
 * Created on December 23, 2013, 12:39 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.image;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 *
 * @author wflores
 */
class ImageCanvas extends JLabel 
{
    private Dimension cropSize;
    private ImageIcon iicon;
    private int iconWidth;
    private int iconHeight;
    private BufferedImage bufferedImage;
    
    private Point dragPoint;
    private Rectangle rectOver;
    private Point imagePoint;
    
    public ImageCanvas() {
        super("Image Canvas"); 
        
        MouseSupport mouseSupport = new MouseSupport();
        addMouseListener(mouseSupport); 
        addMouseMotionListener(mouseSupport);
        addMouseWheelListener(mouseSupport); 
        addComponentListener(new ComponentListenerImpl());
        
        cropSize = new Dimension(2, 2); 
    }
    
    public void setData(Object data) {
        if (data == null) {
            iicon = null; 
        } else if (data instanceof File) {
            File file = (File) data; 
            iicon = new ImageIcon(file.getAbsolutePath());
        } else if (data instanceof URL) {
            URL url = (URL) data;
            iicon = new ImageIcon(url); 
        } else {
            iicon = new ImageIcon((byte[]) data); 
        } 
        bufferedImage = null; 
        iconWidth = (iicon == null? 0: iicon.getIconWidth()); 
        iconHeight = (iicon == null? 0: iicon.getIconHeight()); 
    }
    
    private BufferedImage getBufferedImage() {
        if (bufferedImage == null) {
            Image image = (iicon == null? null: iicon.getImage()); 
            if (image == null) return null; 
            
            bufferedImage = new BufferedImage(iicon.getIconWidth(), iicon.getIconHeight(), BufferedImage.TYPE_INT_ARGB); 
            Graphics2D g2 = bufferedImage.createGraphics(); 
            g2.drawImage(image, 0, 0, null); 
        }
        return bufferedImage;
    }
    
    private BufferedImage createBufferedImage(double ratio) {
        BufferedImage bi = null; 
        if (iicon != null) { 
            int nw = (int) (iicon.getIconWidth() * ratio);
            int nh = (int) (iicon.getIconHeight() * ratio);            
            bi = new BufferedImage(nw, nh, BufferedImage.TYPE_INT_ARGB); 

            Graphics2D g2 = bi.createGraphics(); 
            g2.drawImage(iicon.getImage(), 0, 0, nw, nh, null); 
            iconWidth = nw;
            iconHeight = nh;
        } else {
            iconWidth = 0;
            iconHeight = 0;
        }
        this.bufferedImage = bi;
        return bi; 
    }

    public void paint(Graphics g) {
        super.paint(g);         
    }

    protected void paintComponent(Graphics g) { 
        super.paintComponent(g); 
        
        int width = getWidth();
        int height = getHeight();
        // gets the current clipping area
        Rectangle clip = g.getClipBounds();
        g.clearRect(0, 0, width, height); 
        
        // paint the image
        int sx=0, sy=0;        
        BufferedImage bic = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);        
        BufferedImage bi = getBufferedImage(); 
        if (bi != null) {
            sx = (width - iconWidth) / 2;
            sy = (height - iconHeight) / 2;
            Graphics2D g2b = (Graphics2D) bic.createGraphics();         
            g2b.drawImage(bi, sx, sy, null);
            g2b.dispose();
        } 
        imagePoint = new Point(sx, sy); 
        g.drawImage(bic, 0, 0, null); 
        
        Color oldColor = g.getColor();        
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
        // sets a 80% translucent composite
        AlphaComposite alpha = AlphaComposite.SrcOver.derive(0.63f);
        Composite oldComposite = g2.getComposite();
        g2.setComposite(alpha);
        
        // fills the background
        g2.setColor(Color.BLACK);
        g2.fillRect(clip.x, clip.y, clip.width, clip.height);                
        g2.setComposite(oldComposite);
        g2.setColor(oldColor); 
        
        if (bi != null) {
            int cx = 0;
            int cy = 0;
            int cw = cropSize.width * 72;
            int ch = cropSize.height * 72;
            if (rectOver == null) {
                cx = Math.max((width-cw)/2, 0);
                cy = Math.max((height-ch)/2, 0);
                rectOver = new Rectangle(cx, cy, cw, ch); 
            } else {
                cx = rectOver.x;
                cy = rectOver.y;
            } 
            
            int bw = rectOver.width;
            int bh = rectOver.height;
//            if (rectOver.x + rectOver.width > iconWidth) { 
//                bw = Math.max(iconWidth - rectOver.x, 0);
//            }
//            if (rectOver.y + rectOver.height > iconHeight) {
//                bh = Math.max(iconHeight - rectOver.y, 0);
//            } 
            
            Graphics g3 = (Graphics2D) g2.create(cx, cy, cw, ch); 
            Image img3 = bic.getSubimage(rectOver.x, rectOver.y, bw, bh); 
            g3.drawImage(img3, 0, 0, null); 
            g3.setColor(Color.WHITE); 
            g3.drawRect(0, 0, cw-1, ch-1); 
            g3.dispose(); 
        } 
    }
    
    
    // <editor-fold defaultstate="collapsed" desc=" MouseSupport ">
    
    private class MouseSupport implements MouseListener, MouseMotionListener, MouseWheelListener 
    {
        ImageCanvas root = ImageCanvas.this;
        
        private Point origPoint;
        private Point basePoint;
        
        public void mouseEntered(MouseEvent e) {}        
        public void mousePressed(MouseEvent e) {
            if (hasIntersect(e)) { 
                basePoint = e.getPoint();
                origPoint = new Point(rectOver.x, rectOver.y);
            } else {
                basePoint = null;
                origPoint = null;
            }
        }
        
        public void mouseReleased(MouseEvent e) {
            basePoint = null;
            origPoint = null;
        }
        
        public void mouseClicked(MouseEvent e) {} 
        public void mouseExited(MouseEvent e) {}
        public void mouseMoved(MouseEvent e) {}
        
        public void mouseDragged(MouseEvent e) {
            if (basePoint == null) return;
            
            int pw = root.getWidth();
            int ph = root.getHeight();
            int nx=0, ny=0;
            Point p = e.getPoint();
            if (p.x >= basePoint.x) { 
                nx = p.x-basePoint.x;  
                if (origPoint.x + nx  + rectOver.width < pw) { 
                    rectOver.x = origPoint.x + nx; 
                } 
            } else {
                nx = basePoint.x - p.x;
                rectOver.x = Math.max(origPoint.x - nx, 0); 
            }         
            
            if (p.y >= basePoint.y) {
                ny = p.y - basePoint.y; 
                if (origPoint.y + ny + rectOver.height < ph) { 
                    rectOver.y = origPoint.y + ny; 
                } 
            } else {
                ny = basePoint.y - p.y; 
                rectOver.y = Math.max(origPoint.y - ny, 0); 
            }
            root.repaint(); 
        }                 
        
        private boolean hasIntersect(MouseEvent e) {
            if (root.rectOver == null) return false;
            
            Point p = e.getPoint();
            return rectOver.contains(p); 
        }

        
        
        private double[] BASE_FACTORS = {
            6, 12, 25, 50, 66, 75, 100, 
            106, 112, 125, 150, 166, 175, 200, 
            250, 275, 300
        }; 
        private int baseFactorIndex = 6;
        private double scaleFactor = 1.0D;
        
        public void mouseWheelMoved(MouseWheelEvent e) {
            if (!e.isControlDown()) return;
            if (root.iicon == null) return;

            int oldBaseFactorIndex = baseFactorIndex;
            double oldScaleFactor = scaleFactor;
            try { 
                if (e.getWheelRotation() > 0) {
                    //zoom out
                    scaleFactor = BASE_FACTORS[baseFactorIndex-1] / 100.0;
                    baseFactorIndex -= 1;
                } else {
                    //zoom in
                    scaleFactor = BASE_FACTORS[baseFactorIndex+1] / 100.0;
                    baseFactorIndex += 1;
                } 
            } catch(Throwable t) {
                return; 
            }
            
            BufferedImage oldImage = root.getBufferedImage(); 
            try { 
                root.createBufferedImage(scaleFactor); 
                if (oldImage != null) oldImage.flush(); 
            } catch(Throwable t) {
                //error might be OutOfMemoryError 
                baseFactorIndex = oldBaseFactorIndex;
                scaleFactor = oldScaleFactor;
            } 
            root.repaint(); 
        }
    }

    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" ComponentListenerImpl ">
    
    private class ComponentListenerImpl implements ComponentListener 
    {
        ImageCanvas root = ImageCanvas.this;
        
        public void componentMoved(ComponentEvent e) {}
        public void componentShown(ComponentEvent e) {}
        public void componentHidden(ComponentEvent e) {}

        public void componentResized(ComponentEvent e) {
//            int width = root.getWidth();
//            int height = root.getHeight();
//            root.canvasImage =new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);  
        }        
    }
    
    // </editor-fold>
    
}
