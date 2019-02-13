/*
 * LookupInvokerProvider.java
 *
 * Created on May 3, 2013, 4:29 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.common;

import java.util.List;
import java.util.Map;

/**
 *
 * @author wflores
 */
public interface LookupOpenerProvider 
{
    public Opener lookupOpener(String invokerType, Map params); 
    
    public List lookupOpeners(String invokerType, Map params); 
}
