// File: src/main/java/com/example/scheduler/controllers/LoginController.java
package com.example.scheduler.controllers;

import com.example.scheduler.dao.AppDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Label statusLabel;

    private final AppDAO appDAO = new AppDAO();

    @FXML
    private void handleLoginButtonAction() {
        if (appDAO.validateUser(usernameField.getText(), passwordField.getText())) {
            openMainWindow();
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.close();
        } else {
            statusLabel.setText("Invalid username or password.");
        }
    }

    private void openMainWindow() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/main-view.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Scheduler Dashboard");
            stage.setScene(new Scene(root, 1200, 800));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}