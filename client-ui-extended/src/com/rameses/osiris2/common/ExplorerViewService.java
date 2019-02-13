/*
 * ExplorerViewService.java
 *
 * Created on July 25, 2013, 4:35 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris2.common;

import java.util.List;
import java.util.Map;

/**
 *
 * @author wflores
 */
public interface ExplorerViewService extends ListService { 
    
    List<Map> getNodes(Map params); 
    
}
