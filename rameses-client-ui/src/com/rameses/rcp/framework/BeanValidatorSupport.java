/*
 * BeanValidatorSupport.java
 *
 * Created on October 1, 2013, 9:32 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.framework;

import com.rameses.common.PropertyResolver;
import com.rameses.rcp.common.CallbackHandlerProxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author wflores
 */
public class BeanValidatorSupport 
{
    private Map<String,List<Map.Entry>> handlers; 
    private PropertyResolver propertyResolver; 
    
    public BeanValidatorSupport() {
        handlers = new HashMap(); 
        propertyResolver = PropertyResolver.getInstance(); 
    }
    
    public void add(Map map, boolean immediate) {
        if (map == null || map.isEmpty()) return;
        
        List list = handlers.get(immediate+"");
        if (list == null) {
            list = new ArrayList(); 
            handlers.put(immediate+"", list); 
        } 
        
        Iterator<Map.Entry> entries = map.entrySet().iterator(); 
        while (entries.hasNext()) {
            Map.Entry me = entries.next(); 
            Object val = me.getValue();
            if (val == null) continue; 
            
            list.add(me); 
        }
    } 
    
    public void fireImmediateValidators(String name, Object value) {
        if (name == null || name.trim().length() == 0) return;
        
        List<Map.Entry> list = handlers.get("true");
        if (list == null || list.isEmpty()) return;
        
        for (Map.Entry entry : list) {
            String regex = (entry.getKey() == null? null: entry.getKey().toString());
            if (!match(name, regex)) continue;
            
            Object callback = entry.getValue(); 
            if (callback == null) continue;
            
            try { 
                new CallbackHandlerProxy(callback).call(value); 
            } catch(Throwable t) {
                throw new ValidatorException(t); 
            } 
        } 
    } 
    
    public void fireValidators(Object bean) {
        if (bean == null) return; 
        
        List<Map.Entry> list = handlers.get("false");
        if (list == null || list.isEmpty()) return;
        
        for (Map.Entry entry : list) {
            Object key = entry.getKey();
            if (key == null || key.toString().trim().length() == 0) continue; 
            
            Object value = null; 
            try { 
                value = propertyResolver.getProperty(bean, key.toString());
            } catch(Throwable t) {
                System.out.println("[fireValidators] failed to get bean value for '"+key+"' caused by " + t.getMessage()); 
            } 
            
            Object callback = entry.getValue(); 
            if (callback == null) continue;
            
            try { 
                new CallbackHandlerProxy(callback).call(value); 
            } catch(Throwable t) {
                throw new ValidatorException(t); 
            } 
        } 
    }     
    
    private boolean match(String name, String regex) 
    {
        if (name == null || name.trim().length() == 0) return false;
        if (regex == null || regex.trim().length() == 0) return false; 
        
        try {
            if ("*".equals(regex)) regex = ".*";

            return name.matches(regex); 
        } catch(Throwable ex) {
            return false; 
        }
    } 
}
