/*
 * TableUtil.java
 *
 * Created on June 26, 2010, 10:53 AM
 * @author jaycverg
 */

package com.rameses.rcp.control.table;

import com.rameses.common.ExpressionResolver;
import com.rameses.rcp.common.AbstractListDataProvider;
import com.rameses.rcp.common.Column;
import com.rameses.rcp.common.StyleRule;
import com.rameses.rcp.control.XCheckBox;
import com.rameses.rcp.control.XComboBox;
import com.rameses.rcp.control.XDateField;
import com.rameses.rcp.control.XDecimalField;
import com.rameses.rcp.control.XIntegerField;
import com.rameses.rcp.control.XLookupField;
import com.rameses.rcp.control.XOpenerField;
import com.rameses.rcp.control.XTextField;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.rcp.support.ColorUtil;
import com.rameses.rcp.support.ComponentSupport;
import com.rameses.rcp.ui.UIControl;
import com.rameses.rcp.ui.Validatable;
import com.rameses.rcp.util.ControlSupport;
import com.rameses.rcp.util.UIControlUtil;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.FocusListener;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;

/**
 *   This class handles TableComponents cell renderer and editor management
 * Default Alignments:
 *  1. decimal/double - right
 *  2. integer/boolean/checkbox/date - center
 *  3. string - left
 */

public final class TableUtil 
{
    public static final Insets CELL_MARGIN = new Insets(1, 5, 1, 5);
    public static final Color FOCUS_BG = new Color(254, 255, 208);
    public static final String HIDE_ON_ENTER = "hide.on.enter";
    
    private static Map<String, Class<? extends JComponent>> editors = new HashMap();
    private static Map<String, Class> numClass = new HashMap();
    private static Map<Object, TableCellRenderer> renderers = new HashMap();
    private static TableCellRenderer headerRenderer = new TableHeaderRenderer();
    
    // <editor-fold defaultstate="collapsed" desc="  static initializer  ">
    
    static 
    {
        //map of editors
        editors.put("string", XTextField.class);
        editors.put("text", XTextField.class);
        editors.put("boolean", XCheckBox.class);
        editors.put("checkbox", XCheckBox.class);
        editors.put("combo", XComboBox.class);
        editors.put("combobox", XComboBox.class);
        editors.put("date", XDateField.class);
        editors.put("double", XDecimalField.class);
        editors.put("decimal", XDecimalField.class);
        editors.put("integer", XIntegerField.class);        
        editors.put("lookup", XLookupField.class);
        editors.put("opener", XOpenerField.class);
        editors.put("selection", SelectionCellEditor.class);
        
        //map of renderers
        renderers.put("string", new CellRenderers.TextRenderer());
        renderers.put("text", new CellRenderers.TextRenderer());
        renderers.put("boolean", new CellRenderers.CheckBoxRenderer());
        renderers.put("checkbox", new CellRenderers.CheckBoxRenderer());  
        renderers.put("combo", new CellRenderers.ComboBoxRenderer());
        renderers.put("combobox", new CellRenderers.ComboBoxRenderer());
        renderers.put("date", new CellRenderers.DateRenderer());        
        renderers.put("double", new CellRenderers.DecimalRenderer());
        renderers.put("decimal", new CellRenderers.DecimalRenderer());
        renderers.put("integer", new CellRenderers.IntegerRenderer());        
        renderers.put("label", new CellRenderers.LabelRenderer());
        renderers.put("lookup", new CellRenderers.LookupRenderer());
        renderers.put("opener", new CellRenderers.OpenerRenderer());
        renderers.put("icon", new CellRenderers.IconRenderer());
        renderers.put("dynamic", new DynamicCellRenderer());
        renderers.put("button", new CellRenderers.ButtonRenderer());
        
        renderers.put(SelectionColumnHandler.class, new SelectionCellRenderer());
        
        //number class types
        numClass.put("decimal", BigDecimal.class);
        numClass.put("integer", Integer.class);
        numClass.put("double", Double.class);
    }
    
    // </editor-fold>    
        
