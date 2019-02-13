/*
 * MainViewPanel.java
 *
 * Created on October 24, 2013, 1:06 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.platform;

import com.rameses.platform.interfaces.ContentPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.KeyboardFocusManager;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JPanel;

/**
 *
 * @author wflores
 */
public class MainViewPanel extends JPanel implements MainViewLayout.Provider 
{
    private static final long serialVersionUID = 1L;
        
    private MainViewLayout layout; 
    private Rectangle viewRect;
    private Rectangle dividerRect;
    private Point targetPoint;
    
    private ExplorerView explorerView;
    
    public MainViewPanel() {
        super.setLayout(layout = new MainViewLayout(this)); 
        explorerView = new ExplorerView(); 
        addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                layout.propertyChange(evt); 
            }
        }); 
    }
    
    public LayoutManager getLayout() { return layout; } 
    public void setLayout(LayoutManager mgr) {}
        
    public int getDividerSize() { 
        return layout.getDividerSize(); 
    } 
    public void setDividerSize(int dividerSize) {
        layout.setDividerSize(dividerSize); 
    }
    
    public int getDividerLocation() { 
        return layout.getDividerLocation();
    } 
    public void setDividerLocation(int dividerLocation) {
        layout.setDividerLocation(dividerLocation); 
    }
    
    public void paint(Graphics g) {
        super.paint(g); 
        if (dividerRect != null && targetPoint != null) { 
            Rectangle newRect = new Rectangle();
            newRect.x = dividerRect.x;
            newRect.y = dividerRect.y;
            newRect.width = dividerRect.width;
            newRect.height = dividerRect.height;
            
            newRect.x = dividerRect.x + targetPoint.x;
            
            Color oldColor = g.getColor();
            Color newColor = getBackground();
            if (newColor == null) {
                newColor = Color.DARK_GRAY;
            } else {
                newColor = newColor.darker();
            }
            
            Graphics gg = g.create();             
            gg.setColor(newColor); 
            gg.fillRect(newRect.x, newRect.y, newRect.width, newRect.height); 
            gg.setColor(oldColor); 
            dividerRect = null; 
            targetPoint = null; 
        }
    }

    public void paintDividerHandle(Rectangle viewRect, Rectangle dividerRect, Point targetPoint) {
        this.viewRect = viewRect;
        this.dividerRect = dividerRect;
        this.targetPoint = targetPoint; 
        repaint();
    } 
    
    public Component getExplorer() {
        BorderLayout borderLayout = (BorderLayout) explorerView.getLayout(); 
        return borderLayout.getLayoutComponent(BorderLayout.CENTER); 
    }
    
    public void setExplorer(Component explorer, String title) {
        Component old = layout.getLayoutComponent(MainViewLayout.EXPLORER_SECTION);
        if (old != null) {
            String newname = (explorer == null? null: explorer.getName());
            String oldname = old.getName();
            if (oldname != null && oldname.equals(newname)) return;
            if (newname != null && newname.equals(oldname)) return;
            
            remove(old);
        } else {
            layout.setLocationIndex(200);
        }

        explorerView.removeAll();
        if (explorer != null) { 
            explorerView.setName(explorer.getName()); 
            explorerView.attachHeader(); 
            explorerView.setTitle(title); 
            explorerView.add(explorer);
            add(explorerView, MainViewLayout.EXPLORER_SECTION); 
        }
    }
    
    public void setContent(Component content) {
        Component old = layout.getLayoutComponent(MainViewLayout.CONTENT_SECTION);
        if (old != null) remove(old); 
        
        add(content, MainViewLayout.CONTENT_SECTION); 
    }    
    
    public void updateCanvas() {
        revalidate();
        repaint();
    }
    
    public boolean isExplorerInFocus() {
        KeyboardFocusManager kfm = KeyboardFocusManager.getCurrentKeyboardFocusManager(); 
        Component focusOwner = kfm.getFocusOwner();
        if (focusOwner == null) return false; 
        
        return explorerView.isAncestorOf(focusOwner); 
    }
    
    public void showExplorerInfo() 
    {
        Component comp = getExplorer(); 
        if (!(comp instanceof ContentPane)) return; 

        ContentPane cp = (ContentPane) comp;
        ContentPane.View vw = cp.getView();
        if (vw != null) vw.showInfo(); 
    }
    
    // <editor-fold defaultstate="collapsed" desc=" PropertyChangeSupport ">
    
    private class PropertyChangeSupport implements PropertyChangeListener
    {
        public void propertyChange(PropertyChangeEvent evt) {
            String name = evt.getPropertyName();
            if ("toggleLeftView".equals(name)) {
                layout.propertyChange(evt); 
            }
        }        
    }
    
    // </editor-fold>
    
}
