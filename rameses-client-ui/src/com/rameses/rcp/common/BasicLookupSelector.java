/*
 * BasicLookupSelector.java
 *
 * Created on September 15, 2013, 8:47 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.common;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author wflores
 */
public class BasicLookupSelector implements LookupSelector 
{
    private Map query;
    private String searchtext; 
    
    public BasicLookupSelector() {
        query = new HashMap(); 
    }

    public Map getQuery() { return query; } 
    public void setQuery(Map query) {
        this.query = (query == null? new HashMap(): query); 
    }
    
    public String getSearchtext() { return searchtext; } 
    public void setSearchtext(String searchtext) {
        this.searchtext = searchtext; 
    }
    
    public Object select(Object o) {
        return null; 
    }

    public void cancelSelection() {
    }    
}
