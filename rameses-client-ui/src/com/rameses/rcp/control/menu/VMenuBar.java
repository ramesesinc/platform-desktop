/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.control.menu;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

/**
 *
 * @author wflores
 */
public class VMenuBar extends JPanel {
    
    public VMenuBar() {
        super(); 
        initComponent();
    }
    
    private void initComponent() {
        super.setLayout( new LayoutManagerImpl()); 
        addContainerListener(new ContainerListener() {
            public void componentAdded(ContainerEvent e) {
                if ( e.getChild() instanceof JMenuItem ) {
                    getRoot().componentAdded((JMenuItem) e.getChild()); 
                }
            }
            public void componentRemoved(ContainerEvent e) {
                if ( e.getChild() instanceof JMenuItem ) {
                    getRoot().componentRemoved((JMenuItem) e.getChild()); 
                }
            }
        }); 
        
        AWTEventHandler handler = new AWTEventHandler();
        Toolkit.getDefaultToolkit().addAWTEventListener( handler, AWTEvent.MOUSE_EVENT_MASK ); 
    }
    
    private VMenuBar getRoot() { return this; }

    public void setLayout(LayoutManager mgr) {
    }
    
    private void componentAdded( JMenuItem mi ) {
        if ( mi == null ) return;

        Object o = mi.getClientProperty(MenuItemMouseHandler.class); 
        if (!(o instanceof MenuItemMouseHandler )) {
            MenuItemMouseHandler h = new MenuItemMouseHandler(); 
            mi.addMouseListener( h ); 
            mi.putClientProperty(MenuItemMouseHandler.class, h); 
        } 
        
        if ( mi instanceof JMenu ) {
            // do nothing 
        } else {
            o = mi.getClientProperty(VMenu.ActionHandler.class); 
            if (!(o instanceof VMenu.ActionHandler )) {
                VMenu.ActionHandler h = new VMenu.ActionHandler(); 
                mi.addActionListener( h ); 
                mi.putClientProperty(VMenu.ActionHandler.class, h); 
            } 
        }
    }
    
    private void componentRemoved( JMenuItem mi ) { 
        if ( mi == null ) return; 
        
        Object o = mi.getClientProperty(MenuItemMouseHandler.class); 
        if ( o instanceof MenuItemMouseHandler ) { 
            MenuItemMouseHandler h = (MenuItemMouseHandler) o;
            mi.removeMouseListener( h ); 
        }
        
        o = mi.getClientProperty(VMenu.ActionHandler.class); 
        if ( o instanceof VMenu.ActionHandler ) {
            VMenu.ActionHandler h = (VMenu.ActionHandler) o;
            mi.removeActionListener( h ); 
        }
    }  

    public VMenu addMenu( String text ) {
        VMenu m = new VMenu( text ); 
        add( m ); 
        return m; 
    }
    
    public JMenuItem addMenuItem( String text ) {
        JMenuItem mi = new JMenuItem( text ); 
        add( mi ); 
        return mi; 
    } 

    private class LayoutManagerImpl implements LayoutManager {

        final int MIN_HEIGHT = 24;
        
        public void addLayoutComponent(String name, Component comp) {
        }
        public void removeLayoutComponent(Component comp) {
        }

        public Dimension preferredLayoutSize(Container parent) {
            return getLayoutSize( parent ); 
        }

        public Dimension minimumLayoutSize(Container parent) {
            return getLayoutSize( parent ); 
        }
        
        private Dimension getLayoutSize( Container parent ) {
            synchronized (parent.getTreeLock()) {
                int w=0, h=0; 
                
                Component[] comps = parent.getComponents(); 
                for (int i=0; i<comps.length; i++) {
                    if ( !comps[i].isVisible()) continue; 
                    
                    Dimension dim = comps[i].getPreferredSize(); 
                    h += Math.max(dim.height, MIN_HEIGHT); 
                    w = Math.max(dim.width, w); 
                }
                
                Insets m = parent.getInsets(); 
                w += (m.left + m.right);
                h += (m.top + m.bottom);
                return new Dimension(w, h); 
            }
        }

