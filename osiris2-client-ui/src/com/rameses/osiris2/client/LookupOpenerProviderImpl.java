/*
 * LookupInvokerProviderImpl.java
 *
 * Created on May 3, 2013, 4:30 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris2.client;

import com.rameses.rcp.common.LookupOpenerProvider;
import com.rameses.rcp.common.Opener;
import java.util.List;
import java.util.Map;

/**
 *
 * @author wflores
 */
public class LookupOpenerProviderImpl implements LookupOpenerProvider
{
    public Opener lookupOpener(String invokerType, Map params) { 
        return InvokerUtil.lookupOpener(invokerType, params); 
    }

    public List lookupOpeners(String invokerType, Map params) {
        return InvokerUtil.lookupOpeners(invokerType, params); 
    }
}
