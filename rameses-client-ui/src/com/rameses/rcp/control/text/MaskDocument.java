/*
 * MaskDocument.java
 *
 * Created on September 2, 2013, 11:51 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control.text;

import java.beans.Beans;
import java.util.HashMap;
import java.util.Map;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;

/**
 *
 * @author wflores
 */
public class MaskDocument extends PlainDocument 
{
    private JTextComponent jtext;
    private MaskChar[] masks;    
    private char placeHolder; 
    private boolean includeLiteralChars;
    
    public MaskDocument(JTextComponent jtext) {
        this.jtext = jtext; 
        this.placeHolder = '_';
        this.includeLiteralChars = true;
    }
    
    public char getPlaceHolder() { return placeHolder; } 
    public void setPlaceHolder(char placeHolder) {
        this.placeHolder = placeHolder; 
    }
    
    public boolean isIncludeLiteral() { return includeLiteralChars; } 
    public void setIncludeLiteral(boolean includeLiteralChars) {
        this.includeLiteralChars = includeLiteralChars; 
    }

    public void updateMask(String maskFormat) {
        masks = getMaskCharManager().createMasks(maskFormat); 
        char[] chars = getMaskValues();
        try {
            super.remove(0, getLength());
            super.insertString(0, new String(chars), null); 
        } catch (BadLocationException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        } 
    }
    
    public Object getValue() { 
        StringBuffer sb = new StringBuffer();
        for (int i=0; i<masks.length; i++) {
            char ch = masks[i].getValue();
            if (masks[i] instanceof FixedMaskChar) {
                if (isIncludeLiteral()) sb.append(ch); 
            } else if (ch == '\u0000') {
                //not filled-up 
                return null;
            } else { 
                sb.append(ch);
            }
        } 
        return sb.toString(); 
    } 

    public void setValue(Object value) {
        char[] chars = (value == null? new char[]{}: value.toString().toCharArray()); 
        for (MaskChar mc: masks) mc.reset(); 

        int mi = 0;        
        for (int i=0; i<chars.length; i++) {
            if (i >= masks.length) break; 

            if (masks[mi] instanceof FixedMaskChar) { 
                if (!isIncludeLiteral()) i--;

                mi++;                
                continue;
            }
            
            if (masks[mi].accept(chars[i])) { 
                masks[mi].setValue(chars[i]); 
                mi++;
            } else {
                break;
            }
        }

        chars = getMaskValues(); 
        try {
            super.remove(0, getLength());
            super.insertString(0, new String(chars), null); 
        } catch (BadLocationException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }            
    }     
        
    private String getText() {
        try {
            return new StringBuffer(getText(0, getLength())).toString();
        } catch (BadLocationException ex) {
            ex.printStackTrace(); 
            return null; 
        }
    }
    
    private char[] getMaskValues() {
        char[] chars = new char[masks.length];
        for (int i=0; i<chars.length; i++) {
            chars[i] = masks[i].getValue(); 
            if (chars[i] == '\u0000') chars[i] = getPlaceHolder(); 
        }
        return chars;
    }
    
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
        if (Beans.isDesignTime()) {
            super.insertString(offs, str, a); 
            return; 
        }
        
        if (offs >= masks.length) return;

        boolean hasChanges = false;            
        int index = 0, updatedIndex = 0; 
        char[] chars = str.toCharArray();
        for (int i=offs; i<masks.length; i++) {
            if (index >= chars.length) break;
            if (!masks[i].isAllowInput()) continue; 
            
            char ch = chars[index];
            if (!masks[i].accept(ch)) break;
            
            masks[i].setValue(ch);
            ch = masks[i].getValue();
            
            updatedIndex = i;
            hasChanges = true;                 
            super.remove(i, 1); 
            super.insertString(i, ch+"", a); 
            index++;
        }
        
        if (!hasChanges) return;
        
        int caretPos = getNextUpdatableIndex(updatedIndex+1); 
        if (caretPos < 0) {
            if (updatedIndex+1 == masks.length) 
                caretPos = masks.length;
            else 
                caretPos = offs;
        }        
        
