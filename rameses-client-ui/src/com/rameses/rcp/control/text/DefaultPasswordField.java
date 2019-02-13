/*
 * DefaultPasswordField.java
 *
 * Created on October 1, 2013, 1:59 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control.text;

import com.rameses.rcp.support.FontSupport;
import com.rameses.rcp.support.ThemeUI;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.beans.Beans;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JPasswordField;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

/**
 *
 * @author wflores
 */
public class DefaultPasswordField extends JPasswordField 
{
    public final String ACTION_MAPPING_KEY_ESCAPE = "ACTION_MAPPING_KEY_ESCAPE";
    
    private InputVerifierProxy inputVerifierProxy;  
    private Color focusBackground;
    private Color disabledBackground;
    private Color enabledBackground;
    private boolean readonly;
    
    private String focusKeyStroke;
    private KeyStroke focusKeyStrokeObject;
    private String hint;
    private boolean inFocus;
    
    private Map<String,List<ActionListener>> actionMap;   
    
    public DefaultPasswordField() {
        super();
        initComponent();
    }

    private void initComponent() { 
        TextComponentSupport.getInstance().installUIDefaults(this); 
        setPreferredSize(new Dimension(100,20)); 
        actionMap = new HashMap(); 
        
        KeyStroke esc = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, JComponent.WHEN_FOCUSED); 
        getInputMap(JComponent.WHEN_FOCUSED).put(esc, ACTION_MAPPING_KEY_ESCAPE); 
        getActionMap().put(ACTION_MAPPING_KEY_ESCAPE, new EscapeActionSupport()); 
        
