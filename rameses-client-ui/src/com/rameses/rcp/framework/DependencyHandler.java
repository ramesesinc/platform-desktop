/*
 * DependencyHandler.java
 *
 * Created on February 8, 2014, 11:24 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.framework;

/**
 *
 * @author wflores
 */
public interface DependencyHandler 
{
    Class getAnnotation();
    Object getResource(Binding binding);
    
}
