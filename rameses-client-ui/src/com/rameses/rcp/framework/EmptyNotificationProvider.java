/*
 * EmptyNotificationProvider.java
 *
 * Created on March 6, 2014, 10:15 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.framework;

/**
 *
 * @author wflores
 */
class EmptyNotificationProvider implements NotificationProvider 
{
    public void add(NotificationHandler handler) {
        System.out.println("please specify an implementation of NotificationProvider");
    }

    public boolean remove(NotificationHandler handler) {
        System.out.println("please specify an implementation of NotificationProvider");
        return false; 
    }

    public void sendMessage(Object data) {
        System.out.println("please specify an implementation of NotificationProvider");
    }

    public void removeMessage(Object data) {
        System.out.println("please specify an implementation of NotificationProvider");
    }

    public void close() {
        System.out.println("please specify an implementation of NotificationProvider");
    }
}
