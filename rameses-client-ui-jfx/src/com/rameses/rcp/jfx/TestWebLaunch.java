/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.jfx;

import java.awt.event.WindowAdapter;
import java.lang.reflect.Field;
import java.util.HashMap;
import javafx.stage.WindowEvent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;



/**
 *
 * @author elmonazareno
 */
public class TestWebLaunch {
    
    public static void view() throws Exception {
        /*
        try {
            final Class<?> macFontFinderClass = Class.forName("com.sun.t2k.MacFontFinder");
            final Field psNameToPathMap = macFontFinderClass.getDeclaredField("psNameToPathMap");

            psNameToPathMap.setAccessible(true);
            if (psNameToPathMap.get(null) == null) {
                psNameToPathMap.set(null, new HashMap<String, String>());
            }

            final Field allAvailableFontFamilies = macFontFinderClass.getDeclaredField("allAvailableFontFamilies");
            allAvailableFontFamilies.setAccessible(true);
            if (allAvailableFontFamilies.get(null) == null) {
                allAvailableFontFamilies.set(null, new String[] {});
            }
        } 
        catch (Throwable e) {
            e.printStackTrace();
        } 
        */        
        final WebViewPane vw = new WebViewPane();
        JDialog d = new JDialog();
        d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); 
        d.setTitle("Test JavaFX Web View"); 
        d.setModal(true);
        d.setContentPane( vw ); 
        d.setSize(500, 400);
        d.addWindowListener(new WindowAdapter() {
            public void windowOpened(WindowEvent we) {
                vw.loadView("<h1>Hello World</h1>");
            }
        });
        d.setVisible(true);
    }
    
}
