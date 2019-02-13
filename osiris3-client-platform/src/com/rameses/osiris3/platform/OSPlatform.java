/*
 * OSPlatform.java
 *
 * Created on October 24, 2013, 9:35 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.platform;

import com.rameses.platform.interfaces.ContentPane;
import com.rameses.platform.interfaces.MainWindow;
import com.rameses.platform.interfaces.Platform;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

/**
 *
 * @author wflores
 */
class OSPlatform implements Platform 
{
    private OSManager osManager; 
    private OSMainWindow osMainWindow; 
    
    public OSPlatform(OSManager osManager) {
        this.osManager = osManager;
        this.osMainWindow = osManager.getMainWindow(); 
    }

    // <editor-fold defaultstate="collapsed" desc=" Getters/Setters ">

    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Platform implementation ">
    
    public void showStartupWindow(JComponent actionSource, JComponent comp, Map props) {
        String id = (String) props.get("id");
        if (id == null || id.trim().length() == 0) 
            throw new IllegalStateException("id is required for a page.");
        
        if (osManager.containsView(id)) return;
        
        SubWindowImpl view = new SubWindowImpl(id, comp, this, false); 
        view.setTitle((String) props.get("title"));
        osMainWindow.setComponent(view, MainWindow.CONTENT); 
    } 

    public void showWindow(JComponent actionSource, JComponent comp, Map props) {
        String id = (String) props.get("id");
        if (id == null || id.trim().length() == 0)
            throw new IllegalStateException("id is required for a page.");

        if (osManager.containsView(id)) return;        
        if (osMainWindow.findExplorer(id) != null) return;

        String title = (String) props.get("title");
        if (title == null || title.length() == 0) {
            props.put("title", id); 
            title = id;             
        }
        
        comp.putClientProperty("Window.properties", props);        
        String windowmode = (String) props.get("windowmode");
        if ("glasspane".equals(windowmode)) { 
            showGlassPane(actionSource, comp, props, id); 
            return; 
        }
        
        SubWindowImpl view = new SubWindowImpl(id, comp, this); 
        String canClose = (String) props.get("canclose");
        view.setCanClose(!"false".equals(canClose));
        view.setTitle(title);
        
        if ("explorer".equals(windowmode)) { 
            osMainWindow.setComponent(view, windowmode); 
        } else { 
            osMainWindow.setComponent(view, MainWindow.CONTENT); 
        } 
    }

    public void showPopup(JComponent actionSource, JComponent comp, Map props) {
        final String id = (String) props.get("id");
        if (id == null || id.trim().length() == 0)
            throw new IllegalStateException("id is required for a page.");
        
        if (osManager.containsView(id)) return;

        String title = (String) props.get("title");
        if (title == null || title.length() == 0) {
            props.put("title", id); 
            title = id;             
        }
        
        comp.putClientProperty("Window.properties", props);         
        String windowmode = (String) props.get("windowmode");
        if ("screenlock".equals(windowmode)) { 
            showScreenLock(actionSource, comp, props); 
            return;
        } 
        if ("glasspane".equals(windowmode)) { 
            showGlassPane(actionSource, comp, props, id); 
            return; 
        }        
        
        Component parent = getParentWindow(actionSource);
        OSPopupDialog dialog = null;
        
        if (parent instanceof JDialog) 
            dialog = new OSPopupDialog((JDialog) parent);
        else if ( parent instanceof JFrame ) 
            dialog = new OSPopupDialog((JFrame) parent);
        else 
            dialog = new OSPopupDialog(); 
        
        if (!props.isEmpty()) setProperties(dialog, props);
        
        final OSPopupDialog dx = dialog;
        dx.setId(id);        
        dx.setModal(true);
        dx.setTitle(title);
        dx.setPlatformImpl(this);
        dx.setContentPane(comp);
        
        String modal = props.get("modal")+""; 
        if ("false".equalsIgnoreCase(modal)) {
            dx.setModal(false); 
        } 
        if ("false".equals(props.get("resizable")+"")) {
            dx.setResizable(false);
        }
        if ("true".equals(props.get("undecorated")+"")) {
            dx.setUndecorated(true);
        } 
        if ("true".equals(props.get("alwaysOnTop")+"")) {
            dx.setAlwaysOnTop(true);
            dx.setModal( false );  
        }
        
        dx.pack();
        Dimension dim = dx.getSize();
        int width = toInt(props.get("width"));
        int height = toInt(props.get("height"));
        int pWidth = (width<=0? dim.width: width);
        int pHeight = (height<=0? dim.height: height); 
        dx.setSize(pWidth, pHeight); 
        dx.setLocationRelativeTo(parent);
        dx.setSource(actionSource);
        
        KeyStroke ks = KeyStroke.getKeyStroke("ctrl shift I");  
        ActionListener al = new ShowInfoAction(comp); 
        JRootPane rootPane = dx.getRootPane(); 
        rootPane.registerKeyboardAction(al, ks, JComponent.WHEN_IN_FOCUSED_WINDOW); 
        
        Runnable runnable = new Runnable() {
            public void run() { 
                if (!osManager.containsView(id)) { 
                    dx.setVisible(true); 
                }
            } 
        }; 
        
        if ("true".equals(props.get("immediate")+"")) {
            runnable.run(); 
        } else { 
            EventQueue.invokeLater(runnable); 
        }
    }

