/*
 * RowHeaderView.java
 *
 * Created on March 9, 2011, 4:36 PM
 * @author jaycverg
 */

package com.rameses.rcp.control.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.LayoutManager;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;


public class RowHeaderView extends JPanel implements TableModelListener
{
    private int rowCount;
    private int currentRow = -1;
    private JTable table;
    
    public RowHeaderView(JTable table) 
    {
        this.table = table;
        setLayout(new RowHeaderLayout());
    }
    
    public void setRowCount( int rowCount ) { 
        if ( this.rowCount == rowCount ) return;
        
        this.rowCount = rowCount;
        
        removeAll();
        JComponent label = null;
        for (int i = 0; i < rowCount; ++i) {
            add(new RowView(table));
        }
    }
    
    public void clearEditing() {
        if ( currentRow != -1 ) {
            RowView rh = getRowView(currentRow);
            if ( rh != null ) rh.edit(false);
        }
        currentRow = -1;
    }
    
    public void editRow(int row) {
        if ( currentRow != row ) {
            clearEditing();
        }
        RowView rh = getRowView( row ); 
        if ( rh != null ) rh.edit(true);
        
        currentRow = row;
    }

    public void tableChanged(TableModelEvent e) {
        setRowCount(table.getRowCount());
        repaint();
    }
    
    // <editor-fold defaultstate="collapsed" desc="  RowView (class)  ">
    
    private RowView getRowView( int index ) {
        try { 
            return (RowView) getComponent(index);
        } catch(Throwable t) {
            return null; 
        } 
    }
    
    private class RowView extends JLabel { 
        
        private Color defaultColor = java.awt.SystemColor.control; 
        private JTable table;
        
        public RowView(JTable table) { 
            this.table = table; 
            setPreferredSize(new Dimension(23,23)); 
            setHorizontalAlignment(SwingConstants.CENTER); 
            setVerticalAlignment(SwingConstants.CENTER); 
            setForeground(Color.BLUE); 
            setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12)); 
        }
        
        public void setText(String text) {;}

        public void edit(boolean b) {
            if (b) { 
                super.setText("*"); 
            } else { 
                super.setText(""); 
            } 
        }          
    }
    
    // </editor-fold>            
    
    // <editor-fold defaultstate="collapsed" desc="  RowHeaderLayout (Class) ">
    
    private class RowHeaderLayout implements LayoutManager 
    {        
        public void addLayoutComponent(String name, Component comp) {;}
        public void removeLayoutComponent(Component comp) {;}
        
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
                
                Component[] comps = parent.getComponents();
                for (int i=0; i<comps.length; i++) {
                    if (!(comps[i] instanceof RowView)) continue;
                    
                    int rh = table.getRowHeight(i);
                    comps[i].setBounds(x, y, w, rh);
                    y += rh;
                }
            }
        }
        
        private Dimension getLayoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                int w = 0;
                int h = 0;
                Component[] comps = parent.getComponents();
                for (int i=0; i<comps.length; i++) {
                    if (!(comps[i] instanceof RowView)) continue;
                    
                    Dimension dim = comps[i].getPreferredSize();
                    w = Math.max(w, dim.width);
                    h += table.getRowHeight(i);
                }
                
                Insets margin = parent.getInsets();
                w += (margin.left + margin.right);
                h += (margin.top + margin.bottom);
                return new Dimension(w,h);
            }
        }
    }
    
    // </editor-fold>
}