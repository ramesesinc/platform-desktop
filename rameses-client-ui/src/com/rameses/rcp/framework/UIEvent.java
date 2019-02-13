/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.framework;

import com.rameses.common.PropertyResolver;

/**
 *
 * @author wflores 
 */
public class UIEvent {
    
    private Binding binding; 
    private String name; 
    private Object source;
    
    private Object value; 
    
    public UIEvent( Object source, Binding binding ) { 
        this( source, binding, null );
    } 
    public UIEvent( Object source, Binding binding, String name ) { 
        this.source = source; 
        this.binding = binding; 
        this.name = name; 
    } 
    public UIEvent( Object source, Binding binding, String name, Object value ) { 
        this.source = source; 
        this.binding = binding; 
        this.name = name; 
        this.value = value; 
    } 
    
    public Binding getBinding() {
        return binding; 
    }
    public String getName() {
        return name; 
    }
    public Object getSource() {
        return source; 
    }
    public Object getValue() {
        return value; 
    }
    public Object getBindingBean() {
        return (binding == null? null: binding.getBean()); 
    }
    public Object getValue( String name ) {
        Object bean = getBindingBean(); 
        if (bean == null || name == null || name.length() == 0) {
            return null;
        }
        
        try { 
            PropertyResolver resolver = PropertyResolver.getInstance(); 
            return resolver.getProperty(bean, name); 
        } catch(Throwable t) {  
            return null; 
        } 
    }
    public void setValue( String name, Object value ) {
        Object bean = getBindingBean(); 
        if (bean == null || name == null || name.length() == 0) {
            return; 
        } 
        
        try { 
            PropertyResolver resolver = PropertyResolver.getInstance(); 
            resolver.setProperty(bean, name, value); 
        } catch(Throwable t) {  
            // do nothing 
        } 
    }
}
