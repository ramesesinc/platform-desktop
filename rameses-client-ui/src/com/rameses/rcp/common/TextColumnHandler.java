/*
 * TextColumnHandler.java
 *
 * Created on May 21, 2013, 11:46 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.common;

/**
 *
 * @author wflores
 */
public class TextColumnHandler extends Column.TypeHandler implements PropertySupport.TextPropertyInfo
{   
    private static final long serialVersionUID = 1L;
    
    public TextColumnHandler(){
    } 
    
    public String getType() { return "text"; }    
}
