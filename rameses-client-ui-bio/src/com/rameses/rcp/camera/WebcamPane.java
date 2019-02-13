/*
 * WebcamPane.java
 *
 * Created on December 4, 2013, 9:01 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.camera;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamDiscoveryEvent;
import com.github.sarxos.webcam.WebcamDiscoveryListener;
import com.github.sarxos.webcam.WebcamEvent;
import com.github.sarxos.webcam.WebcamListener;
import com.github.sarxos.webcam.WebcamPanel;
import com.rameses.rcp.common.MsgBox;
import com.rameses.rcp.support.ImageIconSupport;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JToolBar;

/**
 *
 * @author wflores
 */
class WebcamPane extends JPanel 
{
    private WebcamViewer viewer;
    private Webcam webcam;
    private WebcamPanel wcp;
    private JToolBar toolbar;
    private JButton btnShoot;
    private JComboBox cboDevices;
    private JComboBox cboSizes;
    
    private DefaultComboBoxModel deviceModel;
    
    private List<Webcam> webcams;    
    private List<WebcamPaneListener> listeners;
    
    private boolean autoCloseOnSelect;    
    
    public WebcamPane(WebcamViewer viewer) {
        this.listeners = new ArrayList(); 
        this.viewer = viewer;
        initComponent();
    }
    
    // <editor-fold defaultstate="collapsed" desc=" initComponent "> 
    
    private void initComponent() {
        //super.setLayout(new DefaultLayout()); 
        super.setLayout(new BorderLayout()); 
        setPreferredSize(new Dimension(370, 290));
        
        webcams = Webcam.getWebcams();
        Webcam.addDiscoveryListener(new WebcamDiscoveryListener() {
            public void webcamFound(WebcamDiscoveryEvent e) {
                System.out.println("webcamFound: " + e.getWebcam());
                reloadDevices();
            }
            public void webcamGone(WebcamDiscoveryEvent e) {
                System.out.println("webcamGone: " + e.getWebcam());
                reloadDevices();
            }
            private void reloadDevices() {
                deviceModel = new WCDeviceModel();
                cboDevices.setModel(deviceModel); 
            }
        }); 
        
        toolbar = new JToolBar();
        toolbar.setFloatable(false);        
        toolbar.setRollover(false); 
        toolbar.setLayout(new ToolbarLayout()); 
        toolbar.setBorder(BorderFactory.createEmptyBorder(5,5,5,5)); 
        add(toolbar, BorderLayout.SOUTH); 

        deviceModel = new DefaultComboBoxModel();
        cboDevices = new JComboBox();
        cboDevices.setEnabled(false);
        cboDevices.addItemListener(new WCDeviceSelector()); 
        toolbar.add(cboDevices, "left");
        
        cboSizes = new JComboBox();
        cboSizes.setEnabled(false);
        cboSizes.addItemListener(new WCResolutionSelector()); 
        toolbar.add(cboSizes, "left");
        
        btnShoot = new JButton("Shoot");
        btnShoot.setEnabled(false);
        btnShoot.setFocusPainted(false); 
        btnShoot.setIcon(ImageIconSupport.getInstance().getIcon("images/toolbars/camera.png"));  
        btnShoot.addActionListener(new ShootActionSupport(btnShoot));
        toolbar.add(btnShoot, "right"); 
        
        JButton btnCancel = new JButton("Cancel");
        btnCancel.setFocusPainted(false); 
        btnCancel.setIcon(ImageIconSupport.getInstance().getIcon("images/toolbars/cancel.png")); 
        btnCancel.addActionListener(new CancelActionSupport()); 
        toolbar.add(btnCancel, "right"); 
    }
    
    private void initWebcam() {
        initWebcam(WebcamViewer.CACHE.getProperty("webcam.name"));
    }
    
