/*
 * CellRenderers.java
 *
 * Created on June 13, 2013, 1:16 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control.table;

import com.rameses.common.ExpressionResolver;
import com.rameses.rcp.common.AbstractListDataProvider;
import com.rameses.rcp.common.ButtonColumnHandler;
import com.rameses.rcp.common.CheckBoxColumnHandler;
import com.rameses.rcp.common.Column;
import com.rameses.rcp.common.ComboBoxColumnHandler;
import com.rameses.rcp.common.DateColumnHandler;
import com.rameses.rcp.common.DecimalColumnHandler;
import com.rameses.rcp.common.EditorListSupport;
import com.rameses.rcp.common.IconColumnHandler;
import com.rameses.rcp.common.IntegerColumnHandler;
import com.rameses.rcp.common.LabelColumnHandler;
import com.rameses.rcp.common.ListItem;
import com.rameses.rcp.common.LookupColumnHandler;
import com.rameses.rcp.common.MsgBox;
import com.rameses.rcp.common.OpenerColumnHandler;
import com.rameses.rcp.common.StyleRule;
import com.rameses.rcp.constant.TextCase;
import com.rameses.rcp.support.ComponentSupport;
import com.rameses.rcp.support.FontSupport;
import com.rameses.rcp.support.ImageIconSupport;
import com.rameses.rcp.util.ControlSupport;
import com.rameses.rcp.util.UIControlUtil;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.math.BigDecimal;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

/**
 *
 * @author wflores
 */
public class CellRenderers {
    private static Map<String,Class> renderers;
    
    static {
        renderers = new HashMap();
        renderers.put("text", TextRenderer.class);
        renderers.put("string", TextRenderer.class);
        renderers.put("boolean", CheckBoxRenderer.class);
        renderers.put("checkbox", CheckBoxRenderer.class);
        renderers.put("combo", ComboBoxRenderer.class);
        renderers.put("combobox", ComboBoxRenderer.class);
        renderers.put("date", DateRenderer.class);
        renderers.put("decimal", DecimalRenderer.class);
        renderers.put("double", DecimalRenderer.class);
        renderers.put("integer", IntegerRenderer.class);
        renderers.put("label", LabelRenderer.class);
        renderers.put("lookup", LookupRenderer.class);
        renderers.put("opener", OpenerRenderer.class);
        renderers.put("button", ButtonRenderer.class);
        renderers.put("icon", IconRenderer.class);
    }
    
    public static AbstractRenderer getRendererFor(Column oColumn) {
        Column.TypeHandler handler = oColumn.getTypeHandler();
        if (handler == null) { 
            handler = ColumnHandlerUtil.newInstance().createTypeHandler(oColumn);
        } 
        return null;
    }
    
    public static String getPreferredAlignment(Column oColumn) {
        if (oColumn == null) { return null; } 
        
        String alignment = oColumn.getAlignment();
        if (alignment != null) { return alignment; } 
            
        Column.TypeHandler handler = oColumn.getTypeHandler();
        if (handler instanceof CheckBoxColumnHandler) { 
            oColumn.setAlignment("center"); 
        } else if (handler instanceof DecimalColumnHandler) { 
            oColumn.setAlignment("right"); 
        } else if (handler instanceof IntegerColumnHandler) { 
            oColumn.setAlignment("center");
        } else { 
            oColumn.setAlignment("left");
        } 
        return oColumn.getAlignment();
    }
    
    
    // <editor-fold defaultstate="collapsed" desc="  Context (class)  ">
    
    public static class Context {
        private JTable table;
        private Object value;
        private int rowIndex;
        private int columnIndex;
        
        private TableControl tableControl;
        private TableControlModel tableControlModel;
        
        Context(JTable table, Object value, int rowIndex, int columnIndex) {
            this.table = table;
            this.value = value;
            this.rowIndex = rowIndex;
            this.columnIndex = columnIndex;
            
            this.tableControl = (TableControl) table;
            this.tableControlModel = (TableControlModel) this.tableControl.getModel();
        }
        
        public JTable getTable() { return table; }
        public Object getValue() { return value; }
        public int getRowIndex() { return rowIndex; }
        public int getColumnIndex() { return columnIndex; }
        
        public TableControl getTableControl() { return tableControl; }
        public TableControlModel getTableControlModel() { return tableControlModel; }
        
