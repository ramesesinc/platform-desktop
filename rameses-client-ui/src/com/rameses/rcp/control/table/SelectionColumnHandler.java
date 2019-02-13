/*
 * SelectionColumnHandler.java
 *
 * Created on June 5, 2013, 10:37 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control.table;

import com.rameses.rcp.common.Column;

/**
 *
 * @author wflores
 */
public class SelectionColumnHandler extends Column.TypeHandler 
{
    private static final long serialVersionUID = 1L;
    
    public final String getType() { return "selection"; }
}
