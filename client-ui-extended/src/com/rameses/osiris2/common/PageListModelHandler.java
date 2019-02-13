/*
 * PageListModelHandler.java
 *
 * Created on August 11, 2013, 10:52 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris2.common;

/**
 *
 * @author wflores
 */
interface PageListModelHandler extends ListModelHandler {

    void moveFirstPage();
    
    void moveBackPage();
    
    void moveNextPage();
    
    void moveLastPage();
    
    void moveBackRecord();
    
    void moveNextRecord();    
        
}
