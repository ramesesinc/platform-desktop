/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test.window;

import com.rameses.rcp.control.layout.YLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ContainerEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.plaf.MenuBarUI;

/**
 *
 * @author wflores
 */
public class VerticalMenuBar extends JMenuBar {
    
    private Color panelBgcolor;
    private MenuItemMouseHandler mouseHandler;
    
    public VerticalMenuBar() {
        super(); 
        initComponent();
    }
    
    private void initComponent() {
        super.setLayout(new YLayout()); 
        super.setLayout(new GridLayout(0, 1));
        setFocusable(true); 
        mouseHandler = new MenuItemMouseHandler();
        panelBgcolor = new JPanel().getBackground();
        setBorder(BorderFactory.createLineBorder(panelBgcolor, 1)); 
    }

    public void setLayout(LayoutManager mgr) {
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor( panelBgcolor ); 
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
    }
    
    public Menu addMenu( String text ) {
        Menu m = new Menu(); 
        m.setText( text ); 
        add( m ); 
        return m; 
    }
    
    public JMenuItem addMenuItem( String text ) {
        JMenuItem mi = new JMenuItem( text ); 
        add( mi ); 
        return mi; 
    } 

    protected void processContainerEvent(ContainerEvent e) {
        super.processContainerEvent(e);
        if ( e.getID() == ContainerEvent.COMPONENT_ADDED ) {
            processComponentAdded( e.getChild() ); 
        } else if ( e.getID() == ContainerEvent.COMPONENT_REMOVED ) {
            processComponentRemoved( e.getChild() ); 
        }
    }    
    
    protected void processComponentAdded( Component comp ) {
        if ( comp instanceof JMenuItem ) {
            JMenuItem mi = (JMenuItem) comp;
            mi.setForeground( Color.BLACK ); 
            mi.addMouseListener( mouseHandler ); 
        }
        
    }
    protected void processComponentRemoved( Component comp ) {
        if ( comp instanceof JMenuItem ) {
            JMenuItem mi = (JMenuItem) comp;
            mi.setForeground( Color.BLACK ); 
            mi.removeMouseListener(mouseHandler); 
        } 
    }
    
    private class MenuItemMouseHandler extends MouseAdapter {

        public void mouseEntered(MouseEvent e) {
            JMenuItem mi = (JMenuItem) e.getSource(); 
            System.out.println(mi.getText() +": armed="+ mi.isArmed());
        }

        public void mouseExited(MouseEvent e) {
            JMenuItem mi = (JMenuItem) e.getSource(); 
            System.out.println(mi.getText() +": armed="+ mi.isArmed());
        }
    }

    public void setUI(MenuBarUI ui) {
        //com.sun.java.swing.plaf.windows.WindowsMenuBarUI
        super.setUI(ui);
    }
}
