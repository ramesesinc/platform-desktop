/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.common;

/**
 *
 * @author wflores
 */
public interface SigIdResult {
    
    byte[] getImageData(); 
    String getSigString(); 
    String getKeyString(); 
    int getKeyReceipt(); 
    int getNumberOfStrokes(); 
    
    void dump();
}
