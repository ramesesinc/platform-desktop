/*
 * CheckListPanel.java
 *
 * Created on August 9, 2014, 4:57 PM
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
import java.awt.LayoutManager2;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.Beans;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 *
 * @author wflores 
 */
public class CheckListPanel extends JPanel 
{
    private int itemGap;
    private int itemCount;
    private int orientation;
    private Insets padding;
    private String selectionMode; 
    
    private ButtonGroup buttonGroup; 
    private CheckButton selectedButton; 
    
    public CheckListPanel() {
        initComponent();
    }
    
    // <editor-fold defaultstate="collapsed" desc=" init component ">
    
    private void initComponent() {
        itemGap = 5; 
        itemCount = 2; 
        orientation = SwingConstants.HORIZONTAL; 
        selectionMode = SelectionMode.SINGLE; 
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
            if (comps[i] instanceof CheckButton) {
                CheckButton rc = (CheckButton)comps[i]; 
                list.add(rc.getUserObject()); 
            }
        }
        return list; 
    }
    
    public void setSelectedIndex(int index) {
        setSelectedIndex(index, true, true); 
    }
    
    public void setSelectedIndex(int index, boolean allowClearSelection, boolean allowRepaint) {
        if (allowClearSelection) clearSelection(); 
        
        CheckButton rc = null; 
        try { 
            rc = (CheckButton) getComponent(index); 
        } catch(Throwable t){;} 
        
        if (rc != null) rc.setSelected(true); 
        
        if (allowRepaint) {
            revalidate(); 
            repaint(); 
        } 
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
        
        add(new CheckButton(item));  
    }
    
    protected void addImpl(Component comp, Object constraints, int index) {
        if (comp instanceof CheckButton) {
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
            if (comps[i] instanceof CheckButton) {
                comps[i].setEnabled(enabled); 
                comps[i].repaint(); 
            } 
        } 
    } 

    public boolean requestFocusInWindow() {
        if (!isEnabled()) return false; 
        
        Component[] comps = getComponents();
        for (int i=0; i<comps.length; i++) {
            if (!(comps[i] instanceof CheckButton)) continue; 
            
            CheckButton btn = (CheckButton)comps[i]; 
            if (btn.isEnabled() && btn.isFocusable()) {
                return btn.requestFocusInWindow(); 
            }
        } 
        return false; 
    }
    
    public final boolean isSingleSelection() {
        return SelectionMode.SINGLE.equals(getSelectionMode()); 
    } 
    
    public final Object getSelectedValue() {
        List results = new ArrayList(); 
        Component[] comps = getComponents();
        for (int i=0; i<comps.length; i++) {
            if (!(comps[i] instanceof CheckButton)) continue; 
            
            CheckButton btn = (CheckButton)comps[i]; 
            if (btn.isSelected()) results.add(btn.getUserObject()); 
        } 
        
        if (results.isEmpty()) return null; 
        
        try {
            if (isSingleSelection()) {
                return results.get(0); 
            } else { 
                return results.toArray(); 
            } 
        } finally {
            results.clear(); 
        }
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
    
    // <editor-fold defaultstate="collapsed" desc=" CheckButton ">
    
    class CheckButton extends JCheckBox implements ActionListener
    {
        CheckListPanel root = CheckListPanel.this; 
        
        private Item item;
        
        public CheckButton(Item item) {
            super(); 
            this.item = item; 
            setOpaque(false);             
            setMargin(new Insets(0,0,0,0)); 
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
            if (isSingleSelection()) {
                root.buttonGroup.add(this);  
            } 
        }

        public void removeNotify() {
            super.removeNotify();
            if (isSingleSelection()) {
                root.buttonGroup.remove(this); 
            }
        }

        public void actionPerformed(ActionEvent e) {
            if (Beans.isDesignTime()) return;
            
            Object value = root.getSelectedValue();
            if (root.selectedButton != null && root.selectedButton.equals(this)) {
                root.selectedButton = null; 
                root.onselect(value); 
            } else { 
                root.selectedButton = this; 
                root.onselect(value); 
            } 
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ContainerLayout ">
    
    private class ContainerLayout implements LayoutManager, LayoutManager2 {
        CheckListPanel root = CheckListPanel.this;
        
        public void addLayoutComponent(String name, Component comp) {}
        public void removeLayoutComponent(Component comp) {}
        
        private List<CheckButton> getItems(Container parent) {
            List<CheckButton> items = new ArrayList();
            Component[] comps = parent.getComponents();
            for (int i=0; i<comps.length; i++) {
                if (comps[i] instanceof CheckButton) {
                    CheckButton c = (CheckButton) comps[i];
                    if (c.isVisible()) items.add(c); 
                } 
            }
            return items;
        }
        
        private Dimension getLayoutSizeV( Container parent, List<CheckButton> items ) { 
            int itemcount = items.size(); 
            if (itemcount > 0) {
                int rows=0, cols=0;
                int cellpad = root.getPreferredCellPadding();                
                int itemVisibleCount = root.getItemCount();
                if ( itemVisibleCount <= 0 ) {
                    rows = itemcount; 
                    cols = 1; 
                } else {
                    rows = itemVisibleCount; 
                    cols = (int) (itemcount / itemVisibleCount); 
                    if (itemcount % itemVisibleCount > 0) cols += 1; 
                } 
                
                int itemindex = 0; 
                CellInfo[][] cells = new CellInfo[rows][cols];
                for (int c=0; c<cols; c++ ) {
                    for (int r=0; r<rows; r++ ) { 
                        if ( itemindex >= items.size()) break; 

                        Dimension cdim = items.get( itemindex ).getPreferredSize(); 
                        CellInfo ci = new CellInfo();
                        ci.index = itemindex; 
                        ci.width = cdim.width;
                        ci.height = cdim.height; 
                        cells[r][c] = ci; 
                        itemindex += 1;
                    }
                } 
                
                Dimension newdim = new Dimension(0, 0);
                for ( int c=0; c<cols; c++ ) {
                    Dimension coldim = computeColSize(cells, c); 
                    newdim.width += coldim.width;
                    newdim.height = Math.max(newdim.height, coldim.height); 
                }
                if ( cols > 1 ) newdim.width += ((cols-1)*cellpad);
                if ( rows > 1 ) newdim.height += ((rows-1)*cellpad);

                return newdim;  
            } else {
                return new Dimension(0, 0); 
            }
        }
        
        private Dimension getLayoutSizeH( Container parent, List<CheckButton> items ) { 
            int itemcount = items.size(); 
            if (itemcount > 0) {
                int cellpad = root.getPreferredCellPadding();                
                int cellcount = root.getItemCount();
                if ( cellcount <= 0 ) cellcount = itemcount;

                int rows = (int) (itemcount / cellcount); 
                if (itemcount % cellcount > 0) rows += 1; 

                int itemindex = 0; 
                CellInfo[][] cells = new CellInfo[rows][cellcount];
                for (int r=0; r<cells.length; r++ ) {
                    for (int c=0; c<cells[r].length; c++ ) { 
                        if ( itemindex >= items.size()) break; 

                        Dimension cdim = items.get( itemindex ).getPreferredSize(); 
                        CellInfo ci = new CellInfo();
                        ci.index = itemindex; 
                        ci.width = cdim.width;
                        ci.height = cdim.height; 
                        cells[r][c] = ci; 
                        itemindex += 1;
                    }
                } 

                Dimension newdim = new Dimension(0, 0);
                for ( int c=0; c<cellcount; c++ ) {
                    Dimension coldim = computeColSize(cells, c); 
                    newdim.width += coldim.width;
                    newdim.height = Math.max(newdim.height, coldim.height); 
                }
                if ( cellcount > 1 ) newdim.width += ((cellcount-1)*cellpad);
                if ( rows > 1 ) newdim.height += ((rows-1)*cellpad);

                return newdim; 
            } else {
                return new Dimension(0, 0); 
            }
        }
        
        public Dimension getLayoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                Dimension newdim = new Dimension(0, 0);                
                List<CheckButton> items = getItems(parent); 
                if (root.getOrientation() == SwingConstants.VERTICAL) {
                    newdim = getLayoutSizeV( parent, items ); 
                } else {
                    newdim = getLayoutSizeH( parent, items ); 
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
                List<CheckButton> items = getItems(parent); 
                int itemcount = items.size(); 
                if (itemcount == 0) return;
                
                if (root.getOrientation() == SwingConstants.VERTICAL) {
                    layoutContainerV( parent, items ); 
                } else {
                    layoutContainerH( parent, items ); 
                } 
            }
        }
        
        private void layoutContainerH( Container parent, List<CheckButton> items ) { 
            Insets pads = root.getPreferredPadding();
            Insets margin = parent.getInsets();
            int pw = parent.getWidth();
            int ph = parent.getHeight(); 
            int px = margin.left + pads.left;
            int py = margin.top + pads.top;
            
            int itemcount = items.size(); 
            if (itemcount > 0) {
                int cellpad = root.getPreferredCellPadding();                
                int cellcount = root.getItemCount();
                if ( cellcount <= 0 ) cellcount = itemcount; 
                
                int rows = (int) (itemcount / cellcount); 
                if (itemcount % cellcount > 0) rows += 1; 
                
                int itemindex = 0; 
                CellInfo[][] cells = new CellInfo[rows][cellcount];
                for (int r=0; r<cells.length; r++ ) {
                    for (int c=0; c<cellcount; c++ ) { 
                        if ( itemindex >= items.size()) break; 

                        Dimension cdim = items.get( itemindex ).getPreferredSize(); 
                        CellInfo ci = new CellInfo();
                        ci.index = itemindex; 
                        ci.width = cdim.width;
                        ci.height = cdim.height; 
                        cells[r][c] = ci; 
                        itemindex += 1;
                    }
                }      
                    
                int x=px, y=py;
                for (int r=0; r<cells.length; r++ ) {
                    if ( r > 0 ) y += cellpad; 

                    Dimension rowdim = computeRowSize(cells[r]);
                    for (int c=0; c<cellcount; c++) { 
                        CellInfo ci = cells[r][c];
                        if ( ci == null ) break; 
                        if ( c > 0 ) x += cellpad; 

                        Dimension coldim = computeColSize(cells, c); 
                        Component comp = items.get( ci.index ); 
                        comp.setBounds(x, y, coldim.width, rowdim.height); 
                        x += coldim.width; 
                    }
                    x = px;  
                    y += rowdim.height; 
                } 
            } 
        }
        
        private void layoutContainerV( Container parent, List<CheckButton> items ) { 
            Insets pads = root.getPreferredPadding();
            Insets margin = parent.getInsets();
            int pw = parent.getWidth();
            int ph = parent.getHeight(); 
            int px = margin.left + pads.left;
            int py = margin.top + pads.top;
            
            int itemcount = items.size(); 
            if (itemcount <= 0) return;
            
            int rows=0, cols=0;
            int cellpad = root.getPreferredCellPadding();                
            int itemVisibleCount = root.getItemCount();
            if ( itemVisibleCount <= 0 ) {
                rows = itemcount; 
                cols = 1; 
            } else {
                rows = itemVisibleCount; 
                cols = (int) (itemcount / itemVisibleCount); 
                if (itemcount % itemVisibleCount > 0) cols += 1; 
            } 
            
            int itemindex = 0; 
            CellInfo[][] cells = new CellInfo[rows][cols];
            for (int c=0; c<cols; c++ ) {
                for (int r=0; r<rows; r++ ) { 
                    if ( itemindex >= items.size()) break; 

                    Dimension cdim = items.get( itemindex ).getPreferredSize(); 
                    CellInfo ci = new CellInfo();
                    ci.index = itemindex; 
                    ci.width = cdim.width;
                    ci.height = cdim.height; 
                    cells[r][c] = ci; 
                    itemindex += 1;
                } 
            } 
            
            int x=px, y=py;
            for (int r=0; r<rows; r++ ) {
                if ( r > 0 ) y += cellpad; 

                Dimension rowdim = computeRowSize(cells[r]);
                for (int c=0; c<cols; c++) { 
                    CellInfo ci = cells[r][c];
                    if ( ci == null ) break; 
                    if ( c > 0 ) x += cellpad; 

                    Dimension coldim = computeColSize(cells, c); 
                    Component comp = items.get( ci.index ); 
                    comp.setBounds(x, y, coldim.width, rowdim.height); 
                    x += coldim.width; 
                }
                x = px;  
                y += rowdim.height; 
            } 
        }
        
        private Dimension computeRowSize( CellInfo[] cells ) {
            int cellpad = root.getPreferredCellPadding(); 
            Dimension dim = new Dimension(0,0); 
            for (int i=0; i<cells.length; i++) {
                CellInfo ci = cells[i];
                if ( ci == null ) break;

                dim.width += (ci.width + (i>0 ? cellpad: 0)); 
                dim.height = Math.max(dim.height, ci.height); 
            }
            return dim; 
        }
        private Dimension computeColSize( CellInfo[][] cells, int index ) {
            int cellpad = root.getPreferredCellPadding(); 
            Dimension dim = new Dimension(0,0); 
            for (int r=0; r<cells.length; r++) {
                CellInfo[] cols = cells[r];
                if ( index >= 0 && index < cols.length ) {
                    CellInfo ci = cols[index]; 
                    if ( ci == null ) continue; 
                    
                    dim.width = Math.max(dim.width, ci.width); 
                    dim.height += (ci.height + (r > 0 ? cellpad: 0)); 
                } 
            } 
            return dim; 
        }

        public void addLayoutComponent(Component comp, Object constraints) {
        }

        public Dimension maximumLayoutSize(Container target) {
            return new Dimension( Integer.MAX_VALUE, Integer.MAX_VALUE );
        }

        public float getLayoutAlignmentX(Container target) { return 0.5f; }
        public float getLayoutAlignmentY(Container target) { return 0.5f; }

        public void invalidateLayout(Container target) { 
            synchronized ( target.getTreeLock() ){
                
            }
        }
    }
    
    private class CellInfo {
        int index; 
        int width;
        int height;
    }
    
    //</editor-fold>        
    
}
