package com.rameses.client.notification;

import com.rameses.rcp.annotations.*;
import com.rameses.rcp.common.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.util.Base64Cipher;

class CloudNotificationListController extends ListController 
{
    @FormTitle
    def title = 'Cloud Notification List';
    
    @Service('CloudNotificationService')
    def svc; 
    
    String serviceName  = 'CloudNotificationListService';
    boolean allowSearch = false;
    boolean allowCreate = false;
    boolean allowOpen   = false;
    
    def self = this; 
    def categories = [];
    def eventhandle;
    
    void doInit() {
        categories = [
            [name:'all', caption:'All Messages'], 
            [name:'pending', caption:'Pending'], 
            [name:'delivered', caption:'Delivered'], 
            [name:'failed', caption:'Failed'] 
        ]; 
    }
    
    public void beforeFetchList(Map params) {
        if (selectedMenu) { 
            params.putAll(selectedMenu); 
            params.filtertype = selectedMenu.name;
        }
    } 
    
    void beforeGetColumns( Map params ) {
        if ( selectedMenu ) {
            params.putAll( selectedMenu ); 
            params.filtertype = selectedMenu.name; 
        }
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
            if ( selectedMenu?.name == 'failed' ) {
                self.multiSelect = true; 
            } else {
                self.multiSelect = false; 
            } 
            self.reloadAll();
        } 
    ] as ListPaneModel;     
    
    boolean isHasSelectedItems() {
        def result = selectedValue;
        if ( !result ) return false; 
    }
    
    void reschedule() {
        def values = selectedValue;
        if ( !values ) throw new Exception('Please select at least one item'); 
        
        if (!MsgBox.confirm('You are about to reschedule the selected message(s). Continue?')) return; 
        
        def params = [ items:[] ]; 
        values.each { params.items << [objid: it.objid] }
        svc.rescheduleFailedMessages( params ); 
        reload(); 
    } 
} 
