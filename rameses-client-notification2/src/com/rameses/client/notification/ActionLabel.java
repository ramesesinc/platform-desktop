/*
 * ActionLabel.java
 *
 * Created on January 28, 2014, 8:59 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.client.notification;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

/**
 *
 * @author wflores
 */
public class ActionLabel extends JLabel 
{
    private final static int MOUSE_OUT  = 1;
    private final static int MOUSE_OVER = 2;
   
    private int mouse_status = MOUSE_OUT;
    private Color MAIN_COLOR = Color.BLUE;
    
    public ActionLabel() {
        super("ActionLabel"); 
        setForeground(MAIN_COLOR.darker());
        setCursor(new Cursor(Cursor.HAND_CURSOR)); 
        MouseListenerImpl mouseL = new MouseListenerImpl();
        addMouseListener(mouseL);
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g); 
        
        if (mouse_status == MOUSE_OVER) { 
            Insets insets = getInsets(); 
            int left = insets.left; 
            if (getIcon() != null) { 
                left += getIcon().getIconWidth() + getIconTextGap();
            } 

            int x1 = left;
            int y1 = getHeight()-1;
            int x2 = (int) getPreferredSize().getWidth() - insets.right; 
            int y2 = getHeight() - 1 - insets.bottom;
            g.drawLine(x1, y1, x2, y2); 
        } 
    }
    
    protected void onClick() {
    }
    
    protected void onDoubleClick() {
    }    
    
    private Color brighter(Color color, float factor) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();

        int i = (int)(1.0/(1.0-factor));
        if ( r == 0 && g == 0 && b == 0) {
           return new Color(i, i, i);
        }
        if ( r > 0 && r < i ) r = i;
        if ( g > 0 && g < i ) g = i;
        if ( b > 0 && b < i ) b = i;

        return new Color(Math.min((int)(r/factor), 255),
                         Math.min((int)(g/factor), 255),
                         Math.min((int)(b/factor), 255));
    }
    
    
    private class MouseListenerImpl implements MouseListener 
    {
        ActionLabel root = ActionLabel.this; 
        
        public void mouseClicked(MouseEvent e) {
            if (!SwingUtilities.isLeftMouseButton(e)) return;
            if (e.getClickCount() == 2) {
                root.onDoubleClick(); 
            } else {
                root.onClick();
            }
        }

        public void mousePressed(MouseEvent e) {
        }

        public void mouseReleased(MouseEvent e) {
        }

        public void mouseEntered(MouseEvent e) {
            root.setForeground(root.brighter(root.MAIN_COLOR, 0.8f)); 
            root.mouse_status = MOUSE_OVER;
            root.repaint();
        }

        public void mouseExited(MouseEvent e) {
            root.setForeground(root.MAIN_COLOR.darker());
            root.mouse_status = MOUSE_OUT;
            root.repaint();
        }
    }
}
