/*
 * EventManager.java
 *
 * Created on November 5, 2010, 1:36 PM
 */
package com.rameses.rcp.framework;

import com.rameses.util.BreakException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class EventManager {
    
    private Map<String, List<EventListener>> listeners = new HashMap();
    
    public void register( String name ) {
        register( name, null ); 
    }
    
    public void register( String name, EventListener el ) {
        String sname = name.toLowerCase(); 
        List list = listeners.get( sname ); 
        if ( list == null  ) { 
            list = new ArrayList(); 
            listeners.put( sname, list ); 
        }
        if ( el != null ) {
            list.add( el ); 
        }
    }
    
    public void unregister( String name ) {   
        List<EventListener> list = listeners.remove( name.toLowerCase() ); 
        if ( list == null ) { return; }
        
        while ( !list.isEmpty() ) { 
            EventListener el = list.remove(0); 
            try {
                el.onUnregister(); 
            } catch( Throwable t ) { 
                //do nothing 
            } 
        }         
    }

    public void add( String name, EventListener el ) { 
        String sname = name.toLowerCase(); 
        List list = listeners.get( sname ); 
        if ( list != null ) { 
            list.add( el );  
        } 
    } 
    
    public void remove( String name ) {    
        List list = listeners.remove( name.toLowerCase() ); 
        if ( list != null ) { list.clear(); }
    }
    
    
    public void postEvent( String name, Object eventObject ) {
        String sname = name.toLowerCase(); 
        List<EventListener> list = listeners.get( sname ); 
        if ( list == null ) { return; }
        
        for ( EventListener el : list ) {
            try {
                el.onEvent( eventObject ); 
            } catch( BreakException be ) { 
                //do nothing 
            } 
        } 
    }
    
    public void destroyEvents() { 
        Iterator itr = listeners.entrySet().iterator(); 
        while ( itr.hasNext() ) {
            Map.Entry me = (Map.Entry) itr.next(); 
            List<EventListener> list = (List) me.getValue(); 
            if ( list == null ) { continue; } 
            
            while ( !list.isEmpty() ) { 
                EventListener el = list.remove(0); 
                try {
                    el.onDestroy();
                } catch( Throwable t ) { 
                    //do nothing 
                } 
            } 
        } 
    } 
    
    public void sendMessage( String name, Object eventObject ) {
        String sname = name.toLowerCase(); 
        List<EventListener> list = listeners.get( sname ); 
        if ( list == null ) { return; }
        
        for ( EventListener el : list ) {
            try {
                el.onMessage( eventObject ); 
            } catch( BreakException be ) { 
                //do nothing 
            } 
        } 
    }    
}
