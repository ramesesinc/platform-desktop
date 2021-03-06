/*
 * OSMainTabbedPane.java
 *
 * Created on November 18, 2013, 3:56 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.platform;

import com.rameses.platform.interfaces.ContentPane;
import com.rameses.platform.interfaces.SubWindow;
import com.rameses.platform.interfaces.SubWindowContainer;
import com.rameses.platform.interfaces.SubWindowListener;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Rectangle;
import java.util.Hashtable;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author wflores
 */
class OSMainTabbedPane extends WindowTabbedPane implements WindowContainer, SubWindowContainer 
{
    private Map<String,Component> tabIndex;
    private Rectangle closeIconBounds;
    private boolean closeIconHover;
    
    public OSMainTabbedPane() {
        tabIndex = new Hashtable();
        closeIconBounds = new Rectangle(0,0,10,10);
        setFocusable(false);     
        addChangeListener(new ItemSelector()); 
    }
    
    public boolean containsView(String id) {
        return (tabIndex.get(id) != null); 
    }
    
    public Component findWindow(String id) {
        return tabIndex.get(id); 
    }
    
    public void insertTab(String title, Icon icon, Component component, String tip, int index) {
        final String _title = title; 
        final Icon _icon = icon;
        final Component _component = component;
        final String _tip = tip;
        final int _index = index;
        
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                insertTabImpl(_title, _icon, _component, _tip, _index);                
            }
        });
    } 
    
    private void insertTabImpl(String title, Icon icon, Component component, String tip, int index) {
        SubWindowImpl subWindow = null;
        if (component instanceof SubWindowImpl) {
            subWindow = (SubWindowImpl) component;
            subWindow.setListener(new SubWindowHandler()); 
            
            String str = subWindow.getTitle(); 
            if (str != null && str.trim().length() > 0) title = str; 
        }        
        
        String tabid = (subWindow == null? component.getName(): subWindow.getId());
        if (tabid == null) { 
            tabid = "WIN" + new java.rmi.server.UID();
            if (subWindow == null) 
                component.setName(tabid); 
            else 
                subWindow.setId(tabid); 
        }

        Component old = tabIndex.get(tabid);
        if (old != null && indexOfComponent(old) >= 0) {
            setSelectedComponent(old);
            return;
        }

        super.insertTab(title, icon, component, tip, index);
        setSelectedIndex(index);
        tabIndex.put(tabid, component);
        
        OSViewImpl osv = new OSViewImpl(component); 
        OSManager.getInstance().registerView(tabid, osv); 
    }

    protected boolean beforeClose(Component component) {
        int idx = indexOfComponent(component);
        if (idx >= 0) {
            String title = getTitleAt(idx);
            tabIndex.remove(title);
            
            String cname = component.getName();
            if (cname != null) {
                tabIndex.remove(cname);
                OSManager.getInstance().unregisterView(cname); 
            }
        }
        return true; 
    }

    public void removeAll() {   
        super.removeAll();  
        tabIndex.clear(); 
    } 

    protected boolean isCloseable(int index) {
        Component comp = null; 
        try { comp = getComponentAt(index); } catch(Throwable t) {;} 

        if (comp instanceof ContentPane) {
            ContentPane cp = (ContentPane)comp;
            return cp.isCanClose();
        } else { 
            return false; 
        }
    }

    protected void afterClose(Component component) {
    }
        
    public void showInfo() {
        Component comp = getSelectedComponent();
        if (!(comp instanceof ContentPane)) return; 

        ContentPane cp = (ContentPane) comp;
        ContentPane.View vw = cp.getView();
        if (vw != null) vw.showInfo(); 
    }

    // <editor-fold defaultstate="collapsed" desc=" WindowContainer implementation ">
    
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" SubWindowContainer implementation ">
    
    public void add(SubWindow window) { 
        if (!(window instanceof JComponent)) 
            throw new IllegalStateException("window parameter must be an instance of SubWindow and JComponent"); 

        JComponent jc = (JComponent) window;
        jc.putClientProperty("SubWindow.id", window.getName()); 
        window.setListener(new SubWindowHandler()); 
        addTab(window.getTitle(), jc); 
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" SubWindowHandler (class) ">
    
    private class SubWindowHandler implements SubWindowListener
    {
        public void fireUpdate(SubWindow window) {
            if (window == null) return;
            if (!(window instanceof Component)) 
                throw new IllegalStateException("window parameter must be an instance of SubWindow and Component"); 

            JComponent jc = (JComponent) window;
            int index = indexOfComponent(jc);
            if (index < 0) return;

            setTitleAt(index, window.getTitle()+"          "); 
            String newId = window.getName();            
            String oldId = jc.getClientProperty("SubWindow.id")+""; 
            if (oldId != null && newId != null && !oldId.equals(newId)) {
                tabIndex.remove(oldId); 
                tabIndex.put(newId, jc); 
            }
            jc.putClientProperty("SubWindow.id", newId);
        } 
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ItemSelector (class) ">
    
    private class ItemSelector implements ChangeListener  {
        public void stateChanged(ChangeEvent e) {
            Component comp = getSelectedComponent(); 
            if ( comp instanceof ContentPane ) {
                ((ContentPane) comp).activate();
            }
        }         
    }

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" OSView support ">
    
    private class OSViewImpl implements OSView 
    {
        OSMainTabbedPane root = OSMainTabbedPane.this;
        
        Component view;
        SubWindowImpl subWindow;
        
        OSViewImpl(Component view) {
            this.view = view;
            
            if (view instanceof SubWindowImpl) {
                subWindow = (SubWindowImpl) view; 
                subWindow.setView(this); 
            }
        }
        
        public WindowContainer getWindowContainer() {
            return root; 
        }
        
        public String getId() { 
            return view.getName(); 
        } 
        
        public String getType() {
            return "tab"; 
        }
        
        public void requestFocus() { 
            if (indexOfComponent(view) >= 0) {
                root.setSelectedComponent(view); 
            }
            if (subWindow != null) {
                subWindow.activate();
            } 
        }  

        public void closeView() { 
            if (subWindow == null) {
                root.remove(view); 
            } else {
                subWindow.close(); 
            } 
        }
    }
    
    // </editor-fold>            
}
