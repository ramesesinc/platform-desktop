package com.rameses.osiris2.common;

import com.rameses.common.MethodResolver;
import com.rameses.osiris2.client.InvokerFilter;
import com.rameses.osiris2.client.InvokerProxy;
import com.rameses.osiris2.client.InvokerUtil;
import com.rameses.osiris2.client.OsirisContext;
import com.rameses.rcp.annotations.Binding;
import com.rameses.rcp.annotations.ChangeLog;
import com.rameses.rcp.annotations.Invoker;
import com.rameses.rcp.common.Action;
import com.rameses.rcp.common.MsgBox;
import com.rameses.rcp.common.StyleRule;
import com.rameses.rcp.framework.ChangeLogHandler;
import com.rameses.util.MapEditor;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CRUDController 
{
    public final static String MODE_CREATE = "create";
    public final static String MODE_EDIT = "edit";
    public final static String MODE_READ = "read";
                
    @Invoker
    protected com.rameses.osiris2.Invoker invoker;
    
    @Binding
    protected com.rameses.rcp.framework.Binding binding;

    @ChangeLog
    private com.rameses.rcp.framework.ChangeLog changeLog; 
    
    private ListModelHandler listModelHandler;
    private List formActions; 
    private List navActions;   
    private List extActions;   

    private CRUDServiceProxy serviceProxy; 
    private MapEditor mapEditor;  
    private Map entity = new HashMap();    
    private String mode = MODE_READ;  
    
    protected String styleSource;
        
    public String getServiceName() {
        throw new NullPointerException("Please specify a serviceName"); 
    } 
    
    public String getEntityName() {
        throw new NullPointerException("Please specify an entityName"); 
    }
    
            
    // <editor-fold defaultstate="collapsed" desc=" Getter/Setter "> 
    
    public String getIcon() { return null; } 
    
    public String getPreferredTitle() {
        return (invoker == null? null: invoker.getCaption()); 
    }
    
    public String getTitle() { 
        //String text = (invoker == null? "Title": invoker.getCaption()); 
        String text = getPreferredTitle();
        if (text == null) text = "Title";
        
        if (MODE_CREATE.equals(getMode())) 
            return text + " (New)"; 
        else if (MODE_EDIT.equals(getMode())) 
            return text + " (Edit)"; 
        else 
            return text; 
    } 
    
    public final String getMode() { return mode; }
            
    public Map getEntity() { return entity; }    
    public void setEntity(Map entity) {
        this.entity = entity;        
        if (entity != null && entity.get("state") == null) {
            entity.put("state", "DRAFT"); 
        } 
        entityChanged(); 
    }
    
    protected void entityChanged(){} 
    
    public Object getService() 
    {
        String name = getServiceName();
        if (name == null || name.trim().length() == 0)
            throw new NullPointerException("Please specify a serviceName"); 
            
        return InvokerProxy.getInstance().create(name); 
    } 
    
    public final boolean isDirty() { 
        return changeLog.hasChanges(); 
    }
    
    public ListModelHandler getListModelHandler() { return this.listModelHandler; } 
    public void setListModelHandler(ListModelHandler listModelHandler) {
        this.listModelHandler = listModelHandler;
    }
    
    protected Map getChanges() 
    {
        final Map map = new HashMap();
        if (changeLog.hasChanges()) 
        {
            ChangeLogHandler handler = new ChangeLogHandler() 
            {
                private String fldname;

                public void clear(com.rameses.rcp.framework.ChangeLog log) {}
                public void end() {}
                public void endEntity() {}
                public void endField() {}
                public void showChange(Object oldValue, Object newValue) {
                    Map diff = new HashMap();
                    diff.put("oldvalue", oldValue);
                    diff.put("newvalue", newValue);
                    map.put(fldname, diff); 
                }
                public void showHistory(Date timeChanged, Object oldValue, Object newValue) {}
                public void start() {}
                public void startEntity(Object entity) {}
                public void startField(String fieldName) {
                    this.fldname = fieldName; 
                }
            };
            changeLog.scan(handler); 
            //System.out.println("Difference -> " + changeLog.getDifference());
        } 
        return map; 
    }     
    
    // </editor-fold>    
        
    // <editor-fold defaultstate="collapsed" desc=" Options "> 
        
    public boolean isAllowCreate() { return true; } 
    public boolean isAllowOpen() { return true; } 
    public boolean isAllowEdit() { return true; } 
    public boolean isAllowDelete() { return true; } 
    public boolean isAllowApprove() { return true; } 
    public boolean isShowFormActions() { return true; } 
    
    protected boolean isShowConfirmOnSave() { return true; }  
    protected boolean isShowNavigationBar() { return true; }
    
    public String getCreateFocusComponent() {  return "entity.code"; }    
    public String getEditFocusComponent() { return "entity.description"; }
    
    public String getConfirmSaveMsg() { return null; } 
    public String getConfirmApproveMsg() { return null; }
    public String getConfirmDeleteMsg() { return null; }
    
    public Map getCreatePermission() { return null; } 
    public Map getEditPermission() { return null; } 
    public Map getDeletePermission() { return null; } 
    public Map getApprovePermission() { return null; } 
    
    public boolean isDynamic() { return true; } 
    
    /*
     *  use as a prefix value in generating the UID key 
     */
    protected String getPrefixId() { return "MF"; }
    
    public List getStyleRules() {
        List list = new ArrayList();
        list.add(new StyleRule("entity.*", "#{mode=='read'}").add("enabled", false)); 
        list.add(new StyleRule("entity.*", "#{mode!='read'}").add("enabled", true));         
        return list; 
    }    
    
    // </editor-fold>
        
    // <editor-fold defaultstate="collapsed" desc=" Form / Navigation Actions "> 
    
    public boolean isAllowSave() { return true; } 
    
    public List getFormActions() {
        if (formActions == null) 
        {
            if (!isShowFormActions()) return null;
            
            formActions = new ArrayList();
            formActions.add(createAction("close", "Close", "images/toolbars/cancel.png", "ctrl C", 'c', "#{mode=='read'}", true));  
            
            //load create action
            Action a = createAction("init", "New", "images/toolbars/create.png", "ctrl N", 'n', "#{mode=='read' && allowCreate==true}", true); 
            loadPermission(a, getCreatePermission()); 
            formActions.add(a); 
            
            //load edit action
            a = createAction("edit", "Edit", "images/toolbars/edit.png", "ctrl E", 'e',  "#{mode=='read' && allowEdit==true && entity.state=='DRAFT'}", true); 
            loadPermission(a, getEditPermission()); 
            formActions.add(a); 
            
            //load delete action
            a = createAction("delete", "Delete", "images/toolbars/trash.png", null, 'd', "#{mode=='read' && allowDelete==true && entity.state=='DRAFT'}", true); 
            loadPermission(a, getDeletePermission()); 
            formActions.add(a); 
            
            //load approve action
            a = createAction("approve", "Approve", "images/toolbars/approve.png", null, 'v', "#{mode=='read' && allowApprove==true && entity.state=='DRAFT'}", true); 
            loadPermission(a, getApprovePermission()); 
            formActions.add(a); 
            
            //load supporting actions
            formActions.add(createAction("cancel", "Cancel", "images/toolbars/cancel.png", "ctrl C", 'c', "#{mode!='read'}", true)); 
            formActions.add(createAction("save", "Save", "images/toolbars/save.png", "ctrl S", 's', "#{mode!='read' && allowSave==true}", false)); 
            formActions.add(createAction("undo", "Undo", "images/toolbars/undo.png", "ctrl Z", 'u', "#{mode=='edit'}", true)); 
            
            List<Action> xactions = lookupActions("formActions");
            while (!xactions.isEmpty()) {
                formActions.add(xactions.remove(0)); 
            }
        } 
        return formActions;         
    }
    
    public List getNavActions() 
    {
        if (navActions == null) 
        {
            navActions = new ArrayList();
            if (isShowNavigationBar()) { 
                navActions.add(createAction("moveBackRecord", "Move to previous record", "images/toolbars/arrow_up.png", null, '\u0000', "#{navButtonVisible==true}", true));  
                navActions.add(createAction("moveNextRecord", "Move to next record", "images/toolbars/arrow_down.png", null, '\u0000', "#{navButtonVisible==true}", true));  
            }
        }
        return navActions;
    } 
    
    public boolean isNavButtonVisible() {
        if (!MODE_READ.equals(getMode())) return false; 
        
        return (getListModelHandler() instanceof PageListModelHandler); 
    }
    
    public List getExtActions() 
    {
        if (extActions == null) 
            extActions = lookupActions("extActions");
        
        return extActions;
    }     
        
    protected final List<Action> lookupActions(String type) { 
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
        
        for (int i=0; i<actions.size(); i++) {
            Action newAction = actions.get(i).clone();
            actions.set(i, newAction);
        }
        return actions;  
    }    
    
    private Action createAction(String name, String caption, String icon, String shortcut, char mnemonic, String visibleWhen, boolean immediate) 
    {
        Action a = new Action(name, caption, icon, mnemonic);
        if( shortcut != null ) a.getProperties().put("shortcut", shortcut);    
        if (visibleWhen != null) a.setVisibleWhen(visibleWhen); 
        
        a.setImmediate( immediate );
        return a;
    }      
    
    // </editor-fold> 
        
    protected Map createEntity() {
        return new LinkedHashMap();
    }
    
    protected void beforeCreate(Object data){}
    protected void afterCreate(Object data){}
    
    public final void init() { 
        Map data = createEntity();  
        if (data.get("objid") == null) 
            data.put("objid", getPrefixId() + new java.rmi.server.UID());
        if (data.get("state") == null) 
            data.put("state", "DRAFT"); 
        
        beforeCreate(data); 
        setEntity(data); 
        mode = MODE_CREATE; 
        afterCreate(data); 
        focusComponent(getCreateFocusComponent()); 
    }
    
    //this is to support elmo's crud implementation
    public Object create() {
        init(); 
        return null; 
    }
    
    protected void beforeCancel(){}
    protected void afterCancel(){}
    
    public Object cancel() {
        if (MODE_EDIT.equals(this.mode)) {
            if (!MsgBox.confirm("Changes will be discarded. Continue?")) return null; 
            
            beforeCancel();
            if (changeLog.hasChanges()) {
                changeLog.undoAll(); 
                changeLog.clear();
            }
            
            this.mode = MODE_READ; 
            afterCancel();
            return null; 
        }
        else {
            return close(); 
        }
    }
    
    protected void beforeClose(){} 
    protected void onClosing(){}
    
    public Object close() {
        beforeClose();
        this.mode = MODE_READ; 
        this.changeLog.clear(); 
        onClosing(); 
        return "_close"; 
    }
    
    @Deprecated
    protected void onbeforeSave(){}
    @Deprecated
    protected void onafterSave(){}
    
    protected void beforeSave(Object data){}
    protected void afterSave(Object data){}
    protected void afterSaveCreate(Object data){}    
    protected void afterSaveUpdate(Object data){}
        
    public void save(){
        try {
            onbeforeSave();
            Map oldData = getEntity();
            beforeSave(oldData);
            
            if (isShowConfirmOnSave()) {
                String msg = getConfirmSaveMsg();
                if (msg == null || msg.length() == 0) { 
                    msg = "You are about to save this record. Continue?";
                } 
                if (!MsgBox.confirm(msg)) return;
            }
            
            Map newData = null;
            if (MODE_CREATE.equals(this.mode)) {
                oldData.put("createdby", OsirisContext.getEnv().get("USERID")); 
                newData = getServiceProxy().create(oldData); 
                if (newData != null) setEntity(newData); 
            } 
            else if (MODE_EDIT.equals(this.mode)) {
                oldData.put("modifiedby", OsirisContext.getEnv().get("USERID")); 
                newData = getServiceProxy().update(oldData); 
                if (newData != null) setEntity(newData); 
            } 

            String oldmode = this.mode;
            this.mode = MODE_READ; 
            this.changeLog.clear();
            
            if (MODE_CREATE.equals(oldmode))
                afterSaveCreate(newData);
            else if (MODE_EDIT.equals(oldmode)) 
                afterSaveUpdate(newData);
            
            afterSave(getEntity()); 
            
            ListModelHandler lm = getListModelHandler();
            if (lm != null) {
                if (MODE_EDIT.equals(oldmode)) 
                    lm.updateItem(getEntity()); 
                else if (MODE_CREATE.equals(oldmode)) 
                    lm.addItem(getEntity());                 
            }
        }
        catch(Exception ex) {
            MsgBox.err(ex); 
        } 
    }
    
    protected void beforeOpen(Object params) {} 
    protected void afterOpen(Object data) {} 
    
    public Object open() {
        if (!isAllowOpen())
            throw new IllegalStateException("PERMISSION DENIED! Invoking this method is not authorized."); 
        
        Map params = getEntity();
        if (params == null) params = new HashMap();
        
        beforeOpen(params);
        Map data = getServiceProxy().open(params);        
        if (data == null) throw new NullPointerException("Record does not exist");
        
        if (data.get("state") == null) data.put("state", "DRAFT"); 
        
        setEntity(data); 
        mode = MODE_READ; 
        afterOpen(data); 
        return null; 
    }
    
    protected void beforeEdit(Object data) {} 
    protected void afterEdit(Object data) {} 
    
    public Object edit() {
        if (!isAllowEdit())
            throw new IllegalStateException("PERMISSION DENIED! Invoking this method is not authorized."); 
        
        Object data = getEntity();
        beforeEdit(data); 
        this.mode = MODE_EDIT; 
        afterEdit(data); 
        focusComponent(getEditFocusComponent()); 
        return null; 
    } 
    
    public void undo() {
        changeLog.undo(); 
    }    
    
    public Object approve() {
        String msg = getConfirmApproveMsg();
        if (msg == null || msg.length() == 0) {
            msg = "You are about to approve this document. Continue?";
        }
        
        if (MsgBox.confirm(msg)){
            Map data = getEntity();
            getServiceProxy().approve(data); 
            data.put("state", "APPROVED");
        } 
        return null; 
    } 
    
    protected void beforeDelete(Object data){}
    protected void afterDelete(Object data) {}
    
    public Object delete(){
        String msg = getConfirmDeleteMsg();
        if (msg == null || msg.length() == 0) {
            msg = "You are about to delete this document. Continue?";
        }
        
        if (MsgBox.confirm(msg)){
            Map data = getEntity();
            beforeDelete(data);
            getServiceProxy().removeEntity(data); 
            
            ListModelHandler lm = getListModelHandler();
            if (lm != null) lm.removeItem(data); 
            
            afterDelete(data);
            return close();
        }
        else {
            return null; 
        }
    }    
    
    public void moveBackRecord() {
        ListModelHandler lm = getListModelHandler();
        if (lm != null && lm instanceof PageListModelHandler){
            ((PageListModelHandler) lm).moveBackRecord(); 
            
            Object data = lm.getSelectedEntity(); 
            if (data == null) return;
            
            this.entity = (Map) data;
            open(); 
            
            if (binding != null) {
                if (isDynamic()) { 
                    formActions.clear();
                    formActions = null;
                } 
                binding.refresh();
            } 
        }
    }
    
    public void moveNextRecord() {
        ListModelHandler lm = getListModelHandler();
        if (lm != null && lm instanceof PageListModelHandler) {
            ((PageListModelHandler) lm).moveNextRecord();
            
            Object data = lm.getSelectedEntity(); 
            if (data == null) return;

            this.entity = (Map) data;
            open();          
            
            if (binding != null) {
                if (isDynamic()) { 
                    formActions.clear();
                    formActions = null;
                } 
                binding.refresh();
            } 
        }
    }    
      
    private void focusComponent(String ctrlname){
        if ( binding != null ) binding.focus(ctrlname); 
    }    
    
    private void loadPermission(Action a, Map perm) {
        if (perm == null || perm.isEmpty()) return; 
        
        a.setDomain(getString(perm, "domain")); 
        a.setRole(getString(perm, "role")); 
        a.setPermission(getString(perm, "permission")); 
    }
    
    private String getString(Map map, String name) {
        Object ov = (map == null? null: map.get(name)); 
        return (ov == null? null: ov.toString()); 
    }    
    
    // <editor-fold defaultstate="collapsed" desc=" CRUDServiceProxy "> 
    
    private CRUDServiceProxy getServiceProxy() {
        if (serviceProxy == null) 
            serviceProxy = new CRUDServiceProxy(); 
        if (serviceProxy.serviceObj == null) 
            serviceProxy.serviceObj = getService();

        return serviceProxy;
    }
    
    private class CRUDServiceProxy { 
        
        Object serviceObj;   
        MethodResolver resolver = MethodResolver.getInstance();
        
        Map create(Map data) { 
            return (Map) invoke("create", data); 
        } 

        Map open(Map data) {
            return (Map) invoke("open", data); 
        }

        Map update(Map data) {
            return (Map) invoke("update", data);  
        }

        void removeEntity(Map data) {
            invoke("removeEntity", data);  
        }

        Map approve(Map data) {
            return (Map) invoke("approve", data);  
        }     
        
        Object invoke(String methodName, Object data) {
            try { 
                if (serviceObj == null) 
                    throw new NullPointerException("No available service object");
                
                return resolver.invoke(serviceObj, methodName, new Object[]{data}); 
            } catch(RuntimeException re) {
                throw re;
            } catch(Exception re) { 
                throw new RuntimeException(re.getMessage(), re); 
            }
        }
    }
    
    // </editor-fold>
}