        public AbstractListDataProvider getDataProvider() {
            return tableControl.getDataProvider();
        }
        
        public Object getItemData() {
            return getItemData(this.rowIndex);
        }
        
        public Object getItemData(int rowIndex) {
            return getDataProvider().getListItemData(rowIndex);
        }
        
        public Column getColumn() {
            return getColumn(this.columnIndex);
        }
        
        public Column getColumn(int index) {
            return getTableControlModel().getColumn(index);
        }
        
        public Object createExpressionBean() {
            return createExpressionBean(getItemData());
        }
        
        public Object createExpressionBean(Object bean) {
            return getTableControl().createExpressionBean(bean);
        }
        
        public boolean setValueAt( int rowIndex, int colIndex, Object value ) {
            try { 
                getTable().getModel().setValueAt(value, rowIndex, colIndex); 
                return true; 
            } catch(EditorListSupport.BeforeColumnUpdateException bcx) {
                if (bcx.getCause() != null) MsgBox.err(bcx.getCause());
                return false; 
            } catch(EditorListSupport.AfterColumnUpdateException acx) { 
                if (acx.getCause() != null) MsgBox.err(acx.getCause());  
                return true; 
            } catch(Exception ex) { 
                MsgBox.err(ex); 
                return false; 
            } 
        }
        
        public Object fetchValueAt( int rowIndex, int colIndex) { 
            Object itemData = getItemData(rowIndex); 
            if ( itemData == null ) { return null; }
            
            String name = getColumn( colIndex ).getName(); 
            return UIControlUtil.getBeanValue( itemData, name ); 
        } 
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="  HeaderRenderer (class)  ">
    
    public static class HeaderRenderer extends JLabel implements TableCellRenderer {
        
        private ComponentSupport componentSupport; 
        private boolean hideAll;
        
        public HeaderRenderer() { 
            this( false ); 
        } 
        
        public HeaderRenderer( boolean hideAll ) { 
            this.hideAll = hideAll;
        }
        
        private ComponentSupport getComponentSupport() {
            if (componentSupport == null) { 
                componentSupport = new ComponentSupport();
            } 
            return componentSupport; 
        }
        
        private void init( Column col ) { 
            if ( col == null ) return; 

            String alignment = getPreferredAlignment( col ); 
            getComponentSupport().alignText(this, alignment); 
        } 
        
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int rowIndex, int colIndex) { 
            if ( table == null ) return this;
            
            setFont(table.getFont()); 
            setText(value+""); 
            
            putClientProperty("Component.proxy", table); 
            
            TableModel tm = table.getModel(); 
            if ( tm instanceof DataTableModel ) { 
                DataTableModel dtm = (DataTableModel) tm; 
                init ( dtm.getColumn( colIndex )); 
            } else if ( tm instanceof DataTableModelDesignTime ) { 
                DataTableModelDesignTime dtm = (DataTableModelDesignTime) tm; 
                init ( dtm.getColumn( colIndex )); 
            } 
            
            HeaderBorder border = new HeaderBorder();
            if ( hideAll ) {
                border.showLeft = border.showRight = false; 
                return this; 
            }
            
            boolean hasRowHeader = false; 
            if ( table instanceof DataTableComponent ) {
                hasRowHeader = ((DataTableComponent) table).hasRowHeader(); 
            }
            border.showLeft = true; 
            if ( colIndex == 0 && !hasRowHeader ) {
                border.showLeft = false; 
            }
            
            boolean autoresizeoff = (table.getAutoResizeMode() == JTable.AUTO_RESIZE_OFF);
            if ( autoresizeoff ) { 
                border.showRight = ((colIndex+1) == tm.getColumnCount());
            } else { 
                border.showRight = false; 
            } 
            setBorder( border ); 
            return this;
        }      
                
        
        protected Color getHighlightColor() {
            return getBackground().brighter();
        }

        protected Color getShadowColor() {
            return getBackground().darker();
        }  
        
        
        // The following methods override the defaults for performance reasons
        public void validate() {}
        public void revalidate() {}
        protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {}
        public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {}            
    } 
    
    public static class HeaderBorder extends AbstractBorder {

        private final Color BORDER_COLOR = new Color(204, 204, 204);
        
        public boolean showLeft = true;
        public boolean showRight = false;
                
