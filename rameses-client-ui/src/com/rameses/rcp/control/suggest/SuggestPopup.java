/*
 * SuggestPopup.java
 *
 * Created on December 18, 2013, 12:34 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control.suggest;

import com.rameses.rcp.support.ImageIconSupport;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
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
import javax.swing.SwingUtilities;

/**
 *
 * @author wflores 
 */
public class SuggestPopup extends JPopupMenu 
{
    private DefaultSelectionModel model; 
    private JComponent invoker;    
    private int rowHeight; 
    private int rowSize;
    
    private Color selectionBackground;
    private Color selectionForeground;
    
    private List<SelectionListener> listeners;
            
    public SuggestPopup(JComponent invoker) {
        this.invoker = invoker;
        this.listeners = new ArrayList(); 
        
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
    
    public SuggestItem getSuggestedItem() {
        ItemLabel c = getModel().getSelectedItem();
        return (c == null? null: c.getSuggestItem()); 
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
    
    public synchronized void setData(List<SuggestItem> list) {
        removeAll();
        
        if (list != null) { 
            for (int i=0; i<getRowSize(); i++) {
                try {
                    SuggestItem si = list.get(i);
                    add(new ItemLabel(si));
                } catch(Throwable t) {
                    break; 
                }
            } 
        }
        getModel().setSelectedItem(null);         
    }

    protected void addImpl(Component comp, Object constraints, int index) {
        if ((comp instanceof ItemLabel) || (comp instanceof SearchComponent)) {
            super.addImpl(comp, constraints, index); 
        }
    }
    
    public void loadSearchComponent() {
        getModel().setSelectedItem(null); 
        removeAll();
        add( new SearchComponent()); 
    } 
    public boolean isSearchComponentLoaded() {
        try {
            return (getComponent(0) instanceof SearchComponent); 
        } catch(Throwable t) {
            return false; 
        }
    }
    
    
    // <editor-fold defaultstate="collapsed" desc=" DefaultLayout ">
    
    private class DefaultLayout implements LayoutManager 
    {
        SuggestPopup root = SuggestPopup.this;
        
        private int SPACING = 1;
        
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
                    w = Math.max(dim.width, w); 
                    h += root.getRowHeight();
                    has_visibled_items = true;
                }
                
                if (root.invoker != null) {
                    Insets m = root.invoker.getInsets(); 
                    w = Math.max(w, (root.invoker.getWidth()-m.left)+1); 
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
        SuggestPopup root = SuggestPopup.this;
        
        private SuggestItem item;
        
        ItemLabel(SuggestItem item) {
            super();
            this.item = item;
            
            setOpaque(true);            
            setBackground(Color.WHITE); 
            setBorder(BorderFactory.createEmptyBorder(2,5,2,5)); 
            addMouseListener(this); 
            
            String caption = (item == null? null: item.getCaption());
            setText(caption == null? "": caption.toString());  
            
            String sicon = (item == null? null: item.getIcon()); 
            if ( sicon != null ) { 
                try { 
                    setIcon( ImageIconSupport.getInstance().getIcon( sicon ) ); 
                } catch(Throwable t) {;}
            }
        }

        public SuggestItem getSuggestItem() {
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
    
    private class SearchComponent extends JLabel {
        
        private String strIcon = "com/rameses/rcp/icons/loading16.gif"; 
        private String strCaption = " searching...";
        
        public SearchComponent() { 
            super();
            
            setOpaque(true);  
            setBackground(new Color(250,250,250)); 
            setBorder(BorderFactory.createEmptyBorder(5,5,5,5)); 
            setText( strCaption );
            try { 
                setIcon( ImageIconSupport.getInstance().getIcon( strIcon ) ); 
            } catch(Throwable t) {;}
            
            setFont(Font.decode("Dialog-bold-11")); 
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" DefaultSelectionModel ">
    
    private class DefaultSelectionModel 
    {
        SuggestPopup root = SuggestPopup.this;
        
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
                
                SuggestPopup.ItemLabel item = (ItemLabel)c; 
                item.setBackground(Color.WHITE);
                item.setForeground(root.getForeground()); 
                item.repaint();
            }
            
            SuggestPopup.ItemLabel item = getSelectedItem();
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
        void onselect(SuggestItem item); 
    }
    
    // </editor-fold>
}


