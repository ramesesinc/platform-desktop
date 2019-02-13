/*
 * AppConfBuilder.java
 *
 * Created on February 21, 2013, 9:30 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.clientupdate;

import com.rameses.util.URLDirectory;
import com.rameses.util.URLDirectory.URLFilter;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

/**
 *
 * @author Elmo
 */
public class AppConfBuilder {
    
    /**
     * {rootUrl}/shared/lib1
     * {rootUrl}/apps/app1/app.conf
     * {rootUrl}/apps/app1/libs
     * app.conf contains
     * shared->pointer to shared libs
     */
    private String rootUrl;
    
    public AppConfBuilder(String rootUrl) {
        if(rootUrl.endsWith("/")) rootUrl = rootUrl.substring( 0, rootUrl.length()-1 );
        this.rootUrl = rootUrl;
    }
    
    public String buildConf(String appName) throws Exception {
        StringBuilder sb = new StringBuilder();
        InputStream is = null;
        try {
            sb.append("<app>\n");
            
            String appPath = rootUrl + "/apps/" + appName;
            URL u = new URL(appPath + "/app.conf");
            is = u.openStream();
            if(is==null)
                throw new Exception("File " + appPath + "/app.conf not found");
            Properties props = new Properties();
            props.load( u.openStream() );
            String sharedName = (String)props.remove("shared");
            
            //load the env part
            loadEnv(props,sb);
            sb.append("<modules>\n");
            
            Set<String> set = new LinkedHashSet();
            
            if(sharedName!=null) {
                String sharedPath = rootUrl + "/shared/"+sharedName;
                URL ushared = new URL(sharedPath);
                loadModules( ushared, set );
            }
            
            URL umod = new URL(appPath+"/libs");
            loadModules( umod, set);
            
            for(String s: set) {
                sb.append("<module file=\""+s+"\" />\n");
            }
            
            sb.append("</modules>\n");
            sb.append("</app>");
        } catch(Exception e) {
            throw e;
        } finally {
            try { is.close(); } catch(Exception ign){;}
        }
        
        return sb.toString();
    }
    
    private void loadEnv( Properties props, StringBuilder sb ) {
        sb.append("<env>\n");
        Iterator iter = props.keySet().iterator();
        while(iter.hasNext()) {
            String key = (String) iter.next();
            sb.append( key + "=" + props.getProperty(key) + "\n" );
        }
        sb.append("</env>\n");
    }
    
    private void loadModules( URL dir, final Set set ) {
        URLDirectory ud = new URLDirectory(dir);
        ud.list( new URLFilter() {
            public boolean accept(URL u, String filter) {
                if(filter.endsWith(".jar")) {
                    set.add( filter.substring( filter.lastIndexOf("/")+1 ) );
                }
                return false;
            }
        });
    }
    
}
