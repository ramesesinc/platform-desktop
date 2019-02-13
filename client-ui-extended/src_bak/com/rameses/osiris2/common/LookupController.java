/*
 * LookupController.java
 *
 * Created on May 3, 2013, 9:48 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris2.common;

import com.rameses.common.PropertyResolver;
import com.rameses.osiris2.client.InvokerFilter;
import com.rameses.osiris2.client.InvokerProxy;
import com.rameses.osiris2.client.InvokerUtil;
import com.rameses.osiris2.client.WorkUnitUIController;
import com.rameses.rcp.annotations.Binding;
import com.rameses.rcp.annotations.Controller;
import com.rameses.rcp.annotations.Invoker;
import com.rameses.rcp.common.AbstractListDataProvider;
import com.rameses.rcp.common.Action;
import com.rameses.rcp.common.Column;
import com.rameses.rcp.common.LookupModel;
import com.rameses.rcp.common.LookupSelector;
import com.rameses.rcp.common.MsgBox;
import com.rameses.rcp.common.Opener;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author wflores
 */
public abstract class LookupController extends LookupModel 
{
    @Invoker
    protected com.rameses.osiris2.Invoker invoker;
    
    @Binding
    private com.rameses.rcp.framework.Binding binding;
    
    @Controller
    protected WorkUnitUIController controller;    
    
    private boolean allowSearch;
    private Object selectedEntity;
    private Object onselect;
    private Object onempty;
    private String tag;
    private Object callerHandler;
    
    private String serviceName;
    private String entityName;
    private int preferredRowSize;
    private List showedResults;
    
    // this is injected by the caller for notification
    public Object getHandler() { return callerHandler; } 
    public void setHandler(Object handler) {
        this.callerHandler = handler; 
    }
    
    public String getTag() { 
        if (tag == null) {
            try { 
                Object ov = getControllerProperties().get("tag"); 
                tag = (ov == null? null: ov.toString()); 
            } catch(Throwable t){;} 
        }         
        return tag; 
    }
    
    public void setTag(String tag) { this.tag = tag; }
    
    public String getServiceName() { 
        if (serviceName == null) {
            try { 
                Object ov = getControllerProperties().get("serviceName"); 
                serviceName = (ov == null? null: ov.toString()); 
            } catch(Throwable t){;} 
            
            if (serviceName == null) 
                throw new RuntimeException("Please specify a serviceName"); 
        } 
        return serviceName;        
    }
    
    public String getEntityName() { 
        if (entityName == null) {
            try { 
                Object ov = getControllerProperties().get("entityName"); 
                entityName = (ov == null? null: ov.toString()); 
            } catch(Throwable t){;} 
        } 
        return entityName;      
    } 
    
    public Column[] getColumns() { return null; }

    public List<Map> getColumnList() {
        String name = getServiceName();
        if (name != null && name.length() > 0) {
            Map params = new HashMap();
            String stag = getTag();
            if (stag != null) params.put("_tag", stag);            
            
            return getService().getColumns(params); 
        } else {
            return null; 
        } 
    }
    
    public List fetchList(Map params) { 
        if (showedResults != null) {
            List list = new ArrayList();
            list.addAll(showedResults);
            showedResults.clear();
            showedResults = null; 
            return list; 
        }
        
        String name = getServiceName();
        if (name != null && name.length() > 0) {
            String stag = getTag();
            if (stag != null) params.put("_tag", stag);
            
            Map qry = getQuery();
            if (qry != null) params.putAll(qry); 
            
            return getService().getList(params); 
        } else {
            return new ArrayList(); 
        }
    }  
    
    public LookupController() {
        setSelector(new LookupSelectorImpl()); 
    }
    
    public void init() {}
    
    // <editor-fold defaultstate="collapsed" desc="  Getters/Setters  ">

    public boolean isAllowSearch() { return allowSearch; }
    public void setAllowSearch(boolean allowSearch) {
        this.allowSearch = allowSearch;
    }
    
    public Object getOnselect() { return onselect; }    
    public void setOnselect(Object onselect) { this.onselect = onselect; }
    
    public Object getOnempty() { return onempty; }    
    public void setOnempty(Object onempty) { this.onempty = onempty; }
    
    public Object getSelectedEntity() { return selectedEntity; } 
    public void setSelectedEntity(Object selectedEntity) { 
        this.selectedEntity = selectedEntity; 
    }
    
    public final AbstractListDataProvider getListHandler() { return this; } 
    
