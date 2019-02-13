/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.framework;

/**
 *
 * @author wflores 
 */
public interface UIHandler { 
    
    void bind( UIEvent e );
    void unbind( UIEvent e ); 
    
    void refresh( UIEvent e );
}
