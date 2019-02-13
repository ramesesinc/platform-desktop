/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.control.image;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.LayoutManager2;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 *
 * @author wflores 
 */
public class ImageCropPanel extends JPanel {
    
    private Dimension cropSize; 
    private ImageCropItem imageView;
    private ImageCropToolbar imageToolbar; 
    
    public ImageCropPanel() {
        initComponent();
    }
    
    // <editor-fold defaultstate="collapsed" desc=" initComponent "> 
    
    private void initComponent() {
        super.setLayout(new DefaultLayout()); 
        
        add( imageToolbar = new ImageCropToolbar()); 
        add( imageView = new ImageCropItem()); 
        
        imageToolbar.setAction( ImageCropToolbar.ACTION_ZOOM_IN, new ZoomInAction());
        imageToolbar.setAction( ImageCropToolbar.ACTION_ZOOM_OUT, new ZoomOutAction());
        imageToolbar.setAction( ImageCropToolbar.ACTION_FIT_SCREEN, new FitScreenAction());
    }
    
    // </editor-fold>        

    // <editor-fold defaultstate="collapsed" desc=" Getter/Setter "> 
    
    public void setLayout(LayoutManager layoutManager) {}
    
    public Dimension getCropSize() {
        return cropSize; 
    }
    public void setCropSize( int width, int height ) {
        this.cropSize = new Dimension( width, height ); 
        imageView.setBoxWidth( width ); 
        imageView.setBoxHeight( height ); 
    } 
    
    public BufferedImage getCropImage() { 
        return imageView.getCropImage();
    }
    
    // </editor-fold>            
    
    // <editor-fold defaultstate="collapsed" desc=" helper methods "> 
    
    public void loadImage( File file ) {
        try { 
            loadImage( new ImageIcon( file.toURI().toURL() )); 
        } catch (MalformedURLException e) {
            throw new RuntimeException( e.getMessage(), e ); 
        }
    }
    public void loadImage( byte[] bytes ) {
        loadImage( new ImageIcon( bytes)); 
    } 
    public void loadImage( ImageIcon value ) {
        imageView.setValue( value ); 
        repaintImage(); 
    }

    public void repaintImage() {
        imageView.repaint(); 
    }
    
    public byte[] getBytes( BufferedImage image ) { 
        if ( image == null ) return null; 
        
        ByteArrayOutputStream baos = null; 
        try {
            baos = new ByteArrayOutputStream(); 
            ImageIO.write( image, "jpg", baos );
            baos.flush(); 
            return baos.toByteArray(); 
        } catch(IOException e) {
            throw new RuntimeException(e.getMessage(), e); 
        } finally {
            try { baos.close(); }catch(Throwable t){;} 
        } 
    }     
    
    // </editor-fold>      
    
    // <editor-fold defaultstate="collapsed" desc=" Actions "> 

    private class ZoomInAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            ImageCropItem c = ImageCropPanel.this.imageView;
            if ( c != null ) c.zoomIn(); 
        }
    }

    private class ZoomOutAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            ImageCropItem c = ImageCropPanel.this.imageView;
            if ( c != null ) c.zoomOut();
        }
    }
    
    private class FitScreenAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            ImageCropItem c = ImageCropPanel.this.imageView;
            if ( c != null ) c.fit(); 
        }
    }
    
    // </editor-fold>          
    
    // <editor-fold defaultstate="collapsed" desc=" DefaultLayout "> 
    
    private class DefaultLayout implements LayoutManager, LayoutManager2  {
        ImageCropPanel root = ImageCropPanel.this;
        
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
                int width = (margin.left + margin.right); 
                int height = (margin.top + margin.bottom);
                return new Dimension( width, height ); 
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
                
                Component c = root.imageView; 
                if ( c != null && c.isVisible() ) {
                    c.setBounds(x, y, w, h); 
                } else {
                    return; 
                }
                
                c = root.imageToolbar; 
                System.out.println( c );
                if ( c != null && c.isVisible() ) {
                    Dimension dim = c.getPreferredSize(); 
                    y = Math.max(ph - margin.bottom - dim.height, margin.top);  
                    x = Math.max((w/2)-(dim.width/2), margin.left);
                    c.setBounds(x, y, dim.width, dim.height);
                }
            } 
        }

        public void addLayoutComponent(Component comp, Object constraints) {}
        public float getLayoutAlignmentX(Container target) { return 0.5f; }
        public float getLayoutAlignmentY(Container target) { return 0.5f; }

        public Dimension maximumLayoutSize(Container target) { 
            return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE); 
        }

        public void invalidateLayout(Container target) {
            layoutContainer(target); 
        }
    }
    
    // </editor-fold>    
    
}
