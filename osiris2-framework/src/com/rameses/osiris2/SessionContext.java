/*
 * SessionContext.java
 *
 * Created on February 21, 2009, 3:53 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * this is a package level class created only by
 * the AppContext
 */
public class SessionContext {
    
    /**
     * extended permission pattern
     * @description
     *    this expression matches permission name written in formats shown below:
     *    a. <module_name>:<workunit_name>.<action_name>
     *    b. <workunit_name>.<action_name>
     */
    public static final Pattern EXT_PERM_PATTERN = Pattern.compile("(?:(.*):)?[^\\.]*\\.[^\\.]*$");
    
    
    private AppContext context;
    private Map env = new EnvMap();
    private SecurityProvider securityProvider;
    protected Map folderIndex = new Hashtable();
    
    //this holds a map of categorized invokers
    protected Map invokers = new Hashtable();
    
    private Map properties = new Hashtable();
    
    
    protected SessionContext(AppContext ctx) {
        this.context = ctx;
        env = new EnvMap(ctx.getEnv());
        env.put("CLIENTTYPE", "desktop");
    }
    
    public Map getEnv() {
        return env;
    }
    
    public Module getModule(String name) {
        Module c = context.getModule(name);
        if( c == null )
            throw new IllegalStateException( "Module not found : " + name);
        return c;
    }
    
    public WorkUnit getWorkUnit( String name ) {
        return context.getWorkUnit(name);
    }
    
    //the default permission is allow true. The exception is false.
    //this is being called
    public boolean checkPermission(String domain, String role, String name) {
        if(role==null && name == null ) return true;
        return securityProvider.checkPermission(domain, role, name);
    }
    
    
    //if there is no type specified set this as folder
    public List getInvokers() {
        return getInvokers(null);
    }
    
    public List getInvokers( String type ) {
        return getInvokers(type, true);
    }
      
    //this is an overridable additional method to allow the application provider to
    //have additional chcking of the invoker before allowing it to be accessed
    public boolean checkInvoker( Invoker inv ) {
        return true;
    }
    
    public List getInvokers( String type, boolean applySecurity ) {
        if (type == null) type = "folder";
        if (!invokers.containsKey(type)) {
            List list = new ArrayList();
            List removals = new ArrayList();
            
            Iterator iter = context.getInvokers().iterator();
            while (iter.hasNext()) {
                Invoker inv = (Invoker)iter.next();
                String itype = (inv.getType() == null) ? "folder" : inv.getType();
                if(itype.matches(type) || itype.equalsIgnoreCase(type)) {
                    boolean showIt = true;
                    String permission = inv.getPermission();
                    String role = inv.getRole();
                    String domain = inv.getDomain();
                    if (applySecurity) {
                        if(role != null || permission != null) {
                            showIt = checkPermission(domain, role, permission);
                        }
                        
                        if (showIt) {
                            List<Invoker.Action> actions = inv.getActions(); 
                            if ( !actions.isEmpty() ) {
                                boolean foundIt = false; 
                                for ( Invoker.Action ia : actions ) {
                                    String aname = ia.getName();
                                    String arole = ia.getRole(); 
                                    String aperm = ia.getPermission();
                                    if (checkPermission(domain, arole, aperm)) {
                                        ia.update( inv ); 
                                        
                                        
                                        foundIt = true; 
                                        break; 
                                    } 
                                } 
                                showIt = foundIt;
                            }
                        }
                    }

                    if (showIt) { showIt = checkInvoker(inv); } 
                    if (showIt) { list.add(inv); } 
                }
            }
            
            Collections.sort( list ); 
            invokers.put(type, list);
            return list;
        } else {
            return (List)invokers.get(type);
        }
    }
    
    public final List<Invoker> getInvokersByWorkunitid(String id) {
        List<Invoker> list = new ArrayList();
        if (id == null || id.length() == 0) return list; 
        
        Iterator itr = context.getInvokers().iterator();
        while (itr.hasNext()) {
            Invoker inv = (Invoker) itr.next();
            if (id.equals(inv.getWorkunitid())) {
                list.add(inv); 
            } 
        } 
        Collections.sort(list); 
        return list; 
    }
    
    public SecurityProvider getSecurityProvider() {
        return securityProvider;
    }
    
    public void setSecurityProvider(SecurityProvider securityProvider) {
        this.securityProvider = securityProvider;
    }
    
    //returns a list of folders including the invokers
    public List getFolders(String name) {
        return getFolders(name, null);
    }
    
    public List getFolders(Folder parent) {
        return getFolders(parent, null);
    }
    
    public List getFolders(String name, InvokerSource invokerSrc) {
        if( !name.startsWith("/")) name = "/" + name;
        Folder folder = (Folder)context.getFolderManager().getFolders().get(name);
        if ( folder == null ) return null;

        return getFolders(folder, invokerSrc);
    }
    
