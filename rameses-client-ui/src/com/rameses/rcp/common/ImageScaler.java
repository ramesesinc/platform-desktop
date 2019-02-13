/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.common;

import com.rameses.util.ImageUtil;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 *
 * @author wflores 
 */
public class ImageScaler {
    
    public final static int DEFAULT_THUMBNAIL_SIZE = 100; 
    
    private Color background;
        
    public Color getBackground() { return background; } 
    public void setBackground(Color background) {
        this.background = background; 
    }

    public Image createThumbnail( File file ) throws Exception { 
        return createThumbnail(file, DEFAULT_THUMBNAIL_SIZE, DEFAULT_THUMBNAIL_SIZE ); 
    }    
    public Image createThumbnail( Image image ) throws Exception {
        return createThumbnail( image, DEFAULT_THUMBNAIL_SIZE, DEFAULT_THUMBNAIL_SIZE );
    }
    public Image createThumbnail( ImageIcon iicon ) throws Exception { 
        return createThumbnail( iicon, DEFAULT_THUMBNAIL_SIZE, DEFAULT_THUMBNAIL_SIZE ); 
    }

    public Image createThumbnail( File file, int width, int height ) throws Exception {
        return createThumbnail(new ImageIcon( file.toURI().toURL()), width, height);
    }
    public Image createThumbnail( Image image, int width, int height ) throws Exception {
        return createThumbnail(new ImageIcon( image), width, height);
    }
    public Image createThumbnail( ImageIcon iicon, int width, int height ) throws Exception {
        Dimension origdim = new Dimension(iicon.getIconWidth(), iicon.getIconHeight());
        Dimension resizedim = new Dimension( width+10, height+10 );
        double scaleX = resizedim.getWidth() / origdim.getWidth();
        double scaleY = resizedim.getHeight() / origdim.getHeight();
        double scale = (scaleY > scaleX) ? scaleX : scaleY;
        int nw = (int) (origdim.width * scale);
        int nh = (int) (origdim.height * scale);
        int nx = (resizedim.width - nw) / 2;
        int ny = (resizedim.height - nh) / 2;
        
        BufferedImage canvas = new BufferedImage(resizedim.width, resizedim.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = canvas.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        Color bgcolor = getBackground(); 
        if ( bgcolor != null ) {
            g.setColor(Color.WHITE); 
            g.fillRect(0, 0, resizedim.width, resizedim.height);             
        }
        g.drawImage(iicon.getImage(), nx, ny, nw, nh, null);
        g.dispose(); 
        return canvas.getScaledInstance(width, height, Image.SCALE_SMOOTH);
    }
        
    public Image scale( File file, int width, int height ) throws Exception { 
        return scale(new ImageIcon( file.toURI().toURL()), width, height);
    }

    public Image scale( Image image, int width, int height ) { 
        return scale(new ImageIcon( image), width, height);
    }

    public Image scale( ImageIcon iicon, int width, int height ) { 
        Dimension origdim = new Dimension(iicon.getIconWidth(), iicon.getIconHeight());
        Dimension resizedim = new Dimension( width, height );
        double scaleX = resizedim.getWidth() / origdim.getWidth();
        double scaleY = resizedim.getHeight() / origdim.getHeight();
        double scale = (scaleY > scaleX) ? scaleX : scaleY;
        return scale( iicon, scale ); 
    }
    
    public Image scale( File file, double scale ) throws Exception { 
        return scale(new ImageIcon( file.toURI().toURL()), scale);
    }
    public Image scale( Image image, double scale ) { 
        return scale(new ImageIcon( image), scale);
    }
    public Image scale( ImageIcon iicon, double scale ) { 
        if ( scale <= 0.0 ) throw new RuntimeException("scale must be greater than 0.0");
        if ( scale > 1.0 ) throw new RuntimeException("scale must be less than or equal to 1.0");
        
        Dimension origdim = new Dimension(iicon.getIconWidth(), iicon.getIconHeight());        
        int nw = (int) (origdim.width * scale); 
        int nh = (int) (origdim.height * scale); 
        BufferedImage canvas = new BufferedImage(nw, nh, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = canvas.createGraphics();
        g.drawImage(iicon.getImage(), 0, 0, nw, nh, null);
        g.dispose();
        return canvas;
    }
    
    public byte[] getBytes( Image image ) { 
        if ( image == null ) return null; 
        
        RenderedImage rimage = null; 
        if ( image instanceof RenderedImage ) {
            rimage = (RenderedImage)image; 
        } else {
            ImageIcon iicon = new ImageIcon( image ); 
            BufferedImage bi = new BufferedImage(iicon.getIconWidth(), iicon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = bi.createGraphics();
            g.drawImage(iicon.getImage(), 0, 0, null);
            g.dispose();
            rimage = bi; 
        } 
        
        ByteArrayOutputStream baos = null; 
        try { 
            baos = new ByteArrayOutputStream(); 
            ImageIO.write( rimage, "jpg", baos ); 
            baos.flush(); 
            return baos.toByteArray(); 
        } catch(IOException e) {
            throw new RuntimeException(e.getMessage(), e); 
        } finally {
            try { baos.close(); }catch(Throwable t){;} 
        } 
    }
}
