/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.control.image;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 *
 * @author wflores 
 */
class ImageCropItem extends JPanel {
    
    private final static int DEFAULT_BOX_WIDTH  = 200;
    private final static int DEFAULT_BOX_HEIGHT = 200;
    private final static int DEFAULT_ZOOM_VALUE = 25; 
            
    private Meta meta;    
    private ImageIcon value; 
    private ImageCanvas imageCanvas; 
    private BufferedImage cropImage; 
    
    private Dimension boxdim;
    private Rectangle scaleRect;     
    
    public ImageCropItem() {
        initComponent(); 
    }
    
    // <editor-fold defaultstate="collapsed" desc=" initComponent "> 
    
    private void initComponent() {
        super.setLayout(new DefaultLayout()); 
        setPreferredSize(new Dimension(120, 100));
        setOpaque(false); 
        
        boxdim = new Dimension( DEFAULT_BOX_WIDTH, DEFAULT_BOX_HEIGHT ); 
        add( imageCanvas = new ImageCanvas() ); 
        
        MouseAdapterImpl mouseAdapter = new MouseAdapterImpl();
        addMouseMotionListener( mouseAdapter );         
        addMouseWheelListener( mouseAdapter );
        addMouseListener( mouseAdapter );
    }
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" Getters/Setters ">     
    
    public void setLayout(LayoutManager mgr) {}
    
    public ImageIcon getValue() { return value; } 
    public void setValue( ImageIcon value ) {
        this.value = value; 
        this.cropImage = null; 
        this.meta = new Meta( value );         
    }
    
    public int getBoxWidth() { 
        return (boxdim == null ? DEFAULT_BOX_WIDTH : boxdim.width ); 
    }
    public void setBoxWidth( int width ) {
        boxdim.width = width; 
    }
    
    public int getBoxHeight() {
        return (boxdim == null ? DEFAULT_BOX_HEIGHT : boxdim.height );
    }
    public void setBoxHeight( int height ) {
        boxdim.height = height; 
    }
    
    public final void fit() { 
        this.scaleRect = getFitRect(); 
        repaint(); 
    } 
    
    private Rectangle getFitRect() {
        return scaleTo( getWidth(), getHeight()); 
    }
    private Rectangle scaleTo( int width, int height ) {
        ImageIcon iicon = getValue();
        if (iicon == null) return null;
        
        Dimension dim = new Dimension(getBoxWidth(), getBoxHeight()); 
        int iw = iicon.getIconWidth();
        int ih = iicon.getIconHeight(); 
        int cw = Math.max( width, dim.width );
        int ch = Math.max( height, dim.height ); 
        double scaleX = (double)cw  / (double)iw;
        double scaleY = (double)ch / (double)ih;
        double scale  = (scaleY > scaleX)? scaleX: scaleY;
        int nw = (int) (iw * scale);
        int nh = (int) (ih * scale);
        int nx = (cw/2)-(nw/2);
        int ny = (ch/2)-(nh/2); 
        return new FitRect(nx, ny, nw, nh);         
    }
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" Rectangle "> 
 
    private class FitRect extends Rectangle { 
        FitRect( Rectangle rect ) {
            this( rect.x, rect.y, rect.width, rect.height );
        }
        FitRect( int x, int y, int w, int h ) { 
            super( x, y, w, h); 
        }
    }
    private class ZoomRect extends Rectangle { 
        ZoomRect( Rectangle rect ) {
            this( rect.x, rect.y, rect.width, rect.height );
        }        
        ZoomRect( int x, int y, int w, int h ) { 
            super( x, y, w, h); 
        }
    }   
    private class DragRect extends Rectangle { 
        DragRect( Rectangle rect ) {
            this( rect.x, rect.y, rect.width, rect.height );
        } 
        DragRect( int x, int y, int w, int h ) { 
            super( x, y, w, h); 
        }
    } 
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" ImageCanvas "> 
    
    private class ImageCanvas extends JLabel {
        ImageCropItem root = ImageCropItem.this;
        
