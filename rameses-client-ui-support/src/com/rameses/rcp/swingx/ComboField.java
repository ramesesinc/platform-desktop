/*
 * ComboField.java
 *
 * Created on May 28, 2013, 11:25 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.swingx;

import java.util.Comparator;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

/**
 *
 * @author wflores
 */
public class ComboField extends JComboBox implements IComponent 
{
    private ComboItem[] items;
    private Comparator comparator;    
    private boolean updateable = true;
    
    public ComboField() {
    }

    public void setItems(ComboItem[] items) 
    {
        this.items = (items == null? new ComboItem[]{}: items);
        setModel(new DefaultComboBoxModel(this.items)); 
    }
    
    public Object getValue() 
    {
        if (items == null || items.length == 0) return null;
        
        int index = getSelectedIndex();
        if (index >= 0 && index < items.length) 
            return items[index].getValue(); 
        else
            return null; 
    }

    public void setValue(Object value) 
    {
        int index = indexOf(value);
        if (index < 0 && items != null && items.length > 0) index = 0;

        int oldIndex = getSelectedIndex();
        setSelectedIndex(index); 
        if (index == oldIndex && oldIndex >= 0) selectedItemChanged(); 
    }
    
    private int indexOf(Object value) 
    {
        if (items == null || items.length == 0) return -1;
        
        for (int i=0; i<items.length; i++) {            
            if (getComparator() == null) { 
                if (items[i].equals(value)) return i;
            } 
            else {
                int result = getComparator().compare(items[i], value);
                if (result == 1) return i; 
            } 
        }
        return -1;
    }
    
    public boolean isUpdateable() { return updateable; }
    public void setUpdateable(boolean updateable) {
        this.updateable = updateable;
    }  
    
    public Comparator getComparator() { return comparator; } 
    public void setComparator(Comparator comparator) { 
        this.comparator = comparator; 
    } 
}
