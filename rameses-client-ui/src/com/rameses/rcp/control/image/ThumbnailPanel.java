/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.control.image;

import com.rameses.rcp.control.layout.XLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.beans.Beans;
import java.net.URL;
import java.util.HashMap;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author wflores 
 */
public class ThumbnailPanel extends JPanel {
    
    private Dimension cellSize;
    private int cellSpacing;
    private int rowCount;
    
    private boolean singleRowOnly;
    private boolean singleColumnOnly;
    
    private JListImpl jlist; 
    private JScrollPane jscroll;
    private Border cellBorder;    
    private Color selectionBorderColor;
    
    private ThumbnailListModel model; 
    private boolean updating_model;
    
    public ThumbnailPanel() {
        initComponent();
    }    
    
    // <editor-fold defaultstate="collapsed" desc=" init component ">
    
    private void initComponent() {
        cellSpacing = 5; 
        selectionBorderColor = Color.decode("#505050"); 
        
        jlist = new JListImpl();
        jlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); 
        jlist.setCellRenderer(new ListCellRendererImpl());
        jlist.addListSelectionListener(new ListSelectionHandlerImpl());
        setCellSize( getDefaultCellSize() ); 
        setRowCount(-1); 
        
        jscroll = new JScrollPane(jlist); 
        super.setLayout(new DefaultLayout()); 
        add( jscroll ); 
        
