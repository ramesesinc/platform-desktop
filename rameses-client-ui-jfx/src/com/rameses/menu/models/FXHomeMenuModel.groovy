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

    def clientContext = com.rameses.rcp.framework.ClientContext.currentContext;
    def session = OsirisContext.getSession();
    def menuHtml;
    def invokers = [:];
    def notifyHandlers = [];
    
    def icon;
    def items;
    def itemMap = [:];
    
    void init() {
        icon = 'classpath://images/logo.png'; 
        def appEnv = clientContext.appEnv; 
        def customfolder = appEnv['app.custom']; 
        def homeicon = 'images/' + customfolder + '/home-icon.png';  
        def custom_homeicon = clientContext.getResource(homeicon); 
        if (!custom_homeicon) homeicon = 'home/icons/folder.png'; 
        
        items = [];        
        
        def folders = session?.getFolders('home');
        println "folders size is " + folders.size();
        folders?.each {
            println it.caption + " " + it.invoker + " has items?" + hasItems(it) + " " + it.fullId;
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
        items.each { 
            if (!it.icon) it.icon = homeicon;
        }     
        render(items);
    }
    
    def openItem( param ) {
        def o = items.find{ it.hashCode().toString() == param.id };
        println "object found is " + o;
        
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
        println "getting subfolders for " + folder.fullId + " invoker is " + folder.invoker;
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
    void render( items ) {
        def buff = new StringBuilder(); 
        items.each { itm ->
            buff.append( """
                <div class=\"menu\">
                <img src=\"classpath://${itm.icon}\" width=\"48\">
                <a href=\"\" class=\"link\" action=\"openItem\" id=\"${itm.hashCode()}\">
                <p>${itm.caption}</p>
                </a>
                </div>
            """);
            /*
            if( mi.notificationid != null ) {
                def elemid = com.rameses.util.Encoder.MD5.encode(''+ mi.notificationid +'-count'); 
                buff.append("<span class=\"msgcount\" id=\"${elemid}\" style=\"display:none;\"></span>");
            }
            */
        }
        def _html = buildHtml( buff ); 
        menuHtml = [
            getValue: {
                return _html; 
            },
            onCompleted: {
                notifyHandlers.each {k,v->
                    v.renderHandler = { id, cnt->
                        menuHtml.getWebEngine().call("updateCount", id, cnt );     
                    }
                    v.init();
                }
                //complete rendering...
            }
        ] as HtmlViewModel 
    }
    
   
    
    def buildHtml( content ) {
        String script = """
        <style>
        body {
            color: #444;
            font-size:10px;
        }
        .menu{
            display: inline-block;
            vertical-align: top;
            margin-right: 15px;
            margin-bottom: 5px;
            width: 50px;
            flex-wrap: wrap;
        }
        .menu img{
            margin: 0 auto;
            display: flex;
        }
        .menu p{
            padding: 0px;
            margin: 3px 0px 0px 0px;
            align:center;
        }
        </style> 
        <script> 
        function updateCount( elemid, value ) {
            var e = \$('#' + elemid); 
            if ( value && value > 0 ) {\n\
                e.html(' ('+ value +')' ); 
                e.show(); 
            } 
            else {
                e.hide(); 
            } 
        } 
        </script>
        """; 
        def buff = new StringBuilder(); 
        buff.append("<html>");
        buff.append("<head>");
        buff.append("<script src=\"classpath://res/jquery.min.js\" type=\"text/javascript\"></script>"); 
        buff.append("<link href=\"classpath://res/main.css\" type=\"text/css\" rel=\"stylesheet\">");
        buff.append( script ); 
        buff.append("</head>");
        buff.append("<body>");
        buff.append( content ); 
        buff.append("</body>");
        buff.append("</html>");
        return buff.toString(); 
    }
    
    
    
    
    
    /*
    public String getMenuContextName() {
        return null;
    }
    
    def _menuNotificationSvc;
    public def getMenuNotificationService() {
        if(_menuNotificationSvc==null) {
            def connection = invoker?.module?.properties.connection;
            _menuNotificationSvc = InvokerProxy.instance.create("MenuNotificationService", null, connection );
        }
        return _menuNotificationSvc;
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
    
    public int getCols() {
        String _cols = invoker?.properties?.cols;
        if(_cols==null) {
            _cols = workunit?.info?.workunit_properties?.cols;
        }
        if(_cols) {
            try {
                return Integer.parseInt(_cols);
            }
            catch(ign){
                return 2;
            }
        }
        else {
            return 2;
        }
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
                       def eventName = mmv.event;
                       if( !notifyHandlers.containsKey(eventName)) {
                           def fxn = new FXMenuNotifyHandler(event: eventName);
                           fxn.notificationService = getMenuNotificationService();
                           notifyHandlers.put( eventName, fxn );
                       }
                       def nhandler = notifyHandlers.get(eventName);
                       nhandler.add( mmv.notificationid, mmv );
                   }
               }
           }
        };
        render(model);
        notifyHandlers.each{k,v-> 
            v.start() 
        };
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
            
            //reset the number
            if(i == cols) {
                i = 1;
            }
            else {
                i++;    
            }
        }
        
        //correct the final
        model = model.findAll{ it.rowsize > 0 };
        return model;
    }
    
    void render( model ) {
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
                        def elemid = com.rameses.util.Encoder.MD5.encode(''+ mi.notificationid +'-count'); 
                        buff.append("<span class=\"msgcount\" id=\"${elemid}\" style=\"display:none;\"></span>");
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
                notifyHandlers.each {k,v->
                    v.renderHandler = { id, cnt->
                        menuHtml.getWebEngine().call("updateCount", id, cnt );     
                    }
                    v.init();
                }
                //complete rendering...
            }
        ] as HtmlViewModel 
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
        <script> 
        function updateCount( elemid, value ) {
            var e = \$('#' + elemid); 
            if ( value && value > 0 ) {\n\
                e.html(' ('+ value +')' ); 
                e.show(); 
            } 
            else {
                e.hide(); 
            } 
        } 
        </script>
        """; 
        
        def buff = new StringBuilder(); 
        buff.append("<html>");
        buff.append("<head>");
        buff.append("<script src=\"classpath://res/jquery.min.js\" type=\"text/javascript\"></script>"); 
        buff.append("<link href=\"classpath://res/main.css\" type=\"text/css\" rel=\"stylesheet\">");
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
        notifyHandlers.each{k,v-> v.stop(); };    
    }
    */
} 

/*
class FXMenuNotifyHandler {

    String event;
    def dataHandler;
    def notifications = [:];
    def menuHtml;
    def notificationService;
    def renderHandler;
    
    public void add(String id, def obj) {
        notifications.put(id, obj);
    }

    //boolean refreshingScreen = false;
    //def blockingQueue = new LinkedBlockingQueue();
    def notifyHandler = [
        onMessage: { msg ->
            def itms = [];
            notifications*.value.each {
                itms << [id: it.notificationid, domain: it.domain, count: 0 ];
            }
            def result = notificationService.fetchNotifications([event:event, items: itms ]);
            result.each {
                def obj = notifications.get( it.id ); 
                if(obj) {
                    obj.count = it.count;
                    if(obj.count==null) obj.count = 0;
                    def elemid = com.rameses.util.Encoder.MD5.encode(''+ it.id +'-count'); 
                    renderHandler( elemid, obj.count );
                }
            }
            //blockingQueue.put(msg);
            //if(!refreshingScreen) return;
            //while(!blockingQueue.empty()) {
            //    blockingQueue.void(); //clear all elements
            //updateCountHandler();
            //}
            //refreshingScreen = false;
        }
    ] as DefaultNotificationHandler;
    
    public void start() {
        TaskNotificationClient.getInstance().register(event, notifyHandler );
    }
    
    public void init() {
        TaskNotificationClient.getInstance().send(event, [:]);
    }
    
    public void stop() {
        println  " stop listening in fx menu category " + event;        
        TaskNotificationClient.getInstance().unregister( notifyHandler );
    }
}
*/