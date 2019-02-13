/*
 * XHorizontalPanel.java
 *
 * Created on April 28, 2013, 10:56 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control;

import com.rameses.rcp.control.layout.HorizontalLayout;
import com.rameses.rcp.control.layout.LayoutComponent;
import java.awt.Component;
import java.awt.LayoutManager;
import java.beans.Beans;
import javax.swing.JPanel;
import javax.swing.border.Border;

/**
 *
 * @author wflores
 */
public class XHorizontalPanel extends JPanel 
{
    private HorizontalLayout layoutMgr;
    private Border borderSeparator;
    private boolean showLeftSeparator;
    
    public XHorizontalPanel() 
    {
        this.layoutMgr = new HorizontalLayout(); 
        this.borderSeparator = this.layoutMgr.getSeparator(); 
        super.setLayout(this.layoutMgr); 
    }

    public LayoutManager getLayout() { return this.layoutMgr; }
    public void setLayout(LayoutManager mgr) {
        //do nothing
    }
    
    public Border getBorderSeparator() { return this.borderSeparator; }
    public void setBorderSeparator(Border borderSeparator) 
    {
        this.borderSeparator = borderSeparator; 
        this.layoutMgr.setSeparator(this.borderSeparator); 
    }
    
    public boolean isShowLeftSeparator() { return showLeftSeparator; } 
    public void setShowLeftSeparator(boolean showLeftSeparator) {
        this.showLeftSeparator = showLeftSeparator; 
        this.layoutMgr.setShowLeftSeparator(showLeftSeparator); 
    }
    
    // <editor-fold defaultstate="collapsed" desc=" for layout purposes ">   

    public boolean isVisible() {
        boolean b = super.isVisible();
        if (Beans.isDesignTime()) return b;        
        if (!b) return false;
        
        Component[] comps = getComponents();
        for (int i=0; i<comps.length; i++) {
            Component c = comps[i];
            if (c.isVisible()) return true; 
        }
        return false;
    }
    
    // </editor-fold>        
}
