package com.example.scheduler.controllers;

import com.example.scheduler.Main;
import com.example.scheduler.dao.AppDAO;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class LoginController {
    @FXML private JFXTextField usernameField;
    @FXML private JFXPasswordField passwordField;
    @FXML private JFXButton loginButton;
    @FXML private Label statusLabel;

    private Main mainApp;
    private final AppDAO appDAO = new AppDAO();

    @FXML
    private void handleLoginButtonAction() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (appDAO.validateUser(username, password)) {
            String profilePicPath = appDAO.getProfilePicturePath(username);

            mainApp.showMainScreen(profilePicPath);
        } else {
            statusLabel.setText("Invalid username or password.");
        }
    }

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }


    private void openMainWindow(String profilePicPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main-view.fxml"));
            Parent root = loader.load();

            // Get the controller and set the profile picture
            MainViewController controller = loader.getController();
            controller.setProfilePicture(profilePicPath);

            Stage stage = new Stage();
            stage.setTitle("Scheduler Dashboard");
            stage.setScene(new Scene(root, 1200, 800));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}