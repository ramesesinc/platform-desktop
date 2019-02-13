/*
 * ViewHandler.java
 *
 * Created on October 3, 2013, 3:30 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.common;

/**
 *
 * @author wflores
 */
public interface ViewHandler 
{
    void activatePage(Object binding, Object pagename);
    void afterRefresh(Object binding, Object pagename);
}
