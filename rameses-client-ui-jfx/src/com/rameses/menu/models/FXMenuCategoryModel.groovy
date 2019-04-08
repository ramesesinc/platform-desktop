package com.rameses.menu.models;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

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
    
    /**************************************************************************
    * specify number of columns for the menu display. default is 2 cols
    ***************************************************************************/
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
    
    void init() {
        def model = buildModel();
        render( model );
    }
    
    def buildModel() {
        println 'build model pass 1'
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
                    m.subitems << mm;
                    invokers.put( mm.id, sf.invoker ); 
                }       
            }
            
            //override by extending class
            println 'load dynamic items started'
            loadDynamicItems( m.id, m.subitems, invokers );
            println 'load dynamic items ended'
            
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
            println 'build model ended'
        }
        
        //correct the final
        model = model.findAll{ it.rowsize > 0 };
        return model;
    }
    
    void render( model ) {
        println 'render model started'
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
                    buff.append("        <div class=\"raquo\">&raquo;</div>");
                    buff.append("        <div class=\"label\"> ${mi.caption}");
                    buff.append("           <span class=\"msgcount\" id=\"${mi.id}-count\" style=\"display:none;\"></span>");
                    buff.append("        </div>");
                    buff.append("     </a>");
                    buff.append("  </div>");
                }
                buff.append("   </div>"); 
                buff.append("</div>"); 
            }
        }
        
        menuHtml = [
            getValue: {
                return buildHtml( buff ); 
            }
        ] as HtmlViewModel 
        println 'render model ended'
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
        println 'build html started'
        String script = """ 
        <script> 
        function updateCount( elemid, value ) {
            var e = \$('#' + elemid); 
            if ( value && value > 0 ) {
                e.html(''+ value); 
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
        buff.append("<script src=\"classpath://com/rameses/menu/res/jquery.min.js\" type=\"text/javascript\"></script>"); 
        buff.append("<link href=\"classpath://com/rameses/menu/res/main.css\" type=\"text/css\" rel=\"stylesheet\">");
        buff.append( script ); 
        buff.append("</head>");
        buff.append("<body>");
        buff.append( content ); 
        buff.append("</body>");
        buff.append("</html>");
        println 'build html ended'
        return buff.toString(); 
    }
} 