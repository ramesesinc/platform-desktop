/*
 * WorkflowController.java
 *
 * Created on June 6, 2014, 8:45 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris2.common;

import com.rameses.common.MethodResolver;
import com.rameses.osiris2.client.InvokerProxy;
import com.rameses.rcp.common.Action;
import com.rameses.rcp.common.Modal;
import com.rameses.rcp.common.MsgBox;
import com.rameses.rcp.framework.Binding;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Elmo
 */
public abstract class WorkitemController {
    
    @com.rameses.rcp.annotations.Binding
    private Binding binding;
    
    private Map entity = new HashMap();
    private List formActions = new ArrayList();
    private List extActions = new ArrayList();
    private String title;
    private String message;
    private WorkitemServiceProxy _wfService;
    
    //contains list of tasks that are not selected
    private Map workitem;
    private List tasks = new ArrayList();
    
    public void beforeOpen(Object entity){;}
    public void afterOpen(Object entity){;}
    public void beforeSubmit(Object task){;}
    public void afterSubmit(Object result){;}
    
    public Object getService() {
        return null;
    }
    
    public String getServiceName() {
        throw new RuntimeException("Service name not specified");
    }
    
    private WorkitemServiceProxy getWfService() {
        if(_wfService!=null) return _wfService;
        Object ws = getService();
        if(ws!=null) {
            _wfService = new WorkitemServiceProxy( ws );
        } else {
            _wfService = new WorkitemServiceProxy( InvokerProxy.getInstance().create( getServiceName() ));
        }
        return _wfService;
    }
    
    
    //called when opening a task
    public Object open() throws Exception {
        if(entity.get("workitemid")==null)
            throw new Exception("Workitemid is required");
        beforeOpen(entity);
        
        formActions.clear();
        formActions.add( new Action("_close", "Close", null) );
        formActions.add( new Action("closeWorkitem", "Submit", null) );
        
        Map map = new HashMap();
        map.put("objid", entity.get("workitemid"));
        workitem = getWfService().openWorkitem( map );
        message = (String) workitem.get("message");
        
        if(workitem.get("data")!=null) {
            entity = (Map)workitem.get("data");
        } else {
            MsgBox.warn("Warning. Please add a data in task info. Entity will be empty");
        }
        afterOpen( workitem );
        return "default";
    }
    
    // <editor-fold defaultstate="collapsed" desc=" Getter/setter ">
    public Binding getBinding() {
        return binding;
    }
    
    public void setBinding(Binding  binding) {
        this.binding = binding;
    }
    
    public Map getEntity() {
        return entity;
    }
    
    public void setEntity(Map entity) {
        this.entity = entity;
    }
    
    public List getFormActions() {
        return formActions;
    }
    
    public List getExtActions() {
        return extActions;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getMessage() {
        return message;
    }
    
    // </editor-fold>
    
    public class WorkitemServiceProxy  {
        private Object proxy;
        private MethodResolver resolver = MethodResolver.getInstance();
        public WorkitemServiceProxy(Object proxy ) {
            this.proxy = proxy;
        }
        public Map openWorkitem(Map p) throws Exception {
            return (Map)resolver.invoke( proxy, "openWorkitem", new Object[]{p} );
        }
        public Map closeWorkitem(Map p) throws Exception {
            return (Map)resolver.invoke( proxy, "closeWorkitem", new Object[]{p} );
        }
    }
    
    public Map getWorkitem() {
        return workitem;
    }
    
    public Object closeWorkitem() throws Exception {
        Map req = new HashMap();
        req.put("task", req);
        Modal.show( "taskmessage:create", req );
        if( req.get("message")==null ) return null;
        
        Map r = new HashMap();
        r.put("objid", workitem.get("objid"));
        r.put("remarks", req.get("message"));
        getWfService().closeWorkitem( r );
        MsgBox.alert("Work item completed");
        return "_close";        
    }
    
    
}

