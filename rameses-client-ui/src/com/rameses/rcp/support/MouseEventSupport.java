/*
 * MouseEventSupport.java
 *
 * Created on October 21, 2013, 2:41 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.support;

import com.rameses.platform.interfaces.ContentPane;
import com.rameses.rcp.common.MsgBox;
import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.rcp.ui.UIControl;
import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.Beans;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.AbstractButton;
import javax.swing.JComponent;

/**
 *
 * @author wflores
 */
public class MouseEventSupport implements MouseListener
{
    private JComponent component;
    private boolean processing;
    
    public MouseEventSupport(JComponent component) { 
        this.component = component; 
    } 
    
    public void install() {
        if (Beans.isDesignTime()) return;         
        //try to uninstall first all MouseEventSupport class 
        uninstall(); 
        //install the listener 
        if (component instanceof AbstractButton) {
            //do not attached the listener
        } else { 
            component.addMouseListener(this); 
        } 
    }
    
    public void uninstall() {
        MouseListener[] listeners = component.getMouseListeners(); 
        for (MouseListener ml : listeners) {
            if (!(ml instanceof MouseEventSupport)) continue;
            
            component.removeMouseListener(ml); 
        }
    }    

    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}

    public void mouseClicked(MouseEvent e) {
        if (processing) return; 
        if (e.getClickCount() != 1) return; 
        if (!(e.isControlDown() && e.isShiftDown())) return;

        processing = true; 
        EventQueue.invokeLater(new Runnable() { 
            public void run() { 
                try { 
                    showComponentInfo(); 
                } catch(Throwable t) { 
                    MsgBox.err(t); 
                } finally { 
                    processing = false; 
                } 
            } 
        }); 
    } 
    
    public void showComponentInfo() {
        if (!(component instanceof UIControl)) return;
        
        Binding binding = ((UIControl) component).getBinding();
        if (binding == null) return;
        
        Map params = new HashMap();
        params.put("properties", getInfo(binding.getBean())); 
        
        //find UIControllerPanel
        Component fc = findComponent(component, ContentPane.View.class); 
        if (fc instanceof ContentPane.View) { 
            Map info = ((ContentPane.View) fc).getInfo(); 
            params.put("info", (info == null? new HashMap(): info)); 
        } 
        
        Object opener = null; 
        try { 
            opener = ClientContext.getCurrentContext().getOpenerProvider().lookupOpener("ui-control-info:show", params); 
        } catch(Throwable t) {;} 
        
        if (opener != null) binding.fireNavigation(opener); 
    }
    
    private Map getInfo(Object bean) {
        Map info = new LinkedHashMap();
        if (!(component instanceof UIControl)) return info;
        
        info.put("controlClass", component.getClass().getSimpleName()); 
        info.put("beanClass", (bean == null? null: bean.getClass().getSimpleName()));
        
        UIControl uic = (UIControl) component;        
        info.put("name", uic.getName()); 
        info.put("index", uic.getIndex()); 
        String[] depends = uic.getDepends(); 
        if (depends != null) {
            StringBuffer sb = new StringBuffer();
            for (String str: depends) {
                if (sb.length() > 0) sb.append(", ");
                
                sb.append(str);
            } 
            info.put("depends", sb.toString()); 
        } else { 
            info.put("depends", null); 
        } 
        
        if (component instanceof ComponentInfo) 
        {
            Map props = ((ComponentInfo) component).getInfo(); 
            if (props != null) { 
                props.remove("name");
                props.remove("index");
                props.remove("depends");
                info.putAll(props); 
            } 
        }
        return info;
    } 
    
    private Component findComponent(Component source, Class componentClass) {
        if (source == null || componentClass == null) return null; 
        
        Container parent = source.getParent(); 
        while (parent != null) {
            if (componentClass.isInstance(parent)) return parent; 
            
            parent = parent.getParent(); 
        } 
        return null; 
    }
    
    // <editor-fold defaultstate="collapsed" desc=" ComponentInfo interface ">
    
    public static interface ComponentInfo 
    {
        Map getInfo(); 
    }
    
    // </editor-fold>
}
