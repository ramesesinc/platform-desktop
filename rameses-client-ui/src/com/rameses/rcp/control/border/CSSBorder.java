/*
 * CSSBorder.java
 *
 * Created on April 27, 2013, 2:10 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control.border;

import com.rameses.rcp.support.ColorUtil;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.swing.border.AbstractBorder;

public class CSSBorder extends AbstractBorder
{
 
    public static synchronized CSSBorder parse(String text) 
    {
        CSSBorder border = new CSSBorder(); 
        border.parseImpl(text); 
        return border; 
    }
    
    private List<Painter> painters = new ArrayList<Painter>();    
    private Insets margin = new Insets(0,0,0,0);
    private Insets painterMargin = new Insets(0, 0, 0, 0);
    
    public CSSBorder() {}
        
    public Insets getBorderInsets(Component c) {
        return getBorderInsets(c, new Insets(0,0,0,0));
    }

    public Insets getBorderInsets(Component c, Insets insets) 
    {
        Insets pm = new Insets(0,0,0,0);
        for (Painter p : painters) { 
            Insets ins = p.getBorderInsets(); 
            if (ins.top >= 0) pm.top = ins.top;
            if (ins.left >= 0) pm.left = ins.left;
            if (ins.bottom >= 0) pm.bottom = ins.bottom;
            if (ins.right >= 0) pm.right = ins.right;
        } 
        
        insets.top = pm.top;
        insets.left = pm.left;
        insets.bottom = pm.bottom;
        insets.right = pm.right;
        painterMargin = pm; 
        return insets;
    }     
    
    public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) 
    {
        //System.out.println(painterMargin);
        for (Painter painter : painters) {
            painter.paintBorder(c, g, x, y, w, h, painterMargin);
        }
    }    
    
    private Insets copy(Insets padding) {
        if (padding == null) return new Insets(0, 0, 0, 0);

        return new Insets(padding.top, padding.left, padding.bottom, padding.right); 
    }    
    
    private void parseImpl(String text) 
    {
        if (text == null) return;
        /*  
         *  border:none;
         *  border:1px solid #fff; 
         *  border-top:1px solid blue;
         *  border-right:1px solid blue;
         *  border-bottom:1px solid blue;
         *  border-right:1px solid blue;
         */ 
        Scanner scanner = new Scanner(text);
        scanner.useDelimiter(";"); 
        while (scanner.hasNext()) 
        {
            String s = scanner.next().toLowerCase().trim();
            if (!s.startsWith("border")) continue;
            
            int idx = s.indexOf(':'); 
            if (idx <= 0) continue;
            
            painters.add(new Painter(s));
        }
        
    }
 
    
    
    private class Painter
    {
        private String name;
        private int thickness;
        private String style;
        private Color color;
        
        public Painter(String text) 
        {
            int idx = text.indexOf(':'); 
            this.name = text.substring(0, idx);
            
            String[] params = text.substring(idx+1).split(" "); 
            if ("none".equals(params[0]))
                this.name = "border-none";
            
            if (params.length > 0) this.thickness = toInt(params[0]);
            if (params.length > 1) this.style = params[1].trim();
            if (params.length > 2) this.color = toColor(params[2].trim()); 
            
            if (this.color == null) this.color = Color.BLACK; 
        }
        
        private Insets getBorderInsets() {
            Insets ins = new Insets(-1,-1,-1,-1);
            if (this.name.equals("border")) 
                ins.top = ins.left = ins.bottom = ins.right = this.thickness;
            else if (this.name.equals("border-top")) 
                ins.top = this.thickness;
            else if (this.name.equals("border-left")) 
                ins.left = this.thickness;
            else if (this.name.equals("border-bottom")) 
                ins.bottom = this.thickness;
            else if (this.name.equals("border-right")) 
                ins.right = this.thickness;
            else if (this.name.equals("border-none")) 
                ins = new Insets(0,0,0,0);
            
            return ins; 
        }
                
        private void paintBorder(Component c, Graphics g, int x, int y, int w, int h, Insets pm) 
        {
            Color oldColor = g.getColor();
            if (this.name.equals("border-none") || this.color == null)
            {
                g.setColor(oldColor);
                g.drawRect(0, 0, w, h);
            }
            else if (this.name.equals("border"))
            {
                g.setColor(oldColor);
                g.drawRect(0, 0, w, h);                
                g.setColor(this.color);
                for (int i=0; i<this.thickness; i++)
                {
                    g.drawLine(i+1, i+1,   w-i, i+1);
                    g.drawLine(i+1, i+1,   i+1, h-i-1);
                    g.drawLine(w-i, i+1,   w-i, h-i-1);
                    g.drawLine(i+1, h-i-1, w-i, h-i-1);
                }
            }
            else if (this.name.equals("border-top"))
            {
                g.setColor(oldColor);
                for (int i=0; i<pm.top; i++) {
                    g.drawLine(0, i, w, i);
                } 
                
                g.setColor(this.color);
                for (int i=0; i<this.thickness; i++) {
                    g.drawLine(0, i, w, i);
                }
            }   
            else if (this.name.equals("border-left"))
            {
                g.setColor(oldColor);
                for (int i=0; i<pm.left; i++) {
                    g.drawLine(i, 0, i, h);
                }
                
                g.setColor(this.color);
                for (int i=0; i<this.thickness; i++) {
                    g.drawLine(i, 0, i, h);
                }
            }  
            else if (this.name.equals("border-bottom"))
            {
                g.setColor(oldColor);
                for (int i=0; i<pm.bottom; i++) {
                    g.drawLine(0, h-i-1, w, h-i-1);
                }
                
                g.setColor(this.color);
                for (int i=0; i<this.thickness; i++) {
                    g.drawLine(0, h-i-1, w, h-i-1);
                }
            }   
            else if (this.name.equals("border-right"))
            {
                g.setColor(oldColor);
                for (int i=0; i<pm.right; i++) {
                    g.drawLine(w-i, 0, w-i, h);
                }  
                
                g.setColor(this.color);
                for (int i=0; i<this.thickness; i++) {
                    g.drawLine(w-i, 0, w-i, h);
                }
            }             
            g.setColor(oldColor);
        }
        
        private int toInt(String value) 
        {
            try {
                return Integer.parseInt(value.replaceAll("px","").replaceAll("pt","").trim());
            } catch(Exception ex) {
                return 0;
            }
        }
        
        private Color toColor(String value) { 
            return ColorUtil.decode(value); 
        } 
    }    
}
