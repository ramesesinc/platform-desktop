/*
 * CTabbedPane.java
 *
 * Created on November 14, 2013, 2:17 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.platform;

import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

/**
 *
 * @author wflores
 */
public class WindowTabbedPane extends JTabbedPane
{
    private ImageIcon defaultIcon;
    private ImageIcon overIcon;
    private ImageIcon pressIcon;
    
    private ImageIcon selCloseIcon;
    private PropertyTabSupport propertyTabSupport; 
    private MouseTabSupport mouseTabSupport;

    public WindowTabbedPane() {
        defaultIcon = createIcon("icon/tab_close_default.png");
        selCloseIcon = defaultIcon;        
        overIcon = createIcon("icon/tab_close_over.png");
        if (overIcon == null) overIcon = defaultIcon;

        pressIcon = createIcon("icon/tab_close_press.png");
        if (pressIcon == null) pressIcon = overIcon;
        
        addPropertyChangeListener(getPropertyTabSupport()); 
        addMouseListener(getMouseTabSupport());
        addMouseMotionListener(getMouseTabSupport());
        new CloseAllAction().install();
        new CloseAction().install();
        new MaxWinAction().install();
    }

    private PropertyTabSupport getPropertyTabSupport() {
        if (propertyTabSupport == null) {
            propertyTabSupport = new PropertyTabSupport(); 
        }
        return propertyTabSupport; 
    }
    
    private MouseTabSupport getMouseTabSupport() {
        if (mouseTabSupport == null) {
            mouseTabSupport = new MouseTabSupport(); 
        }
        return mouseTabSupport; 
    }
    
    public void setMnemonicAt(int index, int mnemonic) {
        super.setMnemonicAt(index, mnemonic); 
        getPropertyTabSupport().setMnemonicAt(index, mnemonic); 
    }

    public void setDisabledIconAt(int index, Icon icon) {
        super.setDisabledIconAt(index, icon); 
        getPropertyTabSupport().setDisabledIconAt(index, icon); 
    }    

    public void setDisplayedMnemonicIndexAt(int index, int mnemonicIndex) {
        super.setDisplayedMnemonicIndexAt(index, mnemonicIndex); 
        getPropertyTabSupport().setDisplayedMnemonicIndexAt(index, mnemonicIndex); 
    }

    public void setEnabledAt(int index, boolean enabled) {
        super.setEnabledAt(index, enabled); 
        getPropertyTabSupport().setEnabledAt(index, enabled);         
    }

    public void setIconAt(int index, Icon icon) {
        super.setIconAt(index, icon);
        getPropertyTabSupport().setIconAt(index, icon);
    }

    public void setToolTipTextAt(int index, String tip) {
        super.setToolTipTextAt(index, tip);
        getPropertyTabSupport().setToolTipTextAt(index, tip); 
    }

    protected void processMouseEvent(MouseEvent e) {
        if (e.getID() == MouseEvent.MOUSE_PRESSED) {
            //track if the user clicks the right button 
            //if so, do not changed the selection 
            getMouseTabSupport().setPopupInvoker(null);
            if (SwingUtilities.isRightMouseButton(e)) {
                int index = getUI().tabForCoordinate(this, e.getX(), e.getY());
                if (index >= 0) { 
                    PopupInvoker pi = new PopupInvoker(e.getPoint(), index); 
                    getMouseTabSupport().setPopupInvoker(pi);
                } 
            }
        }
        super.processMouseEvent(e); 
    }

    public void setSelectedIndex(int index) {
        if (getMouseTabSupport().hasPopupInvoker()) return;
        
        super.setSelectedIndex(index); 
    }
            
    public void insertTab(String title, Icon icon, Component comp, String tip, int index) {
        super.insertTab(title, icon, comp, tip, index); 
        
        Component tc = getTabComponentAt(index); 
        if (tc instanceof JLabel) return;
        
        TabRenderer tr = new TabRenderer(title, icon, comp, tip, index); 
        if (defaultIcon == null) { 
            tr.setMargin(0, 0, 0, 15); 
        } else {
            tr.setMargin(0, 0, 0, 20); 
        }
        setTabComponentAt(index, tr); 
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g); 
        
        int selIndex = getSelectedIndex();
        if (selIndex < 0) return;
        if (selCloseIcon == null) return;
        if (!isCloseable(selIndex)) return;
        
