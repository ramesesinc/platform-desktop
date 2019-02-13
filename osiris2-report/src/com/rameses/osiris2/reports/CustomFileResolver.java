/*
 * CustomFileResolver.java
 *
 * Created on September 20, 2013, 11:14 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris2.reports;

import com.rameses.rcp.common.MsgBox;
import java.io.File;
import net.sf.jasperreports.engine.util.FileResolver;

/**
 *
 * @author Elmo
 */
public class CustomFileResolver implements FileResolver {
    
    private String parentName;
    
    /** Creates a new instance of CustomFileResolver */
    public CustomFileResolver(String parentName) {
        
    }
    
    public File resolveFile(String string) {
        try {
            MsgBox.alert(string);
            return null;
            /*
            
            return new File(connection.getJarFileURL().toURI());
             */
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    
}
