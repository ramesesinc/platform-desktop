/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.framework;

import com.rameses.rcp.common.CallbackHandlerProxy;
import com.rameses.util.BreakException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author wflores 
 */
public class BindingValidatorSupport {
    
    private List<IFieldValidator> fields = new ArrayList(); 
    private List<IBeanValidator> beans = new ArrayList(); 
    
    public void addFieldValidator( Object obj ) {
        if ( obj == null ) return; 
        
        if ( obj instanceof Map ) {
            fields.add( new MapFieldValidator(obj) ); 
        } else {
            fields.add( new CallableFieldValidator(obj) ); 
        }
    }
    
    public void addBeanValidator( Object obj ) {
        if ( obj == null ) return; 
        
        beans.add(new CallableBeanValidator(obj)); 
    }
    
    public void removeValidators() {
        fields.clear();
        beans.clear(); 
    }
    
    public void fireBeanValidators( Object bean ) {
        for (IBeanValidator bv : beans) {
            bv.validate( bean ); 
        } 
    }
    
    public void fireFieldValidators( String key, Object value ) {
        for (IFieldValidator fv : fields) {
            fv.validate(key, value); 
        } 
    }
    
    private interface IFieldValidator {
        void validate(String key, Object value); 
    }
    private class CallableFieldValidator implements IFieldValidator {
        private Object source; 
        private CallbackHandlerProxy callback;
        
        CallableFieldValidator( Object source ) {
            this.source = source; 
            this.callback = new CallbackHandlerProxy(source); 
        }
        
        public void validate(String key, Object value) { 
            Object keyobj = key;
            callback.setHandleBreakException(false); 
            callback.call(new Object[]{keyobj, value}); 
        }
    }
    private class MapFieldValidator implements IFieldValidator {
        private Map source; 
        
        MapFieldValidator( Object source ) {
            this.source = (Map) source; 
        }
        
        public void validate(String name, Object value) { 
            if ( source == null ) return; 
            
            Iterator keys = source.keySet().iterator(); 
            while (keys.hasNext()) {
                Object okey = keys.next(); 
                Object oval = null; 
                if (okey == null && name == null) {
                    oval = source.get(okey); 
                } else {
                    String sfind = okey+"";
                    String sname = name+"";                    
                    if (sname.matches(sfind)) {
                        oval = source.get(okey); 
                    }
                }
                
                if ( oval == null) continue; 
                
                CallbackHandlerProxy callback = new CallbackHandlerProxy(oval);
                callback.setHandleBreakException(false); 
                callback.call( value ); 
            }
        }
    }
    
    private interface IBeanValidator {
        void validate( Object bean ); 
    }
    private class CallableBeanValidator implements IBeanValidator {
        private Object source; 
        private CallbackHandlerProxy callback;
        
        CallableBeanValidator( Object source ) {
            this.source = source; 
            this.callback = new CallbackHandlerProxy(source); 
        }
        
        public void validate( Object bean ) { 
            callback.setHandleBreakException(false); 
            callback.call( bean ); 
        }
    }
}
