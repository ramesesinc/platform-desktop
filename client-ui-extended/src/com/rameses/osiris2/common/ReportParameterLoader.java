/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris2.common;

import com.rameses.osiris2.client.Inv;
import com.rameses.rcp.common.Opener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author wflores 
 */
public class ReportParameterLoader {
    
    public void load( Map params ) {
        List loaders = new ArrayList(); 
        try {
            loaders = Inv.lookupOpeners("report-parameter-loader", params); 
        } catch(Throwable t) {;} 

        for ( Object o : loaders ) {
            if ( o instanceof Opener ) {
                try { 
                    Opener op = (Opener)o; 
                    op.setTarget("process"); 
                    Inv.invokeOpener( op ); 
                } catch(Throwable t) {
                    t.printStackTrace(); 
                }
            }
        } 
    }
}
