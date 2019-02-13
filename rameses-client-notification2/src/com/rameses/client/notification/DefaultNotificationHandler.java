/*
 * DefaultNotificationHandler.java
 *
 * Created on January 14, 2014, 3:49 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.client.notification;

import com.rameses.rcp.framework.*;
import java.util.Map;

/**
 *
 * @author wflores 
 */
public class DefaultNotificationHandler implements NotificationHandler
{
    public DefaultNotificationHandler() {
        
    }

    public void onMessage(Object data) {
        if (data instanceof Map) {
            onMessage((Map) data); 
        }
    }
    
    public void onRead(Object data) {
        if (data instanceof Map) {
            onRead((Map) data); 
        }        
    }
    
    
    protected void onMessage(Map data) {
    }

    protected void onRead(Map data) {
    }    
}
