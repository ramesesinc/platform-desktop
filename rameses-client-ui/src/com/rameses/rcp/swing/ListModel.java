/*
 * ListModel.java
 *
 * Created on June 9, 2014, 2:53 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.swing;

import com.rameses.rcp.common.AbstractListDataProvider;
import com.rameses.rcp.common.ListItem;
import java.util.List;
import javax.swing.AbstractListModel;

/**
 *
 * @author wflores
 */
public class ListModel extends AbstractListModel 
{
    private AbstractListDataProvider dataProvider;
    
    public ListModel(AbstractListDataProvider dataProvider) {
        this.dataProvider = dataProvider; 
    }
    
    private List<ListItem> getListItems() {
        if (dataProvider == null) return null; 
        
        return dataProvider.getListItems(); 
    }
    
    public int getSize() {
        List<ListItem> items = getListItems();
        return (items == null? 0: items.size());
    }

    public Object getElementAt(int index) {
        List<ListItem> items = getListItems();
        if (items == null) return null; 
        
        if (index >= 0 && index < items.size()) {
            return dataProvider.getListItemData(index); 
        } else {
            return null; 
        }
    }
}
