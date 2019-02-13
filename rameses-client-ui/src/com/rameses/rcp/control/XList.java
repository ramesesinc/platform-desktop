/*
 * XList.java
 *
 * Created on October 29, 2010, 10:59 AM
 * @author jaycverg
 */
package com.rameses.rcp.control;

import com.rameses.common.MethodResolver;
import com.rameses.rcp.common.ListPaneModel;
import com.rameses.rcp.common.MapObject;
import com.rameses.rcp.common.MsgBox;
import com.rameses.rcp.common.PropertySupport;
import com.rameses.rcp.control.table.ExprBeanSupport;
import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.support.FontSupport;
import com.rameses.rcp.support.ImageIconSupport;
import com.rameses.rcp.support.MouseEventSupport;
import com.rameses.rcp.ui.ActiveControl;
import com.rameses.rcp.ui.ControlProperty;
import com.rameses.rcp.ui.UIControl;
import com.rameses.rcp.util.UIControlUtil;
import com.rameses.util.BreakException;
import com.rameses.util.ValueUtil;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.beans.Beans;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class XList extends JList implements UIControl, ActiveControl, MouseEventSupport.ComponentInfo {
    
    private ListSelectionSupport selectionSupport;    
    
    private Binding binding;
    private String[] depends;
    private String varName;
    private String varStatus;
    private String expression;
    private String items;
    private String handler;
    private String openAction;  
    private boolean dynamic;
    private int index;    

    private ListPaneModel listPaneModel;
    private DefaultListModelImpl model;
    private Insets padding = new Insets(1,3,1,3);    
    private int cellVerticalAlignment = SwingConstants.CENTER;
    private int cellHorizontalAlignment = SwingConstants.LEADING;
    private int cellHorizontalTextPosition = SwingConstants.TRAILING; 
    private int cellVecticalTextPosition = SwingConstants.CENTER;     
    
    private boolean enableNavigation;
    
    private FontSupport fontSupport; 
    private Font sourceFont; 
    private String fontStyle; 
    
    private int stretchWidth;
    private int stretchHeight;     
    private String visibleWhen;
    
    private RefreshTask refreshTask;
    
    public XList() {
        super.addListSelectionListener(getSelectionSupport()); 
        setCellRenderer(new DefaultCellRenderer());
        setEnableNavigation(true); 
        setMultiselect(false);
        setVarName("item");

        if ( Beans.isDesignTime() ) {
            setPreferredSize(new Dimension(80, 100));
            super.setModel(new javax.swing.AbstractListModel() {
                String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
                public int getSize() { return strings.length; }
                public Object getElementAt(int i) { return strings[i]; }
            });
        } else {
            registerKeyboardAction(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    fireOpenItem();
                }
            }, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), JComponent.WHEN_FOCUSED);
            
            registerKeyboardAction(new ActionListener() {
                public void actionPerformed(ActionEvent e) { 
                    if ( listPaneModel == null ) return;
                    if ( !listPaneModel.isEditable()) return; 
                    if ( !listPaneModel.isAllowRemove()) return; 
                    
                    fireRemoveItem();
                }
            }, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), JComponent.WHEN_FOCUSED);            
        } 
        new MouseEventSupport(this).install(); 
    }

    // <editor-fold defaultstate="collapsed" desc=" Getters/Setters ">
    
    public final void setModel(ListModel model) {;}    
    
    public String getHandler() { return handler; } 
    public void setHandler(String handler) { this.handler = handler; } 
    
    public String getVarName() { return varName; } 
    public void setVarName(String varName) { this.varName = varName; }
    
    public String getVarStatus() { return varStatus; } 
    public void setVarStatus(String varStatus) { this.varStatus = varStatus; }    
            
    public String getExpression() { return expression; }    
    public void setExpression(String expression) {
        this.expression = expression;
    }
    
    public String getItems() { return items; }    
    public void setItems(String items) { this.items = items; }
    
    public boolean isDynamic() { return dynamic; }    
    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic;
    }
    
    public boolean isEnableNavigation() { return enableNavigation; } 
    public void setEnableNavigation(boolean enableNavigation) {
        this.enableNavigation = enableNavigation;
        boolean enabled = isEnabled();
        firePropertyChange("enabled", !enabled, enabled); 
    }

    public boolean isMultiselect() {
        return getSelectionMode() == ListSelectionModel.MULTIPLE_INTERVAL_SELECTION;
    }       
    public void setMultiselect(boolean multi) 
    {
        if ( multi )
            setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        else
            setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }
    
    public Insets getPadding() { return padding; }    
    public void setPadding(Insets padding) {
        this.padding = (padding == null? new Insets(0,0,0,0): padding);
    }
    
    public int getCellVerticalAlignment() { return cellVerticalAlignment; }    
    public void setCellVerticalAlignment(int cellVerticalAlignment) {
        this.cellVerticalAlignment = cellVerticalAlignment;
    }
    
    public int getCellHorizontalAlignment() { return cellHorizontalAlignment; }    
    public void setCellHorizontalAlignment(int cellHorizontalAlignment) {
        this.cellHorizontalAlignment = cellHorizontalAlignment;
    }

    public int getCellVerticalTextPosition() { return cellVecticalTextPosition; }    
    public void setCellVerticalTextPosition(int cellVecticalTextPosition) {
        this.cellVecticalTextPosition = cellVecticalTextPosition;
    }
        
    public int getCellHorizontalTextPosition() { return cellHorizontalTextPosition; }    
    public void setCellHorizontalTextPosition(int cellHorizontalTextPosition) {
        this.cellHorizontalTextPosition = cellHorizontalTextPosition;
    }    
    
    public String getOpenAction() { return openAction; }
    public void setOpenAction(String openAction) {
        this.openAction = openAction;
    }

    private FontSupport getFontSupport() {
        if (fontSupport == null) { 
            fontSupport = new FontSupport();
        } 
        return fontSupport; 
    }
    
    public void setFont(Font font) {
        sourceFont = font; 
        if (sourceFont != null) {
            Map attrs = getFontSupport().createFontAttributes(getFontStyle()); 
            sourceFont = getFontSupport().applyStyles(sourceFont, attrs);
        }
        super.setFont(sourceFont);         
    }
    
    public String getFontStyle() { return fontStyle; } 
    public void setFontStyle(String fontStyle) {
        this.fontStyle = fontStyle;
        if (sourceFont == null) {
            sourceFont = super.getFont();
        } 
        
        Font font = sourceFont;
        if (font == null) { return; } 
        
        Map attrs = getFontSupport().createFontAttributes(getFontStyle()); 
        font = getFontSupport().applyStyles(font, attrs);
        super.setFont(font); 
    }     
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" UIControl implementation ">

    public int getIndex() { return index; }    
    public void setIndex(int index) { this.index = index; }
    
    public String[] getDepends() { return depends; }    
    public void setDepends(String[] depends) { this.depends = depends; }
    
    public Binding getBinding() { return binding; }    
    public void setBinding(Binding binding) { this.binding = binding; }  
    
    public void load() {
        model = new DefaultListModelImpl();
        super.setModel(model);
        
        if (!isDynamic()) buildList();
    }
    
    public void refresh() { 
        refresh(isDynamic());  
        
        String whenExpr = getVisibleWhen();
        if (whenExpr != null && whenExpr.length() > 0) {
            boolean result = false; 
            try { 
                result = UIControlUtil.evaluateExprBoolean(binding.getBean(), whenExpr);
            } catch(Throwable t) {
                t.printStackTrace();
            }
            setVisible( result ); 
        }
        
        long intervalrate = (listPaneModel==null? 0 : listPaneModel.getRefreshInterval()); 
        if ( intervalrate > 0 ) executeRefreshTask( intervalrate );
    } 
        
    private void refresh(boolean reload) {
        if (reload) buildList();
        
        selectSelectedItems();        
    }    
    
    public int compareTo(Object o) {
        return UIControlUtil.compare(this, o);
    }    
    
    public Map getInfo() { 
        Map map = new HashMap();
        map.put("dynamic", isDynamic()); 
        map.put("expression", getExpression());
        map.put("handler", getHandler()); 
        map.put("items", getItems());
        map.put("multiselect", isMultiselect());
        map.put("openAction", getOpenAction());
        map.put("varName", getVarName());
        map.put("varStatus", getVarStatus());
        return map;
    }     
    
    public void setPropertyInfo(PropertySupport.PropertyInfo info) {
    }
    
    public int getStretchWidth() { return stretchWidth; } 
    public void setStretchWidth(int stretchWidth) {
        this.stretchWidth = stretchWidth; 
    }

    public int getStretchHeight() { return stretchHeight; } 
    public void setStretchHeight(int stretchHeight) {
        this.stretchHeight = stretchHeight;
    }    
    
    public String getVisibleWhen() { return visibleWhen; } 
    public void setVisibleWhen( String visibleWhen ) {
        this.visibleWhen = visibleWhen;
    }    
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ActiveControl implementation ">    
    
    private ControlProperty property; 
    
    public ControlProperty getControlProperty() { 
        if ( property == null ) {
            property = new ControlProperty(); 
        } 
        return property; 
    } 
    
    public String getCaption() { 
        return getControlProperty().getCaption(); 
    }    
    public void setCaption(String caption) { 
        getControlProperty().setCaption( caption ); 
    }
    
    public char getCaptionMnemonic() {
        return getControlProperty().getCaptionMnemonic();
    }    
    public void setCaptionMnemonic(char c) {
        getControlProperty().setCaptionMnemonic(c);
    }

    public int getCaptionWidth() {
        return getControlProperty().getCaptionWidth();
    }    
    public void setCaptionWidth(int width) {
        getControlProperty().setCaptionWidth(width);
    }

    public boolean isShowCaption() {
        return getControlProperty().isShowCaption();
    } 
    public void setShowCaption(boolean show) {
        getControlProperty().setShowCaption(show);
    }
    
    public Font getCaptionFont() {
        return getControlProperty().getCaptionFont();
    }    
    public void setCaptionFont(Font f) {
        getControlProperty().setCaptionFont(f);
    }
    
    public String getCaptionFontStyle() { 
        return getControlProperty().getCaptionFontStyle();
    } 
    public void setCaptionFontStyle(String captionFontStyle) {
        getControlProperty().setCaptionFontStyle(captionFontStyle); 
    }    
    
    public Insets getCellPadding() {
        return getControlProperty().getCellPadding();
    }    
    public void setCellPadding(Insets padding) {
        getControlProperty().setCellPadding(padding);
    }    

    // </editor-fold>        
   
    // <editor-fold defaultstate="collapsed" desc=" Owned / helper methods ">

    public void addListSelectionListener(ListSelectionListener listener) {
        getSelectionSupport().add(listener); 
    }
    
    public void removeListSelectionListener(ListSelectionListener listener) {
        getSelectionSupport().remove(listener); 
    }
    
    private ListSelectionSupport getSelectionSupport() {
        if (selectionSupport == null) 
            selectionSupport = new ListSelectionSupport(); 
        
        return selectionSupport; 
    }
    
    private void fireOpenItem() {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                openItem(); 
            }
        }); 
    }
    
    private void fireRemoveItem() { 
        EventQueue.invokeLater(new Runnable() {
            public void run() { 
                try { 
                    removeItem(); 
                    refresh( true ); 
                } catch(BreakException be) {
                    //do nothing 
                } catch(Exception e) {
                    MsgBox.err(e); 
                }
            }
        });
    }
    
    protected void openItem() {
        try {
            if ( ValueUtil.isEmpty(openAction) ) return;
            
            MethodResolver mr = MethodResolver.getInstance();
            Object outcome = mr.invoke(binding.getBean(), openAction, null, null);
            if ( outcome == null ) return;

            binding.fireNavigation(outcome);
        } catch(BreakException be) {
            //do nothing 
        } catch(Exception e) {
            MsgBox.err(e); 
        }
    }
    
    protected boolean removeItem() {
        Object value = getSelectedValue(); 
        return removeItem( value ); 
    }
    protected boolean removeItem( Object item ) {
        if ( listPaneModel == null ) return false;         
        return listPaneModel.removeItem( item ); 
    }

    protected void processMouseEvent(MouseEvent e) {
        if (e.getID() == MouseEvent.MOUSE_PRESSED && e.getClickCount() == 2) {
            e.consume(); 
            fireOpenItem();
        } else { 
            super.processMouseEvent(e); 
        } 
    }    
    
    private void buildList() {
        String strHandler = getHandler();
        String strItems = getItems();        
        boolean hasHandler = (strHandler != null && strHandler.length() > 0);
        boolean hasItems = (strItems != null && strItems.length() > 0);         
        if (!hasHandler && !hasItems) return; 
        
        ListPaneModel newModel = null;
        if (hasHandler) {            
            Object value = null; 
            try { 
                value = UIControlUtil.getBeanValue(this, strHandler); 
            } catch(Throwable t) {
                System.out.println("[WARN] error get bean value caused by " + t.getMessage());
            }
            //--
            if (value instanceof ListPaneModel) {
                newModel = (ListPaneModel)value;
            }        
        } else if (hasItems) {
            Object value = null; 
            try { 
                value = UIControlUtil.getBeanValue(this, strItems); 
            } catch(Throwable t) {
                System.out.println("[WARN] error get bean value caused by " + t.getMessage());
            }
            //--
            newModel = new DefaultListPaneModel(value); 
        }
        loadItems(newModel);         
    } 
    
    private void loadItems(ListPaneModel newModel) {
        if (newModel == null) return;

        Map params = new HashMap(); 
        List list = newModel.fetchList(params); 
        if (list == null) list = new ArrayList();
            
        model.clear(); 
        int i = 0; 
        for (Object o: list) { 
            model.add(i++, o); 
        } 
        
        ListPaneModel oldModel = listPaneModel;
        if (oldModel != null) oldModel.setProvider(null);
        
        listPaneModel = newModel;
        newModel.setProvider(new ProviderImpl()); 
        newModel.afterFetchList();
    } 
        
    private void selectSelectedItems() {
        Object value = null;
        String name = getName();
        if (name != null && name.length() > 0) {
            try {
                value = UIControlUtil.getBeanValue(this);
            } catch(Throwable e) {
                System.out.println("[WARN] error get bean value caused by " + e.getMessage());
            }            
        }

        if (value == null) {
            setSelectedIndex(0); 
            
        } else if ( isMultiselect() ) {
            List list = new ArrayList();
            if ( value instanceof Collection )
                list.addAll( (Collection) value );
            else if ( value.getClass().isArray() ) {
                for(Object o: (Object[]) value) list.add( o );
            }
            
            if ( list.size() == 0 ) return;
            
            List indices = new ArrayList();
            for( int i=0; i < model.getSize(); i++ ) {
                Object item = model.getElementAt(i);
                if ( list.remove( item ) ) indices.add(i);
            }
            if ( indices.size() == 0 ) return;
            
            ListSelectionModel sm = getSelectionModel();
            sm.clearSelection();
            int size = getModel().getSize();
            for(int i = 0; i < indices.size(); i++) {
                int idx = Integer.parseInt( indices.get(i)+"" );
                if ( idx < size) {
                    sm.addSelectionInterval(idx, idx);
                }
            }
            
        } else {
            setSelectedValue(value, true);
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ListSelectionModelImpl (class) ">
    
    private class ListSelectionModelImpl extends DefaultListSelectionModel 
    {
        XList root = XList.this;
        
        public void moveLeadSelectionIndex(int leadIndex) {
            if (!root.beforeSelectionIndex(leadIndex)) return;
            
            super.moveLeadSelectionIndex(leadIndex);
        }
        
        public void setSelectionInterval(int index0, int index1) { 
            if (index0 == 0 && index1 == 0 && root.getSelectedIndex() < 0) {
                super.setSelectionInterval(index0, index1); 
                
            } else { 
                if (!root.beforeSelectionIndex(index1)) return;

                super.setSelectionInterval(index0, index1); 
            }
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" DefaultCellRenderer (class) ">
    
    private class DefaultCellRenderer implements ListCellRenderer {
        XList root = XList.this; 
        private JLabel cellLabel;
        private FontSupport fontSupport; 
        
        DefaultCellRenderer() {
            cellLabel = new JLabel();
            cellLabel.setOpaque(true);
            cellLabel.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 2));  
            fontSupport = new FontSupport(); 
        }
        
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Insets pads = root.getPadding();
            if (pads == null) pads = new Insets(1,3,1,3); 

            cellLabel.setBorder(BorderFactory.createEmptyBorder(pads.top, pads.left, pads.bottom, pads.right)); 
            cellLabel.setComponentOrientation(list.getComponentOrientation());
            cellLabel.setSize(list.getFixedCellWidth(), list.getFixedCellHeight());
            cellLabel.setEnabled(list.isEnabled());
            cellLabel.setFont(list.getFont());
            cellLabel.setVerticalAlignment(getCellVerticalAlignment());
            cellLabel.setHorizontalAlignment(getCellHorizontalAlignment());
            cellLabel.setVerticalTextPosition(getCellVerticalTextPosition());
            cellLabel.setHorizontalTextPosition(getCellHorizontalTextPosition());
            if (cellLabel.isEnabled()) cellLabel.setEnabled(root.isEnableNavigation());
            
            if (isSelected) {
                cellLabel.setEnabled(true); 
                cellLabel.setBackground(list.getSelectionBackground());
                cellLabel.setForeground(list.getSelectionForeground());
                fontSupport.applyStyles(cellLabel, "font-weight:bold;");
            } else {                
                cellLabel.setBackground(list.getBackground());
                cellLabel.setForeground(list.getForeground());
            }
            
            if (Beans.isDesignTime()) {
                cellLabel.setText( value+"" );
                return cellLabel;
            }
            
            Object cellValue = value;
            String expr = getExpression();
            if (expr != null) {
                try {
                    Object exprBean = createExpressionBean(value);
                    cellValue = UIControlUtil.evaluateExpr(exprBean, expr); 
                } catch(Throwable e) {;}
            } 
            
            cellLabel.setText((cellValue == null? " ": cellValue.toString()));            
            
            String strIcon = new MapObject(value).getString("icon");
            if (strIcon == null || strIcon.length() == 0) { 
                strIcon = (root.listPaneModel == null? null: root.listPaneModel.getDefaultIcon()); 
            } 
            if (strIcon != null && strIcon.length() > 0) {
                Icon anIcon = ImageIconSupport.getInstance().getIcon(strIcon);
                if (anIcon == null) {
                    try { 
                        anIcon = UIManager.getLookAndFeelDefaults().getIcon(strIcon); 
                    } catch(Throwable t){;} 
                }
                cellLabel.setIcon(anIcon); 
            }
            return cellLabel;
        }
        
        private Object createExpressionBean(Object itemBean) {
            ExprBeanSupport beanSupport = new ExprBeanSupport(binding.getBean());
            beanSupport.setItem(getVarName(), itemBean); 
            return beanSupport.createProxy(); 
        } 
    }
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" ListSelectionSupport (class) "> 

    private void updateBeanValueToNull() {
        String vstat = getVarStatus(); 
        if ( vstat != null ) {
            UIControlUtil.setBeanValue(getBinding(), vstat, null); 
        }

        final String sname = getName(); 
        if ( sname != null ) { 
            UIControlUtil.setBeanValue(getBinding(), sname, null); 

            EventQueue.invokeLater(new Runnable() {
                public void run() { 
                    binding.notifyDepends( sname );                  
                }
            });
        }
    }
    
    private class ListSelectionSupport implements ListSelectionListener
    {
        XList root = XList.this; 
        List<ListSelectionListener> listeners = new ArrayList(); 
        
        void remove(ListSelectionListener listener) 
        {
            if (listener != null) listeners.remove(listener); 
        }
        
        void add(ListSelectionListener listener) 
        {
            if (listener != null) 
            {
                listeners.remove(listener); 
                listeners.add(listener); 
            }
        }
        
        public void valueChanged(final ListSelectionEvent evt) 
        {
            try 
            {
                int selIndex = root.getSelectedIndex(); 
                if (selIndex != -1 && !evt.getValueIsAdjusting()) 
                {
                    Object value = (root.isMultiselect()? root.getSelectedValues(): root.getSelectedValue());
                    UIControlUtil.setBeanValue(root.getBinding(), root.getName(), value);
                    
                    if (root.getVarStatus() != null) 
                    {
                        ItemStatus stat = new ItemStatus();
                        stat.multiSelect = root.isMultiselect(); 
                        stat.index = root.getSelectedIndex();
                        stat.name = root.getName();                        
                        stat.value = value;
                        UIControlUtil.setBeanValue(root.getBinding(), root.getVarStatus(), stat); 
                    }

                    EventQueue.invokeLater(new Runnable(){
                        public void run() {
                            try { 
                                if (root.listPaneModel == null) return;

                                root.listPaneModel.onselect(root.getSelectedValue()); 
                            } catch(Throwable e) {
                                System.out.println("[WARN] error onselect caused by " + e.getMessage()); 
                            } 
                        } 
                    });
                    
                    EventQueue.invokeLater(new Runnable() {
                        public void run() {
                            binding.notifyDepends(XList.this);                    
                        }
                    });
                } 
                
                //notify listeners
                notifyListeners(evt);
            }
            catch(Exception ex) {
                MsgBox.err(ex); 
            }
        }

        private void notifyListeners(ListSelectionEvent evt) 
        {
            for (ListSelectionListener listener : listeners) { 
                listener.valueChanged(evt); 
            } 
        }    
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ItemStatus (class) ">
    
    public class ItemStatus 
    {
        private Object value;        
        private String name;
        private int index;
        private boolean multiSelect;

        public Object getValue() { return value; }        
        public String getName() { return name; }        
        public int getIndex() { return index; }
        public boolean isMultiSelect() { return multiSelect; }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" DefaultListPaneModel ">
    
    private class DefaultListPaneModel extends ListPaneModel {
        private List list;
        
        DefaultListPaneModel(Object items) {
            if (items instanceof Object[]) {
                this.list = Arrays.asList((Object[]) items);
            } else if (items instanceof List) {
                this.list = (List) items;
            } 
        }
        
        public List fetchList(Map params) {
            return list; 
        }
    }
    
    private class DefaultListModelImpl extends DefaultListModel {

        void fireItemAdded( int index0, int index1 ) {
            fireIntervalAdded( XList.this, index0, index1);
        }
        void fireItemRemoved( int index0, int index1 ) {
            fireIntervalRemoved( XList.this, index0, index1); 
        }
    }
    
    // </editor-fold> 
    
    // <editor-fold defaultstate="collapsed" desc=" ProviderImpl ">
    
    private class ProviderImpl implements ListPaneModel.Provider {
        
        XList root = XList.this; 
        
        public Object getBinding() {
            return root.getBinding(); 
        }

        public void repaint() {
            root.repaint();
        }
        public void refresh() {
            root.refresh();
        }

        public void reload() {
            root.refresh(true); 
        }        

        public void setSelectedIndex(int index) {
            int size = root.getModel().getSize();
            if (index >= 0 && index < size) {
                root.setSelectedIndex(index); 
            }
        }
        
        public Object getItem(int index) { 
            try {
                return root.getModel().getElementAt( index );
            } catch(Throwable t) {
                return null; 
            }
        }
        
        public void addItem( Object item ) throws Exception { 
            if ( item == null ) return; 
            if ( root.listPaneModel != null ) {
                if ( !root.listPaneModel.isEditable()) return; 
                else if ( !root.listPaneModel.isAllowAdd()) return; 
            }
            
            root.model.addElement(item);
        } 

        public void removeSelectedItem() { 
            if ( root.listPaneModel == null ) return; 
            if ( !root.listPaneModel.isEditable()) return; 
            else if ( !root.listPaneModel.isAllowRemove()) return; 
            
            int size = root.model.getSize(); 
            int selindex = root.getSelectedIndex(); 
            if ( selindex >= 0 && selindex < size) {
                if ( root.listPaneModel.removeItemIndex(selindex)) { 
                    root.model.remove( selindex ); 
                    size = root.model.getSize(); 
                    if ( size <= 0 ) {
                        root.updateBeanValueToNull(); 
                    } else if ( selindex >= size ) {
                        root.setSelectedIndex( selindex-1 ); 
                    } else {
                        root.setSelectedIndex( selindex ); 
                    } 
                }
            }
        }
    }
    
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" override methods ">

    protected ListSelectionModel createSelectionModel() { 
        return new ListSelectionModelImpl(); 
    }

    private boolean beforeSelectionIndex(int lead) {
        if (!isEnableNavigation()) return false;
        if (listPaneModel == null) return true;
        
        try {
            Object o = (lead < 0? null: getModel().getElementAt(lead)); 
            if (!listPaneModel.beforeSelect(o)) return false;
        } catch(Throwable t) { 
            MsgBox.err(t); 
        } 
        return true; 
    }
    
    public void setSelectionInterval(int anchor, int lead) {
        if (!beforeSelectionIndex(lead)) return;
        
        super.setSelectionInterval(anchor, lead); 
    } 
    
    private String getProperty(Map map, String name) {
        Object value = (map == null? null: map.get(name));
        return (value == null? null: value.toString()); 
    }
            
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" RefreshTask ">
    
    private Timer timer;
    
    private void executeRefreshTask( long interval ) {
        if ( timer == null ) {
            timer = new Timer(); 
            timer.schedule(new RefreshTask(), interval, interval );
        }
    }
    
    private class RefreshTask extends TimerTask {

        XList root = XList.this; 
        
        public void run() { 
            if ( SwingUtilities.getRoot( root ) != null ) {
                root.refresh(true); 
            } 
        }
    }
    
    // </editor-fold> 
}
