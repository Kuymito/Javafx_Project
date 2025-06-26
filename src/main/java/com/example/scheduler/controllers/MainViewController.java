package com.example.scheduler.controllers;

import com.example.scheduler.Main;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class MainViewController {
    @FXML private StackPane contentPane;
    @FXML private ImageView profileImageView;
    @FXML private JFXButton logoutButton;

    @FXML private JFXButton classesButton;
    @FXML private JFXButton teachersButton;
    @FXML private JFXButton studentsButton;

    private List<JFXButton> sidebarButtons;
    private Main mainApp;

    @FXML
    private void initialize() {
        sidebarButtons = List.of(classesButton, teachersButton, studentsButton, logoutButton);
        if (profileImageView != null) {
            Circle clip = new Circle(35, 35, 35);
            profileImageView.setClip(clip);
        }
        handleShowClassesView();
    }

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }

    public void setProfilePicture(String imagePath) {
        Image image = null;
        if (imagePath != null && !imagePath.isEmpty()) {
            try {
                if (imagePath.startsWith("http")) {
                    image = new Image(imagePath, true);
                } else {
                    URL resourceUrl = getClass().getResource(imagePath);
                    if (resourceUrl != null) {
                        image = new Image(resourceUrl.toExternalForm());
                    } else {
                        System.err.println("Local profile image not found: " + imagePath);
                    }
                }
            } catch (Exception e) {
                System.err.println("Could not load profile image: " + e.getMessage());
            }
        }

        if (image != null) {
            profileImageView.setImage(image);
        } else {

            URL defaultUrl = getClass().getResource("/images/default-profile.png");
            if(defaultUrl != null) {
                profileImageView.setImage(new Image(defaultUrl.toExternalForm()));
            }
        }

        Circle clip = new Circle(35, 35, 35);
        profileImageView.setClip(clip);
    }

    @FXML private void handleShowClassesView() {
        loadView("/fxml/classes-view.fxml");
        setActiveButton(classesButton);
    }

    @FXML private void handleShowTeachersView() {
        loadView("/fxml/teachers-view.fxml");
        setActiveButton(teachersButton);
    }

    @FXML private void handleShowStudentsView() {
        loadView("/fxml/students-view.fxml");
        setActiveButton(studentsButton);
    }

    private void loadView(String fxmlPath) {
        try {
            Node view = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentPane.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Highlights the active sidebar button and styles the rest as inactive.
     * @param activeButton The button to be styled as active.
     */
    private void setActiveButton(JFXButton activeButton) {
        for (JFXButton button : sidebarButtons) {
            button.getStyleClass().remove("active");
        }
        activeButton.getStyleClass().add("active");
    }

    @FXML
    private void handleLogoutButton() {
        mainApp.showLoginScreen();
    }
}