        try { jtext.setCaretPosition(caretPos); }catch(Throwable t){;} 
    } 
    
    public void remove(int offs, int len) throws BadLocationException { 
        if (Beans.isDesignTime()) {
            super.remove(offs, len); 
            return; 
        } 
        
        super.remove(offs, len);
        StringBuffer sb = new StringBuffer(); 
        int limit = offs + len;
        for (int i=offs; i<limit; i++) {
            if (i >= masks.length) break; 

            masks[i].reset();
            char ch = masks[i].getValue(); 
            sb.append(ch == '\u0000'? getPlaceHolder(): ch);
        }
        super.insertString(offs, sb.toString(), null); 
        try { jtext.setCaretPosition(offs); } catch(Throwable t) {;}        
    }
        
    private int getNextUpdatableIndex(int start) {
        for (int i=start; i<masks.length; i++) {
            if (masks[i].isAllowInput()) return i;
        }
        return -1; 
    }    
    
    // <editor-fold defaultstate="collapsed" desc=" MaskChar implementations ">
    
    private MaskCharManager maskCharManager;
    
    private MaskCharManager getMaskCharManager() {
        if (maskCharManager == null) {
            maskCharManager = new MaskCharManager(); 
        }
        return maskCharManager; 
    }
    
    private class MaskCharManager {
        
        private Map<String,MaskChar> patterns = new HashMap(); 
        
        MaskCharManager() {
            patterns.put("#", new DigitMaskChar());
            patterns.put("U", new UpperCaseMaskChar());
            patterns.put("L", new LowerCaseMaskChar());
            patterns.put("A", new AlphaNumericMaskChar());
            patterns.put("?", new AnyLetterMaskChar());
            patterns.put("*", new AnyCharMaskChar());
            patterns.put("H", new AnyHexMaskChar()); 
        }
        
        MaskChar[] createMasks(String format) {
            if (format == null || format.length() == 0) return new MaskChar[]{};
            
            char[] chars = format.toCharArray();
            MaskChar[] masks = new MaskChar[chars.length];
            for (int i=0; i<chars.length; i++) {
                char ch = chars[i];
                MaskDocument.MaskChar mc = patterns.get(ch+"");
                if (mc != null) {
                    masks[i] = mc.createInstance(); 
                } else {
                    masks[i] = new FixedMaskChar(ch);
                }
            }
            return masks; 
        }
    }
    
    private interface MaskChar {
        boolean accept(char value);        
        MaskChar createInstance(); 
        
        boolean isAllowInput(); 
        char getValue();
        void setValue(char value);
        void reset();
    }
        
    private class FixedMaskChar implements MaskChar {
        private char value;
        
        FixedMaskChar(char value) {
            this.value = value; 
        }
        
        public MaskDocument.MaskChar createInstance() { return null; } 
        public boolean accept(char value) { return true; }        
        public boolean isAllowInput() { return false; }    
        public char getValue() { return value; } 
        public void setValue(char value) {}
        public void reset(){}
    }
    
    private class DigitMaskChar implements MaskChar {
        private char pattern = '#';
        private char value;

        public MaskDocument.MaskChar createInstance() {
            return new DigitMaskChar();
        }        

        public boolean isAllowInput() { return true; }        
        public boolean accept(char value) { 
            return Character.isDigit(value); 
        } 

        public char getValue() { return value; }        
        public void setValue(char value) {
            this.value = (accept(value)? value: '\u0000');
        }
        
        public void reset() {
            this.value = '\u0000'; 
        }
    } 
    
    private class UpperCaseMaskChar implements MaskChar {
        private char pattern = 'U';
        private char value;

        public MaskDocument.MaskChar createInstance() {
            return new UpperCaseMaskChar();
        } 

        public boolean isAllowInput() { return true; }        
        public boolean accept(char value) { 
            return Character.isLetter(value); 
        } 

        public char getValue() { 
            if (value == '\u0000') return value;
            
            return Character.toUpperCase(value); 
        }
        public void setValue(char value) {
            this.value = (accept(value)? value: '\u0000');
        }
        
        public void reset() {
            this.value = '\u0000'; 
        }
    } 
    
    private class LowerCaseMaskChar implements MaskChar {
        private char pattern = 'L';
        private char value;

        public MaskDocument.MaskChar createInstance() {
            return new LowerCaseMaskChar();
        } 

        public boolean isAllowInput() { return true; }        
        public boolean accept(char value) { 
            return Character.isLetter(value); 
        } 

        public char getValue() { 
            if (value == '\u0000') return value;
            
            return Character.toLowerCase(value); 
        }
        public void setValue(char value) {
            this.value = (accept(value)? value: '\u0000');
        }
        
        public void reset() {
            this.value = '\u0000'; 
        }
    } 
    
    private class AlphaNumericMaskChar implements MaskChar {
        private char pattern = 'A';
        private char value;

        public MaskDocument.MaskChar createInstance() {
            return new AlphaNumericMaskChar();
        } 

        public boolean isAllowInput() { return true; }        
        public boolean accept(char value) { 
            return (Character.isLetter(value) || Character.isDigit(value)); 
        } 

        public char getValue() { return value; }
        public void setValue(char value) {
            this.value = (accept(value)? value: '\u0000');
        }
        
        public void reset() {
            this.value = '\u0000'; 
        }
    } 
    
    private class AnyLetterMaskChar implements MaskChar {
        private char pattern = '?';
        private char value;

        public MaskDocument.MaskChar createInstance() {
            return new AnyLetterMaskChar();
        } 

        public boolean isAllowInput() { return true; }        
        public boolean accept(char value) { 
            return Character.isLetter(value); 
        } 

        public char getValue() { return value; }
        public void setValue(char value) {
            this.value = (accept(value)? value: '\u0000');
        }
        
        public void reset() {
            this.value = '\u0000'; 
        }
    } 
    
    private class AnyCharMaskChar implements MaskChar {
        private char pattern = '*';
        private char value;

        public MaskDocument.MaskChar createInstance() {
            return new AnyCharMaskChar();
        } 

        public boolean isAllowInput() { return true; }        
        public boolean accept(char value) { return true; } 

        public char getValue() { return value; }
        public void setValue(char value) { this.value = value; }
        
        public void reset() {
            this.value = '\u0000'; 
        }
    } 
    
    private class AnyHexMaskChar implements MaskChar {
        private char pattern = 'H';        
        private char value;
        private char[] HEX_LETTERS = new char[]{ 
            'a','b','c','d','e','f',
            'A','B','C','D','E','F'
        };

        public MaskDocument.MaskChar createInstance() {
            return new AnyHexMaskChar();
        } 

        public boolean isAllowInput() { return true; }        
        public boolean accept(char value) { 
            if (Character.isDigit(value)) return true; 
            for (int i=0; i<HEX_LETTERS.length; i++) {
                if (HEX_LETTERS[i] == value) return true; 
            }
            return false; 
        } 

        public char getValue() { return value; }
        public void setValue(char value) { 
            this.value = (accept(value)? value: '\u0000'); 
        }
        
        public void reset() {
            this.value = '\u0000'; 
        }
    }
    
    // </editor-fold>
    
}
