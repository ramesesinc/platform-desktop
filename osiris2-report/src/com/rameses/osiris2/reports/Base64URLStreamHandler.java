/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris2.reports;

import com.rameses.util.Base64Cipher;
import com.rameses.util.URLStreamHandler;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

/**
 *
 * @author wflores 
 */
public class Base64URLStreamHandler extends URLStreamHandler {

    private final static String KEY_NAME = "base64";
    
    public String getProtocol() {
        return KEY_NAME;
    }

    public URL getResource(String spath) {
        return null; 
    }

    protected URLConnection openConnection(URL url) throws IOException { 
        return new URLConnImpl( url ); 
    } 
    
    private class URLConnImpl extends URLConnection {

        private String BLANK_ICON_STRING = "rO0ABXVyAAJbQqzzF/gGCFTgAgAAeHAAAABEiVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR4nGP6zwAAAgcBApocMXEAAAAASUVORK5CYII="; 
        private byte[] BLANK_ICON_BYTES;
        private byte[] bytes;
        
        protected URLConnImpl( URL url ) { 
            super( url ); 
            
            Base64Cipher base64 = new Base64Cipher(); 
            BLANK_ICON_BYTES = (byte[]) base64.decode(BLANK_ICON_STRING); 
            
            try {
                String str = getURL().toString();  
                int idx = str.indexOf("://"); 
                str = str.substring( idx+3 ); 
                if ( base64.isEncoded( str )) { 
                    Object o = base64.decode( str ); 
                    if ( o instanceof Map ) {
                        o = ((Map)o).get("image"); 
                    }
                    if ( o instanceof byte[] ) {
                        bytes = (byte[])o; 
                    }
                } 
            } catch(Throwable t) {
                //do nothing 
            } 
        }
        
        public void connect() throws IOException {
            //do nothing 
        }

        public InputStream getInputStream() throws IOException { 
            if ( bytes == null || bytes.length==0 ) {
                return new ByteArrayInputStream( BLANK_ICON_BYTES ); 
            } else {
                return new ByteArrayInputStream( bytes ); 
            }
        } 
    }
}
