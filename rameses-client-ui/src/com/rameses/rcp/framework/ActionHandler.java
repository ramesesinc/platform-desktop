/*
 * ActionHandler.java
 *
 * Created on June 24, 2013, 4:34 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.framework;

/**
 *
 * @author wflores
 */
public interface ActionHandler 
{
    void onBeforeExecute(); 
    void onAfterExecute(); 
}
