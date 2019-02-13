/*
 * FontSupport.java
 *
 * Created on August 8, 2013, 12:55 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.support;

import java.awt.Font;
import java.awt.font.TextAttribute;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.JComponent;

/**
 *
 * @author wflores
 */
public class FontSupport 
{
    // <editor-fold defaultstate="collapsed" desc=" static support ">
    
    private static FontSupport instance = null;
    
    public static FontSupport getInstance() {
        if (instance == null) 
            instance = new FontSupport(); 
        
        return instance; 
    }
    
    // </editor-fold>
        
    public Map createFontAttributes(String styles) {
        Map attrs = new HashMap();   
        if (styles == null || styles.length() == 0) 
            return attrs; 
        
        String[] values = styles.trim().split(";");
        for (String str: values) { 
            int idx = str.indexOf(':');
            if (idx <= 0) continue;
            
            String key = str.substring(0, idx).trim(); 
            if (key.length() == 0) continue;
            
            String val = str.substring(idx+1).trim();
            if (val.length() == 0) continue;
            
            addAttribute(attrs, key, val); 
        } 
        return attrs;
    }
    
    public Font applyStyles(Font source, Map attrs) { 
        if ( attrs == null || attrs.isEmpty()) return source;
        
        Font newfont = source.deriveFont( attrs ); 
        
        try {
            Number num = Math.max( 100, Integer.parseInt(attrs.get("font-scale").toString()));
            DecimalFormat numformat = new DecimalFormat("0.00"); 
            double scale = numformat.parse( numformat.format((num.doubleValue()/100.0))).doubleValue(); 

            int fsize = newfont.getSize();
            numformat = new DecimalFormat("0"); 
            num = ((Number) fsize).doubleValue() * scale;
            fsize = numformat.parse( numformat.format( num )).intValue(); 
            newfont = newfont.deriveFont((float) fsize); 
        } catch(Throwable t){;} 
        
        return newfont; 
    }
    
    public void applyStyles(JComponent component, Map styles) {
        if (component == null || styles == null || styles.isEmpty()) return;
        
        Map attrs = new HashMap();         
        Font oldFont = component.getFont();        
        Iterator entries = styles.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry me = (Map.Entry) entries.next(); 
            String sname = (me.getKey() == null? "": me.getKey().toString()); 
            Object oval = me.getValue();
            if (oval == null || oval.toString().length() == 0) continue;
            if (oval instanceof Font) 
                component.setFont((Font) oval); 
            else 
                addAttribute(attrs, sname, oval.toString());
        } 
        if (!attrs.isEmpty()) component.setFont(oldFont.deriveFont(attrs)); 
    }
    
    public void applyStyles(JComponent component, String styles) {
        if (component == null || styles == null || styles.trim().length() == 0) {
            return; 
        }

        Font oldFont = component.getFont(); 
        Map attrs = createFontAttributes(styles); 
        applyStyles(oldFont, attrs); 
    } 
    
    private void addAttribute(Map attrs, String key, String val) {
        if (key == null || key.length() == 0) return;
        if (val == null || val.length() == 0) return;
        
        if ("font".equals(key)) { 
            String[] fontArrays = val.split("-");
            String fontName = (fontArrays.length >= 1? fontArrays[0].trim(): null);
            String fontSize = (fontArrays.length >= 2? fontArrays[1].trim(): null);
            String fontStyle = (fontArrays.length >=3? fontArrays[2].trim(): null); 
            if (fontName != null && fontName.length() > 0) 
                attrs.put(TextAttribute.FAMILY, val); 
            if (fontStyle != null && fontStyle.length() > 0) 
                addFontStyle(attrs, fontStyle); 

            try { 
                attrs.put(TextAttribute.SIZE, Float.parseFloat(fontSize)); 
            } catch(Throwable t) {;} 
        }
        else if ("font-family".equals(key)) {
            attrs.put(TextAttribute.FAMILY, val);
        } 
        else if ("font-style".equals(key)) {
            addFontStyle(attrs, val); 
        } 
        else if ("font-weight".equals(key)) {
            addFontStyle(attrs, val); 
        } 
        else if ("font-size".equals(key)) {
            try { 
                float size = Float.parseFloat(val); 
                attrs.put(TextAttribute.SIZE, size);
            } catch(Throwable t) {;} 
        } 
        else if ("text-decoration".equals(key)) {
            if ("underline".equalsIgnoreCase(val)) 
                attrs.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
            else if ("underline-dashed".equalsIgnoreCase(val)) 
                attrs.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_LOW_DASHED);
            else if ("underline-dotted".equalsIgnoreCase(val)) 
                attrs.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_LOW_DOTTED);
            else if ("underline-gray".equalsIgnoreCase(val)) 
                attrs.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_LOW_GRAY);
            else if ("underline-one-pixel".equalsIgnoreCase(val)) 
                attrs.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_LOW_ONE_PIXEL);
            else if ("underline-two-pixel".equalsIgnoreCase(val)) 
                attrs.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_LOW_TWO_PIXEL);
            else if ("strikethrough".equalsIgnoreCase(val)) 
                attrs.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
            else if ("superscript".equalsIgnoreCase(val)) 
                attrs.put(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUPER);
            else if ("subscript".equalsIgnoreCase(val)) 
                attrs.put(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUB);
        }       
        else {
            attrs.put(key, val); 
        }
    }
    
    private void addFontStyle(Map attrs, String val) {
        if (val == null || val.length() == 0) return; 
        
        if (val.toLowerCase().matches("normal|regular")) 
            attrs.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_REGULAR);
        else if ("italic".equalsIgnoreCase(val))
            attrs.put(TextAttribute.POSTURE, TextAttribute.POSTURE_REGULAR);
        else if ("oblique".equalsIgnoreCase(val))
            attrs.put(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE);
        
        else if ("bold".equalsIgnoreCase(val)) 
            attrs.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
        else if ("demibold".equalsIgnoreCase(val))
            attrs.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_DEMIBOLD);
        else if ("demilight".equalsIgnoreCase(val))
            attrs.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_DEMILIGHT);
        else if (val.toLowerCase().matches("extrabold|bolder"))
            attrs.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_EXTRABOLD);
        else if ("extralight".equalsIgnoreCase(val))
            attrs.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_EXTRA_LIGHT);
        else if ("heavy".equalsIgnoreCase(val))
            attrs.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_HEAVY);
        else if ("light".equalsIgnoreCase(val))
            attrs.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_LIGHT);
        else if ("medium".equalsIgnoreCase(val))
            attrs.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_MEDIUM);
        else if ("semibold".equalsIgnoreCase(val))
            attrs.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_SEMIBOLD);
        else if ("ultrabold".equalsIgnoreCase(val))
            attrs.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_ULTRABOLD);
        else if ("ultrabold".equalsIgnoreCase(val))
            attrs.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_ULTRABOLD);

        else {  
            try { 
                attrs.put(TextAttribute.WEIGHT, Float.parseFloat(val));
            } catch(Throwable t) {;} 
        }        
    }
}
