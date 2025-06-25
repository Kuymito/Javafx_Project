package com.example.scheduler.controllers;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class MainViewController {
    // --- FXML View Components ---
    @FXML private StackPane contentPane;
    @FXML private ImageView profileImageView;

    // --- FXML Sidebar Buttons ---
    @FXML private JFXButton classesButton;
    @FXML private JFXButton teachersButton;
    @FXML private JFXButton studentsButton;

    private List<JFXButton> sidebarButtons;

    @FXML
    private void initialize() {
        sidebarButtons = List.of(classesButton, teachersButton, studentsButton);

        if (profileImageView != null) {
            Circle clip = new Circle(35, 35, 35);
            profileImageView.setClip(clip);
        }

        handleShowClassesView();
    }

    public void setProfilePicture(String imagePath) {
        Image image = null;
        if (imagePath != null && !imagePath.isEmpty()) {
            try {
                if (imagePath.startsWith("http")) {
                    // It's a web URL
                    image = new Image(imagePath, true); // true for background loading
                } else {
                    // It's a local resource path
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
            // Set a default placeholder image if loading fails or path is null
            URL defaultUrl = getClass().getResource("/images/default-profile.png");
            if(defaultUrl != null) {
                profileImageView.setImage(new Image(defaultUrl.toExternalForm()));
            }
        }

        // Apply a circular clip to the ImageView
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
}
