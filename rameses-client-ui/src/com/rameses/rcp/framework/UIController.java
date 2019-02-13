package com.rameses.rcp.framework;

import java.util.Map;

public abstract class UIController {
    
    public UIController() {}
    
    public abstract View[] getViews();
    public abstract String getDefaultView();
    public abstract Object getCodeBean();
    
    public abstract Object init(Map params, String action, Object[] actionParams);
    
    public abstract String getId();
    public abstract void setId(String id);
    
    public abstract String getTitle();
    public abstract void setTitle(String title);
    
    public abstract String getName();
    public abstract void setName(String name);
    
    public abstract Map getInfo();
    public abstract boolean containsView( String name ); 

    public final Object init(Map params, String action) {
        return init(params, action, null); 
    }
        
    public static class View {
        
        private String name;
        private String template;
        
        public View(String name) {
            this(name, null);
        }
        
        public View(String name, String template) {
            this.name = name;
            this.template = template;
        }
        
        public String getName() {
            return name;
        }
        
        public String getTemplate() {
            return template;
        }
        
    }
        
}
