/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris2.common;

import com.rameses.rcp.common.Action;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author wflores
 */
public class ActionBuilder 
{
    private List<Action> actions;
    
    public ActionBuilder() {
        actions = new ArrayList(); 
    }
    
    public List<Action> getActions() { return actions; } 
    
    public void clear() { 
        actions.clear(); 
    } 
    
    public Action add(String name, String caption, String icon) {
        return add(name, caption, icon, null);
    }
    
    public Action add(String name, String caption, String icon, String shortcut) {
        return add(name, caption, icon, shortcut, '\u0000');
    }    
    
    public Action add(String name, String caption, String icon, String shortcut, char mnemonic) {
        return add(name, caption, icon, shortcut, mnemonic, null);
    }        
    
    public Action add(String name, String caption, String icon, String shortcut, char mnemonic, String visibleWhen) {
        return add(name, caption, icon, shortcut, mnemonic, null, false);
    } 
    
    public Action add( Map params ) {
        if ( params == null ) return null; 
        
        String command = getString(params, "action");
        String caption = getString(params, "caption");
        String icon = getString(params, "icon");
        String shortcut = getString(params, "shortcut");
        char mnemonic = getChar(params, "mnemonic"); 
        String visibleWhen = getString(params, "visibleWhen");
        Boolean immediate = getBoolean(params, "immediate"); 
        if (immediate == null) immediate = Boolean.FALSE; 
        
        return add(command, caption, icon, shortcut, mnemonic, visibleWhen, immediate); 
    }
    
    public Action add(String name, String caption, String icon, String shortcut, char mnemonic, String visibleWhen, boolean immediate) {
        Action a = new Action(name, caption, icon, mnemonic);
        if( shortcut != null ) { 
            a.getProperties().put("shortcut", shortcut); 
        } 
        if (visibleWhen != null) { 
            a.setVisibleWhen(visibleWhen); 
        } 
        a.setImmediate( immediate ); 
        actions.add( a ); 
        return a; 
    } 
    
    private String getString(Map map, Object key) {
        Object value = (map == null? null: map.get(key)); 
        return (value == null? null: value.toString()); 
    }
    
    private char getChar(Map map, Object key) {
        String str = getString(map, key); 
        if (str == null || str.length() == 0) {
            return '\u0000'; 
        } else {
            return str.charAt(0); 
        } 
    } 
    
    private Boolean getBoolean(Map map, Object key) {
        Object o = (map == null? null: map.get(key)); 
        if ( o == null ) {
            return null; 
        } else if ( o instanceof Boolean ) {
            return (Boolean)o;
        } else {
            boolean b = o.toString().equalsIgnoreCase("true"); 
            return new Boolean(b); 
        }
    } 
}
