/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.sigid.device;

import com.rameses.rcp.common.SigIdModel;
import com.rameses.rcp.sigid.SigIdDevice;
import com.rameses.rcp.sigid.SigIdDeviceProvider;

/**
 *
 * @author wflores 
 */
public class TopazDeviceProvider implements SigIdDeviceProvider {

    private final static String PROVIDER_NAME = "Topaz";
    
    public String getName() { 
        return PROVIDER_NAME; 
    } 
    
    public String getDescription() { 
        return "Topaz Signature Device";
    }

    public SigIdDevice create( SigIdModel model ) {  
        return new TopazDevice( model ); 
    } 
    
    public boolean test() { 
        try { 
            return true; 
        } catch(Throwable t) {
            return false; 
        }
    }
}
