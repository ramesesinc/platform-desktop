/*
 * NotificationHandler.java
 *
 * Created on January 14, 2014, 9:51 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.framework;

/**
 *
 * @author wflores
 */
public interface NotificationHandler 
{
    void onMessage(Object data); 
    void onRead(Object data);
}
