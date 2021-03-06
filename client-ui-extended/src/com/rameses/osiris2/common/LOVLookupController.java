/*
 * LOVLookupController.java
 *
 * Created on June 25, 2013, 7:19 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris2.common;

import com.rameses.common.MethodResolver;
import com.rameses.rcp.annotations.FormTitle;
import com.rameses.rcp.common.CallbackHandlerProxy;
import com.rameses.rcp.common.Column;
import com.rameses.rcp.common.LookupModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 *
 * @author Elmo
 */
public class LOVLookupController extends LookupModel {
    
    private String key;
    private Object selectedEntity;
    private Object onselect;
    private boolean multiSelect = false;
    
    private Object beforeSelectItem;
    private Object afterSelectItem;
    
    private String connection;
    
    /** Creates a new instance of LOVLookupController */
    public LOVLookupController() {
    }
    
    public Object getListHandler() {
        return this;
    }
    
    @FormTitle
    public String getTitle() {
        return "List of Values (" + key + ")";
    }
    
    public Column[] getColumns() {
        return new Column[]{
            new Column("key","Key"),
            new Column("value","Value"),
        };
    }
    
    public List fetchList(Map params) {
        return (List)LOV.get(key, connection);
    }
    
    public String getKey() {
        return key;
    }
    
    public void setKey(String key) {
        this.key = key;
    }
    
    public Object getSelectedEntity() {
        return selectedEntity;
    }
    
    public void setSelectedEntity(Object selectedEntity) {
        this.selectedEntity = selectedEntity;
    }
    
    public String doSelect() throws Exception {
        Object value = getSelectedValue();
        if(onselect!=null && value!=null) {
            Object aresult = null;
            if( value instanceof Map) {
                aresult = ((Map)value).get("key");
            } else if(value instanceof List) {
                List list = new ArrayList();
                for( Object k : (List)value) {
                    list.add  (((Map)k).get("key"));
                }
                aresult = list;
            }
            Object result = MethodResolver.getInstance().invoke(onselect,"call", new Object[]{aresult} );
            if(result!=null && (result instanceof String))
                return result.toString();
        }
        return "_close";
    }
    
    public String doCancel() {
        return "_close";
    }
    
    public Object getOnselect() {
        return onselect;
    }
    
    public void setOnselect(Object onselect) {
        this.onselect = onselect;
    }
    
    public boolean isMultiSelect() {
        return multiSelect;
    }
    
    public void setMultiSelect(boolean multiSelect) {
        this.multiSelect = multiSelect;
    }
    
    public void setBeforeSelectItem( Object beforeSelectItem ) {
        this.beforeSelectItem = beforeSelectItem; 
    }
    public void setAfterSelectItem( Object afterSelectItem ) {
        this.afterSelectItem = afterSelectItem; 
    }
    
    
    private CallbackHandlerProxy callbackProxy = new CallbackHandlerProxy( null ); 
    
    protected void beforeSelectionChange( Object fact ) {
        if ( beforeSelectItem != null ) {
            callbackProxy.invoke( beforeSelectItem, fact ); 
        }
    }
    protected void afterSelectionChange( Object fact ) {
        if ( afterSelectItem != null ) {
            callbackProxy.invoke( afterSelectItem, fact ); 
        } 
    } 

    /**
     * @return the connection
     */
    public String getConnection() {
        return connection;
    }

    /**
     * @param connection the connection to set
     */
    public void setConnection(String connection) {
        this.connection = connection;
    }
}
