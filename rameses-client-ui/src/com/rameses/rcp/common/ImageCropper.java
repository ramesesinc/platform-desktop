/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.common;

import com.rameses.rcp.common.CallbackHandlerProxy;
import com.rameses.rcp.common.MsgBox;
import com.rameses.rcp.control.image.ImageCropPanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.LayoutManager;
import java.awt.LayoutManager2;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

/**
 *
 * @author wflores 
 */
public class ImageCropper {
    
    public static void show( Map data ) {  
        new ImageCropper( data ).show(); 
    } 
    
    private int cropWidth  = 200; 
    private int cropHeight = 200;
    
    private int windowWidth  = 640;
    private int windowHeight = 480;
    
    private String title = "Image Cropper"; 
    private Object image;
    private Object handler; 
    
    public ImageCropper() {
    }

    public ImageCropper( Map data ) { 
        if ( data.containsKey("cropWidth")) 
            this.cropWidth = getNumber(data, "cropWidth").intValue(); 
        if ( data.containsKey("cropHeight")) 
            this.cropHeight = getNumber(data, "cropHeight").intValue(); 
        if ( data.containsKey("windowWidth")) 
            this.windowWidth = getNumber(data, "windowWidth").intValue(); 
        if ( data.containsKey("windowHeight")) 
            this.windowHeight = getNumber(data, "windowHeight").intValue(); 
        if ( data.containsKey("title")) 
            this.title = getString(data, "title"); 
        
        this.image = data.get("image"); 
        this.handler = data.get("handler");  
    }
    
    public void setWindowSize( int width, int height ) {
        this.windowWidth = width; 
        this.windowHeight = height; 
    }
    
    public void setCropSize( int width, int height ) {
        this.cropWidth = width; 
        this.cropHeight = height; 
    }
    
    public void setTitle( String title ) {
        this.title = title; 
    }
    
    public void setImage( Object image ) {
        this.image = image; 
    }
    
    public void setHandler( Object handler ) {
        this.handler = handler; 
    }
    
    public void show() {
        ImageCropPanel icp = new ImageCropPanel(); 
        icp.setCropSize(cropWidth, cropHeight);
        
        if ( this.image instanceof File ) {
            icp.loadImage((File) this.image ); 
        } else if ( this.image instanceof byte[] ) {
            icp.loadImage((byte[]) this.image ); 
        } else if ( this.image instanceof ImageIcon ) {
            icp.loadImage((ImageIcon) this.image ); 
        } else {
            throw new RuntimeException("Invalid image data"); 
        }
        
        FooterPanel footer = new FooterPanel(); 
        
        JPanel body = new JPanel(); 
        body.setLayout(new BorderLayout());
        body.add( icp, BorderLayout.CENTER );
        body.add( footer, BorderLayout.SOUTH );

        JDialog d = null; 
        Window win = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusedWindow(); 
        if ( win instanceof Frame ) {
            d = new JDialog((Frame) win); 
        } else if ( win instanceof Dialog ) {
            d = new JDialog((Dialog) win); 
        } else {
            d = new JDialog(); 
        }
        d.setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE ); 
        d.setModal(true);
        d.setTitle( this.title ); 
        d.setContentPane( body ); 
        
        Dimension dim = new Dimension(this.windowWidth, this.windowHeight); 
        Insets margin = Toolkit.getDefaultToolkit().getScreenInsets(d.getGraphicsConfiguration()); 
        Dimension scrdim = Toolkit.getDefaultToolkit().getScreenSize(); 
        int x = Math.max((scrdim.width/2)-(dim.width/2), margin.left); 
        int y = Math.max((scrdim.height/2)-(dim.height/2), margin.top); 
        d.setLocation(x, y); 
        d.setSize( dim.width, dim.height );
        
