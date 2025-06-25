package com.example.scheduler;

import com.example.scheduler.db.DatabaseConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Check database connection and create tables if they don't exist
        DatabaseConnection.initializeDatabase();

        Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));

        // Create the scene only ONCE
        Scene scene = new Scene(root, 400, 300);

        // Apply JFoenix theme
        scene.getStylesheets().add(getClass().getResource("/css/jfoenix-theme.css").toExternalForm());

        primaryStage.setTitle("Scheduler Login");

        // Set the scene that you already created
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}