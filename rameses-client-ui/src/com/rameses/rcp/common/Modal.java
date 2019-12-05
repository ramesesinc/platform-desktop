/*
 * Modal.java
 *
 * Created on December 31, 2013, 7:40 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.common;

import com.rameses.platform.interfaces.Platform;
import com.rameses.rcp.framework.UIController;
import com.rameses.rcp.framework.UIControllerContext;
import com.rameses.rcp.framework.UIControllerPanel;
import com.rameses.rcp.framework.UIViewPanel;
import com.rameses.rcp.impl.ClientContextImpl;
import com.rameses.rcp.util.ControlSupport;
import com.rameses.util.ValueUtil;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author wflores
 */
public final class Modal 
{
    
    public static void show(Opener opener) {
        new Modal().showImpl(opener, null);
    }

    public static void show(Opener opener, Map windowOptions) {
        new Modal().showImpl(opener, windowOptions);
    }
    
    public static void show(String openerName, Map params) {
        new Modal().showImpl(openerName, params, null);
    }    
    
    public static void show(String openerName, Map params, Map windowOptions) {
        new Modal().showImpl(openerName, params, windowOptions);
    }        
    
    
    private Modal() {
    }
        
    private void showImpl(String openerName, Map params, Map windowOptions) {
        if (params == null) params = new HashMap();
        
        Opener opener = LookupOpenerSupport.lookupOpener(openerName+"", params); 
        showImpl(opener, windowOptions); 
    }  
    
    private void showImpl(Opener opener, Map windowOptions) {
        if (windowOptions == null) {
            windowOptions = new HashMap();
        } 
        
        Map props = new HashMap();
        Map opmap = opener.getProperties();
        if ( opmap != null ) { 
            props.putAll( opmap ); 
            props.putAll( WindowUtil.extractWindowAttrs( opmap ) );  
        }
        
        props.remove("windowmode");
        props.put("modal", true); 
        props.putAll( WindowUtil.extractWindowAttrs( windowOptions ) );  
        
        opener.setTarget("popup");
        opener = ControlSupport.initOpener(opener, null); 
        
        String sid = opener.getController().getId();
        Object wid = props.get("windowid"); 
        if ( wid != null && wid.toString().trim().length() > 0 ) {
            sid = wid.toString(); 
        }
        
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
        
        props.put("id", sid);
        props.put("immediate", true); 
        if (props.get("title") == null) {
            props.put("title", opener.getCaption()); 
        }
                
        UIControllerContext uicx = new UIControllerContext(uic);
        if ( !ValueUtil.isEmpty(opener.getOutcome()) ) {
            if ( "_close".equals( opener.getOutcome()) ) {
                return;
            }

            uicx.setCurrentView( opener.getOutcome() );
        }
        
        UIControllerPanel uicp = new UIControllerPanel(uicx); 
        uicp.putClientProperty("Opener.properties", props); 
        uicp.buildFormAttrs( props ); 
        platform.showPopup(null, uicp, props);
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
