package com.rameses.rcp.support;

import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.text.JTextComponent;

public class TextEditorSupport 
{    
    public static TextEditorSupport install(JTextComponent component) 
    {
        TextEditorSupport s = new TextEditorSupport(component);
        component.putClientProperty(TextEditorSupport.class, s);
        return s;
    }
    
    
    
    private JTextComponent component;
    
    private TextEditorSupport(JTextComponent component) 
    {
        this.component = component;
        component.setMargin(new Insets(1,5,2,4)); 
        component.addFocusListener(new SupportFocusListener()); 
        component.putClientProperty("TextField.focusBackground", ThemeUI.getColor("XTextField.focusBackground"));
    }
    
    private class SupportFocusListener implements FocusListener 
    {
        public void focusGained(FocusEvent focusEvent) {
            component.selectAll();
        }
        
        public void focusLost(FocusEvent focusEvent) {
        }
    }
}
