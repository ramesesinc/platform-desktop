/*
 * TileLabel.java
 *
 * Created on August 8, 2014, 9:20 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control.panel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

/**
 *
 * @author wflores 
 */
public class TileLabel extends JLabel 
{
    private JLabel iconLabel;
    private JLabel textLabel;
    private Dimension iconSize;
    private ImageIcon icon; 
    private String text; 
    
    public TileLabel() {
        setLayout(new ContainerLayout()); 
        iconSize = new Dimension(48, 48);        
    }
    
    private JLabel getIconLabel() {
        if (iconLabel == null) {
            iconLabel = new JLabel(); 
            iconLabel.setHorizontalAlignment(SwingConstants.CENTER); 
            iconLabel.setVerticalAlignment(SwingConstants.CENTER); 
            iconLabel.setBorder(BorderFactory.createLineBorder(Color.BLUE)); 
            iconLabel.setBorder(BorderFactory.createEmptyBorder(0,0,0,0)); 
            iconLabel.setOpaque(false);
        }
        return iconLabel; 
    }
    
    private JLabel getTextLabel() {
        if (textLabel == null) {
            textLabel = new JLabel(); 
            textLabel.setHorizontalAlignment(SwingConstants.CENTER); 
            textLabel.setVerticalAlignment(SwingConstants.TOP); 
            textLabel.setBorder(BorderFactory.createEmptyBorder(0,0,0,0)); 
            textLabel.setOpaque(false);
        }
        return textLabel; 
    }    
    
    public Dimension getIconSize() { return iconSize; }
    public void setIconSize(Dimension iconSize) {
        this.iconSize = iconSize; 
    }
    
    public String getText() { return null; } 
    public void setText(String text) {
        this.text = text; 
        if (text == null) {
            getTextLabel().setText(text); 
        } else {
            getTextLabel().setText("<html><center>"+ text +"</center></html>"); 
        }
    }
    
    public void setIcon(Icon icon) {}
    
    public ImageIcon getImageIcon() { return icon; } 
    public void setImageIcon(ImageIcon icon) {
        this.icon = icon; 
        getIconLabel().setIcon(icon); 
    }
    
    // <editor-fold defaultstate="collapsed" desc=" ContainerLayout (Class) ">
    
    private class ContainerLayout implements LayoutManager {
        public void addLayoutComponent(String name, Component comp) {}
        public void removeLayoutComponent(Component comp) {}
        
        public Dimension getLayoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                Dimension newdim = new Dimension(0, 0);
                Dimension icondim = getIconSize();
                if (icondim != null) {
                    newdim.width = icondim.width;
                    newdim.height = icondim.height;
                }
                
                Component comp = getTextLabel();
                if (comp != null) {
                    Dimension dim = comp.getPreferredSize();
                    newdim.width = Math.max(newdim.width, dim.width);
                    newdim.height += dim.height; 
                }
                
                Insets margin = parent.getInsets();                
                newdim.width += (margin.left + margin.right);
                newdim.height += (margin.top + margin.bottom);
                return newdim; 
            }
        }
        
        public Dimension preferredLayoutSize(Container parent) {
            return getLayoutSize(parent);
        }
        
        public Dimension minimumLayoutSize(Container parent) {
            return getLayoutSize(parent);
        }
        
        public void layoutContainer(Container parent) {
            synchronized (parent.getTreeLock()) {
                Insets margin = parent.getInsets();
                int x = margin.left;
                int y = margin.top;
                int w = parent.getWidth() - (margin.left + margin.right);
                int h = parent.getHeight() - (margin.top + margin.bottom);
                
                Dimension icondim = getIconSize();
                if (icondim != null) { 
                    Component iconLabel = getIconLabel();
                    if (iconLabel.getParent() == null) {
                        parent.add(iconLabel); 
                    }
                    iconLabel.setBounds(x, y, w, icondim.height); 
                    y += icondim.height; 
                    h -= icondim.height; 
                } 
                
                Component comp = getTextLabel(); 
                if (comp.getParent() == null) {
                    parent.add(comp); 
                }
                comp.setBounds(x, y, w, h); 
            }
        }
    }
    
    //</editor-fold>            
}
