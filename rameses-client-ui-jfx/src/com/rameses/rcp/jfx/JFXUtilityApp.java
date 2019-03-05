/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.jfx;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author wflores
 */
public class JFXUtilityApp extends Application {

    public void start(Stage stage) throws Exception {
        stage.setWidth(0);
        stage.setHeight(0);
        stage.setX(Double.MAX_VALUE);
        stage.setY(Double.MAX_VALUE);
        stage.initStyle(StageStyle.UTILITY); 
        stage.show(); 
    } 
    
    public static void main(String[] args) {
        Application.launch(args); 
    }    
}
