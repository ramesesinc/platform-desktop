/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test.web;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author ramesesinc
 */
public class JFXApp extends Application {

    public void start(Stage stage) throws Exception {
        System.out.println(System.getProperty("javafx.runtime.version"));
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
