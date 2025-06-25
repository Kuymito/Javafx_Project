// File: src/main/java/com/example/scheduler/controllers/MainViewController.java
package com.example.scheduler.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import java.io.IOException;

public class MainViewController {
    @FXML private StackPane contentPane;

    @FXML private void initialize() {
        handleShowClassesView();
    }

    @FXML private void handleShowClassesView() { loadView("/fxml/classes-view.fxml"); }
    @FXML private void handleShowTeachersView() { loadView("/fxml/teachers-view.fxml"); }
    @FXML private void handleShowStudentsView() { loadView("/fxml/students-view.fxml"); }

    private void loadView(String fxmlPath) {
        try {
            Node view = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentPane.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}