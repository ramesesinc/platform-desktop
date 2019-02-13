/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.common;

import com.rameses.common.PropertyResolver;
import com.rameses.rcp.framework.Binding;

public abstract class ComponentBean implements ChildObject {
 
    private String bindingName; 
    private Binding innerBinding;
    private Binding callerBinding;
    
    // applied only to dynamic data 
    private Object userObject;
    
    public String getBindingName() {
        return bindingName; 
    }
    public void setBindingName( String name ) {
        this.bindingName = name; 
    }
    
    public Binding getBinding() { 
        return innerBinding; 
    } 
    public void setBinding( Binding innerBinding ) {
        this.innerBinding = innerBinding; 
    }
    
    public Binding getCallerBinding() {
        return callerBinding; 
    }
    public void setCallerBinding( Binding callerBinding ) { 
        this.callerBinding = callerBinding; 
    } 
    
    public Object getCaller() { 
        Binding bi = getCallerBinding(); 
        return ( bi == null? null : bi.getBean()); 
    } 

    public Object getParentObject() { 
        return getCaller(); 
    } 
    
    public Object getValue() {
        return getValue( bindingName ); 
    }
    public Object getValue( String name ) { 
        try { 
            PropertyResolver pr = PropertyResolver.getInstance();
            Object bean = getUserObject();
            if ( bean != null ) {
                Object ov = pr.getProperty(bean, "value");
                if( ov != null ) return ov;
            }
            return pr.getProperty( getCaller(), name ); 
        } catch(Throwable t) {
            return null; 
        }
    } 
    public void setValue( Object value ) { 
        setValue( bindingName, value ); 
    } 
    public void setValue( String name, Object value ) { 
        PropertyResolver pr = PropertyResolver.getInstance();
        Object bean = getUserObject();
        if ( bean != null && name==bindingName ) {
            pr.setProperty(bean, "value", value);
        }
        Binding bi = getCallerBinding(); 
        if ( bi == null ) return; 
        
        pr.setProperty( getCaller(), name, value ); 
        bi.getValueChangeSupport().notify(name, value); 
        bi.notifyDepends( name ); 
    }
    
    public Object getProperty( String name ) { 
        try { 
            return PropertyResolver.getInstance().getProperty( this, name ); 
        } catch(Throwable t) {
            return null; 
        } 
    }
    public void setProperty( String name, Object value ) { 
        PropertyResolver.getInstance().setProperty( this, name, value ); 
    } 
    
    public Object getUserObject() { return userObject; }
    public void setUserObject( Object userObject ) {
        this.userObject = userObject; 
    }
}
