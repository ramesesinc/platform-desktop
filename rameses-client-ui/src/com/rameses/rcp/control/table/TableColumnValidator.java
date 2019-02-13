/*
 * TableColumnValidator.java
 *
 * Created on May 9, 2013, 9:41 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control.table;

import com.rameses.common.PropertyResolver;
import com.rameses.rcp.common.Column;
import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.rcp.ui.Validatable;
import com.rameses.rcp.util.ActionMessage;
import com.rameses.util.ValueUtil;

/**
 *
 * @author wflores
 */
public class TableColumnValidator implements Validatable
{
    private ActionMessage actionMessage = new ActionMessage();
    private Binding binding;
    private Column column; 
    
    public TableColumnValidator(Binding binding, Column column) 
    {
        this.binding = binding;
        this.column = column;
    }

    public String getCaption() { return column.getCaption(); } 
    public void setCaption(String caption) {}

    public boolean isRequired() { return column.isRequired(); } 
    public void setRequired(boolean required) {}

    public ActionMessage getActionMessage() { return actionMessage; }
    
    public int compareTo(Object o) { return 0; }
    
    public void validateInput() 
    {
        actionMessage.clearMessages();
        
        if (!column.isEditable()) return;
        
        Object bean = binding.getBean();
        if (bean == null) return;
        
        if (isRequired()) 
        {
            if (ValueUtil.isEmpty(column.getName()))
                actionMessage.addMessage("1001", "Column name is required.", new Object[] {});
            
            else 
            {
                PropertyResolver resolver = PropertyResolver.getInstance();
                if (resolver.getProperty(bean, column.getName()) == null) 
                    actionMessage.addMessage("1001", "{0} is required.", new Object[] { getCaption() });
            }
        }        
    }    
}
