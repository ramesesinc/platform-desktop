/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.sigid;

import com.rameses.rcp.common.SigIdModel;

/**
 *
 * @author wflores 
 */
public interface SigIdDeviceProvider {
    
    public String getName();
    public String getDescription();
    
    public SigIdDevice create( SigIdModel model ); 
    
    public boolean test();
}
