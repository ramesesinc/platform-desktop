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

class FXMenuCategoryModel { 

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
    
    
    public String getMenuContextName() {
        return null;
    }
    
    def _menuNotifications = [:];
    
    public def getMenuNotificationService(def conn) {
        if( !_menuNotifications.containsKey(conn) ) {
            def  svc = InvokerProxy.instance.create("MenuNotificationService", null, conn );
            _menuNotifications.put( (conn==null)?"default": conn, svc );
        }
        return _menuNotifications.get( conn );
    }
    
    //overriddable methods
    public void loadDynamicItems( String menuContext, def subitems, def invokers ) {
        //load invokers 
    }
    
    String getTitle() {
        if( invoker.properties.formTitle ) {
            return ExpressionResolver.getInstance().evalString(invoker.properties.formTitle,this);
        }
        if( invoker.caption ) {
            return invoker.caption;
        }
         if( invoker.properties.windowTitle ) {
            return ExpressionResolver.getInstance().evalString(invoker.properties.windowTitle,this);
        }
        return "";
    }
    
    @FormId
    String getFormId() {
        if( invoker.properties.formId ) {
            return ExpressionResolver.getInstance().evalString(invoker.properties.formId,this);
        }
        return workunit.workunit.id + "_menu";
    }
    
    //this is used for getting the actions
    public String getFormName() {
        if( workunit.info.workunit_properties.formName ) {
            return workunit.info.workunit_properties.formName;
        }
        return getContext()+"_menu";
    }
    
    public String getContext() {
        String context = invoker.properties.context;
        if(context==null) {
            context = workunit?.info?.workunit_properties?.context;
        }
        if(!context.startsWith("menucategory/")){
            context= "menucategory/"+context;
        }
        if(context==null) throw new Exception("Please provide a context in category menu");
        return context;
    }
    
    def notifyHandlers = [:];
    
    void init() {
        //build model
        def model = buildModel();
        if(model) {
            //search if there are items that have a notificationid
           model.each { mo->
               mo.list.each { mv->
                   mv.subitems.findAll{mvx-> mvx.notificationid!=null}.each { mmv->
                       def nid = mmv.notificationid;
                       def nconn = mmv.connection;
                       def notifyHandler = [
                            onMessage: { msg ->
                                def svc = getMenuNotificationService( nconn );
                                def cnt = svc.getCount( [notificationid: nid] );
                                menuHtml.getWebEngine().call("updateCount", nid, cnt.count );                         
                            }
                       ] as DefaultNotificationHandler;
                       notifyHandlers.put( nid, notifyHandler );
                   }
               }
           }
        };
        
        def oneTimeLoad = {
            notifyHandlers.each { k,v->
                TaskNotificationClient.getInstance().register(k, v);
                TaskNotificationClient.getInstance().send(k, [:]);
            }
        };
        
        render( model, oneTimeLoad );
    }

    
    def buildModel() {
        def model = [];
        
        String headMenu = getContext();
        def folders = session.getFolders(headMenu);
        int i = 1;
        def entry = null;
        folders.each {f ->
            if(i==1) {
                entry = [ list:[], rowsize : 0 ];
                model << entry;
            }  
            def m = [subitems:[]];
            m.id = f.properties.id;
            m.caption = f.properties.caption;
            m.icon = f.properties.icon;
            if(!m.icon) m.icon = "home/icons/folder.png";
            
            def _id = headMenu + "/" + m.id;
            session.getFolders( _id ).each { sf->
                if(sf.invoker) {
                    def mm = [:];
                    mm.id = sf.toString();
                    mm.caption = sf.caption;
                    mm.index = sf.index;
                    def notid = sf.invoker.properties?.notificationid;
                    if(notid) {
                        mm.notificationid = notid;
                    }
                    def event = sf.invoker.properties?.event;
                    if( event ) {
                        mm.event = event;
                    }
                    mm.domain = sf.invoker.domain;
                    //add also the module name where it belongs
                    mm.modulename = sf.invoker.module.name?.trim();
                    mm.connection = sf.invoker?.module?.properties?.connection;
                    m.subitems << mm;
                    invokers.put( mm.id, sf.invoker ); 
                }       
            }
            
            //override by extending class
            loadDynamicItems( m.id, m.subitems, invokers );
            
            if( !m.subitems ) return;
            m.subitems = m.subitems.sort{ (!it.index) ? 0 : it.index };
            
            
            entry.list << m;
            if( m.subitems.size() > entry.rowsize ) {
                entry.rowsize = m.subitems.size();
            }
          
        }
        
        //correct the final
        model = model.findAll{ it.rowsize > 0 };
        return model;
    }
    
