package com.rameses.rcp.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataListModel extends AbstractListDataProvider 
{    
    private List DEFAULT_LIST = new ArrayList();    
    private Map query = new HashMap();

    public Map getQuery() { return query; } 
    
    public List fetchList(Map params) { return DEFAULT_LIST; }
    
    public AbstractListDataProvider getListHandler() { return this; } 
        
    public int getRows() 
    { 
        // to indicate that the rows are dynamic 
        // and will use the getRowCount instead
        return -1; 
    }

    public void search() {
        load();
    }
    
    protected void beforeFetchList(Object params){}
    protected void afterFetchList(Object params){}
    
    protected void onbeforeFetchList(Map params) {
        Map qry = getQuery(); 
        if (qry != null) params.putAll(qry); 
        
        beforeFetchList(params); 
    }
    
    protected void onafterFetchList(List list) {
        afterFetchList(list); 
    }  
}
