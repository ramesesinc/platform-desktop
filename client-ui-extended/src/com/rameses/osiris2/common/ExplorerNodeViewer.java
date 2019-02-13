/*
 * ExplorerNodeViewer.java
 *
 * Created on July 30, 2013, 7:07 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris2.common;

import com.rameses.rcp.common.Node;

/**
 *
 * @author wflores
 */
public interface ExplorerNodeViewer {
    
    void setNode(Node node);
    
    void updateView();
}
