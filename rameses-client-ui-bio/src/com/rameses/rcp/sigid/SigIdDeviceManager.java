/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.sigid;

import com.rameses.rcp.framework.ClientContext;
import com.rameses.util.Service;
import com.rameses.util.SharedPreferences;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author wflores 
 */
public final class SigIdDeviceManager { 
    
    private final static String PREF_NAME = "signature-device-name";
    
    private static Map<String,SigIdDeviceProvider> providers = new HashMap(); 
    private static SigIdDeviceProvider defaultProvider; 
    
    static {
        ClassLoader cloader = ClientContext.getCurrentContext().getClassLoader();
        Iterator itr = Service.providers( SigIdDeviceProvider.class, cloader ); 
        while (itr.hasNext()) {
            SigIdDeviceProvider prov = (SigIdDeviceProvider) itr.next();
            providers.put( prov.getName(), prov );
        }
        
        if ( !providers.isEmpty() ) {
            String deviceName = SharedPreferences.getDefault().getString(PREF_NAME); 
            SigIdDeviceProvider devprov = providers.get( deviceName ); 
            if ( devprov == null ) {
                devprov = providers.values().iterator().next(); 
            }
            defaultProvider = devprov; 
        }
        String provname = (defaultProvider==null? null: defaultProvider.getName()); 
        SharedPreferences.getDefault().getEditor().put(PREF_NAME, provname).save(); 
    }
    
    public static Collection<SigIdDeviceProvider> getProviders() { 
        return providers.values(); 
    } 
    
    public static SigIdDeviceProvider getProvider() {
        return defaultProvider; 
    }
    
    public static synchronized void setProvider( String name ) { 
        if ( name == null || name.trim().length()==0 ) return; 
        
        SigIdDeviceProvider prov = providers.get( name ); 
        if ( prov == null ) throw new RuntimeException("provider '"+ name +"' not found");
        
        setProvider( prov ); 
    } 
    public static synchronized void setProvider( SigIdDeviceProvider prov ) { 
        defaultProvider = prov;
        if ( defaultProvider == null && !providers.isEmpty()  ) {
            String deviceName = SharedPreferences.getDefault().getString( PREF_NAME ); 
            SigIdDeviceProvider devprov = providers.get( deviceName ); 
            if ( devprov == null ) {
                devprov = providers.values().iterator().next(); 
            }
            defaultProvider = devprov; 
        } 
        String provname = (defaultProvider==null? null: defaultProvider.getName()); 
        SharedPreferences.getDefault().getEditor().put(PREF_NAME, provname).save(); 
    } 
}
