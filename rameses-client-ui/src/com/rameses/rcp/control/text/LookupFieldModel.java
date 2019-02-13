/*
 * LookupFieldModel.java
 *
 * Created on April 29, 2013, 5:18 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control.text;

import com.rameses.rcp.common.*;

/**
 *
 * @author wflores
 */
public class LookupFieldModel 
{
    private LookupSelector selector;
    private String returnItemKey; 
    private String returnItemValue;
    private String returnFields;
    
    public LookupSelector getSelector() { return selector; }  
    public void setSelector(LookupSelector selector) {
        this.selector = selector; 
    }
    
    public String getReturnItemKey() { return returnItemKey;  }
    public void setReturnItemKey(String returnItemKey) {
        this.returnItemKey = returnItemKey; 
    }

    public String getReturnItemValue() { return returnItemValue; }
    public void setReturnItemValue(String returnItemValue) {
        this.returnItemValue = returnItemValue;
    }
    
    public String getReturnFields() { return returnFields; }
    public void setReturnFields(String returnFields) {
        this.returnFields = returnFields;
    }

    public Object getValue() { return null; } 
        
    public boolean show(String searchtext) {
        return true; 
    }   
}
