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
import com.rameses.osiris2.Invoker;
import com.rameses.osiris2.client.InvokerFilter;
import com.rameses.osiris2.client.InvokerProxy;
import com.rameses.osiris2.client.InvokerUtil;
import com.rameses.osiris2.client.WorkUnitUIController;
import com.rameses.rcp.common.Action;
import com.rameses.rcp.common.Modal;
import com.rameses.rcp.common.MsgBox;
import com.rameses.rcp.common.PopupMenuOpener;
import com.rameses.rcp.framework.Binding;
import com.rameses.util.BreakException;
import com.rameses.util.Warning;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 *
 * @author Elmo
 */
public abstract class WorkflowController {
    
    @com.rameses.rcp.annotations.Binding
    private Binding binding;
    
    @com.rameses.rcp.annotations.Controller
    private WorkUnitUIController controller;
    
    private Map entity = new HashMap();
    private List formActions = new ArrayList();
    private List extActions = new ArrayList();
    private String title;
    private String message;
    private WorkflowServiceProxy _wfService;
    
    
    //contains list of tasks that are not selected
    private Map task;
    private List tasks = new ArrayList();
    private List messagelist = new ArrayList();
    
    private List<WorkflowAction> workActions = new ArrayList();
    
    public void beforeOpen(Object o){;}
    public void afterOpen(Object o){;}
    public void beforeSignal(Object task){;}
    public void afterSignal(Object result){;}
    public Object handleWarning(Warning w){ 
        throw w; 
    } 
    
    public Object getService() {
        return null;
    }
    
    public Object findPage(Map task) {
        return null;
    }
    
    public String getServiceName() {
        throw new RuntimeException("Service name not specified");
    }
    
    public void buildExtActions() {
        extActions.clear();
        //build workitem types if any.
        if(task!=null) {
            if( task.get("workitemtypes")!=null ) {
                List list = (List)task.get("workitemtypes");
                if( list!=null && list.size()>0) {
                    extActions.add( new Action("addWorkitem", "Add Work Item", null)  );
                }
            }
        }
//        final String id = controller.getWorkunit().getWorkunit().getId();
//        List list = InvokerUtil.lookupActions( "extActions", new InvokerFilter(){
//            public boolean accept(Invoker invoker) {
//                return invoker.getWorkunitid().equals(id);
//            }
//            
//        });
//        for(Object a: list) {
//            extActions.add( ((Action) a).clone() ); 
//        }
    }
    
    private void buildFormActions(Map task) {
        boolean pass = false;
        try {
            pass = Boolean.parseBoolean( task.get("owner")+"");
        } catch(Exception ign){;}
        if(!pass) return;
        List<Map> transitions = (List)task.get("transitions");
        if(transitions==null) return;
        workActions.clear();
        for(Map t: transitions) {
            Map props = (Map)t.get("properties");
            if ( props == null ) { 
                props = new HashMap();
                t.put("properties", props); 
            }
            
            boolean visible = true;
            if(props.get("visible")!=null) {
                try {visible = Boolean.parseBoolean( props.get("visible").toString());}catch(Exception ign){;}
            }

            String name = (String)t.get("action");
            WorkflowAction wa = new WorkflowAction(name,task, t);
            if( visible )formActions.add(wa );
            workActions.add(wa);
        }
        
        buildExtActions();
        
        //set the message also
        message = null;
        if(task.get("message")!=null ) {
            message = (String)task.get("message");
            messagelist.clear();
            messagelist.add(message);
        }
    }

    /**
     * @return the messagelist
     */
    public List getMessagelist() {
        return messagelist;
    }
    
    private class WorkitemAction extends Action {
        private Map workitem;
        
