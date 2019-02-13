/*
 * XEtchedBorder.java
 *
 * Created on October 18, 2010, 10:18 AM
 * @author wflores
 */

package com.rameses.rcp.control.border;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import javax.swing.border.AbstractBorder;

public class XEtchedBorder extends AbstractBorder 
{
    private Color highlight;
    private Color shadow;
    private Insets padding;
    private boolean raised;
    private boolean hideTop;
    private boolean hideLeft;
    private boolean hideBottom;
    private boolean hideRight;
    
    public XEtchedBorder() {
        this(null, null, false); 
    }
    
    public XEtchedBorder(Color highlight, Color shadow, boolean raised) {
        this.highlight = highlight;
        this.shadow = shadow;
        this.raised = raised;
        setPadding(null); 
    } 

    public Color getHighlightColor() { return highlight; }
    public void setHighlightColor(Color highlight) { this.highlight = highlight; }
    
    public Color getShadowColor() { return shadow; }
    public void setShadowColor(Color shadow) { this.shadow = shadow; }

    public boolean isRaised() { return raised; } 
    public void setRaised(boolean raised) { this.raised = raised; }
    
    public Insets getPadding() { return padding; } 
    public void setPadding(Insets padding) {  
        this.padding = (padding == null? new Insets(0,0,0,0): padding); 
    }
    
    public boolean isHideTop() { return hideTop; }
    public void setHideTop(boolean hideTop) { this.hideTop = hideTop; }

    public boolean isHideLeft() { return hideLeft; }
    public void setHideLeft(boolean hideLeft) { this.hideLeft = hideLeft; }

    public boolean isHideBottom() { return hideBottom; }
    public void setHideBottom(boolean hideBottom) { this.hideBottom = hideBottom; }

    public boolean isHideRight() { return hideRight; }
    public void setHideRight(boolean hideRight) { this.hideRight = hideRight; }    
            
    public Insets getBorderInsets(Component c) {
        Insets ins = new Insets(0,0,0,0);
        return getBorderInsets(c, ins);
    }
    
    public Insets getBorderInsets(Component c, Insets ins) {
        if (ins == null) ins = new Insets(0, 0, 0, 0);
                
        ins.top = ins.left = ins.bottom = ins.right = 0;
        if (!isHideTop()) ins.top+=2;
        if (!isHideLeft()) ins.left+=2;
        if (!isHideBottom()) ins.bottom+=2;
        if (!isHideRight()) ins.right+=2;
        
        ins.top += padding.top;
        ins.left += padding.left;
        ins.bottom += padding.bottom;
        ins.right += padding.right;
        return ins;
    }    
    
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        Color oldColor = g.getColor(); 
        Color shadColor = getShadowColor(c);
        Color highColor = getHighlightColor(c); 
        if (!isHideLeft()) {
            g2.setColor(shadColor); 
            g2.drawLine(x, y, x, height); 
            g2.setColor(highColor); 
            g2.drawLine(x+1, y+1, x+1, height); 
        } 
        if (!isHideRight()) {
            g2.setColor(shadColor); 
            g2.drawLine(width-2, y, width-2, height); 
            g2.setColor(highColor); 
            g2.drawLine(width-1, y, width-1, height); 
        }                 
        if (!isHideTop()) {
            g2.setColor(shadColor); 
            g2.drawLine(x, y, width, y); 
            g2.setColor(highColor); 
            g2.drawLine(x+1, y+1, width, y+1); 
        } 
        if (!isHideBottom()) {
            g2.setColor(shadColor); 
            g2.drawLine(x, height-2, width, height-2); 
            g2.setColor(highColor); 
            g2.drawLine(x, height-1, width, height-1); 
        } 
        g2.dispose();
    }    
    
    protected Color getHighlightColor(Component c) {
        Color color = getHighlightColor();
        return color != null? color : c.getBackground().brighter();
    }
    
    protected Color getShadowColor(Component c) {
        Color color = getShadowColor();
        return color != null? color : c.getBackground().darker();
    }    
}
 