        public void layoutContainer(Container parent) {
            synchronized (parent.getTreeLock()) {
                Insets pad = parent.getInsets(); 
                int pw = parent.getWidth();
                int ph = parent.getHeight(); 
                int x = pad.left, y = pad.top; 
                int w = pw - (pad.left + pad.right); 
                int h = ph - (pad.top + pad.bottom);
                
                Component[] comps = parent.getComponents(); 
                for (int i=0; i<comps.length; i++) {
                    if ( !comps[i].isVisible()) continue; 
                    
                    Dimension dim = comps[i].getPreferredSize(); 
                    int ch = Math.max(dim.height, MIN_HEIGHT); 
                    comps[i].setBounds(x, y, w, ch);
                    comps[i].setSize( w, ch );
                    y += ch; 
                }
            }
        }
        
    }

    private final Object ItemSelectionLocked = new Object();
    private class ItemSelectionModel { 
        private JMenuItem selItem; 
        
        void select( JMenuItem mi ) {
            synchronized( getRoot().ItemSelectionLocked ) {
                selectImpl( mi ); 
            }
        }
        void deselect( JMenuItem mi ) {
            synchronized( getRoot().ItemSelectionLocked ) {
                deselectImpl( mi ); 
            }
        }
        void deselectAll() {
            synchronized( getRoot().ItemSelectionLocked ) {
                deselectAllImpl(); 
            }
        }
        
        void selectImpl( JMenuItem mi ) {
            if ( selItem != null ) {
                deselect( selItem ); 
                selItem = null;
            } 
            
            mi.setArmed(true);
            mi.setSelected(true);
            mi.setRolloverEnabled(true);
            mi.setBorderPainted(true);
            mi.repaint();
            
            selItem = mi; 
            if ( mi instanceof JMenu ) {
                JMenu jm = (JMenu) mi;
                jm.setPopupMenuVisible(true); 
            }
        }
        
        void deselectImpl( JMenuItem mi ) {
            mi.setArmed(false);
            mi.setSelected(false);
            mi.setRolloverEnabled(false);
            mi.setBorderPainted(false);
            mi.repaint(); 
            
            if ( mi instanceof JMenu ) {
                JMenu jm = (JMenu) mi;
                jm.setPopupMenuVisible( false ); 
            }
        }
        
        void deselectAllImpl() {
            Component[] comps = getRoot().getComponents(); 
            for (int i=0; i<comps.length; i++) {
                if ( comps[i] instanceof JMenuItem ) {
                    deselectImpl((JMenuItem) comps[i]); 
                } 
            } 
        } 
    } 
    
    private ItemSelectionModel ism = new ItemSelectionModel();
    private class MenuItemMouseHandler extends MouseAdapter {        
        public void mouseEntered(MouseEvent e) {
            JMenuItem mi = (JMenuItem) e.getSource(); 
            ism.select( mi ); 
        }
    } 
    
    private class AWTEventHandler implements AWTEventListener {
        public void eventDispatched(AWTEvent e) { 
            if ( e instanceof MouseEvent ) {
                processMouseEvent((MouseEvent) e); 
            } 
        } 
        
        private void processMouseEvent( MouseEvent e ) {
            if ( e.getID() == MouseEvent.MOUSE_CLICKED ) {
                Object source = e.getSource(); 
                if ( source instanceof Component ) {
                    Object vm = findTopVMenuBar((Component) source);
                    if ( vm != null && vm.equals(getRoot())) {
                        //do nothing 
                    } else {
                        getRoot().ism.deselectAll(); 
                    }
                }
            }
        }
        
        private Component findTopVMenuBar( Component comp ) {
            if ( comp == null ) return null;
            
            if ( comp instanceof VMenuBar ) {
                return comp;
                
            } else if ( comp instanceof JPopupMenu ) {
                JPopupMenu pm = (JPopupMenu)comp;
                Component inv = pm.getInvoker(); 
                return (inv == null ? null : findTopVMenuBar(inv)); 
                
            } else {
                return findTopVMenuBar(comp.getParent()); 
            }
        }
    }
}
