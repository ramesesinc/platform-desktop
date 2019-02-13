/*
 * WebcamViewer.java
 *
 * Created on December 4, 2013, 8:44 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.camera;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
import com.rameses.rcp.common.*;
import com.rameses.rcp.common.CameraModel.ViewerProvider;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.swing.JDialog;

/**
 *
 * @author wflores 
 */
public final class WebcamViewer 
{
    public final static Properties CACHE = new Properties(); 
    public static Dimension PREFERRED_SIZE;
    
    public static void open(Map options) {
        new WebcamViewer(options).open();
    }    
    
    public static void open(CameraModel model) {
        new WebcamViewer(model).open();
    }        
    
    private CameraModel model;
    private String title;
    private int width;
    private int height;
    private boolean autoCloseOnSelect;
    private boolean autoOpenMode;
    private boolean alwaysOnTop;
    private boolean modal;
    
    public WebcamViewer() {
        this(new CameraModel()); 
    }
    
    public WebcamViewer(CameraModel model) {
        this.model = (model == null? new CameraModel(): model); 
        init();
    }

    public WebcamViewer(Map options) {
        this.model = new CameraModelProxy(options); 
        init();
    } 
    
    private void init() {
        title = model.getTitle();
        width = model.getWidth();
        height = model.getHeight();
        autoCloseOnSelect = model.isAutoCloseOnSelect();
        autoOpenMode = model.isAutoOpenMode();  
        alwaysOnTop = model.isAlwaysOnTop(); 
        modal = model.isModal(); 
    }
        
    public void setSize(int width, int height) {
        this.width = width;
        this.height = height; 
    }
    
    public void setAutoOpenMode(boolean autoOpenMode) {
        this.autoOpenMode = autoOpenMode;
    }
    
