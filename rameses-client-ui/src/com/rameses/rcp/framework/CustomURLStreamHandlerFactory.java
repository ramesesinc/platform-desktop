/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.framework;

import java.net.URL;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

/**
 *
 * @author Elmo Nazareno
 */
public class CustomURLStreamHandlerFactory implements URLStreamHandlerFactory {

    public URLStreamHandler createURLStreamHandler(String protocol) { 
        if ( protocol != null && "classpath".equalsIgnoreCase(protocol)) {
            return new ClasspathURLStreamHandler(); 
        } else {
            return null; 
        }
    } 
    
    
    private class ClasspathURLStreamHandler extends com.rameses.util.URLStreamHandler {
        
        CustomURLStreamHandlerFactory root = CustomURLStreamHandlerFactory.this; 
        
        public String getProtocol() { 
            return "classpath"; 
        }

        public URL getResource(String spath) { 
            ClassLoader loader = ClientContext.getCurrentContext().getClassLoader(); 
            URL result = loader.getResource(spath); 
            if ( result == null ) {
                result = root.getClass().getClassLoader().getResource(spath); 
            }
            return result; 
        }
    }
    
}
