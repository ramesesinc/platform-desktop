/*
 * TextField.java
 *
 * Created on May 28, 2013, 10:36 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.swingx;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 *
 * @author wflores
 */
public class IntegerField extends TextField 
{
    private NumberDocument document;
    
    public IntegerField() { 
        super.setDocument(document = new NumberDocument()); 
    }

    public Object getValue() 
    {   
        Number num = document.getValue();
        return (num == null? 0: num.intValue());
    }

    public void setValue(Object value) {
        document.setValue(value);
    }
    
    
    // <editor-fold defaultstate="collapsed" desc=" NumberDocument (class) ">    
    
    private class NumberDocument extends PlainDocument
    {    
        private Number value;
        
        public NumberDocument() {}

        private Number decode(Object value) 
        {
            Number num = null;
            if (value instanceof Number) 
                num = (Number) value;            
            else 
            {
                try {
                    num = Integer.valueOf(value.toString()); 
                } catch(Exception e){;} 
            } 
            return num;
        }
        
        public Number getValue() { return value; }
        public void setValue(Number value)
        {
            this.value = value;
            
            try 
            {
                super.remove(0, getLength()); 
                if (value != null) 
                    super.insertString(0, value.intValue()+"", null);
            } 
            catch(Exception ex) {
                throw new IllegalStateException(ex.getMessage(), ex); 
            }
        }

        public void setValue(Object value) {
            setValue(decode(value)); 
        }

        public void insertString(int offs, String str, AttributeSet a) throws BadLocationException 
        {
            StringBuffer sb = new StringBuffer(getText(0, getLength()));
            sb.insert(offs, str);

            if (sb.toString().equals("-"))
            {
                super.insertString(offs, str, a);
                value = null;
            }
            else
            {
                Number num = decode(sb.toString());
                if (num != null)
                {
                    super.insertString(offs, str, a);
                    value = num;
                }
            }
        }

        public void remove(int offs, int len) throws BadLocationException 
        {
            StringBuffer sb = new StringBuffer(getText(0, getLength()));
            sb.delete(offs, offs+len);
            if (sb.length() == 0)
            {
                super.remove(offs, len);
                value = null;
            }
            else
            {
                Number num = decode(sb.toString());
                super.remove(offs, len);
                value = num;
            }
        }
    }    
    
    // </editor-fold>
    
}
