package com.rameses.rcp.util;

import com.rameses.common.MethodResolver;
import com.rameses.common.PropertyResolver;
import com.rameses.rcp.common.CallbackHandlerProxy;
import com.rameses.rcp.common.PopupMenuOpener;
import com.rameses.rcp.control.XButton;
import com.rameses.rcp.framework.*;
import com.rameses.rcp.ui.UICommand;

import com.rameses.rcp.common.Action;
import com.rameses.rcp.common.MsgBox;
import com.rameses.rcp.common.Opener;
import com.rameses.util.BreakException;
import com.rameses.util.BusinessException;
import com.rameses.util.ExceptionManager;
import com.rameses.util.IgnoreException;
import com.rameses.util.ValueUtil;
import java.awt.EventQueue;
import java.beans.Beans;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JComponent;

/**
 *
 * @author jaycverg
 */
public class UICommandUtil {
    
    public static Object processAction(UICommand command) 
    {
        if ( Beans.isDesignTime() ) return null;

        ClientContext ctx = ClientContext.getCurrentContext();
        UICommandSupport uics = new UICommandSupport();         
        MethodResolver resolver = uics.getMethodResolver(); 

        Object callbackErrorHandler = null; 
        Binding binding = null;
        try { 
            binding = command.getBinding();
            binding.formCommit();
            validate(command, binding);
            
            String target = ValueUtil.isEmpty(command.getTarget())? "parent": command.getTarget();
            NavigatablePanel navPanel = UIControlUtil.getParentPanel((JComponent)command, target);
            if ( !"parent".equals(target) ) {
                UIControllerContext rootCon = (UIControllerContext) navPanel.getControllers().peek();
                Binding rootBinding = rootCon.getCurrentView().getBinding();
                validate(command, rootBinding);
            }
            
            //set parameters
            XButton btn = (XButton) command;
            ControlSupport.setProperties(binding.getBean(), btn.getParams());
                        
            //notify handlers who hooked before execution
            binding.getActionHandlerSupport().fireBeforeExecute();
            
            Object outcome = null;
            String action = command.getActionName();
            if ( btn.getClientProperty(Action.class.getName()) != null ) {
                Action a = (Action) btn.getClientProperty(Action.class.getName());
                outcome = a.execute(); 
            } 
            else if ( action != null ) {
                if ( !action.startsWith("_")) {
                    Object[] actionParams = new Object[]{};
                    Object actionInvoker = btn.getClientProperty("Action.Invoker");
                    if (actionInvoker != null) actionParams = new Object[]{ actionInvoker };
                    
                    Object bb = command.getClientProperty("Action.Bean");
                    if (bb == null) bb = binding.getBean(); 
                    
                    if (hasMethod(bb, action, actionParams))
                        outcome = resolver.invoke(bb, action, actionParams);
                    else 
                        outcome = resolver.invoke(bb, action, null, null); 
                } 
                else { 
                    outcome = action;
                }
                
                if ( command.isUpdate() ) binding.update();
            }
            
            //notify handlers who hooked after execution
            binding.getActionHandlerSupport().fireAfterExecute(); 
            
            if (outcome instanceof PopupMenuOpener) {
                return outcome;
            } 

            Opener opener = null; 
            if (outcome instanceof Opener) { 
                opener = (Opener) outcome;
                if ("_close".equals(opener.getOutcome())) {
                    outcome = "_close"; 
                    
                } else if (opener.isAsync()) {
                    String str = opener.getTarget();
                    if (!"popup".equals(str)) opener.setTarget("popup"); 
                    
                    outcome = "_close"; 
                    uics.navigate(binding, opener); 
                    
                } else { 
                    uics.initOpenerParams(binding, opener); 
                }
            } 

            NavigationHandler handler = ctx.getNavigationHandler();             
            if (handler != null) {
                if (opener != null) callbackErrorHandler = opener.getProperties().get("windowError");
                
                navPanel = UIControlUtil.getParentPanel((JComponent)command, "parent");
                if ( opener != null && "popupmenu".equals(opener.getTarget()) ) {
                    handler.navigate(navPanel, command, opener); 
                    return null;
                } 
                
                handler.navigate(navPanel, command, outcome); 
                
                Object pmo = command.getClientProperty(PopupMenuOpener.class); 
                if ( pmo != null ) { 
                    command.putClientProperty(PopupMenuOpener.class, null); 
                    return (PopupMenuOpener) pmo; 
                } 
                
                if (opener != null) {
                    Object closeBehindOnStart = opener.getProperties().get("closeBehindOnStart"); 
                    /*
                     *  closeBehindOnStart: To close the previous binding/controller 
                     *                      when this opener is successfully showed 
                     */
                    if ("true".equals(closeBehindOnStart+"")) {
                        binding.fireNavigation("_close"); 
                    } 
                    
                    if ( "topwindow".equalsIgnoreCase( opener.getTarget()+"")) {
                        binding.fireNavigation("_close"); 
                    }
                }
            }

            return null;
        } 
        catch(Exception ex) 
        {
            Exception e = ExceptionManager.getOriginal(ex); 
            if (e instanceof IgnoreException || e instanceof BreakException) return null; 
         
            if (!ExceptionManager.getInstance().handleError(e)) { 
                ctx.getPlatform().showError((JComponent) command, ex); 
            } 
            
            new Thread(new CallbackErrorNotifier(callbackErrorHandler)).start(); 
            return null; 
        }
    }
        
