/*
 * FormulaDocument.java
 *
 * @author jaycverg
 */

package com.rameses.rcp.control.editor;

import com.rameses.rcp.constant.TextCase;
import java.awt.Color;
import java.beans.Beans;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;

public class FormulaDocument extends DefaultStyledDocument 
{
    private List<String> keywords;
    private TextCase textCase; 
    private boolean dirty;
    
    private Style style;
    private Style varStyle;
    private Style amtStyle;
    
    public FormulaDocument() 
    {
        textCase = TextCase.NONE; 
        style = addStyle("Default-Style", null);
        
        varStyle = addStyle("Variable-Style", style);
        StyleConstants.setForeground(varStyle, new Color(0,0,102));
        StyleConstants.setBold(varStyle, true);
        
        amtStyle = addStyle("Amount-Style", style);
        StyleConstants.setForeground(amtStyle, new Color(204,0,0));
    }
    
    public boolean isDirty() { return dirty; } 
    
    public TextCase getTextCase() { return textCase; } 
    public void setTextCase(TextCase textCase) 
    {
        this.textCase = (textCase == null? textCase.NONE: textCase); 
        
        try 
        {
            String str = getText(0, getLength()); 
        }
        catch(Exception ex) {;}
    }
    
    public final String getTextCaseAsString() 
    {
        TextCase tc = getTextCase(); 
        return (tc == null? null: tc.toString()); 
    }
    
    public final void setTextCaseAsString(String sTextCase) 
    {
        try {
            setTextCase(TextCase.valueOf(sTextCase.toUpperCase())); 
        } catch(Exception ex) {
            setTextCase(null); 
        }
    }
    
    public void setValue(Object value, AttributeSet attr) throws Exception 
    {
        super.remove(0, getLength()); 
        insertString(0, (value == null? "": value.toString()), attr);
        dirty = false; 
        updateCharacterAttributes(); 
    }
    
    public List<String> getKeywords() 
    {
        if( keywords != null ) return keywords;
        
        return (keywords = new ArrayList<String>(){
            
            public boolean add(String str) {                
                return super.add((str == null? null: getTextCase().convert(str)));
            }
            
            public boolean addAll(Collection<? extends String> col) 
            {
                List<String> list = new ArrayList();
                for (String s : col) list.add(getTextCase().convert(s));
                
                return super.addAll( list );
            }
        });
    }
    
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException 
    {
        if (Beans.isDesignTime()) 
        { 
            super.insertString(offs, str, a);
            return;
        }
        
        super.insertString(offs, getTextCase().convert(str), style);
        dirty = true; 
        updateCharacterAttributes();
    }
    
    public void remove(int offs, int len) throws BadLocationException 
    {
        if (Beans.isDesignTime()) 
        {
            super.remove(offs, len);
            return;
        }
        
        super.remove(offs, len);
        updateCharacterAttributes();
    }
    
    
    private void updateCharacterAttributes() throws BadLocationException 
    {
        try 
        {
            StringBuffer sb = new StringBuffer(getText(0, getLength()));
            StringBuffer buffer = new StringBuffer();
            int offset = -1;
            for (int i=0; i<sb.length(); i++) 
            {
                if (offset == -1) offset = i;
                
                char ch = sb.charAt(i);
                buffer.append(ch);
                
                if ((ch+"").matches("[^\\w_]")) 
                {
                    buffer.delete(0, buffer.length());
                    offset = -1;
                }
                else {
                    applyStyle(buffer, offset);
                }
            }
        } 
        catch(BadLocationException ble) {
            throw ble;
        }
    }
    
    private void applyStyle(StringBuffer buffer, int offset) 
    {
        String str = buffer.toString();
        if ( getKeywords().contains( str ) )
            setCharacterAttributes(offset, buffer.length(), varStyle, true);
        else if ( str.matches("(\\d+)(\\.?\\d+)?") )
            setCharacterAttributes(offset, buffer.length(), amtStyle, true);
        else
            setCharacterAttributes(offset, buffer.length(), style, true);
    }    
}
