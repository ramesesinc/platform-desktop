/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.control.panel;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

/**
 *
 * @author wflores
 */
class WrapPanel extends JPanel {

    private ViewportHandlerImpl viewHandler;
    private LayoutImpl layout;
    private Container viewPort;
    private JScrollPane scroller;
    
    private int cellSpacing;
    private Dimension cellSize;
    private Insets padding;
    
    public WrapPanel() {
        super();
        initComponent(); 
    }
 
    private void initComponent() {
        layout = new LayoutImpl();
        super.setLayout(layout);
        viewHandler = new ViewportHandlerImpl(); 
        padding = new Insets(5, 5, 5, 5);
        cellSize = new Dimension(80, 80);
        cellSpacing = 5; 
    } 

    public void setLayout(LayoutManager mgr) {
    }
    
    public int getCellSpacing() { return cellSpacing; } 
    public void setCellSpacing(int cellSpacing) {
        this.cellSpacing = cellSpacing; 
    }
    
    public Dimension getCellSize() { return cellSize; } 
    public void setCellSize(Dimension cellSize) {
        this.cellSize = cellSize; 
    }    
    
    public Insets getPadding() { return padding; } 
    public void setPadding(Insets padding) {
        this.padding = padding;
    }    

    public void addNotify() {
        super.addNotify();
        
        if ( viewPort != null ) {
            viewPort.removeComponentListener(viewHandler);
        }
        viewPort = null; 
        
        if ( getParent() instanceof JViewport ) {
            viewPort = getParent(); 
            viewPort.addComponentListener(viewHandler);
            if ( viewPort.getParent() instanceof JScrollPane ) {
                JScrollPane jsp = (JScrollPane) viewPort.getParent(); 
                jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); 
                jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); 
                jsp.getVerticalScrollBar().setUnitIncrement( 10 ); 
                scroller = jsp; 
            }
        } else {
            addComponentListener(viewHandler); 
        }
    }

    public void removeNotify() {
        removeComponentListener(viewHandler); 
        
        if ( viewPort != null ) {
            viewPort.removeComponentListener(viewHandler);
        }
        viewPort = null;
        scroller = null; 
        super.removeNotify();
    }
    
    private Dimension getPreferredCellSize() {
        Dimension dim = getCellSize();
        if ( dim == null ) { 
            dim = new Dimension(80,80);
        }
        return dim;
    }
    
    
    private class ViewportHandlerImpl extends ComponentAdapter {

        WrapPanel root = WrapPanel.this;
        
        public void componentResized(ComponentEvent e) {
            Component comp = e.getComponent();
            Dimension dim = comp.getSize(); 
            root.layout.maxWidth = dim.width;    
            if ( comp instanceof JViewport ) {
                root.layout.maxWidth = dim.width-30; 
            }
            root.revalidate(); 
        }        
    }
    

    private class LayoutImpl implements LayoutManager {
        
        WrapPanel root = WrapPanel.this;
        int maxWidth = Integer.MAX_VALUE; 
        
        public void addLayoutComponent(String name, Component comp) {
        }

        public void removeLayoutComponent(Component comp) {
        }

        private Component[] getVisibleComponents(Container parent) {
            ArrayList<Component> list = new ArrayList(); 
            Component[] comps = parent.getComponents(); 
            for (int i=0; i<comps.length; i++) {
                if ( comps[i].isVisible()) {
                    list.add( comps[i]); 
                }
            }
            return list.toArray(new Component[]{}); 
        }
   
        private boolean isInViewport( Container parent ) {
            return (parent.getParent() instanceof JViewport );
        }
        
        private Dimension getLayoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                Insets margin = parent.getInsets(); 
                int w = (margin.left + margin.right);
                int h = (margin.top + margin.bottom);
                
                Insets pad = root.getPadding(); 
                if ( pad != null ) {
                    w += (pad.left + pad.right);
                    h += (pad.top + pad.bottom);
                }

                Dimension dim = root.getPreferredCellSize(); 
                if ( isInViewport(parent)) {
                    TableInfo table = buildTable(parent); 
                    dim = table.getSize();
                }
                
                w += dim.width; 
                h += dim.height;
                return new Dimension( w, h ); 
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
                Insets pad = root.getPadding(); 
                if ( pad == null ) pad = new Insets(0,0,0,0); 
                
                Insets margin = parent.getInsets();
                int x = (margin.left + pad.left); 
                int y = (margin.top + pad.top); 
                
                TableInfo table = buildTable(parent); 
                for (int r=0; r<=table.rowIndex; r++) {
                    if ( r > 0 ) y += table.spacing;
                    
                    Dimension rowdim = table.getRowSize(r); 
                    CellInfo[] cells = table.getRowCells(r); 
                    for (int i=0; i<cells.length; i++) {
                        if ( i > 0 ) x += table.spacing;
                        
                        Component comp = cells[i].comp;
                        comp.setBounds(x, y, cells[i].width, cells[i].height);
                        x += cells[i].width;
                    }
                    y += rowdim.height; 
                    x = (margin.left + pad.left); 
                } 
            }
        }
        
        private TableInfo buildTable(Container parent) {       
            Insets margin = parent.getInsets();
            Component[] comps = getVisibleComponents(parent); 
            TableInfo table = new TableInfo( comps.length, root.getCellSpacing(), root.getPreferredCellSize() );             
            for (int i=0; i<comps.length; i++) { 
                CellInfo ci = table.addCell( comps[i] ); 
                Dimension rowdim = table.getCurrentRowSize(); 
                int rwidth = rowdim.width + margin.left + margin.right;
                if ( rwidth > maxWidth ) {
                    table.moveToNextRow( ci ); 
                } 
            } 
            return table; 
        }        
    }
    
    private class TableInfo {
        int rowIndex;
        int colIndex;
        int cellIndex;
        int maxColIndex;
        int spacing;
        CellInfo[] cells;
        Dimension cellSize;
        
        TableInfo( int size, int spacing, Dimension cellSize ) {
            this.cells = new CellInfo[ size ]; 
            this.spacing = (spacing > 0 ? spacing : 0);
            this.cellSize = cellSize;
        }
        CellInfo addCell( Component comp ) {
            CellInfo c = new CellInfo(); 
            c.comp = comp; 
            c.width = cellSize.width;
            c.height = cellSize.height; 
            c.rowIndex = rowIndex;
            c.colIndex = colIndex; 
            cells[ cellIndex ] = c; 
            cellIndex += 1; 
            colIndex += 1;
            maxColIndex = Math.max( maxColIndex, c.colIndex ); 
            return c; 
        }
        void moveToNextRow( CellInfo ci ) {
            if ( ci == null ) return;
            
            colIndex = 0;
            rowIndex = ci.rowIndex+1;
            ci.rowIndex = rowIndex;
            ci.colIndex = colIndex; 
        }
        void addRow() {
            colIndex = 0;
            rowIndex += 1;
        }    
        Dimension getCurrentRowSize() {
            return getRowSize( rowIndex );
        }
        Dimension getRowSize( int index ) {
            int w=0, h=0; 
            CellInfo[] arr = getRowCells( index ); 
            for (int i=0; i<arr.length; i++) {
                w += arr[i].width; 
                h = Math.max(h, arr[i].height);
            }
            if ( arr.length > 1 && spacing > 0 ) {
                w += ((arr.length-1) * spacing);
            }
            return new Dimension(w,h); 
        }
        int getCellMaxWidth( int index ) {
            int maxw = 0;
            for (int i=0; i<cells.length; i++) {
                if ( cells[i] != null && cells[i].colIndex == index ) {
                    maxw = Math.max( maxw, cells[i].width ); 
                }
            }
            return maxw; 
        }
        int getCellMaxHeight( int index ) {
            int maxh = 0;
            for (int i=0; i<cells.length; i++) {
                if ( cells[i] != null && cells[i].rowIndex == index ) {
                    maxh = Math.max( maxh, cells[i].height ); 
                }
            }
            return maxh; 
        }        
        Dimension getSize() {
            int w=0, h=0; 
            for (int i=0; i<=rowIndex; i++) {
                Dimension dim = getRowSize(i); 
                w = Math.max(w, dim.width); 
                h += dim.height; 
            }
            if ( rowIndex > 1 && spacing > 0 ) {
                h += ((rowIndex-1) * spacing);
            }
            return new Dimension(w,h); 
        }
        
        CellInfo[] getRowCells( int index ) {
            ArrayList<CellInfo> list = new ArrayList();
            for (int i=0; i<cells.length; i++) {
                if ( cells[i] != null && cells[i].rowIndex == index ) {
                    list.add(cells[i]);
                }
            }
            return list.toArray(new CellInfo[]{}); 
        }
    }
    
    private class CellInfo {
        int rowIndex;
        int colIndex;
        int width;
        int height;
        Component comp;
    }
    
}
