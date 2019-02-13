/*
 * ExtTabbedPane.java
 *
 * Created on July 20, 2011, 10:04 PM
 */

package com.rameses.rcp.impl;

import com.rameses.platform.interfaces.ContentPane;
import com.rameses.platform.interfaces.SubWindow;
import com.rameses.platform.interfaces.SubWindowContainer;
import com.rameses.platform.interfaces.SubWindowListener;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Hashtable;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author jaycverg
 */
public class ExtTabbedPane extends JTabbedPane implements SubWindowContainer 
{    
    private Map<String,Component> tabIndex;
    private Rectangle closeIconBounds;
    private boolean closeIconHover;
        
    public ExtTabbedPane() {
        initComponent(); 
    }
    
    private void initComponent() {
        tabIndex = new Hashtable();
        closeIconBounds = new Rectangle(0,0,10,10);
        setFocusable(false);
        
        TabSupport support = new TabSupport();
        addChangeListener(support);
        addMouseListener(support);
        addMouseMotionListener(support); 
    }
    
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Component comp = getSelectedComponent();
        if( comp instanceof PlatformTabWindow && !((PlatformTabWindow)comp).isCanClose() ) return;
        
        int idx = getSelectedIndex();
        if( idx < 0 ) return;
        Rectangle rec = getBoundsAt( idx );
        
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = closeIconBounds.width;
        int h = closeIconBounds.height;
        int x = rec.x + rec.width - w - 8;
        int y = rec.y + (rec.height/2) - (h/2);
        
        g2.setColor(closeIconHover? Color.RED : Color.BLACK);
        g2.drawLine(x+2, y+2, x+w-3, y+h-3);
        g2.drawLine(x+w-3, y+2, x+2, y+h-3);
        
        closeIconBounds.x = x;
        closeIconBounds.y = y;
        
        g2.setColor(closeIconHover? UIManager.getColor("Separator.shadow") : UIManager.getColor("control"));
        Rectangle cib = closeIconBounds;
        g2.drawRoundRect(cib.x, cib.y, cib.width-1, cib.height-1, 3,3);
        g2.dispose();
    }
    
    public void insertTab(String title, Icon icon, Component component, String tip, int index) {
        final String _title = title + "          ";
        final Icon _icon = icon;
        final Component _component = component;
        final String _tip = tip;
        final int _index = index;
        
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                String tabid = _component.getName();
                if (tabid == null) 
                { 
                    tabid = "WIN"+new java.rmi.server.UID();
                    _component.setName(tabid);
                }
                
                Component old = tabIndex.get(tabid);
                if (old != null) {
                    if (indexOfComponent(old) >= 0) {
                        setSelectedComponent(old);
                        return;
                    }
                }
                
                ExtTabbedPane.super.insertTab(_title, _icon, _component, _tip, _index);
                setSelectedIndex(_index);
                tabIndex.put(tabid, _component);
            }
        });
    }
    
    public void remove(Component component) {
        int idx = indexOfComponent(component);
        if (idx >= 0) 
        {
            String title = getTitleAt(idx);
            tabIndex.remove(title);
            
            String cname = component.getName();
            if (cname != null) tabIndex.remove(cname);
        }
        super.remove(component);
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
    
    private class TabSupport implements MouseListener, MouseMotionListener, ChangeListener 
    {
        public void mousePressed(MouseEvent e) {}
        public void mouseReleased(MouseEvent e) {}
        public void mouseEntered(MouseEvent e) {}
        public void mouseDragged(MouseEvent e) {}        
        
        public void mouseClicked(MouseEvent e) {
            if( closeIconBounds.contains(e.getPoint()) ) {
                Component comp = getSelectedComponent();
                if( comp instanceof PlatformTabWindow ) {
                    ((PlatformTabWindow)comp).close();
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
        
        public void stateChanged(ChangeEvent e) {
            Component comp = getSelectedComponent(); 
            if ( comp instanceof PlatformTabWindow ) {
                ((PlatformTabWindow) comp).activate();
            }
        } 
    }

    // </editor-fold> 
}