        public WorkitemAction(Map m) {
            this.workitem = m;
        }
        public Object execute() {
            Map req = new HashMap();
            req.put("name", workitem.get("name"));
            req.put("taskid", task.get("objid"));
            req.put("action", workitem.get("action"));
            req.put("refid", task.get("refid"));
            req.put("workitemid", workitem.get("objid"));
            Map params = new HashMap();
            params.put("task", req);
            params.put("assignees", workitem.get("assignees"));
            Map opt = new HashMap();
            opt.put("title", getCaption());
            Modal.show( "taskmessage:create", params, opt );
            if( req.get("message") == null ) return null;
            try {
                getWfService().addWorkItem( req );
                MsgBox.alert(getCaption() + " workitem created");
                return null;
            } catch(Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    public Object addWorkitem() {
        PopupMenuOpener opener = new PopupMenuOpener();
        for(Object m: (List)task.get("workitemtypes")) {
            Map o = (Map)m;
            Action a = new WorkitemAction(o);
            a.setCaption( o.get("title")+"" );
            opener.add( a );
        }
        return  opener;
    }
    
    private WorkflowServiceProxy getWfService() {
        if(_wfService!=null) return _wfService;
        Object ws = getService();
        if(ws!=null) {
            _wfService = new WorkflowServiceProxy( ws );
        } else {
            _wfService = new WorkflowServiceProxy( InvokerProxy.getInstance().create( getServiceName() ));
        }
        return _wfService;
    }
    
    
    //called when opening a task
    public Object open() throws Exception {
        beforeOpen(entity);
        if(entity.get("taskid")==null) {
            throw new Exception("Please indicate a taskid in the selectedItem");
        }
        Map map = new HashMap();
        map.put("taskid", entity.get("taskid"));
        task = getWfService().openTask( map );
        
        formActions.clear();
        formActions.add(new Action("_close", "Close", null));
        buildFormActions(task);
        
        if(task.get("data")!=null) {
            entity = (Map)task.get("data");
        } else {
            MsgBox.warn("Warning. Please add a data in task info. Entity will be empty");
        }
        
        afterOpen( entity );
        Object nextPage = findPage(task);
        if(nextPage==null) return "default";
        return nextPage;
    }
    
    public Object signal() throws Exception {
        if(workActions.size()>0) {
            WorkflowAction wfa = workActions.iterator().next();
            return wfa.execute();
        } else {
            throw new Exception("No action to signal found");
        }
    }
    
    public void onEnd() {
        //need to override here.
    }
    
    public Object signal(  String action ) throws Exception {
        if(action ==null) throw new Exception("Please provide an action in signal");
        WorkflowAction wfa = null;
        for(WorkflowAction wa: workActions) {
            if( action.equals(wa.getName())) {
                wfa = wa;
                break;
            }
        }
        if(wfa == null) throw new Exception("Action " + action + " not found");
        return wfa.execute();
    }
    
    private Object invokeSignal(  Map req ) throws Exception {
        formActions.clear();
        formActions.add(new Action("_close", "Close", null));
        Map result = getWfService().signal( req );
        
        if( result.get("task")==null ) return null;
        this.task = (Map)result.get("task");
        this.tasks = (List)result.get("tasks");
        buildFormActions( this.task );
        return result;
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
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    // </editor-fold>
    
    public class WorkflowServiceProxy  {
        
        private Object proxy;
        private MethodResolver resolver = MethodResolver.getInstance();
        
        public WorkflowServiceProxy(Object proxy ) {
            this.proxy = proxy;
        }
        
        public Map openTask(Map p) throws Exception {
            return (Map)resolver.invoke( proxy, "openTask", new Object[]{p} );
        }
        
        public Map signal(Map p) throws Exception {
            return (Map)resolver.invoke( proxy, "signal", new Object[]{p} );
        }
        
        public List getSubTaskTypes(Map p) throws Exception{
            return (List)resolver.invoke( proxy, "getSubTaskTypes", new Object[]{p} );
        }
        
        public void addWorkItem(Map p) throws Exception {
            resolver.invoke( proxy, "addWorkitem", new Object[]{p} );
        }
    }
    
    public class WorkflowAction extends Action {
        private Map task;
        private String confirm;
        private String messageHandler;
        private List assignees;
        private boolean closeOnEnd;
        private String tag; 
        
        public WorkflowAction(String n, Map task, Map t) {
            super(n);
            this.task = task;
            if(task.get("domain")!=null) setDomain( task.get("domain").toString() );
            if(task.get("role")!=null) setRole( task.get("role").toString() );
            if(t.get("permission")!=null) setPermission( t.get("permission").toString() );
            if(t.get("assignees")!=null) {
                this.assignees = (List)t.get("assignees");
            }
            String caption = getName();
            messageHandler = null;
            Map props = (Map)t.get("properties");
            if(props!=null) {
                if( props.get("caption")!=null) caption = props.get("caption").toString();
                if( props.get("confirm")!=null) confirm = props.get("confirm").toString();
                if( props.get("messagehandler")!=null) {
                    messageHandler = props.get("messagehandler").toString();
                    if( messageHandler.equalsIgnoreCase("default")) {
                        messageHandler = "taskmessage:create";
                    }
                }
                if(props.get("closeonend")!=null) {
                    try { closeOnEnd = Boolean.parseBoolean( props.get("closeonend")+"" ); } catch(Exception e){;}
                }
                
                Object val = props.get("tag"); 
                tag = ( val == null ? null: val.toString()); 
            }
            if(caption==null) caption = "Submit";
            setCaption(caption);
        }
        
        public Object execute() {
            try {
                if(task.get("state")==null)
                    throw new Exception("WorkflowController.execute error. There is no state in task.");
                
                String action = getName();
                Map req = new HashMap();
                req.put("taskid", task.get("objid"));
                req.put("refid", task.get("refid"));
                req.put("data", entity);
                req.put("state", task.get("state"));
                req.put("extended", task.get("extended"));
                if (action!=null && action.trim().length()>0) req.put("action", action);
                if ( tag != null ) req.put("tag", tag); 
                
                beforeSignal(req);
                if( confirm !=null ) {
                    boolean pass = MsgBox.confirm(confirm);
                    if(!pass) return null;
                }
                
                if( messageHandler != null ) {
                    final Map resmap = new HashMap();
                    Callable c = new Callable() {
                        public Object call() throws Exception {
                            resmap.put("pass", true); 
                            return null; 
                        }
                    };
                    
                    try {
                        Map params = new HashMap();
                        params.put("task", req);
                        params.put("assignees", assignees);
                        params.put("handler", c);
                        Map opt = new HashMap();
                        opt.put("title", getCaption());
                        Modal.show( messageHandler, params, opt );
                        if (!resmap.containsKey("pass")) throw new BreakException();
                    } catch(BreakException be) { 
                        throw be; 
                    } catch(Exception e) {
                        e.printStackTrace();
                        throw new Exception("Error on displaying "+messageHandler, e);
                    }
                }
                
                Object result = invokeSignal(req);
                if(result==null) {
                    //if owner is not set, we must close the task to avoid loading errors.
                    onEnd();
                    if(closeOnEnd)
                        return "_close";
                    else
                        return null;
                }
                afterSignal(result);
                binding.refresh();
                return findPage(task);
            } catch(Warning w) {
                return handleWarning(w); 
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(),e);
            }
        }
    }
    
    public Map getTask() {
        return task;
    }
    
    
    
    public List getExtActions() {
        return extActions;
    }
    
    
    
}

