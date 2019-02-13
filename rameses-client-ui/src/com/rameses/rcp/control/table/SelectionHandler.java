/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.control.table;

/**
 *
 * @author wflores
 */
public interface SelectionHandler {
    
    void setBeanValue( String name, Object value ); 
    void setStatusValue( String name, Object value ); 
    
    void notifyDepends( String name );
    
}