    public byte[] open() { 
        Window win = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusedWindow(); 
        
        final WebcamPane pane = new WebcamPane(this);  
        pane.setAutoCloseOnSelect(model.isAutoCloseOnSelect()); 
        
        JDialog dialog = null; 
        if (win instanceof Frame) {
            dialog = new JDialog((Frame) win); 
        } else if (win instanceof Dialog) {
            dialog = new JDialog((Dialog) win); 
        } else {
            dialog = new JDialog(); 
        } 
        
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); 
        dialog.setTitle("Camera"); 
        if (alwaysOnTop) {
            dialog.setAlwaysOnTop(true); 
            dialog.setModal(false); 
        } else {
            dialog.setAlwaysOnTop(true);
            dialog.setModal(true); 
        }         
        //dialog.setResizable(false); 
        dialog.setContentPane(pane);
        dialog.pack();
        dialog.addWindowListener(new WindowListener() {
            public void windowActivated(WindowEvent e) {}
            public void windowClosed(WindowEvent e) {}
            public void windowClosing(WindowEvent e) { 
                try { 
                    pane.stop(); 
                } catch(Throwable t) {
                    MsgBox.err(t); 
                } 
                
                onclose(); 
            }
            
            public void windowDeactivated(WindowEvent e) {}
            public void windowDeiconified(WindowEvent e) {}
            public void windowIconified(WindowEvent e) {}
            
            public void windowOpened(WindowEvent e) {
                pane.start(); 
            }
        }); 
        centerWindow(dialog); 
        pane.addListener(new WebcamPaneListenerImpl(dialog));
        model.setViewerProvider(new ViewerProviderImpl(dialog)); 
        dialog.setVisible(true); 
        return null; 
    }
    
    private void centerWindow(Window win) {
        Dimension windim = win.getSize();
        Dimension scrdim = Toolkit.getDefaultToolkit().getScreenSize(); 
        Insets margin = Toolkit.getDefaultToolkit().getScreenInsets(win.getGraphicsConfiguration()); 
        int scrwidth = scrdim.width - (margin.left + margin.right);
        int scrheight = scrdim.height - (margin.top + margin.bottom);
        int x = Math.max((scrwidth - windim.width) / 2, 0) + margin.left;
        int y = Math.max((scrheight - windim.height) / 2, 0) + margin.top;
        win.setLocation(x, y); 
    } 
    
    private Webcam whichWebcam(List<Webcam> webcams) {
        if (webcams.isEmpty())
            throw new RuntimeException("No available Webcam on your computer"); 
        
        if (webcams.size() > 1) {
            return webcams.get(1); 
        } else {
            return webcams.get(0); 
        }
    }
    
    // <editor-fold defaultstate="collapsed" desc=" CameraModelProxy "> 
    
    private class CameraModelProxy extends CameraModel 
    {
        private Map options; 
        private String title;
        private Integer width; 
        private Integer height;
        private Boolean autoOpenMode;
        private CallbackHandlerProxy onselectCallback;
        private CallbackHandlerProxy oncloseCallback;
        
        CameraModelProxy(Map options) {
            this.options = options;
            this.title = getString(options, "title"); 
            this.width = getInt(options, "width"); 
            this.height = getInt(options, "height");
            this.autoOpenMode = getBool(options, "autoOpenMode");
            
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
        
        public boolean isAutoOpenMode() {
            if (autoOpenMode == null) {
                return super.isAutoOpenMode(); 
            } else {
                return autoOpenMode.booleanValue(); 
            }
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
        
        private Boolean getBool(Map map, String name) {
            try {
                return (Boolean) map.get(name);
            } catch(Throwable t) { 
                return null; 
            }
        } 
        
        private Object get(Map map, String name) {
            return (map == null? null: map.get(name)); 
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" WebcamPaneListenerImpl "> 
    
    private class WebcamPaneListenerImpl implements WebcamPaneListener
    {
        WebcamViewer root = WebcamViewer.this;
        
        private JDialog dialog;
        
        WebcamPaneListenerImpl(JDialog dialog) {
            this.dialog = dialog; 
        }

        public void oncreate(Webcam webcam) {
            String title = root.title;
            if (title == null) title = "Camera";
            
            dialog.setTitle(title); 
            Dimension[] customViewSizes = new Dimension[]{ 
                WebcamResolution.PAL.getSize(), 
                new Dimension(1024, 768), 
                WebcamResolution.HD720.getSize() 
            };
            webcam.setCustomViewSizes(customViewSizes); 
            webcam.setViewSize(WebcamResolution.HD720.getSize());
            
            try { 
                Integer owidth = getInteger(root.CACHE, "webcam.width");
                Integer oheight = getInteger(root.CACHE, "webcam.height");
                webcam.setViewSize(new Dimension(owidth.intValue(), oheight.intValue())); 
            } catch(Throwable t) {;} 
            
            Dimension dim = webcam.getViewSize();         
            String deviceName = webcam.getDevice().getName();            
            dialog.setTitle(deviceName + " ("+dim.width+" x "+dim.height+")");             
            webcam.setAutoOpenMode(autoOpenMode); 
        }
        
        public void onchangeResolution(Dimension dim) {
            if (dim == null) return;
            
            
        }
        
        public void onselect(byte[] bytes) {
            if (autoCloseOnSelect) dialog.dispose();
            if (root.model != null) root.model.onselect(bytes); 
        } 

        public void oncancel() {
            dialog.dispose(); 
            root.onclose(); 
        }
        
        private Integer getInteger(Map data, String name) {
            try {
                int num = Integer.parseInt(data.get(name).toString());
                return new Integer(num); 
            } catch(Throwable t) {
                return null; 
            }
        }
    }
    
    private void onclose() {
        if (model != null) model.onclose();
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ViewerProviderImpl "> 
    
    private class ViewerProviderImpl implements ViewerProvider 
    {
        WebcamViewer root = WebcamViewer.this;
        
        private JDialog dialog;
        
        ViewerProviderImpl(JDialog dialog) {
            this.dialog = dialog; 
        }
        
        public void close() { 
            dialog.dispose(); 
            root.onclose();
        } 
    } 
    
    // </editor-fold>
}
