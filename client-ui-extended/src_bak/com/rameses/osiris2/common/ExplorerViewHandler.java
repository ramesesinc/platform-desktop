/*
 * ExplorerViewHandler.java
 *
 * Created on August 2, 2013, 11:58 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris2.common;

import com.rameses.rcp.common.Node;
import com.rameses.rcp.common.Opener;

/**
 *
 * @author wflores
 */
public interface ExplorerViewHandler {
    
    void setParent(ExplorerViewController_old parentController); 
    
    void setNode(Node node); 
    
    void updateView(); 
    
}
