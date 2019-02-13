/*
 * XDataTable.java
 *
 * Created on January 31, 2011, 10:51 AM
 * @author jaycverg
 */
package com.rameses.rcp.control;

import com.rameses.rcp.common.AbstractListDataProvider;
import com.rameses.rcp.common.DataListModel;
import com.rameses.rcp.common.Column;
import com.rameses.rcp.common.EditorListModel;
import com.rameses.rcp.common.ListItem;
import com.rameses.rcp.common.MsgBox;
import com.rameses.rcp.common.PopupMenuOpener;
import com.rameses.rcp.common.PropertyChangeHandler;
import com.rameses.rcp.common.PropertySupport;
import com.rameses.rcp.common.TableModelHandler;
import com.rameses.rcp.control.table.CellRenderers;
import com.rameses.rcp.control.table.DataTableComponent;
import com.rameses.rcp.control.table.DataTableModel;
import com.rameses.rcp.control.table.ListScrollBar;
import com.rameses.rcp.control.table.RowHeaderView;
import com.rameses.rcp.control.table.SelectionHandler;
import com.rameses.rcp.control.table.TableBorders;
import com.rameses.rcp.control.table.TableUtil;
import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.rcp.support.ColorUtil;
import com.rameses.rcp.support.FontSupport;
import com.rameses.rcp.support.MouseEventSupport;
import com.rameses.rcp.ui.*;
import com.rameses.rcp.util.*;
import com.rameses.util.ValueUtil;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;

public class XDataTable extends JPanel implements UIInput, UIComplex, Validatable, FocusListener, ActiveControl, MouseEventSupport.ComponentInfo 
{    
    private DataTableComponentImpl table;
    private ListScrollBar scrollBar;
    private RowHeaderView rowHeaderView;
    private JScrollPane scrollPane;
    
    private PropertyChangeHandlerImpl propertyHandler;    
    private AbstractListDataProvider dataProvider;
    private TableModelHandler tableModelHandler;    
    
    private ActionMessage actionMessage = new ActionMessage();    
    private Binding binding;    
    private Column[] columns; 
    private String[] depends;
    private String items;    
    private String handler;
    private String id;
    private String readonlyWhen;
    private int index;    
    private boolean dynamic;
    private boolean showRowHeader;
    private boolean showColumnHeader; 
    private boolean immediate;
    private boolean editable; 
            
    private ListItem currentItem;    
    private RowChangeNotifier rowChangeNotifier; 
    private ListModelLoader loader;

    private int stretchWidth;
    private int stretchHeight;
    private String visibleWhen; 
    
    private Color borderColor; 
    private String multiSelectFieldName; 
    
    private ControlProperty property = new ControlProperty(); 

    private boolean _user_set_showrowheader;
    
    public XDataTable() { 
        init();        
        
        if (!Beans.isDesignTime()) {
            rowChangeNotifier = new RowChangeNotifier(); 
            loader = new ListModelLoader();
        }
    }
    
    // <editor-fold defaultstate="collapsed" desc=" setup handlers ">
    
    public void addMouseListener(MouseListener l) {
        table.addMouseListener(l);
    }
    
    public void removeMouseListener(MouseListener l) {
        table.removeMouseListener(l);
    }
    
    public void addKeyListener(KeyListener l) {
        table.addKeyListener(l);
    }
    
