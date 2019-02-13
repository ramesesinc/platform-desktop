/*
 * ControlProperty.java
 *
 * Created on June 21, 2010, 5:15 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.ui;

import java.awt.Font;
import java.awt.Insets;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 *
 * @author jaycverg
 */
public class ControlProperty 
{
    private PropertyChangeSupport support = new PropertyChangeSupport(this);
    
    private String caption = "Caption";
    private String errorMessage;
    private String captionFontStyle;     
    private char captionMnemonic;
    private int index;
    private int captionWidth = 0;    
    private boolean captionSet;
    private boolean required;
    private boolean showCaption = true;
    private Insets cellPadding = new Insets(0,0,0,0);
    private Font captionFont;
        
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        if (listener == null) return;
        
        support.removePropertyChangeListener(listener); 
        support.addPropertyChangeListener(listener);
    }
    
    public String getCaption() { return caption; }    
    public void setCaption(String caption) {
        String oldvalue = this.caption;
        this.caption = caption;
        this.captionSet = true;
        support.firePropertyChange("caption", oldvalue, this.caption);        
    }
    
    public boolean isCaptionSet() { return captionSet; }
    
    public char getCaptionMnemonic() { return captionMnemonic; }    
    public void setCaptionMnemonic(char captionMnemonic) {
        char oldvalue = this.captionMnemonic;
        this.captionMnemonic = captionMnemonic;
        support.firePropertyChange("captionMnemonic", oldvalue, this.captionMnemonic); 
    }
    
    public int getIndex() { return index; }    
    public void setIndex(int index) {
        int oldvalue = this.index; 
        this.index = index;
        support.firePropertyChange("index", oldvalue, this.index);
    }
    
    public boolean isRequired() { return required; }    
    public void setRequired(boolean required) {
        boolean oldvalue = this.required; 
        this.required = required;
        support.firePropertyChange("required", oldvalue, this.required);
    }
    
    public int getCaptionWidth() { return captionWidth; }    
    public void setCaptionWidth(int captionWidth) {
        int oldvalue = this.captionWidth;
        this.captionWidth = captionWidth;
        support.firePropertyChange("captionWidth", oldvalue, this.captionWidth);
    }
    
    public boolean isShowCaption() { return showCaption; }    
    public void setShowCaption(boolean showCaption) {
        boolean oldvalue = this.showCaption;
        this.showCaption = showCaption;
        support.firePropertyChange("showCaption", oldvalue, this.showCaption); 
    }
    
    public String getErrorMessage() { return errorMessage; }    
    public void setErrorMessage(String message) {
        String oldvalue = this.errorMessage;
        this.errorMessage = message;
        support.firePropertyChange("errorMessage", oldvalue, this.errorMessage);
    }

    public Font getCaptionFont() { return captionFont; }
    public void setCaptionFont(Font captionFont) {
        Font oldvalue = this.captionFont;
        this.captionFont = captionFont;
        support.firePropertyChange("captionFont", oldvalue, this.captionFont);
    }
    
    public String getCaptionFontStyle() { return captionFontStyle; } 
    public void setCaptionFontStyle(String captionFontStyle) {
        String oldvalue = this.captionFontStyle;
        this.captionFontStyle = captionFontStyle;
        support.firePropertyChange("captionFontStyle", oldvalue, this.captionFontStyle);
    }     

    public Insets getCellPadding() { return cellPadding; }
    public void setCellPadding(Insets cellPadding) {
        Insets oldvalue = this.cellPadding;
        this.cellPadding = cellPadding;
        support.firePropertyChange("cellPadding", oldvalue, this.cellPadding);
    } 
}
