package com.rameses.rcp.draw.support;

import com.rameses.rcp.draw.interfaces.Figure;
import com.rameses.rcp.draw.utils.DataUtil;
import java.util.Map;

public class AttributeKey<T> {
    private String key;
    private Class<T> clazz;
    private T defaultValue;

    public AttributeKey(String key, Class<T> clazz) {
        this(key, clazz, null);
    }

    /** Creates a new instance with the specified attribute key, type token class,
     * and default value, and allowing null values. */
    public AttributeKey(String key, Class<T> clazz, T defaultValue) {
        this.key = key;
        this.clazz = clazz;
        this.defaultValue = defaultValue;
    }

    public String getKey() {
        return key;
    }
    
    public T getDefaultValue() {
        return defaultValue;
    }
    
    public Class getAttributeClass(){
        return clazz;
    }
    
    public T put(Map<AttributeKey, Object> attrs, T value) {
        return (T) attrs.put(this, value);
    }

    public T get(Map<AttributeKey, Object> attrs) {
        return attrs.containsKey(this) ? (T) attrs.get(this) : defaultValue;
    }
    
    public T get(Figure f) {
        return f.get(this);
    }

    public void set(Figure f, T value) {
        f.set(this, value);
    }

    @Override
    public String toString() {
        return key;
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public boolean equals(Object that) {
        if (that instanceof AttributeKey) {
            return ((AttributeKey) that).key.equals(this.key);
        }
        return false;
    }
    
    
    
    /**
     * String is the default encode/decode values for properties 
     */
    public String encode(Object o){
        return DataUtil.encode(o);
    }
    
    public T decode(Object value, Class clazz){
        return (T)DataUtil.decode(value, clazz);
    }
    
}
