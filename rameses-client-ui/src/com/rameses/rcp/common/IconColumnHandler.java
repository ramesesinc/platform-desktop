/*
 * IconColumnHandler.java
 *
 * Created on August 2, 2013, 4:50 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.common;

/**
 *
 * @author wflores
 */
public class IconColumnHandler extends Column.TypeHandler {

    private static final long serialVersionUID = 1L;
    
    public IconColumnHandler(){
    } 
    
    public String getType() { return "icon"; }   
}
