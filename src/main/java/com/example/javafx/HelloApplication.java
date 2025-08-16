/*
FA24-BSE-180
FA24-BSE-006
FA24-BSE-144
FA24-BSE-075
 */
package com.example.javafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HelloApplication extends Application {

    private static Stage primaryStage; // Main stage of the application


    @Override
    public void start(Stage stage) throws Exception {
        // Load admin statistics (total revenue, customers, products sold) from file when program starts
          AppData.loadFromFile("adminData.txt");

        // Set the primary stage and load the first scene (role selection)
        primaryStage = stage;
        changeScene("role-selection.fxml");

        // Set the application title and full screen mode
        primaryStage.setTitle("Shopping Cart");
        primaryStage.setFullScreen(true);
        primaryStage.show();
        primaryStage.setFullScreenExitHint("");

    }

    // This method changes the current scene to the one specified by the FXML file
    public static void changeScene(String fxmlFile) throws Exception {
        Parent root = FXMLLoader.load(HelloApplication.class.getResource(fxmlFile));
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true); // Ensures fullscreen display
    }

    // Launches the JavaFX application
    public static void main(String[] args) {
        launch();
    }
}
