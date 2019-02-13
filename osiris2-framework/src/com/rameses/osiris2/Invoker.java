/*
 * IInvoker.java
 *
 * Created on February 23, 2009, 9:15 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author elmo
 */
public class Invoker implements Serializable, Comparable {
    
    private String workunitid;
    private String workunitname;
    private String name;
    private String caption;
    private String ref;
    private String type = "folder";
    private Integer index;
    private String action;
    private Map properties = new HashMap();

    private Module module;

    private String role;
    private String domain;
    private String permission;
    
    public Invoker() {
        
    }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getPermission() { return permission; }
    public void setPermission(String permission) {
        this.permission = permission;
    }

    public Map getProperties() { return properties; }
    
    public int hashCode() {
        StringBuffer sb = new StringBuffer();
        sb.append( getType() +":"+getWorkunitid() );
        if( this.getName()!=null ) {
            sb.append( "_"+getName() );
        }
        if(this.getAction()!=null) {
            sb.append("_"+getAction());
        }
        return sb.toString().hashCode();
    }

    public boolean equals(Object object) {
        if( object == null || !(object instanceof Invoker)) return false;
        Invoker i = (Invoker)object;
        return hashCode() == i.hashCode();
    }

    public String getCaption() {
        if( caption == null )
            if( name !=null)
                return name;
            else
                return "[No Caption]";
        else            
            return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getRef() { return ref; }
    public void setRef(String ref) { this.ref = ref; }

    public Integer getIndex() { return index; }
    public void setIndex(Integer index) { this.index = index; }

    public int compareTo(Object o ) {
        if( o == null || !(o instanceof Invoker) ) return 0;
        Invoker i = (Invoker)o;
        int aidx = getIndex() == null? 0 : getIndex();
        int bidx = i.getIndex() == null? 0 : i.getIndex();
        
        return aidx - bidx;
    }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getWorkunitid() { return workunitid; }
    public void setWorkunitid(String workunitid) { 
        this.workunitid = workunitid;
    }

    public String getRole() { return role; }
    public void setRole(String roles) { this.role = roles; }

    public Module getModule() { return module; }
    public void setModule(Module module) { this.module = module; }

    public String getWorkunitname() { return workunitname; }
    public void setWorkunitname(String workunitname) {
        this.workunitname = workunitname;
    }

    public String getDomain() { return domain; }
    public void setDomain(String domain) { this.domain = domain; }
    
    // <editor-fold defaultstate="collapsed" desc=" clone utility "> 
    
    public Invoker clone() {
        return copy( null ); 
    }
    
    public Invoker copy( Invoker target ) {
        if (target == null) {
            target = new Invoker(); 
        }
        
        target.workunitid = this.workunitid;
        target.workunitname = this.workunitname;
        target.name = this.name;
        target.caption = this.caption;
        target.ref = this.ref;
        target.type = this.type;
        target.index = this.index;
        target.action = this.action;
        target.module = this.module;
        target.role = this.role;
        target.domain = this.domain;
        target.permission = this.permission;
        target.properties.putAll(this.properties); 
        return target; 
    } 
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" Action facility "> 
    
    private List<Action> actions = new ArrayList();
    
    public List<Invoker.Action> getActions() { 
        return actions; 
    } 
    
    public void addAction( Invoker.Action a ) { 
        if (a != null) { 
            a.source = this; 
            actions.add(a); 
        } 
    }
    
    public void removeActions() { 
        actions.clear(); 
    } 
    
    public static class Action implements Serializable {

        private String name;
        private String role;
        private String permission;
        private Map properties;
                
        private Invoker source;
        private Invoker target; 
        
        public Action( String name ) { 
            this.name = name; 
            this.properties = new HashMap();
        }

        public String getName() { return name; } 
        
        public String getRole() { return role; } 
        public void setRole(String role) { 
            this.role = role; 
        }
        
        public String getPermission() { return permission; } 
        public void setPermission( String permission ) {
            this.permission = permission; 
        }
        
        public String getProperty( String name ) {
            Object ov = properties.get( name ); 
            return (ov == null? null: ov.toString()); 
        }
        
        public Iterator<Map.Entry> getProperties() {
            return properties.entrySet().iterator(); 
        }
        public void setProperties( Map extended ) {
            if (extended != null) {
                this.properties.putAll( extended ); 
            }
        }  
        
        public void update( Invoker inv ) {
            String arole = getRole(); 
            String aperm = getPermission(); 
            String acaption = getProperty("caption"); 
            String aname = getName(); 
            
            if (!isEmpty(arole)) { inv.setRole( arole ); }
            if (!isEmpty(aperm)) { inv.setPermission( aperm ); }
            if (!isEmpty(acaption)) { inv.setCaption(acaption); } 
            if (!isEmpty(aname)) { inv.setAction( aname ); }  
            
            inv.getProperties().putAll( properties ); 
        } 
        
        private boolean isEmpty( String value ) {
            return ( value == null || value.trim().length() == 0 ); 
        }
    }
    
    // </editor-fold>
   
}
