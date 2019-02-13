/*
 * ImageView.java
 *
 * Created on December 4, 2013, 11:10 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control.image;

import com.rameses.rcp.framework.Binding;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

/**
 *
 * @author wflores
 */
public class ImageView extends JLabel   
{
    private Binding binding;
    private String[] depends;
    private int index; 
    
    private ImageIcon iicon; 
    
    private NoImageCanvas noImageCanvas;
    private ImageCanvas imageCanvas;
    
    private Color noImageBackground;
    private Color noImageForeground; 
        
    private double ZOOM_FACTOR = 0.10;
    private Dimension fitSize;
    private Rectangle image_rect;
    private Rectangle drag_rect;
    private boolean zoom_mode;
    
    private Vector<ImageView.Handler> handlers;
    
    public ImageView() { 
        this(null);
    }
    
    public ImageView(ImageIcon iicon) { 
        this.iicon = iicon;
        handlers = new Vector();
        initComponent();
    }    
    
    // <editor-fold defaultstate="collapsed" desc=" initComponent "> 
    
    private void initComponent() {
        super.setLayout(new DefaultLayout()); 
        setOpaque(false); 
        setPreferredSize(new Dimension(120, 100));
        setNoImageBackground(Color.BLACK);
        setNoImageForeground(Color.WHITE); 
        
        noImageCanvas = new NoImageCanvas(); 
        imageCanvas = new ImageCanvas(); 

        Font oldFont = getFont(); 
        if (oldFont != null) {
            setFont(oldFont.deriveFont(Font.BOLD, 14.0f));
        } 

        addComponentListener(new ComponentListenerImpl()); 
        MouseAdapterImpl mouseAdapter = new MouseAdapterImpl();
        addMouseListener(mouseAdapter);
        addMouseWheelListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter); 
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Getters/Setters ">     
    
    public void setLayout(LayoutManager mgr) {}
    
    public void setFont(Font font) {
        super.setFont(font);
        if (noImageCanvas != null) noImageCanvas.setFont(font); 
    }
    
    public Color getNoImageBackground() { return noImageBackground; } 
    public void setNoImageBackground(Color noImageBackground) { 
        this.noImageBackground = noImageBackground; 
    } 
    
    public Color getNoImageForeground() { return noImageForeground; } 
    public void setNoImageForeground(Color noImageForeground) { 
        this.noImageForeground = noImageForeground; 
    } 
    
    public ImageIcon getValue() { return iicon; }
    public void setValue(ImageIcon iicon) { 
        this.iicon = iicon; 
        image_rect = null;
        zoom_mode = false;
    }
    
    public void removeHandler(ImageView.Handler handler) {
        if (handler != null) handlers.remove(handler); 
    }
    public void addHandler(ImageView.Handler handler) {
        if (handler == null) return;
        if (!handlers.contains(handler)) {
            handlers.addElement(handler); 
        }
    }
    
    public final double getMinScale() {
        ImageIcon iicon = getValue();
        if (iicon == null) return 0.0;

        int iw = iicon.getIconWidth();
        int ih = iicon.getIconHeight(); 
        int width = getWidth();
        int height = getHeight();
        double scaleX = (double)width  / (double)iw;
        double scaleY = (double)height / (double)ih;
        return (scaleY > scaleX)? scaleX: scaleY;        
    }
    public final double getCurrentScale() {
        ImageIcon iicon = getValue();
        if (iicon == null) return 0.0;

        Rectangle rect = cloneImageRect();
        if (rect == null) return 0.0;
        
        double iw = (double)iicon.getIconWidth();
        double rw = (double)rect.width; 
        double scale = (rw / iw);
        return scale;
    }
    public final double getNextScale() {
        return 0.0;
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" helper/override methods "> 
    
    protected void paintNoImage(Graphics2D g2, int width, int height) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setBackground(Color.BLACK);
        g2.fillRect(0, 0, width, height);

        g2.setColor(Color.DARK_GRAY);
        g2.setStroke(new BasicStroke(3));
        g2.drawLine(0, 0, width-1, height-1);
        g2.drawLine(0, height-1, width-1, 0);

        String str = "No Available"; 
        FontMetrics metrics = g2.getFontMetrics(getFont());
        int fw = metrics.stringWidth(str);
        int fh = metrics.getHeight();
        int x = Math.max(((width - fw) / 2), 0);
        int y = Math.max(((height / 2) - (fh / 2)), 0) + 4;
        
        g2.setFont(getFont());
        g2.setColor(Color.WHITE);
        g2.drawString(str, x, y);
        
        str = "Photo";
        fw = metrics.stringWidth(str);
        fh = metrics.getHeight();        
        x = Math.max(((width - fw) / 2), 0);
        y = Math.max(((height / 2) + (fh / 2)), 0) + 4;
        g2.drawString(str, x, y);
    }
    
