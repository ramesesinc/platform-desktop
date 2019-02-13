/*
 * RadioListPanel.java
 *
 * Created on July 30, 2014, 10:29 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.Beans;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;

/**
 *
 * @author wflores 
 */
public class RadioListPanel extends JPanel 
{
    private int itemGap;
    private int itemCount;
    private int orientation;
    private Insets padding;
    private String selectionMode; 
    
    private ButtonGroup buttonGroup; 
    private RadioComponent selectedButton; 
    
    public RadioListPanel() {
        super();
        initComponent();
    }
    
    // <editor-fold defaultstate="collapsed" desc=" init component ">
    
    private void initComponent() {
        itemGap = 5; 
        itemCount = 2; 
        orientation = SwingConstants.HORIZONTAL; 
        selectionMode = SelectionMode.SINGLE; 
        setPreferredSize(new Dimension(100, 50)); 
        super.setLayout(new ContainerLayout()); 
        setOpaque(false); 
        buttonGroup = new ButtonGroup(); 

        if (Beans.isDesignTime()) {
            addItem("Option 1", "Option 1"); 
            addItem("Option 2", "Option 2"); 
        }
    }

    public void setLayout(LayoutManager mgr) {;}
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Getters/Setters ">
    
    public String getSelectionMode() { return selectionMode; } 
    public void setSelectionMode(String selectionMode) {
        this.selectionMode = selectionMode; 
    }
    
    public int getItemGap() { return itemGap; } 
    public void setItemGap(int itemGap) {
        this.itemGap = itemGap; 
    }
    
    public int getItemCount() { return itemCount; } 
    public void setItemCount(int itemCount) {
        this.itemCount = itemCount; 
        
        if (Beans.isDesignTime()) {
            removeAll();
            for (int i=0; i<itemCount; i++) {
                addItem("Option "+(i+1), "Option "+(i+1)); 
            }
        }
    }
    
    public int getOrientation() { return orientation; } 
    public void setOrientation(int orientation) {
        this.orientation = orientation; 
    } 
    
    public Insets getPadding() { return padding; } 
    public void setPadding(Insets padding) {
        this.padding = padding; 
    }
    
    public List getUserObjects() {
        List list = new ArrayList(); 
        Component[] comps = getComponents();
        for (int i=0; i<comps.length; i++) {
            if (comps[i] instanceof RadioComponent) {
                RadioComponent rc = (RadioComponent)comps[i]; 
                list.add(rc.getUserObject()); 
            }
        }
        return list; 
    }
    
    public void setSelectedIndex(int index) {
        clearSelection(); 
        
        RadioComponent rc = null; 
        try { 
            rc = (RadioComponent) getComponent(index); 
        } catch(Throwable t){;} 
        
        if (rc != null) rc.setSelected(true); 
        
        revalidate();
        repaint(); 
    }
    
    private Insets getPreferredPadding() {
        Insets pads = getPadding(); 
        return (pads == null? new Insets(0,0,0,0): pads); 
    }
    private int getPreferredCellPadding() {
        return Math.max(getItemGap(), 0); 
    }    
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" override and helper methods ">
    
    public void addItem(String caption, Object userObject) {
        addItem(new ItemImpl(caption, userObject)); 
    } 
    
    public void addItem(Item item) {
        if (item == null) return;
        
        add(new RadioComponent(item));  
    }
    
    protected void addImpl(Component comp, Object constraints, int index) {
        if (comp instanceof RadioComponent) {
            super.addImpl(comp, constraints, index); 
        }
    }
    
    public void clearSelection() {
        buttonGroup.clearSelection(); 
        selectedButton = null; 
    }
    
    public Item getSelectedItem() {
        return (selectedButton == null? null: selectedButton.getItem()); 
    }
    
    protected void onselect(Object obj) {
        //to be implemented 
    }

    public void repaint(long tm, int x, int y, int width, int height) {
        enableComponents(isEnabled()); 
        super.repaint(tm, x, y, width, height); 
    }
    
    public void enableComponents(boolean enabled) {
        Component[] comps = getComponents();
        for (int i=0; i<comps.length; i++) {
            if (comps[i] instanceof RadioComponent) {
                comps[i].setEnabled(enabled); 
                comps[i].repaint(); 
            } 
        } 
    } 
    
    public boolean requestFocusInWindow() {
        if (!isEnabled()) return false; 
        
        Component[] comps = getComponents();
        for (int i=0; i<comps.length; i++) {
            if (!(comps[i] instanceof RadioComponent)) continue; 
            
            RadioComponent btn = (RadioComponent)comps[i]; 
            if (btn.isEnabled() && btn.isFocusable()) {
                return btn.requestFocusInWindow(); 
            }
        } 
        return false; 
    }

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Item ">
    
    public static interface Item 
    {
        Object getUserObject();
        String getCaption(); 
    }
    
