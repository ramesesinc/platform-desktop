/*
 * ToolbarUtil.java
 *
 * Created on June 12, 2010, 1:36 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris2.client;

import com.rameses.osiris2.Invoker;
import com.rameses.osiris2.SessionContext;
import com.rameses.rcp.framework.UIController;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.rcp.framework.ControllerProvider;
import com.rameses.rcp.framework.UIControllerContext;
import com.rameses.rcp.framework.UIControllerPanel;
import com.rameses.rcp.support.ImageIconSupport;
import com.rameses.rcp.support.ResourceUtil;
import com.rameses.util.BreakException;
import com.rameses.util.ValueUtil;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;

/**
 *
 * @author ms
 */
public final class ToolbarUtil 
{    
    
    public static JToolBar getToolBar() {
        final SessionContext app = OsirisContext.getSession();        
        JToolBar toolbar = new JToolBar();
        toolbar.setLayout(new ToolBarLayout());
        
        Object obj = OsirisContext.getEnv().get("toolbar.type"); 
        if ( obj == null ) obj = "toolbar";
        
        ButtonHelper buttonHelper = new ButtonHelper();
        List<Invoker> invokers = app.getInvokers( obj.toString());
        for (Invoker inv : invokers) {
            boolean isButton = true;            
            MapHelper helper = new MapHelper(inv.getProperties()); 
            try {
                Boolean bool = helper.getBoolean("button");
                if (bool != null) isButton = bool.booleanValue();
            } catch(Throwable ign){;}
            
            if (isButton) { 
                String strclass = helper.getString("buttonClass");
                if (strclass == null || strclass.length() == 0 ) { 
                    toolbar.add(new InvokerAction(inv)); 
                } else {
                    try {
                        Class clazz = helper.getClass("buttonClass"); 
                        JButton btn = (JButton) clazz.newInstance(); 
                        btn.addActionListener(new InvokerActionHandler(inv)); 
                        buttonHelper.setProperties(btn, inv, helper); 
                        toolbar.add(btn); 
                    } catch(Throwable t) {
                        System.out.println("error caused by " + t.getClass().getName() + ": " + t.getMessage());
                        t.printStackTrace(); 
                    }
                }
            } else { 
                toolbar.add(getViewComponent(inv)); 
            } 
        }
        return toolbar;
    }
    
    public static Component getViewComponent(Invoker inv) {
        ControllerProvider cp = ClientContext.getCurrentContext().getControllerProvider();
        UIController c = cp.getController( inv.getWorkunitid(), null );
        String action = inv.getAction();
        UIControllerContext uic = new UIControllerContext( c );
        if (action != null) {
            String out = (String)c.init(new HashMap(), action);
            if (!ValueUtil.isEmpty(out)) uic.setCurrentView(out);
        }
        return new UIControllerPanel(uic);
    }
    
    // <editor-fold defaultstate="collapsed" desc=" MapHelper ">

    private static class MapHelper 
    {
        private Map props; 
        
        MapHelper(Map props) {
            this.props = props; 
        }
        
        private Object get(String key) {
            return (props == null? null: props.get(key));
        }
        
        public Boolean getBoolean(String key) {
            Object value = get(key);
            if (value == null) return null; 
            
            if ("true".equals(value.toString())) 
                return Boolean.TRUE; 
            else 
                return Boolean.FALSE; 
        } 
        
        public String getString(String key) {
            Object value = get(key);
            return (value == null? null: value.toString()); 
        }
        
        public Class getClass(String key) {
            String str = getString(key); 
            if (str == null || str.length() == 0) return null; 
            
            try {
                return ClientContext.getCurrentContext().getClassLoader().loadClass(str); 
            } catch(Throwable t) { 
                return null; 
            } 
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ButtonHelper ">

    private static class ButtonHelper 
    {
        void setProperties(JButton btn, Invoker inv, MapHelper helper) {
            btn.setFocusable(false); 
            String caption = helper.getString("caption"); 
            if (caption != null) btn.setText(caption); 
            
            String tooltip = helper.getString("tooltip"); 
            if (tooltip != null) btn.setToolTipText(tooltip); 
            
            String icon = helper.getString("icon");
            if (icon != null) {
                ImageIcon iicon = ImageIconSupport.getInstance().getIcon(icon); 
                btn.setIcon(iicon); 
            }
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" InvokerAction ">
    
    private static class InvokerAction extends JButton 
    {
        private Invoker invoker;
        
        public InvokerAction(Invoker invoker) {
            this.invoker = invoker;

            setFocusable(false);             
            setText(invoker.getCaption());
            addActionListener(new InvokerActionHandler(invoker));             
            try {
                String tooltip = (String) invoker.getProperties().get("tooltip"); 
                if (tooltip != null) this.setToolTipText(tooltip); 
                
                String icn = (String) invoker.getProperties().get("icon");
                if (icn != null) this.setIcon(ResourceUtil.getImageIcon(icn));
            } catch(Throwable e) {
                //do nothing 
            } 
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" InvokerActionHandler ">
    
    private static class InvokerActionHandler implements ActionListener 
    {
        private Invoker invoker;
        
        public InvokerActionHandler(Invoker invoker) {
            this.invoker = invoker;
        }
        
        public void actionPerformed(ActionEvent e) {
            try {
                InvokerUtil.invoke(invoker, null);
            } catch(BreakException be) { 
                //do nothing 
            } catch(RuntimeException re) { 
                throw re; 
            } catch(Exception ex) { 
                throw new IllegalStateException(ex.getMessage(), ex); 
            } 
        } 
    } 
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ToolbarLayout ">
    
    private static class ToolBarLayout implements LayoutManager 
    {
        private static final int SPACING = 2;
        
        public void addLayoutComponent(String name, Component comp) {;}
        public void removeLayoutComponent(Component comp) {;}
        
        public Dimension getLayoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                int w=0, h=0;
                
                Component[] comps = parent.getComponents();
                for (int i=0; i<comps.length; i++) {
                    if (!comps[i].isVisible()) continue;
                    
                    Dimension dim = comps[i].getPreferredSize();
                    w += (dim.width + SPACING);
                    h = Math.max(h, dim.height);
                }
                return new Dimension(w,h);
            }
        }
        
        public Dimension preferredLayoutSize(Container parent) {
            return getLayoutSize(parent);
        }
        
        public Dimension minimumLayoutSize(Container parent) {
            return getLayoutSize(parent);
        }
        
        public void layoutContainer(Container parent) {
            synchronized (parent.getTreeLock()) {
                Insets margin = new Insets(2,2,2,2);
                
                int x = margin.left;
                int y = margin.top;
                int w = parent.getWidth() - (margin.left + margin.right);
                int h = parent.getHeight() - (margin.top + margin.bottom);
                
                Component[] comps = parent.getComponents();
                for (int i=0; i<comps.length; i++) {
                    Component comp = comps[i];
                    
                    if (!comp.isVisible()) continue;
                    
                    Dimension dim = comp.getPreferredSize();
                    comp.setBounds(x, y, dim.width, h);
                    x += dim.width + SPACING;
                }
            }
        }
    } 
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" CustomAction ">
    
    public static interface CustomAction 
    {
        void setInvoker(Invoker invoker); 
        void setController(UIController controller); 
    }
    
    // </editor-fold>
}
