/*
 * AbstractDateField.java
 *
 * Created on August 28, 2013, 11:05 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control.text;

import java.awt.event.KeyEvent;
import java.beans.Beans;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EventObject;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

/**
 *
 * @author wflores
 */
public abstract class AbstractDateField extends DefaultTextField 
{
    private IDateDocument document;
    private String outputFormat;
    private String valueFormat;
    private String inputMask;
    private String hint;
    private int advanceYearLimit = 15;
    
    protected final void initDefaults() 
    {
//        addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                actionPerformedImpl(e);
//            }
//        });
        
        if (!Beans.isDesignTime()) 
            setDocumentImpl(new BasicDateDocument()); 
    }        
    
    // <editor-fold defaultstate="collapsed" desc=" Getters / Setters "> 
    
    public String getHint() { return hint; } 
    public void setHint(String hint) { this.hint = hint; }
    
    public String getInputFormat() {  
        return getInputMask(); 
    } 
    public void setInputFormat(String inputFormat) {
        setInputMask(inputFormat); 
    } 
    
    public String getOutputFormat() { return outputFormat; } 
    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat; 
    }
        
    public String getValueFormat() { return valueFormat; } 
    public void setValueFormat(String valueFormat) {
        this.valueFormat = valueFormat; 
    } 
    
    public String getInputMask() { return inputMask; } 
    public void setInputMask(String inputMask) {
        this.inputMask = inputMask; 
    }
    
    public int getAdvanceYearLimit() { return advanceYearLimit; } 
    public void setAdvanceYearLimit(int advanceYearLimit) {
        this.advanceYearLimit = advanceYearLimit; 
    }
    
    public Object getValue() {
        return (document == null? null: document.getValue()); 
    }
    
    public void setValue(Object value) {
        if (document == null) 
            super.setText(""); 
        else 
            document.setValue(value); 
    } 
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" helper and supporting methods "> 
    
    protected void reloadDocument() {
        String mask = getInputMask();
        if (mask == null || mask.length() == 0) 
            setDocumentImpl(new BasicDateDocument()); 
        else 
            setDocumentImpl(new MaskDateDocument(mask)); 
    } 
    
    private void setDocumentImpl(Document document) {
        super.setDocument(document); 
        if (document instanceof IDateDocument) {
            this.document = (IDateDocument)document;
        } else {
            this.document = null; 
        }
    }
    
