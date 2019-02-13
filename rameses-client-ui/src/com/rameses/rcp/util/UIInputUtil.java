/*
 * UIInputUtil.java
 *
 * Created on June 21, 2010, 3:37 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.util;

import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.rcp.ui.UIInput;
import com.rameses.common.PropertyResolver;
import com.rameses.rcp.framework.ValidatorException;
import com.rameses.rcp.ui.UIControlEvent;
import com.rameses.rcp.ui.UIControlHandler;
import com.rameses.util.BreakException;
import com.rameses.util.ExceptionManager;
import com.rameses.util.ValueUtil;
import java.beans.Beans;
import javax.swing.InputVerifier;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.text.JTextComponent;

/**
 *
 * @author jaycverg
 */
public class UIInputUtil {
    
    public static UIInputVerifier VERIFIER = new UIInputVerifier();
    
    public static class UIInputVerifier extends InputVerifier 
    {
        public boolean verify(JComponent input) 
        {
            if ( Beans.isDesignTime() ) return true;
            
            UIInput control = null;
            if (input instanceof UIInput) { 
                control = (UIInput) input; 
            } else { 
                Object delegator = input.getClientProperty(UIInput.class); 
                if (delegator instanceof UIInput) {
                    control = (UIInput) delegator; 
                } else {
                    throw new IllegalStateException("UIInputVerifier should be used for UIInput controls only."); 
                }
            }
            
            if ( control.isReadonly() || !input.isEnabled() ) return true;
            if ( input instanceof JTextComponent && !((JTextComponent) input).isEditable() ) return true;
            if ( input.getParent() == null ) return true;
            
            try {
                updateBeanValueImpl(control, true, true); 
            } catch(BreakException be) {
                if (control != null) {
                    control.refresh(); 
                }
            } catch(Exception e) { 
                Exception src = ExceptionManager.getOriginal(e);
                if ( !ExceptionManager.getInstance().handleError(src) ) {
                    ClientContext.getCurrentContext().getPlatform().showError((JComponent) control, e);
                }
                
                if (e instanceof ValidatorException) {
                    //since the exception thrown is from the validator 
                    //do not transfer the focus
                    return false; 
                } 
            } 
            return true;
        }
        
    }
    
    public static synchronized void updateBeanValue(UIInput control) {
        updateBeanValue(control, true, true);
    }
    
    public static synchronized void updateBeanValue(UIInput control, boolean addLog, boolean refresh) {
        try {
            updateBeanValueImpl(control, addLog, refresh); 
        } catch(BreakException be) {
            if (control != null) {
                control.refresh(); 
            }
        } catch(Exception e) {
            Exception src = ExceptionManager.getOriginal(e);
            if ( !ExceptionManager.getInstance().handleError(src) ) {
                ClientContext.getCurrentContext().getPlatform().showError((JComponent) control, e);
            }
        }
    }
    
    private static synchronized void updateBeanValueImpl(UIInput control, boolean addLog, boolean refresh) 
    {
        Support support = (Support) ((JComponent) control).getClientProperty(UIInputUtil.Support.class); 
        if (support != null) {
            if (control instanceof JComponent)
                support.setValue(control.getName(), control.getValue(), (JComponent)control);
            else 
                support.setValue(control.getName(), control.getValue()); 

            return;
        }

        Binding binding = control.getBinding();
        if (binding == null) return;

        Object bean = binding.getBean();
        if (bean == null) return;

        EventHelper eventHelper = new EventHelper();
        if (eventHelper.allowCustomUpdate(control)) {
            eventHelper.fireCustomUpdate(control); 
            return; 
        } 
        
        ClientContext ctx = ClientContext.getCurrentContext();
        PropertyResolver resolver = PropertyResolver.getInstance();
        String name = control.getName();
        if (ValueUtil.isEmpty(name)) { return; }

        Object inputValue = control.getValue();        
        Object beanValue = resolver.getProperty(bean, name);
        boolean forceUpdate = false;
        if (control instanceof JComponent) {
            //if the input is a JTable check for the flag
            Object value = ((JComponent) control).getClientProperty(JTable.class);
            forceUpdate = (value != null);
        }

        if (forceUpdate || !ValueUtil.isEqual(inputValue, beanValue)) {
            //fire field validators before committing to the bean 
            binding.getValidatorSupport().fireFieldValidators(name, inputValue);
            //commit to bean
            resolver.setProperty(bean, name, inputValue);
            if (addLog) {
                //add entry to change log
                binding.getChangeLog().addEntry(bean, name, beanValue, inputValue);
            } 
            
            // fire value change event 
            createHandlerSupport(control).valueChanged( inputValue ); 
            
            //notify value change support 
            binding.getValueChangeSupport().notify(name, inputValue);
            //refresh the component
            if ( refresh && control instanceof JTextComponent ) {
                JTextComponent jtxt = (JTextComponent) control;
                int oldCaretPos = jtxt.getCaretPosition(); 

                try { 
                    control.refresh(); 
                } catch(RuntimeException re) {
                    throw re;
                } catch(Exception e) {
                    throw new RuntimeException(e.getMessage(), e); 
                } finally {
                    try {
                        jtxt.setCaretPosition(oldCaretPos); 
                    } catch(Exception ign) {;} 
                }

                jtxt.putClientProperty("CaretPosition", oldCaretPos); 
            }
            
            //notify dependencies
            binding.notifyDepends(control);
            eventHelper.fireAfterUpdate(control, inputValue);
        } else if (control instanceof JComboBox) {
            //do nothing, we dont want to fire the refresh to prevent cyclic updating 
            //or never ending update 
        } else {
            //refresh component
            control.refresh(); 
        }
    }    
    
    public static interface Support {
        Object setValue(String name, Object value);         
        Object setValue(String name, Object value, JComponent jcomp); 
    }    
    
    public static interface EventHandler {
        boolean allowCustomUpdate(); 
        void customUpdate();
        
        void afterUpdate(Object value);
    }
    
    private static class EventHelper {
        boolean allowCustomUpdate(UIInput ui) {
            Object o = ui.getClientProperty(UIInputUtil.EventHandler.class); 
            if (o instanceof UIInputUtil.EventHandler) {
                return ((UIInputUtil.EventHandler)o).allowCustomUpdate();
            } else {
                return false; 
            }
        }
        
        void fireCustomUpdate(UIInput ui) {
            Object o = ui.getClientProperty(UIInputUtil.EventHandler.class); 
            if (o instanceof UIInputUtil.EventHandler) {
                ((UIInputUtil.EventHandler)o).customUpdate();
            }
        }
        
        void fireAfterUpdate(UIInput ui, Object value) {
            Object o = ui.getClientProperty(UIInputUtil.EventHandler.class); 
            if (o instanceof UIInputUtil.EventHandler) {
                ((UIInputUtil.EventHandler)o).afterUpdate(value);
            }
        }
    } 
    
    public static synchronized HandlerSupport createHandlerSupport( UIInput uic ) {
        return new HandlerSupport( uic ); 
    }
    public static class HandlerSupport {
        
        private UIInput uic; 
        
        HandlerSupport( UIInput uic ) {
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
        
        public void valueChanged( Object value ) { 
            UIControlHandler uihandler = getControlHandler(); 
            if ( uihandler != null ) { 
                uihandler.valueChanged( new UIControlEvent( uic, value )); 
            } 
        } 
    } 
}
