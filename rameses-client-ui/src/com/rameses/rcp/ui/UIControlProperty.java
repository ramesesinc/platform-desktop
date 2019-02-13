/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.ui;

import java.awt.Font;
import java.awt.Insets;

/**
 *
 * @author wflores
 */
public interface UIControlProperty {
    
    String getCaption();  
    void setCaption(String caption);
    
    char getCaptionMnemonic(); 
    void setCaptionMnemonic(char captionMnemonic);
    
    int getIndex();
    void setIndex(int index);
    
    boolean isRequired();
    void setRequired(boolean required);
    
    int getCaptionWidth();
    void setCaptionWidth(int captionWidth);
    
    Font getCaptionFont();
    void setCaptionFont(Font captionFont);
    
    String getCaptionFontStyle();
    void setCaptionFontStyle(String captionFontStyle);

    boolean isShowCaption();
    void setShowCaption(boolean showCaption);
    
    Insets getCellPadding();
    void setCellPadding(Insets cellPadding);
}
