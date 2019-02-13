/*
 * CheckField.java
 *
 * Created on May 28, 2013, 11:14 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.swingx;

import javax.swing.JCheckBox;

/**
 *
 * @author wflores
 */
public class CheckField extends JCheckBox implements IComponent
{
    private boolean updateable = true;
    
    public CheckField() {
    }

    public Object getValue() { return isSelected(); }
    public void setValue(Object value) 
    {
        boolean selected = false;
        if (value instanceof Boolean) 
            selected = ((Boolean) value).booleanValue();
        else 
            selected = "true".equals(value+""); 
        
        setSelected(selected); 
    }

    public boolean isUpdateable() { return updateable; }
    public void setUpdateable(boolean updateable) {
        this.updateable = updateable;
    }
        
}
