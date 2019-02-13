/*
 * MainWindowPanel.java
 *
 * Created on October 24, 2013, 1:06 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.platform;

import java.awt.Component;
import java.awt.LayoutManager;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

/**
 *
 * @author wflores
 */
class MainWindowPanel extends JPanel 
{
    private static final long serialVersionUID = 1L;
    
    private MainWindowLayout layout; 
    
    public MainWindowPanel() {
        layout = new MainWindowLayout(); 
        super.setLayout(layout); 
    }
    
    public LayoutManager getLayout() { return layout; } 
    public void setLayout(LayoutManager mgr) {}

    public void setMenuBar(JMenuBar menubar) {
        removeMenuBar();
        add(menubar, MainWindowLayout.MENUBAR_SECTION);
    }
    
    public void setToolBar(Component toolbar) {
        removeToolBar();
        add(toolbar, MainWindowLayout.TOOLBAR_SECTION);
    } 
    
    public void setStatusBar(Component statusbar) {
        removeStatusBar();
        add(statusbar, MainWindowLayout.STATUSBAR_SECTION);
    } 
    
    public void setContent(Component content) {
        removeContent();
        add(content, MainWindowLayout.CONTENT_SECTION);
    } 
    
    public Component removeMenuBar() {
        Component comp = layout.getLayoutComponent(MainWindowLayout.MENUBAR_SECTION); 
        if (comp != null) remove(comp); 
        
        return comp; 
    } 
    
    public Component removeToolBar() {
        Component comp = layout.getLayoutComponent(MainWindowLayout.TOOLBAR_SECTION); 
        if (comp != null) remove(comp); 
        
        return comp;
    } 
    
    public Component removeStatusBar() {
        Component comp = layout.getLayoutComponent(MainWindowLayout.STATUSBAR_SECTION); 
        if (comp != null) remove(comp); 
        
        return comp;
    } 
    
    public Component removeContent() {
        Component comp = layout.getLayoutComponent(MainWindowLayout.CONTENT_SECTION); 
        if (comp != null) remove(comp); 
        
        return comp;
    } 
}
