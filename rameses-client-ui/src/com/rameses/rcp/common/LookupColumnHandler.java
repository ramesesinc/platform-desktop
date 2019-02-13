/*
 * LookupColumnHandler.java
 *
 * Created on May 21, 2013, 11:46 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.common;

/**
 *
 * @author wflores
 */
public class LookupColumnHandler extends Column.TypeHandler implements PropertySupport.LookupPropertyInfo
{   
    private static final long serialVersionUID = 1L;
    private Object handler;
    private String expression;
    
    public LookupColumnHandler(){
    } 
    
    public LookupColumnHandler(String expression, Object handler) 
    {
        this.expression = expression;
        this.handler = handler;
    }
    
    public String getType() { return "lookup"; }
    
    public String getExpression() 
    {
        Object value = super.get("expression");
        if (value == null) value = this.expression;
        
        return (value == null? null: value.toString());
    }
    public void setExpression(String expression) {
        this.expression = expression;
    }

    public Object getHandler() 
    {
        Object value = super.get("handler");
        if (value == null) value = this.handler;
        
        return value; 
    }
    
    public void setHandler(Object handler) {
        this.handler = handler;
    }   
}
