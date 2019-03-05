/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test.web;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import javafx.embed.swing.JFXPanel;
import javax.swing.JLabel;

/**
 *
 * @author ramesesinc
 */
public class JFXPanelImpl extends JFXPanel {
 
    private JLabel label = new JLabel(" Connecting   "); 
        
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Dimension dim = label.getPreferredSize(); 
        int dh = dim.height + 5;
        
        Insets margin = getInsets();
        int x = margin.left + 5; 
        int y = getHeight() - margin.bottom - dh -1;
        y = Math.max(y, margin.top); 

        AlphaComposite alpha = createAlphaComposite(0.5f); 
        if ( alpha != null ) {
            Graphics2D g2 = (Graphics2D) g.create(); 
            g2.setComposite(alpha); 
            g2.fillRect(x, y, dim.width, dh);
            g2.dispose();
        }
        
        Graphics2D g2 = (Graphics2D) g.create(); 
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);  
        g2.setColor( Color.WHITE ); 
        g2.drawString( label.getText(), x+2, y+12); 
        g2.dispose();
    }
    
    private AlphaComposite createAlphaComposite(float alpha) {
        try {
            return AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha); 
        } catch (Throwable t) {
            return null; 
        } 
    }    
}
