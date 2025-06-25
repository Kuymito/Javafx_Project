package com.example.scheduler.controllers;

import com.example.scheduler.dao.AppDAO;
import com.example.scheduler.models.Teacher;
import com.jfoenix.controls.JFXTextField; // Import JFXTextField
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class TeachersController {
    @FXML private TableView<Teacher> teachersTable;
    @FXML private TableColumn<Teacher, String> nameColumn;
    @FXML private TableColumn<Teacher, String> subjectColumn;
    @FXML private JFXTextField nameField; // Use JFXTextField
    @FXML private JFXTextField subjectField; // Use JFXTextField

    private final AppDAO dao = new AppDAO();
    private final ObservableList<Teacher> teacherList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        subjectColumn.setCellValueFactory(new PropertyValueFactory<>("subject"));
        loadTeacherData();
        teachersTable.setItems(teacherList);
        teachersTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        nameField.setText(newSelection.getName());
                        subjectField.setText(newSelection.getSubject());
                    }
                });
    }

    private void loadTeacherData() {
        teacherList.setAll(dao.getAllTeachers());
    }

    @FXML private void handleAddButton() {
        if(nameField.getText().isEmpty() || subjectField.getText().isEmpty()) return;
        dao.addTeacher(new Teacher(0, nameField.getText(), subjectField.getText()));
        loadTeacherData();
        clearFields();
    }

    @FXML private void handleUpdateButton() {
        Teacher selected = teachersTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            dao.updateTeacher(new Teacher(selected.getId(), nameField.getText(), subjectField.getText()));
            loadTeacherData();
            clearFields();
        }
    }

    @FXML private void handleDeleteButton() {
        Teacher selected = teachersTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            dao.deleteTeacher(selected.getId());
            loadTeacherData();
            clearFields();
        }
    }

    @FXML private void handleClearButton() { clearFields(); }

    private void clearFields() {
        nameField.clear();
        subjectField.clear();
        teachersTable.getSelectionModel().clearSelection();
    }
}