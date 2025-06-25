package com.example.scheduler.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;

import java.io.IOException;

public class MainViewController {
    @FXML private StackPane contentPane;
    @FXML private ImageView profileImageView;

    @FXML
    private void initialize() {
        // Make the profile picture circular
        if (profileImageView != null) {
            Circle clip = new Circle(35, 35, 35); // CenterX, CenterY, Radius
            profileImageView.setClip(clip);
        }

        // Load the initial view
        handleShowClassesView();
    }

    public void setProfilePicture(String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            try {
                // Load the image from the URL
                Image image = new Image(imageUrl, true); // true for background loading
                profileImageView.setImage(image);

                // Apply a circular clip to the ImageView
                Circle clip = new Circle(35, 35, 35);
                profileImageView.setClip(clip);
            } catch (Exception e) {
                System.err.println("Could not load profile image: " + e.getMessage());
                // Optionally set a default placeholder image on error
                profileImageView.setImage(new Image("https://i.imgur.com/S8w42gM.png"));
            }
        }
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