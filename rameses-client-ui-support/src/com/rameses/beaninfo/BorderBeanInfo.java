/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.beaninfo;

import java.beans.PropertyDescriptor;
import java.util.List;

/**
 *
 * @author wflores
 */
public class BorderBeanInfo extends DefaultBeanInfo {

    private Class beanClass; 
    
    public BorderBeanInfo( Class beanClass ) {
        this.beanClass = beanClass; 
    }
    
    public Class getBeanClass() {
        return beanClass; 
    }

    protected void loadProperties(List<PropertyDescriptor> list) {
        add( list, "border" );
    } 
}
