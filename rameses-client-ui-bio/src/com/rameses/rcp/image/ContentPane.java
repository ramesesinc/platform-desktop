/*
 * ContentPane.java
 *
 * Created on December 23, 2013, 5:04 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.image;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 *
 * @author wflores
 */
class ContentPane extends JPanel 
{
    private JPanel toolbar;
    private ImageCanvas canvas;
    
    public ContentPane() {
        setLayout(new BorderLayout()); 
        initComponent(); 
    }
    
    private void initComponent() {
        toolbar = new JPanel(new ToolbarLayout());  
        toolbar.setBorder(BorderFactory.createEmptyBorder(5,5,5,5)); 
        add(toolbar, BorderLayout.SOUTH); 
        
        JButton btnOK = new JButton("   OK   ");
        btnOK.setFont(btnOK.getFont().deriveFont(12.0f)); 
        btnOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            }
        });
        toolbar.add(btnOK);
        
        JButton btnCancel = new JButton(" Cancel ");
        btnCancel.setFont(btnCancel.getFont().deriveFont(12.0f)); 
        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            }
        }); 
        toolbar.add(btnCancel); 
        
        canvas = new ImageCanvas();
        add(canvas);
    } 
    
    public void setData(Object data) {
        canvas.setData(data); 
    }
    
    
    // <editor-fold defaultstate="collapsed" desc=" ToolbarLayout "> 
    
    private class ToolbarLayout implements LayoutManager 
    {
        ContentPane root = ContentPane.this;
        
        public void addLayoutComponent(String name, Component comp) {}
        public void removeLayoutComponent(Component comp) {}

        public Dimension preferredLayoutSize(Container parent) {
            return layoutSize(parent);
        }

        public Dimension minimumLayoutSize(Container parent) {
            return layoutSize(parent);
        }

        private Dimension layoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                int w=0, h=0;
                Component[] comps = parent.getComponents();
                for (int i=0; i<comps.length; i++) {
                    Component c = comps[i];
                    if (!c.isVisible()) continue;
                    
                    Dimension dim = c.getPreferredSize(); 
                    w += dim.width + 3;
                    h = Math.max(dim.height, h); 
                }
                
                Insets margin = parent.getInsets();
                w += margin.left + margin.right;
                h += margin.top + margin.bottom;
                return new Dimension(w, h); 
            }
        }
        
        public void layoutContainer(Container parent) { 
            synchronized (parent.getTreeLock()) { 
                Insets margin = parent.getInsets(); 
                int pw = parent.getWidth();
                int ph = parent.getHeight();
                int x = margin.left;
                int y = margin.top;
                int w = pw - (margin.left + margin.right);
                int h = ph - (margin.top + margin.bottom);
                
                Component[] comps = parent.getComponents();
                for (int i=0; i<comps.length; i++) {
                    Component c = comps[i];
                    if (!c.isVisible()) continue;
                    
                    Dimension dim = c.getPreferredSize(); 
                    c.setBounds(x, y, dim.width, h); 
                    x += dim.width + 3; 
                }
            } 
        }
    }
    
    // </editor-fold>    
}
