/*
 * LookupOpenerSupport.java
 *
 * Created on May 17, 2013, 2:10 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.common;

import com.rameses.rcp.framework.ClientContext;
import com.rameses.util.Service;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author wflores
 */
public class LookupOpenerSupport 
{
    private static LookupOpenerSupport instance;
    
    static {
        instance = new LookupOpenerSupport();
        instance.initialize(); 
    }
    
    public static Opener lookupOpener(String invokerType, Map params) 
    {
        if (instance.provider == null) return null; 
        
        return instance.provider.lookupOpener(invokerType, params); 
    }

    public static List lookupOpeners(String invokerType, Map params) 
    {
        if (instance.provider == null) return new ArrayList();
        
        return instance.provider.lookupOpeners(invokerType, params); 
    }
    
    
    private LookupOpenerProvider provider;
    
    private LookupOpenerSupport() {
    }
    
    private void initialize() 
    {
        Iterator itr = Service.providers(LookupOpenerProvider.class, ClientContext.getCurrentContext().getClassLoader());  
        if (itr.hasNext()) {
            provider = (LookupOpenerProvider) itr.next();
        } 
    }
}