    class ItemImpl implements Item 
    {
        private String caption;
        private Object userObject;
        
        ItemImpl(String caption, Object userObject) {
            this.caption = caption;
            this.userObject = userObject; 
        }

        public Object getUserObject() { return userObject; }
        public String getCaption() { return caption; }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" RadioComponent ">
    
    class RadioComponent extends JRadioButton implements ActionListener
    {
        RadioListPanel root = RadioListPanel.this; 
        
        private Item item;
        
        public RadioComponent(Item item) {
            super(); 
            this.item = item; 
            setMargin(new Insets(0,0,0,0)); 
            setOpaque(false); 
            addActionListener(this); 
        }
        
        public Item getItem() { return item; } 
        
        public Object getUserObject() { 
            return (item == null? null: item.getUserObject());  
        } 

        public String getText() {
            return (item == null? "null": item.getCaption()); 
        }

        public void addNotify() {
            super.addNotify(); 
            root.buttonGroup.add(this); 
        }

        public void removeNotify() {
            super.removeNotify();
            root.buttonGroup.remove(this); 
        }

        public void actionPerformed(ActionEvent e) {
            if (Beans.isDesignTime()) return;
            
            if (root.selectedButton != null && root.selectedButton.equals(this)) {
                boolean toggle = SelectionMode.TOGGLE.equals(root.getSelectionMode()); 
                if (toggle) {
                    root.buttonGroup.clearSelection(); 
                    repaint(); 
                    
                    root.selectedButton = null; 
                    root.onselect(null); 
                }
            } else {
                root.selectedButton = this; 
                root.onselect(getUserObject()); 
            } 
        }
    }
    
    private class ToggleSelectionProcess implements Runnable
    {
        private RadioComponent comp;
        
        ToggleSelectionProcess(RadioComponent comp) {
            this.comp = comp; 
        }
                
        public void run() {
        }
    }
    
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ContainerLayout ">
    
    private class ContainerLayout implements LayoutManager 
    {
        RadioListPanel root = RadioListPanel.this;
        
        public void addLayoutComponent(String name, Component comp) {}
        public void removeLayoutComponent(Component comp) {}
        
        private List<RadioComponent> getRadioItems(Container parent) {
            List<RadioComponent> items = new ArrayList();
            Component[] comps = parent.getComponents();
            for (int i=0; i<comps.length; i++) {
                if (comps[i] instanceof RadioComponent) {
                    RadioComponent c = (RadioComponent) comps[i];
                    if (c.isVisible()) items.add(c); 
                } 
            }
            return items;
        }
        
        private Dimension computePageSize(List<RadioComponent> items, int pageindex) {
            Dimension dim = new Dimension(0, 0);
            int itemcount = items.size();
            if (itemcount == 0) return dim;
            
            int cellpad   = root.getPreferredCellPadding(); 
            int cellcount = root.getItemCount();
            if (cellcount <= 0) cellcount = itemcount; 
            
            int pagecount = (int) (itemcount / cellcount); 
            if (itemcount % cellcount > 0) pagecount += 1; 

            boolean has_visible_components = false;
            if (root.getOrientation() == SwingConstants.VERTICAL) {
                for (int cellindex=0; cellindex<cellcount; cellindex++) {
                    int index = (pageindex*cellcount)+cellindex; 
                    if (index >= itemcount) break; 
                    
                    RadioComponent ri = items.get(index); 
                    int cwidth = ri.getPreferredSize().width;
                    int cheight = ri.getPreferredSize().height;
                    int ii = cellindex + cellcount; 
                    while (ii < itemcount) {
                        RadioComponent o = items.get(ii); 
                        Dimension odim = o.getPreferredSize();
                        cheight = Math.max(cheight, odim.height); 
                        ii += cellcount; 
                    }
                    
                    dim.width = Math.max(dim.width, cwidth);
                    dim.height += cheight; 
                    if (has_visible_components) {
                        dim.height += cellpad; 
                    } 
                    has_visible_components = true; 
                }
            } else {
                for (int cellindex=0; cellindex<cellcount; cellindex++) {
                    int index = (pageindex*cellcount)+cellindex; 
                    if (index >= itemcount) break; 
                    
                    RadioComponent ri = items.get(index); 
                    int cwidth = ri.getPreferredSize().width;
                    int cheight = ri.getPreferredSize().height;
                    int ii = cellindex + cellcount; 
                    while (ii < itemcount) {
                        RadioComponent o = items.get(ii); 
                        Dimension odim = o.getPreferredSize();
                        cwidth = Math.max(cwidth, odim.width); 
                        ii += cellcount; 
                    }
                    
                    dim.width += cwidth;
                    dim.height = Math.max(dim.height, cheight); 
                    if (has_visible_components) {
                        dim.width += cellpad; 
                    } 
                    has_visible_components = true; 
                }
            }
            return dim; 
        }
        
