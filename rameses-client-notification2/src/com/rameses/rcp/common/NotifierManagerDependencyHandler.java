/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.common;

import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.framework.DependencyHandler;
import java.util.Map;

/**
 *
 * @author wflores
 */
public class NotifierManagerDependencyHandler implements DependencyHandler {

    public Class getAnnotation() {
        return com.rameses.rcp.annotations.NotifierManager.class; 
    }

    public Object getResource(Binding binding) {
        return new NotifierManagerProxy( binding ); 
    }
    
    
    public class NotifierManagerProxy {
        
        private Binding binding;
        
        NotifierManagerProxy(Binding binding) {
            this.binding = binding;
        }
        
        public NotifierSession register( String id ) { 
             return NotifierManager.register(id); 
        }
        
        public NotifierSession register( String id, Object handler ) { 
             return NotifierManager.register(id, handler); 
        }

        public NotifierSession register( String id, Object handler, Map options ) { 
             return NotifierManager.register(id, handler, options); 
        }
        
        public String unregister( String id ) { 
            return NotifierManager.unregister(id); 
        }

        public void notify( String id ) {
            NotifierManager.notify(id); 
        }
    }    
}
