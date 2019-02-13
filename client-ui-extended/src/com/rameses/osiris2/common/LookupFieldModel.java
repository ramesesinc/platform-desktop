/*
 * LookupFieldModel.java
 *
 * Created on April 29, 2013, 1:14 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris2.common;

import com.rameses.rcp.common.Column;
import com.rameses.rcp.common.PageListModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author wflores
 */
public abstract class LookupFieldModel extends PageListModel
{
    
    public LookupFieldModel() {
    }

    public abstract String getDisplayName();
    public abstract Column[] getColumns();    
    
        
    // <editor-fold defaultstate="collapsed" desc=" Getter/Setter ">

    public String getTarget() { return "popup"; } 
    
    public Object getOpener() { return null; }
    
    
    // </editor-fold>
        
    // <editor-fold defaultstate="collapsed" desc=" Options ">    
    
    
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" PageListModel implementations ">

    public List fetchList(Map params) { 
        return new ArrayList(); 
    }
    
    public int getRows() { return 10; }
        
    // </editor-fold>
    
    
    protected void onselect(Object data) {}
    
}
