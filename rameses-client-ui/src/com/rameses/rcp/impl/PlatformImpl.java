package com.rameses.rcp.impl;

import com.rameses.common.PropertyResolver;
import com.rameses.platform.interfaces.ContentPane;
import com.rameses.platform.interfaces.MainWindow;
import com.rameses.platform.interfaces.Notifier;
import com.rameses.platform.interfaces.Platform;
import com.rameses.platform.interfaces.SubWindow;
import com.rameses.rcp.util.ErrorDialog;
import com.rameses.util.ValueUtil;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;


/**
 *
 * @author jaycverg
 */
public class PlatformImpl implements Platform 
{
    private MainDialog mainWindow; 
    private NotifierImpl notifier;
    
    Map windows = new HashMap();
    
    public PlatformImpl() {
        mainWindow = new MainDialog(this); 
    }
    
    public void showStartupWindow(JComponent actionSource, JComponent comp, Map properties) {
        String id = (String) properties.get("id");
        if ( ValueUtil.isEmpty(id) )
            throw new IllegalStateException("id is required for a page.");
        
        if ( windows.containsKey(id) ) return;
        
        PlatformTabWindow tab = new PlatformTabWindow(id, comp, this, false);
        tab.setTitle( (String) properties.get("title") );
        mainWindow.setComponent(tab, MainWindow.CONTENT);
        windows.put(id, mainWindow);
    }
    
    public void showWindow(JComponent actionSource, JComponent comp, Map properties) {
        String id = (String) properties.get("id");
        if (id == null || id.trim().length() == 0)
            throw new IllegalStateException("id is required for a page.");
        
        if (windows.containsKey(id)) return;
        
        String title = (String) properties.get("title");
        if (title == null || title.length() == 0) {
            properties.put("title", id); 
            title = id;             
        }
        
        comp.putClientProperty("Window.properties", properties);        
        String windowmode = (String) properties.get("windowmode");
        if ("glasspane".equals(windowmode)) { 
            showGlassPane(actionSource, comp, properties, id); 
            return; 
        }
                
        String canClose = (String) properties.get("canclose");
        String modal = properties.remove("modal")+"";
        
        PlatformTabWindow t = new PlatformTabWindow(id, comp, this);
        t.setTitle(title);
        t.setCanClose( !"false".equals(canClose) );
        
        mainWindow.setComponent(t, MainWindow.CONTENT);
        windows.put(id, t);
    }
    
    public void showPopup(JComponent actionSource, JComponent comp, Map properties) {
        String id = (String) properties.get("id");
        if (id == null || id.trim().length() == 0)
            throw new IllegalStateException("id is required for a page.");
        
        if (windows.containsKey(id)) return;
        
        String title = (String) properties.get("title");
        if (title == null || title.length() == 0) {
            properties.put("title", id); 
            title = id;             
        }
        
        comp.putClientProperty("Window.properties", properties);        
        String windowmode = (String) properties.get("windowmode");
        if ("glasspane".equals(windowmode)) { 
            showGlassPane(actionSource, comp, properties, id); 
            return; 
        }
        
        String modal = properties.get("modal")+"";
        
        Component parent = getParentWindow(actionSource);
        PopupDialog dd = null;
        
        if ( parent instanceof JDialog ) 
            dd = new PopupDialog((JDialog) parent);
        else if ( parent instanceof JFrame ) 
            dd = new PopupDialog((JFrame) parent);
        else 
            dd = new PopupDialog(); 
        
        if ( properties.size() > 0 ) setProperties(dd, properties);
        
        final PopupDialog d = dd;
        d.setTitle(title);
        d.setContentPane(comp);
        d.setId( id );
        d.setPlatformImpl(this);
        d.setModal( "false".equals(modal)? false: true );
        
        if ("false".equals(properties.get("resizable")+"")) d.setResizable(false);
        if ("true".equals(properties.get("undecorated")+"")) d.setUndecorated(true);
        if ("true".equals(properties.get("headless")+"")) d.setUndecorated(true);
        if ("true".equals(properties.get("alwaysOnTop")+"")) { 
            d.setAlwaysOnTop(true);
            d.setModal( false ); 
        }
        
        d.pack();
        Dimension dim = d.getSize();
        int width = toInt(properties.get("width"));
        int height = toInt(properties.get("height"));
        int pWidth = (width<=0? dim.width: width);
        int pHeight = (height<=0? dim.height: height); 
        d.setSize(pWidth, pHeight); 
        d.setLocationRelativeTo(parent);
        d.setSource(actionSource);
                
        KeyStroke ks = KeyStroke.getKeyStroke("ctrl shift I");  
        ActionListener al = new ShowInfoAction(comp); 
        JRootPane rootPane = d.getRootPane(); 
        rootPane.registerKeyboardAction(al, ks, JComponent.WHEN_IN_FOCUSED_WINDOW); 
        
        Runnable runnable = new Runnable() {
            public void run() { 
                d.setVisible(true); 
            } 
        }; 
        
        windows.put(id, d);
        if ("true".equals(properties.get("immediate")+"")) {
            runnable.run(); 
        } else { 
            EventQueue.invokeLater(runnable); 
        }
    }
    
