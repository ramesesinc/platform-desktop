/*
 * OsirisTestPlatform.java
 *
 * Created on October 27, 2009, 5:02 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris2.test;

import com.rameses.osiris2.client.*;
import com.rameses.platform.interfaces.Platform;
import com.rameses.rcp.common.Opener;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.rcp.framework.LookAndFeelCustomizer;
import com.rameses.rcp.framework.UIControllerContext;
import com.rameses.rcp.framework.UIControllerPanel;
import com.rameses.rcp.util.ControlSupport;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.swing.JDialog;
import javax.swing.UIManager;

/**
 *
 * @author elmo
 */
public final class OsirisTestPlatform {
    
    private static Map conf;
    private static Map roles = new HashMap();
    private static Map profile;
    
    
    public static void setConf(Map aEnv) { 
        conf = aEnv; 
        //env;
        if(conf==null) conf = new HashMap();
        if( conf.get("app.title") == null )
            conf.put("app.title", "Osiris Test Platform");
        if( conf.get("app.debugMode") == null )
            conf.put("app.debugMode", true);
    }
    
    public static void setRoles(Map aRoles) { 
        roles = aRoles;
        if(roles==null) roles = new HashMap();
        roles.put("ALLOWED", "system.*");        
    }
    
    public static void setProfile(Map aProfile) { 
        profile = aProfile;
    }
    
    private static Map buildEnv() throws Exception {
        if( conf == null ) conf = getTestConf();
        if(roles==null) roles = new HashMap();
        if(profile==null) profile = new HashMap();
        //add the client env here.    
        Map clientEnv = new HashMap();
        clientEnv.putAll(profile);
        clientEnv.put("ROLES", roles);
        conf.put("CLIENT_ENV", clientEnv);
        return conf;
    } 
    
    private static Platform buildPlatform() throws Exception {
        OsirisAppLoader loader = new OsirisAppLoader();
        Platform platform = ClientContext.getCurrentContext().getPlatform();
        loader.load(ClientContext.getCurrentContext().getClassLoader(), buildEnv(), platform);
        return platform;
    }
    
    public static void runTest(Map conf, Map roles) throws Exception {
        runTest( conf, roles, new HashMap());
    }
    
    public static void runTest(Map conf, Map aroles, Map aprofile) throws Exception {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); 
        } catch(Exception ex) {;}  
        
        LookAndFeelCustomizer.install();
        
        setConf(conf);
        setRoles(aroles);
        setProfile(aprofile);
        buildPlatform().getMainWindow().show();
    }

    
    /**
     * Test cases
     */
    private static Map getTestConf() throws Exception{
        if(conf!=null) return conf;
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("test.conf");
        if(is==null)
            throw new Exception("Cannot find test.conf");
        Properties props = new Properties();
        props.load(is);
        Map map = new HashMap();
        map.putAll(props);
        return map;
    }
    
    public static void testPlatform() throws Exception {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); 
        } catch(Exception ex) {;}  
        
        LookAndFeelCustomizer.install();
        buildPlatform().getMainWindow().show();
    }
    
    
    public static void testWorkunit(String name, Map params ) throws Exception {
        if(name==null)
            throw new Exception("Workunit name must not be null");
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); 
        } catch(Exception ex) {;}  
        
        LookAndFeelCustomizer.install();
        
        OsirisAppLoader loader = new OsirisAppLoader();
        Platform platform = ClientContext.getCurrentContext().getPlatform();
        loader.load(Thread.currentThread().getContextClassLoader(), buildEnv(), platform);
        
        Opener opener = Inv.lookupOpener(name, params); 
        ControlSupport.initOpener(opener, null);
        UIControllerContext uic = new UIControllerContext( opener.getController() );
        UIControllerPanel panel = new UIControllerPanel( uic );
        JDialog d = new JDialog();
        d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); 
        d.setTitle(opener.getCaption()); 
        d.setModal(true);
        d.setContentPane(panel);
        d.pack();
        d.setVisible(true);         
    }
    
}
