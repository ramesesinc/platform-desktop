/*
 * DynamicCellRenderer.java
 *
 * Created on June 14, 2013, 1:09 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control.table;

import com.rameses.rcp.common.Column;
import com.rameses.rcp.common.DynamicColumn;
import com.rameses.rcp.util.UIControlUtil;
import java.awt.Component;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;

/**
 *
 * @author wflores
 */
public class DynamicCellRenderer extends CellRenderers.AbstractRenderer 
{
    private ColumnHandlerUtil columnUtil = ColumnHandlerUtil.newInstance();
    private JLabel label = new JLabel(""); 
    private CellRenderers.Context ctx; 
    
    public JComponent getComponent(JTable table, int rowIndex, int columnIndex) 
    {
        return label;
    }

    public void refresh(JTable table, Object value, boolean selected, boolean focus, int rowIndex, int columnIndex) {
    }    

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int rowIndex, int columnIndex) 
    {
        ctx = new CellRenderers.Context(table, value, rowIndex, columnIndex); 
        Column itemColumn = getItemColumn(); 
        if (itemColumn == null) return label; 
        
        //CellRenderers.getRendererFor(itemColumn);
        return null;
    }
    
    
    private Column getItemColumn() 
    {
        Column oColumn = ctx.getColumn();
        if (!(oColumn instanceof DynamicColumn)) return null; 

        Object exprBean = ctx.createExpressionBean();
        
        Column selColumn = null;         
        DynamicColumn dc = (DynamicColumn) oColumn; 
        for (Column ic : dc.getColumns()) 
        {
            String visibleWhen = ic.getVisibleWhen(); 
            if (visibleWhen == null && ic.isVisible()) 
            {
                selColumn = ic;
                break;
            }
            else if (ic.isVisible()) 
            {
                boolean b = false;
                try {
                    b = UIControlUtil.evaluateExprBoolean(exprBean, visibleWhen); 
                } catch(Throwable t) {
                    t.printStackTrace();
                } 
                
                if (b) { 
                    selColumn = ic;
                    break;
                }
            }
        }
        return selColumn;
    }    
}
