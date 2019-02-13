/*
 * OSTabbedPane2.java
 *
 * Created on July 20, 2011, 10:04 PM
 */

package com.rameses.osiris3.platform;

import com.rameses.platform.interfaces.ContentPane;
import com.rameses.platform.interfaces.SubWindow;
import com.rameses.platform.interfaces.SubWindowContainer;
import com.rameses.platform.interfaces.SubWindowListener;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Hashtable;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.JComponent;

/**
 *
 * @author jaycverg
 */
class OSTabbedPane2 extends WindowTabbedPane implements SubWindowContainer 
{    
    private final static long serialVersionUID = 1L;
    
    private Map<String,Component> tabIndex;
    private Rectangle closeIconBounds;
    private boolean closeIconHover;
    
    public OSTabbedPane2() {
        tabIndex = new Hashtable();
        closeIconBounds = new Rectangle(0,0,10,10);
        setFocusable(false);
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
        String tabid = component.getName();
        if (tabid == null) { 
            tabid = "WIN"+new java.rmi.server.UID();
            component.setName(tabid);
        }

        Component old = tabIndex.get(tabid);
        if (old != null) {
            if (indexOfComponent(old) >= 0) {
                setSelectedComponent(old);
                return;
            }
        }

        if (component instanceof OSTabbedView) {
            String s = ((OSTabbedView) component).getTitle(); 
            if (s != null && s.trim().length() > 0) {
                title = s; 
            } 
        }
        
        super.insertTab(title, icon, component, tip, index);
        setSelectedIndex(index);
        tabIndex.put(tabid, component);
        OSManager.getInstance().registerView(tabid, new OSViewImpl(component));
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
    
    // <editor-fold defaultstate="collapsed" desc=" TabSupport (class) ">
    
    private class TabSupport implements MouseListener, MouseMotionListener 
    {
        public void mousePressed(MouseEvent e) {}
        public void mouseReleased(MouseEvent e) {}
        public void mouseEntered(MouseEvent e) {}
        public void mouseDragged(MouseEvent e) {}        
        
        public void mouseClicked(MouseEvent e) {
            if( closeIconBounds.contains(e.getPoint()) ) {
                Component comp = getSelectedComponent();
                if (comp instanceof ContentPane) {
                    ((ContentPane)comp).close();
                    closeIconHover = false;
                }
            }
        }
        
        public void mouseExited(MouseEvent e) {
            closeIconHover = false;
        }
        
        public void mouseMoved(MouseEvent e) {
            if( closeIconBounds.contains(e.getPoint()) ) {
                closeIconHover = true;
            } else {
                closeIconHover = false;
            }
        }
    }

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" OSView support ">
    
    private class OSViewImpl implements OSView 
    {
        OSTabbedPane2 root = OSTabbedPane2.this;
        Component view;
        
        OSViewImpl(Component view) {
            this.view = view;
        }
        
        public String getId() { 
            return view.getName(); 
        } 
        
        public String getType() {
            return "tab"; 
        }
        
        public void requestFocus() { 
            if (view instanceof OSTabbedView) {
                ((OSTabbedView) view).activate(); 
            }            
        }  

        public void closeView() { 
            root.remove(view); 
        }
        
        public WindowContainer getWindowContainer() {
            return null; 
        }        
    }
    
    // </editor-fold>        
}
