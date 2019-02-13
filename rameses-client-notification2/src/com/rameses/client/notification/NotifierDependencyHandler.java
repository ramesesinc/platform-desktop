/*
 * NotifierDependencyHandler.java
 *
 * Created on February 8, 2014, 11:48 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.client.notification;

/**
 *
 * @author wflores
 */
public class NotifierDependencyHandler extends NotificationDependencyHandler 
{
    public Class getAnnotation() { 
        return com.rameses.rcp.annotations.Notifier.class; 
    }
}
