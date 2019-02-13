package com.rameses.rcp.util;

import com.rameses.common.MethodResolver;
import com.rameses.rcp.common.Opener;
import com.rameses.common.PropertyResolver;
import com.rameses.rcp.common.PopupMenuOpener;
import com.rameses.rcp.framework.*;
import com.rameses.rcp.support.ColorUtil;
import com.rameses.rcp.support.FontSupport;
import com.rameses.rcp.ui.UIControl;
import com.rameses.util.ValueUtil;
import java.awt.Color;
import java.awt.Component;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JComponent;



public final class ControlSupport {
    
    private static Object getClientProperty(Component component, Object key) {
        if (component instanceof JComponent) {
            return ((JComponent) component).getClientProperty(key);
        } else {
            return null; 
        }
    }
    
    private static void setClientProperty(Component component, Object key, Object value) {
        if (component instanceof JComponent) {
            ((JComponent) component).putClientProperty(key, value); 
        } 
    }    
    
    public static void setStyles(Map props, Component component) {
        PropertyResolver resolver = PropertyResolver.getInstance();
        for (Object o : props.entrySet()) {
            Map.Entry me = (Map.Entry)o;
            if (me.getKey() == null) continue;
            
            String key = me.getKey().toString();
            try {
                if ("background".equals(key) || "background-color".equals(key)) {
                    Color color = null;
                    if (me.getValue() instanceof Color) { 
                        color = (Color) me.getValue(); 
                    } else { 
                        color = ColorUtil.decode(me.getValue()+"");
                    } 
                    
                    if ( color != null ) {
                        int num = 0; 
                        try {
                            num = Integer.parseInt( props.get("opacity").toString());
                        } catch(Throwable t) {;} 

                        if ( num > 0 ) { 
                            num = Math.max( num, 0 ); 
                            num = Math.min( num, 100 ); 
                            double orate = ((Number) num).doubleValue() / 100.0; 
                            int opacity = ((Number) (orate * 255.0)).intValue(); 
                            color = new Color( color.getRed(), color.getGreen(), color.getBlue(), opacity); 
                        }
                    }
                    
                    Color oldColor = (Color) getClientProperty(component, "ControlSupport.background");
                    if (color != null) {
                        if (oldColor == null) {
                            setClientProperty(component, "ControlSupport.background", component.getBackground()); 
                        }
                        component.setBackground(color);
                        
                    } else if (oldColor != null) {
                        component.setBackground(oldColor);
                    }                   
                }
                else if ("foreground".equals(key) || "color".equals(key)) {
                    Color color = null;
                    if (me.getValue() instanceof Color) 
                        color = (Color) me.getValue(); 
                    else 
                        color = ColorUtil.decode(me.getValue()+"");
                    
                    Color oldColor = (Color) getClientProperty(component, "ControlSupport.foreground");
                    if (color != null) {
                        if (oldColor == null) 
                            setClientProperty(component, "ControlSupport.foreground", component.getForeground()); 
                        
                        component.setForeground(color);
                    } else if (oldColor != null) {
                        component.setForeground(oldColor); 
                    }
                } else if ("repaint".equals(key)) {
                    component.repaint();
                } else if ("requestFocus".equals(key) && component.isFocusable() && component.isEnabled()) { 
                    component.requestFocusInWindow(); 
                } else {
                    resolver.setProperty(component, key, me.getValue()); 
                }
            } catch(Throwable t) {
                //do nothing 
            }   
        }
        
        try { 
            if (component instanceof JComponent) { 
                new FontSupport().applyStyles((JComponent) component, props); 
            } 
        } catch(Throwable t){
            //do nothing 
        } 
    }
    
    public static Object init(Object bean, Map params, String action ) {
        setProperties(bean, params);
        return invoke( bean, action, null );
    }
    
    public static void setProperties(Object bean, Map params ) {
        if( params != null ) {
            ClientContext ctx = ClientContext.getCurrentContext();
            PropertyResolver resolver = PropertyResolver.getInstance();
            for( Object oo : params.entrySet()) {
                Map.Entry me = (Map.Entry)oo;
                try {
                    resolver.setProperty(bean, me.getKey()+"", me.getValue() );
                } catch(Throwable t) {
                    t.printStackTrace();
                }
            }
        }
    }
    
    public static Object invoke(Object bean, String action, Object[] params  ) 
    {
        ClientContext ctx = ClientContext.getCurrentContext();
        //fire actions
        if ( action != null && action.trim().length() > 0) 
        {
            try {
                return MethodResolver.getInstance().invoke(bean,action,null,params);
            } catch (RuntimeException re) {
                throw re;
            } catch(Exception ex) {
                throw new IllegalStateException(ex.getMessage(), ex);
            }
        }
        return null;
    }
    
    public static void fireNavigation(UIControl source, Object outcome) {
        NavigationHandler nh = ClientContext.getCurrentContext().getNavigationHandler();
        NavigatablePanel navPanel = UIControlUtil.getParentPanel((JComponent)source, null);
        nh.navigate(navPanel, source, outcome);
    }
    
    public static boolean isResourceExist( String name ) {
        try {
            InputStream is = ClientContext.getCurrentContext().getResourceProvider().getResource(name);
            return (is != null); 
        } catch(Throwable t) {
            return false; 
        }
    }
    
