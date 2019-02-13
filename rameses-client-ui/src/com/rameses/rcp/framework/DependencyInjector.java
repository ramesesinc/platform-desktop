/*
 * DependencyInjector.java
 *
 * Created on February 8, 2014, 11:28 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.framework;

import com.rameses.util.Service;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author wflores
 */
public final class DependencyInjector 
{
    private Map<Class,DependencyHandler> handlers;

    public DependencyInjector() {
        handlers = Collections.synchronizedMap(new HashMap());
        Iterator itr = Service.providers(DependencyHandler.class, ClientContext.getCurrentContext().getClassLoader()); 
        while (itr.hasNext()) {
            Object o = itr.next();
            if (!(o instanceof DependencyHandler)) continue;
            
            DependencyHandler dh = (DependencyHandler)o;
            handlers.put(dh.getAnnotation(), dh);
        }
    }
    
    public Object getResource( Annotation anno, Binding binding ) {
        if (anno == null) return null;
        
        DependencyHandler dh = handlers.get(anno.annotationType());
        if (dh == null) return null;
        
        return dh.getResource(binding);
    }
}
