/*
 * XSeparatorBorder.java
 *
 * Created on September 01, 2013, 8:36 AM
 * @author wflores
 */

package com.rameses.rcp.control.border;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;

public class XSeparatorBorder extends AbstractBorder 
{
    private String title;
    private Color color;
    private Insets padding;
    
    private int descent = 0;
    private int ascent = 16;
    private int height = 16;            
    
    public XSeparatorBorder() {
        this.color = Color.BLACK;
    }
    
    public String getTitle() { return title; } 
    public void setTitle(String title) { this.title = title; }
    
    public Color getColor() { return color; }
    public void setColor(Color color) { this.color = color; }
    
    public Insets getPadding() { return padding; } 
    public void setPadding(Insets padding) {  
        this.padding = (padding == null? new Insets(0,0,0,0): padding); 
    }
                
    public Insets getBorderInsets(Component c) {
        return getBorderInsets(c, new Insets(0, 0, 0, 0));
    }
    
    public Insets getBorderInsets(Component c, Insets ins) {
        if (ins == null) ins = new Insets(0, 0, 0, 0);
                
        ins.top = ins.left = ins.bottom = ins.right = 0;
        
        String title = getTitle();
        if (title != null) {
            Font font = c.getFont();
            try {
                if (font == null) 
                    font = UIManager.getLookAndFeelDefaults().getFont("Label.font"); 
            } catch(Throwable t){;} 
            
            if (font == null) font = Font.decode("-plain-");
            
            FontMetrics fm = c.getFontMetrics(font); 
            if (fm != null) {
               descent = fm.getDescent(); 
               ascent = fm.getAscent(); 
               height = fm.getHeight(); 
               ins.top += (ascent + descent);
            }
        }
        
        if (padding == null) padding = new Insets(0,0,0,0); 
        
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
        Color newColor = getColor();
        if (newColor == null) newColor = Color.BLACK;
        
        Color shadColor = getShadowColor(c);
        Color highColor = getHighlightColor(c); 

        int ny = y;
        String title = getTitle();
        if (title != null) {
            ny =  Math.max((ascent + descent)/2, 0);
        }
                
        g2.setColor(shadColor); 
        g2.drawLine(x, y, width, y); 
        g2.setColor(highColor); 
        g2.drawLine(x, y+1, width, y+1); 
        g2.setColor(newColor);
        if (title != null) {
            g.drawString(title, 0, 0);
        }
        g2.setColor(oldColor);
        g2.dispose();
    }    
    
    protected Color getHighlightColor(Component c) {
        return c.getBackground().brighter();
    }
    
    protected Color getShadowColor(Component c) {
        return c.getBackground().darker();
    }    
}
 