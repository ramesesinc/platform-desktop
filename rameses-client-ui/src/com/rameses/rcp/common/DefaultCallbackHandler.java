/*
 * DefaultCallbackHandler.java
 *
 * Created on June 11, 2013, 11:57 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.common;

/**
 *
 * @author wflores
 */
public class DefaultCallbackHandler implements CallbackHandler
{
    
    public DefaultCallbackHandler() {
    }

    public Object call() {
        return null;
    }

    public Object call(Object arg) {
        //return call((arg == null? null: new Object[]{arg}));
        return call(new Object[]{ arg });
    }

    public Object call(Object[] args) { 
        return null; 
    }    
}
