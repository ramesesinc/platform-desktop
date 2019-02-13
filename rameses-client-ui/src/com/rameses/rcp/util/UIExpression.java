/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.util;

import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.ui.UIControl;
import java.awt.Component;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.rmi.server.UID;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author wflores 
 */
public class UIExpression {
    
    public boolean isEmpty( String expr ) { 
        return ( expr==null || expr.trim().length()==0 ); 
    } 
    
    public Object getBindingBean( UIControl uic ) { 
        if ( uic == null ) return null; 
        
        Binding binding = uic.getBinding(); 
        return ( binding == null? null : binding.getBean()); 
    }
    
    public void disableWhen( UIControl uic, String expr ) {
        if ( uic==null || isEmpty(expr) ) return; 
        
        try { 
            Object bean = getBindingBean( uic ); 
            if ( bean == null ) return; 
            
            if ( uic instanceof Component ) {
                Component comp = (Component) uic; 
                boolean b = UIControlUtil.evaluateExprBoolean( bean, expr ); 
                comp.setEnabled( !b ); 
            } 
        } catch(Throwable t) {
            t.printStackTrace(); 
        } 
    }
    
    public void enableWhen( UIControl uic, String expr ) {
        if ( uic==null || isEmpty(expr) ) return; 
        
        try { 
            Object bean = getBindingBean( uic ); 
            if ( bean == null ) return; 
            
            if ( uic instanceof Component ) {
                Component comp = (Component) uic; 
                boolean b = UIControlUtil.evaluateExprBoolean( bean, expr ); 
                comp.setEnabled( b ); 
            } 
        } catch(Throwable t) { 
            t.printStackTrace(); 
        } 
    } 
    
    public void visibleWhen( UIControl uic, String expr ) {
        if ( uic==null || isEmpty(expr) ) return; 
        
        try { 
            Object bean = getBindingBean( uic ); 
            if ( bean == null ) return; 
            
            if ( uic instanceof Component ) {
                Component comp = (Component) uic; 
                boolean b = UIControlUtil.evaluateExprBoolean( bean, expr ); 
                comp.setVisible( b );  
            } 
        } catch(Throwable t) { 
            t.printStackTrace(); 
        } 
    } 
    
    public Object translateExpr( UIControl uic, String expr, String itemName, Object itemObj ) {
        if ( uic==null || isEmpty(expr) ) return null; 
        
        Object bean = getBindingBean( uic ); 
        Object exprBean = createExpressionBean( bean, itemName, itemObj );
        return UIControlUtil.evaluateExpr(exprBean, expr); 
    } 
    
    public Object createExpressionBean( Object bean, String itemName, Object itemObj ) {
        ExprBean eb = new ExprBean( bean ); 
        eb.setItem( itemName, itemObj ); 
        return eb.createProxy(); 
    }  
    
    
    
    public class ExprBean extends HashMap implements InvocationHandler {
        
        private Object root;
        private String _toString;

        public ExprBean(Object root) {
            super(); 
            
            this.root = root; 
            this._toString = "$ExprBeanSupport@"+new UID(); 
        }

        public void setItem(String name, Object item) {
            if (name == null) name = "item";

            super.put(name, item); 
        }

        public Object get(Object key) {
            if (key == null) return null; 
            if (containsKey(key)) return super.get(key);

            String skey = key.toString(); 
            String propName = "get" + skey.substring(0,1).toUpperCase() + skey.substring(1); 
            Object[] params = new Object[]{};
            Method method = findGetterMethod(root, propName, params); 
            if (method == null) return null;

            try {
                return method.invoke(root, params);
            } catch (IllegalArgumentException ex) {
                //do nothing
            } catch (IllegalAccessException ex) {
                //do nothing
            } catch (InvocationTargetException ex) {
                //do nothing
            }
            return null;
        }

        public Object put(Object key, Object value) {
            //restrict from adding key values to this bean 
            return null; 
        }     

        public Object createProxy() {
            ClassLoader classLoader = root.getClass().getClassLoader();
            Class[] interfaces = root.getClass().getInterfaces(); 
            List<Class> classes = new ArrayList<Class>();
            for (int i=0; i<interfaces.length; i++) 
                classes.add(interfaces[i]); 

            if (!(root instanceof Map)) classes.add(Map.class); 

            return Proxy.newProxyInstance(classLoader, classes.toArray(new Class[]{}), this); 
        } 

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if ("toString".equals(method.getName()))  return _toString;
            else if ("hashCode".equals(method.getName())) return proxy.hashCode();

            try {
                return method.invoke(this, args); 
            } catch (IllegalArgumentException ex) {
                //do nothing
            } catch (InvocationTargetException ex) {
                //do nothing
            } catch (IllegalAccessException ex) {
                //do nothing
            } catch (NullPointerException npe) {
                //do nothing
            }           
            return null; 
        }   

        private Method findGetterMethod(Object bean, String name, Object[] args) {
            if (bean == null || name == null) return null;
            if (args == null) args = new Object[]{};

            Class beanClass = bean.getClass();
            Method[] methods = beanClass.getMethods(); 
            for (int i=0; i<methods.length; i++) 
            {
                Method m = methods[i];
                if (!m.getName().equals(name)) continue;

                int paramSize = (m.getParameterTypes() == null? 0: m.getParameterTypes().length); 
                int argSize = (args == null? 0: args.length); 
                if (paramSize == argSize) return m;
            }
            return null;
        }    
    }
}