        public Insets getBorderInsets(Component c) {
            return getBorderInsets(c, new Insets(0,0,0,0)); 
        }

        public Insets getBorderInsets(Component c, Insets pad) { 
            if ( pad == null ) pad = new Insets(0,0,0,0); 
            
            Insets newpad = new Insets(0,0,0,0); 
            newpad.top = newpad.bottom = 5;
            newpad.left = newpad.right = 8;
            return newpad;
        }
        
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Color oldColor = g.getColor(); 
            g.setColor( BORDER_COLOR );
            if ( showLeft ) {
                g.drawLine(0, 5, 0, h-6 ); 
            }
            if ( showRight ) {
                g.drawLine(w-1, 5, w-1, h-6 ); 
            } 
            // draw bottom line 
            g.drawLine(0, h-1, w, h-1);
            g.setColor( oldColor );
        } 
    } 
    
    //private static class HeaderBorder extends 
    
    // </editor-fold>    
      
    // <editor-fold defaultstate="collapsed" desc="  AbstractRenderer (class)  ">
    
    public static abstract class AbstractRenderer implements TableCellRenderer {
        private Insets CELL_MARGIN = TableUtil.CELL_MARGIN;
        private Color FOCUS_BG = TableUtil.FOCUS_BG;
        private ComponentSupport componentSupport;
        private CellRenderers.Context ctx;
        
        protected ComponentSupport getComponentSupport() {
            if (componentSupport == null)
                componentSupport = new ComponentSupport();
            
            return componentSupport;
        }
        
        protected CellRenderers.Context getContext() { return ctx; }
        protected TableControl getTableControl() { return ctx.getTableControl(); }
        protected TableControlModel getTableControlModel() { return ctx.getTableControlModel(); }
        
        public abstract JComponent getComponent(JTable table, int rowIndex, int columnIndex);
        
        public abstract void refresh(JTable table, Object value, boolean selected, boolean focus, int rowIndex, int columnIndex);
        
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int rowIndex, int columnIndex) {
            ctx = new CellRenderers.Context(table, value, rowIndex, columnIndex);
            TableControl tc = ctx.getTableControl();
            TableControlModel tcm = ctx.getTableControlModel();
            
            JComponent comp = getComponent(table, rowIndex, columnIndex);
            getComponentSupport().setEmptyBorder(comp, CELL_MARGIN);
            comp.setFont(table.getFont());

            comp.setForeground(table.getForeground());
            comp.setOpaque(false);

            if ((rowIndex+1)%2 == 0) {
                if (tc.getEvenBackground() != null) {
                    comp.setBackground(tc.getEvenBackground());
                    comp.setOpaque(true);
                }
                if (tc.getEvenForeground() != null)
                    comp.setForeground(tc.getEvenForeground());
            } 
            else {
                if (tc.getOddBackground() != null) {
                    comp.setBackground(tc.getOddBackground());
                    comp.setOpaque(true);
                }                    
                if (tc.getOddForeground() != null)
                    comp.setForeground(tc.getOddForeground());
            }
            
            if (isSelected) {
                decorateSelected( table, comp, isSelected, hasFocus ); 
            } 
            
            try {
                applyStyles(comp, hasFocus);
            } catch(Throwable ex) {;}
            
            AbstractListDataProvider ldp = ctx.getDataProvider();
            String errmsg = ldp.getMessageSupport().getErrorMessage(rowIndex);
            if (errmsg != null) {
                if (!hasFocus) {
                    comp.setBackground( tc.getErrorBackground() );
                    comp.setForeground( tc.getErrorForeground() );
                    comp.setOpaque(true);
                }
            }
            
//            if ( !table.isEnabled() ) {
//                Color c = comp.getBackground();
//                comp.setBackground(ColorUtil.brighter(c, 5));
//                
//                c = comp.getForeground();
//                comp.setForeground(ColorUtil.brighter(c, 5));
//            }
            
            //border support
            TableBorders.CellBorder cellborder = new TableBorders.CellBorder(table, rowIndex, columnIndex); 
            Border inner = getComponentSupport().createEmptyBorder(CELL_MARGIN);
            Border border = BorderFactory.createEmptyBorder(1,1,1,1);
            if (hasFocus) {
                if (isSelected) { 
                    border = UIManager.getBorder("Table.focusSelectedCellHighlightBorder"); 
                } 
                if (border == null) {
                    border = UIManager.getBorder("Table.focusCellHighlightBorder"); 
                } 
            }
            comp.setBorder(BorderFactory.createCompoundBorder(border, inner));
            
            refresh(table, value, isSelected, hasFocus, rowIndex, columnIndex);
            return comp;
        }
        
        protected void decorateSelected(JTable table, JComponent comp, boolean selected, boolean hasFocus) {
            if ( selected ) {
                comp.setBackground(table.getSelectionBackground());
                comp.setForeground(table.getSelectionForeground());
                comp.setOpaque( true );
            }
            if ( hasFocus ) { 
                comp.setBackground(FOCUS_BG); 
                comp.setForeground(table.getForeground()); 
                comp.setOpaque( true );
            }
        } 
              
        private void applyStyles(JComponent comp, boolean hasFocus) {
            TableControl tc = getContext().getTableControl();
            if (tc.getVarName() == null || tc.getVarName().length() == 0) return;
            if (tc.getId() == null || tc.getId().length() == 0) return;
            
            StyleRule[] styles = tc.getBinding().getStyleRules();
            if (styles == null || styles.length == 0) return;
            
            String colName = getContext().getColumn().getName();
            String sname = tc.getId()+":"+tc.getVarName()+"."+colName;
            ExpressionResolver res = ExpressionResolver.getInstance();
            FontSupport fontSupport = new FontSupport();
            
            //apply style rules
            for (StyleRule r : styles) {
                String pattern = r.getPattern();
                String expr = r.getExpression();
                if (expr != null && sname.matches(pattern)){
                    try {
                        Object exprBean = getContext().createExpressionBean();
                        boolean matched = res.evalBoolean(expr, exprBean); 
                        if (!matched) continue;
                        
                        if (hasFocus) { 
                            fontSupport.applyStyles(comp, r.getProperties()); 
                        } else { 
                            r.getProperties().remove("enabled");
                            ControlSupport.setStyles(r.getProperties(), comp);
                        }
                    } catch (Throwable ign){;}
                }
            }
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="  AbstractNumberRenderer (class)  ">
    
    public abstract static class AbstractNumberRenderer extends AbstractRenderer {
        private JLabel label;
        
        public AbstractNumberRenderer() {
            label = new JLabel();
            label.setHorizontalAlignment(SwingConstants.RIGHT);
        }
        
        public JComponent getComponent(JTable table, int rowIndex, int columnIndex) {
            return label;
        }
        
        protected abstract String getFormattedValue(Column c, Object value);
        
        protected String resolveAlignment(String alignment) {
            return alignment;
        }
        
        public void refresh(JTable table, Object value, boolean selected, boolean focus, int rowIndex, int columnIndex) {
            Column c = getContext().getColumn();
            String result = getFormattedValue(c, value);
            label.setText((result == null ? "" : result));
            
            String alignment = c.getAlignment();
            if (alignment != null)
                getComponentSupport().alignText(label, alignment);
        }
        
        protected String formatValue(Number value, String format, String defaultFormat) {
            if (value == null) return null;
            if ("".equals(format)) return value.toString();
            
            DecimalFormat formatter = null;
            if ( format != null)
                formatter = new DecimalFormat(format);
            else
                formatter = new DecimalFormat(defaultFormat);
            
            return formatter.format(value);
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="  TextRenderer (class)  ">
    
    public static class TextRenderer extends AbstractRenderer {
        
        private LabelControl label;
        
        public TextRenderer() {
            label = createComponent();
            label.setVerticalAlignment(SwingConstants.CENTER);
            initComponent(label); 
        }
        
        protected void initComponent(LabelControl label) {}
        
        private LabelControl createComponent() {
            label = new LabelControl();
            return label;
        }
        
        public JComponent getComponent(JTable table, int rowIndex, int columnIndex) {
            return label;
        }
        
        protected Object resolveValue(CellRenderers.Context ctx) {
            return ctx.getValue();
        }
        
        public void refresh(JTable table, Object value, boolean selected, boolean focus, int rowIndex, int columnIndex) {
            Object columnValue = resolveValue(getContext());
            Column oColumn = getContext().getColumn();
            
            TextCase oTextCase = (oColumn == null? null: oColumn.getTextCase());
            if (oTextCase != null && columnValue != null)
                label.setText(oTextCase.convert(columnValue.toString())); 
            
            label.setHorizontalAlignment( SwingConstants.LEFT );
            //set alignment if it is specified in the Column model
            if ( oColumn != null && oColumn.getAlignment() != null )
                getComponentSupport().alignText(label, oColumn.getAlignment());

            try { 
                ListItem li = getContext().getDataProvider().getListItem(rowIndex); 
                decorate(label, oColumn, li); 
            } catch(Throwable t) {;} 
            
            setValue(label, oColumn, columnValue);
        }
        
        protected void decorate(LabelControl label, Column oColumn, ListItem li) {
            //to be implemented
        }
        
        protected void setValue(JLabel label, Column oColumn, Object value) {
            if ( value != null && oColumn != null && oColumn.isHtmlDisplay() )
                value = "<html>" + value + "</html>";
                        
            String str = (value == null ? "" : value.toString());
            TextCase oTextCase = (oColumn == null? null: oColumn.getTextCase());
            if (oTextCase != null) { 
                label.setText(oTextCase.convert(str));
            } else {
                label.setText(str);
            }
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="  CheckBoxRenderer (class)  ">
    
    public static class CheckBoxRenderer extends AbstractRenderer {
        private JCheckBox component;
        private JLabel empty;
        
        public CheckBoxRenderer() {
            component = new JCheckBox();
            component.setHorizontalAlignment(SwingConstants.CENTER);
            component.setBorderPainted(true);
            
            //empty renderer when row object is null
            empty = new JLabel("");
        }
        
        public JComponent getComponent(JTable table, int rowIndex, int colIndex) {
            return (getContext().getItemData() == null? empty: component);
        }
        
        public void refresh(JTable table, Object value, boolean selected, boolean focus, int rowIndex, int columnIndex) {
            Object itemData = getContext().getItemData();
            if (itemData == null) return;
            
            Column oColumn = getContext().getColumn();
            component.setSelected(resolveValue(oColumn, value));
        }
        
        private boolean resolveValue(Column oColumn, Object value) {
            Object checkValue = null;
            if (oColumn.getTypeHandler() instanceof CheckBoxColumnHandler) { 
                checkValue = ((CheckBoxColumnHandler) oColumn.getTypeHandler()).getCheckValue(); 
            } else { 
                checkValue = oColumn.getCheckValue(); 
            } 
            
            boolean selected = false;
            if (value == null) selected = false;
            else if (value != null && checkValue != null && value.equals(checkValue)) selected = true; 
            else if (value.equals(checkValue+"")) selected = true;
            else if ("true".equals(value+"")) selected = true;
            else if ("yes".equals(value+"")) selected = true;
            else if ("t".equals(value+"")) selected = true;
            else if ("y".equals(value+"")) selected = true;
            else if ("1".equals(value+"")) selected = true;
            
            return selected;
        } 
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="  ComboBoxRenderer (class)  ">
    
    public static class ComboBoxRenderer extends TextRenderer {
        protected Object resolveValue(CellRenderers.Context ctx) {
            Column oColumn = ctx.getColumn();
            String expression = oColumn.getExpression();
            if ( expression==null || expression.trim().length()==0 ) { 
                if (oColumn.getTypeHandler() instanceof ComboBoxColumnHandler) { 
                    expression = ((ComboBoxColumnHandler) oColumn.getTypeHandler()).getExpression(); 
                } 
            } 
            
            Object cellValue = ctx.getValue();
            if (expression != null && !(cellValue instanceof String)) {
                try {
                    Object exprBean = ctx.createExpressionBean();
                    cellValue = UIControlUtil.evaluateExpr(exprBean, expression);
                } catch(Exception e) {;}
            }
            return cellValue;
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="  DateRenderer (class)  ">
    
    public static class DateRenderer extends TextRenderer {
        private SimpleDateFormat outputFormatter;
        
        protected Object resolveValue(CellRenderers.Context ctx) {
            String format = null;
            Column oColumn = ctx.getColumn();
            DateColumnHandler dateHandler = null; 
            if (oColumn.getTypeHandler() instanceof DateColumnHandler) { 
                dateHandler = (DateColumnHandler) oColumn.getTypeHandler();
                format = dateHandler.getOutputFormat();
            } else { 
                format = oColumn.getFormat();
            } 
            
            Object cellValue = ctx.getValue();
            if ( format == null || format.trim().length()==0 ) { 
                return cellValue; 
            }
            
            if ( dateHandler != null ) { 
                try { 
                    return dateHandler.format( cellValue, format ); 
                } catch(Throwable ex) {;}
            } 
            
            if ( format != null && cellValue instanceof Date) {
                try {
                    if (outputFormatter == null) {
                        outputFormatter = new SimpleDateFormat(format);
                    } 
                    cellValue = outputFormatter.format((Date) cellValue);
                } catch(Throwable ex) {;}
            }
            return cellValue;
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="  DecimalRenderer (class)  ">
    
    public static class DecimalRenderer extends AbstractNumberRenderer {
        protected String getFormattedValue(Column c, Object value) {
            Number num = null;
            if (value == null) {
                /* do nothing */
            } else if (value instanceof BigDecimal) {
                num = (BigDecimal) value;
            } else {
                try {
                    num = new BigDecimal(value.toString());
                } catch(Exception e) {}
            }
            
            if (num == null) return null;
            
            String format = null;
            if (c.getTypeHandler() instanceof DecimalColumnHandler)
                format = ((DecimalColumnHandler) c.getTypeHandler()).getFormat();
            else
                format = c.getFormat();
            
            if (format == null || format.length() == 0) return num.toString();
            
            return formatValue(num, format, "#,##0.00");
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="  IntegerRenderer (class)  ">
    
    public static class IntegerRenderer extends AbstractNumberRenderer {
        protected String resolveAlignment(String alignment) {
            if (alignment == null || alignment.length() == 0)
                return "CENTER";
            else
                return alignment;
        }
        
        protected String getFormattedValue(Column c, Object value) {
            Number num = null;
            if (value == null) {
                /* do nothing */
            } else if (value instanceof Integer) {
                num = (Integer) value;
            } else {
                try {
                    num = new Integer(value.toString());
                } catch(Exception e) {}
            }
            
            if (num == null) return null;
            
            String format = null;
            if (c.getTypeHandler() instanceof IntegerColumnHandler)
                format = ((IntegerColumnHandler) c.getTypeHandler()).getFormat();
            else
                format = c.getFormat();
            
            if (format == null || format.length() == 0)
                return num.toString();
            else
                return formatValue(num, c.getFormat(), "0");
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ButtonRenderer "> 
    
    public static class ButtonRenderer extends AbstractRenderer implements ActionColumnHandler {

        private JLabel label; 
        private JButton button;
        
        public ButtonRenderer() {
            label = new JLabel();
            button = new JButton();
            button.setMargin(new Insets(0,0,0,0)); 
        } 
        
        public JComponent getComponent(JTable table, int rowIndex, int columnIndex) { 
            button.setVisible(getContext().getItemData() != null);
            if ( !button.isVisible() ) {
                return label; 
            }

            Column oColumn = getContext().getColumn(); 
            ButtonColumnHandler bch = (ButtonColumnHandler) oColumn.getTypeHandler(); 
            String expr = bch.getVisibleWhen();
            if ( expr != null && expr.trim().length() > 0 ) { 
                boolean b = false; 
                try { 
                    Object exprBean = getContext().createExpressionBean(); 
                    b = UIControlUtil.evaluateExprBoolean(exprBean, expr); 
                } catch(Throwable t) {;} 

                button.setVisible( b ); 
            } 
            return ( button.isVisible()? button : label ); 
        } 

        public void refresh(JTable table, Object value, boolean selected, boolean focus, int rowIndex, int colIndex) { 
            if ( !button.isVisible()) return; 
            
            Object cellValue = null; 
            Column oColumn = getContext().getColumn(); 
            String expr = oColumn.getExpression(); 
            if ( expr != null && expr.trim().length() > 0 ) { 
                Object exprBean = getContext().createExpressionBean(); 
                cellValue = UIControlUtil.evaluateExpr(exprBean, expr);
            }             
            button.setText( cellValue==null? " " : cellValue.toString() ); 
        } 
        
        public void invokeAction() throws Exception { 
            Column oColumn = getContext().getColumn(); 
            String name = oColumn.getName(); 
            if ( name != null && name.trim().length() > 0 ) { 
                getContext().getTableControl().invokeAction( name, new Object[]{} ); 
            } 
        } 
    } 
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="  LabelRenderer (class)  ">
    
    public static class LabelRenderer extends AbstractRenderer {
        private JLabel label;
        
        public LabelRenderer() {
            label = createComponent();
            label.setVerticalAlignment(SwingConstants.CENTER);
        }
        
        private JLabel createComponent() {
            if (label == null) {
                label = new JLabel();
            }
            return label;
        }
        
        public JComponent getComponent(JTable table, int rowIndex, int columnIndex) {
            return label;
        }
        
        protected Object resolveValue(CellRenderers.Context ctx) {
            return ctx.getValue();
        }
        
        public void refresh(JTable table, Object value, boolean selected, boolean focus, int rowIndex, int columnIndex) {
            Object columnValue = resolveValue(getContext());
            Column oColumn = getContext().getColumn();
            
            TextCase oTextCase = (oColumn == null? null: oColumn.getTextCase());
            if (oTextCase != null && columnValue != null)
                label.setText("<html>"+oTextCase.convert(columnValue.toString())+"</html>"); 
            
            label.setHorizontalAlignment( SwingConstants.LEFT );
            //set alignment if it is specified in the Column model
            if ( oColumn != null && oColumn.getAlignment() != null )
                getComponentSupport().alignText(label, oColumn.getAlignment());
            
            setValue(label, oColumn, columnValue);
        }
        
        protected void setValue(JLabel label, Column oColumn, Object value) {
            Object cellValue = value;            
            Column.TypeHandler handler = (oColumn==null? null: oColumn.getTypeHandler()); 
            
            try {
                String expression = ((LabelColumnHandler) handler).getExpression();
                if (expression != null && expression.length() > 0) {
                    Object exprBean = getContext().createExpressionBean(); 
                    cellValue = UIControlUtil.evaluateExpr(exprBean, expression);
                }                
            } catch(Throwable t){;} 
            
            if (oColumn != null && cellValue != null && oColumn.isHtmlDisplay() )
                cellValue = "<html>" + cellValue + "</html>";
            
            label.setText("<html>"+(cellValue == null? "": cellValue.toString())+"</html>");
        }
    }
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc="  LookupRenderer (class)  ">
    
    public static class LookupRenderer extends TextRenderer {
        private ImageIcon icon;
        
        protected void initComponent(CellRenderers.LabelControl label) {
            String iconpath = "com/rameses/rcp/icons/search.png";
            icon = ImageIconSupport.getInstance().getIcon(iconpath); 
        }        
        
        protected Object resolveValue(CellRenderers.Context ctx) {
            Column oColumn = ctx.getColumn();
            String expression = oColumn.getExpression(); 
            if ( expression==null || expression.trim().length()==0 ) { 
                if (oColumn.getTypeHandler() instanceof LookupColumnHandler) { 
                    expression = ((LookupColumnHandler) oColumn.getTypeHandler()).getExpression(); 
                } 
            } 
            
            Object cellValue = ctx.getValue();
            if (expression != null) {
                try {
                    Object exprBean = getContext().createExpressionBean();
                    cellValue = UIControlUtil.evaluateExpr(exprBean, expression);
                } catch(Exception e) {;}
            }
            return cellValue;
        }

        protected void decorate(CellRenderers.LabelControl label, Column oColumn, ListItem li) {
            if (li == null) {
                label.setRightIcon(null); 
            } else if (li.getIndex() == 0) {
                label.setRightIcon(icon); 
            } else if (li.getState() == ListItem.STATE_EMPTY) { 
                label.setRightIcon(null); 
            } else { 
                label.setRightIcon(icon); 
            } 
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="  OpenerRenderer (class)  ">
    
    public static class OpenerRenderer extends TextRenderer {
        protected Object resolveValue(CellRenderers.Context ctx) {
            Column oColumn = ctx.getColumn();
            String expression = oColumn.getExpression();             
            if (expression == null || expression.length() == 0) {
                if (oColumn.getTypeHandler() instanceof OpenerColumnHandler)
                    expression = ((OpenerColumnHandler) oColumn.getTypeHandler()).getExpression();
            }
                        
            Object cellValue = ctx.getValue();
            if (expression != null) {
                try {
                    Object exprBean = getContext().createExpressionBean();
                    cellValue = UIControlUtil.evaluateExpr(exprBean, expression);
                } catch(Exception e) {;}
            }
            return cellValue;
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="  IconRenderer (class)  ">
    
    public static class IconRenderer extends AbstractRenderer { 
        
        private JLabel label;
        
        public IconRenderer() {
            label = createComponent();
            label.setVerticalAlignment(SwingConstants.CENTER);
        }
        
        private JLabel createComponent() {
            if (label == null) {
                label = new JLabel();
                label.setHorizontalAlignment( SwingConstants.CENTER ); 
            }
            return label;
        }
        
        public JComponent getComponent(JTable table, int rowIndex, int columnIndex) {
            return label;
        }

        protected void decorateSelected(JTable table, JComponent comp, boolean selected, boolean hasFocus) {
            super.decorateSelected(table, comp, false, hasFocus); 
        } 
        
        protected Object resolveValue(CellRenderers.Context ctx) {
            return ctx.getValue();
        }
        
        public void refresh(JTable table, Object value, boolean selected, boolean focus, int rowIndex, int columnIndex) {
            Object cellValue = resolveValue(getContext());
            Column oColumn = getContext().getColumn();

            Column.TypeHandler handler = (oColumn==null? null: oColumn.getTypeHandler()); 
            try {
                String expression = (oColumn == null ? null: oColumn.getExpression()); 
                if (expression != null && expression.trim().length() > 0) {
                    Object exprBean = getContext().createExpressionBean(); 
                    cellValue = UIControlUtil.evaluateExpr(exprBean, expression);
                } 
            } catch(Throwable t){;} 
            
            loadIcon( cellValue ); 
        }
        
        void loadIcon( Object value ) { 
            label.setText("");
            if ( value == null ) {
                label.setIcon( null ); 
            } else {
                if (value instanceof ImageIcon) {
                    label.setIcon((ImageIcon) value); 
                    
                } else if (value instanceof byte[]) { 
                    try { 
                        label.setIcon(new ImageIcon((byte[]) value)); 
                    } catch(Throwable t) {
                        System.out.println("[WARN] failed to render icon caused by " + t.getClass().getName() + ": " + t.getMessage()); 
                        label.setIcon(null); 
                    } 
                    
                } else if (value instanceof String) {
                    try { 
                        String sval = value.toString();
                        if (sval.matches("[a-zA-Z0-9]://.*")) {
                            label.setIcon(new ImageIcon(new URL(sval)));
                        } else {
                            ImageIcon iicon = ImageIconSupport.getInstance().getIcon(sval); 
                            if (iicon == null) {
                                iicon = new ImageIcon(getClass().getClassLoader().getResource(sval));
                            }
                            label.setIcon(iicon); 
                        }
                    } catch(Throwable t) { 
                        label.setIcon(null); 
                    }
                } else { 
                    label.setIcon(null); 
                } 
            }
        }
    }
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" LabelControl ">
    
    static class LabelControl extends JLabel {
        
        private ImageIcon rightIcon;
        
        public ImageIcon getRightIcon() { return rightIcon; } 
        public void setRightIcon(ImageIcon rightIcon) {
            this.rightIcon = rightIcon; 
        } 
        
        public Insets getInsets(Insets insets) {
            Insets ins = super.getInsets(insets); 
            if (ins == null) ins = new Insets(0, 0, 0, 0); 

            ImageIcon iicon = getRightIcon();
            if (iicon == null) return ins;

            Insets ins0 = new Insets(ins.top, ins.left, ins.bottom, ins.right); 
            int iconWidth = iicon.getIconWidth(); 
            int rightPad = Math.max(ins0.right, iconWidth+3); 
            ins0.right = rightPad;
            return ins0; 
        }        

        public void paint(Graphics g) { 
            super.paint(g);

            ImageIcon iicon = getRightIcon();
            if (iicon != null) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.8f));

                int imgWidth = iicon.getIconWidth();
                int imgHeight = iicon.getIconHeight();
                int x = this.getWidth() - (imgWidth + 3);
                int y = (this.getHeight() - imgHeight) / 2;
                iicon.paintIcon(this, g2, x, y);
                g2.dispose();
            }        
        }            
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ActionColumnHandler ">
    
    public static interface ActionColumnHandler {
        
        void invokeAction() throws Exception; 
        
    }
    
    // </editor-fold> 
}
