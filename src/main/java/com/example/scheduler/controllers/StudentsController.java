package com.example.scheduler.controllers;

import com.example.scheduler.dao.AppDAO;
import com.example.scheduler.models.Student;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class StudentsController {

    @FXML private TableView<Student> studentsTable;
    @FXML private TableColumn<Student, String> nameColumn;
    @FXML private TableColumn<Student, String> majorColumn;
    @FXML private TextField nameField;
    @FXML private TextField majorField;

    private final AppDAO dao = new AppDAO();
    private final ObservableList<Student> studentList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        majorColumn.setCellValueFactory(new PropertyValueFactory<>("major"));
        loadStudentData();
        studentsTable.setItems(studentList);

        // Add listener to populate form on table row selection
        studentsTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        nameField.setText(newSelection.getName());
                        majorField.setText(newSelection.getMajor());
                    }
                });
    }

    private void loadStudentData() {
        studentList.setAll(dao.getAllStudents());
    }

    @FXML
    private void handleAddButton() {
        if (nameField.getText().isEmpty() || majorField.getText().isEmpty()) return;
        dao.addStudent(new Student(0, nameField.getText(), majorField.getText()));
        loadStudentData();
        clearFields();
    }

    @FXML
    private void handleUpdateButton() {
        Student selected = studentsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            dao.updateStudent(new Student(selected.getId(), nameField.getText(), majorField.getText()));
            loadStudentData();
            clearFields();
        }
    }

    @FXML
    private void handleDeleteButton() {
        Student selected = studentsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            dao.deleteStudent(selected.getId());
            loadStudentData();
            clearFields();
        }
    }

    @FXML
    private void handleClearButton() {
        clearFields();
    }

    private void clearFields() {
        nameField.clear();
        majorField.clear();
        studentsTable.getSelectionModel().clearSelection();
    }
}