        footer.handler = new CropHandler( d, icp, handler ); 
        d.setVisible(true);
    }
    
    private Number getNumber( Map data, String name ) {
        try {
            return (Number) data.get(name); 
        } catch(Throwable t) {
            return null; 
        }
    }
    private String getString( Map data, String name ) {
        Object value = (data == null ? null : data.get(name)); 
        return (value == null ? null : value.toString()); 
    }
    
    // <editor-fold defaultstate="collapsed" desc=" Panels "> 
        
    private class FooterPanel extends JPanel {

        CropHandler handler; 
        
        FooterPanel() {
            super.setLayout(new DefaultLayout()); 
            setBorder( BorderFactory.createEmptyBorder(10, 5, 10, 5));
            
            JButton btn = new JButton(" OK "); 
            btn.setMnemonic('O');
            btn.setMargin(new Insets(2,4,2,4));
            btn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    doOk(); 
                }
            }); 
            add( btn );
            
            btn = new JButton("Cancel"); 
            btn.setMnemonic('C');
            btn.setMargin(new Insets(2,4,2,4));
            btn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    doCancel(); 
                }
            }); 
            add( btn );
        }
        
        public void setLayout(LayoutManager layoutManager) {}

        private void doOk() {
            handler.crop(); 
        }
        
        private void doCancel() { 
            handler.cancel(); 
        }
    }
    
    // </editor-fold>    
        
    // <editor-fold defaultstate="collapsed" desc=" CropHandler "> 
    
    private class CropHandler {
        
        private JDialog dialog;
        private Object handler; 
        private ImageCropPanel panel;
        
        CropHandler( JDialog dialog, ImageCropPanel panel, Object handler ) {
            this.dialog = dialog; 
            this.handler = handler; 
            this.panel = panel; 
        }
        
        void cancel() {
            dialog.dispose(); 
        }
        
        void crop() { 
            try { 
                BufferedImage bi = panel.getCropImage(); 
                if ( bi == null ) 
                    throw new Exception("No image to crop. Please check.");  
                
                if ( handler != null ) { 
                    CallbackHandlerProxy proxy = new CallbackHandlerProxy( handler ); 
                    proxy.call( panel.getBytes(bi) ); 
                } 
                dialog.dispose(); 
            } catch(Throwable t) { 
                MsgBox.err( t ); 
            } 
        }
    }
    
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" DefaultLayout "> 
    
    private class DefaultLayout implements LayoutManager, LayoutManager2 {
        
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
                int width=0, height=25, count=0;
                Component[] comps = parent.getComponents(); 
                for (int i=0; i<comps.length; i++) {
                    Component c = comps[i];
                    if (!c.isVisible()) continue; 
                    
                    Dimension dim = c.getPreferredSize();
                    width = Math.max(dim.width, width); 
                    count += 1; 
                }
                
                width = width * count; 
                if ( count > 1 ) {
                    width += (count-1)*5;
                }
                
                Insets margin = parent.getInsets();
                width += (margin.left + margin.right);
                height += (margin.top + margin.bottom);
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
                
                ArrayList<Component> items = new ArrayList();
                Component[] comps = parent.getComponents(); 
                for (int i=0; i<comps.length; i++) {
                    Component c = comps[i];
                    if ( c.isVisible()) {
                        items.add( c ); 
                    }                     
                } 
                
                int cw=0, ch=25, mw=0, count=items.size(); 
                for ( Component c : items ) {
                    Dimension dim = c.getPreferredSize();
                    mw = Math.max( mw, dim.width ); 
                    cw += mw; 
                }
                if ( count > 1 ) {
                    cw += ((count-1) * 5);
                }
                
                x = Math.max((w/2)-(cw/2), margin.left);
                for ( Component c : items ) {
                    Dimension dim = c.getPreferredSize(); 
                    c.setBounds(x, y, mw, ch);
                    x += mw + 5; 
                }
            } 
        }

        public void addLayoutComponent(Component comp, Object constraints) {}

        public Dimension maximumLayoutSize(Container target) { 
            return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE); 
        }

        public float getLayoutAlignmentX(Container target) { return 0.5f; }
        public float getLayoutAlignmentY(Container target) { return 0.5f; }

        public void invalidateLayout(Container target) {
            layoutContainer(target); 
        }
    }
    
    // </editor-fold>                
}
