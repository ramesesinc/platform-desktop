/*
 * TextEditorPopupSelector.java
 *
 * Created on April 4, 2014, 10:44 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control.text;

import com.rameses.rcp.common.PopupItem;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

/**
 *
 * @author wflores 
 */
public class TextEditorPopupSelector extends JPopupMenu 
{
    private DefaultSelectionModel model; 
    private JTextComponent editor;
    private int x;
    private int y;
    private int rowHeight; 
    private int rowSize;
    
    private Color selectionBackground;
    private Color selectionForeground;
    
    private List<SelectionListener> listeners;
            
    public TextEditorPopupSelector(JTextComponent editor) {
        this.listeners = new ArrayList();         
        this.editor = editor;
        
        super.setLayout(new DefaultLayout());
        setLightWeightPopupEnabled(true); 
        setFocusable(false); 
        setRowHeight(20); 
        setRowSize(10);
        setSelectionBackground(new Color(51, 153, 255));
        setSelectionForeground(Color.WHITE);
        getModel();
        
        Color shadow = getBackground().darker(); 
        setBorder(BorderFactory.createLineBorder(shadow)); 
    }
    
    private DefaultSelectionModel getModel() {
        if (model == null) {
            model = new DefaultSelectionModel(this); 
        }
        return model; 
    }
        
    public void remove(SelectionListener listener) {
        if (listener != null) listeners.remove(listener); 
    }
    
