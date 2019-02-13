/*
 * FormPanelModel.java
 *
 * Created on January 25, 2011, 11:31 AM
 */

package com.rameses.rcp.common;

import com.rameses.common.PropertyResolver;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author jaycverg
 * @modifiedby wflores
 */
public class FormPanelModel {
    
    private Listener listener;
    
    public Object getFormControls() { return null; }    
    public List<Map> getControlList() { return null; } 
    
    public void setProperty(String name, Object value) {
        if( listener != null ) {
            listener.onPropertyUpdated(name, value);
        }
    }
    
    public void setProperties(Map props) {
        if ( listener == null ) return;
        
        for(Map.Entry<String, Object> me : (Set<Map.Entry>) props.entrySet()) {
            listener.onPropertyUpdated(me.getKey()+"", me.getValue());
        }
    }
    
    public String getCategory(String id) { return id; } 
    
    public String getHtmlFormat() {
        if( listener != null ) {
            return listener.getHtmlFormat(false);
        }        
        return "";
    }
    
    public String getPartialHtmlFormat() {
        if( listener != null ) {
            return listener.getHtmlFormat(true);
        }        
        return "";
    }
    
    public void reload() {
        if( listener != null ) {
            listener.onReload();
        }
    }
    
    public void setListener(Listener listener) {
        this.listener = listener;
    }
    
    //This method can be overridden, if custom updating of value is necessary 
    public void updateBean(String name, Object value, Object userObject) {
        Object bean = (provider == null? null: provider.getBindingBean()); 
        if (bean != null) { 
            PropertyResolver.getInstance().setProperty(bean, name, value); 
        }
    } 

        
    // <editor-fold defaultstate="collapsed" desc=" Listener interface ">
    
    public static interface Listener {
        String getHtmlFormat(boolean partial);         
        void onPropertyUpdated(String name, Object value);
        void onReload(); 
    }  
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Provider interface and its helper methods ">

    private FormPanelModel.Provider provider; 

    public final void setProvider(FormPanelModel.Provider provider) {
        this.provider = provider; 
    } 

    public Object getBinding() {
        return (provider == null? null: provider.getBinding()); 
    }
    
    
    public static interface Provider {
        Object getBinding(); 
        Object getBindingBean(); 
    }  
    
    // </editor-fold>    
}
