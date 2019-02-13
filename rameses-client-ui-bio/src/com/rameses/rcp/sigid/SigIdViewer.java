/*
 * SigIdViewer.java
 *
 * Created on December 19, 2013, 8:58 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.sigid;

import com.rameses.rcp.common.CallbackHandlerProxy;
import com.rameses.rcp.common.SigIdModel;
import java.util.Map;

/**
 *
 * @author wflores 
 */
public class SigIdViewer 
{
    public static void open(Map options) {
        new SigIdViewer(options).open();
    }    
    
    public static void open(SigIdModel model) {
        new SigIdViewer(model).open();
    }        
    
    private Map options;
    private SigIdModel model;
    private int width;
    private int height; 
    private boolean autoOpenMode;
    
    public SigIdViewer() {
        this(new SigIdModel()); 
    }

    public SigIdViewer(SigIdModel model) {
        this.model = (model == null? new SigIdModel(): model); 
    }     

    public SigIdViewer(Map options) {
        this.model = new SigIdModelProxy(options); 
    } 
        
    public byte[] open() { 
        SigIdDeviceProvider prov = SigIdDeviceManager.getProvider(); 
        if ( prov == null ) {
            throw new RuntimeException("No available signature plugin device");
        } 
        
        SigIdDevice device = prov.create( model ); 
        device.open(); 
        return null; 
    } 
    
    // <editor-fold defaultstate="collapsed" desc=" SigIdModelProxy "> 
    
    private class SigIdModelProxy extends SigIdModel 
    {
        private Map options; 
        private String title;
        private Integer width; 
        private Integer height;
        private Integer penWidth;
        private Integer imageXSize;
        private Integer imageYSize;
        private String key;
        private CallbackHandlerProxy onselectCallback;
        private CallbackHandlerProxy oncloseCallback;
        
        SigIdModelProxy(Map options) {
            this.options = options;
            this.title = getString(options, "title"); 
            this.width = getInt(options, "width"); 
            this.height = getInt(options, "height");
            this.penWidth = getInt(options, "penWidth"); 
            this.imageXSize = getInt(options, "imageXSize"); 
            this.imageYSize = getInt(options, "imageYSize");
            this.key = getString(options, "key"); 
            
            Object source = get(options, "onselect"); 
            if (source != null) onselectCallback = new CallbackHandlerProxy(source); 
            
            source = get(options, "onclose"); 
            if (source != null) oncloseCallback = new CallbackHandlerProxy(source); 
        }
        
        public String getTitle() {
            if (title == null) {
                return super.getTitle(); 
            } else { 
                return title; 
            } 
        }
        
        public int getWidth() {
            if (width == null) {
                return super.getWidth(); 
            } else {
                return width.intValue(); 
            }
        }
        
        public int getHeight() {
            if (height == null) {
                return super.getHeight(); 
            } else {
                return height.intValue(); 
            }
        }        
        
        public int getPenWidth() {
            if (penWidth == null) {
                return super.getPenWidth(); 
            } else {
                return penWidth.intValue(); 
            }
        }
        public int getImageXSize() {
            if (imageXSize == null) {
                return super.getImageXSize(); 
            } else {
                return imageXSize.intValue(); 
            }
        }    
        public int getImageYSize() {
            if (imageYSize == null) {
                return super.getImageYSize(); 
            } else {
                return imageYSize.intValue(); 
            }
        }  
        public String getKey() {
            return (key == null? super.getKey(): key); 
        } 

        public void onselect(Object result) {
            if (onselectCallback == null) return;
            
            onselectCallback.call(result); 
        } 

        public void onclose() {
            if (oncloseCallback == null) return;
            
            oncloseCallback.call(); 
        } 
        
        private Integer getInt(Map map, String name) {
            try {
                return (Integer) map.get(name);
            } catch(Throwable t) { 
                return null; 
            }
        }

        private String getString(Map map, String name) {
            try {
                Object o = map.get(name);
                return (o == null? null: o.toString()); 
            } catch(Throwable t) { 
                return null; 
            }
        } 
        
        private Object get(Map map, String name) {
            return (map == null? null: map.get(name)); 
        }
    }
    
    // </editor-fold>
        
}
