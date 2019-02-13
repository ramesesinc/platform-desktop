/*
 * CallbackHandlerAdapter.java
 *
 * Created on July 24, 2013, 9:51 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.common;

/**
 *
 * @author wflores
 */
public class CallbackHandlerAdapter implements CallbackHandler {
    
    public Object call() {
        return call((Object) null);
    }

    public Object call(Object arg) {
        return call(new Object[]{ arg });
    }

    public Object call(Object[] args) { 
        return null; 
    }
}
