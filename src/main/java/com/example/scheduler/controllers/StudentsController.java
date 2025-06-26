package com.example.scheduler.controllers;

import com.example.scheduler.dao.AppDAO;
import com.example.scheduler.models.Student;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class StudentsController {

    // --- Table ---
    @FXML private TableView<Student> studentsTable;
    @FXML private TableColumn<Student, String> firstNameColumn;
    @FXML private TableColumn<Student, String> lastNameColumn;
    @FXML private TableColumn<Student, String> emailColumn;
    @FXML private TableColumn<Student, String> majorColumn;
    @FXML private TableColumn<Student, String> semesterColumn;

    // --- Form Fields ---
    @FXML private JFXTextField firstNameField;
    @FXML private JFXTextField lastNameField;
    @FXML private JFXTextField emailField;
    @FXML private JFXTextField phoneNumberField;
    @FXML private JFXTextField majorField;
    @FXML private JFXTextField semesterField;
    @FXML private JFXTextField addressField;

    // --- Filter Fields ---
    @FXML private JFXTextField filterFirstNameField;
    @FXML private JFXTextField filterLastNameField;
    @FXML private JFXTextField filterMajorField;

    // --- Data Lists ---
    private final AppDAO dao = new AppDAO();
    private final ObservableList<Student> masterStudentList = FXCollections.observableArrayList();
    private FilteredList<Student> filteredStudentList;

    @FXML
    private void initialize() {
        // Setup Filtering
        filteredStudentList = new FilteredList<>(masterStudentList, p -> true);
        filterFirstNameField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        filterLastNameField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        filterMajorField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());

        // Setup Table Columns
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        majorColumn.setCellValueFactory(new PropertyValueFactory<>("major"));
        semesterColumn.setCellValueFactory(new PropertyValueFactory<>("semester"));

        loadStudentData();
        studentsTable.setItems(filteredStudentList); // Bind table to the filtered list

        studentsTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> populateForm(newSelection));
    }

    private void loadStudentData() {
        masterStudentList.setAll(dao.getAllStudents());
    }

    private void applyFilters() {
        String firstNameFilter = filterFirstNameField.getText().toLowerCase();
        String lastNameFilter = filterLastNameField.getText().toLowerCase();
        String majorFilter = filterMajorField.getText().toLowerCase();

        filteredStudentList.setPredicate(student -> {
            boolean firstNameMatch = firstNameFilter.isEmpty() || student.getFirstName().toLowerCase().contains(firstNameFilter);
            boolean lastNameMatch = lastNameFilter.isEmpty() || student.getLastName().toLowerCase().contains(lastNameFilter);
            boolean majorMatch = majorFilter.isEmpty() || (student.getMajor() != null && student.getMajor().toLowerCase().contains(majorFilter));

            return firstNameMatch && lastNameMatch && majorMatch;
        });
    }

    private void populateForm(Student student) {
        if (student == null) {
            clearFields();
            return;
        }
        firstNameField.setText(student.getFirstName());
        lastNameField.setText(student.getLastName());
        emailField.setText(student.getEmail());
        phoneNumberField.setText(student.getPhoneNumber());
        majorField.setText(student.getMajor());
        semesterField.setText(student.getSemester());
        addressField.setText(student.getAddress());
    }

    @FXML
    private void handleAddButton() {
        if (firstNameField.getText().isEmpty() || lastNameField.getText().isEmpty()) return;
        Student newStudent = new Student(0, firstNameField.getText(), lastNameField.getText(), emailField.getText(), phoneNumberField.getText(), majorField.getText(), semesterField.getText(), addressField.getText());
        dao.addStudent(newStudent);
        loadStudentData();
        clearFields();
    }

    @FXML
    private void handleUpdateButton() {
        Student selected = studentsTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        Student updatedStudent = new Student(selected.getId(), firstNameField.getText(), lastNameField.getText(), emailField.getText(), phoneNumberField.getText(), majorField.getText(), semesterField.getText(), addressField.getText());
        dao.updateStudent(updatedStudent);
        loadStudentData();
        clearFields();
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
        studentsTable.getSelectionModel().clearSelection();
        firstNameField.clear();
        lastNameField.clear();
        emailField.clear();
        phoneNumberField.clear();
        majorField.clear();
        semesterField.clear();
        addressField.clear();
    }
}
