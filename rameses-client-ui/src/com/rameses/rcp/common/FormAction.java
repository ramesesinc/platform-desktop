/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.common;

import com.rameses.common.MethodResolver;

/**
 *
 * @author Elmo Nazareno
 */
public class FormAction extends Action {
    
    private Object context;

   
    /**
     * @return the context
     */
    public Object getContext() {
        return context;
    }

    /**
     * @param context the context to set
     */
    public void setContext(Object context) {
        this.context = context;
    }

    public Object execute() {
        try {
            if(getParams() ==null ||getParams().isEmpty() ) {
                return MethodResolver.getInstance().invoke(context, getName(), new Object[]{} );
            }
            else {
                return MethodResolver.getInstance().invoke(context, getName(), new Object[]{ getParams() } );
            }
        }
        catch(Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    
    
}
