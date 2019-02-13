/*
 * SubWindowImpl.java
 *
 * Created on November 20, 2013, 4:41 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.platform;

import com.rameses.platform.interfaces.ContentPane;
import com.rameses.platform.interfaces.SubWindow;
import com.rameses.platform.interfaces.SubWindowListener;
import com.rameses.platform.interfaces.ViewContext;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.util.Map;
import javax.swing.JPanel;

/**
 *
 * @author wflores 
 */
class SubWindowImpl extends JPanel implements SubWindow, ContentPane  
{
    private String id;
    private String title;
    private boolean canClose = true;

    private OSPlatform platform;    
    private ViewContext viewContext;
    private DefaultLayout defaultLayout;
    private OSView view;
    
    public SubWindowImpl(String id, Component comp, OSPlatform platform) {
        this(id, comp, platform, true);
    }
    
    public SubWindowImpl(String id, Component comp, OSPlatform platform, boolean canClose) {
        this.id = (id == null? "WIN"+new java.rmi.server.UID(): id);
        this.platform = platform;
        this.canClose = canClose;
        
        if (comp instanceof ViewContext) { 
            viewContext = (ViewContext) comp; 
            viewContext.setSubWindow(this); 
        } 

        super.setLayout(defaultLayout = new DefaultLayout()); 
        add(comp); 
    }

    public LayoutManager getLayout() { return defaultLayout; }
    public void setLayout(LayoutManager mgr) {} 
    
    public String getId() { return id; } 
    public void setId(String id) {
        this.id = id; 
    }
    
    public void setView(OSView view) {
        this.view = view; 
    }

    protected void addImpl(Component comp, Object constraints, int index) {
        removeAll();
        super.addImpl(comp, constraints, -1); 
    }
    
    public void activate() { 
        if ( viewContext != null ) {
            viewContext.activate(); 
        }
    }
        
    // <editor-fold defaultstate="collapsed" desc=" SubWindow implementation ">

    private SubWindowListener listener;
    
    public void setListener(SubWindowListener listener) {
        this.listener = listener;
    }
    
    public String getName() { return this.id; } 
    
    public String getTitle() { return title; }
    public void setTitle(String title) { 
        this.title = title; 
        
        if (this.listener != null) 
            this.listener.fireUpdate(this);
    }
    
    public void closeWindow() { close(); }

    public void update(Map attrs) {
        if (attrs == null || attrs.isEmpty()) return;
        
        Object oid = attrs.remove("id");
        if (oid != null) this.id = oid.toString();
        
        Object otitle = attrs.remove("title");
        if (otitle != null) this.title = otitle.toString();
        
        if (this.listener != null) 
            this.listener.fireUpdate(this); 
    }
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" ContentPane implementation ">

    public boolean isCanClose() { return canClose; }
    public void setCanClose(boolean canClose) { this.canClose = canClose; }
    
    public void close() {
        if ( !isCanClose() ) return; 
        if ( viewContext != null && !viewContext.close() ) return;
        
        if ( view != null ) {
            WindowContainer wc = view.getWindowContainer();
            if (wc != null) wc.remove(this); 
        } 
        
        OSManager.getInstance().unregisterView(getId()); 
    }
    
    public ContentPane.View getView() { 
        Component[] comps = getComponents();
        for (Component c : comps) {
            if (!c.isVisible() || !(c instanceof ContentPane.View)) continue; 
            
            return (ContentPane.View) c; 
        }
        return null; 
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" DefaultLayout ">
    
    private class DefaultLayout implements LayoutManager
    {
        public void addLayoutComponent(String name, Component comp) {}
        public void removeLayoutComponent(Component comp) {}

        public Dimension preferredLayoutSize(Container parent) {
            return getLayoutSize(parent);
        }

        public Dimension minimumLayoutSize(Container parent) {
            return getLayoutSize(parent);
        }
        
        private Dimension getLayoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                int w=0, h=0;
                
                Component comp = getFirstVisible(parent); 
                if (comp != null) {
                    Dimension dim = comp.getPreferredSize();
                    w = dim.width;
                    h = dim.height; 
                }
                
                Insets margin = parent.getInsets();
                w += (margin.left + margin.right);
                h += (margin.top + margin.bottom); 
                return new Dimension(w, h); 
            }
        }

        private Component getFirstVisible(Container parent) {
            Component[] comps = parent.getComponents();
            for (int i=0; i<comps.length; i++) {
                if (comps[i].isVisible()) return comps[i]; 
            }
            return null; 
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
                
                Component comp = getFirstVisible(parent); 
                if (comp == null) return;
                
                Dimension dim = comp.getPreferredSize();
                comp.setBounds(x, y, w, h); 
            } 
        }        
    }
    
    // </editor-fold>    

}
