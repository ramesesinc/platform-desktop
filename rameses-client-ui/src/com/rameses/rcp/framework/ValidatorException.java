/*
 * ValidatorException.java
 *
 * Created on October 1, 2013, 10:36 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.framework;

/**
 *
 * @author wflores
 */
public class ValidatorException extends RuntimeException 
{
    
    public ValidatorException(String message) {
        this(message, null); 
    }
    
    public ValidatorException(Throwable caused) {
        this((caused==null? null: caused.getMessage()), caused); 
    }    
    
    public ValidatorException(String message, Throwable caused) {
        super(message, caused); 
    }    
    
}
