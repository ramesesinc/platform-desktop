/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.ui;

import com.rameses.rcp.constant.TextCase;
import com.rameses.rcp.constant.TrimSpaceOption;
import java.awt.Color;
import java.awt.Insets;

/**
 *
 * @author wflores
 */
public interface UITextComponent {
    
    boolean isReadonly();
    void setReadonly( boolean readonly );
    
    boolean isEditable();
    void setEditable( boolean editable );
    
    boolean isFocusable();
    void setFocusable( boolean focusable );
    
    Color getDisabledTextColor();
    void setDisabledTextColor( Color disabledTextColor );
    
    Insets getMargin(); 
    void setMargin( Insets margin ); 
    
    String getActionCommand();
    void setActionCommand( String actionCommand );
    
    char getFocusAccelerator();
    void setFocusAccelerator( char focusAccelerator);
    
    String getFocusKeyStroke();
    void setFocusKeyStroke(String focusKeyStroke);
    
    String getHint();
    void setHint( String hint ); 
    
    int getHorizontalAlignment(); 
    void setHorizontalAlignment( int horizontalAlignment ); 
    
    String getInputFormat();
    void setInputFormat( String inputFormat);
    
    String getInputFormatErrorMsg();
    void setInputFormatErrorMsg( String inputFormatErrorMsg);
    
    int getMaxLength();
    void setMaxLength(int length);
    
    char getSpaceChar();
    void setSpaceChar(char spaceChar); 
    
    TextCase getTextCase();
    void setTextCase(TextCase textCase);
    
    TrimSpaceOption getTrimSpaceOption();
    void setTrimSpaceOption(TrimSpaceOption trimSpaceOption);
    
    boolean isNullWhenEmpty();
    void setNullWhenEmpty( boolean nullWhenEmpty ); 
}
