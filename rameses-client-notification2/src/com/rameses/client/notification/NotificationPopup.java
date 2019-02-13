/*
 * NotificationPopup.java
 *
 * Created on January 14, 2014, 5:24 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.client.notification;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.border.AbstractBorder;

/**
 *
 * @author wflores
 */
public class NotificationPopup extends JPopupMenu 
{
    private NotificationButton invoker;
    private JScrollPane scrollpane; 
    private JList jlist;
    private JLabel jheader;
    
    private Color borderColor;
    private Color backgroundColor;
    private int hoverIndex = -1;
    private ActionLabel actionLabel;
    
    public NotificationPopup(NotificationButton invoker) {
        this.invoker = invoker; 
        initComponent();
    }
    
    private void initComponent() { 
        setLayout(new DefaultLayout()); 
        setBorder(new BorderImpl()); 
        setOpaque(false); 

        borderColor = Color.decode("#a0a0a0");
        backgroundColor = Color.WHITE;
        scrollpane = new JScrollPane();
        scrollpane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); 
        scrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); 
        scrollpane.setBackground(Color.WHITE); 
        scrollpane.setBorder(BorderFactory.createEmptyBorder()); 
        add(scrollpane);  
        
        jlist = new JList();
        jlist.setCellRenderer(new DefaultCellRenderer()); 
        jlist.setSelectionBackground(new Color(244, 246, 249)); 
        jlist.setSelectionForeground(Color.BLACK); 
        jlist.setFixedCellHeight(40);  
        jlist.addMouseMotionListener(new MouseMotionListener() {
            public void mouseDragged(MouseEvent e) {}
            public void mouseMoved(MouseEvent e) {
                hoverIndex = jlist.locationToIndex(e.getPoint());
                jlist.repaint();
            }
        });
        jlist.addMouseListener(new MouseListener() {
            public void mouseEntered(MouseEvent e) {}
            public void mousePressed(MouseEvent e) {}
            public void mouseReleased(MouseEvent e) {}
            public void mouseExited(MouseEvent e) {
                hoverIndex = -1;
                jlist.repaint();
            } 
            public void mouseClicked(MouseEvent e) {
                onmouseClicked(e);
            } 
        });
        scrollpane.setViewportView(jlist); 
        
        jheader = new JLabel("Notifications");
        Font font = jheader.getFont();
        jheader.setFont(font.deriveFont(Font.BOLD));
        jheader.setForeground(Color.decode("#606060")); 
        jheader.setBorder(new HeaderBorderImpl());
        add(jheader);
        
        actionLabel = new ActionLabel() {
            protected void onDoubleClick() {
            }

            protected void onClick() { 
                if (eventHandler != null) {
                    eventHandler.seeAll();
                } 
            }
        }; 
        actionLabel.setText("See All");
        add(actionLabel); 
    } 
    
    public void setData(Collection list) {
        DefaultListModel model = new DefaultListModel();
        if (list != null) {
            for (Object o : list) {
                model.addElement(o); 
            } 
        }
        if (model.getSize() == 0) {
            model.addElement(new NoData());
        } 
        jlist.setModel(model);         
    } 
    
    private void onmouseClicked(MouseEvent e) {
        if (e.getClickCount() != 2) return;
        if (!SwingUtilities.isLeftMouseButton(e)) return; 
        if (eventHandler == null) return;
        
        Object value = jlist.getSelectedValue(); 
        if (value == null || value instanceof NoData) return;
        
        eventHandler.openItem(value);
    }
    
    // <editor-fold defaultstate="collapsed" desc=" NoData ">
    
    private class NoData {}
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" EventHandler support ">
    
    private EventHandler eventHandler;
    public void setEventHandler(EventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }
    
    public static interface EventHandler 
    {
        void openItem(Object data);
        void seeAll();
    }
    
    // </editor-fold>
        
    // <editor-fold defaultstate="collapsed" desc=" DefaultLayout ">
    
    private class DefaultLayout implements LayoutManager 
    {
        NotificationPopup root = NotificationPopup.this;
        
        public void addLayoutComponent(String name, Component comp) {}
        public void removeLayoutComponent(Component comp) {}

        public Dimension preferredLayoutSize(Container parent) {
            return getLayoutSize(parent);
        }

        public Dimension minimumLayoutSize(Container parent) {
            return getLayoutSize(parent);
        }

        private Dimension getLayoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                int w=0, h=0; 
                if (root.jheader != null) {
                    Dimension dim = root.jheader.getPreferredSize();
                    w = dim.width;
                    h = dim.height;
                } 
                if (root.actionLabel != null) {
                    Dimension dim = root.actionLabel.getPreferredSize();
                    w += dim.width;
                    h = Math.max(dim.height, h); 
                }
                if (root.jlist != null) {
                    Dimension dim = root.jlist.getPreferredSize();
                    w = Math.max(dim.width, w); 
                    h += dim.height;
                }
                if (w <= 300) w = 300;
                else if (w > 300) w = 400;
                
                if (h <= 40) h = 40;
                else if (h > 300) h = 300;
                
                Insets margin = parent.getInsets();
                w += (margin.left + margin.right);
                h += (margin.top + margin.bottom);
                return new Dimension(w, h);
            }
        }
        
        public void layoutContainer(Container parent) {
            synchronized (parent.getTreeLock()) {
                Insets margin = parent.getInsets();
                int pw = parent.getWidth();
                int ph = parent.getHeight();
                int x = margin.left;
                int y = margin.top;
                int w = pw - (margin.left + margin.right); 
                int h = ph - (margin.top + margin.bottom);
                if (root.jheader != null) {
                    Dimension dim = root.jheader.getPreferredSize();
                    root.jheader.setBounds(x, y, w, dim.height); 
                    y += dim.height;
                    h -= dim.height;
                    
                    Dimension dim2 = root.actionLabel.getPreferredSize();
                    int x2 = (w - dim2.width - 10) + margin.left; 
                    int y2 = (dim.height - dim2.height) / 2;
                    root.actionLabel.setBounds(x2, y2, dim2.width, dim2.height); 
                }                 
                if (root.scrollpane != null) {
                    root.scrollpane.setBounds(x, y, w, h); 
                } 
            }
        }
    }
    
    // </editor-fold> 
    
    // <editor-fold defaultstate="collapsed" desc=" BorderImpl ">
    
    private class BorderImpl extends AbstractBorder 
    {
        NotificationPopup root = NotificationPopup.this;
        
        public Insets getBorderInsets(Component c) {
            Insets ins = new Insets(0,0,0,0);
            return getBorderInsets(c, ins);
        }

        public Insets getBorderInsets(Component c, Insets ins) {
            ins.top = ins.left = ins.bottom = ins.right = 2;
            return ins;
        } 

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            if (c instanceof JComponent) { 
                ((JComponent)c).setOpaque(false); 
            }
            Color oldColor = g.getColor();
            g.setColor(root.backgroundColor);
            g.fillRoundRect(x+2, y+2, w-3, h-3, 5, 5);
            g.setColor(root.borderColor);
            g.drawRoundRect(x, y, w-1, h-1, 5, 5); 
            g.setColor(oldColor); 
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" HeaderBorderImpl ">
    
    private class HeaderBorderImpl extends AbstractBorder 
    {
        NotificationPopup root = NotificationPopup.this;
        Color borderColor = Color.decode("#e7e7e7");
        
        public Insets getBorderInsets(Component c) {
            Insets ins = new Insets(0,0,0,0);
            return getBorderInsets(c, ins);
        }

        public Insets getBorderInsets(Component c, Insets ins) {
            ins.top = ins.left = ins.bottom = ins.right = 5;
            ins.left = 10;
            return ins;
        } 

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Color oldColor = g.getColor();
            g.setColor(borderColor); 
            g.drawLine(0, h-1, w, h-1);
            g.setColor(oldColor); 
        }
    }
    
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" DefaultCellRenderer ">
    
    private class DefaultCellRenderer implements ListCellRenderer 
    {
        NotificationPopup root = NotificationPopup.this; 
        private JLabel cellLabel;
        
        DefaultCellRenderer() {
            cellLabel = new JLabel();
        }
        
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) 
        {
            CellBorderImpl border = new CellBorderImpl();
            if (index+1 >= list.getModel().getSize()) { 
                border.hideBorder();
            } else {
                border.showBorder(); 
            }

            cellLabel.setBorder(border); 
            cellLabel.setComponentOrientation(list.getComponentOrientation());
            cellLabel.setSize(list.getFixedCellWidth(), list.getFixedCellHeight());
            cellLabel.setEnabled(list.isEnabled());
            cellLabel.setFont(list.getFont());
            
            if (isSelected || index == root.hoverIndex) {
                cellLabel.setBackground(list.getSelectionBackground());
                cellLabel.setForeground(list.getSelectionForeground());
                cellLabel.setOpaque(true); 
            } else {                
                cellLabel.setBackground(list.getBackground());
                cellLabel.setForeground(list.getForeground());
                cellLabel.setOpaque(false); 
            }
           
            String message = null; 
            if (value instanceof NoData) {
                message = "<p align=\"center\" width=\"300\">--<i> No available record(s) found </i>--</p>";
            } else { 
                message = getBeanValueAsString(value, "message"); 
            } 
            cellLabel.setText("<html>"+(message == null? "": message.toString())+"</html>");
            return cellLabel;
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
        
    // <editor-fold defaultstate="collapsed" desc=" CellBorderImpl ">
    
    private class CellBorderImpl extends AbstractBorder 
    {
        NotificationPopup root = NotificationPopup.this;
        Color borderColor = Color.decode("#eaeaea");
        
        private boolean borderVisible = true;
        
        void hideBorder() { borderVisible = false; }
        void showBorder() { borderVisible = true; } 
        
        public Insets getBorderInsets(Component c) {
            Insets ins = new Insets(0,0,0,0);
            return getBorderInsets(c, ins);
        }

        public Insets getBorderInsets(Component c, Insets ins) {
            ins.top = ins.left = ins.bottom = ins.right = 5;
            ins.left = ins.right = 10;
            return ins;
        } 

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            if (!borderVisible) return;
            Color oldColor = g.getColor();
            g.setColor(borderColor); 
            g.drawLine(0, h-1, w, h-1);
            g.setColor(oldColor); 
        }
    }
    
    // </editor-fold>
}
