/*
 * RendererSupport.java
 *
 * Created on May 30, 2013, 5:13 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control.table;

import com.rameses.rcp.constant.TextCase;

/**
 *
 * @author wflores
 */
class RendererSupport 
{
    
    public RendererSupport() {
    }

    public String convertValue(TextCase textCase, Object value) 
    {
        if (value == null || textCase == null) return null; 
        
        return textCase.convert(value.toString()); 
    }
    
}
