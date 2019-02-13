/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.sigid.device;

import com.florentis.signature.SigCtl;
import com.rameses.rcp.common.SigIdModel;
import com.rameses.rcp.sigid.SigIdDevice;
import com.rameses.rcp.sigid.SigIdDeviceProvider;

/**
 *
 * @author wflores 
 */
public class STU430DeviceProvider implements SigIdDeviceProvider {

    private final static String PROVIDER_NAME = "STU-430";
    
    public String getName() { 
        return PROVIDER_NAME; 
    } 
    
    public String getDescription() { 
        return "Wacom STU-430 Device";
    }

    public SigIdDevice create( SigIdModel model ) {  
        return new STU430Device( model ); 
    } 
    
    public boolean test() { 
        try { 
            new SigCtl(); 
            return true; 
        } catch(Throwable t) {
            return false; 
        }
    }
}