    public void add(SelectionListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener); 
        }
    }
    
    public int getRowHeight() { return rowHeight; } 
    public void setRowHeight(int rowHeight) {
        this.rowHeight = rowHeight; 
    }

    public int getRowSize() { return rowSize; } 
    public void setRowSize(int rowSize) {
        this.rowSize = rowSize; 
    }
    
    public Color getSelectionBackground() { return selectionBackground; } 
    public void setSelectionBackground(Color selectionBackground) {
        this.selectionBackground = selectionBackground;
    }
    
    public Color getSelectionForeground() { return selectionForeground; } 
    public void setSelectionForeground(Color selectionForeground) {
        this.selectionForeground = selectionForeground; 
    }
    
    public PopupItem getSelectedItem() {
        ItemLabel c = getModel().getSelectedItem();
        return (c == null? null: c.getPopupItem()); 
    }
    
    private int indexOf(ItemLabel item) {
        if (item == null) return -1;
        
        Component[] comps = getComponents();
        for (int i=0; i<comps.length; i++) {
            ItemLabel c = (ItemLabel) comps[i];
            if (c.equals(item)) return i;
        }
        return -1;
    }
    
    private ItemLabel getItem(int index) {
        try {
            return (ItemLabel) getComponent(index);
        } catch(Throwable t) {
            return null; 
        }
    }
    
    public void moveDown() {
        ItemLabel selItem = getModel().getSelectedItem();
        int index = indexOf(selItem);         
        ItemLabel c = getItem(index+1);
        if (c == null) return;
        
        getModel().setSelectedItem(c);
        getModel().refresh(); 
    }
    
    public void moveUp() {
        ItemLabel selItem = getModel().getSelectedItem();
        int index = indexOf(selItem)-1; 
        if (index < 0) index = 0;
        
        ItemLabel c = getItem(index);
        if (c == null) return;
        
        getModel().setSelectedItem(c);
        getModel().refresh(); 
    } 
    
    public synchronized void setData(List<PopupItem> list) {
        removeAll();
        
        if (list != null) { 
            for (int i=0; i<getRowSize(); i++) {
                try {
                    PopupItem si = list.get(i);
                    add(new ItemLabel(si));
                } catch(Throwable t) {
                    break; 
                }
            } 
        }
        getModel().setSelectedItem(null);         
    }

    protected void addImpl(Component comp, Object constraints, int index) {
        if (!(comp instanceof ItemLabel)) return;
        
        super.addImpl(comp, constraints, index); 
    }
    
    
    // <editor-fold defaultstate="collapsed" desc=" DefaultLayout ">
    
    private class DefaultLayout implements LayoutManager 
    {
        TextEditorPopupSelector root = TextEditorPopupSelector.this;
        
        private int SPACING = 1;
        private int MAX_WIDTH = 200;
        
        public void addLayoutComponent(String name, Component comp) {}
        public void removeLayoutComponent(Component comp) {}

        public Dimension preferredLayoutSize(Container parent) {
            return getLayoutSize(parent);
        }

        public Dimension minimumLayoutSize(Container parent) {
            return getLayoutSize(parent);
        }

        private Dimension getLayoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                int w=0, h=0;               
                boolean has_visibled_items = false;
                Component[] comps = parent.getComponents();
                for (int i=0; i<comps.length; i++) {
                    Component c = comps[i];
                    if (!c.isVisible()) continue; 
                    if (has_visibled_items) h += SPACING;
                    
                    Dimension dim = c.getPreferredSize();
                    int cw = dim.width;
                    if (cw > MAX_WIDTH) cw = MAX_WIDTH;
                    
                    w = Math.max(cw, w); 
                    h += root.getRowHeight();
                    has_visibled_items = true;
                }
                                
                Insets margin = parent.getInsets();
                w += (margin.left + margin.right);
                h += (margin.top + margin.bottom);
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
                int rowHeight = root.getRowHeight();
                
                boolean has_visibled_items = false;
                Component[] comps = parent.getComponents();
                for (int i=0; i<comps.length; i++) {
                    Component c = comps[i];
                    if (!c.isVisible()) continue; 
                    if (has_visibled_items) y += 1;
                    
                    Dimension dim = c.getPreferredSize();
                    c.setBounds(x, y, w, rowHeight); 
                    y += rowHeight;
                    has_visibled_items = true; 
                }
            }
        }
    }
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" ItemLabel ">
    
    private class ItemLabel extends JLabel implements MouseListener
    {
        TextEditorPopupSelector root = TextEditorPopupSelector.this;
        
        private PopupItem item;
        
        ItemLabel(PopupItem item) {
            super();
            this.item = item;
            
            setOpaque(true);            
            setBackground(Color.WHITE); 
            setBorder(BorderFactory.createEmptyBorder(2,5,2,5)); 
            setVerticalAlignment(SwingConstants.TOP); 
            addMouseListener(this); 
            
            String caption = (item == null? null: item.getCaption());
            setText(caption == null? "": caption.toString());  
        }

        public PopupItem getPopupItem() {
            return item; 
        }
        
        public void mousePressed(MouseEvent e) {}
        public void mouseReleased(MouseEvent e) {}
        public void mouseExited(MouseEvent e) {}
        
        public void mouseEntered(MouseEvent e) {
            if (root.model == null) return;
            
            root.model.setSelectedItem(this); 
            root.model.refresh(); 
        } 
        
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) return;
            if (!SwingUtilities.isLeftMouseButton(e)) return; 
            
            for (SelectionListener sl : root.listeners) {
                sl.onselect(item);
            }
        }        
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" DefaultSelectionModel ">
    
    private class DefaultSelectionModel 
    {
        TextEditorPopupSelector root = TextEditorPopupSelector.this;
        
        private JComponent container;
        private ItemLabel selectedItem;
        
        DefaultSelectionModel(JComponent container) {
            this.container = container; 
        }

        public ItemLabel getSelectedItem() { return selectedItem; } 
        public void setSelectedItem(ItemLabel selectedItem) {
            this.selectedItem = selectedItem; 
        }
        
        public void refresh() {
            Component[] comps = container.getComponents(); 
            for (int i=0; i<comps.length; i++) {
                Component c = comps[i];
                if (!(c instanceof ItemLabel)) continue; 
                
                TextEditorPopupSelector.ItemLabel item = (ItemLabel)c; 
                item.setBackground(Color.WHITE);
                item.setForeground(root.getForeground()); 
                item.repaint();
            }
            
            TextEditorPopupSelector.ItemLabel item = getSelectedItem();
            if (item != null) {
                item.setBackground(root.getSelectionBackground()); 
                item.setForeground(root.getSelectionForeground()); 
                item.repaint();
            } 
        }
    }
        
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" SelectionListener ">
    
    public static interface SelectionListener 
    {
        void onselect(PopupItem item); 
    }
    
    // </editor-fold>
}


