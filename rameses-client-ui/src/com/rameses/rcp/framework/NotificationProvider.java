/*
 * NotificationProvider.java
 *
 * Created on January 24, 2014, 9:58 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.framework;

/**
 *
 * @author wflores 
 */
public interface NotificationProvider 
{
    void add(NotificationHandler handler);
    boolean remove(NotificationHandler handler);
    
    void sendMessage(Object data);
    void removeMessage(Object data); 
    void close();
}
