package com.rameses.menu.models;

import com.rameses.rcp.common.*;
import com.rameses.rcp.jfx.WebViewPane
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.client.notification.socketio.*;
import com.rameses.osiris2.*;

class FXHomeMenuModel { 

    @Binding
    def binding;
    
    @Controller
    def workunit;
        
    @Invoker
    def invoker;
    
    @Caller
    def caller;

    def clientContext = com.rameses.rcp.framework.ClientContext.currentContext;
    def session = OsirisContext.getSession();
    def menuHtml;
    def invokers = [:];
    
    def icon;
    def items;
    
    def notifyHandlers = [:];
    
    def _menuNotifications = [:];
    public def getMenuNotificationService(def conn) {
        if( !_menuNotifications.containsKey(conn) ) {
            def  svc = InvokerProxy.instance.create("MenuNotificationService", null, conn );
            _menuNotifications.put( (conn==null)?"default": conn, svc );
        }
        return _menuNotifications.get( conn );
    }
    
    void init() {
        icon = 'classpath://images/logo.png'; 
        def appEnv = clientContext.appEnv; 
        def customfolder = appEnv['app.custom']; 
        def homeicon = 'images/' + customfolder + '/home-icon.png';  
        def custom_homeicon = clientContext.getResource(homeicon); 
        if (!custom_homeicon) homeicon = 'home/icons/folder.png'; 
        
        items = [];        
        
        def folders = session?.getFolders('home');
        folders?.each {
            if (it.invoker == null) { 
                def result = hasItems(it); 
                if (result) { 
                    def map = [:];
                    map.putAll( it.properties );
                    map.userObject = it; 
                    items.add( map );
                }
            } 
        } 
        //this if for items tagged as home.action
        for( ia in Inv.lookupActions('home.action') ) {
            if( session.getSecurityProvider().checkPermission( ia.invoker.domain, "*", null  ) == true ) {
                items.add( ia );
            }
        }
        //
        items.each { 
            if (!it.icon) it.icon = homeicon;
            if( it.properties?.notificationid !=null ) {
                def nid = it.properties?.notificationid;
                def conn = null;
                if( it instanceof InvokerAction ) {
                    conn = it.invoker?.module.name?.trim();
                }
                def notifyHandler = [
                    onMessage: { msg ->
                        def svc = getMenuNotificationService( conn );
                        def cnt = svc.getCount( [notificationid: nid] );
                        menuHtml.getWebEngine().call("updateCount", nid, cnt.count );                         
                    }
                ] as DefaultNotificationHandler;
                notifyHandlers.put( nid, notifyHandler);
            } 
        }     

        def oneTimeLoad = {
            notifyHandlers.each { k,v->
                TaskNotificationClient.getInstance().register(k, v);
                //fire first so we can update the task count after loading
                TaskNotificationClient.getInstance().send(k, [:]);
            }
        };
        render(items, oneTimeLoad);
    }
    
    @Close
    void onClose() {
        notifyHandlers.each{k,v-> 
            TaskNotificationClient.getInstance().unregister( v ); 
        };    
    }
    
    def openItem( param ) {
        def o = items.find{ it.hashCode().toString() == param.id };
        def userobj = null; 
        try {
            userobj = o.userObject;
        } catch(Throwable t) {
            userobj = o; 
        }
        if (userobj instanceof com.rameses.osiris2.Folder) {
            def opener = Inv.lookupOpener('home_group_action', [folder: userobj]); 
            if (!opener.target) opener.target = 'window'; 
            binding.fireNavigation( opener ); 
        } else {
            return o.execute();
        }
    } 
    
    private boolean hasItems( def folder ) {
        if (folder.invoker != null) return true;
        def list = session.getFolders(folder.fullId); 
        if (list) {
            for (o in list) {
                if (hasItems(o)) return true;  
            }
        } 
        return false; 
    }
    
    public boolean hasChildren( String name ) { 
        def folders = session?.getFolders( name ); 
        if ( folders == null ) return;
        
        for ( o in folders ) {
            if ( hasItems(o)) return true; 
        }
        return false; 
    }
    