        ImageCanvas() {
            setHorizontalAlignment(SwingConstants.CENTER); 
            setOpaque(true); 
        }
        
        public void paintComponent(Graphics g) { 
            super.paintComponent(g);
            cropImage = null; 
            
            ImageIcon iicon = root.getValue();            
            if (iicon == null) return;
            
            Rectangle rect = root.scaleRect; 
            if ( rect == null ) { 
                rect = getFitRect();
                root.scaleRect = rect; 
            }
            
            int nw = rect.width;
            int nh = rect.height; 
            int pw = root.getWidth();
            int ph = root.getHeight();            
            Rectangle box = new Rectangle(0, 0, root.getBoxWidth(), root.getBoxHeight()); 
            box.x = Math.max(( pw/2 )-( box.width/2 ),0); 
            box.y = Math.max(( ph/2 )-( box.height/2 ),0); 
            
            Dimension canvasdim = new Dimension( pw, ph ); 
            canvasdim.width = Math.max( canvasdim.width, box.width); 
            canvasdim.height = Math.max( canvasdim.height, box.height );  
            root.meta.setViewSize( canvasdim.width, canvasdim.height );

            BufferedImage bi = new BufferedImage(canvasdim.width, canvasdim.height, BufferedImage.TYPE_INT_ARGB); 
            Graphics2D big2 = bi.createGraphics();
            int nx = (canvasdim.width/2)-(nw/2);
            int ny = (canvasdim.height/2)-(nh/2);
            if ( rect instanceof DragRect ) {
                DragRect dr = (DragRect) rect; 
                nx = dr.x; 
                ny = dr.y; 
            } else { 
                rect.x = nx;
                rect.y = ny;
            } 
            big2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            big2.drawImage(iicon.getImage(), nx, ny, nw, nh, null); 
            root.meta.setImageRect( nx, ny, nw, nh );
            
            try { 
                cropImage = bi.getSubimage(box.x, box.y, box.width, box.height ); 
            } catch(Throwable t) {;} 
            
            Graphics2D g2 = (Graphics2D) g.create(); 
            g2.drawImage(bi, 0, 0, null); 
            g2.dispose();
            
            big2.dispose(); 
            
            AlphaComposite alpha = null; 
            try {
                alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f); 
            } catch (Throwable t) {;}
            
            if (alpha != null) { 
                g2 = (Graphics2D) g.create();
                g2.setComposite(alpha);
                g2.setBackground(Color.BLACK); 
                g2.fillRect(0, 0, getWidth(), getHeight()); 
                g2.dispose(); 
            } 

