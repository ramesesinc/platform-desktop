/*
 * MapObject.java
 *
 * Created on September 4, 2013, 9:40 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.common;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author wflores
 */
public class MapObject implements Map   
{
    public static Map clone(Map source) {
        return new Helper().clone(source); 
    }
    
    public static MapObject.Helper getHelper() {
        return new Helper(); 
    }
    
    
    private Map map;
    private Object anObject;
    private Helper helper;
    
    public MapObject(Object anObject) {
        this.anObject = anObject;
        if (anObject instanceof Map) {
            map = (Map) anObject; 
        }
        
        this.helper = new Helper();
    }
    
    public Object getObject() { return anObject; } 
    
    public String getString(String name) {
        Object ov = get(name); 
        return (ov == null? null: ov.toString()); 
    }
    
    public Integer getInteger(String name) {
        Object ov = get(name); 
        if (ov == null) {
            return null; 
        } else if (ov instanceof Integer) {
            return (Integer)ov; 
        } else { 
            return Integer.valueOf(ov.toString());
        }
    }
    
    public Double getDouble(String name) {
        Object ov = get(name); 
        if (ov == null) {
            return null; 
        } else if (ov instanceof Double) {
            return (Double)ov; 
        } else { 
            return Double.valueOf(ov.toString());
        }
    }    
    
    public BigDecimal getDecimal(String name) {
        Object ov = get(name); 
        if (ov == null) {
            return null; 
        } else if (ov instanceof BigDecimal) {
            return (BigDecimal)ov; 
        } else { 
            return new BigDecimal(ov.toString()); 
        }
    } 
    
    public Boolean getBoolean(String name) {
        Object ov = get(name); 
        if (ov == null) {
            return null; 
        } else if (ov instanceof Boolean) {
            return (Boolean)ov; 
        } else { 
            return Boolean.valueOf(ov.toString());
        }
    } 
    
    public Object setIfNull(Object key, Object value) {
        Object oldvalue = get(key);
        if (oldvalue == null) {
            return put(key, value);
        } else {
            return oldvalue; 
        }
    } 
    
    public Map copy(Map value, Map defaultValue) {
        return helper.copy(value, defaultValue); 
    } 
    
    public Map clone() {
        return helper.clone(map); 
    }    
    

    public int size() {
        return (map == null? 0: map.size()); 
    }

    public boolean isEmpty() {
        return (map == null? true: map.isEmpty());
    }

    public boolean containsKey(Object key) {
        return (map == null? false: map.containsKey(key));
    }

    public boolean containsValue(Object value) {
        return (map == null? false: map.containsValue(value)); 
    }

    public Object get(Object key) {
        return (map == null? null: map.get(key));
    }
    
    public Object get(Object key, Object defaultValue) {
        Object value = (map == null? null: map.get(key));
        return (value == null? defaultValue: value); 
    }    
    
    public Object put(Object key, Object value) {
        return map.put(key, value); 
    }

    public Object remove(Object key) {
        return (map == null? null: map.remove(key)); 
    }

    public void putAll(Map map) {
        map.putAll(map); 
    }

    public void clear() {
        map.clear();
    }

    public Set keySet() {
        return map.keySet(); 
    }

    public Collection values() {
        return map.values();
    }

    public Set entrySet() {
        return map.entrySet(); 
    }

    
    
    public static class Helper 
    {
        public Map clone(Map source) {
            if (source == null) return null;

            Map newmap = new HashMap(); 
            Iterator<Map.Entry> itr = source.entrySet().iterator(); 
            while (itr.hasNext()) {
                Map.Entry me = itr.next(); 
                Object key = me.getKey(); 
                Object val = me.getValue(); 
                if (val instanceof Map) {
                    Map m = clone((Map) val); 
                    newmap.put(key, m);
                } else {
                    newmap.put(key, val); 
                }
            } 
            return newmap;
        } 
        
        Map copy(Map value, Map defaultValue) {
            if (value == null) return defaultValue;

            return clone(value); 
        } 
        
        Object ifNull(Object value, Object defaultValue) {
            return (value == null? defaultValue: value); 
        }
    }
}