        Rectangle rect = getBoundsAt(selIndex);
        int iw = selCloseIcon.getIconWidth(); 
        int ih = selCloseIcon.getIconHeight(); 
        int x = rect.x + (rect.width - 16);
        int y = rect.y + Math.max((rect.height - ih) / 2, 0);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.drawImage(selCloseIcon.getImage(), x, y, null); 
        g2.dispose(); 
    }
    
    private ImageIcon createIcon(String name) {
        try { 
            URL url = WindowTabbedPane.class.getResource(name); 
            return new ImageIcon(url); 
        } catch(Throwable t) {
            return null; 
        }
    }
    
    private ImageIcon createEmptyIcon() {
        BufferedImage bi = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = (Graphics2D) bi.createGraphics(); 
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR));
        g2.fillRect(0, 0, 16, 16); 
        g2.dispose(); 
        return new ImageIcon(bi); 
    }
    
    private void close(int index) {
        if (index < 0 || index >= getTabCount()) return;
        if (!isCloseable(index)) return;
        
        Component comp = getComponentAt(index); 
        if (beforeClose(comp)) super.remove(index);  
    } 
    
    private void closeAll() { 
        closeAll(-1); 
    } 
    
    private void closeAll(int excludeIndex) { 
        Component xc = null;
        try { 
            xc = getComponentAt(excludeIndex); 
        } catch(Throwable t){;} 
                
        Component[] comps = getComponents();
        for (Component c: comps) {
            int idx = indexOfComponent(c); 
            if (idx < 0) continue; 
            if (!isCloseable(idx)) continue; 
            if (xc != null && xc.equals(c)) continue; 
            if (beforeClose(c)) super.remove(c);
        }
    } 

    public void remove(int index) { 
        if (index < 0 || index >= getTabCount()) return;
        
        Component comp = getComponentAt(index);         
        if (beforeClose(comp)) super.remove(index); 
    }

    public void remove(Component component) {
        if (component != null && beforeClose(component)) {
            super.remove(component);
        } 
    }

    public void removeTabAt(int index) {
        if (index < 0 || index >= getTabCount()) return;
        
        Component comp = getComponentAt(index); 
        String title = getTitleAt(index); 
        super.removeTabAt(index); 
        afterClose(comp, title); 
    }

    protected boolean beforeClose(Component component) {
        return true; 
    }
    
    protected void afterClose(Component component, String title) {
    }
    
    protected boolean isCloseable(int index) {
        return true;
    }

    
    // <editor-fold defaultstate="collapsed" desc=" MouseTabSupport ">
    
    private class MouseTabSupport implements MouseListener, MouseMotionListener 
    {
        WindowTabbedPane root = WindowTabbedPane.this;
        
        private boolean pressed;
        private int focusIndex;
        private PopupMenuSupport popupMenuSupport;
        
        private PopupMenuSupport getPopupMenuSupport() {
            if (popupMenuSupport == null) {
                popupMenuSupport = new PopupMenuSupport(); 
            }
            return popupMenuSupport; 
        }
        
        boolean hasPopupInvoker() {
            return (getPopupMenuSupport().popupInvoker != null); 
        }
        
        void setPopupInvoker(PopupInvoker popupInvoker) {
            getPopupMenuSupport().popupInvoker = popupInvoker; 
        }
        
        
        public void mouseEntered(MouseEvent e) {}        
        public void mousePressed(MouseEvent e) {
            boolean b = hasIntersect(e);
            if (b) {
                if (focusIndex == root.getSelectedIndex()) {
                    root.selCloseIcon = root.pressIcon;    
                } else {
                    root.selCloseIcon = root.defaultIcon;
                }                
                pressed = true;                
            } else { 
                root.selCloseIcon = root.defaultIcon;
                pressed = false;
            } 
            root.repaint();
        }
        
        public void mouseReleased(MouseEvent e) {
            pressed = false;
        }
        
        public void mouseClicked(MouseEvent e) {  
            boolean b = hasIntersect(e);
            if (b) {
                root.selCloseIcon = root.overIcon;
                root.repaint();
                
                int selIndex = root.getSelectedIndex();
                if (focusIndex == selIndex) {
                    EventQueue.invokeLater(new CloseTabRunnable(selIndex)); 
                    return; 
                }
            } 
            
            if (SwingUtilities.isRightMouseButton(e)) {
                int locIndex = root.getUI().tabForCoordinate(root, e.getX(), e.getY()); 
                if (locIndex >= 0) {
                    getPopupMenuSupport().show(e.getPoint());
                } else {
                    getPopupMenuSupport().reset(); 
                }
            } 
        }
        
        public void mouseExited(MouseEvent e) {}        
        public void mouseMoved(MouseEvent e) {
            if (pressed) return;
            
            focusIndex = root.getSelectedIndex();
            if (hasIntersect(e)) { 
                root.selCloseIcon = root.overIcon;
            } else { 
                root.selCloseIcon = root.defaultIcon;
            }
            
            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    root.repaint();
                }
            });
        }
        
        public void mouseDragged(MouseEvent e) {
            if (!pressed) return;
            
            boolean b = hasIntersect(e);
            if (b) { 
                root.selCloseIcon = root.pressIcon;
            } else {
                root.selCloseIcon = root.defaultIcon;
            } 
            focusIndex = root.getSelectedIndex();
            root.repaint();
        }                 
        
        private boolean hasIntersect(MouseEvent e) {
            int selIndex = root.getSelectedIndex();
            if (selIndex < 0) return false; 
            if (!isCloseable(selIndex)) return false; 
            
            Point p = e.getPoint();
            Rectangle rect = root.getBoundsAt(selIndex);
            if (!rect.contains(p)) return false; 
            
            int w = rect.x + rect.width;
            if (p.getX() >= w-16 && p.getX() < w) {
                return true; 
            } else {
                return false; 
            }            
        }
    }

    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" PopupMenuSupport ">
    
    private class PopupMenuSupport 
    {
        WindowTabbedPane root = WindowTabbedPane.this;
        
        private PopupInvoker popupInvoker;
        private JPopupMenu popup;
        private int index;
        
        PopupMenuSupport() {
            popup = new JPopupMenu();
            popup.add(createCloseAllMenuItem());
            popup.add(createCloseOthersMenuItem());
            popup.addSeparator(); 
            popup.add(createCloseMenuItem());     
            popup.add(createMaxWinMenuItem()); 
        }
        
        void show(Point location) {
            if (popupInvoker == null) return;
            
            popupInvoker.location = location; 
            popupInvoker.setPopup(popup);
            EventQueue.invokeLater(popupInvoker); 
        }
        
        void reset() {
            popupInvoker = null; 
        }
                
        JMenuItem createCloseAllMenuItem() {
            CloseAllAction a = new CloseAllAction(); 
            a.setCallback(new ActionListener() { 
                public void actionPerformed(ActionEvent e) { 
                    reset(); 
                } 
            }); 
            return a.createMenuItem();
        } 
        
        JMenuItem createCloseOthersMenuItem() {
            CloseOtherAction a = new CloseOtherAction();
            a.setDelegate(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try { 
                        root.closeAll(popupInvoker.index); 
                    } finally {
                        reset();    
                    }  
                }
            }); 
            return a.createMenuItem(); 
        }         
        
        JMenuItem createCloseMenuItem() {
            CloseAction a = new CloseAction();
            a.setDelegate(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    root.close(popupInvoker.index);
                }
            });
            a.setCallback(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    reset();  
                }
            }); 
            return a.createMenuItem(); 
        } 
        
        JMenuItem createMaxWinMenuItem() {
            MaxWinAction a = new MaxWinAction();
            return a.createMenuItem();
        }         
    } 
    
    private class PopupInvoker implements Runnable {
        WindowTabbedPane root = WindowTabbedPane.this; 
        
        private JPopupMenu popup;
        private Point location;
        private int index;
                
        PopupInvoker(Point location, int index) {
            this.location = location;
            this.index = index; 
        }

        void setPopup(JPopupMenu popup) {
            this.popup = popup; 
        }

        public void run() {
            int x = (int) location.getX();
            int y = (int) location.getY();
            popup.show(root, x, y); 
        }
    } 
    
    private final static Object TABBEDPANE_CLOSE_ALL_ACTION = new Object();
    private final static Object TABBEDPANE_CLOSE_ACTION = new Object();
    private final static Object TABBEDPANE_MAX_WIN_ACTION = new Object();
    
    private class CloseAllAction extends AbstractAction {       
        WindowTabbedPane root = WindowTabbedPane.this;
        private KeyStroke keystroke;
        private ActionListener callback;
        
        CloseAllAction() {
            keystroke = KeyStroke.getKeyStroke("ctrl shift F4"); 
        }
        
        void install() {
            InputMap inputMap = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
            inputMap.put(keystroke, TABBEDPANE_CLOSE_ALL_ACTION); 
            root.getActionMap().put(TABBEDPANE_CLOSE_ALL_ACTION, this); 
        }
        
        void setCallback(ActionListener callback) {
            this.callback = callback;
        }
        
        KeyStroke getKeyStroke() { return keystroke; }
        
        JMenuItem createMenuItem() {
            JMenuItem jmi = new JMenuItem("Close All Windows");
            jmi.setAccelerator(getKeyStroke());
            jmi.addActionListener(this);
            return jmi; 
        }
        
        public void actionPerformed(final ActionEvent e) {
            EventQueue.invokeLater(new Runnable() {
                public void run() { 
                    actionPerformedImpl(e); 
                }
            }); 
        }
        
        public void actionPerformedImpl(ActionEvent e) {
            try {
                root.closeAll(); 
            } finally {
                if (callback != null) callback.actionPerformed(e); 
            }
        }
    }
    
    private class CloseOtherAction extends AbstractAction {       
        WindowTabbedPane root = WindowTabbedPane.this;
        private ActionListener callback;
        private ActionListener delegate;
        
        CloseOtherAction() {
        }
                
        void setCallback(ActionListener callback) {
            this.callback = callback;
        }
        
        void setDelegate(ActionListener delegate) {
            this.delegate = delegate;
        }
                
        JMenuItem createMenuItem() {
            JMenuItem jmi = new JMenuItem("Close Other Windows");
            jmi.addActionListener(this);
            return jmi; 
        }
        
        public void actionPerformed(final ActionEvent e) {
            EventQueue.invokeLater(new Runnable() {
                public void run() { 
                    actionPerformedImpl(e); 
                }
            }); 
        }
        
        public void actionPerformedImpl(ActionEvent e) {
            try {
                if (delegate != null) delegate.actionPerformed(e); 
            } finally {
                if (callback != null) callback.actionPerformed(e); 
            }
        }
    }
    
    private class CloseAction extends AbstractAction {       
        WindowTabbedPane root = WindowTabbedPane.this;
        private ActionListener delegate;
        private ActionListener callback;
        private KeyStroke keystroke;
        
        CloseAction() {
            keystroke = KeyStroke.getKeyStroke("ctrl W"); 
        }
        
        void install() {
            InputMap inputMap = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
            inputMap.put(keystroke, TABBEDPANE_CLOSE_ACTION); 
            root.getActionMap().put(TABBEDPANE_CLOSE_ACTION, this); 
        }        
        
        void setCallback(ActionListener callback) {
            this.callback = callback;
        }
        
        void setDelegate(ActionListener delegate) {
            this.delegate = delegate;
        }
        
        JMenuItem createMenuItem() {
            JMenuItem jmi = new JMenuItem("Close Window");
            jmi.setAccelerator(keystroke);
            jmi.addActionListener(this);
            return jmi; 
        }
        
        public void actionPerformed(final ActionEvent e) {
            EventQueue.invokeLater(new Runnable() {
                public void run() { 
                    actionPerformedImpl(e); 
                }
            }); 
        }
        
        public void actionPerformedImpl(ActionEvent e) {
            try {
                if (delegate != null) { 
                    delegate.actionPerformed(e); 
                } else { 
                    root.close(root.getSelectedIndex()); 
                } 
            } finally { 
                if (callback != null) callback.actionPerformed(e); 
            } 
        } 
    }
    
    private class MaxWinAction extends AbstractAction {       
        WindowTabbedPane root = WindowTabbedPane.this;
        private ActionListener delegate;
        private ActionListener callback;
        private KeyStroke keystroke;
        
        MaxWinAction() {
            keystroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, KeyEvent.SHIFT_MASK); 
        }
        
        void install() {
            InputMap inputMap = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
            inputMap.put(keystroke, TABBEDPANE_MAX_WIN_ACTION); 
            root.getActionMap().put(TABBEDPANE_MAX_WIN_ACTION, this); 
        }        
        
        void setCallback(ActionListener callback) {
            this.callback = callback;
        }
        
        void setDelegate(ActionListener delegate) {
            this.delegate = delegate;
        }
        
        JMenuItem createMenuItem() {
            JMenuItem jmi = new JMenuItem("Maximize Window");
            jmi.setAccelerator(keystroke);
            jmi.addActionListener(this);
            return jmi; 
        }
        
        public void actionPerformed(final ActionEvent e) {
            EventQueue.invokeLater(new Runnable() {
                public void run() { 
                    actionPerformedImpl(e); 
                }
            }); 
        }
        
        public void actionPerformedImpl(ActionEvent e) {
            try {
                if (delegate != null) { 
                    delegate.actionPerformed(e); 
                } else { 
                    //
                } 
            } finally { 
                if (callback != null) callback.actionPerformed(e); 
            } 
        } 
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" CloseTabRunnable ">
    
    private class CloseTabRunnable implements Runnable 
    {
        WindowTabbedPane root = WindowTabbedPane.this; 
        
        private int index;
        
        CloseTabRunnable(int index) {
            this.index = index; 
        }
        
        public void run() { 
            try { 
                root.remove(index); 
                root.selCloseIcon = root.defaultIcon; 
            } catch(Throwable t) {;} 
        }        
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" PropertyTabSupport ">
    
    private class PropertyTabSupport implements PropertyChangeListener 
    {
        WindowTabbedPane root = WindowTabbedPane.this;
        
        public void propertyChange(PropertyChangeEvent evt) {
            String propName = evt.getPropertyName();
            Object propValue = evt.getNewValue(); 
            if ("indexForTitle".equals(propName)) {
                if (!(propValue instanceof Integer)) return;
                
                int index = ((Integer) propValue).intValue();
                fireTitleChanged(index); 
                
            } else if ("enabled".equals(propName)) {
                if (!(propValue instanceof Boolean)) return;
                
                boolean enabled = ((Boolean) propValue).booleanValue();
                fireEnabledChanged(enabled);
            }
        }
        
        private TabRenderer getRenderer(int index) {
            if (index < 0) return null; 
            
            Component comp = root.getTabComponentAt(index); 
            if (comp instanceof TabRenderer) {
                return (TabRenderer) comp;
            } else {
                return null; 
            }
        }
        
        private void fireTitleChanged(int index) {
            WindowTabbedPane.TabRenderer tr = getRenderer(index); 
            if (tr == null) return;
            
            String title = root.getTitleAt(index); 
            tr.setText(title);
        }
        
        private void fireEnabledChanged(boolean enabled) {
            int count = root.getTabCount(); 
            for (int i=0; i<count; i++) {
                root.setEnabledAt(i, enabled); 
            }
        }        

        private void setDisplayedMnemonicIndexAt(int index, int mnemonicIndex) {
            WindowTabbedPane.TabRenderer tr = getRenderer(index); 
            if (tr != null) tr.setDisplayedMnemonicIndex(mnemonicIndex); 
        }

        private void setMnemonicAt(int index, int mnemonic) {
            WindowTabbedPane.TabRenderer tr = getRenderer(index); 
            if (tr != null) tr.setDisplayedMnemonic(mnemonic); 
        }

        private void setDisabledIconAt(int index, Icon icon) {
            WindowTabbedPane.TabRenderer tr = getRenderer(index); 
            if (tr != null) tr.setDisabledIcon(icon);
        }

        private void setIconAt(int index, Icon icon) {
            WindowTabbedPane.TabRenderer tr = getRenderer(index); 
            if (tr != null) tr.setIcon(icon);
        }
        
        private void setEnabledAt(int index, boolean enabled) {
            WindowTabbedPane.TabRenderer tr = getRenderer(index); 
            if (tr != null) tr.setEnabled(root.isEnabledAt(index)); 
        }        
        
        private void setToolTipTextAt(int index, String tip) {
            WindowTabbedPane.TabRenderer tr = getRenderer(index); 
            if (tr != null) tr.setToolTipText(tip); 
        }                
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" TabRenderer support ">
    
    private class TabRenderer extends JLabel 
    {
        private ImageIcon defaultIcon;
        private ImageIcon overIcon;
        private ImageIcon pressIcon;
        private int index;
        
        public TabRenderer() {
            initComponent();
        }
        
        public TabRenderer(String title, Icon icon, Component comp, String tip, int index) {
            this.index = index; 
            initComponent();
            setText(title);
            setIcon(icon);
            setToolTipText(tip);
        }
        
        private void initComponent() {
            setIconTextGap(5); 
            setMargin(0, 0, 0, 15); 
        }
        
        void setMargin(int top, int left, int bottom, int right) {
            setBorder(BorderFactory.createEmptyBorder(top,left,bottom,right)); 
        }
    } 
    
    // </editor-fold>   
}
