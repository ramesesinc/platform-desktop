/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.jfx;

import javafx.application.Application;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author wflores
 */
public class JFXUtilityApp extends Application {

    public void start(Stage stage) throws Exception {
        System.out.println("start( Stage )...");
        stage.setWidth(1);
        stage.setHeight(1);
        stage.setX(-200);
        stage.setY(-200);
        stage.initStyle(StageStyle.UTILITY); 
        System.out.println("before show stage...");
        stage.show(); 
        System.out.println("after show stage...");
    } 
    
    public static void main(String[] args) { 
        String[] fonts = new String[]{
            "fonts/OpenSans-Bold-webfont.ttf", 
            "fonts/OpenSans-Regular-webfont.ttf"
        };
        for ( String str : fonts ) { 
            try { 
                Font.loadFont(JFXUtilityApp.class.getClassLoader().getResourceAsStream( str ), 11); 
            } catch(Throwable t) {
                t.printStackTrace();
            }
        }
        
        System.out.println("launch jfx application...");
        Application.launch(args); 
    }    
}
