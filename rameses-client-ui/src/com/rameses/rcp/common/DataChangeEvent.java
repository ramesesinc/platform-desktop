/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.common;

/**
 *
 * @author wflores
 */
public class DataChangeEvent {
    
    private Object bean;
    private String name;
    private Object value;
    
    public DataChangeEvent(Object bean, String name, Object value) {
        this.bean = bean;
        this.name = name;
        this.value = value; 
    }
    
    public Object getBean() { return bean; } 
    public String getName() { return name; }
    public Object getValue() { return value; } 
}