    private void showGlassPane(JComponent actionSource, JComponent comp, Map props, String id) 
    {
        Container con = mainWindow.getGlassPane();
        con.removeAll();
        con.add(comp);
        con.setName(id); 
        con.setVisible(true); 
        windows.put(id, con);
    }
    
    private void showInfo(ActionEvent e) {
        //System.out.println(e.getSource());
    }     
    
    private void setProperties(Object bean, Map properties) {
        PropertyResolver resolver = PropertyResolver.getInstance();
        for(Map.Entry me: (Set<Map.Entry>) properties.entrySet()) {
            try {
                resolver.setProperty( bean,me.getKey().toString(),  me.getValue());
            }catch(Exception e) {;}
        }
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
    
    public void showError(JComponent actionSource, Exception e) {
        ErrorDialog.show(e, actionSource);
    }
    
    public boolean showConfirm(JComponent actionSource, Object message) {
        return JOptionPane.showConfirmDialog(getParentWindow(actionSource), message, "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
    
    public void showInfo(JComponent actionSource, Object message) {
        JOptionPane.showMessageDialog(getParentWindow(actionSource), message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void showAlert(JComponent actionSource, Object message) {
        JOptionPane.showMessageDialog(getParentWindow(actionSource), message, "Warning", JOptionPane.WARNING_MESSAGE);
    }
    
    public Object showInput(JComponent actionSource, Object message) {
        return JOptionPane.showInputDialog(getParentWindow(actionSource), message);
    }
    
    public MainWindow getMainWindow() {
        return mainWindow;
    }
    
    public Notifier getNotifier() {
        if (notifier == null) {
            notifier = new NotifierImpl();
        }
        return notifier; 
    }
    
    public boolean isWindowExists(String id) {
        return windows.containsKey(id);
    }
    
    public void closeWindow(String id) {
        if ( windows.containsKey(id) ) {
            SubWindow d = (SubWindow) windows.get(id);
            d.closeWindow();
        }
    }
    
    public void activateWindow(String id) {
        if ( windows.containsKey(id) ) {
            SubWindow w = (SubWindow) windows.get(id);
            if( w instanceof JDialog )
                ((JDialog)w).requestFocus();
            else if ( w instanceof PlatformTabWindow )
                ((PlatformTabWindow)w).activate();
        }
    }
    
    public Map getWindows() { return windows; }
    
    private Window getParentWindow(JComponent src) {
        if ( src == null ) {
            Window w = KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow();
            if( w != null && w.isShowing() ) 
                return w;
            else
                return mainWindow.getComponent();
        }
        return SwingUtilities.getWindowAncestor(src);
    }
    
    private String getMessage(Throwable t) {
        if (t == null) return null;
        
        String msg = t.getMessage();
        Throwable cause = t.getCause();
        while (cause != null) {
            String s = cause.getMessage();
            if (s != null) msg = s;
            
            cause = cause.getCause();
        }
        return msg;
    }
    
    public void shutdown() {
        mainWindow.close();
    }
    
    public void logoff() {
        mainWindow.close();
    }
    
    public void lock() {
    }
    
    public void unlock() {
    }
    
    public void showFloatingWindow(JComponent owner, JComponent comp, Map properties) {
        showPopup(owner, comp, properties);
    }    
    
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