    protected void paintImage(Graphics2D g2, int width, int height, ImageIcon anIcon) {
        g2.setBackground(getBackground()); 
        g2.clearRect(0, 0, width, height);
        g2.setBackground(Color.YELLOW);
        g2.fillRect(0, 0, width, height); 
        g2.drawImage(anIcon.getImage(), 0, 0, null); 
    }
    
    private Rectangle getFitViewRect() {
        ImageIcon iicon = getValue();
        if (iicon == null) return null;
        
        int iw = iicon.getIconWidth();
        int ih = iicon.getIconHeight(); 
        int width = getWidth();
        int height = getHeight();
        double scaleX = (double)width  / (double)iw;
        double scaleY = (double)height / (double)ih;
        double scale  = (scaleY > scaleX)? scaleX: scaleY;
        int nw = (int) (iw * scale);
        int nh = (int) (ih * scale);
        int nx = (width - nw) / 2;
        int ny = (height - nh) / 2;
        return new Rectangle(nx, ny, nw, nh); 
    }
      
    private Rectangle cloneImageRect() {
        return cloneRect(image_rect);
    }
    
    private Rectangle cloneRect(Rectangle source) {
        if (source == null) return null;
        
        return new Rectangle(source.x, source.y, source.width, source.height); 
    }
    
    private boolean matchSize(Rectangle rect1, Rectangle rect2) {
        if (rect1.width != rect2.width) return false;
        if (rect1.height != rect2.height) return false;
        
        return true; 
    }
    
    private boolean overlap(Rectangle outr, Rectangle inr) {
        boolean xb = (inr.x >= outr.x && inr.x+inr.width <= outr.width);
        boolean yb = (inr.y >= outr.y && inr.y+inr.height <= outr.height);
        return (xb && yb);
    }
    
    private Rectangle computeDelta(Rectangle inr, int width, int height) {
        Rectangle rect = new Rectangle(0, 0, 0, 0); 
        if (inr.x < 0) {
            rect.x = Math.abs(inr.x); 
            int n = (rect.x + width) - inr.width;
            if (n < 0) rect.width = Math.abs(n); 
        } else {
            int n = (inr.x + inr.width) - width;
            if (n > 0) rect.width = n;
        }
        if (inr.y < 0) {
            rect.y = Math.abs(inr.y); 
            int n = (rect.y + height) - inr.height;
            if (n < 0) rect.height = Math.abs(n);
        } else {
            int n = (inr.y + inr.height) - height;
            if (n > 0) rect.height = n;
        } 
        return rect;
    }
    
    private Rectangle computeInnerDelta(Rectangle inr, int width, int height) {
        Rectangle rect = new Rectangle(0, 0, 0, 0); 
        if (inr.x < 0) {
            int lx = Math.abs(inr.x);
            int rx = (inr.width - lx) - width;
            if (rx < 0) rect.width = Math.abs(rx);
        } else {
            rect.x = inr.x;
            int rx = (inr.x + inr.width) - width;
            if (rx < 0) rect.width = Math.abs(rx);
        }
        if (inr.y < 0) {
            int ty = Math.abs(inr.y);
            int by = (inr.height - ty) - height;
            if (by < 0) rect.height = Math.abs(by);
        } else {
            rect.y = inr.y; 
            int by = (inr.y + inr.height) - height;
            if (by < 0) rect.height = Math.abs(by);
        }
        return rect;
    }
    
    private void computeCenter(Rectangle inr, int width, int height) {
        inr.x = (width - inr.width) / 2; 
        inr.y = (height - inr.height) / 2; 
    }
        
