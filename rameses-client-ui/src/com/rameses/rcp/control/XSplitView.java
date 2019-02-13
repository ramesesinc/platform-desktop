/*
 * XSplitView.java
 *
 * Created on August 25, 2013, 9:44 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control;

import com.rameses.rcp.control.layout.SplitViewLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import javax.swing.JPanel;

/**
 *
 * @author wflores
 */
public class XSplitView extends JPanel implements SplitViewLayout.Provider 
{
    private static final long serialVersionUID = 1L;
    
    private SplitViewLayout layout; 
    private Rectangle viewRect;
    private Rectangle dividerRect;
    private Point targetPoint;
    
    private boolean showDividerBorder; 
    
    public XSplitView() {
        layout = new SplitViewLayout(this); 
        super.setLayout(layout); 
    }
    
    public LayoutManager getLayout() { return layout; } 
    public void setLayout(LayoutManager mgr) {}
    
    public String getOrientation() { 
        return layout.getOrientation(); 
    }    
    public void setOrientation(String orientation) { 
        layout.setOrientation(orientation); 
    }
    
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
    
    public int getDividerLocationPercentage() { 
        return layout.getDividerLocationPercentage(); 
    } 
    public void setDividerLocationPercentage( int dividerLocationPercentage ) {
        layout.setDividerLocationPercentage( dividerLocationPercentage ); 
    }
    
    public boolean isShowDividerBorder() { 
        return layout.isShowDividerBorder(); 
    } 
    public void setShowDividerBorder( boolean showDividerBorder ) {
        layout.setShowDividerBorder( showDividerBorder ); 
    }
    
    public void paint(Graphics g) {
        super.paint(g); 
        if (dividerRect != null && targetPoint != null) { 
            Rectangle newRect = new Rectangle();
            newRect.x = dividerRect.x;
            newRect.y = dividerRect.y;
            newRect.width = dividerRect.width;
            newRect.height = dividerRect.height;
            if ("vertical".equalsIgnoreCase(getOrientation()+"")) {
                newRect.y = dividerRect.y + targetPoint.y;
            } else {                  
                newRect.x = dividerRect.x + targetPoint.x;
            }
            
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
}
