/*
 * HtmlEditorModel.java
 *
 * Created on April 5, 2014, 10:31 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.common;

import java.util.List;
import java.util.Map;

/**
 *
 * @author wflores 
 */
public class HtmlEditorModel 
{
    public List fetchList(Map params) {
        return null; 
    }
    
    public Object getTemplate(Object item) {
        return (item == null? null: item.toString()); 
    }    
}
