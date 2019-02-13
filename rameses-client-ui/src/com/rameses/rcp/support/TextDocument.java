package com.rameses.rcp.support;

import com.rameses.rcp.constant.TextCase;
import java.beans.Beans;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class TextDocument extends PlainDocument 
{
    private List<TextDocument.DocumentListener> listeners;
    private TextCase textCase;
    private int maxlength;
    private boolean dirty;
    
    private TextDocument.Filter filter; 
    
    public TextDocument() {
        this.listeners = new ArrayList();
        this.textCase = TextCase.NONE;
        this.maxlength = -1;
    } 
    
    public void remove(TextDocument.DocumentListener listener) {
        if (listener != null) listeners.remove(listener); 
    }
    
    public void add(TextDocument.DocumentListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }
    
    public TextCase getTextCase() { return textCase; }    
    public void setTextCase(TextCase textCase) {
        this.textCase = textCase;
        update(); 
    }
    
    public int getMaxlength() { return maxlength; }    
    public void setMaxlength(int length) { maxlength = length; }
    
    public boolean isDirty() { return dirty; } 
    
    public void reset() { 
        dirty = false; 
    }  
    
    public void loadValue(Object value) {
        loadValue(value, false);
    }
    
    public void loadValue(Object value, boolean dirty) {
        try {
            super.remove(0, getLength());
            insertString(0, (value == null? "": value.toString()), null, false);
        } catch(Throwable ex) {;}
        
        this.dirty = dirty; 
    }
    
    private boolean _replacing_;
    
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
        if ( str != null && str.length() > 0 ) {
            TextDocument.Filter ff = getFilter(); 
            if ( ff != null && !ff.accept(str) ) { 
                //not accepted by the filter 
                return;
            }
        }
        insertString(offs, str, a, true);
    }
    
    private void insertString(int offs, String str, AttributeSet a, boolean dirty) throws BadLocationException {
        if (Beans.isDesignTime()) { 
            super.insertString(offs, str, a); 
            return;
        }
        
        if (maxlength > 0) {
            if (getLength() >= maxlength) return;
            
            if (getLength()+str.length() > maxlength) {
                str = str.substring(0, maxlength - getLength());
            }
        }

        String newstr = null;
        try { 
            newstr = parse(str);
        } catch(Throwable t) {
            //do nothing 
        } finally {
            newstr = str;
        }
        
        //convert if textCase is specified
        if (textCase != null) newstr = textCase.convert(newstr);

        super.insertString(offs, newstr, a);
        this.dirty = dirty; 
        if (dirty) fireOnupdate();
    }

    public void remove(int offs, int len) throws BadLocationException {
        super.remove(offs, len); 
        dirty = true; 
        fireOnupdate(); 
    }
    
    private void update() { 
        try {
            String text = getText(0, getLength());
            super.remove(0, getLength());
            insertString(0, text, null); 
        } catch (Throwable ex) {;}
    } 
        
    private void fireOnupdate() {
        for (TextDocument.DocumentListener listener: listeners) {
            listener.onupdate(); 
        }
    }
    
    private String parse(String text) {
        StringBuffer sb = new StringBuffer();
        if (text == null) return sb.toString();
        
        int start = 0;
        while (true) {
            int idx0 = text.indexOf("\\u", start); 
            if (idx0 < 0) idx0 = text.indexOf("\\U", start); 
            if (idx0 < 0) break;
            
            sb.append(text.substring(start, idx0)); 
            try {
                String str = text.substring(idx0+2, idx0+6); 
                int num = Integer.parseInt(str, 16); 
                sb.append((char) num); 
            } catch(IndexOutOfBoundsException iobe) {
                sb.append(text.substring(idx0));
            } catch(NumberFormatException nfe) {
                sb.append(text.substring(idx0));
            } catch(Throwable t) {
                sb.append(text.substring(idx0, idx0+6));
            } finally {
                start = idx0+6;
            }
        }
        if (start < text.length()) {
            sb.append(text.substring(start));
        } 
        return sb.toString();
    }     
    
    public TextDocument.Filter getFilter() { return filter; } 
    public void setFilter( TextDocument.Filter filter ) {
        this.filter = filter; 
    }
    
    
    public static interface DocumentListener {
        void onupdate(); 
    }
    
    public static interface Filter {
        boolean accept( String value );
    }    
    
    public static class DefaultFilter implements Filter {
        public boolean accept( String value ) {
            return true; 
        }
    }
    
    public static class NumberFilter implements Filter {
        public boolean accept( String value ) { 
            try {
                new Integer( value ); 
                return true; 
            } catch(Throwable t) {
                return false; 
            }
        }
    }
    
    public static class DecimalFilter implements Filter {
        public boolean accept( String value ) { 
            try {
                new BigDecimal( value ); 
                return true; 
            } catch(Throwable t) {
                return false; 
            }
        }
    } 
}

