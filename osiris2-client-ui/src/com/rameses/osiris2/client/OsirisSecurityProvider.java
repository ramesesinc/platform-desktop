/*
 * OsirisSecurityProvider.java
 *
 * Created on October 17, 2009, 10:22 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris2.client;

import com.rameses.osiris2.SecurityProvider;
import com.rameses.rcp.framework.ClientSecurityProvider;
import java.util.Map;

public class OsirisSecurityProvider implements SecurityProvider, ClientSecurityProvider {
    
    public OsirisSecurityProvider() {
    }

    private Map.Entry getRolePermission( Map roles, String matchPattern ) {
        Map.Entry retval = null;
        for(Object o: roles.entrySet()) {
            Map.Entry me = (Map.Entry)o;
            String key = me.getKey().toString();
            if( key.matches(matchPattern)) {
                retval = me;
                break;
            }
        }
        return retval;
    }

    public boolean checkPermission(String domain, String role, String name) {
        Map roles = (Map) OsirisContext.getEnv().get("ROLES");
        if (roles != null && role != null && role.length() > 0) {
            String[] arrays = role.split(",");
            for (int i=0; i<arrays.length; i++) {
                String srole = arrays[i].trim();
                if (domain != null) srole = domain+"."+srole;
                Map.Entry me = getRolePermission( roles, srole );
                if( me == null ) continue;
                //if (!roles.containsKey(srole)) continue;
                
                if (name == null || name.trim().length() == 0) return true;
                
                //String disallowed = (String) roles.get(srole);
                String disallowed = (String)me.getValue();
                if (disallowed != null && name.matches(disallowed)) continue;
                
                return true;
            } 
            return false; 
//            if(domain!=null) role = domain+"."+role;
//            if(! roles.containsKey(role)) return false;
//            if(name==null || name.trim().length()==0) return true;
//            String disallowed = (String)roles.get(role);
//            if(disallowed!=null && name.matches(disallowed))return false; 
//            return true;
        } 
        
        if (roles != null && name != null && name.length() > 0) { 
            String allowed = (String)roles.get("ALLOWED");
            if (allowed != null && name.matches(allowed)) 
                return true; 
            else 
                return false;
        }
        
        return (role == null && name == null? true: false);
    }
}
