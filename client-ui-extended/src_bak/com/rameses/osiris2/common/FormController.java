package com.rameses.osiris2.common;

import com.rameses.common.PropertyResolver;
import com.rameses.rcp.annotations.Binding;
import com.rameses.rcp.annotations.ChangeLog;
import com.rameses.rcp.annotations.Invoker;
import com.rameses.rcp.common.Action;
import com.rameses.rcp.framework.ChangeLogHandler;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormController 
{    
    @Invoker
    protected com.rameses.osiris2.Invoker invoker;

    @Binding
    protected com.rameses.rcp.framework.Binding binding;
    
    @ChangeLog
    private com.rameses.rcp.framework.ChangeLog changeLog; 
    
    private ListModelHandler listModelHandler; 
    private Object entity;
    
    
    public String getTitle() { 
        return (invoker == null? null: invoker.getCaption()); 
    }

    protected void entityChanged(){}
    
    public Object getEntity() { return entity; } 
    public void setEntity(Object entity) { this.entity = entity; }
    
    // <editor-fold defaultstate="collapsed" desc=" helper/override methods "> 
    
    public void initOpenerHandle(Object handle) {
        try {
            if (handle != null) { 
                Object o = getEntity();
                PropertyResolver.getInstance().setProperty(handle, "entity", o); 
            }
        } catch(Throwable t){;} 
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ChangeLog support "> 
    
    public final boolean isDirty() { 
        return (changeLog == null? false: changeLog.hasChanges());
    }

    public void clearChangeLog() { 
        if (changeLog != null && changeLog.hasChanges()) { 
            changeLog.undoAll(); 
            changeLog.clear(); 
        } 
    } 
    
    public void undoChangeLog() { 
        if (changeLog != null) changeLog.undo(); 
    } 
    
    protected Map getChanges() {
        final Map map = new HashMap();
        if (changeLog != null && changeLog.hasChanges()) {
            ChangeLogHandler handler = new ChangeLogHandler() {
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
        } 
        return map; 
    }        
    
    // </editor-fold> 
    
    // <editor-fold defaultstate="collapsed" desc=" ListModelHandler support "> 
    
    private List navActions; 
    
    public boolean isShowNavigationButtons() {
        return (getListModelHandler() instanceof PageListModelHandler); 
    } 
    
    public ListModelHandler getListModelHandler() { return this.listModelHandler; } 
    public void setListModelHandler(ListModelHandler listModelHandler) {
        this.listModelHandler = listModelHandler;
    }
        
    public List getNavActions() {
        if (navActions == null) {
            navActions = new ArrayList();
            if (getListModelHandler() instanceof PageListModelHandler) { 
                navActions.add(createAction("moveBackRecord", "Move to previous record", "images/toolbars/arrow_up.png", null, '\u0000', "#{showNavigationButtons==true}", true));  
                navActions.add(createAction("moveNextRecord", "Move to next record", "images/toolbars/arrow_down.png", null, '\u0000', "#{showNavigationButtons==true}", true));  
            }
        }
        return navActions;
    } 
    
    private Action createAction(String name, String caption, String icon, String shortcut, char mnemonic, String visibleWhen, boolean immediate) 
    {
        Action a = new Action(name, caption, icon, mnemonic);
        if( shortcut != null ) a.getProperties().put("shortcut", shortcut);    
        if (visibleWhen != null) a.setVisibleWhen(visibleWhen); 
        
        a.setImmediate( immediate );
        return a;
    }      
    
    public void moveBackRecord() {
        ListModelHandler lm = getListModelHandler();
        if (lm instanceof PageListModelHandler){
            ((PageListModelHandler) lm).moveBackRecord(); 
            
            Object data = lm.getSelectedEntity(); 
            if (data == null) return;
            
            setEntity(data);
            entityChanged(); 
            
            if (binding != null) binding.refresh(); 
        }
    }
    
    public void moveNextRecord() {
        ListModelHandler lm = getListModelHandler();
        if (lm instanceof PageListModelHandler) {
            ((PageListModelHandler) lm).moveNextRecord();
            
            Object data = lm.getSelectedEntity(); 
            if (data == null) return;

            setEntity(data);
            entityChanged(); 
            
            if (binding != null) binding.refresh(); 
        }
    } 
    
    // </editor-fold>
}
