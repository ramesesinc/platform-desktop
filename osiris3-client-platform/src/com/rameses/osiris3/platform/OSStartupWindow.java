/*
 * OSStartupWindow.java
 *
 * Created on October 29, 2013, 2:08 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.platform;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

/**
 *
 * @author wflores
 */
class OSStartupWindow extends JFrame 
{
    private ImageCanvas imageCanvas;
    private ImageIcon iicon; 
        
    private Color lineColor;
    private Color textColor;
    private Rectangle textPosition;
    private Rectangle progressPosition;
    
    public OSStartupWindow() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);         
        setUndecorated(true); 
        setAlwaysOnTop(true);
        
        imageCanvas = new ImageCanvas(); 
        imageCanvas.lblText.setForeground(textColor = new Color(0,0,0));
        
        Container container = getContentPane(); 
        container.setLayout(new BorderLayout());
        container.add(imageCanvas); 
        setSize(480, 300); 
    } 
    
    public void setImageIcon(ImageIcon iicon) {
        this.iicon = iicon; 
    } 
    
    public void setTextColor(Color textColor) {
        this.textColor = textColor;
        imageCanvas.lblText.setForeground(textColor);
    }
    
    public void setTextPosition(Rectangle textPosition) {
        this.textPosition = textPosition; 
        imageCanvas.lblText.setBounds(textPosition); 
    }
    
    public void setProgressPosition(Rectangle progressPosition) {
        this.progressPosition = progressPosition; 
        imageCanvas.progressbar.setBounds(progressPosition); 
    }    
        
    public void setTextValue(String value) {
        imageCanvas.lblText.setText(value); 
        imageCanvas.lblText.revalidate();
        imageCanvas.lblText.repaint(); 
    }
    
    public void center() {
        Dimension scrdim = Toolkit.getDefaultToolkit().getScreenSize(); 
        Insets margin = Toolkit.getDefaultToolkit().getScreenInsets(imageCanvas.getGraphicsConfiguration()); 
        scrdim.width -= (margin.left + margin.right); 
        scrdim.height -= (margin.top + margin.bottom); 
        
        Dimension windim = getSize();
        int x = Math.max((scrdim.width/2)-(windim.width/2), 0); 
        int y = Math.max((scrdim.height/2)-(windim.height/2), 0);
        setLocation(x, y); 
    }
    
    public void setVisible(boolean b) {
        beforeVisible();
        super.setVisible(true); 
    }
    
    private void beforeVisible() {
        OSPlatformIdentity spi = OSPlatformIdentity.getInstance(); 
        ImageIcon splashIcon = spi.getIcon("splash");
        ImageIcon winIcon = spi.getIcon("icon");
        if (winIcon == null) winIcon = spi.getDefaultIcon();
        
        try { setImageIcon(splashIcon); } catch(Throwable t) {;} 
        try { setIconImage(winIcon.getImage()); } catch(Throwable t) {;}         
        
        center();
    }
    
    // <editor-fold defaultstate="collapsed" desc=" ImageCanvas (class) ">
    
    private class ImageCanvas extends JPanel 
    { 
        OSStartupWindow root = OSStartupWindow.this;
        
        JProgressBar progressbar; 
        JLabel lblText; 
        
        ImageCanvas() {
            setLayout(null); 
            lblText = new JLabel("Downloading updates please wait...");
            lblText.setFont(Font.decode("dialog-plain-10"));
            Dimension dim = lblText.getPreferredSize();
            //lblText.setBounds(new Rectangle(20, 241, 436, dim.height));
            lblText.setBounds(new Rectangle(20, 230, 440, dim.height));
            add(lblText);          
            
            progressbar = new JProgressBar(); 
            progressbar.setIndeterminate(true); 
            //progressbar.setBounds(new Rectangle(20, 235, 440, 8)); 
            progressbar.setBounds(new Rectangle(20, 245, 440, 10)); 
            add(progressbar);            
        }
        
        public void paintComponent(Graphics g) {
            super.paintComponent(g); 
            if (root.iicon == null) return;
            
            Graphics2D g2 = (Graphics2D) g.create(); 
            g2.drawImage(iicon.getImage(), 0, 0, getWidth(), getHeight(), null); 
            g2.dispose(); 
        }
        
        public void setText(String text) {
            lblText.setText(text); 
            lblText.revalidate();
            lblText.repaint(); 
        }
    }
    
    // </editor-fold>
}