    void render( model, def oneTimeLoad ) {
        def buff = new StringBuilder(); 
        
        model.each { row ->
            row.list.each { m->
                buff.append("<div class=\"menu\">"); 
                buff.append("   <div class=\"icon\">"); 
                buff.append("      <img src=\"classpath://${m.icon}\" width=\"48\" height=\"48\">"); 
                buff.append("   </div>"); 
                buff.append("   <div class=\"category\">"); 
                buff.append("      <h1>${m.caption}</h1>"); 
                m.subitems.each { mi-> 
                    buff.append("  <div>");
                    buff.append("     <a href=\"\" class=\"link\" action=\"openItem\" id=\"${mi.id}\">");
                    buff.append("        <div class=\"label\"> ${mi.caption}");
                    if( mi.notificationid != null ) {
                        def elemid = mi.notificationid; 
                        buff.append("<span class=\"msgcount\" id=\"${elemid}\" xstyle=\"display:none;\"></span>");
                    }
                    buff.append("        </div>");
                    buff.append("     </a>");
                    buff.append("  </div>");
                }
                buff.append("   </div>"); 
                buff.append("</div>"); 
            }
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
        ] as HtmlViewModel; 
    }

    def openItem( param ) {
        if ( invokers == null || !param?.id ) return null; 
        
        def inv = invokers.get( param.id ); 
        if ( inv == null ) return null; 
        if( inv instanceof Opener ) {
            return inv;
        }
        else {
            def op = Inv.createOpener( inv, [:] ); 
            if ( !op.target ) op.target = 'window'; 
            return op; 
        }
    } 

    
    //debugging purposes
    def viewHtml() {
        MsgBox.alert( menuHtml ); 
    }

    void changeColor() {
        def color = MsgBox.prompt( "Enter color ")
        if(color) fontColor = color;
        init();
        binding.refresh();
    }
    
    boolean isExist() {
        return true;
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
                float: left;
                padding-left: 30px;
                padding-bottom: 20px;
            }
            .link{
                color: #0B76CA;
                text-decoration: none;
            }
            .link:hover{
                text-decoration: underline;
            }
            .label{
                font-size: 14px;
                padding-top: 3px;
            }
            .icon{
                width: 50px;
                display: inline-block;
                vertical-align: top;
            }
            .category{
                display: inline-block;
            }
            .msgcount{
                color: red;
                font-weight: bold;
                font-size: 12px;
                vertical-align: top;
            }
            .menu h1{
                font-size: 14px;
                margin: 0px;
                padding-top: 5px;
            }
            @media(min-width:380px){
                .menu{
                    width: 100%;
                    float: left;
                    padding-left: 30px;
                }
             }
            @media(min-width:792px){
                .menu{
                    width: 400px;
                    display: inline-block;
                    padding-left: 30px;
                }
             }
             @media(min-width:992px){
                .menu{
                    width: 400px;
                    display: inline-block;
                    padding-left: 30px;
                }
            }
            @media(min-width:1200px){
                .menu{
                    width: 400px;
                    display: inline-block;
                    padding-left: 30px;
                }
            }
        </style>
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
        buff.append( script ); 
        buff.append("</head>");
        buff.append("<body>");
        buff.append( content ); 
        buff.append("</body>");
        buff.append("</html>");
        return buff.toString(); 
    }
    
    @Close
    void onClose() {
        notifyHandlers.each { k,v->
            TaskNotificationClient.getInstance().unregister( v );
        }
    }
    
} 
