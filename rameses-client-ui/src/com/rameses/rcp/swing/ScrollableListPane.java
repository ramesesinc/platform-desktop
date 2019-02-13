/*
 * ScrollableListPane.java
 *
 * Created on June 9, 2014, 2:33 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.swing;

import com.rameses.rcp.common.AbstractListDataProvider;
import com.rameses.rcp.common.ListItem;
import com.rameses.rcp.common.PropertyChangeHandler;
import com.rameses.rcp.common.ScrollListModel;
import com.rameses.rcp.common.TableModelHandler;
import com.rameses.rcp.support.ColorUtil;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;

/**
 *
 * @author wflores
 */
public class ScrollableListPane extends JPanel 
{
    private ListScrollBar scrollBar;
    private JScrollPane scrollPane;
    private JListImpl jList;
    
    private ScrollListModelImpl scrollModel;
    private AbstractListDataProvider model;
    private Object modelObject;
    private int fixedCellHeight;
    
    public ScrollableListPane() {
        initComponent();
    }
    
    private void initComponent() {
        jList = new JListImpl();
        fixedCellHeight = jList.getFixedCellHeight();
        
        scrollBar = new ListScrollBar();
        scrollPane = new JScrollPane(jList); 
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); 
        scrollPane.setViewport(new ViewportImpl(jList));  
        
        super.setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(new ScrollBarPanel(scrollBar), BorderLayout.EAST);