    public com.rameses.rcp.framework.Binding getBinding() { return binding; }  
    
    public Opener getQueryForm() { 
        if (isAllowSearch()) { 
            Opener o = new Opener();
            o.setOutcome("queryform");
            return o; 
        } else { 
            return null; 
        } 
    }    
    
    public List getFormActions() { 
        return new ArrayList(); 
    } 

    public int getRows() {
        if (preferredRowSize <= 0) {
            try {
                Object ov = getControllerProperties().get("rows");
                preferredRowSize = Integer.parseInt(ov.toString()); 
            } catch(Throwable t) {;} 
            
            if (preferredRowSize <= 0) preferredRowSize = 20;
        } 
        return preferredRowSize;
    }
    
    public List getNavActions() { 
        return new ArrayList(); 
    }
       
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Options ">  
    
    protected final List<Action> lookupActions(String type)
    {
        List<Action> actions = new ArrayList();
        try { 
            actions = InvokerUtil.lookupActions(type, new InvokerFilter() {
                public boolean accept(com.rameses.osiris2.Invoker o) { 
                    return o.getWorkunitid().equals(invoker.getWorkunitid()); 
                }
            }); 
        } catch(Throwable t) {
            System.out.println("[WARN] error lookup actions caused by " + t.getMessage());
        } 
        
        for (int i=0; i<actions.size(); i++) 
        {
            Action newAction = actions.get(i).clone();
            actions.set(i, newAction);
        }
        return actions; 
    }
        
    // </editor-fold>    
    
    public boolean show(String searchtext) {
        boolean success = super.show(searchtext);
        if (success) {
            showedResults = new ArrayList();
            showedResults.addAll(getDataList()); 
        } else {
            showedResults = null; 
        }
        return success;
    }    
    
    public void search() {
        load(); 
    }
    
    public Object open() throws Exception { return null; }    
    
    public final Object onOpenItem(Object item, String columnName) 
    {
        try
        {
            setSelectedEntity(item);
            if (item == null) return null;
            
            return open(); 
        }
        catch(Exception ex) 
        {
            MsgBox.err(ex); 
            return null; 
        } 
    }  
    
    public Object doSelect() 
    {
        onselect(getSelectedEntity());        
        return super.select(); 
    }
    
    public Object doCancel() {
        return super.cancel(); 
    }    
    
    protected void onselect(Object item) {} 
    
    // <editor-fold defaultstate="collapsed" desc=" Helper methods for the ListService ">
    
    private ListService oListService;
    
    protected ListService getService()
    {
        if (oListService == null) {
            String name = getServiceName();
            if (name == null || name.length() == 0)
                throw new RuntimeException("No service name specified"); 
            
            oListService = (ListService) InvokerProxy.getInstance().create(name, ListService.class);
        }
        return oListService;
    }     
    
    // </editor-fold>
        
    // <editor-fold defaultstate="collapsed" desc=" LookupSelectorImpl (class) ">  
    
    private class LookupSelectorImpl implements LookupSelector
    {
        LookupController root = LookupController.this;
        
        public Object select(Object o) {
            Object outcome = invokeCallbackHandler(root.getOnselect(), o);
            if (outcome == null || "_close".equals(outcome)) return "_close"; 
            
            Object callerHandler = root.getHandler();
            if (callerHandler != null) invokeCallbackHandler(callerHandler, o);
            
            return null; 
        } 

        public void cancelSelection() {
        }
        
        private Object invokeCallbackHandler(Object callback, Object item)
        {
            if (callback == null) return null;

            Method method = null;         
            Class clazz = callback.getClass();
            try { method = clazz.getMethod("call", new Class[]{Object.class}); }catch(Exception ign){;} 

            try {
                if (method != null) { 
                    return method.invoke(callback, new Object[]{item}); 
                } else {
                    return null; 
                }
            } catch(RuntimeException re) {
                throw re;
            } catch(Exception ex) {
                throw new IllegalStateException(ex.getMessage(), ex); 
            }
        }         
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" helper methods ">      
    
    private Map wuprops = null;
    
    protected final Map getControllerProperties() {
        try {
            if (wuprops == null) {
                PropertyResolver res = PropertyResolver.getInstance(); 
                wuprops = (Map) res.getProperty(controller, "workunit.workunit.properties"); 
            } 
        } catch(Throwable t){;} 
            
        if (wuprops == null) wuprops = new HashMap();
        
        return wuprops;
    }
    
    // </editor-fold>    

}
