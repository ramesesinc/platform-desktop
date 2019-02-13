/*
 * OpenerUtil.java
 *
 * Created on January 15, 2014, 11:34 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.util;

import com.rameses.platform.interfaces.Platform;
import com.rameses.rcp.common.LookupOpenerSupport;
import com.rameses.rcp.common.Opener;
import com.rameses.rcp.framework.UIController;
import com.rameses.rcp.framework.UIControllerContext;
import com.rameses.rcp.framework.UIControllerPanel;
import com.rameses.rcp.impl.ClientContextImpl;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author wflores
 */
public class OpenerUtil 
{
    public static void show(Opener opener) {
        new OpenerUtil().showImpl(opener, null);
    }

    public static void show(Opener opener, Map windowOptions) {
        new OpenerUtil().showImpl(opener, windowOptions);
    }
    
    public static void show(String openerName, Map params) {
        new OpenerUtil().showImpl(openerName, params, null);
    }    
    
    public static void show(String openerName, Map params, Map windowOptions) {
        new OpenerUtil().showImpl(openerName, params, windowOptions);
    }  
    
    private OpenerUtil() {
    }
    
    private void showImpl(String openerName, Map params, Map windowOptions) {
        if (params == null) params = new HashMap();
        
        Opener opener = LookupOpenerSupport.lookupOpener(openerName+"", params); 
        showImpl(opener, windowOptions); 
    }  
    
    private void showImpl(Opener opener, Map windowOptions) {
        if (windowOptions == null) {
            windowOptions = opener.getProperties();
        } 
        
        Map props = opener.getProperties();
        if (hasValue(windowOptions, "title")) {
            opener.setCaption(getString(windowOptions, "title"));
            props.put("title", opener.getCaption());
        } 
        if (hasValue(windowOptions, "width")) {
            props.put("width", windowOptions.get("width"));
        } 
        if (hasValue(windowOptions, "height")) {
            props.put("height", windowOptions.get("height"));
        } 
        if (hasValue(windowOptions, "resizable")) {
            props.put("resizable", windowOptions.get("resizable"));
        } 
        if (hasValue(windowOptions, "alwaysOnTop")) {
            props.put("alwaysOnTop", windowOptions.get("alwaysOnTop"));
        } 
        if (hasValue(windowOptions, "undecorated")) {
            props.put("undecorated", windowOptions.get("undecorated"));
        } 
        
        opener = ControlSupport.initOpener(opener, null); 
        String target = opener.getTarget()+"";
        if (target.matches("process|_process")) return;
        
        String sid = opener.getController().getId();
        Platform platform = ClientContextImpl.getCurrentContext().getPlatform();
        if (platform.isWindowExists(sid)) {
            platform.activateWindow(sid);
            return;
        }
        
        UIController uic = opener.getController();
        String permission = opener.getPermission();
        String role = opener.getRole();
        String domain = opener.getDomain();

        //check permission(if specified) if allowed
        if (permission != null && permission.length() > 0) {
            permission = uic.getName() + "." + permission;
            if (!ControlSupport.isPermitted(domain, role, permission)) {
                String msg = "You don't have permission to perform this transaction.";
                platform.showError(null, new IllegalStateException(msg)); 
                return;
            }
        }
                
        UIControllerContext uicx = new UIControllerContext(uic);
        String outcome = opener.getOutcome();
        if (outcome != null && outcome.length() > 0) {
            uicx.setCurrentView(outcome);
        }
        
        UIControllerPanel uicp = new UIControllerPanel(uicx); 
        
        Map map = new HashMap();
        map.putAll(props); 
        map.put("id", sid);
        
        if (!hasValue(map, "title")) { 
            map.put("title", uicx.getTitle());
        }

        uicp.putClientProperty("Opener.properties", map); 
        if ("popup".equals(target) ) {
            platform.showPopup(null, uicp, map);
        } else {
            platform.showWindow(null, uicp, map);
        } 
    } 
    
    private String getString(Map map, String name) {
        Object value = (map == null? null: map.get(name));
        return (value == null? null: value.toString()); 
    }
    
    private boolean hasValue(Map map, String name) {
        Object value = (map == null? null: map.get(name)); 
        if (value == null) return false; 
        if (value.toString().length() == 0) return false;
        
        return true; 
    }    
}
