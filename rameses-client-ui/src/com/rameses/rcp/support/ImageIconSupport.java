/*
 * ImageIconSupport.java
 *
 * Created on July 8, 2013, 9:57 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.support;

import com.rameses.rcp.framework.ClientContext;
import com.rameses.rcp.util.ControlSupport;
import java.awt.Image;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;

/**
 *
 * @author wflores
 */
public class ImageIconSupport 
{
    private static ImageIconSupport instance;
    
    public static ImageIconSupport getInstance() {
        if (instance == null) {
            instance = new ImageIconSupport();
        }         
        return instance; 
    }
    
    public static ImageIconSupport.Resizer getResizer() {
        return new Resizer(); 
    }
    
    
    
    private Map<String,Image> cache = new HashMap(); 
            
    private ImageIconSupport() {
    }
    
    public synchronized void removeIcon(String path) 
    {
        if (path == null || path.length() == 0) return;
        
        cache.remove(path);
    }
    
    public synchronized ImageIcon getIcon(String path) 
    {
        if (path == null || path.length() == 0) return null;
        
        Image image = cache.get(path); 
        if (image == null) 
        {                        
            try 
            {
                if (path.toLowerCase().indexOf("://") > 0) {  
                    URL url = ClientContext.getCurrentContext().getResource(path); 
                    if ( url == null ) { 
                        url = new URL(path); 
                    } 
                    return new ImageIcon( url ); 
                } 
                
                byte[] bytes = ControlSupport.getByteFromResource(path);
                if (bytes == null) {
                    URL url = getResource(path);
                    if (url == null) return null; 
                    
                    ImageIcon icon = new ImageIcon(url);
                    cache.put(path, icon.getImage());
                    return icon; 
                } else { 
                    ImageIcon icon = new ImageIcon(bytes); 
                    cache.put(path, icon.getImage());
                    return icon;
                } 
            } catch(Throwable ex) {
                return null; 
            } 
        }
        return new ImageIcon(image); 
    }   
    
    private URL getResource(String path) {
        try { 
            ClassLoader classLoader = getClass().getClassLoader();
            URL url = classLoader.getResource(path);
            if (url == null) {
                return classLoader.getSystemClassLoader().getResource(path);
            } else {
                return url;
            } 
        } catch(Throwable t) {
            return null; 
        }
    }
    
    // <editor-fold defaultstate="collapsed" desc=" Resizer "> 
    
    public static class Resizer 
    {
        public final static int SCALE_AREA_AVERAGING = Image.SCALE_AREA_AVERAGING;
        public final static int SCALE_DEFAULT = Image.SCALE_DEFAULT;
        public final static int SCALE_FAST = Image.SCALE_FAST;
        public final static int SCALE_REPLICATE = Image.SCALE_REPLICATE;
        public final static int SCALE_SMOOTH = Image.SCALE_SMOOTH;
        
        private Resizer() {
        }
        
        public Image resizeWidth(Image image, int size) {
            return resizeWidth(image, size, SCALE_FAST);
        }
        
        public Image resizeWidth(Image image, int size, int scaleType) {
            return resize(image, size, -1, scaleType);
        }        
        
        public Image resizeHeight(Image image, int size) {
            return resizeHeight(image, size, SCALE_FAST);
        }

        public Image resizeHeight(Image image, int size, int scaleType) {
            return resize(image, -1, size, scaleType);
        }
        
        public Image resize(Image image, int width, int height) {
            return resize(image, width, height, SCALE_FAST);
        }      
        
        public Image resize(Image image, int width, int height, int scaleType) {
            if (image == null) {
                return null;
            } else if (width < 0 && height < 0) { 
                return image;
            } else if (width > 0 && height < 0) { 
                return image.getScaledInstance(width, -1, scaleType); 
            } else if (width < 0 && height > 0) { 
                return image.getScaledInstance(-1, height, scaleType); 
            } else {
                return image.getScaledInstance(width, height, scaleType); 
            }
        } 
    }
    
    // </editor-fold>
}