//    private void actionPerformedImpl(ActionEvent e) {         
//        transferFocus(); 
//    }
    
    private DateParser createDateParser() {
        DateParser parser = new DateParser();
        parser.setAdvanceYearLimit(getAdvanceYearLimit()); 
        return parser; 
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" IDateDocument interface "> 
    
    private interface IDateDocument {
        Object getValue(); 
        void setValue(Object value); 
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" BasicDateDocument class ">
    
    private class BasicDateDocument extends PlainDocument implements IDateDocument  
    {        
        AbstractDateField root = AbstractDateField.this; 
        private SimpleDateFormat outputFormatter; 
        private SimpleDateFormat valueFormatter;
        private boolean dirty;

        public Object getValue() { 
            String sval = getText();
            if (sval == null || sval.length() == 0) return null;
            
            try {
                Date dt = createDateParser().parse(sval); 
                if (dt == null) dt = getOutputFormatter().parse(sval); 
                if (dt == null) return null; 

                return getValueFormatter().format(dt); 
            } catch(Throwable t) {
                return null; 
            }            
        } 

        public void setValue(Object value) {
            String sval = null;
            if (value == null) {
                //do nothing 
            } else if (value instanceof EventObject) {
                if (value instanceof KeyEvent) { 
                    char ch = ((KeyEvent) value).getKeyChar(); 
                    root.setText(ch+""); 
                } 
                //exit
                return;
            }
            
            if (value == null) {
                //do nothing 
            } else if (value instanceof Date) {
                sval = getOutputFormatter().format((Date) value); 
            } else {
                Date dt = createDateParser().parse(value.toString()); 
                if (dt != null) sval = getOutputFormatter().format(dt); 
            } 
            
            try {
                super.remove(0, getLength());
                if (sval != null) insertString(0, sval, null); 
            } catch (BadLocationException ex) {
                throw new RuntimeException(ex.getMessage(), ex);
            } finally {
                dirty = false; 
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
        
        private SimpleDateFormat getOutputFormatter() {
            if (outputFormatter == null) {
                String format = root.getOutputFormat(); 
                if (format == null || format.length() == 0) format = "yyyy-MM-dd"; 
                
                outputFormatter = new SimpleDateFormat(format); 
            } 
            return outputFormatter; 
        } 
        
        private SimpleDateFormat getValueFormatter() {
            if (valueFormatter == null) {
                String format = root.getValueFormat();
                if (format == null || format.length() == 0) format = "yyyy-MM-dd"; 
                
                valueFormatter = new SimpleDateFormat(format); 
            } 
            return valueFormatter; 
        }         
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" MaskDateDocument class ">
    
    private class MaskDateDocument extends PlainDocument implements IDateDocument  
    {        
        AbstractDateField root = AbstractDateField.this; 
        private SimpleDateFormat valueFormatter;
        private MaskChar[] maskDefs;
        private MaskChar[] masks;

        MaskDateDocument(String mask) {
            char[] chars = mask.toCharArray();
            masks = new MaskChar[chars.length];
            for (int i=0; i<chars.length; i++) {
                masks[i] = getWhichMaskChar(chars[i]); 
            }
        }
        
        public Object getValue() { 
            char[] chars = new char[masks.length];
            for (int i=0; i<chars.length; i++) {
                chars[i] = masks[i].getValue(); 
            } 
            try {
                String sval = new String(chars);
                String dtval = java.sql.Date.valueOf(sval).toString(); 
                if (sval.equals(dtval)) return sval; 
                
                return null; 
            } catch(Throwable t) {
                return null; 
            }
        } 

        public void setValue(Object value) {
            char[] chars = (value == null? new char[]{}: value.toString().toCharArray()); 
            for (MaskChar mc: masks) mc.reset();            
            for (int i=0; i<chars.length; i++) {
                if (i >= masks.length) break; 
                
                masks[i].setValue(chars[i]); 
            }
            
            chars = getMaskValues(); 
            try {
                super.remove(0, getLength());
                super.insertString(0, new String(chars), null); 
            } catch (BadLocationException ex) {
                throw new RuntimeException(ex.getMessage(), ex);
            }            
        } 
        
        private MaskChar getWhichMaskChar(char ch) {
            if (maskDefs == null) {
                maskDefs = new MaskChar[]{
                    new PatternMaskChar(), 
                    new LiteralMaskChar()                     
                };
            }
            
            for (MaskChar mc: maskDefs) {
                if (mc.accept(ch)) 
                    return mc.createMaskChar(ch); 
            }
            return null; 
        }
        
        private char[] getMaskValues() {
            char[] chars = new char[masks.length];
            for (int i=0; i<chars.length; i++) {
                chars[i] = masks[i].getValue(); 
                if (chars[i] == '\u0000') chars[i] = '_';
            }
            return chars;
        }
        
        private String getText() {
            try {
                return new StringBuffer(getText(0, getLength())).toString();
            } catch (BadLocationException ex) {
                ex.printStackTrace(); 
                return null; 
            }
        }
        
        private SimpleDateFormat getValueFormatter() {
            if (valueFormatter == null) {
                String format = root.getValueFormat();
                if (format == null || format.length() == 0) format = "yyyy-MM-dd"; 
                
                valueFormatter = new SimpleDateFormat(format); 
            } 
            return valueFormatter; 
        }     

        public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
            if (offs >= masks.length) return;

            boolean hasChanges = false;            
            int index = 0, updatedIndex = 0; 
            char[] chars = str.toCharArray();
            for (int i=offs; i<masks.length; i++) {
                if (index >= chars.length) break;
                if (!masks[i].isAllowInput()) continue; 
                
                masks[i].setValue(chars[index]);
                char ch = masks[i].getValue();
                if (ch == '\u0000') break;
                
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
            
            try { root.setCaretPosition(caretPos); } catch(Throwable t) {;} 
        }
                
        private int getNextUpdatableIndex(int start) {
            for (int i=start; i<masks.length; i++) {
                if (masks[i].isAllowInput()) return i;
            }
            return -1; 
        }

        public void remove(int offs, int len) throws BadLocationException {
            super.remove(offs, len);
            StringBuffer sb = new StringBuffer(); 
            int limit = offs + len;
            for (int i=offs; i<limit; i++) {
                if (i >= masks.length) break; 
                
                masks[i].reset();
                char ch = masks[i].getValue(); 
                sb.append(ch == '\u0000'? '_': ch);
            }
            super.insertString(offs, sb.toString(), null); 
            int caretPos = offs;
            try { root.setCaretPosition(caretPos); } catch(Throwable t) {;}        
        }
        
        private int getUpdatableIndexBefore(int start) {
            for (int i=start-1; i>0; i--) {
                if (i >= masks.length) break;                
                if (masks[i].isAllowInput()) return i;
            }
            return -1; 
        }
        
        private boolean isUpdatableIndex(int index) { 
            if (index >= 0 && index < masks.length) 
                return masks[index].isAllowInput(); 
            else 
                return false; 
        } 
    } 
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" MaskChar implementation ">
    
    private interface MaskChar {
        boolean accept(char value);        
        MaskChar createMaskChar(char value); 
        
        boolean isAllowInput(); 
        char getValue();
        void setValue(char value);
        void reset();
    }
    
    private class LiteralMaskChar implements MaskChar 
    {
        private char value;
        
        public boolean accept(char value) { return true; }

        public AbstractDateField.MaskChar createMaskChar(char value) {
            LiteralMaskChar mc = new LiteralMaskChar();
            mc.value = value;
            return mc; 
        } 

        public boolean isAllowInput() { return false; }    
        public char getValue() { return value; } 
        public void setValue(char value) {}
        public void reset(){}
    }
    
    private class PatternMaskChar implements MaskChar 
    {
        private char pattern;
        private char value;
        
        public boolean accept(char value) { 
            return (value=='y' || value=='M' || value=='d' || value=='H' || value=='m' || value=='s');
        } 

        public AbstractDateField.MaskChar createMaskChar(char value) {
            PatternMaskChar mc = new PatternMaskChar();
            mc.pattern = value;
            return mc; 
        }        

        public boolean isAllowInput() { return true; }

        public char getValue() { return value; }
        
        public void setValue(char value) {
            this.value = (Character.isDigit(value)? value: '\u0000');
        }
        
        public void reset() {
            this.value = '\u0000'; 
        }
    }    
    
    // </editor-fold>
}
