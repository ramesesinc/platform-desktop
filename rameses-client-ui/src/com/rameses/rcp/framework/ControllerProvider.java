package com.rameses.rcp.framework;

import com.rameses.rcp.annotations.Controller;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Map;

/**
 *
 * @author jaycverg
 */
public abstract class ControllerProvider 
{
    private Map<Class,Field> classIndex = new Hashtable();
    
    public UIController getController(String name, UIController caller) {
        UIController controller = provide(name, caller);
        Object bean = controller.getCodeBean();
        injectController( bean, bean.getClass(), controller );
        
        return controller;
    }
    
    protected abstract UIController provide(String name, UIController caller);
    
    private void injectController( Object o, Class clazz, UIController u ) {
        if (o == null) return;
        
        if (classIndex.containsKey(clazz)) { 
            Field f = classIndex.get(clazz); 
            if (f == null) return;
            
            boolean success = setValue(f, o, u);
            if (success) {
                com.rameses.rcp.annotations.Controller a = f.getAnnotation(com.rameses.rcp.annotations.Controller.class);
                fireOnreadyCallback(a, o);
            }             
        } else {
            for (Field f: clazz.getDeclaredFields()) {
                //inject Controller
                if (f.isAnnotationPresent(Controller.class)) {
                    boolean success = setValue(f, o, u); 
                    if (success) {
                        com.rameses.rcp.annotations.Controller a = f.getAnnotation(com.rameses.rcp.annotations.Controller.class);
                        classIndex.put(clazz, f); 
                        fireOnreadyCallback(a, o);
                    }
                    return; 
                } 
            }
            if (clazz.getSuperclass() != null) {
                injectController(o, clazz.getSuperclass(), u);
            }
        }
    }
    
    private boolean setValue(Field f, Object owner, Object value) {
        boolean accessible = f.isAccessible();        
        try { 
            f.setAccessible(true); 
            f.set(owner, value); 
            return true; 
        } catch(Throwable ex) { 
            System.out.println("ERROR injecting @Controller "  + ex.getMessage() );
            return false;  
        } finally { 
            f.setAccessible(accessible); 
        }        
    }
    
    private void fireOnreadyCallback(com.rameses.rcp.annotations.Controller a, Object bean) {
        if (a == null || bean == null) return;
        
        String onready = (a == null? null: a.onready()); 
        if (onready == null || onready.length() == 0) return;
        
        Class clazz = bean.getClass();
        Method method = null; 
        try { 
            method = clazz.getMethod(onready, new Class[]{});
        } catch(Throwable t) {;} 
        
        try {
            if (method == null) return;
            
            method.invoke(bean, new Object[]{}); 
        } catch (RuntimeException re) {
            throw re;
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex); 
        } 
    }     
}
