package com.rameses.menu.models;

import com.rameses.rcp.common.*;
import com.rameses.rcp.jfx.WebViewPane
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue
import javax.swing.JDialog;

import com.rameses.client.notification.socketio.*;

class FXHomeMenuModel { 

    @Binding
    def binding;
    
    @Controller
    def workunit;
        
    @Invoker
    def invoker;
    
    @Caller
    def caller;

    @Service("HomeMenuService")
    def menuService;
    
    def clientContext = com.rameses.rcp.framework.ClientContext.currentContext;
    def session = OsirisContext.getSession();
    def menuHtml;
    def invokers = [:];
    
    def icon;
    def items;
    
    def notifyHandlers = [];
    
    void init() {
        println "INIT : entering init in model";
        
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
        items.addAll(Inv.lookupActions('home.action'));
        
        //
        items.each { 
            if (!it.icon) it.icon = homeicon;
            if( it.properties?.notificationid !=null ) {
                def nid = it.properties?.notificationid;
                def notifyHandler = [
                    onMessage: { msg ->
                        println "receive message " +msg;
                        //menuHtml.getWebEngine().call("updateCount", id, cnt );                         
                    }
                ] as DefaultNotificationHandler;
                notifyHandlers << notifyHandler;
            } 
        }     

        def fin = {
            println "starting notifyhandlers";
            notifyHandlers.each {
                println "starting " + it;
                //it.start();
            }
        }
        render(items,fin);
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
    void render( items, onfinish ) {
        def buff = new StringBuilder(); 
        items.each { itm ->
            buff.append( """
                <div class=\"menu grow-shadow\">
                <a href=\"#\" action=\"openItem\" id=\"${itm.hashCode()}\">
                <img src=\"classpath://${itm.icon}\" width=\"48\">
                <label>${itm.caption}</label>""");
            if( itm.properties?.notificationid !=null ) {
                if(itm.properties?.notificationid ) {
                    buff.append( """<span class=\"msgcount\" id=\"${itm.properties.notificationid}\"></span>"""  );
                }
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
                if(onfinish!=null) {
                    onfinish();
                    onfinish = null;
                }
            }
        ] as HtmlViewModel 
    }
    
   
    
    def buildHtml( content ) {
        String script = """
        <style>
        body {
        font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto,
          Helvetica, Arial, sans-serif, "Apple Color Emoji", "Segoe UI Emoji",
          "Segoe UI Symbol";
          color: #444;
          font-size:10px;
        }
        .menu{
            display: inline-block;
            vertical-align: top;
            margin-bottom: 20px;
            width: 80px;
            height:80px;
            margin-left: 5px;
            text-align: center;
            padding: 10px 5px 5px 5px;
        }
        .menu a{
            text-decoration: none;
            color:#000;
            font-size: 12px;
        }
        .menu img{
            margin: 0 auto;
            display: flex;
            margin-bottom:15px;
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
        .grow-shadow label:hover {
            text-decoration: underline;
            color: blue;
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
        <script> 
        function updateCount( elemid, value ) {
            document.getElementById( elemid ).innerHTML = (value==0) ? : " ("+value+")";    
        } 
        </script>
        """; 
        def buff = new StringBuilder(); 
        buff.append("<html>");
        buff.append("<head>");
        buff.append( script ); 
        buff.append("</head>");
        buff.append("<body>");
        buff.append( content ); 
        buff.append("</body>");
        buff.append("</html>");
        return buff.toString(); 
    }
    
    void updateStatus() {
        def id = MsgBox.prompt("Enter id");
        if( !id ) return null;
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

/*    
    public void start() {
        TaskNotificationClient.getInstance().register(event, notifyHandler );
    }
    
    public void init() {
        TaskNotificationClient.getInstance().send(event, [:]);
    }
    
    public void stop() {
        TaskNotificationClient.getInstance().unregister( notifyHandler );
    }
*/