    public void removeKeyListener(KeyListener l) {
        table.removeKeyListener(l);
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" initialize table ">
    
    private void init() {
        property = new ControlProperty(); 
        propertyHandler = new PropertyChangeHandlerImpl();     
        tableModelHandler = new TableModelHandlerImpl();
        showColumnHeader = true;
        
        table = new DataTableComponentImpl();
        scrollBar = new ListScrollBar();
        borderColor = TableBorders.BORDER_COLOR; 
        
        //--create and decorate scrollpane for the JTable
        scrollPane = new JScrollPane(table); 
        TableUtil.customize(scrollPane, table);
        //--additional customization for the JScrollPane 
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); 
        scrollPane.setViewport(new ViewportImpl(table)); 
                                
        //--attach mouse wheel listener to table
        table.addMouseWheelListener(new MouseWheelListener() {
            public void mouseWheelMoved(MouseWheelEvent e) 
            {
                if (dataProvider.isProcessing()) return;
                if (table.isProcessingRequest()) return;
                
                int rotation = e.getWheelRotation();
                if (rotation == 0) return;
                
                if (rotation < 0)
                    dataProvider.moveBackRecord();
                else 
                    dataProvider.moveNextRecord(true); 
                
                int scrollPolicy = scrollPane.getVerticalScrollBarPolicy();
                try { 
                    int selRow = table.getSelectedRow();
                    int selCol = table.getSelectedColumn(); 
                    Rectangle rect = table.getCellRect(selRow, selCol, true);                    
                    if (scrollPolicy == JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED) {
                        table.scrollRectToVisible(rect);
                        
                    } else { 
                        scrollBar.adjustValues(); 
                        table.scrollRectToVisible(rect);
                    }
                } catch(Throwable ex) {;} 
            }
        });
        
        super.setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(new ScrollBarPanel(scrollBar), BorderLayout.EAST);
        setBorder(new TableBorders.DefaultBorder());        
        
        //--default table properties
        //setGridColor(new Color(217,216,216));
        setCellSpacing(new Dimension(0,0));        
        setGridColor(new Color(235, 240, 244)); 
        //setGridColor( Color.decode("#afafaf") ); 
        setOddBackground(new Color(235, 240, 244)); 
        setEvenBackground(new Color(251, 251, 251)); 
        setShowRowHeaderImpl(true); 
        setShowHorizontalLines(true); 
        setShowVerticalLines(true);        
        setAutoResize(true);
        setRowMargin(0); 
        setRowHeight(21);
        setRowHeaderHeight(getRowHeight()+2); 
        
        if ( table.getEvenBackground() == null ) {
            Color bg = (Color) UIManager.get("Table.evenBackground");
            if ( bg == null ) bg = table.getBackground();
            table.setEvenBackground(bg);
        }
        
        if ( table.getEvenForeground() == null ) {
            Color fg = (Color) UIManager.get("Table.evenForeground");
            if ( fg != null ) table.setEvenForeground(fg);
        }
        
        if ( table.getOddBackground() == null ) {
            Color bg = (Color) UIManager.get("Table.oddBackground");
            if ( bg == null ) bg = new Color(225, 232, 246);
            
            table.setOddBackground(bg);
        }
        
        if ( table.getOddForeground() == null ) {
            Color fg = (Color) UIManager.get("Table.oddForeground");
            if ( fg != null ) table.setOddForeground(fg);
        }
        
        new MouseEventSupport(this).install();         
        
        //--design time display
        if (Beans.isDesignTime()) {
            if (rowHeaderView != null) rowHeaderView.setRowCount(1);
            
            setPreferredSize(new Dimension(200,80));
            table.setDataProvider(new DesignTimeListModel()); 
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ActiveControl implementation ">    
    
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
    
    // <editor-fold defaultstate="collapsed" desc=" UIInput properties ">
    
    public String[] getDepends() { return depends; }    
    public void setDepends(String[] depends) { this.depends = depends; }
    
    public int getIndex() { return index; }    
    public void setIndex(int index) { 
        this.index = index; 
        getControlProperty().setIndex( index ); 
    }

    public Binding getBinding() { return binding; }    
    public void setBinding(Binding binding) { this.binding = binding; }
    
    private boolean refreshed;
    private boolean hasLoaded;
    
    public void refresh() {
        //force to update component status
        if (isEnabled()) setReadonly(isReadonly()); 
        
        applyExpressions();
        
        if ( dataProvider != null ) { 
            boolean empty = (dataProvider.getDataListSize() == 0); 
            if ( !refreshed && !empty ) {
                EventQueue.invokeLater(loader.refresh());  
            } else if ( !refreshed || dynamic ) { 
                EventQueue.invokeLater(loader.load()); 
            } else { 
                EventQueue.invokeLater(loader.refresh());  
            } 
        } 
        refreshed = true;
        
        Object bean = (getBinding() == null? null : getBinding().getBean()); 
        String whenExpr = getVisibleWhen();
        if (whenExpr != null && whenExpr.length() > 0 && bean != null) {
            boolean result = false; 
            try { 
                result = UIControlUtil.evaluateExprBoolean(bean, whenExpr);
            } catch(Throwable t) {
                t.printStackTrace();
            }
            setVisible( result ); 
        }         
    }
    
    public void load() {
        if (hasLoaded) return;
        
        applyExpressions();
        
        refreshed = false;
        AbstractListDataProvider newProvider = null;
        if ( handler != null ) {
            Object oHandler = UIControlUtil.getBeanValue(this, handler);
            if ( oHandler instanceof AbstractListDataProvider ) { 
                newProvider = (AbstractListDataProvider) oHandler;  
            } else {
                System.out.println("[WARN] '"+handler+"' list model is null");
                newProvider = new ReadonlyListModel(null); 
            }
        }
        
        if (newProvider == null && getItems() != null) {
            if (isEditable()) { 
                newProvider = new EditableListModel(getItems()); 
            } else { 
                newProvider = new ReadonlyListModel(getItems()); 
            } 
        } 
        
        if (newProvider != null) {
            if (getColumns() != null) {
                newProvider.setColumns(getColumns()); 
            } 
            
            if ( _user_set_showrowheader ) {
                //do nothing 
            } else if (newProvider instanceof EditorListModel) { 
                setShowRowHeader(true); 
            } else { 
                setShowRowHeader(false);  
            } 
            
            dataProvider = newProvider;
            table.setBinding(binding);
            table.setDataProvider(dataProvider);
            scrollBar.setDataProvider(dataProvider); 

            if (rowHeaderView != null) { 
                table.getModel().removeTableModelListener(rowHeaderView); 
                rowHeaderView.setRowCount( dataProvider.getRows() );
                table.getModel().addTableModelListener(rowHeaderView);
            }
        }
        hasLoaded = true;
    }
        
    public Object getValue() 
    {
        if ( Beans.isDesignTime() ) return null;
        
        if ( dataProvider == null || dataProvider.getSelectedItem() == null ) 
            return null; 
        else 
            return dataProvider.getSelectedItem().getItem(); 
    } 
    
    public void setValue(Object value) {}
    
    public boolean isNullWhenEmpty() { return true; }
    
    public int compareTo(Object o) {
        return UIControlUtil.compare(this, o);
    }   
    
    public Map getInfo() { 
        Map map = new HashMap();
        map.put("dynamic", isDynamic());
        map.put("handler", getHandler());
        map.put("immediate", isImmediate()); 
        map.put("items", getItems());
        map.put("id", getId());
        map.put("varName", getVarName());
        map.put("varStatus", getVarStatus());
        map.put("multiSelectName", getMultiSelectName());
        map.put("readonlyWhen", getReadonlyWhen());
        map.put("required", isRequired()); 
        return map;
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
    
    
    private void applyExpressions() {
        String expr = getReadonlyWhen();
        if (expr != null && expr.length() > 0) { 
            try { 
                boolean b = UIControlUtil.evaluateExprBoolean(getBinding().getBean(), expr); 
                setReadonly(b); 
            } catch(Throwable t) {
                t.printStackTrace();
            }
        } 
    }
     
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" UIComplex implementation ">
    
    public String getId() { return id; }
    public void setId(String id) { 
        this.id = id; 
        table.setId(id);
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="  table listener methods  ">
    
    public void cancelRowEdit() {
        if ( rowHeaderView != null ) { 
            rowHeaderView.clearEditing();
        }
    }
        
    protected void log(String msg) {
        String name = getClass().getSimpleName(); 
        System.out.println("["+name+"] " + msg);
    }
    
    //</editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="  Getters/Setters  ">
    
    public String getVarName() {
        return (table == null? null: table.getVarName()); 
    }
    public void setVarName(String varName) {
        table.setVarName(varName); 
    }
    
    public String getVarStatus() { 
        return (table == null? null: table.getVarStatus());
    }    
    public void setVarStatus(String varStatus) { 
        table.setVarStatus(varStatus);
    } 
        
    public String getMultiSelectName() { 
        return (table == null? null: table.getMultiSelectName()); 
    } 
    public void setMultiSelectName(String multiSelectName) {
        table.setMultiSelectName(multiSelectName);
    } 
    
    public boolean isRequired() { return table.isRequired(); }    
    public void setRequired(boolean required) {}
    
    public void validateInput() { 
        validateInput( actionMessage ); 
    } 
    public void validateInput( ActionMessage am ) {
        if (dataProvider == null) return;

        am.clearMessages();        
        String errmsg = dataProvider.getMessageSupport().getErrorMessages(); 
        if ( errmsg == null ) {
            ListItem li = dataProvider.getSelectedItem(); 
            if ( li == null ) return; 
            
            int state = li.getState(); 
            if ( state==ListItem.STATE_DRAFT || state==ListItem.STATE_EDIT ) {
                errmsg = "There are changes on your data table. Please commit or revert it first.";
                dataProvider.getMessageSupport().addErrorMessage(li.getIndex(), errmsg); 
                am.addMessage(null, errmsg, null);
                dataProvider.refreshSelectedItem(); 
            }
        } else { 
            StringBuffer buffer = new StringBuffer(errmsg);
            String caption = getCaption();
            if ( !ValueUtil.isEmpty(caption) ) {
                buffer.insert(0, caption + " (\n").append("\n)");
            }
            am.addMessage(null, buffer.toString(), null);
        }
    }
    
    public ActionMessage getActionMessage() { return actionMessage; }

    public boolean isReadonly() { return table.isReadonly(); }    
    public void setReadonly(boolean readonly) { table.setReadonly(readonly); }
        
    public void setName(String name) {
        super.setName(name); 
        if ( table != null ) {
            table.setName(name);
        }
    }
    
    public void setLayout(LayoutManager mgr) {;}
    
    public Column[] getColumns() { return columns; }
    public void setColumns(Column[] columns) { 
        this.columns = columns; 
        if (Beans.isDesignTime()) {
            try {
                ReadonlyListModel lm = new ReadonlyListModel(null);
                lm.setColumns(columns);                
                table.setDataProvider(lm);
            } catch(Exception ex) {
                MsgBox.err(ex); 
            }
        }
    }
    
    public String getItems() { return items; } 
    public void setItems(String items) { 
        this.items = items; 
        if (getId() == null) setId(this.items); 
    }
    
    public String getHandler() { return handler; }
    public void setHandler(String handler) { 
        this.handler = handler; 
        if (getId() == null) setId(this.handler); 
    }
    
    public boolean isDynamic() { return dynamic; }
    public void setDynamic(boolean dynamic) { this.dynamic = dynamic; }

    public boolean isShowHorizontalLines() { return table.getShowHorizontalLines(); }    
    public void setShowHorizontalLines(boolean show) { table.setShowHorizontalLines(show); }

    public boolean isShowVerticalLines() { return table.getShowVerticalLines(); }    
    public void setShowVerticalLines(boolean show) { table.setShowVerticalLines(show); }

    public boolean isAutoResize() { return table.isAutoResize(); }    
    public void setAutoResize(boolean autoResize) { table.setAutoResize(autoResize); }
    
    public Dimension getCellSpacing() { return table.getIntercellSpacing(); }
    public void setCellSpacing(Dimension cellSpacing) {
        table.setIntercellSpacing(cellSpacing); 
    }
    
    public void setRequestFocus(boolean focus) {
        if ( focus ) table.requestFocus();
    }
    
    public void requestFocus() { table.requestFocus(); }
    
    public boolean requestFocusInWindow() { return table.requestFocusInWindow(); }
    
    public void focusGained(FocusEvent e) { table.grabFocus(); }
    public void focusLost(FocusEvent e)   {}
    
    public Color getEvenBackground() { return table.getEvenBackground(); }
    public void setEvenBackground(Color evenBackground) { table.setEvenBackground( evenBackground ); }
    
    public Color getOddBackground() { return table.getOddBackground(); }
    public void setOddBackground(Color oddBackground) { table.setOddBackground( oddBackground ); }
    
    public Color getErrorBackground() { return table.getErrorBackground(); }
    public void setErrorBackground(Color errorBackground) { table.setErrorBackground( errorBackground ); }
    
    public Color getEvenForeground() { return table.getEvenForeground(); }
    public void setEvenForeground(Color evenForeground) { table.setEvenForeground( evenForeground ); }
    
    public Color getOddForeground() { return table.getOddForeground(); }
    public void setOddForeground(Color oddForeground) { table.setOddForeground( oddForeground ); }
    
    public Color getErrorForeground() { return table.getErrorForeground(); }
    public void setErrorForeground(Color errorForeground) { table.setErrorForeground( errorForeground ); }
    
    public boolean isImmediate() { return immediate; }
    public void setImmediate(boolean immediate) { this.immediate = immediate; }
    
    public boolean isShowColumnHeader() { return showColumnHeader; }
    public void setShowColumnHeader( boolean showColumnHeader ) {
        if ( table == null ) return; 
        
        this.showColumnHeader = showColumnHeader; 
        if ( this.showColumnHeader ) { 
            table.attachTableHeader(); 
        } else {
            table.setTableHeader(null); 
        } 
    }
    
    public boolean isShowRowHeader() { return showRowHeader; }
    public void setShowRowHeader(boolean showRowHeader) {
        setShowRowHeaderImpl( showRowHeader ); 
        _user_set_showrowheader = true; 
    }
    
    private void setShowRowHeaderImpl(boolean show) {
        this.showRowHeader = show; 
        if ( show ) {
            JLabel corner = new CellRenderers.HeaderRenderer(true);
            corner.putClientProperty("Component.proxy", table); 
            scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, corner); 
            scrollPane.setRowHeaderView( (rowHeaderView = new RowHeaderView(table)) );
            rowHeaderView.setRowCount( table.getRowCount() );
        } else {
            scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, null );
            scrollPane.setRowHeaderView( (rowHeaderView = null) );
        }        
    }
    
    public int getColumnMargin() { return table.getColumnModel().getColumnMargin(); }
    public void setColumnMargin(int margin) { table.getColumnModel().setColumnMargin(margin); }
    
    public int getRowMargin() { return table.getRowMargin(); }
    public void setRowMargin(int margin) { table.setRowMargin(margin); }
    
    public Color getGridColor() { return table.getGridColor(); }
    public void setGridColor(Color color) { table.setGridColor(color); }
    
    public boolean isEnabled() { return table.isEnabled(); }
    public void setEnabled(boolean e) 
    { 
        table.setEnabled(e); 
        scrollBar.setEnabled(e);
        scrollPane.setEnabled(e);
    }
    
    public int getRowHeight() { return table.getRowHeight(); }
    public void setRowHeight(int h) { table.setRowHeight(h); }

    public int getRowHeaderHeight() { return table.getRowHeaderHeight(); }
    public void setRowHeaderHeight(int h) { table.setRowHeaderHeight(h); }
    
    public boolean isScrollbarAlwaysVisible() {
        return scrollBar.isVisibleAlways();
    }
    public void setScrollbarAlwaysVisible(boolean scrollbarAlwaysVisible) {
        scrollBar.setVisibleAlways(scrollbarAlwaysVisible);
    }

    public void setPropertyInfo(PropertySupport.PropertyInfo info) {
    }
    
    public boolean isEditable() { return editable; } 
    public void setEditable(boolean editable) { this.editable = editable; }
    
    public String getReadonlyWhen() { return readonlyWhen; } 
    public void setReadonlyWhen(String readonlyWhen) {
        this.readonlyWhen = readonlyWhen; 
    }

    public Color getBorderColor() { return borderColor; } 
    public void setBorderColor(Color borderColor) { 
        this.borderColor = borderColor; 
        putClientProperty("Border.color", borderColor); 
        if ( table != null ) {
            table.putClientProperty("Border.color", borderColor); 
        } 
        repaint();
    }
    
    public String getMultiSelectFieldName() {
        return multiSelectFieldName; 
    }   
    public void setMultiSelectFieldName( String multiSelectFieldName ) {
        this.multiSelectFieldName = multiSelectFieldName; 
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" font support ">
    
    private FontSupport fontSupport;    
    private Font sourceFont;    
    private String fontStyle; 
    
    private FontSupport getFontSupport() {
        if (fontSupport == null) 
            fontSupport = new FontSupport();
        
        return fontSupport; 
    }
    
    public void setFont(Font font) { 
        sourceFont = font; 
        if (sourceFont != null) {
            Map attrs = getFontSupport().createFontAttributes(getFontStyle()); 
            sourceFont = getFontSupport().applyStyles(sourceFont, attrs);
        }
        
        super.setFont(sourceFont); 
        if (table != null) table.setFont(font); 
    } 
    
    public String getFontStyle() { return fontStyle; } 
    public void setFontStyle(String fontStyle) {
        this.fontStyle = fontStyle;
        
        if (sourceFont == null) sourceFont = super.getFont(); 
        
        Font font = sourceFont;
        if (font == null) return;
        
        Map attrs = getFontSupport().createFontAttributes(getFontStyle()); 
        font = getFontSupport().applyStyles(font, attrs); 
        
        super.setFont(font); 
        if (table != null) table.setFont(font); 
    } 
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="  ScrollBarPanel (class)  ">
    
    private class ScrollBarPanel extends JPanel 
    {
        ScrollBarPanel(JScrollBar scrollBar) 
        {
            Dimension ps = scrollBar.getPreferredSize();
            setPreferredSize(ps);
            setLayout(new BorderLayout());
            
            scrollBar.addPropertyChangeListener(new PropertyChangeListener() 
            {
                public void propertyChange(PropertyChangeEvent evt) 
                {
                    String propName = evt.getPropertyName();
                    if ( "visible".equals(propName) ) 
                    {
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
        
    // <editor-fold defaultstate="collapsed" desc="  ReadonlyListModel (class)  ">
    
    private class ReadonlyListModel extends DataListModel {
        
        XDataTable root = XDataTable.this;
        
        private java.util.List userDefinedList;
        private String name;
        
        ReadonlyListModel(String name) { this.name = name; } 

        public java.util.List fetchList(Map params) {
            if (Beans.isDesignTime()) { return null; }
            
            if ((userDefinedList == null && name != null) || root.isDynamic()) {
                userDefinedList = (java.util.List) UIControlUtil.getBeanValue(binding, name); 
            }
            
            if (userDefinedList == null) { 
                return new ArrayList(); 
            } else { 
                return userDefinedList; 
            } 
        }        
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="  EditableListModel (class)  ">
    
    private class EditableListModel extends EditorListModel { 
        
        XDataTable root = XDataTable.this;
        
        private java.util.List userDefinedList;
        private String name;
        
        EditableListModel(String name) { this.name = name; } 

        public java.util.List fetchList(Map params) {
            if (Beans.isDesignTime()) { return null; }
            
            if ((userDefinedList == null && name != null) || root.isDynamic()) { 
                userDefinedList = (java.util.List) UIControlUtil.getBeanValue(binding, name); 
            } 
            
            if (userDefinedList == null) { 
                return new ArrayList();
            } else { 
                return userDefinedList; 
            } 
        } 
    }
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" DesignTimeListModel (class) ">
    
    private class DesignTimeListModel extends DataListModel 
    {
        public Column[] getColumns() {
            return new Column[]{
                new Column(null, "Column 1"),
                new Column(null, "Column 2")
            };
        }
        
        public java.util.List fetchList(Map params) { return null; } 
    }
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc="  RowChangeNotifier (Class)  ">

    private class RowChangeNotifier 
    {
        void execute() 
        {
            int rowIndex = 0;
            if (dataProvider.getSelectedItem() != null) 
                rowIndex = dataProvider.getSelectedItem().getIndex();
            
            updateBean(rowIndex); 
            notifyDepends(rowIndex); 
        }
        
        void updateBean(int rowIndex) {
            try {
                SelectionHandler selhandler = getSelectionHandler(); 
                
                String name = getName();                 
                ListItem oListItem = dataProvider.getListItem(rowIndex);
                if ( !ValueUtil.isEmpty(name)) {
                    Object value = (oListItem == null? null: oListItem.getItem()); 
                    UIControlUtil.setBeanValue(binding, name, value);
                    
                    if ( selhandler != null ) {
                        selhandler.setBeanValue( name, value );
                    } 
                }
                
                String statName = getVarStatus();
                if ( !ValueUtil.isEmpty(statName)) {
                    Object statValue = dataProvider.createListItemStatus(oListItem); 
                    UIControlUtil.setBeanValue(binding, statName, statValue);
                    
                    if ( selhandler != null ) {
                        selhandler.setStatusValue( statName, statValue );
                    }                     
                } 
            } catch (Throwable ex) {
                if (ClientContext.getCurrentContext().isDebugMode()) 
                    ex.printStackTrace(); 
            }
        }
        
        void notifyDepends(final int rowIndex) {
            String sname = getName();
            if ( !ValueUtil.isEmpty( sname)) {
                if (immediate) {
                    binding.notifyDepends(XDataTable.this); 

                    SelectionHandler selhandler = getSelectionHandler(); 
                    if ( selhandler != null ) {
                        selhandler.notifyDepends(sname); 
                    }
                    
                } else {
                    Thread thread = new Thread(new Runnable() {
                        public void run() { 
                            notifyDependsAsync(rowIndex);
                        }
                    });
                    thread.start(); 
                }
            } 
        }
        
        void notifyDependsCheckedItems(final String name) {
            if (!dataProvider.isMultiSelect()) return;
            
            if (immediate) { 
                binding.notifyDepends(XDataTable.this, name); 
                
                SelectionHandler selhandler = getSelectionHandler(); 
                if ( selhandler != null ) {
                    selhandler.notifyDepends( name ); 
                }
                
            } else { 
                Thread thread = new Thread(new Runnable() {
                    public void run() { 
                        notifyDependsCheckedItemsAsync(name); 
                    } 
                }); 
                thread.start(); 
            } 
        } 
        
        private synchronized void notifyDependsAsync(int selectedRow) 
        {
            try { Thread.sleep(200); } catch(Throwable ex) {;}             
            
            try {
                if (selectedRow == table.getSelectedRow()) { 
                    binding.notifyDepends(XDataTable.this); 
                } 
            } catch(Throwable ex) {;} 
        }       
        
        private synchronized void notifyDependsCheckedItemsAsync(String name) 
        {
            try {
                binding.notifyDepends(XDataTable.this, name); 
                
                SelectionHandler selhandler = getSelectionHandler(); 
                if ( selhandler != null ) {
                    selhandler.notifyDepends( name ); 
                }
            } catch(Throwable ex) {;} 
        } 
    }
           
    // </editor-fold>     
    
    // <editor-fold defaultstate="collapsed" desc="  ListModelLoader (Class)  ">

    private class ListModelLoader implements Runnable 
    {
        private Object LOCK = new Object();        
        private boolean refreshOnly;
        private boolean processing;
                
        Runnable load() {
            refreshOnly = false; 
            return this; 
        }
        
        Runnable refresh() {
            refreshOnly = true; 
            return this; 
        } 
        
        public void run() {
            synchronized(LOCK) {
                try {
                    if (refreshOnly) { 
                        dataProvider.refresh();
                    } else {
                        dataProvider.load();
                        table.onrowChanged(); 
                    }
                } catch(Exception ex) {
                    showError(ex); 
                }
            }
        } 
        
        void showError(final Exception error) {
            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    MsgBox.err(error); 
                }
            });
        }
    }
           
    // </editor-fold>     
    
    // <editor-fold defaultstate="collapsed" desc="  DataTableComponentImpl (Class)  ">
    
    private SelectionHandler getSelectionHandler() {
        Object o = getClientProperty( SelectionHandler.class ); 
        if ( o instanceof SelectionHandler ) {
            return (SelectionHandler) o; 
        }
        return null; 
    }
    
    private class DataTableComponentImpl extends DataTableComponent {
        
        XDataTable root = XDataTable.this;
        
        PropertyChangeListener propertyHandler = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
                onPropertyChange(e);
            }
        };
        
        protected void onPropertyChange(PropertyChangeEvent e) {
            String propName = e.getPropertyName();
            if ("checkedItemsChanged".equals(propName)) {
                if (!dataProvider.isMultiSelect()) return;

                String multiName = getMultiSelectName();
                if (multiName == null) { 
                    multiName = table.getDataTableModel().DEFAULT_MULTI_SELECT_NAME; 
                } 
                rowChangeNotifier.notifyDependsCheckedItems(multiName); 
            }
        }

        protected void uninstall(AbstractListDataProvider dataProvider) {
            dataProvider.removeHandler(root.propertyHandler);
            dataProvider.removeHandler(root.tableModelHandler); 
        }
        
        protected void install(AbstractListDataProvider dataProvider) {
            dataProvider.addHandler(root.propertyHandler);
            dataProvider.addHandler(root.tableModelHandler);             
        }

        protected void onTableModelChanged(DataTableModel tableModel) { 
            tableModel.addHandler(propertyHandler); 
        } 
        
        protected void onrowChanged() { 
            if (dataProvider == null) return;
            
            ListItem selectedItem = dataProvider.getSelectedItem();

            Object oldValue = (currentItem == null? null: currentItem.getItem());
            Object newValue = (selectedItem == null? null: selectedItem.getItem());            
            
            currentItem = null; 
            if (selectedItem != null) { 
                currentItem = selectedItem.clone(); 
            } 
            if (oldValue != newValue && rowChangeNotifier != null) { 
                rowChangeNotifier.execute(); 
            } 
            if (rowHeaderView != null) { 
                rowHeaderView.clearEditing();
            } 
            try { 
                root.scrollBar.adjustValues(); 
            } catch(Exception ex) {;} 
        } 
        
        protected void onopenItem() { 
            try {
                ListItem selectedItem = dataProvider.getSelectedItem();
                if (selectedItem != null && selectedItem.getItem() != null) {
                    Object outcome = dataProvider.openSelectedItem();
                    if (outcome == null) return; 

                    if (outcome instanceof PopupMenuOpener) {
                        outcome = ((PopupMenuOpener) outcome).getFirst();
                        if (outcome == null) return; 
                    }
                    
                    binding.fireNavigation(outcome); 
                }
            } catch(Exception ex) {
                MsgBox.err(ex); 
            } 
        } 

        protected void onchangedItem(ListItem li) {
            currentItem = null; 
            if ( li != null ) { 
                currentItem = li.clone(); 
            } 
            if ( rowChangeNotifier != null ) { 
                rowChangeNotifier.execute(); 
            } 
        }

        protected void oneditCellAt(int rowIndex, int colIndex) { 
            if (root.rowHeaderView != null) { 
                root.rowHeaderView.editRow(rowIndex); 
            } 
        } 

        public boolean hasRowHeader() { 
            return root.isShowRowHeader(); 
        } 
    } 
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc="  PropertyChangeHandlerImpl (class)  ">    
    
    private class PropertyChangeHandlerImpl implements PropertyChangeHandler 
    {
        XDataTable root = XDataTable.this; 
        
        public void firePropertyChange(String name, int value) {
        }

        public void firePropertyChange(String name, boolean value) 
        {
            if ("loading".equals(name)) {
                root.scrollBar.setEnabled(!value); 
            }             
        }

        public void firePropertyChange(String name, String value) {
        }

        public void firePropertyChange(String name, Object value) 
        {
            if ("selectedItemChanged".equals(name)) 
                root.table.onrowChanged();
        }   
    }
    
    // </editor-fold>     
    
    // <editor-fold defaultstate="collapsed" desc="  TableModelHandlerImpl (class)  ">    
    
    private class TableModelHandlerImpl implements TableModelHandler 
    {
        XDataTable root = XDataTable.this; 

        public void fireTableDataChanged() { 
            root.scrollBar.adjustValues(); 
        }
        
        public void fireTableDataProviderChanged() { 
            root.table.setDataProvider(root.dataProvider); 
        }
        
        public void fireTableStructureChanged() {}

        public void fireTableCellUpdated(int row, int column) {}        
        public void fireTableRowsDeleted(int firstRow, int lastRow) {}
        public void fireTableRowsInserted(int firstRow, int lastRow) {}
        public void fireTableRowsUpdated(int firstRow, int lastRow) {}
        public void fireTableRowSelected(int row, boolean focusOnItemDataOnly) {}
    }
    
    // </editor-fold>         
    
    // <editor-fold defaultstate="collapsed" desc="  ViewPortImpl (class)  ">    
    
    private class ViewportImpl extends JViewport {
        
        XDataTable root = XDataTable.this;      
        
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
                
                public void componentResized(ComponentEvent e) {
                    Rectangle rect = getBounds(); 
                    if (rect.height == oldBounds.height) return;
                    
                    oldBounds = rect;
                    boolean dynamic = root.scrollBar.isDynamicallyVisible();
                    if (rect.height < root.table.getBounds().height && !dynamic) {
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
        
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            Insets margin = getInsets();             
            if ( margin == null ) margin = new Insets(0,0,0,0);
            
            int pw = getWidth(); 
            int ph = getHeight(); 
            int x = margin.left;
            int y = margin.top;
            int w = pw - (margin.left + margin.right);
            int h = ph - (margin.top + margin.bottom);
            int maxy = ph - margin.bottom; 
            int rowh = root.table.getRowHeight();

            Graphics2D g2 = (Graphics2D) g.create(); 
            int rownum = 0;
            while ( y < maxy ) {
                int test = rownum % 2; 
                if ( test == 0 ) {
                    g2.setColor( root.getOddBackground()); 
                } else {
                    g2.setColor( root.getEvenBackground()); 
                } 
                g2.fillRect(x, y, w, rowh); 
                rownum += 1; 
                y += rowh; 
            }
            g2.dispose(); 
        }        
    }
    
    // </editor-fold>             
}