    public def getEnv() { 
        return clientContext.headers; 
    } 
    
    //html portions
    void render( items, def oneTimeLoad ) {
        def buff = new StringBuilder(); 
        items.each { itm ->
            buff.append( """
                <div class=\"menu grow-shadow\">
                    <a href=\"#\" action=\"openItem\" id=\"${itm.hashCode()}\">
                        <img src=\"classpath://${itm.icon}\" width=\"48\" style=\"min-height:48px; max-width:48px;\" />
                        <label>${itm.caption}</label>""");
            if( itm.properties?.notificationid !=null ) {
                buff.append( """<span class=\"msgcount\" id=\"${itm.properties.notificationid}\"></span>"""  );
            }
            buff.append("""
                    </a>
                </div>
            """);
        }

        def _html = buildHtml( buff ); 
                
        menuHtml = [
            getValue: {
                return _html; 
            },
            onCompleted: {
                if(oneTimeLoad !=null) {
                    //we have to remove and nullify this so it wont be executed again.
                    //it seems this hloader is cached by the html view model.
                    oneTimeLoad();
                    oneTimeLoad = null;
                }
            }
        ] as HtmlViewModel 
    }
    
   
    
    def buildHtml( content ) {
        String styles = """
        <style>
        body {
            color: #444;
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, 
            Helvetica, Arial, sans-serif, "Apple Color Emoji", "Segoe UI Emoji", 
            "Segoe UI Symbol";
        }
        .menu-container{
             display:flex;
             flex-wrap:wrap;
        }
        .menu{
            width: 94px;
            text-align: center;
            vertical-align: top;
            margin-bottom: 5px;
            margin-top: 10px;
            padding: 8px 3px 8px 3px;
        }        
        .menu a{
            color: #000;
            font-size: 11px;
            text-decoration: none;
            -webkit-user-select: none;  
            -moz-user-select: none;    
            -ms-user-select: none;      
            user-select: none;
        }
        .menu img{
            margin: 0 auto;
            display: flex;
        }
        .menu p{
            margin: 3px 0px 0px 0px;
        }
        .grow-shadow {
            -webkit-transition: all .2s ease-in-out;
                    transition: all .2s ease-in-out;
        }
        .grow-shadow:hover {
            -webkit-transform: scale(1.1);
            -ms-transform: scale(1.1);
            transform: scale(1.1);
            box-shadow: 0px 5px 9px #C7C7C7;
            background: #eaf1f9;
        }
        .active {
            -webkit-transform: scale(1.1);
            -ms-transform: scale(1.1);
            transform: scale(1.1);
            box-shadow: 0px 5px 9px #C7C7C7;
            background: #b8daff;;
        }

        .menu .msgcount {
            color: red; font-weight: bold;
        }
        </style> 
        """; 
        
        def scripts = """ 
        <script> 
        function updateCount( elemid, value ) {
            if( value != null && value > 0 ) {
                value = " (" + value + ")"; 
            }
            else {
                value = "";
            }    
            var v = document.getElementById( elemid );
            if( v!=null ) v.innerHTML = value;  
        } 
        </script>
        """; 
        
        def buff = new StringBuilder(); 
        buff.append("<html>");
        buff.append("<head>");
        buff.append( styles ); 
        buff.append( scripts ); 
        buff.append("</head>");
        buff.append("<body>");
        buff.append('<div class="menu-container">');
        buff.append( content ); 
        buff.append("</div>");
        buff.append("</body>");
        buff.append("</html>");
        return buff.toString(); 
    }
    
    void updateStatus() {
        def id = MsgBox.prompt("Enter id");
        if( !id ) return null;
        TaskNotificationClient.getInstance().send(id, [domain:id]);
    }

    void deductStatus() {
        def id = MsgBox.prompt("Enter id");
        if( !id ) return null;
        
    }
    
    def viewHtml() {
        //MsgBox.alert( menuHtml ); 
        println ""+menuHtml.value;
    }
} 
