/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test.window;

import com.rameses.rcp.control.layout.YLayout;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Icon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

/**
 *
 * @author wflores
 */
public class Menubar extends JPanel {
    
    private MouseEventHandler mouseHandler;
    
    public Menubar() {
        super();
        initComponent(); 
    }
    
    private void initComponent() {
        setLayout( new YLayout());
        mouseHandler = new MouseEventHandler(); 
    }

    protected void addImpl(Component comp, Object constraints, int index) { 
        if ( comp instanceof JMenuItem ) {
            JMenuItem mi = (JMenuItem) comp;
            mi.removeMouseListener(mouseHandler); 
            mi.addMouseListener(mouseHandler); 
        }
        super.addImpl(comp, constraints, index);        
    }
    
    private class MouseEventHandler extends MouseAdapter {
        
        Menubar root = Menubar.this;
        
        Menu selMenu;
        JMenuItem selMenuItem;
        
        public void mouseEntered(MouseEvent e) {
            Object source = e.getSource(); 
            if ( source instanceof JMenuItem ) {
                System.out.println("old -> " + selMenuItem);
                System.out.println("new -> " + source);
                
                selMenuItem = (JMenuItem) source; 
                selMenuItem.setSelected(true); 
                
                if ( source instanceof Menu ) {
                    selMenu = (Menu) source; 
                    selMenu.showPopup(); 
                    
//                    ButtonGroup group = new ButtonGroup();
//                    if ( selMenu.getPopupMenu().getComponentCount() > 0 ) {
//                        Component[] comps = selMenu.getPopupMenu().getComponents(); 
//                        for (int i=0; i<comps.length; i++) {
//                            if ( comps[i] instanceof JMenuItem ) {
//                                JMenuItem jmi = (JMenuItem) comps[i]; 
//                                jmi.removeMouseListener(this);
//                                jmi.addMouseListener(this); 
//                            }
//                        }
//                    }
                    
                }
            }
        }

        public void mouseExited(MouseEvent e) {
            Object source = e.getSource(); 
            if ( source instanceof JMenuItem ) {
                JMenuItem mi = (JMenuItem) source; 
                System.out.println("exited -> "+ mi);
                mi.setSelected(false); 
                
                if ( source instanceof JMenu ) {
                    JMenu jm = (JMenu) source; 
                    if ( jm.isPopupMenuVisible()) {
                        jm.getPopupMenu().setVisible( false ); 
                    }
                }
            }
        }
    }
    
    
    private class MenuProxy extends JMenu {
        
        private JMenu source; 
        
        MenuProxy( JMenu source ) {
            super(); 
            this.source = source; 
        }

        public String getText() { 
            return source.getText(); 
        }

        public Icon getIcon() {
            return source.getIcon(); 
        }
        
    }
}