            if ( cropImage != null ) {
                g2 = (Graphics2D) g.create();
                g2.setColor( Color.WHITE ); 
                g2.drawImage(cropImage, box.x, box.y, null);
                g2.drawRect(box.x, box.y, box.width, box.height); 
                g2.dispose(); 
                
                root.meta.setBoxRect( box.x, box.y, box.width, box.height ); 
            }
        }
    }
    
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc=" DefaultLayout "> 
    
    private class DefaultLayout implements LayoutManager {
        ImageCropItem root = ImageCropItem.this;
        
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
                Dimension dim = new Dimension(root.getBoxWidth(), root.getBoxHeight());
                System.out.println("layout size "+ dim);
                return new Dimension(dim.width, dim.height); 
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
                
                ImageIcon iicon = root.getValue();
                if (iicon == null) return; 

                Component c0 = root.imageCanvas; 
                if ( c0 == null ) return; 
                
                c0.setBounds(x, y, w, h); 
            } 
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" MouseAdapterImpl "> 
    
    public void zoomIn() { 
        zoom( DEFAULT_ZOOM_VALUE );
    }
    public void zoomOut() { 
        zoom( DEFAULT_ZOOM_VALUE * -1 );
    } 
    private void zoom( int value ) { 
        ImageIcon iicon = getValue();
        if ( iicon == null || scaleRect == null ) return; 
        
        Rectangle oldrect = scaleRect; 
        Rectangle newrect = scaleTo( oldrect.width+value, oldrect.height+value);

        if ( oldrect instanceof DragRect ) {
            DragRect dr = (DragRect) oldrect; 
            dr.width = newrect.width; 
            dr.height = newrect.height; 
        } else {
            scaleRect = newrect; 
        } 
        repaint(); 
    }
    
    private class MouseAdapterImpl implements MouseWheelListener, MouseListener, MouseMotionListener { 

        ImageCropItem root = ImageCropItem.this;
                
        private boolean start_tracking;
        private Rectangle dragRect; 
        private Point startPoint; 
        private Cursor oldCursor;
                
        public void mouseWheelMoved(MouseWheelEvent e) { 
            boolean ctrlDown = ((e.getModifiers() & InputEvent.CTRL_MASK) == InputEvent.CTRL_MASK);
            if (!ctrlDown) return;
            
            int rotation = e.getWheelRotation(); 
            if ( rotation < 0 ) { 
                root.zoomIn(); 
            } else if ( rotation > 0 ) {
                root.zoomOut(); 
            } 
        }
        
        public void mouseEntered(MouseEvent e) {}
        public void mouseExited(MouseEvent e) {}
        public void mouseMoved(MouseEvent e) {}

        public void mousePressed(MouseEvent e) { 
            dragRect = new DragRect( root.scaleRect ); 
            startPoint = e.getPoint();             
            start_tracking = true; 
            
            oldCursor = root.getCursor(); 
            root.setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        public void mouseReleased(MouseEvent e) { 
            start_tracking = false; 
            root.setCursor(oldCursor);
        }
        public void mouseClicked(MouseEvent e) {
            start_tracking = false; 
            root.setCursor(oldCursor);
        }

        public void mouseDragged(MouseEvent e) { 
            if ( !start_tracking ) return; 
            
            int ax = startPoint.x; 
            int ay = startPoint.y; 
            int bx = e.getPoint().x;
            int by = e.getPoint().y; 
            int nx = dragRect.x;
            int ny = dragRect.y; 
            if ( bx < ax ) {
                nx -= (ax-bx); 
            } else { 
                nx += (bx-ax); 
            }
            if ( by < ay ) {
                ny -= (ay-by);
            } else {
                ny += (by-ay);
            }
            root.scaleRect = new DragRect(nx, ny, dragRect.width, dragRect.height); 
            root.repaint(); 
        }
    }
    
    // </editor-fold> 
    
    // <editor-fold defaultstate="collapsed" desc=" Meta "> 

    private DecimalFormat decformatter = new DecimalFormat("0");
    private int toInt( double value ) { 
        String str = decformatter.format( value ); 
        return new Integer( str ).intValue(); 
    }    
    
    Rectangle copy( Rectangle2D rect ) { 
        int x = (int) rect.getX();
        int y = (int) rect.getY();
        int w = (int) rect.getWidth(); 
        int h = (int) rect.getHeight(); 
        return new Rectangle( x, y, w, h ); 
    }
        
    public BufferedImage getCropImage() { 
        if ( meta.icon == null ) return null; 
        
        int iw = meta.icon.getIconWidth(); 
        int ih = meta.icon.getIconHeight(); 
        Dimension dim = new Dimension(iw, ih);         
        Dimension canvasdim = new Dimension( getWidth(), getHeight()); 
        
        double vscaleX = canvasdim.getWidth() / dim.getWidth(); 
        double vscaleY = canvasdim.getHeight() / dim.getHeight(); 
        double vscale  = (vscaleY > vscaleX)? vscaleX: vscaleY;
        Dimension scaledim = new Dimension(0, 0);
        scaledim.width = toInt( dim.getWidth() * vscale );
        scaledim.height = toInt( dim.getHeight() * vscale);
        
        Rectangle boxrect = copy( meta.boxRect ); 
        Rectangle imagerect = copy( meta.imageRect );
        
        if ( imagerect.x > 0 ) {
            boxrect.x -= imagerect.x; 
        } else {
            boxrect.x += Math.abs(imagerect.x); 
        }
        if ( imagerect.y > 0 ) {
            boxrect.y -= imagerect.y; 
        } else {
            boxrect.y += Math.abs(imagerect.y); 
        }
        imagerect.x = 0; 
        imagerect.y = 0; 
        
        boxrect = copy( imagerect.createIntersection( boxrect ));
        if ( !imagerect.contains(boxrect.x, boxrect.y, boxrect.width, boxrect.height)) {
            // crop rectangle is not inside the image rectangle 
            return null; 
        }

        Rectangle nimagerect = copy( imagerect ); 
        nimagerect.x = toInt((imagerect.getX()/scaledim.getWidth()) * dim.getWidth());
        nimagerect.y = toInt((imagerect.getY()/scaledim.getHeight()) * dim.getHeight());
        nimagerect.width = toInt((imagerect.getWidth()/scaledim.getWidth()) * dim.getWidth());
        nimagerect.height = toInt((imagerect.getHeight()/scaledim.getHeight()) * dim.getHeight());

        double scaleL = boxrect.getX() / imagerect.getWidth();
        double scaleT = boxrect.getY() / imagerect.getHeight(); 
        double scaleR = (imagerect.getWidth()-(boxrect.getX()+boxrect.getWidth())) / imagerect.getWidth(); 
        double scaleB = (imagerect.getHeight()-(boxrect.getY()+boxrect.getHeight())) / imagerect.getHeight();
        
        Rectangle nboxrect = new Rectangle(0, 0, 0, 0); 
        nboxrect.x = toInt( nimagerect.getWidth() * scaleL);
        nboxrect.y = toInt( nimagerect.getHeight() * scaleT);
        nboxrect.width = nimagerect.width - toInt( nimagerect.getWidth() * scaleR) - nboxrect.x;
        nboxrect.height = nimagerect.height - toInt( nimagerect.getHeight() * scaleB) - nboxrect.y;
        nboxrect = copy( nimagerect.createIntersection( nboxrect )); 
        
        double scaleW = imagerect.getWidth() / scaledim.getWidth(); 
        double scaleH = imagerect.getHeight() / scaledim.getHeight(); 
        if ( scaleW < 1.0 ) scaleW = 1.0; 
        if ( scaleH < 1.0 ) scaleH = 1.0; 
        
        int ndimwidth = toInt( dim.getWidth() * scaleW ); 
        int ndimheight = toInt( dim.getHeight() * scaleH ); 
        
        BufferedImage bi = new BufferedImage( ndimwidth, ndimheight, BufferedImage.TYPE_INT_ARGB); 
        Graphics2D g2 = bi.createGraphics(); 
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.drawImage( meta.icon.getImage(), nimagerect.x, nimagerect.y, nimagerect.width, nimagerect.height, null); 
        g2.dispose();         
        return bi.getSubimage(nboxrect.x, nboxrect.y, nboxrect.width, nboxrect.height); 
    }  
        
    private class Meta {

        private Rectangle imageRect = new Rectangle(0,0,0,0);
        private Rectangle viewRect = new Rectangle(0,0,0,0);
        private Rectangle boxRect = new Rectangle(0,0,0,0);
        private ImageIcon icon; 
        
        Meta( ImageIcon icon ) {
            this.icon = icon; 
        }
        
        ImageIcon getIcon() { return icon; }
        
        Rectangle getBoxRect() { return boxRect; } 
        Rectangle getViewRect() { return viewRect; } 
        Rectangle getImageRect() { return imageRect; } 
                
        void setViewSize( int width, int height ) {
            viewRect.width = width; 
            viewRect.height = height; 
        }
        
        void setBoxRect( int x, int y, int w, int h ) {
            boxRect.x = x;
            boxRect.y = y;
            boxRect.width = w; 
            boxRect.height = h; 
        }
        
        void setImageRect( int x, int y, int w, int h ) {
            imageRect.x = x;
            imageRect.y = y;
            imageRect.width = w; 
            imageRect.height = h; 
        }
    }
    
    // </editor-fold> 

}