        if (Beans.isDesignTime()) {
            jlist.setModel(new DesignTimeListModel()); 
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Getters/Setters ">
        
    public void setLayout(LayoutManager layout) {}
    
    public ThumbnailListModel getModel() { return model; } 
    public void setModel( ThumbnailListModel model ) {
        this.model = (model == null ? new ThumbnailListModel() : model); 
        
        try { 
            updating_model = true;
            jlist.setModel( this.model ); 
        } finally {
            updating_model = false; 
        }
    }
    
    protected Dimension getDefaultCellSize() {
        return new Dimension(60, 60);
    }
    
    public Dimension getCellSize() { return cellSize; } 
    public void setCellSize(Dimension cellSize) {
        this.cellSize = cellSize;         
        adjustListComponent();
    }

    public int getCellSpacing() { return cellSpacing; } 
    public void setCellSpacing(int cellSpacing) {
        this.cellSpacing = cellSpacing;
        adjustListComponent();
    }
    
    public int getRowCount() { return rowCount; } 
    public void setRowCount(int rowCount) {
        this.rowCount = ( rowCount < 0 ? -1 : rowCount); 
        adjustListComponent();
    }
    
    public Color getSelectionBorderColor() { return selectionBorderColor; } 
    public void setSelectionBorderColor(Color selectionBorderColor) {
        this.selectionBorderColor = selectionBorderColor; 
    }
    
    public Border getCellBorder() { return cellBorder; } 
    public void setCellBorder(Border cellBorder) {
        this.cellBorder = cellBorder; 
    }
    
    public boolean isSingleRowOnly() { return singleRowOnly; }
    public void setSingleRowOnly( boolean singleRowOnly ) {
        this.singleRowOnly = singleRowOnly; 
        adjustListComponent();
    }
    
    public boolean isSingleColumnOnly() { return singleColumnOnly; } 
    public void setSingleColumnOnly( boolean singleColumnOnly ) {
        this.singleColumnOnly = singleColumnOnly; 
        adjustListComponent();
    }

    public int getSelectedIndex() {
        return jlist.getSelectedIndex(); 
    }
    public void setSelectedIndex( int index ) {
        try { 
            jlist.setSelectedIndex( index ); 
        } catch(Throwable t){;} 
    }
    
    public ThumbnailItem getSelectedItem() { 
        Object o = jlist.getSelectedValue(); 
        if ( o instanceof ThumbnailItem ) {
            return (ThumbnailItem) o;
        } else {
            return null; 
        }
    }
    public void selectFirstItem() { 
        try { 
            jlist.setSelectedIndex(0); 
        } catch(Throwable t){;} 
    } 
    
    public void refresh() {
        jlist.revalidate();
        jlist.repaint();
    }
    public void moveNext() {
        int selindex = jlist.getSelectedIndex()+1; 
        if ( selindex >= 0 && selindex < jlist.getModel().getSize()) {
            jlist.setSelectedIndex( selindex );
        }
    }
    public void movePrevious() {
        int selindex = jlist.getSelectedIndex()-1; 
        if ( selindex >= 0 && selindex < jlist.getModel().getSize()) { 
            jlist.setSelectedIndex( selindex ); 
        } 
    }
    
    private Dimension getPreferredCellSize() {
        Dimension size = getCellSize();
        if (size == null) size = getDefaultCellSize();
        
        return new Dimension(size.width, size.height); 
    }
    private void adjustListComponent() {
        int spacing = getCellSpacing();
        if ( spacing <= 0 ) spacing=0;
        
        Dimension dim = getPreferredCellSize();         
        jlist.setFixedCellWidth( dim.width + spacing );
        jlist.setFixedCellHeight( dim.height + spacing ); 
                
        int rowcount = getRowCount();
        if ( isSingleColumnOnly()) {
            jlist.setLayoutOrientation(JList.VERTICAL); 
        } else if ( isSingleRowOnly()) { 
            jlist.setVisibleRowCount(1); 
            jlist.setLayoutOrientation(JList.HORIZONTAL_WRAP); 
        } else if ( rowcount <= 0 ) {
            jlist.setVisibleRowCount(-1);
            jlist.setLayoutOrientation(JList.HORIZONTAL_WRAP);  
        } 
    }
    
    protected void selectionChanged(){         
    }
        
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" DefaultLayout "> 
    
    private class DefaultLayout implements LayoutManager {
        
        ThumbnailPanel root = ThumbnailPanel.this; 
        
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
                Insets margin = parent.getInsets();
                int w = margin.left + margin.right;
                int h = margin.top + margin.bottom;
                
                Dimension dim = root.getPreferredCellSize();
                return new Dimension(w + 100, h + 50); 
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
                root.jscroll.setBounds(x, y, w, h); 
            }
        }
    }
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" ListModel implementations ">
        
    private class DesignTimeListModel extends ThumbnailListModel { 
        DesignTimeListModel() {
            add( new HashMap());
            add( new HashMap());
            add( new HashMap());
        }
    }
    
    private class ListSelectionHandlerImpl implements ListSelectionListener { 
        
        ThumbnailPanel root = ThumbnailPanel.this;
        
        public void valueChanged(ListSelectionEvent e) { 
            if ( root.updating_model ) return;
            if ( e.getValueIsAdjusting() ) return; 
            root.selectionChanged(); 
        }        
    }
    
    // </editor-fold> 
    
    // <editor-fold defaultstate="collapsed" desc=" ListCellRendererImpl ">
    
    private class ListCellRendererImpl implements ListCellRenderer {

        ThumbnailPanel root = ThumbnailPanel.this;
        
        private JPanel panel;
        private ItemLabel label;
        private ImageIcon unknownIcon; 
        
        ListCellRendererImpl() {
            label = new ItemLabel();
            panel = new JPanel();
            panel.setLayout(new XLayout()); 
            panel.setOpaque( false );
            panel.add( label ); 
            
            URL url = getClass().getResource("unknown.png"); 
            if ( url != null ) unknownIcon = new ImageIcon( url ); 
        }

        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) { 
            ThumbnailItem item = null; 
            if ( value instanceof ThumbnailItem ) {
                item = (ThumbnailItem) value; 
            } else {
                item = new ThumbnailItem();
            }
            
            int spacing = root.getCellSpacing(); 
            if ( spacing < 0 ) spacing = 0; 
            
            panel.setBorder( BorderFactory.createEmptyBorder(spacing, spacing, 0, 0 )); 
            
            ImageIcon icon = item.getIcon(); 
            if ( icon == null ) icon = unknownIcon; 
            
            label.setOpaque( false ); 
            label.setPreferredSize( root.getPreferredCellSize()); 
            label.selected = isSelected;            
            label.setImage( icon ); 
            return panel; 
        }
    }
    
    private class ItemLabel extends JLabel {

        ThumbnailPanel root = ThumbnailPanel.this;
        
        private ImageIcon icon; 
        private boolean selected; 
        
        private Border defaultBorder; 
        private Border selectedBorder; 
        
        ItemLabel() {
            defaultBorder = BorderFactory.createEmptyBorder(1,1,1,1); 
            selectedBorder = BorderFactory.createLineBorder(root.jlist.getSelectionBackground(), 1); 
        }
        
        void setImage( ImageIcon icon ) {
            this.icon = (icon == null? createEmptyImage() : icon); 
            
        }      
        
        private ImageIcon createEmptyImage() { 
            Dimension dim = root.getPreferredCellSize();
            BufferedImage bi = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_ARGB); 
            Graphics2D g = bi.createGraphics(); 
            g.setColor( Color.decode("#a0a0a0") );  
            g.fillRect(0, 0, dim.width, dim.height);
            g.dispose();
            return new ImageIcon(bi); 
        } 

        protected void paintComponent(Graphics g) {
            paintIconImpl(g); 
            paintBorderImpl(g);
        }
        
        private void paintIconImpl(Graphics g) { 
            if ( icon != null ) {
                Dimension resizedim = new Dimension( getWidth(), getHeight() );
                Dimension origdim = new Dimension(icon.getIconWidth(), icon.getIconHeight());
                double scaleX = resizedim.getWidth() / origdim.getWidth();
                double scaleY = resizedim.getHeight() / origdim.getHeight();
                double scale = (scaleY > scaleX) ? scaleX : scaleY;
                int nw = (int) (origdim.width * scale);
                int nh = (int) (origdim.height * scale);
                int nx = (resizedim.width - nw) / 2;
                int ny = (resizedim.height - nh) / 2;
                Graphics2D g2 = (Graphics2D) g.create(); 
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.drawImage( icon.getImage(), nx, ny, nw, nh, null);
                g2.dispose();
            } 
        } 
        private void paintBorderImpl(Graphics g) { 
            Graphics2D g2 = (Graphics2D) g.create(); 
            if ( selected ) {
                selectedBorder.paintBorder(this, g2, 0, 0, getWidth(), getHeight());
            } else {
                defaultBorder.paintBorder(this, g2, 0, 0, getWidth(), getHeight());
            }
            g2.dispose(); 
        } 
                
    }
    
    // </editor-fold>     
    
    // <editor-fold defaultstate="collapsed" desc=" JListImpl ">

    private class JListImpl extends JList {
        
    }
    
    // </editor-fold> 
}
