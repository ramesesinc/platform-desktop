package com.rameses.client.notification;

import com.rameses.rcp.annotations.*;
import com.rameses.rcp.common.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.rcp.framework.ClientContext;

class NotificationListController extends ExplorerViewController 
{
    private ExplorerViewService svc;
    
    boolean allowSearch = false;
    String entityName = "";
    
    def groupnames;
    
    void init() {
        def headers = ClientContext.currentContext.headers;
        groupnames = headers.ROLES?.collect{k,v-> "'"+k+"'" }.join(',');
        if (!groupnames) groupnames = "''";
    }
    
    public ExplorerViewService getService() {
        if (svc == null) {
            svc = (ExplorerViewService) NotificationServiceProxy.create(ExplorerViewService.class); 
        }
        return svc;
    }
    
    public void beforeFetchList(Map params) {
        params.userid = ClientContext.currentContext.headers.USERID;
        params.groupnames = groupnames;
    } 
    
    protected List getNodes(Map params) {
        def nodes = [
            [name:'user', caption:'My Notifications'] 
        ];
        def list = super.getNodes(params); 
        if (list) nodes.putAll(list);
        
        return nodes; 
    }
} 
