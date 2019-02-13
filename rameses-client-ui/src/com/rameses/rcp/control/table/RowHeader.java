/*
 * RowHeader.java
 *
 * Created on February 22, 2011, 3:10 PM
 * @author jaycverg
 */

package com.rameses.rcp.control.table;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;

public class RowHeader extends TableHeaderRenderer 
{    
    private JTable table;
    
    public RowHeader(JTable table) 
    {
        this.table = table;
        setBorder(new DataTableHeader.CornerBorder(table, JScrollPane.UPPER_LEFT_CORNER)); 
        //setBorder(BorderFactory.createLineBorder(borderColor));
        setPreferredSize(new Dimension(23,23));
        setHorizontalAlignment(SwingConstants.CENTER);
        setVerticalAlignment(SwingConstants.CENTER);
        setForeground(Color.BLUE);
        setFont(new Font("Courier", Font.PLAIN, 11));
        //edit(true);
    }
    
    public void setText(String text) {;}
    
    public void edit(boolean b) {
        if (b)
            super.setText("*");
        else
            super.setText("");
    }
}
