/*
 * SelectionCellUtil.java
 *
 * Created on June 5, 2013, 10:26 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 *
 * @author wflores
 */
class SelectionCellUtil 
{
    public static SelectionCellUtil newInstance() {
        return new SelectionCellUtil();
    }
    
    
    private SelectionCellUtil() {
    }
    
    public boolean match(Object checkedItems, Object itemBean) 
    {
        boolean matched = false;
        if (checkedItems instanceof Collection) 
        {
            Iterator itr = ((Collection) checkedItems).iterator(); 
            while (itr.hasNext()) 
            {
                Object o = itr.next();
                if (o != null && o.equals(itemBean))
                {
                    matched = true;
                    break;
                }
            }
        }
        else if (checkedItems instanceof Object[])
        {
            Object[] values = (Object[]) checkedItems;
            for (int i=0; i<values.length; i++) 
            {
                Object o = values[i];
                if (o != null && o.equals(itemBean)) 
                {
                    matched = true;
                    break;
                }
            }
        }
        else if (checkedItems != null && checkedItems.equals(itemBean)) 
        { 
            matched = true;
        } 
        return matched; 
    }

    public Object attachItem(Object checkedItems, Object itemBean) 
    {
        if (checkedItems == null) checkedItems = new ArrayList(); 
        if (itemBean == null) return checkedItems;
        
        if (checkedItems instanceof Collection) 
        {
            Collection coll = (Collection) checkedItems;
            coll.remove(itemBean);
            coll.add(itemBean); 
            return coll;
        } 
        else if (checkedItems instanceof Object[]) 
        {
            Object[] sources = (Object[]) checkedItems;
            Object[] targets = new Object[sources.length+1];
            for (int i=0; i<sources.length; i++) { 
                targets[i] = sources[i];
            }
            targets[sources.length] = itemBean;
            return targets;
        }
        else {
            return itemBean;
        }
    }
    
    public Object detachItem(Object checkedItems, Object itemBean) 
    {
        if (checkedItems == null) return new ArrayList(); 
        if (itemBean == null) return checkedItems;
        
        if (checkedItems instanceof Collection) 
        {
            Collection coll = (Collection) checkedItems;
            coll.remove(itemBean);
            return coll;
        } 
        else if (checkedItems instanceof Object[]) 
        {
            int selIndex = -1;
            Object[] sources = (Object[]) checkedItems;
            for (int i=0; i<sources.length; i++) 
            { 
                if (sources[i] != null && sources[i].equals(itemBean))
                {
                    selIndex = i;
                    break;
                }
            }
            
            if (selIndex < 0) return sources;

            int counter = 0;            
            Object[] targets = new Object[Math.max(sources.length-1, 0)];
            for (int i=0; i<sources.length; i++) 
            { 
                if (i == selIndex) continue;
                
                targets[counter] = sources[i];
                counter += 1;
            }
            return targets;
        }
        else {
            return checkedItems;
        }
    }    
}
