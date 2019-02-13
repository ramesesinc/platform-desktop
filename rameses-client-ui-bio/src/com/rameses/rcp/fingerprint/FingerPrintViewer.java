/*
 * FingerPrintViewer.java
 *
 * Created on December 8, 2013, 11:19 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.fingerprint;

import com.digitalpersona.uareu.Reader;
import com.digitalpersona.uareu.ReaderCollection;
import com.digitalpersona.uareu.UareUException;
import com.digitalpersona.uareu.UareUGlobal;
import com.rameses.rcp.common.CallbackHandlerProxy;
import com.rameses.rcp.common.FingerPrintModel;
import com.rameses.rcp.common.MsgBox;
import com.rameses.rcp.control.border.XEtchedBorder;
import com.rameses.rcp.support.ImageIconSupport;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;

/**
 *
 * @author wflores 
 */
public final class FingerPrintViewer 
{
    
    public static void open(Map options) {
        new FingerPrintViewer(options).open();
    } 
    
    public static void open(FingerPrintModel model) {
        new FingerPrintViewer(model).open();
    } 
    
    private FingerPrintModel model;    
    private Reader reader;
    
    public FingerPrintViewer() {
        this(new FingerPrintModel()); 
    }

    public FingerPrintViewer(Map options) { 
        this.model = new FingerPrintModelProxy(options); 
    } 
    
    public FingerPrintViewer(FingerPrintModel model) {
        this.model = (model == null? new FingerPrintModel(): model); 
    }
    
    private Reader getReader() {
        if (reader == null) {
            try { 
                ReaderCollection collection = UareUGlobal.GetReaderCollection();  
                collection.GetReaders(); 
                reader = (collection.isEmpty()? null: collection.get(0)); 
            } catch(RuntimeException re) {
                throw re;
            } catch(Exception e) {
                throw new RuntimeException(e.getMessage(), e); 
            }
        }
        return reader; 
    }
    
    public byte[] open() { 
        Window win = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusedWindow();
        
        final Reader reader = getReader(); 
        if (reader == null) throw new NullPointerException("No available fingerprint reader");
        
        try { 
            reader.Open(Reader.Priority.COOPERATIVE); 
        } catch(RuntimeException re) {
            throw re;
        } catch(UareUException ue) {
            String str = String.format("%s returned DP error %d \n%s", "Reader.Open", (ue.getCode() & 0xffff), ue.toString());
            throw new RuntimeException(str); 
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage(), e); 
        }
        
        final FingerPrintPanel panel = new FingerPrintPanel(); 
        panel.setFingerType( model.getFingerType() ); 
        
        String title = model.getTitle();
        if (title == null) title = "FingerPrint Capture";
        