    public List getFolders(Folder parent, InvokerSource invokerSrc) {
        String fullId = parent.getFullId();
        List list = null;
        if( folderIndex.get( fullId )==null ) {
            list = new ArrayList();
            Iterator iter = parent.getFolders().iterator();
            while(iter.hasNext()) {
                list.add(iter.next());
            }
            List invokers = getInvokers("folder");
            
            //force add all the invokers
            if(invokerSrc!=null) {
                invokers.addAll( invokerSrc.getInvokers("folder") );
            }
            
            iter = invokers.iterator(); 
            while(iter.hasNext()) {
                Invoker inv = (Invoker)iter.next();
                String fid = (String)inv.getProperties().get("folderid");
                if(fid!=null) {
                    if( !fid.startsWith("/")) fid = "/" + fid;
                    if( fid.equals(parent.getFullId())) {
                        if( inv.getName()==null ) {
                            inv.setName(fid);
                        }
                        String fname = inv.getName();
                        if( fname != null) {
                            Folder f = new Folder( fname, inv.getCaption(), parent, inv);
                            list.add(f);
                        }
                    }
                }
            }

            sortFolders( list ); 
            folderIndex.put(fullId, list);
        } else {
            list = (List)folderIndex.get(fullId);
        }
        return list;
    }
    
    public ClassLoader getClassLoader() {
        return context.getClassLoader();
    }
    
    public Map getProperties() {
        return properties;
    }
    
        
    private void sortFolders( List list ) { 
        List<FolderGroup> groups = new ArrayList();
        for (int i=0; i<list.size(); i++ ) { 
            Object o = list.get(i); 
            if (!(o instanceof Folder )) continue; 
            
            Folder f = (Folder)o; 
            int findex = (f.getIndex()==null ? 0 : f.getIndex());             
            FolderGroup g = findFolderGroup( groups, findex );
            if ( g == null ) {
                g = new FolderGroup( findex ); 
                groups.add( g ); 
            }
            g.add( f ); 
        } 
        Collections.sort( groups ); 
        
        List target = new ArrayList();
        for ( FolderGroup g : groups ) {
            g.sortItems();
            g.copyItemsTo( target ); 
            g.clear();
        } 
        list.clear(); 
        list.addAll( target ); 
        target.clear(); 
    } 
    private FolderGroup findFolderGroup( List<FolderGroup> groups, int key ) {
        for ( FolderGroup g : groups ) {
            if ( g == null ) continue; 
            if ( g.index == key ) return g; 
        }
        return null; 
    }

    private class FolderGroup implements Comparable {
        
        private int index; 
        private List folders; 
        
        FolderGroup( int index ) {
            this.index = index; 
            this.folders = new ArrayList();
        }

        public boolean equals(Object obj) { 
            if ( obj instanceof FolderGroup ) {
                return (((FolderGroup)obj).index == this.index); 
            } else if ( obj instanceof Number ) {
                return (((Number)obj).intValue() == this.index ); 
            }
            return super.equals(obj);
        }

        public int compareTo(Object o) { 
            if ( o instanceof FolderGroup ) {
                FolderGroup g = (FolderGroup)o; 
                if ( this.index < g.index ) return -1; 
                else if ( this.index > g.index ) return 1; 
                else return 0; 
            }
            return 999999999;
        }

        public String toString() { 
            StringBuilder sb = new StringBuilder();
            sb.append( super.toString() ).append(" ( "); 
            sb.append("index=" + this.index ).append(", "); 
            sb.append("size=" + folders.size()).append(" ) "); 
            return sb.toString(); 
        } 
        
        void add( Object o ) {
            if ( !folders.contains(o)) { 
                folders.add( o ); 
            } 
        }
        void copyItemsTo( List target ) {
            for ( Object o : this.folders ) { 
                if ( o == null ) continue; 
                if ( target.contains(o)) continue; 
                
                target.add( o );  
            } 
        }
        void sortItems() {
            Collections.sort(folders, new Comparator(){
                public int compare(Object o1, Object o2) {
                    Folder f1 = (Folder)o1; 
                    Folder f2 = (Folder)o2; 
                    Invoker inv1 = f1.getInvoker(); 
                    Invoker inv2 = f2.getInvoker(); 
                    String s1 = (inv1 == null ? f1.getCaption() : inv1.getCaption()); 
                    String s2 = (inv2 == null ? f2.getCaption() : inv2.getCaption()); 
                    if ( s1 == null ) s1 = "";
                    if ( s2 == null ) s2 = "";
                    return s1.toLowerCase().compareTo(s2.toLowerCase()); 
                }
            });
        }
        void clear() {
            this.folders.clear(); 
        }
    }
}
