/*
 * NotificationButton.java
 *
 * Created on January 13, 2014, 9:12 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.client.notification;

import com.rameses.osiris2.Invoker;
import com.rameses.osiris2.client.InvokerUtil;
import com.rameses.osiris2.client.ToolbarUtil;
import com.rameses.rcp.common.MsgBox;
import com.rameses.rcp.common.Opener;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.rcp.framework.NotificationManager;
import com.rameses.rcp.framework.NotificationProvider;
import com.rameses.rcp.framework.NotificationRenderer;
import com.rameses.rcp.framework.UIController;
import com.rameses.rcp.support.ImageIconSupport;
import com.rameses.rcp.util.OpenerUtil;
import com.rameses.util.ExceptionManager;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.LayoutManager2;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

/**
 *
 * @author wflores
 */
public class NotificationButton extends JButton implements ActionListener, ToolbarUtil.CustomAction  
{
    private ImageIcon defaultIcon;
    private LabelImpl lbl;
    private Map messages;
 
    private Invoker invoker;
    private UIController controller;
    
    public NotificationButton() {
        super.addActionListener(this); 
        setMargin(new Insets(5,2,5,2)); 
        setHorizontalAlignment(SwingConstants.LEFT); 
        
        defaultIcon = ImageIconSupport.getInstance().getIcon("images/notification-bell.png"); 
        super.setIcon(defaultIcon); 
        
        super.setLayout(new LayoutImpl()); 
        lbl = new LabelImpl(); 
        lbl.setText(""); 
        lbl.setBounds(2, 2, 18, 18); 
        add(lbl); 
        
        messages = new LinkedHashMap();
        initComponent(); 

        NotificationManager.addRenderer(new NotificationRendererImpl());         
        NotificationProvider np = ClientContext.getCurrentContext().getNotificationProvider();
        if (np != null) np.add(new NotificationHandlerImpl()); 
        
        Runnable runnable = new Runnable() {
            public void run() {
                NotificationLoader.execute();
                updateCount();
            }
        };
        EventQueue.invokeLater(runnable);
    } 

    protected void initComponent() {
    }
    
    public void addActionListener(ActionListener listener) {
    }
    
    public void setIcon(Icon icon) {
        if (icon == null) icon = defaultIcon; 
        super.setIcon(icon); 
    }
    
    public void setText(String text) {
        super.setText("");
    }
    
    private void updateCount() {
        lbl.setText(messages.size()+""); 
        revalidate(); 
        repaint(); 
    }
    
    public void actionPerformed(ActionEvent e) {
        showPopup();
    }
    
    // <editor-fold defaultstate="collapsed" desc=" ToolbarUtil.CustomAction implementation ">
    
    public void setInvoker(Invoker invoker) {
        this.invoker = invoker;
    }