    public static void processAction(JComponent invoker, Binding binding, Action action) 
    {
        if ( Beans.isDesignTime() ) return;

        ClientContext ctx = ClientContext.getCurrentContext();
        MethodResolver resolver = MethodResolver.getInstance();  
        try 
        {
            Object[] actionParams = new Object[]{};
            Object actionInvoker = action.getProperties().get("Action.Invoker");
            if (actionInvoker != null) actionParams = new Object[]{ actionInvoker };

            Object outcome = null;            
            String command = action.getName();
            if (hasMethod(binding.getBean(), command, actionParams))
                outcome = resolver.invoke(binding.getBean(), command, actionParams);
            else 
                outcome = resolver.invoke(binding.getBean(), command, null, null); 
            
            if (outcome != null) binding.fireNavigation(outcome);
        }
        catch(Exception ex) 
        {
            Exception e = ExceptionManager.getOriginal(ex); 
            if (e instanceof IgnoreException || e instanceof BreakException) return; 
            
            if (!ExceptionManager.getInstance().handleError(e))
                ctx.getPlatform().showError(invoker, ex);
        }        
    }
    
    public static void processAction(JComponent invoker, Binding binding, Opener anOpener) 
    {
        if ( Beans.isDesignTime() ) return;

        ClientContext ctx = ClientContext.getCurrentContext();
        try {
            if (anOpener != null) binding.fireNavigation(anOpener);
        }
        catch(Exception ex) 
        {            
            Exception e = ExceptionManager.getOriginal(ex); 
            if (e instanceof IgnoreException || e instanceof BreakException) return; 
            
            if (!ExceptionManager.getInstance().handleError(e))
                ctx.getPlatform().showError(invoker, ex);
        }        
    }    
    
    private static void validate(UICommand command, Binding binding) throws BusinessException 
    {
        if ( binding == null ) return;
        if ( !command.isUpdate() && command.isImmediate() ) return;
        
        binding.validate();
    }
    
    private static boolean hasMethod(Object bean, String name, Object[] args) 
    {
        if (bean == null || name == null) return false;
        
        Class beanClass = bean.getClass();
        while (beanClass != null) 
        {
            Method[] methods = beanClass.getMethods(); 
            for (int i=0; i<methods.length; i++) 
            {
                Method m = methods[i];
                if (!m.getName().equals(name)) continue;

                int paramSize = (m.getParameterTypes() == null? 0: m.getParameterTypes().length); 
                int argSize = (args == null? 0: args.length); 
                if (paramSize == argSize && paramSize == 0) return true;
                if (paramSize == argSize && m.getParameterTypes()[0] == Object.class) return true; 
            }
            beanClass = beanClass.getSuperclass(); 
        }
        return false;
    }
    
    // <editor-fold defaultstate="collapsed" desc=" UICommandSupport ">  
    
    private static class UICommandSupport 
    {
        private MethodResolver methodResolver;
        private PropertyResolver propertyResolver;
        
        private MethodResolver getMethodResolver() {
            if (methodResolver == null) {
                methodResolver = MethodResolver.getInstance(); 
            }
            return methodResolver; 
        }
        
        private PropertyResolver getPropertyResolver() {
            if (propertyResolver == null) {
                propertyResolver = PropertyResolver.getInstance();
            }
            return propertyResolver; 
        }
        
        private void initOpenerParams(Binding binding, Opener opener) {
            try { 
                Object bean = binding.getBean(); 
                //invoke a callback method getOpenerParams to get the extended opener parameters 
                Object paramEntity = null;
                try { 
                    paramEntity = getPropertyResolver().getProperty(bean, "entity");
                } catch(Throwable t) {;} 

                Map openerParams = opener.getParams();
                if (openerParams == null) openerParams = new HashMap();

                try { 
                    Map params = (Map) getMethodResolver().invoke(bean, "getOpenerParams", new Object[]{});
                    if (params != null) openerParams.putAll(params); 
                } catch(Throwable t) {;} 

                if (paramEntity != null && !openerParams.containsKey("entity")) 
                    openerParams.put("entity", paramEntity);

                opener.setParams(openerParams); 
                
            } catch(Throwable t){;}  
        } 
        
        private void navigate(final Binding binding, final Opener opener) {
            Runnable runnable = new Runnable() {
                public void run() {
                    binding.fireNavigation(opener); 
                }
            };
            new Thread(runnable).start(); 
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" CallbackErrorNotifier "> 
    
    private static class CallbackErrorNotifier implements Runnable 
    {
        private Object callback;
        
        CallbackErrorNotifier(Object callback) {
            this.callback = callback; 
        }
        
        public void run() {
            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    runImpl();
                }
            });
        } 
        
        private void runImpl() {
            try {
                if (callback == null) return;
                if (callback instanceof Runnable) {
                    ((Runnable) callback).run(); 
                } else {
                    new CallbackHandlerProxy(callback).call(); 
                }
            } catch(Throwable t) {
                MsgBox.err(t); 
            }
        }
    }
    
    // </editor-fold>
}
