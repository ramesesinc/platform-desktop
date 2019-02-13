/*
 * SelectionCellRenderer.java
 *
 * Created on June 5, 2013, 1:32 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control.table;

import com.rameses.rcp.common.AbstractListDataProvider;
import com.rameses.rcp.common.Column;
import com.rameses.rcp.util.UIControlUtil;
import java.util.Collection;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;

/**
 *
 * @author wflores
 */
public class SelectionCellRenderer extends CellRenderers.AbstractRenderer {
    
    private JLabel label;
    private JCheckBox component;
    
    public SelectionCellRenderer() {
        label = new JLabel("");    
        component = new JCheckBox();
        component.setHorizontalAlignment(SwingConstants.CENTER);
        component.setBorderPainted(true);        
    }

    public JComponent getComponent(JTable table, int rowIndex, int columnIndex) {
        Object itemData = getTableControl().getDataProvider().getListItemData(rowIndex);
        return (itemData == null? label: component);
    }

    public void refresh(JTable table, Object value, boolean selected, boolean focus, int rowIndex, int columnIndex) {                
        component.setSelected(false);
        
        Column oColumn = getTableControlModel().getColumn(columnIndex); 
        Object itemData = getTableControl().getDataProvider().getListItemData(rowIndex); 
        
        boolean matched = getSelectionSupport().isItemChecked( itemData );
        component.setSelected(matched); 
    }  
    
    private Collection getSourceItems(Object itemData, String name) {
        Object exprBean = getTableControl().createExpressionBean(itemData);

        Collection checkedItems = null; 
        try {
            checkedItems = (Collection) UIControlUtil.getBeanValue(exprBean, name); 
        } catch(Exception ex) {;} 
        
        return checkedItems; 
    }
        
    private AbstractListDataProvider.ListSelectionSupport getSelectionSupport() {
        return getTableControl().getDataProvider().getSelectionSupport(); 
    }  
    
    
}
