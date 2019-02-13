/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test.window;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

/**
 *
 * @author wflores
 */
public class Menu extends JMenu {

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

    protected Point getPopupMenuOrigin() {
        Point p = super.getPopupMenuOrigin();
        Container comp = getParent();
        if ( comp instanceof JMenuBar ) {
            Rectangle rect = getBounds(); 
            p.x += rect.width; 
            p.y -= rect.height;
        }         
        return p; 
    }

    @Override
    protected void fireActionPerformed(ActionEvent event) {
        System.out.println("fireActionPerformed");
        super.fireActionPerformed(event);
    }

    @Override
    protected void fireItemStateChanged(ItemEvent e) {
        System.out.println("fireItemStateChanged " + e);
        super.fireItemStateChanged(e);
    }

    @Override
    protected void fireMenuDeselected() {
        System.out.println("fireMenuDeselected");
        super.fireMenuDeselected();
    }

    @Override
    protected void fireMenuSelected() {
        System.out.println("fireMenuSelected");
        super.fireMenuSelected();
    }
    
    
    
    
    
    
    public void showPopup() { 
        if ( getMenuComponentCount() > 0 ) {
            isPopupMenuVisible();
            Rectangle rect = getBounds();
            getPopupMenu().show(this, rect.width, 0 ); 
        }
    }
    
    public void hidePopup() {
        isPopupMenuVisible(); 
        getPopupMenu().setVisible(false); 
    }

    public Point getCustomPopupMenuOrigin() { 
        Insets margin = Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration());
        Dimension scrdim = Toolkit.getDefaultToolkit().getScreenSize();
        Rectangle scrRect = new Rectangle(0, 0, scrdim.width, scrdim.height); 
        
        Point p = getLocationOnScreen(); 
        Rectangle rect = getBounds(); 
        int y = (p.y + rect.height); 
        int x = p.x;  

        Rectangle r = getExtendedScreenBounds( p ); 
        if ( r != null ) {
            margin = new Insets(0,0,0,0); 
            scrdim = new Dimension(r.width, r.height); 
            scrRect = r; 
        }
        
        Dimension windim = getPopupMenu().getPreferredSize();
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
        return new Point( x, y); 
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
    
}
