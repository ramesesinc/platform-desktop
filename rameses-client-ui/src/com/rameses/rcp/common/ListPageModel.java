/*
 * ListPageModel.java
 *
 * Created on May 15, 2013, 5:57 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.common;

/**
 *
 * @author wflores
 */
public interface ListPageModel 
{
    void moveFirstPage();
    void moveBackPage();
    void moveNextPage();
    void moveLastPage();
    
    void moveBackRecord();
    void moveNextRecord();
    void moveNextRecord(boolean includesEmptyItem);
}
