/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.common;

/**
 *
 * @author wflores
 */
public interface BatchProcessingHandler {
    
    void onFinished();
    void onError( Object stat );
    void onRefresh( Object stat ); 
}
