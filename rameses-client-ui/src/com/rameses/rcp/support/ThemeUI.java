package com.rameses.rcp.support;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JLabel;


public final class ThemeUI 
{
    
    static {
        
    }
    
    private static JLabel template = new JLabel();
    
    private ThemeUI() {}
    
    public static Font getFont(String key) {
        return template.getFont().deriveFont(Font.PLAIN, 11.0f); 
    }
    
    public static Color getColor(String key) 
    {
        if( "XTextField.focusBackground".equals(key) )
            return new Color(254, 255, 208);
        if( "XTextField.disabledTextColor".equals(key) ) 
            return new Color(0, 0, 0);

        return null;
    }
}
