/*
 * IconedTextField.java
 *
 * Created on May 9, 2013, 5:15 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control.text;

import com.rameses.rcp.common.MsgBox;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.util.ValueUtil;
import java.awt.AlphaComposite;
import java.awt.Cursor; 
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.Beans;
import java.net.URL;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.border.Border;

/**
 *
 * @author wflores
 */
public class IconedTextField extends DefaultTextField implements ActionListener 
{
    public static final String ICON_ON_LEFT = "LEFT";
    public static final String ICON_ON_RIGHT = "RIGHT";
    
    private static final int XPAD = 4;
    private static final int MARGIN_PAD = 5;
    
    private Icon icon;
    private int imgWidth = 0;
    private int imgHeight = 0;
    private String orientation = ICON_ON_RIGHT;
    private boolean mouseOverImage = false;
    
    public IconedTextField() {
        this(null); 
    }
    
    public IconedTextField(String icon) 
    {
        super();
        FieldSupport support = new FieldSupport();
        addMouseMotionListener(support);
        addMouseListener(support); 
        addActionListener(this); 
        setIcon(icon);         
    }    
    
    protected boolean isEnabledEnterActionSupport() { 
        return false; 
    } 
    
    public void paint(Graphics g) 
    {
        try { paintImpl(g); } catch(Exception e) {;} 
    }
    
    private void paintImpl(Graphics g) 
    {
        super.paint(g);
        
        if (icon != null) 
        {
            Graphics2D g2 = (Graphics2D) g.create();
            if ( isReadonly() || !isEnabled() )
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.5f));
            else if ( !isFocusable() && !Beans.isDesignTime() ) 
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.3f));
            else if( mouseOverImage == true )
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.5f));
            
            if (imgWidth > 0) 
            {
                int x=0 ,y=0;
                if (orientation.toUpperCase() == "RIGHT") 
                {
                    x = this.getWidth() - (imgWidth + XPAD);
                    y = (this.getHeight() - imgHeight) / 2;
                }
                else 
                {
                    x = XPAD;
                    y = (this.getHeight() - imgHeight) / 2;
                }
                
                icon.paintIcon(this, g2, x, y);
            }
            g2.dispose();
        }        
    }
    
    public void setBorder(Border border) 
    {
        Border inner = null;
        if ( ICON_ON_LEFT.equals( orientation ) ) 
            inner = BorderFactory.createEmptyBorder(0, imgWidth + MARGIN_PAD, 0, 0);
        else 
            inner = BorderFactory.createEmptyBorder(0, 0, 0, imgWidth + MARGIN_PAD);
        
        Border newBorder = null;
        if ( border != null )
            newBorder = BorderFactory.createCompoundBorder(border, inner);
        else
            newBorder = inner;
        
        super.setBorder(newBorder);
    }
    
    public String getOrientation() { return orientation; }    
    public void setOrientation(String orient) 
    {
        if ( orient != null ) orient = orient.toUpperCase();
        
        this.orientation = orient;
        Insets insets = super.getMargin();
        Insets actualInsets = null;
        if ( ICON_ON_LEFT.equals( orientation ) ) 
        {
            actualInsets = new Insets(insets.top,  imgWidth + MARGIN_PAD, insets.bottom, 0);
            super.setMargin(actualInsets);
        } 
        else 
        {
            actualInsets = new Insets(insets.top,  insets.right, insets.bottom, imgWidth + MARGIN_PAD);
            super.setMargin(actualInsets);
        }
    }    
    
    public Icon getIcon() { return icon; }    
    public void setIcon(Icon icon) 
    {
        this.icon = icon;
        if ( icon != null ) 
        {
            imgWidth = icon.getIconWidth();
            imgHeight = icon.getIconHeight();
        }
    }
    
    public void setIcon(String path) 
    {
        if ( ValueUtil.isEmpty(path) ) 
            setIcon( (ImageIcon) null );
        
        else 
        {
            try
            {
                ClassLoader loader = null;
                if ( Beans.isDesignTime() ) 
                    loader = getClass().getClassLoader();
                else 
                    loader = ClientContext.getCurrentContext().getClassLoader();
                
                URL url = loader.getResource(path);                
                setIcon( new ImageIcon(url) );
            }
            catch (Exception ex) 
            {
                if( ClientContext.getCurrentContext().isDebugMode() ) {
                    ex.printStackTrace();
                }
            }
        }
    }    
    
    
    protected void onactionPerformed(ActionEvent e) {}    
    public final void actionPerformed(ActionEvent e) 
    {
        try 
        {
            if (!isFocusable() || !isVisible() || !isEnabled()) return;
        
            onactionPerformed(e); 
        } 
        catch(Exception ex) { 
            MsgBox.err(ex); 
        }
    }
    
    // <editor-fold defaultstate="collapsed" desc="  FieldSupport (class)  ">
    
    private class FieldSupport implements MouseListener, MouseMotionListener 
    {        
        public void mousePressed(MouseEvent e) {}
        public void mouseReleased(MouseEvent e) {}
        public void mouseEntered(MouseEvent e) {}
        public void mouseDragged(MouseEvent e) {}
        
        public void mouseClicked(MouseEvent e) 
        {
            if (orientation.toUpperCase() == "RIGHT") 
            {
                if (e.getX() >= (IconedTextField.this.getWidth() - (imgWidth + XPAD))) 
                {
                    EventQueue.invokeLater(new Runnable() {
                        public void run() {
                            actionPerformed(null);
                        }
                    });
                }
            } 
            else 
            {
                if (e.getX() > 0 && e.getX() < (XPAD + imgWidth)) 
                {
                    EventQueue.invokeLater(new Runnable() {
                        public void run() {
                            actionPerformed(null);
                        }
                    });
                }
            }
        }
        
        public void mouseExited(MouseEvent e) 
        {
            mouseOverImage = false;
            IconedTextField.this.repaint();
        }
        
        public void mouseMoved(MouseEvent e) 
        {
            if (orientation.toUpperCase() == "RIGHT") 
            {
                if (e.getX() >= (IconedTextField.this.getWidth() - (imgWidth + XPAD))) 
                {
                    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    mouseOverImage = true;
                    IconedTextField.this.repaint();
                } 
                else 
                {
                    setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
                    mouseOverImage = false;
                    IconedTextField.this.repaint();
                }
            } 
            else 
            {
                if (e.getX() > 0 && e.getX() < (XPAD + imgWidth)) 
                {
                    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    mouseOverImage = true;
                    IconedTextField.this.repaint();
                } 
                else 
                {
                    setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
                    mouseOverImage = false;
                    IconedTextField.this.repaint();
                }
            }
        }        
    }
    
    // </editor-fold>    
}
