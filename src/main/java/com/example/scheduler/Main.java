// File: src/main/java/com/example/scheduler/Main.java
package com.example.scheduler;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.example.scheduler.db.DatabaseConnection;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Check database connection and create tables if they don't exist
        DatabaseConnection.initializeDatabase();

        Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
        primaryStage.setTitle("Scheduler Login");
        primaryStage.setScene(new Scene(root, 400, 300));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}