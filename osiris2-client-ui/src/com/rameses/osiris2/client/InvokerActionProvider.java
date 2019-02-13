package com.rameses.osiris2.client;

import com.rameses.osiris2.Folder;
import com.rameses.osiris2.Invoker;
import com.rameses.osiris2.SessionContext;
import com.rameses.rcp.common.Action;
import com.rameses.rcp.common.Opener;
import com.rameses.rcp.framework.ActionProvider;
import com.rameses.rcp.framework.UIController;
import com.rameses.util.ValueUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map; 

public class InvokerActionProvider implements ActionProvider 
{    
    public InvokerActionProvider() {}
    
    public boolean hasItems(String category, Object context) 
    {
        List list = OsirisContext.getSession().getInvokers( category );
        return (list.size() > 0);
    }
    
    //this should support wildcards. example /icon/* will display second level items instead
    public List<Action> getActions(String name, Object context) 
    {
        if ( !name.startsWith("/")) name = "/" + name;
        
        SessionContext app = OsirisContext.getSession();        
        List<Action> actions = new ArrayList<Action>();
        
        if ( name.endsWith("/*") ) 
        {
            name = name.substring(0, name.indexOf("/*"));
            
            List items = (List) app.getFolders(name);
            if (items != null) 
            {
                for (Object o: items) 
                {
                    Folder pf = (Folder)o;
                    for (Object ff: app.getFolders(pf)) 
                    {
                        Folder f = (Folder)ff;
                        if (f.getInvoker() != null) 
                        {
                            Action a = createAction(f.getInvoker(), context);
                            if (f.getParent() != null ) 
                                a.setCategory(f.getParent().getCaption());
                            
                            actions.add(a);
                        }
                    }
                }
            }            
        } 
        else 
        {
            List items = (List) app.getFolders(name);
            if (items != null) 
            {
                for (Object o: items) 
                {
                    Folder f = (Folder)o;
                    if (f.getInvoker() != null) 
                    {
                        Action a = createAction(f.getInvoker(), context);
                        if (f.getParent() != null ) 
                            a.setCategory(f.getParent().getCaption());
                        
                        actions.add(a);
                    }
                }
            }
        }
        return actions;
    }
    
    public List<Action> getActionsByType(String type, UIController controller) 
    {
        Object context = null;
        if ( controller != null ) context = controller.getCodeBean();
        
        List<Invoker> invList = InvokerUtil.lookup(type, context);
        List<Action> actions = new ArrayList();

        for (Invoker inv: invList) {
            if (!(controller instanceof WorkUnitUIController)) continue; 
            
            WorkUnitUIController wuc = (WorkUnitUIController) controller;
            String wucid = wuc.getWorkunit().getId();                 
            if (!wucid.equals(inv.getWorkunitid())) continue;
            
            actions.add(createAction(inv, context));
            //actions.add(new ActionInvoker(inv)); 
        }
        return actions;
    }
    
    public List<Action> lookupActions(String actionType) { 
        try { 
            List<Invoker> invokers = InvokerUtil.lookup(actionType);
            List<Action> actions = new ArrayList();
            for (Invoker invoker: invokers) {
                actions.add(new ActionInvoker(invoker)); 
            }
            return actions; 
        } catch(Throwable t) { 
            System.out.println("[WARN] failed to lookup actions '"+actionType+"' caused by " + t.getMessage());
            return new ArrayList(); 
        } 
    } 
    
    public Opener lookupOpener(String actionType, Map params) {
        try {
            return InvokerUtil.lookupOpener(actionType, params); 
        } catch(Throwable t) {
            System.out.println("[WARN] failed to lookup opener '"+actionType+"' caused by " + t.getMessage());
            return null; 
        } 
    } 
    
    private Action createAction(Invoker inv, Object context) 
    {
        Action a = new Action( inv.getName() == null? inv.getCaption()+"" :inv.getName() );
        
        Map invProps = new HashMap(inv.getProperties());
        a.setName( (String) inv.getAction() );
        a.setCaption( (String) inv.getCaption() );
        if (inv.getIndex() != null) a.setIndex(inv.getIndex());
        
        a.setIcon((String) invProps.remove("icon"));
        a.setImmediate( "true".equals(invProps.remove("immediate")+"") );
        a.setVisibleWhen( (String) invProps.remove("visibleWhen") );
        a.setUpdate( "true".equals(invProps.remove("update")+"") );
        
        String mnemonic = (String) invProps.remove("mnemonic");
        if ( !ValueUtil.isEmpty(mnemonic) ) a.setMnemonic(mnemonic.charAt(0));
        
        Object tooltip = invProps.remove("tooltip");
        if ( !ValueUtil.isEmpty(tooltip) ) a.setTooltip(tooltip+"");
        
        if ( !invProps.isEmpty() ) a.getProperties().putAll( invProps );

        a.getProperties().put("Action.Invoker", inv);
        return a;
    }    
    
    // <editor-fold defaultstate="collapsed" desc=" ActionInvoker (class) "> 
    
    private class ActionInvoker extends Action 
    {
        private Invoker invoker;
        
        ActionInvoker(Invoker invoker) {
            this.invoker = invoker; 
            
            this.setName(invoker.getAction()); 
            this.setCaption(invoker.getCaption()); 
            
            Integer index = invoker.getIndex();
            if (index != null) this.setIndex(index); 
            
            this.setIcon(getString("icon")); 
            this.setVisibleWhen(getString("visibleWhen"));            
            this.setMnemonic(getChar("mnemonic"));
            this.setTooltip(getString("tooltip"));
            
            Boolean bool = getBoolean("immediate"); 
            this.setImmediate(bool != null? bool.booleanValue(): true);
            
            bool = getBoolean("update"); 
            if (bool != null) this.setUpdate(bool.booleanValue());
            
            bool = getBoolean("showCaption"); 
            if (bool != null) this.setShowCaption(bool.booleanValue());
            
            this.getProperties().putAll(invoker.getProperties()); 
            this.getProperties().put("Action.Invoker", invoker);
        }

        public Object execute() { 
            Opener opener = InvokerUtil.createOpener(invoker, null);
            String target = opener.getTarget();
            if (target == null || target.length() == 0) {
                Object oval = getProperties().get("target"); 
                if (oval != null) opener.setTarget(oval.toString());
            } 
              
            return opener;
        } 
        
        private String getString(String name) {
            if (invoker == null || invoker.getProperties() == null) return null;
            
            Object ov = invoker.getProperties().get(name); 
            return (ov == null? null: ov.toString()); 
        }
        
        private char getChar(String name) {
            String sv = getString(name);
            if (sv == null || sv.trim().length() == 0) return '\u0000';
            
            return sv.trim().charAt(0); 
        } 
        
        private Boolean getBoolean(String name) {
            String sv = getString(name);
            if (sv == null || sv.length() == 0) return null;
            
            return "true".equalsIgnoreCase(sv); 
        } 
    }
    
    // </editor-fold> 
    
}