        jList.setCellRenderer(new CellRendererImpl()); 
        jList.addMouseListener(new ListMouseHandler()); 
        jList.addMouseWheelListener(new ListMouseHandler()); 
        jList.addKeyListener(new TableKeyAdapter()); 
    }
    
    // <editor-fold defaultstate="collapsed" desc=" Getters/Setters ">    
    
    public void setLayout(LayoutManager mgr) {
    }
    
    public AbstractListDataProvider getModel() { return model; } 
    public void setModel(AbstractListDataProvider model) { 
        this.model = model; 
        this.modelObject = model; 
        fireModelChanged();
    } 
    
    public void setModel(Object modelObject) { 
        this.model = (AbstractListDataProvider)modelObject;
        this.modelObject = modelObject; 
        fireModelChanged(); 
    } 
    
    public void setCellHeight(int cellHeight) {
        if (jList == null) return;
        if (cellHeight > 0) {
            jList.setFixedCellHeight(cellHeight); 
        } else {
            jList.setFixedCellHeight(fixedCellHeight); 
        }
    }
    
    // </editor-fold>
        
    // <editor-fold defaultstate="collapsed" desc=" helper methods ">    
    
    private void fireModelChanged() {
        AbstractListDataProvider model = getModel();
        scrollModel = new ScrollListModelImpl();
        scrollBar.setDataProvider(scrollModel); 
        jList.setModel(new ListModel(scrollModel)); 
        
        scrollModel.addHandler(new PropertyChangeHandlerImpl()); 
        scrollModel.load(); 
        scrollModel.addHandler(new TableModelHandlerImpl());        
        scrollBar.adjustValues(); 
    }

    protected String getItemText(Object item) {
        return (item == null? null: item.toString()); 
    }
    
    public void requestFocus() {
        jList.requestFocus(); 
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" JListImpl ">   
    
    private class JListImpl extends JList 
    {
        ScrollableListPane root = ScrollableListPane.this; 

        JListImpl() {
            super(); 
        }
        
        public void setSelectedIndex(int index) {
            super.setSelectedIndex(index);
        }

        protected void fireSelectionValueChanged(int firstIndex, int lastIndex, boolean isAdjusting) {
            super.fireSelectionValueChanged(firstIndex, lastIndex, isAdjusting);
        }

        public void addSelectionInterval(int anchor, int lead) {
            super.addSelectionInterval(anchor, lead);
        }
    }

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ViewPortImpl ">    
    
    private class ViewportImpl extends JViewport
    {
        ScrollableListPane root = ScrollableListPane.this; 
        
        private Color defaultBgcolor = java.awt.SystemColor.control; 
        private Color bgcolor = Color.WHITE;
        private Rectangle oldBounds = new Rectangle(); 
        
        ViewportImpl(Component view) { 
            super.setBackground(bgcolor); 
            super.setOpaque(true); 
            super.setView(view); 
            
            addComponentListener(new ComponentListener() {
                public void componentHidden(ComponentEvent e) {}
                public void componentMoved(ComponentEvent e) {}
                public void componentShown(ComponentEvent e) {}
                
                public void componentResized(ComponentEvent e) 
                {
                    Rectangle rect = getBounds(); 
                    if (rect.height == oldBounds.height) return;
                    
                    oldBounds = rect;
                    boolean dynamic = root.scrollBar.isDynamicallyVisible();
                    if (rect.height < root.jList.getBounds().height && !dynamic) {
                        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); 
                    } else { 
                        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER); 
                    } 
                } 
            });
        }

        public void setBackground(Color bg) {}
        
        public void paint(Graphics g) {
            super.paint(g); 
            
            Color newColor = ColorUtil.brighter(defaultBgcolor.darker(), 20);
            Graphics2D g2 = (Graphics2D) g.create();            
            g2.setColor(newColor);
            g2.drawLine(0, 0, 0, getHeight()); 
            g2.dispose(); 
        }
    }
    
    // </editor-fold>                 
    
    // <editor-fold defaultstate="collapsed" desc=" ScrollBarPanel ">
    
    private class ScrollBarPanel extends JPanel 
    {
        ScrollableListPane root = ScrollableListPane.this;
        
        private Rectangle oldBounds = new Rectangle(); 
        
        ScrollBarPanel(final ListScrollBar scrollBar) {
            Dimension ps = scrollBar.getPreferredSize();
            setPreferredSize(ps);
            setLayout(new BorderLayout());
            
            scrollBar.addPropertyChangeListener(new PropertyChangeListener() 
            {
                public void propertyChange(PropertyChangeEvent evt) {
                    String propName = evt.getPropertyName();
                    if ( "visible".equals(propName) ) {
                        Boolean visible = (Boolean) evt.getNewValue();;
                        setVisible(visible.booleanValue());
                    } 
                } 
            });
            
            setVisible( scrollBar.isVisible() );
            add(scrollBar, BorderLayout.CENTER);
        }        
    }
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" ScrollListModelImpl ">
    
    private class ScrollListModelImpl extends ScrollListModel 
    {
        ScrollableListPane root = ScrollableListPane.this;
        
        public List fetchList(Map params) {
            AbstractListDataProvider model = root.getModel();
            if (model == null) return null; 
            
            return model.fetchList(params); 
        } 
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" CellRendererImpl ">
    
    private class CellRendererImpl implements ListCellRenderer
    {
        ScrollableListPane root = ScrollableListPane.this;
        
        private JLabel label;
        
        CellRendererImpl() {
            label = new JLabel();
            label.setOpaque(true);            
            label.setBorder(BorderFactory.createEmptyBorder(3,5,3,5)); 
        }

        public Component getListCellRendererComponent(JList jlist, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            if (isSelected) {
                label.setOpaque(true);
                label.setBackground(jlist.getSelectionBackground());
                label.setForeground(jlist.getSelectionForeground());                 
            } else {
                label.setOpaque(false);
                label.setForeground(jlist.getForeground()); 
            }
            
            String text = root.getItemText(value); 
            label.setText(text == null? " ": text); 
            return label; 
        }
    }
    
    // </editor-fold>  
    
    // <editor-fold defaultstate="collapsed" desc=" ListMouseHandler ">
    
    private class ListMouseHandler implements MouseWheelListener, MouseListener
    {
        ScrollableListPane root = ScrollableListPane.this;
        
        private void scroll() {
            JList jList = root.jList;
            try { 
                int selIndex = jList.getSelectedIndex();
                ListItem li = root.scrollModel.getListItem(selIndex); 
                root.scrollBar.adjustValue(li.getRownum()); 
                
                Rectangle rect = jList.getCellBounds(selIndex, selIndex);
                jList.scrollRectToVisible(rect);
            } catch(Throwable ex) {;}             
        }
        
        public void mouseWheelMoved(MouseWheelEvent e){ 
            ScrollListModelImpl model = root.scrollModel;
            if (model == null) return;
            
            
            if (model.isProcessing()) return; 

            int rotation = e.getWheelRotation();
            if (rotation == 0) return;

            if (rotation < 0) { 
                model.moveBackRecord();
            } else { 
                model.moveNextRecord(true); 
            } 
            scroll();
        } 

        public void mouseClicked(MouseEvent e) {
            if (root.scrollModel == null) return;
            if (!SwingUtilities.isLeftMouseButton(e)) return;
            if (e.getClickCount() == 2) return;
        }

        public void mousePressed(MouseEvent e) {
            if (root.scrollModel == null) return;            
            if (!SwingUtilities.isLeftMouseButton(e)) return;    
            
            int index = root.jList.getSelectedIndex(); 
            if (index < 0) return;
            
            root.scrollModel.setSelectedItem(index);
        }
        
        public void mouseReleased(MouseEvent e) {
            if (root.scrollModel == null) return;            
            if (!SwingUtilities.isLeftMouseButton(e)) return;    
            if (e.getClickCount() == 2) return;
            
            int index = root.jList.getSelectedIndex(); 
            ListItem li = root.scrollModel.getListItem(index); 
            EventQueue.invokeLater(new OnselectNotifier(li));             
        }
        
        public void mouseEntered(MouseEvent e) {}
        public void mouseExited(MouseEvent e) {}
    }
            
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" PropertyChangeHandlerImpl ">    
    
    private class PropertyChangeHandlerImpl implements PropertyChangeHandler 
    {
        ScrollableListPane root = ScrollableListPane.this; 
        
        public void firePropertyChange(String name, int value) {
        }

        public void firePropertyChange(String name, boolean value) {
            if ("loading".equals(name)) {
                root.scrollBar.setEnabled(!value); 
            }             
        }

        public void firePropertyChange(String name, String value) {
        }

        public void firePropertyChange(String name, Object value) {
            if ("selectedItemChanged".equals(name)) {
                ScrollListModel slm = root.scrollModel; 
                ListItem li = slm.getSelectedItem();
                if (li == null) return;
                
                int index = li.getIndex(); 
                root.jList.setSelectedIndex(index);                 
                root.jList.ensureIndexIsVisible(index); 
                root.scrollBar.adjustValues(); 
            } 
        }   
    }
    
    // </editor-fold>     
    
    // <editor-fold defaultstate="collapsed" desc=" TableModelHandlerImpl ">    
    
    private class TableModelHandlerImpl implements TableModelHandler 
    {
        ScrollableListPane root = ScrollableListPane.this; 

        public void fireTableCellUpdated(int row, int column) {}
        public void fireTableRowsDeleted(int firstRow, int lastRow) {}
        public void fireTableRowsInserted(int firstRow, int lastRow) {}
        public void fireTableRowsUpdated(int firstRow, int lastRow) {}
        
        public void fireTableDataProviderChanged() {
        } 
        
        public void fireTableStructureChanged() {
            root.jList.repaint(); 
        }

        public void fireTableDataChanged() {
            root.jList.repaint(); 
        }
        
        public void fireTableRowSelected(int row, boolean focusOnItemDataOnly) {
        }
    }
    
    // </editor-fold>  
    
    // <editor-fold defaultstate="collapsed" desc=" TableKeyAdapter ">
    
    private class TableKeyAdapter extends KeyAdapter 
    {       
        ScrollableListPane root = ScrollableListPane.this; 
        
        private void scroll() { 
            JList jList = root.jList;
            try { 
                int selIndex = jList.getSelectedIndex();
                Rectangle rect = jList.getCellBounds(selIndex, selIndex);
                root.scrollBar.adjustValues(); 
                jList.scrollRectToVisible(rect);
            } catch(Throwable ex) {;} 
        } 
        
        public void keyPressed(KeyEvent e) {
            ScrollListModel model = root.scrollModel;
            if (model == null) return;
            
            int index = root.jList.getSelectedIndex();
            if (index < 0) return;
            
            switch (e.getKeyCode()) {
                case KeyEvent.VK_DOWN:
                    e.consume(); 
                    model.moveNextRecord(); 
                    scroll();
                    break;
                    
                case KeyEvent.VK_UP:
                    e.consume(); 
                    model.moveBackRecord(); 
                    scroll(); 
                    break;
                    
                case KeyEvent.VK_ENTER:
                    notifyOnSelect( model.getListItem(index) );
                    break;
                    
                case KeyEvent.VK_PAGE_DOWN: 
                    e.consume(); 
                    break;
                    
                case KeyEvent.VK_PAGE_UP: 
                    e.consume(); 
                    break;
                    
                case KeyEvent.VK_HOME: 
                    e.consume(); 
                    break;
                    
                case KeyEvent.VK_END: 
                    e.consume(); 
                    break;
            }
        }        
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Handler interface ">
    
    public static interface Handler 
    {
        void onselect(Object item);  
    } 
    
    
    private List<Handler> handlers = new ArrayList(); 
    
    public void removeHandler(Handler handler) {
        if (handler != null) handlers.remove(handler); 
    }
    public void addHandler(Handler handler) {
        if (handler == null) return;
        if (!handlers.contains(handler)) {
            handlers.add(handler); 
        }
    }
    
    private void notifyOnSelect(ListItem li) {
        Object item = (li == null? null: li.getItem()); 
        if (item == null) return;
        
        for (Handler handler: handlers) {
            handler.onselect(item); 
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" OnselectNotifier ">
    
    private class OnselectNotifier implements Runnable 
    {
        ScrollableListPane root = ScrollableListPane.this;
        
        private ListItem item; 
        
        OnselectNotifier(ListItem item) {
            this.item = item;
        }

        public void run() {
            if (item == null) return;
            
            root.notifyOnSelect(item);
        }
    }
    
    // </editor-fold>
    
}
