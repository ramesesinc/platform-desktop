/*
 * AbstractListForm.java
 *
 * Created on November 13, 2012, 10:46 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris2.client;

import com.rameses.common.MethodResolver;
import com.rameses.rcp.annotations.Controller;
import com.rameses.rcp.common.Action;
import com.rameses.rcp.common.MsgBox;
import com.rameses.rcp.common.Opener;
import com.rameses.rcp.common.StyleRule;
import com.rameses.util.ExceptionManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 *
 * @author Elmo
 *
 * If there are customizations in creating an object
 * call doCreate.
 *
 * If there are customizations in editing an object
 * call doEdit.
 *
 * If there are needed overrides in saving override
 * doSaveCreate and doSaveUpdate instead 
 */
public abstract class MasterListFormController {
    
    @Controller
    private WorkUnitUIController controller;
    
    public static String MODE_CREATE = "create";
    public static String MODE_READ = "read";
    public static String MODE_EDIT = "edit";
    
    private Object entity = new HashMap();
    
    public abstract Object getEntityService();
    
    
    private EntityUtil entityUtil;
    
    private EntityUtil getEntityUtil() {
        if(entityUtil==null){
            entityUtil = new EntityUtil(getEntityService());    
        }
        return entityUtil;
    }
    
    
    public Object getEntity() {
        return entity;
    }
    
    public void setEntity(Object o) {
        this.entity = o;
    }
    
    
    
    
    public abstract String getEntityName();
    public abstract String getRole();
    
    public String getDomain() {
        return controller.getWorkunit().getModule().getDomain();
    }
    
    
    public String getPageTitle() {
        return controller.getWorkunit().getWorkunit().getTitle();
    }
    
    public void doCreateObject() {
        this.entity = new HashMap();
    }
    
    public void doEditObject() {
        //do nothing
    }
    
    private String mode = MODE_READ;
    
    public List<StyleRule> getStyleRules() {
        List<StyleRule> styles = new ArrayList();
        styles.add( new StyleRule("entity.*", "mode=='create'").add("enabled",true));
        styles.add( new StyleRule("entity.*", "mode=='edit'" ).add("enabled",true));
        styles.add( new StyleRule("entity.*", "mode=='read'" ).add("enabled",false));
        return styles;
    }
    
    public Opener getForm() {
        Opener o = new Opener();
        o.setOutcome( MODE_READ );
        return o;
    }
    
    private void addAction( String name, String caption, List<Action> actions ) {
        if(!OsirisContext.getSession().getSecurityProvider().checkPermission( getDomain(),getRole(),name+getEntityName() )) {
            return;
        }
        Action a = new Action(name, caption,null);
        actions.add(a);
    }
    
    public List<Action> getActions() {
        List<Action> actions = new ArrayList();
        if(mode.equals(MODE_CREATE)) {
            addAction( "save", "Save", actions );
            addAction( "cancel", "Cancel", actions );
        } else if(mode.equals(MODE_READ)) {
            addAction( "create", "New", actions );
            addAction( "edit", "Edit",actions );
        } else if(mode.equals(MODE_EDIT)) {
            addAction( "save", "Save", actions );
            addAction( "cancel", "Cancel", actions );
        }
        return actions;
    }
    
    public Object save() throws Exception{
        Object retval = null;
        if( MsgBox.confirm("You are about to save this record. Continue?")) {
            if( this.mode.equals(MODE_CREATE)) {
                retval = doSaveCreate();
            } else if( this.mode.equals(MODE_EDIT)) {
                retval = doSaveUpdate();
            }
            this.mode = MODE_READ;
            if(retval==null)
                return this.mode;
        }
        return retval;
    }
    
    //overridable
    public Object doSaveCreate() throws Exception{
        this.entity = getEntityUtil().saveCreate(this.getEntity());
        return null;
    }
    
    public Object doSaveUpdate() throws Exception {
        this.entity = getEntityUtil().saveUpdate(this.getEntity());
        return null;
    }
    
    public String create() {
        this.mode = MODE_CREATE;
        doCreateObject();
        return this.mode;
    }
    
    public String edit() {
        this.mode = MODE_EDIT;
        doEditObject();
        return this.mode;
    }
    
    public String cancel() {
        this.mode = MODE_READ;
        return this.mode;
    }
    
    public String getMode() {
        return mode;
    }
    
    
    public static interface EntityIntf {
        List getList( Object qry) throws Exception;
        Object saveCreate(Object data) throws Exception;
        Object saveUpdate(Object data) throws Exception;
        Object remove(Object data) throws Exception;
        Object open(Object data) throws Exception;
    }
    
    private class EntityUtil implements EntityIntf {
        
        private Object service;
        public EntityUtil(Object s) {
            this.service = s;
        }

        private Object execute(String name, Object args) throws Exception {
            try {
                MethodResolver mr = MethodResolver.getInstance();
                return mr.invoke(service,name,new Object[]{args});
            }
            catch(Exception e){
                throw ExceptionManager.getOriginal(e);
            }
        }
        
        public List getList(Object qry)  throws Exception{
            return (List) execute("getList",qry);
        }

        public Object saveCreate(Object data)  throws Exception{
            return execute("saveCreate",data);
        }

        public Object saveUpdate(Object data) throws Exception {
            return execute("saveUpdate",data);
        }

        public Object remove(Object data) throws Exception {
            return execute("remove",data);
        }

        public Object open(Object data) throws Exception {
            return execute("open",data);
        }
        
    }
    
    
    
}
