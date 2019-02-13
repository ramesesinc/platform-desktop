/*
 * ImageViewer.java
 *
 * Created on December 23, 2013, 12:39 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.image;

import com.rameses.rcp.common.CallbackHandlerProxy;
import com.rameses.rcp.common.ImageModel;
import com.rameses.rcp.common.MsgBox;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.Map;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author wflores
 */
public final class ImageViewer 
{
    public static void open(Map options) {
        new ImageViewer(options).open();
    } 
    
    public static void open(ImageModel model) {
        new ImageViewer(model).open();
    }        
    
    private Map options;
    private ImageModel model;
    private int width;
    private int height; 
    private boolean autoOpenMode;
    
    private JFileChooser fileChooser;
    
    public ImageViewer() {
        this(new ImageModel()); 
    }

    public ImageViewer(ImageModel model) {
        this.model = (model == null? new ImageModel(): model); 
    }     

    public ImageViewer(Map options) {
        this.model = new ImageModelProxy(options); 
    } 
        
    public byte[] open() { 
        Window win = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusedWindow(); 

        final ContentPane panel = new ContentPane(); 
        Object imageData = model.getData(); 
        if (imageData == null) {
            JFileChooser fc = getFileChooser();
            fc.addChoosableFileFilter(new ImageFileFilter(model)); 
            int retopt = fc.showOpenDialog(win); 
            if (retopt == JFileChooser.APPROVE_OPTION) {
                panel.setData(fc.getSelectedFile()); 
            } else {
                return null;
            }
        } else {
            panel.setData(imageData); 
        }
        
        JDialog dialog = null; 
        if (win instanceof Frame) {
            dialog = new JDialog((Frame) win); 
        } else if (win instanceof Dialog) {
            dialog = new JDialog((Dialog) win); 
        } else {
            dialog = new JDialog(); 
        } 
        
        final JDialog jdialog = dialog;        
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); 
        dialog.setModal(true); 
        dialog.setResizable(false); 
        dialog.setContentPane(panel);         
        dialog.setTitle(model.getTitle());
        dialog.setSize(model.getWidth(), model.getHeight());
        dialog.addWindowListener(new WindowListener() {
            public void windowActivated(WindowEvent e) {}
            public void windowClosed(WindowEvent e) {}
            public void windowClosing(WindowEvent e) { 
                try { 
                    //panel.stop();
                } catch(Throwable t) {
                    JOptionPane.showMessageDialog(jdialog, "[ERROR] " + t.getClass().getName() + ": " + t.getMessage()); 
                } 
                
                //oncloseImpl(); 
            }
            
            public void windowDeactivated(WindowEvent e) {}
            public void windowDeiconified(WindowEvent e) {}
            public void windowIconified(WindowEvent e) {}
            
            public void windowOpened(WindowEvent e) { 
                try { 
                    //panel.start(); 
                } catch(Throwable t) {
                    MsgBox.err(t); 
                    EventQueue.invokeLater(new Runnable() {
                        public void run() {
                            jdialog.dispose(); 
                        }
                    }); 
                }
            }
        }); 
        
        centerWindow(dialog);
        dialog.setVisible(true); 
        return null; 
    } 
    
    private JFileChooser getFileChooser() {
        if (fileChooser == null) {
            fileChooser = new JFileChooser(); 
        }
        return fileChooser;
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
    
    // <editor-fold defaultstate="collapsed" desc=" ImageModelProxy "> 
    
    private class ImageModelProxy extends ImageModel 
    {
        private Map options; 
        private String title;
        private Integer width; 
        private Integer height;
        private CallbackHandlerProxy onselectCallback;
        private CallbackHandlerProxy oncloseCallback;
        
        ImageModelProxy(Map options) {
            this.options = options;
            this.title = getString(options, "title"); 
            this.width = getInt(options, "width"); 
            this.height = getInt(options, "height");
            
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
        
        public Object getData() {
            Object o = get(options, "data");
            return (o == null? super.getData(): o);
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
    
    // <editor-fold defaultstate="collapsed" desc=" ImageFileFilter "> 
    
    private class ImageFileFilter extends FileFilter 
    {
        private ImageModel model;
        private String filterDescription;
        
        ImageFileFilter(ImageModel model) {
            this.model = model;
            
            filterDescription = model.getFilterDescription(); 
            if (filterDescription == null || filterDescription.length() == 0) {
                filterDescription = "*.jpg|*.png|*.gif"; 
            }
        }

        public String getDescription() {
            return model.getFilterDescription();
        }
        
        public boolean accept(File file) {
            return model.accept(file); 
        }
    }
    
    // </editor-fold>
}
