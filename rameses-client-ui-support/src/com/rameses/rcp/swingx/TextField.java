/*
 * TextField.java
 *
 * Created on May 28, 2013, 10:36 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.swingx;

import java.awt.event.FocusEvent;
import javax.swing.JTextField;

/**
 *
 * @author wflores
 */
public class TextField extends JTextField implements IComponent
{
    private boolean updateable = true;
    
    public TextField() {
    }

    protected void processFocusEvent(FocusEvent e) 
    {
        if (e.getID() == FocusEvent.FOCUS_GAINED) {
            selectAll();
        }
        super.processFocusEvent(e); 
    }
    
    public Object getValue() 
    {
        String text = getText();
        if (text == null || text.length() == 0) return null; 
        
        return text; 
    }

    public void setValue(Object value) {
        setText((value == null? "": value.toString())); 
    }
    
    
    public boolean isUpdateable() { return updateable; }
    public void setUpdateable(boolean updateable) {
        this.updateable = updateable;
    }     
}
