/*
 * TextWriter.java
 *
 * Created on July 21, 2014, 5:26 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.common;

/**
 *
 * @author wflores
 */
public class TextWriter 
{
    private StringBuilder buffer;
    private TextWriter.Handler handler; 
            
    public TextWriter() {
        buffer = new StringBuilder(); 
    }
    
    public TextWriter.Handler getHandler() { return handler; } 
    public void setHandler(TextWriter.Handler handler) {
        this.handler = handler; 
    }
    
    public String getText() {
        return buffer.toString(); 
    }

    public void write(char[] cbuf) {
        writeObject(new String(cbuf)); 
    }

    public void write(char[] cbuf, int off, int len) {
        writeObject(new String(cbuf, off, len)); 
    }

    public void write(int c) {
        writeObject(c + ""); 
    }

    public void write(String str) {
        writeObject(str); 
    }

    public void write(String str, int off, int len) {
        writeObject(new String(str.getBytes(), off, len)); 
    }
    
    public void write(Object o) {
        writeObject(o); 
    }    
    
    public void writeln(Object o) {
        writeObject(o);
        writeObject("\n"); 
    }
    
    private void writeObject(Object o) {
        buffer.append(o); 
        
        TextWriter.Handler handler = getHandler();
        if (handler != null) {
            handler.write(o == null? "null": o.toString());
        } 
    }
    
    public void clear() {
        int len = buffer.length(); 
        if (len == 0) return;
        
        buffer.delete(0, len); 
        TextWriter.Handler handler = getHandler();
        if (handler != null) handler.clear(); 
    }
    
    
    public static interface Handler {
        void write(String str); 
        void clear();
    }
}
