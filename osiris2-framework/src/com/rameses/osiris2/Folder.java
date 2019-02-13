/*
 * Folder.java
 *
 * Created on March 29, 2009, 11:04 AM
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
public class Folder implements Serializable, Comparable {
    
    private String id;
    private String fullId;
    private String caption;
    private Folder parent;
    private List folders = new ArrayList();
    private Invoker invoker;
    private Integer index = new Integer(0);
    private Map properties = new HashMap();
    
    //package level
    private boolean visible = false;
    
    
    public Folder(String id, String caption) {
        this.id = id;
        this.caption = caption;
        this.fullId = "/" + id;
    }
    
    
    public Folder(String id, String caption, Folder parent, Invoker invoker) {
        this.id = id;
        this.caption = caption;
        this.setParent(parent);
        this.fullId = getParent().getFullId() + "/" + id;
        this.invoker = invoker;
        this.index = invoker.getIndex();
        notifyVisible();
    }
    
    

    // <editor-fold defaultstate="collapsed" desc="GETTER/SETTER">
    public String getId() {
        return id;
    }

    public String getFullId() {
        return fullId;
    }

    public String getCaption() {
        return caption;
    }
    
    public void setCaption(String caption) {
        this.caption = caption;
    }

    public Folder getParent() {
        return parent;
    }

    public List getFolders() {
        return folders;
    }

    public Invoker getInvoker() {
        return invoker;
    }

    public void setInvoker(Invoker invoker) {
        this.invoker = invoker;
    }

    public boolean isVisible() {
        return visible;
    }
    //</editor-fold>

    public boolean equals( Object o ) { 
        if ( o == null || !(o instanceof Folder)) return false;
        if ( super.equals(o)) return true; 
        
        Folder f = (Folder) o;
        if ( !getFullId().equals( f.getFullId())) return false; 
        
        Invoker inv1 = getInvoker(); 
        Invoker inv2 = f.getInvoker(); 
        if ( inv1 != null && inv2 != null && genKey(inv1).equals(genKey(inv2))) {
            return true; 
        } else if ( inv1 == null && inv2 == null ) {
            return true; 
        } else {
            return false; 
        } 
    }
    
    private String genKey( Invoker inv ) { 
        StringBuilder sb = new StringBuilder();
        sb.append( inv.getType()).append("://").append( inv.getWorkunitid());
        sb.append("/").append( inv.getName()).append("/").append( inv.getAction());
        sb.append("/").append( inv.getCaption());         
        return sb.toString().toLowerCase(); 
    } 
    
    public void removeSelf() {
        this.getParent().getFolders().remove(this);
    }

    
    public void notifyVisible() {
        this.visible = true;
        Folder p = getParent();
        while(p!=null) {
            p.visible = true;
            p = p.getParent();
        }
    }
    
    
    public String toXml() {
        StringBuffer sb = new StringBuffer();
        sb.append( "<folder id=\""+ id + "\" caption=\"" + caption + "\" visible=\"" + visible + "\"  index=\"" + index+ "\"");
        if( this.getFolders().size()>0 ) {
            sb.append(">\n");
            Iterator iter = folders.iterator();
            while(iter.hasNext()) {
                Folder f = (Folder)iter.next();
                sb.append( f.toXml() );
            }
            sb.append("</folder>\n");
        }
        else {
            sb.append("/>\n");
        }
        return sb.toString();
    }
    
    /*
    public void mergeFolders( List newFolders ) {
        Iterator iter = newFolders.iterator();
        while(iter.hasNext()) {
            Folder newFolder = (Folder)iter.next();
            
            //important! replace the parent with the old...
            newFolder.setParent(this);
            int idx = folders.indexOf(newFolder);
            if( idx >=0 ) {
                Folder old = (Folder)folders.get( idx );
                old.mergeFolders( newFolder.getFolders() );
            }
            else {
                folders.add( newFolder );
            }
        }
        Collections.sort( folders );
    }
     */

    public void setParent(Folder parent) {
        this.parent = parent;
    }
    
    public int compareTo(Object o ) { 
        if ( o instanceof Folder ) { 
            Folder item2 = (Folder)o; 
            int idx1 = (getIndex() == null ? 0 : getIndex()); 
            int idx2 = (item2.getIndex() == null ? 0 : item2.getIndex()); 
            if ( idx1 < idx2 ) return -1; 
            else if ( idx1 > idx2 ) return 1; 
            else return 0; 
        } 
        return 999999999; 
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Map getProperties() {
        return properties;
    }

    public void setFullId(String fullId) {
        this.fullId = fullId;
    }
    
}
