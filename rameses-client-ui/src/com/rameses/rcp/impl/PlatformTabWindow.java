/*
 * PlatformTabWindow.java
 *
 * Created on July 20, 2011, 10:03 PM
 */

package com.rameses.rcp.impl;

import com.rameses.platform.interfaces.ContentPane;
import com.rameses.platform.interfaces.SubWindow;
import com.rameses.platform.interfaces.SubWindowListener;
import com.rameses.platform.interfaces.ViewContext;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

/**
 *
 * @author jaycverg
 */
public class PlatformTabWindow extends JPanel implements SubWindow, ContentPane  
{
    private String id;
    private String title;
    private boolean canClose = true;
    private ViewContext viewContext;
    private PlatformImpl platform;
    
    public PlatformTabWindow(String id, Component comp, PlatformImpl platform) {
        this(id, comp, platform, true);
    }
    
    public PlatformTabWindow(String id, Component comp, PlatformImpl platform, boolean canClose) {
        this.id = (id == null? "WIN"+new java.rmi.server.UID(): id);
        this.platform = platform;
        this.canClose = canClose;
        
        registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                close();
            }
        }, KeyStroke.getKeyStroke("ctrl W"), JComponent.WHEN_IN_FOCUSED_WINDOW);

        if (comp instanceof ViewContext) { 
            viewContext = (ViewContext) comp; 
            viewContext.setSubWindow(this); 
        } 
        
        setLayout(new BorderLayout()); 
        add(comp); 
    }

    public void close() {
        if ( !canClose ) return;
        if ( viewContext != null && !viewContext.close() ) return;
        
        Component p = getParent();
        if (p instanceof ExtTabbedPane) 
            ((ExtTabbedPane) p).remove(this);                
        if (platform != null) 
            platform.windows.remove(id);
    }

    public boolean isCanClose() { return canClose; }
    public void setCanClose(boolean canClose) { this.canClose = canClose; }
    
    public void activate() {
        Component p = getParent();
        if (p instanceof ExtTabbedPane) { 
            ((ExtTabbedPane) p).setSelectedComponent(this);   
        } 
        if ( viewContext != null ) {
            viewContext.activate(); 
        } 
    }
    
    public ContentPane.View getView() { 
        Component[] comps = getComponents();
        for (Component c : comps) {
            if (!(c instanceof ContentPane.View)) continue; 
            
            return (ContentPane.View) c; 
        }
        return null; 
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

        if (this.listener != null) { 
            this.listener.fireUpdate(this); 
        } 
    }
    
    // </editor-fold>    

}