    public static byte[] getByteFromResource( String name ) {
        if(name==null || name.trim().length()==0)
            return null;
        ByteArrayOutputStream bos = null;
        InputStream is = ClientContext.getCurrentContext().getResourceProvider().getResource(name);
        if( is != null ) {
            try {
                bos =  new ByteArrayOutputStream();
                int i = 0;
                while((i=is.read())!=-1) {
                    bos.write(i);
                }
                return bos.toByteArray();
            } catch(Exception ex) {
                return null;
            } finally {
                try { bos.close(); } catch(Exception ign){;}
                try { is.close(); } catch(Exception ign){;}
            }
        } else {
            return  null;
        }
    }
    
    public static ImageIcon getImageIcon(String name) {
        byte[] b = ControlSupport.getByteFromResource(name);
        if (b != null) {
            return new ImageIcon( b );
        } else {
            return null;
        }
    }
    
    public static Opener initOpener( Opener opener, UIController caller ) {
        return initOpener(opener, caller, true);
    }
    
    public static Opener initOpener( Opener opener, UIController caller, boolean invokeOpenerAction ) 
    {
        Object invoker = opener.getProperties().get("_INVOKER_");        
        if ( caller != null && ValueUtil.isEmpty(opener.getName()) ) 
        {
            opener.setController( caller );
            if ( opener.getCaption() != null )
                caller.setTitle( opener.getCaption() );
            if ( opener.getId() != null )
                caller.setId( opener.getId() );
            
        } 
        else if ( opener.getController() == null ) 
        {
            ControllerProvider provider = ClientContext.getCurrentContext().getControllerProvider();
            UIController controller = provider.getController(opener.getName(), caller);
            controller.setId( opener.getId() );
            controller.setName( opener.getName() );
            controller.setTitle( opener.getCaption() );

            Object callee = controller.getCodeBean();
            if ( caller != null ) {
                injectCaller( callee, callee.getClass(), caller.getCodeBean());
            }
            
            if (invoker != null) {
                injectInvoker(callee, callee.getClass(), invoker); 
            }
            
            opener.setController( controller );
            
            if ( invokeOpenerAction ) 
            {
                Object[] actionParams = new Object[]{};
                if (invoker != null) actionParams = new Object[]{ invoker };

                Object o = opener.getController().init(opener.getParams(), opener.getAction(), actionParams);
                if ( o == null ) {;} 
                else if ( o instanceof String ) {
                    opener.setOutcome( (String)o );
                } 
                //if the opener action returns another opener,
                //then intialize the opener and return it
                else if ( o instanceof PopupMenuOpener ) {
                    opener = (Opener) o; 
                    return opener; 
                }
                else if ( o instanceof Opener ) {
                    Opener oo = (Opener) o;
                    opener = initOpener(oo, oo.getController(), invokeOpenerAction);
                }
            }            
        }
        
        UIController controller = opener.getController();
        if( controller.getTitle() == null ) {
            controller.setTitle( controller.getName() );
        }
        
        return opener;
    }
    
    public static void injectCaller( Object callee, Class clazz, Object caller ) {
        //if caller is the same as calle do not proceed
        //for cases for subforms having the same controller.
        if( callee!=null && callee.equals(caller)) return;
        
        //inject the caller here..
        for(Field f: clazz.getDeclaredFields()) {
            if( f.isAnnotationPresent(com.rameses.rcp.annotations.Caller.class)) {
                boolean accessible = f.isAccessible();
                f.setAccessible(true);
                try { f.set(callee, caller); } catch(Exception ign){;}
                f.setAccessible(accessible);
                break;
            }
        }
        Class superClass = clazz.getSuperclass();
        if(superClass!=null) {
            injectCaller( callee, superClass, caller );
        }
    }
    
    public static void injectInvoker( Object object, Class clazz, Object invoker ) {
         for(Field f: clazz.getDeclaredFields()) {
            if( f.isAnnotationPresent(com.rameses.rcp.annotations.Invoker.class)) {
                boolean accessible = f.isAccessible();
                f.setAccessible(true);
                try { f.set(object, invoker); } catch(Exception ign){;}
                f.setAccessible(accessible);
                break;
            }
        }
        Class superClass = clazz.getSuperclass();
        if(superClass!=null) {
            injectInvoker( object, superClass, invoker );
        }
    }
    
    public static boolean isPermitted(String domain, String role, String permission ) {
        //check if not permitted, block this
        ClientContext ctx = ClientContext.getCurrentContext();
        if (ctx.getSecurityProvider() == null) return  true;
        
        return ctx.getSecurityProvider().checkPermission(domain, role, permission); 

//        if(permission!=null && permission.trim().length()>0) {
//            ClientContext ctx = ClientContext.getCurrentContext();
//            if( ctx.getSecurityProvider()==null ) {
//                return  true;
//            }
//            return ctx.getSecurityProvider().checkPermission(domain, role, permission);
//        } else {
//            return true;
//        }
    }
    
    public static boolean hasMethod(Object bean, String name, Object[] args) 
    {
        if (bean == null || name == null) { return false; }
        
        Class beanClass = bean.getClass();
        while (beanClass != null) 
        {
            Method[] methods = beanClass.getMethods(); 
            for (int i=0; i<methods.length; i++) 
            {
                Method m = methods[i];
                if (!m.getName().equals(name)) { continue; } 

                int paramSize = (m.getParameterTypes() == null? 0: m.getParameterTypes().length); 
                int argSize = (args == null? 0: args.length); 
                if (paramSize == argSize && paramSize == 0) { return true; } 
                if (paramSize == argSize && m.getParameterTypes()[0] == Object.class) { return true; } 
            }
            beanClass = beanClass.getSuperclass();
        }
        return false;
    }    
    
}
