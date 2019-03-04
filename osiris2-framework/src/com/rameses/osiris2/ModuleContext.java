/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris2;

/**
 *
 * @author elmonazareno
 */
public class ModuleContext {
    
    protected static final ThreadLocal<Module> threadLocal = new ThreadLocal();
    
    public static String getModuleConnection() {
        Module m = threadLocal.get();
        if(m!=null) return (String)m.getProperties().get("connection");
        return null;
    }
    
    public static final void set(Module mod) {
        threadLocal.set(mod);
    }
    
    public static final void remove() {
        threadLocal.remove();
    }
    
    public static final Module get() {
        return threadLocal.get();
    }
    
}