    public void showFloatingWindow(JComponent owner, JComponent comp, Map props) {
        showPopup(owner, comp, props);
    }
    
    public void showScreenLock(JComponent actionSource, JComponent comp, Map props) {
        String id = (String) props.get("id");
        if (id == null || id.trim().length() == 0)
            throw new IllegalStateException("id is required for a page.");
        
        OSScreenLock lock = osManager.getScreenLock();
        if (lock != null) return; 
        
        lock = new OSScreenLock();
        lock.setName(id); 
        lock.updateBackgroundImage();
        lock.setContent(comp); 
        lock.setVisible(true); 
    }
    
    private void showGlassPane(JComponent actionSource, JComponent comp, Map props, String id) {
        osMainWindow.showInGlassPane(comp, props);
    }    

    public boolean isWindowExists(String id) {
        if (osManager.containsView(id)) return true;
        if (osMainWindow.findExplorer(id) != null) return true; 
        
        Component c = osManager.getScreenLock();
        return (c != null && id.equals(c.getName())); 
    }

    public void activateWindow(String id) {
        OSView view = osManager.lookupView(id);
        if (view != null) {
            view.requestFocus();             
        } else {
            Component comp = osMainWindow.findExplorer(id); 
            if (comp != null) comp.requestFocus();  
        }
    }

    public void closeWindow(String id) {
        if (id == null) return;
        
        OSView view = osManager.lookupView(id); 
        if (view == null) return;
        
        view.closeView(); 
    }

    public void showError(JComponent actionSource, Exception e) {
        ErrorDialog.show(e, actionSource); 
    }

    public boolean showConfirm(JComponent actionSource, Object message) {
        Component parent = getParentWindow(actionSource);
        int retval = JOptionPane.showConfirmDialog(parent, message, "Confirm", JOptionPane.YES_NO_OPTION);
        return (retval== JOptionPane.YES_OPTION);
    }

    public void showInfo(JComponent actionSource, Object message) {
        Component parent = getParentWindow(actionSource);
        JOptionPane.showMessageDialog(parent, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showAlert(JComponent actionSource, Object message) {
        Component parent = getParentWindow(actionSource);
        JOptionPane.showMessageDialog(parent, message, "Warning", JOptionPane.WARNING_MESSAGE);
    }

    public Object showInput(JComponent actionSource, Object message) { 
        Component parent = getParentWindow(actionSource);
        return JOptionPane.showInputDialog(parent, message);
    }

    public MainWindow getMainWindow() {
        return osMainWindow;
    }

    public void shutdown() {
        osMainWindow.close(); 
    } 

    public void logoff() {
        LogoffPanel panel = new LogoffPanel(); 
        osMainWindow.showInGlassPane(panel, null); 
        closeAllDialogs(); 
        closeAllNonDialogViews(); 
        osManager.stopScheduledTasks();
        
        OSScreenLock lock = osManager.getScreenLock(); 
        if (lock != null) lock.setVisible(false); 
    }

    public void lock() {
    }

    public void unlock() { 
        osMainWindow.hideGlassPane(); 
        OSScreenLock lock = osManager.getScreenLock(); 
        if (lock != null) lock.setVisible(false); 
    } 
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" helper methods ">
    
    private Window getParentWindow(JComponent source) {
        if (source == null ) {
            Window w = KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow();
            if (w != null && w.isShowing()) return w;

            return osMainWindow.getComponent();
        } 
        return SwingUtilities.getWindowAncestor(source);
    }
    
    private void setProperties(OSPopupDialog bean, Map props) {
        if ("false".equalsIgnoreCase(props.get("resizable")+"")) 
            bean.setResizable(false); 
        if ("true".equalsIgnoreCase(props.get("alwaysOnTop")+"")) 
            bean.setAlwaysOnTop(true); 
        if ("false".equalsIgnoreCase(props.get("enabled")+"")) 
            bean.setEnabled(false); 
        if ("true".equalsIgnoreCase(props.get("undecorated")+"")) 
            bean.setUndecorated(true); 
    }  
    
    private int toInt(Object value) 
    {
        if (value == null) 
            return -1; 
        else if (value instanceof Number)
            return ((Number) value).intValue();
        
        try {
            return Integer.parseInt(value.toString()); 
        } catch(Exception ex) {
            return -1; 
        } 
    } 
    
    private void closeAllDialogs() {
        List<OSView> list = osManager.findViews("popup");
        while (!list.isEmpty()) { 
            try { 
                list.remove(0).closeView();
            } catch(Throwable t) {
                t.printStackTrace();
            }
        }
    }
    
    private void closeAllNonDialogViews() {
        List<OSView> list = osManager.findViews("screenlock");
        while (!list.isEmpty()) { 
            try { 
                list.remove(0).closeView();
            } catch(Throwable t) {
                t.printStackTrace();
            }
        }
        osManager.unregisterAllViews(); 
    }
    
    // </editor-fold> p

    // <editor-fold defaultstate="collapsed" desc=" ShowInfoAction ">
    
    private class ShowInfoAction implements ActionListener 
    {
        private Component source;
        
        ShowInfoAction(Component source) {
            this.source = source; 
        }
        
        public void actionPerformed(ActionEvent e) { 
            if (!(source instanceof ContentPane.View)) return; 
            
            ((ContentPane.View) source).showInfo(); 
        } 
    }
    
    // </editor-fold>    
}
