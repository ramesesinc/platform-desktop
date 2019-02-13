package com.rameses.client.notification;

import com.rameses.rcp.annotations.*;
import com.rameses.rcp.common.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.util.Base64Cipher;

class NotificationListController extends ListController 
{
    @Notifier
    def notifier;
    
    @FormTitle
    def title = 'Notifications';
    
    String serviceName  = 'NotificationService';
    boolean allowSearch = false;
    boolean allowCreate = false;
    
    def categories = [];
    def eventhandle;

    @Close
    void onclose() { 
        eventhandle?.unregister(); 
    } 
    
    void init() {
        def userid = ClientContext.currentContext.headers.USERID;
        categories << [name:userid, caption:'All Notifications', type:'all'];        
        Inv.lookup('notification-group').each{
            def groupname = it.properties.group;
            if (groupname) {
                def caption = (it.caption == null? groupname: it.caption);
                categories << [name: groupname.toUpperCase(), caption:caption, type:'group'];
            } 
        } 
        
        eventhandle = notifier.register({ reload() });
    }
    
    public void beforeFetchList(Map params) {
        params.userid = ClientContext.currentContext.headers.USERID;
        if (selectedMenu) { 
            params.putAll(selectedMenu); 
            params.recipienttype = selectedMenu.type;
            
            if (selectedMenu.type == 'all') {
                params.recipienttype = 'my-messages';
                //params.recipients = categories.collect{ it.name }; 
            } else if (selectedMenu.type == 'user') {
                params.recipientid = params.userid;
            } else if (selectedMenu.type == 'group') { 
                params.recipientid = selectedMenu.name; 
            } 
        }
    } 
    
    public def open() {
        def item = selectedEntity;
        if (item != null) {
            item.type = selectedMenu?.type;
            if (!item.filetype) item.filetype='notification-item';
        }     
        def data = new Base64Cipher().decode(item.data); 
        item.data = data;
        item.taskid = data?.taskid; 
        return super.open();
    } 
    
    void removeMessage() {
        if (!MsgBox.confirm('You are about to remove this message. Continue?')) return;
        
        if (!notifier) {
            MsgBox.alert('@Notifier is not set'); 
            return;
        }
        
        def item = selectedEntity;
        if (item) notifier.removeMessage(item);
    }
    
    def selectedMenu;    
    def menuHandler = [
        getDefaultIcon: {
            return 'Tree.closedIcon'; 
        },         
        getItems: { 
            return categories;
        }, 
        onselect: {o->
            selectedMenu = o;
            reload();
        } 
    ] as ListPaneModel; 
    
    
    public void onMessage( data ) { 
        def np = ctx.getNotificationProvider(); 
        if ( np == null ) return; 

        def env = ctx.getHeaders(); 
        if ( data.recipienttype == 'user' && data.recipientid == env?.USERID) {
            np.sendMessage( data ); 
            
        } else if ( data.recipienttype == 'group' && groups.contains(data.recipientid.toString().toUpperCase()) ) {  
            np.sendMessage( data ); 
        }
    } 
    
    public void onRead( data ) { 
        //
    }     
} 
