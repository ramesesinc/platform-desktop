/*
 * ParamMap.java
 *
 * Created on August 8, 2014, 3:08 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.common;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author wflores
 */
public class ParamMap<K,V> extends LinkedHashMap<K,V> implements Map<K,V> 
{
    public ParamMap() {
        super();
    }
    
    public <T> T getValue(Object key) {
        return (T) get(key); 
    }
}
