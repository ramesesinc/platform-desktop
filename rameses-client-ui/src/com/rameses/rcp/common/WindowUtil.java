/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.common;

import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Toolkit;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author wflores
 */
public final class WindowUtil {
    
    private final static String[] WIN_ATTR_KEYS = new String[]{
        "modal", "resizable", "undecorated", 
        "headless", "alwaysOnTop", "fitScreen", 
        "width", "height", "title"
    }; 
    
    
    public static synchronized Map extractWindowAttrs( Map props ) {
        HashMap newmap = new HashMap();
        if ( props == null || props.isEmpty()) {
            return newmap;
        }
        
        for ( String sname : WIN_ATTR_KEYS ) { 
            Object val = props.get( sname ); 
            if ( val != null ) {
                newmap.put( sname, val ); 
            }
        }

        Object val = newmap.get("fitScreen");
        boolean fitscreen = "true".equalsIgnoreCase((val == null ? "" : val.toString()));
        if ( fitscreen ) {
            Dimension dim = WindowUtil.getFitScreenSize(); 
            newmap.put("width", ""+dim.width);
            newmap.put("height", ""+dim.height);
        } 
        return newmap;
    }
    
    public static Dimension getFitScreenSize() {
        Dimension scrdim = Toolkit.getDefaultToolkit().getScreenSize(); 
        Dimension newdim = new Dimension( scrdim.width, scrdim.height );
        
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment(); 
        GraphicsConfiguration gc = ge.getDefaultScreenDevice().getDefaultConfiguration(); 
        Insets margin = Toolkit.getDefaultToolkit().getScreenInsets( gc ); 
        if ( margin == null ) {
            margin = new Insets( 0, 0, 0, 0 );
        }         
        newdim.width -= (margin.left + margin.right); 
        newdim.height -= (margin.top + margin.bottom); 
        return newdim;
    } 
}
