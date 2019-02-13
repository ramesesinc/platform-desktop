package com.rameses.rcp.impl;

import com.rameses.rcp.framework.ClientContext;
import com.rameses.rcp.framework.ControllerProvider;
import com.rameses.rcp.framework.UIController;

/**
 *
 * @author jaycverg
 */
public class ControllerProviderImpl extends ControllerProvider {
    
    protected UIController provide(String name, UIController caller) {
        ClassLoader loader = ClientContext.getCurrentContext().getClassLoader();
        try {
            return (UIController) loader.loadClass(name).newInstance();
        } catch (RuntimeException re) {
            throw re; 
        } catch (Exception ex) {
            throw new IllegalStateException(ex.getMessage(), ex);
        }
    }
    
}
