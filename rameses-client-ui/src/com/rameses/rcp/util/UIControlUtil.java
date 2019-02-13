/*
 * UIControlUtil.java
 *
 * Created on July 8, 2010, 9:40 AM
 * @author jaycverg
 */

package com.rameses.rcp.util;

import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.framework.NavigatablePanel;
import com.rameses.rcp.framework.UIControllerPanel;
import com.rameses.rcp.ui.UIControl;
import com.rameses.common.ExpressionResolver;
import com.rameses.common.PropertyResolver;
import com.rameses.rcp.ui.UIControlEvent;
import com.rameses.rcp.ui.UIControlHandler;
import com.rameses.rcp.ui.UIInput;
import com.rameses.rcp.ui.Validatable;
import com.rameses.util.ValueUtil;
import java.awt.Component;
import java.awt.Container;
import java.util.List;
import javax.swing.JComponent;


public class UIControlUtil {
    
    public static final String COMPONENT_PARENT_KEY = "Component.parent"; 

    public static void setBeanValue(Binding binding, String name, Object value) {
        setBeanValue(binding.getBean(), name, value); 
    }
    
    public static void setBeanValue(Object bean, String name, Object value) 
    {
        PropertyResolver resolver = PropertyResolver.getInstance();
        resolver.setProperty(bean, name, value); 
    }    
    
    public static Object getBeanValue(UIControl control) {
        return getBeanValue(control, control.getName());
    }
    
    public static Object getBeanValue(UIControl control, String property) {
        if ( control == null ) return null; 
        
        Object beanValue = getBeanValue(control.getBinding(), property);
        if ( beanValue != null ) return beanValue;
        
        Object userObj = control.getClientProperty(UIControl.KEY_USER_OBJECT); 
        if (userObj != null) {
            try {
                return PropertyResolver.getInstance().getProperty(userObj, "value"); 
            } catch(Throwable t){ ; } 
        } 
        return null; 
    } 
    
    public static Object getBeanValue(Binding binding, String property) { 
        if ( binding != null && binding.getBean() != null ) {
            return getBeanValue(binding.getBean(), property); 
        } else {
            return null; 
        }
    } 
    
    public static Object getBeanValue(Object bean, String property) {
        if (bean == null || property == null || property.length() == 0) return null;
        
        PropertyResolver resolver = PropertyResolver.getInstance();
        try { 
            return resolver.getProperty(bean, property); 
        } catch(Throwable t) {  
            return null; 
        } 
    } 
    
    public static Class getValueType(UIControl control, String property) 
    {
        PropertyResolver resolver = PropertyResolver.getInstance();
        Object bean = control.getBinding().getBean();
        try { 
            return resolver.getPropertyType(bean, property);
        } catch(NullPointerException npe) {
            return null;
        }
    }
    
    public static Object evaluateExpr(Object bean, String expression) 
    {
        if (bean == null || expression == null) return null; 
        
        ExpressionResolver er = ExpressionResolver.getInstance();
        try 
        { 
            String result = er.evalString(expression, bean); 
            if (result != null && "null".equals(result)) return null; 
                
            return result;
        } 
        catch(NullPointerException npe) {
            return null; 
        }
    }
    
    public static boolean evaluateExprBoolean(Object bean, String expression) 
    {
        if (bean == null || expression == null) return false; 
        
        ExpressionResolver er = ExpressionResolver.getInstance();
        try { 
            return er.evalBoolean(expression, bean); 
        } catch(NullPointerException npe) {
            return false; 
        }
    } 
    
    public static Object evaluate(Object bean, String expression) 
    {
        if (bean == null || expression == null) return null; 
        
        ExpressionResolver er = ExpressionResolver.getInstance();
        try { 
            return er.eval(expression, bean);
        } catch(NullPointerException npe) {
            return null; 
        }
    }       
    
    public static int compare(UIControl control, Object control2) 
    {
        if ( control2 == null || !(control2 instanceof UIControl)) return 0;
        return control.getIndex() - ((UIControl) control2).getIndex();
    }
    
    public static NavigatablePanel getParentPanel(JComponent comp, String target) 
    {
        Object np = (comp == null ? null : comp.getClientProperty(NavigatablePanel.class));
        if ( np instanceof NavigatablePanel ) {
            return (NavigatablePanel) np; 
        }
        
        NavigatablePanel panel = null;
        if ( panel == null ) {
            Container parent = (comp==null? null: comp.getParent());
            if ( comp != null && parent==null ) {
                parent = (Container)comp.getClientProperty( COMPONENT_PARENT_KEY ); 
            }
            
            while( parent != null ) {
                if ( parent instanceof NavigatablePanel ) {
                    panel = (NavigatablePanel) parent;
                    break;
                }
//                if ( (panel != null && "parent".equals(target)) || (parent instanceof UIControllerPanel && "root".equals(parent.getName())) ) {
//                    break;
//                }
                parent = parent.getParent();
            }
            if ( panel != null && comp != null ) {
                comp.putClientProperty(NavigatablePanel.class, panel);
            }
        }
        return panel;
    }
    
    public static void validate(List<Validatable> validatables, ActionMessage actionMessage) {
        for ( Validatable vc: validatables ) {
            validate(vc, actionMessage);
        }
    }
    
    public static void validate(Validatable vc, ActionMessage actionMessage) {
        Component comp = null;
        if ( vc instanceof Component ) {
            comp = (Component) vc;
            if ( !comp.isFocusable() || !comp.isEnabled() || !comp.isShowing() || comp.getParent() == null ) {
                //do not validate non-focusable, disabled, or hidden fields.
                return;
            }
        }
        if ( vc instanceof UIInput ) {
            //do not validate readonly fields
            if ( ((UIInput)vc).isReadonly() ) return;
        }
        
        vc.validateInput();
        
        ActionMessage ac = vc.getActionMessage();
        if ( ac != null && ac.hasMessages() ) 
        {
            if ( ValueUtil.isEmpty(actionMessage.getSource()) )
                actionMessage.setSource( comp );
            
            actionMessage.addMessage(ac);
        }
    } 

    public static synchronized HandlerSupport createHandlerSupport( UIControl uic ) {
        return new HandlerSupport( uic ); 
    }
    public static class HandlerSupport {
        
        private UIControl uic; 
        
        HandlerSupport( UIControl uic ) {
            this.uic = uic; 
        } 
        
        UIControlHandler getControlHandler() {
            Object o = uic.getClientProperty(UIControlHandler.class); 
            if ( o instanceof UIControlHandler ) {
                return (UIControlHandler)o; 
            } else {
                return null; 
            }
        } 
        
        public void fireBind( Binding binding ) { 
            UIControlHandler uihandler = getControlHandler(); 
            if ( uihandler == null ) {
                //do nothing 
            } else if ( binding == null ) {
                uihandler.unbind( new UIControlEvent(uic) );  
            } else {
                uihandler.bind( new UIControlEvent(uic) );  
            }
        }
        
        public void refresh() {
            UIControlHandler uihandler = getControlHandler(); 
            if ( uihandler != null ) { 
                uihandler.refresh( new UIControlEvent(uic) ); 
            } 
        } 
    } 
}
