package com.rameses.rcp.common;

import com.rameses.rcp.framework.UIController;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author jaycverg
 */
public class Opener 
{    
    private static final long serialVersionUID = 1L;
    public final static String INVOKER_OBJECT_KEY = "_INVOKER_";
    
    private String id;
    private String caption;
    private String name;
    private String action;
    private String target;
    private String outcome;
    private String permission;
    private boolean modal = true;
    private Map params = new HashMap();
    
    private UIController controller;
    private UIController caller;
    private String role;
    private String domain;
    
    /*
     *  applies to popup windows only
     *  true: closes the active dialog when this opener is visible
     *  false: nothing to do with the active dialog. (Default) 
     */
    private boolean async;
    
    private Map properties = new HashMap();
    private OpenerLoader loader;
    
    public Opener() {
    }
    
    public Opener(String name) {
        this.name = name;
    }
    
    public String getName() { return name; }    
    public void setName(String name) { this.name = name; }
    
    public String getId() {
        if (id == null) {
            StringBuffer sb = new StringBuffer();
            if ( name != null ) sb.append(name);
            if ( caption != null ) sb.append(caption);
            
            return sb.toString();
        }         
        return id;
    }
    
    public void setId(String id) { this.id = id; }
    
    public String getCaption() { return caption; }    
    public void setCaption(String caption) { 
        this.caption = caption; 
    }
    
    public String getAction() { return action; }    
    public void setAction(String action) { 
        this.action = action; 
    }
    
    public Map getParams() { return params; } 
    public void setParams(Map params) {
        this.params = params;
    }
    
    public String getTarget() { return target; }    
    public void setTarget(String target) {
        this.target = target;
    }
    
    public String getOutcome() { return outcome; }    
    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }
    
    public UIController getController() { return controller; }    
    public void setController(UIController controller) {
        this.controller = controller;
    }
    
    /**
     *  Shorthand for getting the controllers codeBean.
     *  Returns null if the controller is null.
     */
    public Object getHandle() {
        if (controller == null) {
            if (loader != null) loader.load(this);
        }
        return controller.getCodeBean();
    }
    
    public UIController getCaller() { return caller; }    
    public void setCaller(UIController caller) {
        this.caller = caller;
    }
    
    public String getPermission() { return permission; }    
    public void setPermission(String permission) {
        this.permission = permission;
    }
    
    public boolean isModal() { return modal; }    
    public void setModal(boolean modal) {
        this.modal = modal;
    }
    
    public boolean isAsync() { return async; } 
    public void setAsync(boolean async) { this.async = async; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getDomain() { return domain; }
    public void setDomain(String domain) { 
        this.domain = domain;
    }  
        
    public OpenerLoader getLoader() { return loader; }
    public void setLoader(OpenerLoader loader) {
        this.loader = loader;
    }
    
    public Map getProperties() { return properties; }
    
    // <editor-fold defaultstate="collapsed" desc=" OpenerLoader interface "> 
    
    public static interface OpenerLoader {
        void load( Opener o );
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Provider interface and helper methods "> 
    
    private Opener.Provider provider;
    
    public final void setProvider(Opener.Provider provider) {
        this.provider = provider; 
    }
    
    public Opener createInstance(Map params) {
        return createInstance(params, null); 
    }
    
    public Opener createInstance(Map params, String caption) {
        if (provider == null) return null; 
        
        return provider.createInstance(params, caption); 
    }
    
    public static interface Provider {
        Opener createInstance(Map params, String caption); 
    }
    
    // </editor-fold>
}
