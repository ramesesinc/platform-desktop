package com.rameses.client.notification.models;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.client.notification.*;

class GroupNotificationLoaderModel implements Runnable {

    @Script('Notification')
    def notification;

    @Notifier
    def notifier;

    def groups = [];

    def init() {
        NotificationLoader.add(this); 
        return '_close'; 
    } 

    def notifyHandler = {
        Inv.lookupOpeners('notification:handler', [:]).each{ 
            it.target = 'process'; 
            try { 
                Inv.invoke( it );  
            } catch(Throwable t) { 
                t.printStackTrace(); 
            } 
        } 
    } 

    void run() { 
        def env = ClientContext.currentContext.appEnv;
        if (!env['ws.host']) {
            println 'ws.host env not set';
            return;
        } 

        println 'group-notification-loader started...'; 

        WebsocketClient.open([ 
            protocol        : 'group', 
            host            : env['ws.host'], 
            maxConnection   : env['ws.maxConnection'],
            reconnectDelay  : env['ws.reconnectDelay'],
            maxIdleTime     : env['ws.maxIdleTime'], 

            onstart     : { 
                try { 
                    notifyHandler(); 
                } catch(Throwable t) { 
                    t.printStackTrace(); 
                } 
            }, 

            onmessage   : {o-> 
                if (!o?.groupid) return;
                if (groups.contains(o.groupid.toUpperCase())) { 
                    notifier.sendMessage(o); 
                } 
            } 
        ]); 
    }         
} 