    private void notifyRefresh() {
        ImageIcon iicon = getValue();
        if (iicon == null) return;
        
        for (ImageView.Handler handler: handlers) {
            handler.refresh(this); 
        }
    } 
    
    public final void fitToWindow() {
        Rectangle rect = getFitViewRect(); 
        image_rect = rect;
        zoom_mode = false;
        refreshCanvas(); 
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" NoImageCanvas "> 
    
    private class NoImageCanvas extends JLabel 
    {
        ImageView root = ImageView.this;
        
        public void paint(Graphics g) {
            int width = getWidth();
            int height = getHeight();
            Color bgcolor = root.getNoImageBackground();
            Color fgcolor = root.getNoImageForeground();
            if (bgcolor == null) bgcolor = Color.decode("#a0a0a0");
            if (fgcolor == null) fgcolor = Color.WHITE;
            
            Graphics2D g2 = (Graphics2D)g.create(); 
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);            
            g2.setBackground(bgcolor);
            g2.fillRect(0, 0, width, height);

            //g2.setColor(Color.DARK_GRAY);
            //g2.setStroke(new BasicStroke(3));
            //g2.drawLine(0, 0, width-1, height-1);
            //g2.drawLine(0, height-1, width-1, 0);

            String str = "No Available"; 
            FontMetrics metrics = g2.getFontMetrics(getFont());
            int fw = metrics.stringWidth(str);
            int fh = metrics.getHeight();
            int x = Math.max(((width - fw) / 2), 0);
            int y = Math.max(((height / 2) - (fh / 2)), 0) + 4;

            g2.setFont(getFont());
            g2.setColor(fgcolor);
            g2.drawString(str, x, y);

            str = "Image";
            fw = metrics.stringWidth(str);
            fh = metrics.getHeight();        
            x = Math.max(((width - fw) / 2), 0);
            y = Math.max(((height / 2) + (fh / 2)), 0) + 4;
            g2.drawString(str, x, y);            
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ImageCanvas "> 
    
    private AffineTransform tx = new AffineTransform();
    
    private class ImageCanvas extends JLabel 
    {
        ImageView root = ImageView.this;
        
        ImageCanvas() {
            setHorizontalAlignment(SwingConstants.CENTER); 
            setOpaque(true); 
        }
        
        public void paintComponent(Graphics g) { 
            super.paintComponent(g);
            ImageIcon iicon = root.getValue();            
            if (iicon == null) return;

            Rectangle fitrect = getFitViewRect();
            int nx = (image_rect==null? fitrect.x: image_rect.x);
            int ny = (image_rect==null? fitrect.y: image_rect.y);
            int nw = (image_rect==null? fitrect.width: image_rect.width);
            int nh = (image_rect==null? fitrect.height: image_rect.height);
            Graphics2D g2 = (Graphics2D) g.create(); 
            g2.drawImage(iicon.getImage(), nx, ny, nw, nh, null); 
            g2.dispose();
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" DefaultLayout "> 
    
    private class DefaultLayout implements LayoutManager 
    {
        ImageView root = ImageView.this;
        
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
                
                Component c = null;
                ImageIcon iicon = root.getValue();
                if (iicon == null) {
                    c = noImageCanvas; 
                } else {
                    c = imageCanvas;
                }                 
                if (c.getParent() == null) {
                    parent.add(c);
                } 
                c.setBounds(x, y, w, h); 
            } 
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" MouseAdapterImpl "> 
    
    public final void refreshCanvas() {
        removeAll();
        revalidate(); 
        repaint();  
        EventQueue.invokeLater(new Runnable() {
            public void run() { 
                notifyRefresh(); 
            }
        });
    }    
    
    public final void zoom(int zoomlevel) {
        ImageIcon iicon = getValue();
        if (iicon == null) return;

        boolean hasFitScreen = false;
        double scale = (double)zoomlevel / 100.0;
        int iw = (int) (iicon.getIconWidth() * scale);
        int ih = (int) (iicon.getIconHeight() * scale);
        Rectangle newrect = new Rectangle(0, 0, iw, ih); 
        computeCenter(newrect, getWidth(), getHeight()); 
        zoom_mode = true;       
        image_rect = newrect; 
        refreshCanvas(); 
    }

    private boolean customize_mode; 
    
    private class MouseAdapterImpl implements MouseWheelListener, MouseListener, MouseMotionListener   
    {
        ImageView root = ImageView.this;
        boolean start_tracking;
        Point startPoint;
        
        public void mouseWheelMoved(MouseWheelEvent e) {
            boolean ctrlDown = ((e.getModifiers() & InputEvent.CTRL_MASK) == InputEvent.CTRL_MASK);
            if (!ctrlDown) return;
            
            ImageIcon iicon = root.getValue();
            if (iicon == null) return;

            Point pivot = e.getPoint();
            int width = getWidth();
            int height = getHeight();
            if (e.getWheelRotation() <= -1) {
                //zoom in
                try {
                    int maxWidth  = (int) (iicon.getIconWidth() * 8.0);
                    int maxHeight = (int) (iicon.getIconWidth() * 8.0);
                    Rectangle rect = new Rectangle(image_rect.x, image_rect.y, image_rect.width, image_rect.height);
                    rect.grow(50, 50); 
                    zoom_mode = true;                     
                    if (rect.width <= maxWidth && rect.height <= maxHeight) {                    
                        image_rect = rect;
                        root.refreshCanvas(); 
                    } 
                } catch(Throwable t) {;}
            } else {
                //zoom out
                try {
                    Rectangle rect = new Rectangle(image_rect.x, image_rect.y, image_rect.width, image_rect.height);
                    rect.grow(-50, -50);
                    Rectangle fit_rect = getFitViewRect();
                    if (rect.width < fit_rect.width || rect.height < fit_rect.height) {
                        image_rect = fit_rect; 
                    } else { 
                        Rectangle deltaR = computeInnerDelta(rect, width, height);
                        if (deltaR.width > 0) rect.x = rect.x + deltaR.width; 
                        if (rect.x > 0) computeCenter(rect, width, height);
                        if (deltaR.height > 0) rect.y = rect.y + deltaR.height;
                        if (rect.y > 0) computeCenter(rect, width, height);
                        
                        image_rect = rect; 
                    } 
                    if (fitSize.width==image_rect.width && fitSize.height==image_rect.height) {
                        zoom_mode = false; 
                    } 
                    root.refreshCanvas();                   
                } catch(Throwable t) {;}
            }
        }

        public void mouseEntered(MouseEvent e) {}
        public void mouseExited(MouseEvent e) {}
        public void mouseMoved(MouseEvent e) {}
        
        private Rectangle orig_image_rect;
        public void mousePressed(MouseEvent e) {
            if (image_rect == null) return;
            
            startPoint = e.getPoint();
            if (image_rect.contains(startPoint)) { 
                orig_image_rect = cloneImageRect();
                start_tracking = true; 
            } 
        }

        public void mouseReleased(MouseEvent e) { 
            onstopDragging(e);
        }
        
        public void mouseClicked(MouseEvent e) {
            onstopDragging(e);
        }
        
        private void onstopDragging(MouseEvent e) {
            start_tracking = false;
            orig_image_rect = null; 
        }
        
        public void mouseDragged(MouseEvent e) {
            if (!start_tracking) return;            
            
            Point p = e.getPoint(); 
            int width = getWidth();
            int height = getHeight();            
            Rectangle rect = root.cloneRect(orig_image_rect); 
            Rectangle deltaR = computeDelta(rect, width, height); 
            int dx = p.x - startPoint.x; 
            int dy = p.y - startPoint.y; 
            if (dy == 0) {
                //do nothing 
            } else if (dy < 0 && deltaR.height > 0) {
                int ady = Math.abs(dy);
                if (ady > deltaR.height) {
                    rect.y = -(deltaR.y + deltaR.height);
                } else {
                    rect.y = -(deltaR.y + ady);
                }
            } else if (dy > 0 && deltaR.y > 0) {
                int ady = Math.abs(dy);
                if (ady > deltaR.y) {
                    rect.y = rect.y + deltaR.y; 
                } else {
                    rect.y = rect.y + ady;
                }
            }
            
            if (dx == 0) {
                //do nothing 
            } else if (dx < 0 && deltaR.width > 0) {
                int adx = Math.abs(dx);
                if (adx > deltaR.width) {
                    rect.x = -(deltaR.x + deltaR.width);
                } else {
                    rect.x = -(deltaR.x + adx);
                }
            } else if (dx > 0 && deltaR.x > 0) {
                int adx = Math.abs(dx);
                if (adx > deltaR.x) {
                    rect.x = rect.x + deltaR.x;
                } else {
                    rect.x = rect.x + adx;
                } 
            }
            image_rect = rect; 
            root.refreshCanvas();
        } 
    }
            
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ComponentListenerImpl "> 
    
    private class ComponentListenerImpl implements ComponentListener 
    {
        ImageView root = ImageView.this;
        
        public void componentMoved(ComponentEvent e) {}
        public void componentShown(ComponentEvent e) {}
        public void componentHidden(ComponentEvent e) {}
        
        public void xcomponentResized(ComponentEvent e) {
            int width = root.getWidth();
            int height = root.getHeight();
            ImageIcon iicon = root.getValue();            
            int iw = (iicon == null? 0: iicon.getIconWidth());
            int ih = (iicon == null? 0: iicon.getIconHeight()); 
            double scaleX = (double)width  / (double)iw;
            double scaleY = (double)height / (double)ih;
            double scale  = (scaleY > scaleX)? scaleX: scaleY;
            int nw = (int) (iw * scale);
            int nh = (int) (ih * scale); 
            fitSize = new Dimension(nw, nh);
            root.refreshCanvas();
        }        
        
        public void componentResized(ComponentEvent e) {
            ImageIcon iicon = root.getValue();  
            if (iicon == null) return;
            
            int width = root.getWidth();
            int height = root.getHeight();
            int iw = iicon.getIconWidth(); 
            int ih = iicon.getIconHeight(); 
            Rectangle fitrect = getFitViewRect();
            fitSize = new Dimension(fitrect.width, fitrect.height);            
            if (image_rect == null) {
                image_rect = fitrect; 
            } else if (zoom_mode) {
                adjustOnZoomMode(); 
            } else {
                image_rect = fitrect;     
            }
            root.refreshCanvas();
        }
        
        private void adjustOnZoomMode() {
            ImageIcon iicon = root.getValue();  
            if (iicon == null) return;
            
            int width = root.getWidth();
            int height = root.getHeight();
            int iw = iicon.getIconWidth(); 
            int ih = iicon.getIconHeight(); 
            int nx=0, ny=0, nw=0, nh=0;
            Rectangle rect = cloneImageRect();
            Rectangle deltaR = computeInnerDelta(rect, width, height);
            if (deltaR.x==0 && deltaR.y==0 && deltaR.width==0 && deltaR.height==0) {
                nx = rect.x;     
                ny = rect.y;
                nw = rect.width; 
                nh = rect.height;
            } else if (deltaR.x > 0 && deltaR.y > 0 && deltaR.width > 0 && deltaR.height > 0) {
                Rectangle fit_rect = getFitViewRect();
                nw = fit_rect.width;
                nh = fit_rect.height;
                nx = (width - nw) / 2; 
                ny = (height - nh) / 2; 
                zoom_mode = false; 
            } else {
                if (deltaR.width > 0) rect.x = rect.x+deltaR.width;
                if (rect.x > 0) rect.x = (width-rect.width)/2;
                if (deltaR.height > 0) rect.y = rect.y+deltaR.height;
                if (rect.y > 0) rect.y = (height-rect.height)/2;
                
                if (rect.x + rect.width < width || rect.y + rect.height < height) {
                    Rectangle fit_rect = getFitViewRect();
                    nx = fit_rect.x;
                    ny = fit_rect.y;
                    nw = fit_rect.width;
                    nh = fit_rect.height;
                    zoom_mode = false;
                } else { 
                    nx = rect.x; 
                    ny = rect.y;
                    nw = rect.width; 
                    nh = rect.height;
                }
            } 
            image_rect = new Rectangle(nx, ny, nw, nh); 
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Handler "> 
    
    public static interface Handler 
    {
        void refresh(ImageView imageView); 
    }
    
    // </editor-fold> 
    
}
