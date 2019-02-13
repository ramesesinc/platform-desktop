/*
 * ListCellModel.java
 *
 * Created on March 21, 2018, 3:34 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.rameses.rcp.common;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author wflores
 */
public class ProgressModel {
    
    private int minValue;
    private int maxValue; 
    private int value;
    
    public int getMinValue() { return minValue; } 
    
    public int getMaxValue() { return maxValue; } 
    public void setMaxValue( int maxValue ) {
        this.maxValue = maxValue; 
        refresh();
    }

    public int getValue() { return value; } 
    public void setValue( int value ) {
        this.value = value; 
        refresh(); 
    }
    
    public void refresh() {
        if ( provider != null ) {
            provider.adjustValues( getMinValue(), getMaxValue(), getValue() ); 
        }
    }

    public void setText( String text ) {
        if ( provider != null ) { 
            provider.adjustText( text ); 
        }
    }
    
    
    private LinkedBlockingQueue queue;    
    public void pause() {
        pause( 250 ); 
    }
    public void pause( long millis ) {
        if ( queue == null ) {
            queue = new LinkedBlockingQueue();
        }
        
        try {
            queue.poll(millis, TimeUnit.MILLISECONDS );
        } catch(Throwable t) {;} 
    }
    
    
    // <editor-fold defaultstate="collapsed" desc=" Provider ">
    
    private Provider provider;    
    public final void setProvider(Provider provider) { 
        this.provider = provider; 
    }
    
    public Object getBinding() {
        return (provider == null? null: provider.getBinding()); 
    }
    
    public static interface Provider {
        Object getBinding(); 
        void adjustValues( int min, int max, int value );  
        void adjustText( String text ); 
    }
    
    // </editor-fold>
    
}