        JDialog dialog = null; 
        if (win instanceof Frame) {
            dialog = new JDialog((Frame) win); 
        } else if (win instanceof Dialog) {
            dialog = new JDialog((Dialog) win); 
        } else {
            dialog = new JDialog(); 
        } 
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); 
        dialog.setModal(true);
        dialog.setResizable(false); 
        dialog.setTitle(title);
        
        Toolbar toolbar = new Toolbar(dialog, panel); 
        JPanel contentPane = new JPanel(); 
        contentPane.setLayout(new DefaultLayout(panel, toolbar)); 
        contentPane.add(panel); 
        contentPane.add(toolbar); 
        dialog.setContentPane(contentPane); 
        
        dialog.pack(); 
        dialog.addWindowListener(new WindowListener() { 
            public void windowActivated(WindowEvent e) {} 
            public void windowClosed(WindowEvent e) {} 
            public void windowClosing(WindowEvent e) { 
                close(panel); 
                fireOnClose(); 
            } 
            
            public void windowDeactivated(WindowEvent e) {} 
            public void windowDeiconified(WindowEvent e) {} 
            public void windowIconified(WindowEvent e) {} 
            
            public void windowOpened(WindowEvent e) { 
                panel.start(reader, false); 
            } 
        }); 
        centerWindow(dialog);
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
    
    private void close(FingerPrintPanel panel) {
        try { 
            panel.stop(); 
            panel.waitForCaptureThread(); 
        } catch(Throwable t) {
            MsgBox.err(t); 
        } 

        try { 
            reader.Close();  
        } catch(Throwable t) { 
            t.printStackTrace(); 
        } 
    } 
    
    private void fireOnClose() { 
        model.onclose(); 
    } 
    
    private void fireOnSelect(ImageContext[] results) { 
        model.onselect(new FingerPrintResultInfo(results)); 
    } 
    
    // <editor-fold defaultstate="collapsed" desc=" DefaultLayout "> 
    
    private class DefaultLayout implements LayoutManager 
    {
        private JComponent panel;
        private JComponent toolbar;
        
        DefaultLayout(JComponent panel, JComponent toolbar) {
            this.panel = panel; 
            this.toolbar = toolbar; 
        }
        
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
                if (panel != null) {
                    Dimension dim = panel.getPreferredSize(); 
                    w = dim.width;
                    h = dim.height; 
                }
                if (toolbar != null) {
                    Dimension dim = toolbar.getPreferredSize(); 
                    w = Math.max(dim.width, w); 
                    h += dim.height; 
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
                
                if (panel != null) {
                    Dimension dim = panel.getPreferredSize();
                    panel.setBounds(x, y, w, dim.height);
                    y += dim.height;
                } 
                if (toolbar != null) {
                    Dimension dim = toolbar.getPreferredSize();
                    toolbar.setBounds(x, y, w, dim.height);
                }
            } 
        }
    }
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" Toolbar "> 
    
    private class Toolbar extends JPanel implements PropertyChangeListener 
    {
        FingerPrintViewer root = FingerPrintViewer.this;
        
        private JDialog dialog;
        private FingerPrintPanel panel;
        
        private JButton btnOK;
        private JButton btnCancel;
        
        Toolbar(JDialog dialog, FingerPrintPanel panel) {
            this.dialog = dialog;
            this.panel = panel;
            
            setLayout(new ToolbarLayout()); 
            panel.removePropertyChangeListener(this);
            panel.addPropertyChangeListener(this); 
            
            XEtchedBorder border = new XEtchedBorder();
            border.setPadding(new Insets(2,5,5,5));
            border.setHideLeft(true); 
            border.setHideBottom(true); 
            border.setHideRight(true); 
            setBorder(border); 
            initComponents(); 
        }
        
        private void initComponents() {
            btnOK = new JButton("Select");
            btnOK.setEnabled(false); 
            btnOK.setMargin(new Insets(3,7,3,7));
            btnOK.setIcon(ImageIconSupport.getInstance().getIcon("images/toolbars/approve.png"));  
            btnOK.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    doSelect(e);
                }
            });
            add(btnOK); 

            btnCancel = new JButton("Cancel");
            btnCancel.setMargin(new Insets(3,7,3,7));
            btnCancel.setIcon(ImageIconSupport.getInstance().getIcon("images/toolbars/cancel.png")); 
            btnCancel.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    doCancel(e);
                }
            }); 
            add(btnCancel);                     
        }
        
        private void doSelect(ActionEvent e) { 
            dialog.dispose(); 
            root.close(panel); 
            root.fireOnSelect(panel.getImageContexts());
        }
        
        private void doCancel(ActionEvent e) {
            dialog.dispose(); 
            root.close(panel); 
            root.fireOnClose();
        } 

        public void propertyChange(PropertyChangeEvent evt) {
            String name = evt.getPropertyName();
            if ("imageDataChanged".equals(name)) {
                btnOK.setEnabled(evt.getNewValue() instanceof byte[]);
                btnOK.repaint(); 
            }
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ToolbarLayout "> 
    
    private class ToolbarLayout implements LayoutManager 
    {
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

    // <editor-fold defaultstate="collapsed" desc=" FingerPrintModelProxy "> 
    
    private class FingerPrintModelProxy extends FingerPrintModel 
    {
        private Map options; 
        private String title;
        private Integer fingerType; 
        private CallbackHandlerProxy onselectCallback;
        private CallbackHandlerProxy oncloseCallback;
        
        FingerPrintModelProxy(Map options) {
            this.options = options;
            this.title = getString(options, "title"); 
            this.fingerType = getInt(options, "fingerType"); 
            
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

        public int getFingerType() {
            if (fingerType == null) {
                return super.getFingerType(); 
            } else { 
                return fingerType.intValue(); 
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
        
        private Object get(Map map, String name) {
            return (map == null? null: map.get(name)); 
        }
    }
    
    // </editor-fold>
}
