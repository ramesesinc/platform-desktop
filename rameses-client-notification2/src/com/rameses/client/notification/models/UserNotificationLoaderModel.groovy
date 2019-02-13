package com.rameses.client.notification.models;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.client.notification.*;

class UserNotificationLoaderModel implements Runnable
{   
    @Script('Notification')
    def notification;

    @Notifier
    def notifier;

    def recipientid;

    def init() {
        NotificationLoader.add(this); 
        return '_close';
    } 
    
    void run() { 
        def env = ClientContext.currentContext.appEnv;
        if (!env['ws.host']) {
            println 'ws.host env not set';
            return;
        }

        println 'user-notification-loader started...';            
        def headers = ClientContext.currentContext.headers;
        recipientid = headers.USERID; 

        WebsocketClient.open([ 
            protocol        : 'user', 
            host            : env['ws.host'], 
            maxConnection   : env['ws.maxConnection'],
            reconnectDelay  : env['ws.reconnectDelay'],
            maxIdleTime     : env['ws.maxIdleTime'], 

            onstart     : { 
                try { 
                    def svc = notification.service; 
                    svc.getNotified([recipientid: recipientid]); 
                } catch(Throwable t) { 
                    t.printStackTrace(); 
                } 
            }, 

            onmessage   : {o-> 
                if (o?.recipientid == recipientid) { 
                    notifier.sendMessage(o); 
                } 
            } 
        ]);             
    }        
} 
