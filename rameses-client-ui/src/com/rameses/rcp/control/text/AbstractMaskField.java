/*
 * AbstractMaskField.java
 *
 * Created on September 2, 2013, 1:19 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control.text;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.text.Document;

/**
 *
 * @author wflores 
 */
public class AbstractMaskField extends DefaultTextField 
{
    private String mask;
    private MaskDocument document; 
    
    protected final void initDefaults() 
    {
        addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { 
                actionPerformedImpl(e);
            } 
        });

        document = new MaskDocument(this); 
        super.setDocument(document); 
    } 
    
    // <editor-fold defaultstate="collapsed" desc=" Getters / Setters "> 
    
    public void setDocument(Document document) {}
    
    public String getMask() { return mask; } 
    public void setMask(String mask) { this.mask = mask; } 

    public char getPlaceHolder() {
        return (document == null? '\u0000': document.getPlaceHolder()); 
    }
    public void setPlaceHolder(char placeHolder) {
        if (document != null) document.setPlaceHolder(placeHolder); 
    }
    
    public boolean isIncludeLiteral() { 
        return (document == null? true: document.isIncludeLiteral()); 
    } 
    public void setIncludeLiteral(boolean includeLiteral) {
        if (document != null) document.setIncludeLiteral(includeLiteral); 
    }    
        
    public Object getValue() {
        return (document == null? null: document.getValue()); 
    }
    
    public void setValue(Object value) {
        if (document != null) document.setValue(value); 
    } 
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" helper and supporting methods "> 
    
    protected void updateMask() { 
        document.updateMask(getMask()); 
    } 
        
    private void actionPerformedImpl(ActionEvent e) {         
        transferFocus(); 
    }
    
    // </editor-fold>
    
}
