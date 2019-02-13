/*
 * TableHeaderRenderer.java
 *
 * Created on February 22, 2011, 2:38 PM
 * @author jaycverg
 */

package com.rameses.rcp.control.table;

import com.rameses.rcp.common.Column;
import com.rameses.rcp.support.ColorUtil;
import com.rameses.rcp.support.ComponentSupport;
import com.rameses.util.ValueUtil;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.beans.Beans;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;


public class TableHeaderRenderer extends JLabel implements TableCellRenderer 
{
    private ComponentSupport componentSupport;
    
    protected ComponentSupport getComponentSupport() 
    {
        if (componentSupport == null) 
            componentSupport = new ComponentSupport();

        return componentSupport;
    }    
    
    public void xpaint(Graphics g) 
    {
        Graphics2D g2 = (Graphics2D) g.create();
        Color bg = Color.LIGHT_GRAY;
        Dimension d = getSize();
        //GradientPaint gp = new GradientPaint(0, 0, ColorUtil.brighter(bg, 30), 0, (d.height-1)/2, ColorUtil.brighter(bg, 15));
        GradientPaint gp = new GradientPaint(0, 0, ColorUtil.brighter(bg,30), 0, (d.height-1)/2, ColorUtil.darker(bg,0));
        g2.setPaint(gp);
        g2.fillRect(0,0,d.width-1,d.height-1);
        g2.dispose();
        
        super.paint(g);
    }
    
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int rowIndex, int colIndex) {
        if (!Beans.isDesignTime()) {
            if (table instanceof TableControl) {
                TableControl xtable = (TableControl) table;
                TableControlModel tcm = (TableControlModel) xtable.getModel();
                Column c = tcm.getColumn(colIndex);
                if (c != null && c.getAlignment() != null) 
                    getComponentSupport().alignText(this, c.getAlignment()); 
            }
        }
        
        setText( ValueUtil.isEmpty(value) ? " " : value + "");
        //setBorder( new DataTableHeader.CustomBorder(table, new Insets(2,5,2,5)) );        
        super.setBorder(new TableBorders.HeaderBorder()); 
        return this;
    }

    
    // The following methods override the defaults for performance reasons
    public void validate() {}
    public void revalidate() {}
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {}
    public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {}

    public void setBorder(Border border) {}
    
}