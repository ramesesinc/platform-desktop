/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test.window;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;

/**
 *
 * @author wflores
 */
public final class WindowMenu extends JWindow { 
    
    public static synchronized void show( Component invoker, Component view ) { 
        if ( view == null ) {
            //do nothing, exit immediately...
            return; 
        }
        
        WindowMenu wm = null; 
        Component root = SwingUtilities.getRoot( invoker ); 
        if ( root instanceof Window ) { 
            wm = new WindowMenu((Window) root); 
        } else { 
            wm = new WindowMenu( null ); 
        } 
        
        wm.setView( view ); 
        wm.pack();
        
        Insets margin = Toolkit.getDefaultToolkit().getScreenInsets(wm.getGraphicsConfiguration());
        Dimension scrdim = Toolkit.getDefaultToolkit().getScreenSize();
        Rectangle scrRect = new Rectangle(0, 0, scrdim.width, scrdim.height); 
        
        int x = 0, y = 0;
        if ( invoker != null ) {
            Point p = invoker.getLocationOnScreen(); 
            Rectangle rect = invoker.getBounds(); 
            y = p.y + rect.height; 
            x = p.x;  
            
            Rectangle r = wm.getExtendedScreenBounds( p ); 
            if ( r != null ) {
                margin = new Insets(0,0,0,0); 
                scrdim = new Dimension(r.width, r.height); 
                scrRect = r; 
            }
        }
        
        Dimension windim = wm.getPreferredSize();
        int scrMaxX = (scrRect.x+scrdim.width) - margin.right; 
        int scrMaxY = (scrRect.y+scrdim.height) - margin.bottom; 
        int lx = x + windim.width; 
        int ly = y + windim.height; 
        if ( lx > scrMaxX ) { 
            x = Math.max(x - (lx - scrMaxX), margin.left);
        } 
        if ( ly > scrMaxY ) {
            y = Math.max(y - (ly - scrMaxY), margin.top);
        }
        
        x = Math.max(x, margin.left); 
        wm.setLocation( x, y ); 
        wm.setVisible(true);
    }

    
    
    
    
    private JPanel contentpane;
    private AWTEventHandler eventHandler;
    
    private WindowMenu( Window win ) { 
        super( win );
        initComponent(); 
    }
    
    private void initComponent() {
        contentpane = new JPanel();
        contentpane.setLayout(new BorderLayout()); 
        contentpane.setBorder(BorderFactory.createLineBorder(new Color(150,150,150), 1)); 
        setContentPane( contentpane ); 
        
        eventHandler = new AWTEventHandler(); 
        addWindowListener(new WindowEventHandler()); 
    }
    
    void setView( Component view ) {
        contentpane.removeAll(); 
        contentpane.add(view, BorderLayout.NORTH); 
    }
    
    private Rectangle getExtendedScreenBounds( Point p ) {
        if ( p == null ) return null; 
        
        Rectangle screenRect = null; 
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment(); 
        GraphicsDevice gdmain = ge.getDefaultScreenDevice(); 
        GraphicsDevice[] gds = ge.getScreenDevices(); 
        for ( GraphicsDevice gd : gds ) { 
            if ( screenRect != null ) break;
            if ( gdmain.equals(gd)) continue; 
            
            GraphicsConfiguration[] gcs = gd.getConfigurations();
            for ( GraphicsConfiguration gc : gcs ) {
                Rectangle rect = gc.getBounds(); 
                if ( rect.contains(p)) {
                    screenRect = rect; 
                    break; 
                }
            }
        }
        return screenRect; 
    }
    
    private class AWTEventHandler implements AWTEventListener {
        
        WindowMenu root = WindowMenu.this; 
        
        public void eventDispatched(AWTEvent e) {
            if ( e instanceof MouseEvent ) {
                processMouseEvent((MouseEvent) e);
            } else if ( e instanceof FocusEvent ) {
                processFocusEvent((FocusEvent) e); 
            }
        }
        
        private void processMouseEvent( MouseEvent me ) {
            if ( me.getID() == MouseEvent.MOUSE_PRESSED ) {
                Object source = me.getSource(); 
                if ( source instanceof Component ) {
                    WindowMenu wm = findTop((Component) source); 
                    if ( wm != null && wm.equals(root)) {
                        //do nothing 
                    } else {
                        root.dispose(); 
                    }
                }
            }
        }
        
        private void processFocusEvent( FocusEvent e ) {
            if ( e.getID() == FocusEvent.FOCUS_LOST ) {
                if ( e.isTemporary()) { 
                    WindowMenu wm = findTop(e.getOppositeComponent()); 
                    if ( wm != null && wm.equals(root)) {
                        //do nothing 
                    } else {
                        root.dispose();
                    }
                }
            }
        }
        
        private WindowMenu findTop( Component comp ) { 
            if ( comp == null ) return null;
            
            if ( comp instanceof WindowMenu ) {
                return (WindowMenu) comp; 
            } else {
                return findTop( comp.getParent() ); 
            }
        }
        
        private void dumpTree( Component comp, int level ) {
            if ( comp == null ) return; 
            if ( level <= 0 ) level = 1; 
            
            StringBuilder sb = new StringBuilder("-> "); 
            for (int i=1; i<level; i++) {
                sb.append("   "); 
            }
            sb.append(comp.getClass().getName());
            System.out.println( sb );
            dumpTree(comp.getParent(), level+1);
        }
    }
    
    private class WindowEventHandler extends WindowAdapter { 
        
        WindowMenu root = WindowMenu.this; 
        
        public void windowOpened(WindowEvent e) { 
            Toolkit.getDefaultToolkit().addAWTEventListener( root.eventHandler, AWTEvent.MOUSE_EVENT_MASK | AWTEvent.FOCUS_EVENT_MASK ); 
        }

        public void windowClosing(WindowEvent e) {
        }

        public void windowClosed(WindowEvent e) { 
            Toolkit.getDefaultToolkit().removeAWTEventListener( root.eventHandler ); 
        } 
    } 
}