        public Dimension getLayoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                Dimension newdim = new Dimension(0, 0);                
                List<RadioComponent> items = getRadioItems(parent); 
                int itemcount = items.size(); 
                if (itemcount > 0) {
                    int cellpad = root.getPreferredCellPadding();                
                    int cellcount = root.getItemCount();
                    if (cellcount <= 0) cellcount = itemcount; 

                    int pagecount = (int) (itemcount / cellcount); 
                    if (itemcount % cellcount > 0) pagecount += 1; 

                    for (int pageindex=0; pageindex<pagecount; pageindex++) {
                        Dimension pagedim = computePageSize(items, pageindex); 
                        newdim.width += pagedim.width;
                        newdim.height += pagedim.height; 
                    }
                }

                Insets margin = parent.getInsets();
                Insets pads = root.getPreferredPadding(); 
                newdim.width += (margin.left + margin.right + pads.left + pads.right);
                newdim.height += (margin.top + margin.bottom + pads.top + pads.bottom);
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
                Insets pads = root.getPreferredPadding();
                Insets margin = parent.getInsets();
                int pw = parent.getWidth();
                int ph = parent.getHeight(); 
                int sx = margin.left + pads.left;
                int sy = margin.top + pads.top;
                int rb = pw - (margin.right + pads.right);
                int w = pw - (margin.left + margin.right + pads.left + pads.right);
                int h = ph - (margin.top + margin.bottom + pads.top + pads.bottom);
                int x = sx, y = sy;
                
                List<RadioComponent> items = getRadioItems(parent); 
                int itemcount = items.size(); 
                if (itemcount == 0) return;
                
                int cellpad = root.getPreferredCellPadding();                
                int cellcount = root.getItemCount();
                if (cellcount <= 0) cellcount = itemcount;     
                
                int pagecount = (int) (itemcount / cellcount); 
                if (itemcount % cellcount > 0) pagecount += 1; 
                
                for (int pageindex=0; pageindex<pagecount; pageindex++) {
                    if (root.getOrientation() == SwingConstants.VERTICAL) {
                        y = sy; 
                        if (pageindex > 0) x += cellpad;
                        
                        int maxwidth = 0;
                        for (int cellindex=0; cellindex<cellcount; cellindex++) {
                            int index = (pageindex*cellcount)+cellindex; 
                            if (index >= itemcount) break; 
                            
                            RadioComponent ri = items.get(index); 
                            Dimension rdim = ri.getPreferredSize(); 
                            maxwidth = Math.max(maxwidth, rdim.width); 
                        }
                        
                        for (int cellindex=0; cellindex<cellcount; cellindex++) {
                            int index = (pageindex*cellcount)+cellindex; 
                            if (index >= itemcount) break; 

                            RadioComponent ri = items.get(index); 
                            Dimension rdim = ri.getPreferredSize(); 
                            int maxheight = rdim.height;
                            for (int pgidx=0; pgidx<pagecount; pgidx++) {
                                int idx = (pgidx*cellcount)+cellindex; 
                                if(idx >= itemcount) break; 
                                
                                RadioComponent o = items.get(idx); 
                                Dimension odim = o.getPreferredSize();
                                maxheight = Math.max(maxheight, odim.height); 
                            }
                            
                            ri.setBounds(x, y, maxwidth, rdim.height); 
                            y += (maxheight + cellpad); 
                        }
                        x += maxwidth; 
                        
                    } else {
                        x = sx; 
                        if (pageindex > 0) y += cellpad;
                        
                        int maxheight=0;
                        for (int cellindex=0; cellindex<cellcount; cellindex++) {
                            int index = (pageindex*cellcount)+cellindex; 
                            if (index >= itemcount) break; 
                            
                            RadioComponent ri = items.get(index); 
                            Dimension rdim = ri.getPreferredSize(); 
                            maxheight = Math.max(maxheight, rdim.height); 
                        } 
                                                
                        for (int cellindex=0; cellindex<cellcount; cellindex++) {
                            int index = (pageindex*cellcount)+cellindex; 
                            if (index >= itemcount) break; 

                            RadioComponent ri = items.get(index); 
                            Dimension rdim = ri.getPreferredSize(); 
                            int maxwidth = rdim.width;
                            for (int pgidx=0; pgidx<pagecount; pgidx++) {
                                int idx = (pgidx*cellcount)+cellindex; 
                                if(idx >= itemcount) break; 
                                
                                RadioComponent o = items.get(idx); 
                                Dimension odim = o.getPreferredSize();
                                maxwidth = Math.max(maxwidth, odim.width); 
                            }
                            
                            ri.setBounds(x, y, maxwidth, rdim.height); 
                            x += (maxwidth + cellpad); 
                        } 
                        y += maxheight; 
                    }
                }
            }
        }
    }
    
    //</editor-fold>        
    
}