    private void initWebcam(final String name) {
        if (webcams == null) {
            webcams = Webcam.getWebcams();
        }
        
        Webcam new_webcam = findWebcam(name);
        if (new_webcam == null) return; 
        
        System.out.println("initWebcam ...(" + name + ")");
        //close previous webcam
        if (wcp != null) wcp.stop(); 
        if (wcp != null) { 
            remove(wcp); 
            wcp = null; 
            revalidate();
            repaint();
        } 
        
        webcam = new_webcam;         
        cboSizes.removeAllItems();          
        webcam.addWebcamListener(new WebcamPanelSupport());         
        for (WebcamPaneListener wpl : listeners) { 
            wpl.oncreate(webcam); 
        } 
        
        WebcamViewer.CACHE.setProperty("webcam.name", webcam.getDevice().getName()); 
        deviceModel = new WCDeviceModel(); 
        cboDevices.setModel(deviceModel); 
        cboDevices.setEnabled(true); 
        cboSizes.setModel(new WCResolutionModel()); 
        
        wcp = new WebcamPanel(webcam, webcam.getViewSize(), false); 
        wcp.setFPSDisplayed(true);
        wcp.setFillArea(true); 
        add(wcp); 

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                if (!wcp.isStarted()) wcp.start(); 

                revalidate();
                repaint();
            }
        });
    }
    
    private Webcam findWebcam(String name) {
        List<Webcam> webcams = Webcam.getWebcams();
        if (webcams.isEmpty())
            throw new RuntimeException("No available Webcam on your computer"); 
        
        for (Webcam wc : webcams) {
            if (name == null || name.length() == 0) return wc;
            if (name.equalsIgnoreCase(wc.getDevice().getName())) return wc; 
        }
        return webcams.get(0); 
    }    
        
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Getters/Setters "> 
    
    public void setLayout(LayoutManager mgr){
    } 
    
    void setAutoCloseOnSelect(boolean autoCloseOnSelect) {
        this.autoCloseOnSelect = autoCloseOnSelect;
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" helper/override methods "> 

    public void removeListener(WebcamPaneListener listener) {
        if (listener != null) listeners.remove(listener); 
    }
    
    public void addListener(WebcamPaneListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener); 
        }
    }
    
    public void start() {
        if (wcp == null) initWebcam(); 

        wcp.start(); 
    }
    
    public void stop() { 
        if (wcp != null) wcp.stop(); 
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g); 
        
        int width = getWidth();
        int height = getHeight();
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(Color.BLACK); 
        g2.fillRect(0, 0, width, height);
        g2.setColor(Color.WHITE);        
        String str = "loading device ...";
        if (webcams.isEmpty()) str = "No available Webcam device";
        g2.drawString(str, 20, 30); 
        g2.dispose();
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" WCDevice "> 
    
    private class WCDeviceModel extends DefaultComboBoxModel 
    {
        WCDeviceModel() { 
            super();
            List<Webcam> webcams = Webcam.getWebcams(); 
            if (webcams.isEmpty()) {
                addElement(new WCDevice(null, "-- Devices --"));
            } else { 
                for (Webcam wc : webcams) {
                    String name = wc.getDevice().getName();
                    addElement(new WCDevice(name, name));
                } 
                
                String wc_name = WebcamViewer.CACHE.getProperty("webcam.name");
                setSelectedItem(new WCDevice(wc_name, wc_name)); 
                
                WCDevice sel = (WCDevice) getSelectedItem();
                if (sel == null) setSelectedItem(getElementAt(0));
            } 
        }
    }
    
    private class WCDevice 
    {
        private Object key;
        private Object value;
        private Webcam webcam;
        
        WCDevice(Object key, Object value) {
            this.key = key;
            this.value = value; 
        }
        
        public Object getKey() { return key; } 
        public Object getValue() { return value; } 
        public String toString() {
            return (value == null? "null": value.toString()); 
        }
        
        public boolean equals(Object obj) {
            if (!(obj instanceof WCDevice)) return false;
            
            WCDevice wc = (WCDevice)obj;
            Object key1 = wc.getKey();
            Object key2 = getKey();
            if (key1 == null && key2 == null) return true;
            
            return (key1 != null && key2 != null && key1.equals(key2));
        } 
    }    
    
    private class WCDeviceSelector implements ItemListener 
    {
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Object item = e.getItem();
                if (!(item instanceof WCDevice)) return; 
                
                Object key = ((WCDevice) item).getKey();
                if (key == null) return;
                
                initWebcam(key.toString()); 
            }
        }
    }      
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" WCResolution "> 
    
    private class WCResolutionModel extends DefaultComboBoxModel 
    {
        WebcamPane root = WebcamPane.this;
        
        WCResolutionModel() { 
            super();
            if (root.webcam == null) return;
            
            Dimension[] sizes = root.webcam.getViewSizes();
            for (Dimension dim : sizes) {
                addElement(new WCResolution(dim));
            }
            sizes = root.webcam.getCustomViewSizes();
            for (Dimension dim : sizes) {
                addElement(new WCResolution(dim));
            }
            
            if (getSize() == 0) {
                addElement(new WCResolution(null, "-- Resolutions --"));
            } else {
                Dimension dim = root.webcam.getViewSize();
                setSelectedItem(new WCResolution(dim));
                
                WCResolution sel = (WCResolution) getSelectedItem();
                if (sel == null) setSelectedItem(getElementAt(0));
            }
        }
    }    
    
    private class WCResolution 
    {
        private Dimension size;
        private String caption;
        
        WCResolution(Dimension size) {
            this.size = size;
            if (size != null) { 
                this.caption = size.width + " x " + size.height; 
            } 
        }
        
        WCResolution(Dimension size, String caption) {
            this.size = size;
            this.caption = caption;
        }
        
        public Dimension getSize() { return size; } 
        public String toString() {
            return (caption == null? "null": caption);
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof WCResolution)) return false;
            
            WCResolution res = (WCResolution)obj;
            Dimension dim1 = res.getSize(); 
            Dimension dim2 = getSize();
            if (dim1 == null || dim2 == null) return false;
            return (dim1.width==dim2.width && dim1.height==dim2.height);
        }
    }
    
    private class WCResolutionSelector implements ItemListener 
    {
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                WCResolution res = (WCResolution) e.getItem();
                final Dimension size = res.getSize();
                if (size == null) return;
                
                WebcamViewer.CACHE.setProperty("webcam.width", size.width+"");
                WebcamViewer.CACHE.setProperty("webcam.height", size.height+"");
                String wc_name = webcam.getDevice().getName();
                initWebcam(wc_name); 
            }
        }
    }    
    
    // </editor-fold>
        
    // <editor-fold defaultstate="collapsed" desc=" DefaultLayout "> 
    
    private class DefaultLayout implements LayoutManager 
    {
        WebcamPane root = WebcamPane.this;
        
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
                int w=320, h=240;
                Insets margin = parent.getInsets();
                w += margin.left + margin.right;
                h += margin.top + margin.bottom;
                return new Dimension(w, h); 
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
                
                Component c = root.wcp;
                if (c != null) {
                    Component toolbar = root.toolbar;
                    if (toolbar != null) {
                        Dimension dim = toolbar.getPreferredSize();
                        int ny1 = ph - margin.top;
                        int ny2 = (ph - margin.bottom)-dim.height;
                        int ny = Math.max(ny1, ny2); 
                        toolbar.setBounds(x, ny, w, dim.height);
                        h -= dim.height;
                    }
                    
                    Dimension dim = c.getPreferredSize();
                    c.setBounds(x, y, w, Math.max(h, 0));
                }                 
            } 
        }
    }
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" ToolbarLayout "> 
    
    private class ToolbarLayout implements LayoutManager 
    {
        WebcamPane root = WebcamPane.this;
        private Vector<Component> leftComponents = new Vector();
        private Vector<Component> rightComponents = new Vector();
        
        public void addLayoutComponent(String name, Component comp) {
            if (comp == null) return;
            if ("right".equalsIgnoreCase(name+"")) {
                rightComponents.removeElement(comp);
                rightComponents.addElement(comp);
            } else {
                leftComponents.removeElement(comp);
                leftComponents.addElement(comp);
            } 
        }
        
        public void removeLayoutComponent(Component comp) {
            if (comp == null) return;
            leftComponents.removeElement(comp);
            rightComponents.removeElement(comp);
        }

        public Dimension preferredLayoutSize(Container parent) {
            return layoutSize(parent);
        }

        public Dimension minimumLayoutSize(Container parent) {
            return layoutSize(parent);
        }

        private Dimension layoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                int w=0, h=0;
                Component[] comps = parent.getComponents();
                for (int i=0; i<comps.length; i++) {
                    Component c = comps[i];
                    if (!c.isVisible()) continue;
                    
                    Dimension dim = c.getPreferredSize(); 
                    w += dim.width + 3;
                    h = Math.max(dim.height, h); 
                }
                
                Insets margin = parent.getInsets();
                w += margin.left + margin.right;
                h += margin.top + margin.bottom;
                return new Dimension(w, h); 
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
                
                int size = 0;
                for (int i=0; i<leftComponents.size(); i++) {
                    Component c = leftComponents.get(i);
                    if (!c.isVisible()) continue;
                    
                    Dimension dim = c.getPreferredSize(); 
                    c.setBounds(x, y, dim.width, h); 
                    x += (dim.width + 3);
                }
                
                int remWidth = (pw - margin.right);
                int rightWidth = getRightWidth();           
                int rightX = remWidth - (x + rightWidth);
                if (rightX < 0) rightX = 0;
                
                x += rightX;
                for (int i=0; i<rightComponents.size(); i++) {
                    Component c = rightComponents.get(i);
                    if (!c.isVisible()) continue;
                    
                    Dimension dim = c.getPreferredSize(); 
                    c.setBounds(x, y, dim.width, h); 
                    x += (dim.width + 3);
                } 
            } 
        }
        
        private int getRightWidth() {
            int width = 0;
            for (int i=0; i<rightComponents.size(); i++) {
                Component c = rightComponents.get(i);
                if (!c.isVisible()) continue;

                Dimension dim = c.getPreferredSize(); 
                width += (dim.width + 3);
            }
            return width; 
        }
    }
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" FooterLayout "> 
    
    private class FooterLayout implements LayoutManager 
    {
        WebcamPane root = WebcamPane.this;
        
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
                int w=0, h=0;
                Component[] comps = parent.getComponents();
                for (int i=0; i<comps.length; i++) {
                    Component c = comps[i];
                    if (!c.isVisible()) continue;
                    
                    Dimension dim = c.getPreferredSize(); 
                    w += dim.width + 3;
                    h = Math.max(dim.height, h); 
                }
                
                Insets margin = parent.getInsets();
                w += margin.left + margin.right;
                h += margin.top + margin.bottom;
                return new Dimension(w, h); 
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
                
                int size = 0;
                Component[] comps = parent.getComponents();
                for (int i=0; i<comps.length; i++) {
                    Component c = comps[i];
                    if (!c.isVisible()) continue;
                    
                    Dimension dim = c.getPreferredSize(); 
                    size += dim.width + 3;
                }
                
                x = Math.max((w - size) / 2, 0) + margin.left; 
                for (int i=0; i<comps.length; i++) {
                    Component c = comps[i];
                    if (!c.isVisible()) continue;
                    
                    Dimension dim = c.getPreferredSize(); 
                    c.setBounds(x, y, dim.width, h); 
                    x += dim.width + 3; 
                }
            } 
        }
    }
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" WebcamPanelSupport "> 
    
    private class WebcamPanelSupport implements WebcamListener
    {
        WebcamPane root = WebcamPane.this;
        
        public void webcamOpen(WebcamEvent we) {
            cboDevices.setEnabled(true);
            cboSizes.setEnabled(true);
            btnShoot.setEnabled(true);
            btnShoot.repaint();
            btnShoot.requestFocus(); 
        }

        public void webcamClosed(WebcamEvent we) {
            cboSizes.setEnabled(false);
            btnShoot.setEnabled(false);
            btnShoot.repaint();
            if (btnShoot.hasFocus()) btnShoot.transferFocus();

            removeWebcamPanel(); 
        }

        public void webcamDisposed(WebcamEvent we) {
            cboSizes.setEnabled(false);
            btnShoot.setEnabled(false);
            btnShoot.repaint();
            if (btnShoot.hasFocus()) btnShoot.transferFocus();
        }

        public void webcamImageObtained(WebcamEvent we) {
        }
        
        private void removeWebcamPanel() {
            WebcamPanel wcp = root.wcp; 
            if (wcp == null) return;
            
            if (wcp != null) { 
                root.remove(wcp); 
                root.wcp = null; 
                root.revalidate();
                root.repaint();
            } 
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ShootActionSupport "> 
    
    private class ShootActionSupport implements ActionListener 
    {
        WebcamPane root = WebcamPane.this;
        
        private JButton button;
        
        ShootActionSupport(JButton button) {
            this.button = button;
        }
        
        public void actionPerformed(ActionEvent e) {
            button.setEnabled(false); 
            button.setText("Processing...");
            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    runImpl(); 
                }
            });
        } 
        
        private void runImpl() {
            try {
                shoot();
            } catch(Throwable t) {
                MsgBox.err(t); 
            } finally {
                button.setText("Shoot"); 
                button.setEnabled(true); 
            }
        }
        
        private void shoot() {
            BufferedImage bi = null; 
            try { 
                bi = root.webcam.getImage(); 
            } catch(Throwable t) { 
                MsgBox.err(t); 
            } 

            if (bi == null) return; 

            byte[] bytes = null; 
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
                ImageIO.write(bi, "JPG", baos); 
                bytes = baos.toByteArray(); 
            } catch (Throwable t) { 
                MsgBox.err(t);  
                return; 
            } 

            if (bytes == null) return;

            try { 
                if (autoCloseOnSelect) root.stop(); 
            } catch(Throwable t){;} 
            
            for (WebcamPaneListener listener : root.listeners) { 
                listener.onselect(bytes); 
            } 
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" CancelActionSupport "> 
    
    private class CancelActionSupport implements ActionListener 
    {
        WebcamPane root = WebcamPane.this;
        
        public void actionPerformed(ActionEvent e) {
            try { 
                root.stop(); 
            } catch(Throwable t) {
                MsgBox.err(t); 
            } 
            
            try { 
                cancel(); 
            } catch(Throwable t) {
                MsgBox.err(t); 
            }            
        } 
        
        private void cancel() {
            for (WebcamPaneListener listener : root.listeners) { 
                listener.oncancel(); 
            } 
        }
    }
    
    // </editor-fold>
}
