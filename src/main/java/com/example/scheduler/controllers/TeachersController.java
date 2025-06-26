package com.example.scheduler.controllers;

import com.example.scheduler.dao.AppDAO;
import com.example.scheduler.models.Teacher;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class TeachersController {
    // --- Table ---
    @FXML private TableView<Teacher> teachersTable;
    @FXML private TableColumn<Teacher, String> firstNameColumn;
    @FXML private TableColumn<Teacher, String> lastNameColumn;
    @FXML private TableColumn<Teacher, String> emailColumn;
    @FXML private TableColumn<Teacher, String> phoneColumn;
    @FXML private TableColumn<Teacher, String> degreeColumn;

    // --- Form Fields ---
    @FXML private JFXTextField firstNameField;
    @FXML private JFXTextField lastNameField;
    @FXML private JFXTextField emailField;
    @FXML private JFXTextField phoneNumberField;
    @FXML private JFXTextField degreeField;
    @FXML private JFXTextField majorField;
    @FXML private JFXTextField addressField;

    // --- Filter Fields ---
    @FXML private JFXTextField filterFirstNameField;
    @FXML private JFXTextField filterLastNameField;
    @FXML private JFXTextField filterMajorField;

    // --- Data Lists ---
    private final AppDAO dao = new AppDAO();
    private final ObservableList<Teacher> masterTeacherList = FXCollections.observableArrayList();
    private FilteredList<Teacher> filteredTeacherList;

    @FXML
    private void initialize() {
        // Setup Filtering
        filteredTeacherList = new FilteredList<>(masterTeacherList, p -> true);
        filterFirstNameField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        filterLastNameField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        filterMajorField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());

        // Setup Table Columns
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        degreeColumn.setCellValueFactory(new PropertyValueFactory<>("degree"));

        loadTeacherData();
        teachersTable.setItems(filteredTeacherList); // Bind table to the filtered list

        teachersTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> populateForm(newSelection));
    }

    private void loadTeacherData() {
        masterTeacherList.setAll(dao.getAllTeachers());
    }

    private void applyFilters() {
        String firstNameFilter = filterFirstNameField.getText().toLowerCase();
        String lastNameFilter = filterLastNameField.getText().toLowerCase();
        String majorFilter = filterMajorField.getText().toLowerCase();

        filteredTeacherList.setPredicate(teacher -> {
            boolean firstNameMatch = firstNameFilter.isEmpty() || teacher.getFirstName().toLowerCase().contains(firstNameFilter);
            boolean lastNameMatch = lastNameFilter.isEmpty() || teacher.getLastName().toLowerCase().contains(lastNameFilter);
            boolean majorMatch = majorFilter.isEmpty() || (teacher.getMajor() != null && teacher.getMajor().toLowerCase().contains(majorFilter));

            return firstNameMatch && lastNameMatch && majorMatch;
        });
    }

    private void populateForm(Teacher teacher) {
        if (teacher == null) {
            clearFields();
            return;
        }
        firstNameField.setText(teacher.getFirstName());
        lastNameField.setText(teacher.getLastName());
        emailField.setText(teacher.getEmail());
        phoneNumberField.setText(teacher.getPhoneNumber());
        degreeField.setText(teacher.getDegree());
        majorField.setText(teacher.getMajor());
        addressField.setText(teacher.getAddress());
    }

    @FXML private void handleAddButton() {
        if (firstNameField.getText().isEmpty() || lastNameField.getText().isEmpty()) return;
        Teacher newTeacher = new Teacher(0, firstNameField.getText(), lastNameField.getText(), emailField.getText(), phoneNumberField.getText(), degreeField.getText(), majorField.getText(), addressField.getText());
        dao.addTeacher(newTeacher);
        loadTeacherData();
        clearFields();
    }

    @FXML private void handleUpdateButton() {
        Teacher selected = teachersTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        Teacher updatedTeacher = new Teacher(selected.getId(), firstNameField.getText(), lastNameField.getText(), emailField.getText(), phoneNumberField.getText(), degreeField.getText(), majorField.getText(), addressField.getText());
        dao.updateTeacher(updatedTeacher);
        loadTeacherData();
        clearFields();
    }

    @FXML private void handleDeleteButton() {
        Teacher selected = teachersTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            dao.deleteTeacher(selected.getId());
            loadTeacherData();
            clearFields();
        }
    }

    @FXML private void handleClearButton() {
        clearFields();
    }

    private void clearFields() {
        teachersTable.getSelectionModel().clearSelection();
        firstNameField.clear();
        lastNameField.clear();
        emailField.clear();
        phoneNumberField.clear();
        degreeField.clear();
        majorField.clear();
        addressField.clear();
    }
}