    public void setController(UIController controller) {
        this.controller = controller; 
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" NotificationHandlerImpl ">

    private static Object LOCK = new Object();
    
    private class NotificationHandlerImpl extends DefaultNotificationHandler 
    {
        NotificationButton root = NotificationButton.this;
        
        public void onMessage(Object data) { 
            synchronized (LOCK) { 
                if (data == null) return; 
                
                String objid = getBeanValueAsString(data, "notificationid");
                if (objid == null) return;
                
                String status = getBeanValueAsString(data, "status");
                if ("REMOVED".equals(status)) {
                    remove(objid); 
                    return;
                }

                String recipientid = getBeanValueAsString(data, "recipientid");
                if (recipientid == null || recipientid.trim().length()==0 ) return;
                
                if (root.messages.containsKey(objid)) return; 
                
                root.messages.put(objid, data);
                EventQueue.invokeLater(new Runnable() { 
                    public void run() { 
                        root.updateCount(); 
                        Toolkit.getDefaultToolkit().beep(); 
                    } 
                }); 
            } 
        } 

        public void onRead(Object data) {
            synchronized (LOCK) { 
                if (data == null) return; 

                String objid = getBeanValueAsString(data, "notificationid");
                if (objid != null) remove(objid);
            }
        }
        
        private void remove(String objid) {
            if (objid == null) return; 

            root.messages.remove(objid); 
            EventQueue.invokeLater(new Runnable() { 
                public void run() { 
                    root.updateCount(); 
                } 
            }); 
        }
        
        private int indexOf(Object data) {
            if (data == null) return -1;
            
            String objid = getBeanValueAsString(data, "notificationid");
            for (int i=0; i<root.messages.size(); i++) {
                Object o = root.messages.get(i);
                if (o == null) continue;
                if (o.equals(data)) return i;
                
                String sid = getBeanValueAsString(o, "notificationid");
                if (objid != null && objid.equals(sid)) return i;
            }
            return -1;
        }
        
        private String getString(Map map, String name) {
            Object value = (map == null? null: map.get(name));
            return (value == null? null: value.toString()); 
        }
    } 
    
    private String getBeanValueAsString(Object bean, String name) {
        Object value = getBeanValue(bean, name);
        return (value == null? null: value.toString());
    }
    
    private Object getBeanValue(Object bean, String name) {
        if (bean == null) return null;
        if (bean instanceof Map) { 
            return ((Map) bean).get(name); 
        }
        
        Class beanClass = bean.getClass();
        Method method = findGetMethod(beanClass, "notificationid"); 
        if (method == null) return null;
        
        try { 
            return method.invoke(bean, new Object[]{}); 
        } catch(Throwable t) {
            System.out.println("failed to get value from method ["+method+"] caused by " + t.getMessage());
            return null; 
        }
    }
    
    private Method findGetMethod(Class clazz, String name) {
        try { 
            if (name == null || name.length() <= 3) return null; 

            String methodName = "get" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
            for (Method method : clazz.getMethods()) {
                if (!method.getName().equals(methodName)) continue;
                if ("void".equals(method.getReturnType().toString())) continue;
                
                Class[] paramTypes = method.getParameterTypes();
                if (paramTypes != null && paramTypes.length > 1) continue;
                
                return method;
            }
            return null;
        } catch(Throwable t) {
            return null; 
        }
    }  
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" LabelImpl ">
    
    private class LabelImpl extends JLabel 
    {
        public LabelImpl() {
            setOpaque(true);
            setBackground(Color.RED);
            setForeground(Color.WHITE); 
            setFont(Font.decode("monospaced-bold-11")); 
            setBorder(BorderFactory.createEmptyBorder(0,1,0,1)); 
            setHorizontalAlignment(SwingConstants.CENTER); 
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" LayoutImpl ">
    
    private class LayoutImpl implements LayoutManager, LayoutManager2 
    {
        NotificationButton root = NotificationButton.this;
        
        public void addLayoutComponent(String name, Component comp) {}
        public void removeLayoutComponent(Component comp) {}

        public Dimension preferredLayoutSize(Container parent) {
            return getLayoutSize(parent);
        }

        public Dimension minimumLayoutSize(Container parent) {
            return getLayoutSize(parent);
        }

        private boolean isLabelVisible() {
            if (root.lbl == null) return false; 
            if (!root.lbl.isVisible()) return false; 
            
            String text = root.lbl.getText();
            return (text != null && text.length() > 0 && !text.equals("0")); 
        }
        
        private Dimension getLayoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                int w=0, h=0;      
                Icon icon = root.getIcon();
                if (icon != null) {
                    w = icon.getIconWidth();
                    h = icon.getIconHeight();
                }
                if (isLabelVisible()) {
                    Dimension dim = root.lbl.getPreferredSize();
                    w = Math.max((w/2)+dim.width, 0);
                    h = Math.max(dim.height+2, h); 
                } 
                
                Insets margin = parent.getInsets(); 
                w += (margin.left + margin.right); 
                h += (margin.top + margin.bottom); 
                return new Dimension(w, h); 
            }
        }
        
        public void layoutContainer(Container parent) {
            synchronized (parent.getTreeLock()) {
                if (root.lbl == null) return; 
                if (isLabelVisible()) { 
                    Dimension dim = root.lbl.getPreferredSize(); 
                    int pw = parent.getWidth(); 
                    int cw = Math.max(dim.width, 12);
                    int px = Math.max(pw-cw, 0);
                    root.lbl.setBounds(px, 2, cw, dim.height); 
                } else {
                    root.lbl.setBounds(0, 0, 0, 0); 
                }
            }
        }

        public void addLayoutComponent(Component comp, Object constraints) {}

        public Dimension maximumLayoutSize(Container target) {
            return getLayoutSize(target); 
        }

        public float getLayoutAlignmentX(Container target) {
            return 0.1f;
        }

        public float getLayoutAlignmentY(Container target) {
            return 0.1f;
        }

        public void invalidateLayout(Container target) {
            layoutContainer(target); 
        }
    } 
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Popup support ">
    
    private NotificationPopup popup; 
    
    private NotificationPopup getPopup() {
        if (popup == null) {
            popup = new NotificationPopup(this);
            popup.setEventHandler(new EventHandlerImpl()); 
        }
        return popup;
    }
    
    private void showPopup() {
        Rectangle rect = getBounds();
        Point point = getLocationOnScreen(); 
        NotificationPopup popup = getPopup(); 
        popup.setData(messages.values()); 
        popup.pack();
        popup.show(this, 0, rect.height); 
    }

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" EventHandlerImpl ">
    
    private static Object EVENT_LOCK = new Object();
    
    private class EventHandlerImpl implements NotificationPopup.EventHandler 
    {
        NotificationButton root = NotificationButton.this;
        
        public void openItem(Object data) {
            String filetype = root.getBeanValueAsString(data, "filetype");
            if (filetype == null || filetype.length() == 0) filetype = "notification-item";
            
            Map params = new HashMap();
            params.put("entity", data);
            
            if (data instanceof Map) {
                Map datamap = (Map)data;
                Object userobj = datamap.get("data"); 
                if (userobj instanceof Map) {
                    Map userobjmap = (Map)userobj;
                    datamap.put("taskid", userobjmap.get("taskid")); 
                }
            }
                        
            Opener opener = null;
            try { 
                opener = InvokerUtil.lookupOpener(filetype+":open", params);
            } catch(Exception e) {
                root.getPopup().setVisible(false); 
                e = ExceptionManager.getOriginal(e); 
                MsgBox.alert(e.getMessage()); 
                return; 
            }
            if (opener == null) return;
            
            String target = opener.getTarget();
            if (target == null || target.length() == 0) {
                opener.setTarget("window"); 
            } else if (target.matches("popup|_popup")) {
                opener.setTarget("popup"); 
                opener.getProperties().put("immediate", true); 
            } 
            root.getPopup().setVisible(false); 
            EventQueue.invokeLater(new ShowOpenerProcess(opener));
        }

        public void seeAll() {
            Map params = new HashMap();
            params.put("messages", messages); 
            Opener opener = null;
            try { 
                opener = InvokerUtil.lookupOpener("notification-list:open", params);
            } catch(Exception e) {
                root.getPopup().setVisible(false); 
                e = ExceptionManager.getOriginal(e); 
                MsgBox.alert(e.getMessage()); 
                return; 
            }
            if (opener == null) return;
            
            opener.setTarget("window");
            opener.getProperties().put("immediate", true); 
            root.getPopup().setVisible(false); 
            EventQueue.invokeLater(new ShowOpenerProcess(opener));            
        }
    }
    
    private class ShowOpenerProcess implements Runnable 
    {
        private Opener opener;
        
        ShowOpenerProcess(Opener opener) {
            this.opener = opener;
        }
        
        public void run() {
            synchronized (EVENT_LOCK) {
                Object val = opener.getProperties().get("has_been_processed");
                if ("true".equals(val+"")) return;
                
                opener.getProperties().put("has_been_processed", true);
                OpenerUtil.show(opener); 
            }
        }        
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" NotificationRendererImpl ">
    
    private class NotificationRendererImpl implements NotificationRenderer, Runnable {

        NotificationButton root = NotificationButton.this; 
        
        public void refresh() { 
            if ( EventQueue.isDispatchThread() ) { 
                run(); 
            } else { 
                EventQueue.invokeLater( this ); 
            } 
        }

        public void run() { 
            root.updateCount(); 
        }
    }
    
    // </editor-fold>
}