    public static JComponent createCellEditor(Column oColumn) {
        if (oColumn.getTypeHandler() == null) { 
            oColumn.setTypeHandler(ColumnHandlerUtil.newInstance().createTypeHandler(oColumn)); 
        } 
        oColumn.setType(oColumn.getTypeHandler().getType()); 
        Class editorClass = editors.get(oColumn.getType()); 
        JComponent editor = null;
        try { 
            editor = (editorClass==null? null: (JComponent) editorClass.newInstance());
            if (editor != null) customize(editor, oColumn);            
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return editor;
    }
    
    public static TableCellRenderer getCellRenderer(Column oColumn) 
    {
        ColumnHandlerUtil u = ColumnHandlerUtil.newInstance();
        if (oColumn.getTypeHandler() == null) 
            oColumn.setTypeHandler(u.createTypeHandler(oColumn)); 
        
        oColumn.setType(oColumn.getTypeHandler().getType()); 
        //this will force to set the preferred alignment of each column
        CellRenderers.getPreferredAlignment(oColumn); 
        
        TableCellRenderer renderer = renderers.get(oColumn.getType()); 
        if (renderer == null) 
            return renderers.get(oColumn.getTypeHandler().getClass());
        else
            return renderer; 
    }
    
    public static TableCellRenderer getHeaderRenderer() {
        return headerRenderer;
    }
    
    public static JComponent getTableCornerComponent(Color borderColor) {
        JLabel label = new JLabel(" ");
        Border bb = BorderFactory.createLineBorder(borderColor);
        Border eb = BorderFactory.createEmptyBorder(2,5,2,1);
        label.setBorder( BorderFactory.createCompoundBorder(bb, eb) );
        return label;
    }
    
    public static synchronized void customize(JScrollPane scrollPane, JTable table) 
    {
//        JLabel corner = new JLabel();
//        corner.setBorder(new TableHeaderBorder());
//        corner.setPreferredSize(new Dimension(23,23));  
        
        JComponent corner = new DataTableHeader.CornerBorder(table, JScrollPane.UPPER_RIGHT_CORNER).createComponent();
        corner.putClientProperty("Component.proxy", table); 
        scrollPane.setCorner(JScrollPane.UPPER_RIGHT_CORNER, corner);
        //scrollPane.setCorner(JScrollPane.UPPER_RIGHT_CORNER, (JComponent) headerRenderer);
    }
    
    
    // <editor-fold defaultstate="collapsed" desc="  editor customizer method  "> 
    
    private static void customize(JComponent editor, Column col) 
    {
        //add JTable flag to notify that editor is in a JTable component
        editor.putClientProperty(JTable.class, true);
                
        //remove all focus listeners (we don't need it in the table)
        for (FocusListener l : editor.getFocusListeners() ) {
            editor.removeFocusListener(l);
        }
        
        //apply required if editor is Validatable
        if ( editor instanceof Validatable ) 
        {
            Validatable v = (Validatable) editor;
            v.setRequired( col.isRequired() );
            v.setCaption( col.getCaption() );
        }

        Column.TypeHandler oHandler = ColumnHandlerUtil.newInstance().createTypeHandler(col); 
        if (col.getTypeHandler() == null) col.setTypeHandler(oHandler); 
        
        if ( editor instanceof UIControl && oHandler != null) 
            ((UIControl) editor).setPropertyInfo(oHandler); 

        if ( editor instanceof XCheckBox ) 
        { 
            XCheckBox xcomp = (XCheckBox) editor;
            xcomp.setHorizontalAlignment(SwingConstants.CENTER);
            xcomp.setBorderPainted(true); 
        } 
        else if ( editor instanceof XComboBox ) 
        {
            XComboBox xcomp = (XComboBox) editor;
            xcomp.setDynamic( col.isDynamic() );
            xcomp.setImmediate(true);
            if (col.isRequired()) 
                xcomp.setAllowNull(false);
            if (col.getFieldType() != null) 
                xcomp.setFieldType(col.getFieldType());
        }
        else if ( editor instanceof XLookupField ) 
        {
            XLookupField xcomp = (XLookupField) editor;
            xcomp.setTransferFocusOnSelect(false);            
        } 
        
        Font font = (Font) UIManager.get("Table.font");
        editor.setFont(font);
        editor.setBackground(FOCUS_BG);
        
        //set alignment if it is specified in the Column model
        if ( col.getAlignment() != null && editor instanceof JTextField ) 
        {
            JTextField jtf = (JTextField) editor;
            if ( "right".equals(col.getAlignment().toLowerCase()) )
                jtf.setHorizontalAlignment(SwingConstants.RIGHT);
            else if ( "center".equals(col.getAlignment().toLowerCase()))
                jtf.setHorizontalAlignment(SwingConstants.CENTER);
            else if ( "left".equals(col.getAlignment().toLowerCase()) )
                jtf.setHorizontalAlignment(SwingConstants.LEFT);
        } 
        else 
        {
            //border support
            Border inner = BorderFactory.createEmptyBorder(CELL_MARGIN.top, CELL_MARGIN.left, CELL_MARGIN.bottom, CELL_MARGIN.right);
            Border border = UIManager.getBorder("Table.focusSelectedCellHighlightBorder");
            if (border == null) border = UIManager.getBorder("Table.focusCellHighlightBorder");
            
            editor.setBorder(BorderFactory.createCompoundBorder(border, inner));
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="  AbstractRenderer (class)  ">
    
    public static abstract class AbstractRenderer implements TableCellRenderer 
    {
        private ComponentSupport componentSupport;
        
        protected ComponentSupport getComponentSupport() 
        {
            if (componentSupport == null) 
                componentSupport = new ComponentSupport();

            return componentSupport;
        }
        
        public abstract JComponent getComponent(JTable table, int row, int column);
        
        public abstract void refresh(JTable table, Object value, boolean selected, boolean focus, int row, int column);
        
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) 
        {
            TableControl xtable = (TableControl) table;
            TableControlModel xmodel = (TableControlModel) xtable.getModel();
            JComponent comp = getComponent(table, row, column);
            comp.setBorder(BorderFactory.createEmptyBorder(CELL_MARGIN.top, CELL_MARGIN.left, CELL_MARGIN.bottom, CELL_MARGIN.right));
            comp.setFont(table.getFont());
            
            if (isSelected) 
            {
                comp.setBackground(table.getSelectionBackground());
                comp.setForeground(table.getSelectionForeground());
                comp.setOpaque(true);                
                if (hasFocus) 
                {
                    comp.setBackground(FOCUS_BG);
                    comp.setForeground(table.getForeground());
                }
            } 
            else 
            {
                comp.setForeground(table.getForeground());
                comp.setOpaque(false);
                
                if ( (row % 2 == 0) ) 
                {
                    if ( xtable.getEvenBackground() != null ) 
                    {
                        comp.setBackground( xtable.getEvenBackground() );
                        comp.setOpaque(true);
                    }
                    if ( xtable.getEvenForeground() != null ) {
                        comp.setForeground( xtable.getEvenForeground() );
                    }
                    
                } 
                else 
                {
                    if ( xtable.getOddBackground() != null ) 
                    {
                        comp.setBackground( xtable.getOddBackground() );
                        comp.setOpaque(true);
                    }
                    if ( xtable.getOddForeground() != null ) {
                        comp.setForeground( xtable.getOddForeground() );
                    }
                }
            }
            
            AbstractListDataProvider lm = xtable.getDataProvider();
            ClientContext clientCtx = ClientContext.getCurrentContext();
            ExpressionResolver exprRes = ExpressionResolver.getInstance();
            Column colModel = xmodel.getColumn(column);
            
//            try {
//                StyleRule[] styles = xtable.getBinding().getStyleRules();
//                if( styles != null && styles.length > 0) {
//                    comp.setOpaque(true);
//
//                    ListItem listItem = lm.getSelectedItem();
//                    if( listItem == null ) {
//                        listItem = lm.getItemList().get(0);
//                    }
//
//                    Map bean = new HashMap();
//                    bean.put("row", listItem.getRownum());
//                    bean.put("column", column);
//                    bean.put("columnName", colModel.getName());
//                    bean.put("root", listItem.getRoot());
//                    bean.put("selected", isSelected);
//                    bean.put("hasFocus", hasFocus);
//                    bean.put("item", listItem.getItem());
//                    applyStyle( xtable.getName(), bean, comp, styles, exprRes );
//                }
//            } catch(Exception e){;}
            
            
            String errmsg = lm.getMessageSupport().getErrorMessage(row);
            if (errmsg != null) 
            {
                if (!hasFocus) 
                {
                    comp.setBackground( xtable.getErrorBackground() );
                    comp.setForeground( xtable.getErrorForeground() );
                    comp.setOpaque(true);
                }
            }
            
            if ( !table.isEnabled() ) 
            {
                Color c = comp.getBackground();
                comp.setBackground(ColorUtil.brighter(c, 5));
                c = comp.getForeground();
                comp.setForeground(ColorUtil.brighter(c, 5));
            }
            
            //border support
            Border inner = BorderFactory.createEmptyBorder(CELL_MARGIN.top, CELL_MARGIN.left, CELL_MARGIN.bottom, CELL_MARGIN.right);
            Border border = BorderFactory.createEmptyBorder(1,1,1,1);
            if (hasFocus) 
            {
                if (isSelected) 
                    border = UIManager.getBorder("Table.focusSelectedCellHighlightBorder");
                if (border == null) 
                    border = UIManager.getBorder("Table.focusCellHighlightBorder");
            }
            comp.setBorder(BorderFactory.createCompoundBorder(border, inner));
            
            refresh(table, value, isSelected, hasFocus, row, column);
            return comp;
        }
        
        private void applyStyle(String name, Map bean, Component comp, StyleRule[] styles, ExpressionResolver exprRes) {
            if ( styles == null ) return;
            
            if( name == null ) name = "_any_name";
            
            //apply style rules
            for(StyleRule r : styles) {
                String pattern = r.getPattern();
                String rule = r.getExpression();
                
                //test expression
                boolean applyStyles = false;
                if ( rule!=null && name.matches(pattern) ){
                    try {
                        Object o = exprRes.eval(rule, bean);
                        applyStyles = Boolean.valueOf(o+"");
                    } catch (Exception ign){
                        System.out.println("STYLE RULE ERROR: " + ign.getMessage());
                    }
                }
                
                if ( applyStyles ) {
                    ControlSupport.setStyles( r.getProperties(), comp );
                }
            }
        }
        
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="  StringRenderer (class)  ">
    
    public static class StringRenderer extends AbstractRenderer 
    {        
        private JLabel label;
        
        public StringRenderer() 
        {
            label = new JLabel();
            label.setVerticalAlignment(SwingConstants.CENTER);
        }
        
        public JComponent getComponent(JTable table, int row, int column) {
            return label;
        }
        
        public void refresh(JTable table, Object value, boolean selected, boolean focus, int row, int column) 
        {
            TableControl tc = (TableControl) table;
            Column c = ((TableControlModel) tc.getModel()).getColumn(column);                        
            Object columnValue = value; 
            
            if ( c.getExpression() != null ) 
            {
                ExpressionResolver er = ExpressionResolver.getInstance();
                try 
                {
                    Object bean = columnValue;
                    if (table.getModel() instanceof DataTableModel)
                        bean = ((DataTableModel) table.getModel()).getItem(row); 
                    
                    if (table instanceof DataTableComponent) 
                        bean = ((DataTableComponent) table).createExpressionBean(bean); 
                    
                    columnValue = UIControlUtil.evaluateExpr(bean, c.getExpression()); 
                } 
                catch(Exception e) {
                    //e.printStackTrace();
                }
            }
            
            String format = c.getFormat();
            String type = c.getType();
            if ( "decimal".equals(type) || "double".equals(type) || columnValue instanceof BigDecimal || columnValue instanceof Double ) 
            {
                label.setHorizontalAlignment( SwingConstants.RIGHT );
                label.setText((columnValue == null ? "" : format(columnValue, format, "#,##0.00")));
                
            } 
            else if ( "integer".equals(type) || columnValue instanceof Number ) 
            {
                label.setHorizontalAlignment( SwingConstants.CENTER );
                label.setText((columnValue == null ? "" : format(columnValue, format, "#,##0")));
                
            } 
            else if ( "date".equals(type) || columnValue instanceof Date || columnValue instanceof Time || columnValue instanceof Timestamp ) 
            {
                
                label.setHorizontalAlignment( SwingConstants.CENTER );
                SimpleDateFormat formatter = null;
                if ( format != null )
                    formatter = new SimpleDateFormat(format);
                else
                    formatter = new SimpleDateFormat("yyyy-MM-dd");
                
                label.setText((columnValue == null ? "" : formatter.format(columnValue)));
            } 
            else 
            {
                label.setHorizontalAlignment( SwingConstants.LEFT );
                if ( columnValue != null && c.isHtmlDisplay() ) 
                    columnValue = "<html>" + columnValue + "</html>";

                label.setText((columnValue == null ? "" : columnValue.toString()));
            }
            
            if ( c.getAlignment() != null ) 
                getComponentSupport().alignText(label, c.getAlignment());
        }
        
        private String format(Object value, String format, String defaultFormat) {
            DecimalFormat formatter = null;
            if ( format != null)
                formatter = new DecimalFormat(format);
            else
                formatter = new DecimalFormat(defaultFormat);
            
            return formatter.format(value);
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="  BooleanRenderer (class)  ">
    
    public static class BooleanRenderer extends AbstractRenderer 
    {        
        private JCheckBox component;
        private JLabel empty;
        
        public BooleanRenderer() {
            component = new JCheckBox();
            component.setHorizontalAlignment(SwingConstants.CENTER);
            component.setBorderPainted(true);
            
            //empty renderer when row object is null
            empty = new JLabel("");
        }
        
        public JComponent getComponent(JTable table, int rowIndex, int colIndex) 
        {
            AbstractListDataProvider ldp = ((TableControl) table).getDataProvider();
            if (ldp.getListItemData(rowIndex) == null) return empty;
            
            return component;
        }
        
        public void refresh(JTable table, Object value, boolean selected, boolean focus, int rowIndex, int colIndex) 
        {
            AbstractListDataProvider ldp = ((TableControl) table).getDataProvider();
            if (ldp.getListItemData(rowIndex) == null) return;
            
            component.setSelected(resolveValue(value));
        }
        
        private boolean resolveValue(Object value) 
        {
            boolean selected = false; 
            if (value == null) { /* do nothing */ }
            else if ("true".equals(value+"")) selected = true; 
            else if ("yes".equals(value+"")) selected = true; 
            else if ("t".equals(value+"")) selected = true; 
            else if ("y".equals(value+"")) selected = true;             
            else if ("1".equals(value+"")) selected = true; 

            return selected;
        }        
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="  AbstractNumberRenderer (class)  ">
    
    public abstract static class AbstractNumberRenderer extends AbstractRenderer 
    {        
        private JLabel label;
        
        public AbstractNumberRenderer() {
            label = new JLabel();
            label.setHorizontalAlignment(SwingConstants.RIGHT);
        }
        
        public JComponent getComponent(JTable table, int row, int column) {
            return label;
        }
        
        protected abstract String getFormattedValue(Column c, Object value);
        
        public void refresh(JTable table, Object value, boolean selected, boolean focus, int row, int column) 
        {
            TableControl tc = (TableControl) table;
            Column c = ((TableControlModel) tc.getModel()).getColumn(column); 
            String result = getFormattedValue(c, value);
            label.setText((result == null ? "" : result));
            
            String alignment = (c.getAlignment() == null? null: c.getAlignment().toUpperCase());
            if ( alignment != null ) 
            {
                if ("CENTER".equals(alignment))
                    label.setHorizontalAlignment(SwingConstants.CENTER);
                else if ("LEFT".equals(alignment))
                    label.setHorizontalAlignment(SwingConstants.LEFT);
                else 
                    label.setHorizontalAlignment(SwingConstants.RIGHT);
            }            
        }
        
        protected String formatValue (Number value, String format, String defaultFormat) 
        {
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

    // <editor-fold defaultstate="collapsed" desc="  DecimalRenderer (class)  ">
    
    public static class DecimalRenderer extends AbstractNumberRenderer 
    { 
        protected String getFormattedValue(Column c, Object value)
        {
            Number num = null;
            if (value == null) { /* do nothing */ } 
            else if (value instanceof BigDecimal) {
                num = (BigDecimal) value;
            } 
            else {
                try {
                    num = new BigDecimal(value.toString());
                } catch(Exception e) {}
            }
            
            if (num == null) return null; 
            
            return formatValue(num, c.getFormat(), "#,##0.00");
        }
    }
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc="  IntegerRenderer (class)  ">
    
    public static class IntegerRenderer extends AbstractNumberRenderer 
    { 
        protected String getFormattedValue(Column c, Object value)
        {
            Number num = null;
            if (value == null) { /* do nothing */ } 
            else if (value instanceof Integer) {
                num = (Integer) value;
            } 
            else {
                try {
                    num = new Integer(value.toString());
                } catch(Exception e) {}
            }
            
            if (num == null) return null;             
            if (c.getFormat() == null) return num.toString(); 
            
            return formatValue(num, c.getFormat(), "0");
        }
    }
    
    // </editor-fold>        
    
    //<editor-fold defaultstate="collapsed" desc="  DefaultActionTextIcon (class)  ">
    private static class DefaultActionTextIcon implements Icon {
            
        public void paintIcon(Component c, Graphics g, int x, int y) {
            JButton b = new JButton("...");
            b.setSize(getIconWidth(), getIconHeight());

            Rectangle r = g.getClipBounds();
            Graphics g2 = g.create(x, y, r.width, r.height);
            b.paint(g2);
            g2.dispose();
        }

        public int getIconWidth() {
            return 16;
        }

        public int getIconHeight() {
            return 16;
        }
        
    }
    //</editor-fold>
    
}
