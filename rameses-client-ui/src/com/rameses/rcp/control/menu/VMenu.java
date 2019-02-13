/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.control.menu;

import com.rameses.rcp.common.MenuRootElement;
import java.awt.Component;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 *
 * @author wflores
 */
public class VMenu extends JMenu {
    
    private boolean borderPainted;
    
    public VMenu( String text ) {
        super( text ); 
        initComponent();
    }
    
    private void initComponent() {
        Insets p = getMargin();
        if ( p == null ) p = new Insets(0,0,0,0);
        
        Insets newpad = new Insets(p.top, p.left, p.bottom, p.right);
        newpad.right += 5; 
        setMargin( newpad ); 
    }
    
    private VMenu getRoot() { return this; }
    
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

    public JMenuItem add(JMenuItem menuItem) {
        if ( menuItem == null ) return null;
        installItemDefaults(menuItem); 
        return super.add(menuItem);
    }
    
    public boolean isBorderPainted() {
        return borderPainted;
    }
    public void setBorderPainted( boolean borderPainted ) {
        super.setBorderPainted( borderPainted ); 
        this.borderPainted = borderPainted;    
    }
    
    protected Point getPopupMenuOrigin() {
        Point p = super.getPopupMenuOrigin();
        Rectangle prect = getParent().getBounds();
        Rectangle crect = getBounds(); 
        p.x += prect.width; 
        p.y -= crect.height;
        return p; 
    }    

    public JPopupMenu getPopupMenu() {
        JPopupMenu pm = super.getPopupMenu();
        Object o = pm.getClientProperty(ContainerHandler.class); 
        if (!(o instanceof ContainerHandler)) {
            ContainerHandler ch = new ContainerHandler();
            pm.addContainerListener(ch);
            pm.putClientProperty(ContainerHandler.class, ch); 
        }
        return pm;
    }
    
    private void installItemDefaults( JMenuItem mi ) {
        Object o = mi.getClientProperty(MenuItemMouseHandler.class); 
        if (!(o instanceof MenuItemMouseHandler )) {
            MenuItemMouseHandler h = new MenuItemMouseHandler(); 
            mi.addMouseListener( h ); 
            mi.putClientProperty(MenuItemMouseHandler.class, h); 
        } 
        
        if ( mi instanceof JMenu ) {
            // do nothing 
        } else {
            o = mi.getClientProperty(ActionHandler.class); 
            if (!(o instanceof ActionHandler )) {
                ActionHandler h = new ActionHandler(); 
                mi.addActionListener( h ); 
                mi.putClientProperty(ActionHandler.class, h); 
            } 
        }
    }
    private void uninstallItemDefaults( JMenuItem mi ) {
        Object o = mi.getClientProperty(MenuItemMouseHandler.class); 
        if ( o instanceof MenuItemMouseHandler ) {
            MenuItemMouseHandler h = (MenuItemMouseHandler) o;
            mi.removeMouseListener( h ); 
        } 
        
        o = mi.getClientProperty(ActionHandler.class); 
        if ( o instanceof ActionHandler ) {
            ActionHandler h = (ActionHandler) o;
            mi.removeActionListener( h ); 
        }
    }
    
    private class ContainerHandler implements ContainerListener {
        public void componentAdded(ContainerEvent e) {
            if ( e.getChild() instanceof JMenuItem ) {
                JMenuItem mi = (JMenuItem) e.getChild(); 
                getRoot().installItemDefaults(mi); 
            }
        }

        public void componentRemoved(ContainerEvent e) {
            if ( e.getChild() instanceof JMenuItem ) {
                JMenuItem mi = (JMenuItem) e.getChild(); 
                getRoot().uninstallItemDefaults(mi); 
            }
        }
    }
    
    public static class ActionHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource(); 
            if ( source instanceof JMenu ) {
                //do nothing 
            } else if ( source instanceof JMenuItem) {
                closeParentPopups((JMenuItem) e.getSource());
            }
        }
        
        private void closeParentPopups( Component comp ) {
            if ( comp == null ) return;
            if ( comp instanceof MenuRootElement ) {
                MenuRootElement e = (MenuRootElement) comp;
                e.dispose();
                
            } else if ( comp instanceof JPopupMenu ) {
                JPopupMenu pm = (JPopupMenu)comp;
                Component inv = pm.getInvoker(); 
                if ( inv instanceof JMenu ) {
                    ((JMenu) inv).setPopupMenuVisible( false ); 
                } 
                closeParentPopups( inv );
            } else {
                closeParentPopups(comp.getParent()); 
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
                if ( jm.getMenuComponentCount() > 0 ) { 
                    Rectangle rect = jm.getBounds(); 
                    jm.isPopupMenuVisible(); 
                    jm.getPopupMenu().show(jm, rect.width, 0);
                } 
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
                jm.isPopupMenuVisible();
                jm.getPopupMenu().setVisible(false); 
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
}
