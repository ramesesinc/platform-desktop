/*
 * LookupHandler.java
 * Created on July 25, 2011, 4:07 PM
 *
 * Rameses Systems Inc
 * www.ramesesinc.com
 *
 */

package com.rameses.rcp.common;

/**
 *
 * @author wflores
 */
public interface LookupHandler 
{
    
    Object getOpener();
    
    void onselect(Object item); 
    
}
