/*
 * AbstractNumberField.java
 *
 * Created on May 7, 2013, 10:41 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control.text;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.text.Document;

/**
 *
 * @author wflores
 */
public abstract class AbstractNumberField extends DefaultTextField
{
    private AbstractNumberDocument document;
    private boolean usePrimitiveValue;
    private boolean defaultFocus;

    protected abstract AbstractNumberDocument createDocument();

    protected final void initDefaults() 
    {
        document = createDocument();
        if (document == null) 
            throw new NullPointerException(getClass().getSimpleName() + " requires an AbstractNumberDocument");
        
        super.setDocument(document); 
        
        putClientProperty("TextField.font", getFont());         
        setHorizontalAlignment(SwingConstants.RIGHT);
        setPreferredSize(new Dimension(100, getPreferredSize().height));        
        setFont(Font.decode("Monospaced--"));         
        new ESCKeyAction().register(this);
    }

    protected InputVerifier getMainInputVerifier() { 
        return document.getInputVerifier();
    }
    
    public final void setDocument(Document doc) {;}

    protected final AbstractNumberDocument getModel() { return document; }    
    
    public boolean isUsePrimitiveValue() { return usePrimitiveValue; }
    public void setUsePrimitiveValue(boolean usePrimitiveValue) { this.usePrimitiveValue = usePrimitiveValue; }
    
    public boolean isDefaultFocus() { return defaultFocus; }
    public void setDefaultFocus(boolean defaultFocus) { this.defaultFocus = defaultFocus; }
 
    public double getMinValue() { return document.getMinValue(); }
    public void setMinValue(double value) { document.setMinValue(value); }
    
    public double getMaxValue() { return document.getMaxValue(); }
    public void setMaxValue(double value) { document.setMaxValue(value); }
    
    public String getFormat() { return document.getFormat(); }
    public void setFormat(String format) { document.setFormat(format); }
    
    public Object getValue() { return getNumberValue(); }
    public void setValue(Object value) 
    {
        Number number = null;
        try { 
            number = getModel().decode(value.toString()); 
        } catch(Exception e){;} 
        
        setNumberValue((Number) value); 
    }
    
    public Number getNumberValue() { return document.getValue(); }
    public void setNumberValue(Number value) { 
        document.setValue(value); 
    }
           
    protected void onfocusGained(FocusEvent e) {
        getModel().showFormattedText(false); 
    }
    
    protected void onfocusLost(FocusEvent e) 
    {
        if (e.isTemporary()) return;
        
        getModel().showFormattedText(true); 
    } 
    
    protected void oncancelEditing() {
    }
    
    // <editor-fold defaultstate="collapsed" desc="  ESCKeyAction (class)  ">
    
    private class ESCKeyAction implements ActionListener 
    {        
        KeyStroke keyStroke;
        private JComponent component;
        private ActionListener origAction;
        
        ESCKeyAction() {
            keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false); 
        } 
        
        void register(JComponent component) 
        {
            this.component = component;
            
            origAction = component.getActionForKeyStroke(keyStroke); 
            component.registerKeyboardAction(this, keyStroke, JComponent.WHEN_FOCUSED); 
        }
        
        public void actionPerformed(ActionEvent e) 
        {
            oncancelEditing(); 
        }  
    } 
    
    // </editor-fold>        
}
