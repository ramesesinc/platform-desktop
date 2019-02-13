/*
 * ColorUtil.java
 *
 * Created on February 22, 2011, 3:00 PM
 * @author jaycverg
 */

package com.rameses.rcp.support;

import java.awt.Color;
import java.util.Properties;


public final class ColorUtil {
    
    private static Properties COLOR_NAMES; 
   
    static 
    {
        COLOR_NAMES = new Properties();
        try { 
            COLOR_NAMES.load(ColorUtil.class.getResourceAsStream("color_names.properties")); 
        } catch(Exception ex) {
            System.out.println("Unable to load 'color_names.properties' file");
        }
    }    
    
    public static Color brighter(Color c, int value) {
        if (value < 0) return c;
        
        float[] hsb = Color.RGBtoHSB(c.getRed(),c.getGreen(),c.getBlue(),new float[3]);
        int h = (int) (hsb[0] * 360);
        int s = (int) (hsb[1] * 100);
        int b = (int) (hsb[2] * 100);
        
        int rm = 0;
        b += value;
        if (b > 100) {
            rm = b - 100;
            b = 100;
        }
        s -= rm;
        if (s < 0) s = 0;
        
        int rgb = Color.HSBtoRGB(h/360.0f, s/100.0f, b/100.0f);
        return new Color(rgb);
    }
    
    public static Color darker(Color c, int value) {
        if (value < 0) return c;
        
        float[] hsb = Color.RGBtoHSB(c.getRed(),c.getGreen(),c.getBlue(),new float[3]);
        int h = (int) (hsb[0] * 360);
        int s = (int) (hsb[1] * 100);
        int b = (int) (hsb[2] * 100);
        
        int rm = 0;
        b -= value;
        if (b < 0) {
            rm = b * (-1);
            b = 0;
        }
        s += rm;
        if (s > 100) s = 100;
        
        int rgb = Color.HSBtoRGB(h/360.0f, s/100.0f, b/100.0f);
        return new Color(rgb);
    }
    
    public static Color decode(String text)
    {
        if (text == null || text.trim().length() == 0) return null;
        
        String s = text.trim().toLowerCase();
        if (s.startsWith("#")) { 
            if (s.length() <= 1) return null;
            else if (s.length() == 7) return Color.decode(s); 
            else if (s.length() == 4) return Color.decode("#" + s.substring(1) + s.substring(1)); 
            else {
                int rem = 7-s.substring(1).length(); 
                StringBuffer sb = new StringBuffer(s); 
                for (int i=0; i<rem; i++) sb.append("0");
                
                return Color.decode(sb.toString()); 
            }
        } 

        try { 
            if (s.startsWith("rgb(") && s.endsWith(")")) {
                String[] values = s.substring(s.indexOf('(')+1, s.lastIndexOf(')')).split(",");
                int r=0, g=0, b=0;
                try { r = Integer.parseInt(values[0].trim()); } catch(Throwable x){;} 
                try { g = Integer.parseInt(values[1].trim()); } catch(Throwable x){;}        
                try { b = Integer.parseInt(values[2].trim()); } catch(Throwable x){;} 
                return new Color(r, g, b); 
            } 
        } catch(Throwable t) {
            System.out.println("[WARN] invalid rgb value: " + s);
            return null; 
        } 
        
        try { 
            return Color.decode(COLOR_NAMES.getProperty(s)); 
        } catch(Throwable x) {
            return null; 
        } 
    }
    

    
}
