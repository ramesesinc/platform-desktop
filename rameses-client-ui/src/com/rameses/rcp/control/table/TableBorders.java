/*
 * TableBorders.java
 *
 * Created on August 5, 2013, 4:42 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control.table;

import com.rameses.rcp.support.ColorUtil;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.metal.MetalLookAndFeel;

/**
 *
 * @author wflores
 */
public class TableBorders 
{
    public final static Color BORDER_COLOR = new Color(204, 204, 204);
            
    public TableBorders() {
    }
    
    // <editor-fold defaultstate="collapsed" desc=" Helper ">    
    
    public static class Helper {
        
        public Color getBorderColor( Component c ) {
            if ( c instanceof JComponent ) {
                return getBorderColor((JComponent)c); 
            } else {
                return null; 
            }
        }
        
        public Color getBorderColor( JComponent jc ) { 
            Color color = null; 
            Object obj = jc.getClientProperty("Component.proxy");
            if ( obj instanceof JComponent ) {
                color = (Color) ((JComponent)obj).getClientProperty("Border.color"); 
            }
            if ( color == null ) {
                color = (Color) jc.getClientProperty("Border.color"); 
            }
            return (color == null? BORDER_COLOR : color); 
        }
        
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" DefaultBorder (class) ">
    
    public static class DefaultBorder extends AbstractBorder 
    {
        private Insets PADDING = new Insets(1, 1, 1, 1); 

        public Insets getBorderInsets(Component c)       {
            return getBorderInsets(c, new Insets(0,0,0,0)); 
        }

        public Insets getBorderInsets(Component c, Insets insets) {
            if (insets == null) insets = new Insets(0,0,0,0);
            
            insets.top = insets.left = insets.bottom = insets.right = 0; 
            insets.top += PADDING.top;
            insets.left += PADDING.left;
            insets.bottom += PADDING.bottom;
            insets.right += PADDING.right;
            return insets; 
        }
        
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) { 
            Color oldColor = g.getColor(); 
            Color newColor = null; 
            if ( c instanceof JComponent ) {
                newColor = (Color) ((JComponent)c).getClientProperty("Border.color"); 
            }
            if ( newColor == null ) {
                newColor = BORDER_COLOR; 
            }
            
            g.setColor( newColor );
            g.drawRect(0, 0, w-1, h-1);
            g.setColor( oldColor ); 
        }
    }

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" HeaderBorder (class) "> 
    
    public static class HeaderBorder extends AbstractBorder 
    {
        private boolean hideTop = true;
        private boolean hideLeft = false;
        private boolean hideBottom = false;
        private boolean hideRight = false;
        private JLabel jlabel = new JLabel();
        
        public HeaderBorder() {
            init(); 
        }
        
        public HeaderBorder(boolean hideTop, boolean hideLeft,  boolean hideBottom, boolean hideRight) { 
            init(); 
            this.hideTop = hideTop;
            this.hideLeft = hideLeft;
            this.hideBottom = hideBottom;
            this.hideRight = hideRight;            
        }
        
        private void init() {
            jlabel.setOpaque(true); 
        } 
                
        public boolean isHideTop() { return hideTop; }
        public void setHideTop(boolean hideTop) { this.hideTop = hideTop; }
        
        public boolean isHideLeft() { return hideLeft; }
        public void setHideLeft(boolean hideLeft) { this.hideLeft = hideLeft; }
        
        public boolean isHideBottom() { return hideBottom; }
        public void setHideBottom(boolean hideBottom) { this.hideBottom = hideBottom; }
        
        public boolean isHideRight() { return hideRight; }
        public void setHideRight(boolean hideRight) { this.hideRight = hideRight; }        
                
        public Insets getBorderInsets(Component c, Insets insets) {
            if (insets == null) insets = new Insets(0,0,0,0);
            
            insets.top = insets.bottom = 5; 
            insets.left = insets.right = 5; 
            return insets; 
        }
        
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Color oldColor = g.getColor();
            Color color = new Helper().getBorderColor(c); 
            c.setBackground( jlabel.getBackground() ); 
            
            if (!hideTop) {
                g.setColor( color );
                g.drawLine(0, 0, w, 0);
            } 
            
            if (!hideLeft) {
                g.setColor( color );
                g.drawLine(0, 2, 0, h-5);
            } 
            
            if (!hideBottom) {
                g.setColor( color );
                g.drawLine(0, h-2, w, h-2);
            } 
            
            if (!hideRight) {
                g.setColor( color );
                g.drawLine(w-1, 2, w-1, h-5);
            }
            
            g.setColor(oldColor); 
        }
        
        protected Color getHighlightColor(Component c) {
            return c.getBackground().brighter();
        }

        protected Color getShadowColor(Component c) {
            return c.getBackground().darker();
        }  
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" RowBorder (class) "> 
    
    public static class RowBorder extends AbstractBorder 
    {
        private Color defaultColor = java.awt.SystemColor.control; 
        
        public RowBorder() {}
        
        public Insets getBorderInsets(Component c, Insets insets) {
            if (insets == null) insets = new Insets(0,0,0,0);
            
            insets.top = insets.bottom = 2; 
            insets.left = insets.right = 2; 
            return insets; 
        }
        
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Color oldColor = g.getColor();
            Color newColor = ColorUtil.brighter(defaultColor.darker(), 20);
            g.setColor(newColor);
            g.drawLine(w-1, 0, w-1, h); 
            g.setColor(oldColor); 
        }  
    }
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" ViewPortBorder (class) "> 
    
    public static class ViewPortBorder extends AbstractBorder 
    {
        private Color defaultColor = java.awt.SystemColor.control; 
        
        public ViewPortBorder() {}
        
        public Insets getBorderInsets(Component c, Insets insets) {
            if (insets == null) insets = new Insets(0,0,0,0);
            
            insets.top = insets.bottom = 5; 
            insets.left = insets.right = 5; 
            return insets; 
        }
        
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Color oldColor = g.getColor();
            Color newColor = ColorUtil.brighter(defaultColor.darker(), 20);
            g.setColor(newColor);
            g.drawLine(0, 0, 0, h); 
            g.setColor(oldColor); 
        }        
    }
    
    // </editor-fold>        
    
    // <editor-fold defaultstate="collapsed" desc=" CellBorder "> 
    
    public static class CellBorder extends AbstractBorder {

        private JTable table;
        private int rowIndex; 
        private int columnIndex;
        
        public CellBorder( JTable table, int rowIndex, int columnIndex ) {
            this.table = table; 
            this.rowIndex = rowIndex; 
            this.columnIndex = columnIndex; 
        }
        
        public Insets getBorderInsets(Component c) {
            return getBorderInsets(c, null); 
        }

        public Insets getBorderInsets(Component c, Insets insets) {
            if ( insets == null ) { insets = new Insets(0,0,0,0); }
            
            return super.getBorderInsets(c, insets);
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            
        }
    }
    
    // </editor-fold>
    
}
