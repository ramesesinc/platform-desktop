package com.rameses.osiris2.common;

import com.rameses.osiris2.client.InvokerFilter;
import com.rameses.osiris2.client.InvokerUtil;
import com.rameses.osiris2.client.WorkUnitUIController;
import com.rameses.rcp.annotations.Binding;
import com.rameses.rcp.annotations.Controller;
import com.rameses.rcp.annotations.Invoker;
import com.rameses.rcp.common.AbstractListDataProvider;
import com.rameses.rcp.common.Action;
import com.rameses.rcp.common.Column;
import com.rameses.rcp.common.MsgBox;
import com.rameses.rcp.common.Opener;
import com.rameses.rcp.common.PageListModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BasicListController extends PageListModel 
{    
    @Invoker
    protected com.rameses.osiris2.Invoker invoker;
    
    @Binding
    private com.rameses.rcp.framework.Binding binding;
    
    @Controller(onready="onready_controller")
    protected WorkUnitUIController controller;
    
    private Object selectedEntity;
    private List<Action> formActions;
    
    public abstract List fetchList(Map params); 
    
    public Column[] getColumns() {
        Column[] columns = super.getColumns();
        if (columns != null) return columns;
        
        List<Map> list = getColumnList();
        if (list == null) return null;

        columns = new Column[list.size()]; 
        for (int i=0; i<columns.length; i++) {
            columns[i] = new Column(list.get(i));
        }
        return columns; 
    } 
    
    
    // <editor-fold defaultstate="collapsed" desc=" Getter/Setter ">        
        
    public final AbstractListDataProvider getListHandler() { return this; } 
    
    public com.rameses.rcp.framework.Binding getBinding() { return binding; } 
    
    public String getTitle() { return invoker.getCaption(); } 
    
    public Object getSelectedEntity() { return selectedEntity; }
    public void setSelectedEntity(Object selectedEntity) {
        this.selectedEntity = selectedEntity;
    }    
              
    public String getFormTarget() { return "popup"; }    
            
    public Opener getQueryForm() { return null; }    
        
    // </editor-fold>    
        
    // <editor-fold defaultstate="collapsed" desc=" Action Methods ">        

    public void search() {
        load(); 
    }    
    
    public Object open() throws Exception { 
        return null; 
    }
    
    public Object onOpenItem(Object o, String columnName) 
    {
        try
        {
            setSelectedEntity(o);
            if (o == null) return null;
            
            return open(); 
        }
        catch(Exception ex) 
        {
            MsgBox.err(ex); 
            return null; 
        } 
    }         
      
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" Form and Extended Actions ">  
    
    public List getFormActions() 
    { 
        if (formActions == null)
        {
            formActions = new ArrayList();
            try { 
                formActions.addAll(lookupActions("formActions")); 
            } catch(Exception ex) {;} 
        }
        return formActions;
    } 
    
    public List getNavActions() { 
        return new ArrayList(); 
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
            
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" helper methods ">      
    
    private Map wuprops = null;
    
    protected final Map getControllerProperties() {
        try {
            if (wuprops == null) {
                wuprops = controller.getWorkunit().getWorkunit().getProperties();
            } 
        } catch(Throwable t){;} 
            
        if (wuprops == null) wuprops = new HashMap();
        
        return wuprops;
    }
    
    public final void onready_controller() {
        handleWorkunitProperties(getControllerProperties()); 
    }
    
    protected void handleWorkunitProperties(Map props) {
        //to be implemented 
    }
    
    // </editor-fold>
    
}
