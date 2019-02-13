/*
 * DataTableModelDesignTime.java
 *
 * Created on May 28, 2013, 5:20 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control.table;

import com.rameses.rcp.common.Column;
import com.rameses.rcp.support.ColorUtil;
import com.rameses.rcp.support.ComponentSupport;
import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

/**
 *
 * @author wflores
 */
class DataTableModelDesignTime extends AbstractTableModel
{
    private Column[] columns;
    
    public DataTableModelDesignTime(Column[] columns) {
        this.columns = columns; 
    }

    public int getRowCount() { return 1; }

    public int getColumnCount() { 
        return (columns == null? 0: columns.length); 
    }

    public String getColumnName(int index) { 
        Column col = getColumn( index ); 
        return (col == null? "": col.getCaption()); 
    } 
    
    public Column getColumn( int index ) { 
        int colCount = getColumnCount(); 
        if (index >= 0 && index < colCount) { 
            return columns[index]; 
        } else { 
            return null; 
        } 
    } 
    
    public Object getValueAt(int rowIndex, int columnIndex) { 
        return ""; 
    } 

    void applyColumnAttributes(DataTableComponent table) {
        int columnCount = getColumnCount(); 
        for (int i=0; i<columnCount; i++) {
            Column oColumn = columns[i];             
            if (oColumn.getTypeHandler() instanceof SelectionColumnHandler) { 
                oColumn.setEditable(true);
            } 
            
            TableColumn oTableColumn = table.getColumnModel().getColumn(i);            
            if (oColumn.getWidth() >= 0) { 
                oTableColumn.setPreferredWidth(oColumn.getWidth());
                oTableColumn.setWidth(oColumn.getWidth());
            }
            if (oColumn.getMinWidth() > 0) { 
                oTableColumn.setMinWidth(oColumn.getMinWidth()); 
            } 
            if (oColumn.getMaxWidth() > 0) { 
                oTableColumn.setMaxWidth(oColumn.getMaxWidth());
            } 
            oTableColumn.setResizable(oColumn.isResizable()); 
            oTableColumn.setCellRenderer(new DefaultRenderer()); 
        }
    }
    
    // <editor-fold defaultstate="collapsed" desc="  DefaultRenderer (class)  ">
    
    private class DefaultRenderer implements TableCellRenderer {
        private Insets CELL_MARGIN = TableUtil.CELL_MARGIN;
        private Color FOCUS_BG = TableUtil.FOCUS_BG;
        private ComponentSupport componentSupport;
        
        private JLabel comp;
        
        DefaultRenderer() {
            comp = new JLabel();
            comp.setOpaque(false);
        }
        
        private ComponentSupport getComponentSupport() {
            if (componentSupport == null)
                componentSupport = new ComponentSupport();
            
            return componentSupport;
        }
        
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int rowIndex, int columnIndex) {
            getComponentSupport().setEmptyBorder(comp, CELL_MARGIN);
            comp.setFont(table.getFont());
            
            if (isSelected) {
                comp.setBackground(table.getSelectionBackground());
                comp.setForeground(table.getSelectionForeground());
                comp.setOpaque(true);
                if (hasFocus) {
                    comp.setBackground(FOCUS_BG);
                    comp.setForeground(table.getForeground());
                }
            } 
            else {
                comp.setForeground(table.getForeground());
                comp.setOpaque(false);
                
                DataTableComponent dtc = (DataTableComponent) table;
                if ((rowIndex+1)%2 == 0) {
                    if (dtc.getEvenBackground() != null) {
                        comp.setBackground(dtc.getEvenBackground());
                        comp.setOpaque(true);
                    }
                    
                    if (dtc.getEvenForeground() != null)
                        comp.setForeground(dtc.getEvenForeground());
                } 
                else {
                    if (dtc.getOddBackground() != null) {
                        comp.setBackground(dtc.getOddBackground());
                        comp.setOpaque(true);
                    }
                    
                    if (dtc.getOddForeground() != null)
                        comp.setForeground(dtc.getOddForeground());
                }
            }
            
            if ( !table.isEnabled() ) {
                Color c = comp.getBackground();
                comp.setBackground(ColorUtil.brighter(c, 5));
                
                c = comp.getForeground();
                comp.setForeground(ColorUtil.brighter(c, 5));
            }
            
            //border support
            Border inner = getComponentSupport().createEmptyBorder(CELL_MARGIN);
            Border border = BorderFactory.createEmptyBorder(1,1,1,1);
            if (hasFocus) {
                if (isSelected)
                    border = UIManager.getBorder("Table.focusSelectedCellHighlightBorder");
                if (border == null)
                    border = UIManager.getBorder("Table.focusCellHighlightBorder");
            }
            comp.setBorder(BorderFactory.createCompoundBorder(border, inner));
            return comp;
        }
    }
    
    // </editor-fold>    
}