        initDefaults(); 
        resetInputVerifierProxy();         
        focusBackground = ThemeUI.getColor("XTextField.focusBackground");
    }
    
    protected void initDefaults() {
        //to be implemented by the sub-class
    }
        
    protected InputVerifier getMainInputVerifier() { return null; } 
    protected InputVerifier getChildInputVerifier() { return null; } 
    
    public Color getFocusBackground() { return focusBackground; } 
    public Color getDisabledBackground() { return disabledBackground; } 
    public Color getEnabledBackground() { return enabledBackground; } 
    
    public Color getBackground() 
    {
        if (Beans.isDesignTime()) return super.getBackground();
        
        Color bgcolor = null;
        boolean enabled = isEnabled(); 
        if (enabled) 
        {
            if (hasFocus()) 
            {
                Color newColor = getFocusBackground();
                bgcolor = (newColor == null? enabledBackground: newColor);
            }
            else {
                bgcolor = enabledBackground; 
            } 
        } 
        else { 
            bgcolor = disabledBackground;
        } 
        
        return bgcolor == null? new Color(255,255,255): bgcolor;
    } 
    
    public boolean isReadonly() { return readonly; }
    public void setReadonly(boolean readonly) 
    {
        if (!isEnabled()) return;

        this.readonly = readonly;
        setEditable(!readonly);
        super.firePropertyChange("editable", readonly, !readonly); 
    }

    public void setEnabled(boolean enabled) 
    {
        super.setEnabled(enabled);
        setEditable((enabled? !isReadonly(): enabled));    
    }
    
    public String getFocusKeyStroke() { return focusKeyStroke; }
    public void setFocusKeyStroke(String focusKeyStroke) {
        this.focusKeyStroke = focusKeyStroke;        
        
        KeyStroke oldKeyStroke = this.focusKeyStrokeObject;
        if (oldKeyStroke != null) unregisterKeyboardAction(oldKeyStroke); 
        
        try {
            this.focusKeyStrokeObject = KeyStroke.getKeyStroke(focusKeyStroke); 
        } catch(Throwable t){
            this.focusKeyStrokeObject = null; 
        } 
        
        if (this.focusKeyStrokeObject != null) {
            ActionListener actionL = new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    invokeFocusAction(e); 
                }                
            };
            registerKeyboardAction(actionL, this.focusKeyStrokeObject, JComponent.WHEN_IN_FOCUSED_WINDOW);
        }
    }
    
    public String getHint() { return hint; } 
    public void setHint(String hint) { this.hint = hint; }
    
    protected void resetInputVerifierProxy() 
    {
        inputVerifierProxy = new InputVerifierProxy(getMainInputVerifier()); 
        inputVerifierProxy.setChild(getChildInputVerifier());        
        super.setInputVerifier(inputVerifierProxy); 
        super.putClientProperty(InputVerifierProxy.class, inputVerifierProxy);  
    }
    
    public final InputVerifier getInputVerifier() { return inputVerifierProxy; }    
    public final void setInputVerifier(InputVerifier verifier) { 
        inputVerifierProxy.setEnabled((verifier == null? false: true)); 
    } 
    
    public final InputVerifierProxy getInputVerifierProxy() { return inputVerifierProxy; }
    
    protected final void processFocusEvent(FocusEvent e) 
    {
        if (e.getID() == FocusEvent.FOCUS_GAINED) 
        {
            inFocus = true;
            updateBackground();
            
            resetInputVerifierProxy(); 
            inputVerifierProxy.setEnabled(true); 
            super.setInputVerifier(inputVerifierProxy); 
            
            try { onfocusGained(e); } catch(Exception ex) {;} 
        } 
        else if (e.getID() == FocusEvent.FOCUS_LOST) 
        { 
            if (!e.isTemporary()) {
                inFocus = false;
                updateBackground();
            } 

            try { onfocusLost(e); } catch(Exception ex) {;} 
            
            inputVerifierProxy.setEnabled(false);
        } 
        
        super.processFocusEvent(e); 
    }     
        
    protected void updateBackground() 
    {
        if (enabledBackground == null) 
            enabledBackground = UIManager.getLookAndFeelDefaults().getColor("TextField.background");
        if (disabledBackground == null)
            disabledBackground = UIManager.getLookAndFeelDefaults().getColor("TextField.disabledBackground");
        
        Color newColor = getBackground(); 
        setBackground(newColor); 
        repaint();
    }
    
    protected void onfocusGained(FocusEvent e) {
    }
    
    protected void onfocusLost(FocusEvent e) {
    }    
    
    public final void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) 
    {
        if ("enableInputVerifier".equals(propertyName)) 
            inputVerifierProxy.setEnabled(newValue); 
        else if ("detachInputVerifier".equals(propertyName)) 
            super.setInputVerifier((newValue == true? null: inputVerifierProxy));
        
        onpropertyChange(propertyName, oldValue, newValue); 
        super.firePropertyChange(propertyName, oldValue, newValue); 
    } 
        
    protected void onpropertyChange(String propertyName, boolean oldValue, boolean newValue) {}

    protected void processKeyEvent(KeyEvent e) 
    {
        onprocessKeyEvent(e); 
        super.processKeyEvent(e); 
    }
    
    protected void onprocessKeyEvent(KeyEvent e){
    }    
    
    private void invokeFocusAction(ActionEvent e) {
        if (isEnabled() && isFocusable()) requestFocus();
    }

    protected final boolean isInFocus() { return inFocus; } 
    
    public void paint(Graphics g) {
        super.paint(g); 
        
        String hint = getHint();
        if (hint == null || hint.length() == 0) return;
        
        char[] chars = getPassword();
        if (chars == null || chars.length == 0) {
            if (inFocus) return;
            
            Color oldColor = g.getColor();
            Color newColor = new Color(150, 150, 150);
            g.setColor(newColor);
            g.drawString(hint, 0, 0);
            g.setColor(oldColor); 
        }
    } 
    
    // <editor-fold defaultstate="collapsed" desc=" Font support implementation ">
    
    private FontSupport fontSupport;
    private Font sourceFont;     
    private String fontStyle; 
    
    private FontSupport getFontSupport() {
        if (fontSupport == null) 
            fontSupport = new FontSupport();
        
        return fontSupport; 
    }    
    
    public void setFont(Font font) { 
        sourceFont = font; 
        if (sourceFont != null) {
            Map attrs = getFontSupport().createFontAttributes(getFontStyle()); 
            sourceFont = getFontSupport().applyStyles(sourceFont, attrs);
        }
        
        super.setFont(sourceFont); 
    }     
    
    public String getFontStyle() { return fontStyle; } 
    public void setFontStyle(String fontStyle) {
        this.fontStyle = fontStyle;
        
        if (sourceFont == null) sourceFont = super.getFont(); 
        
        Font font = sourceFont;
        if (font == null) return;
        
        Map attrs = getFontSupport().createFontAttributes(getFontStyle()); 
        font = getFontSupport().applyStyles(font, attrs);         
        super.setFont(font); 
    } 
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Event handler support ">
    
    public void addActionMapping(String actionMappingKey, ActionListener actionListener) {
        if (actionListener == null) return;
        
        List<ActionListener> listeners = actionMap.get(actionMappingKey); 
        if (listeners == null) {
            listeners = new ArrayList();
            actionMap.put(actionMappingKey, listeners); 
        }
        if (!listeners.contains(actionListener)) {
            listeners.add(actionListener); 
        }
    }
    
    public void removeActionMapping(String actionMappingKey, ActionListener actionListener) {
        if (actionListener == null) return;
        
        List<ActionListener> listeners = actionMap.get(actionMappingKey); 
        if (listeners != null) listeners.remove(actionListener); 
    }
    
    public void removeActionMapping(ActionListener actionListener) {
        if (actionListener == null) return;
        
        Collection<List<ActionListener>> values = actionMap.values(); 
        for (List<ActionListener> val : values) val.remove(actionListener); 
    }    
    
    public void removeActionMappings() {
        Collection<List<ActionListener>> values = actionMap.values(); 
        for (List val : values) {
            val.clear();
        }         
        actionMap.clear();
    }     
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" EscapeActionSupport ">
    
    private class EscapeActionSupport extends AbstractAction
    {
        DefaultPasswordField root = DefaultPasswordField.this;
        
        public void actionPerformed(ActionEvent e) {
            List<ActionListener> listeners = root.actionMap.get(root.ACTION_MAPPING_KEY_ESCAPE); 
            if (listeners == null || listeners.isEmpty()) return;
            
            for (ActionListener al : listeners) { 
                al.actionPerformed(e); 
            } 
        } 
    } 
    
    // </editor-fold>
}
