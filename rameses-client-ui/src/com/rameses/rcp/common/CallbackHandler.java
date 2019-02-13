/*
 * CallbackHandler.java
 *
 * Created on June 11, 2013, 11:56 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.common;

/**
 *
 * @author wflores
 */
public interface CallbackHandler 
{
    Object call();
    Object call(Object arg);
    Object call(Object[] args);
}
