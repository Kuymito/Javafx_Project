package com.example.scheduler.controllers;

import com.example.scheduler.dao.AppDAO;
import com.example.scheduler.models.Schedule;
import com.example.scheduler.models.Student;
import com.example.scheduler.models.Teacher;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

import java.util.List;

public class ClassesController {

    // Main Schedule Table
    @FXML private TableView<Schedule> scheduleTable;

    // Enrolled Students Table
    @FXML private TableView<Student> enrolledStudentsTable;
    @FXML private TableColumn<Student, String> studentNameColumn;
    @FXML private Label enrollmentLabel;

    // Enrollment Management
    @FXML private JFXComboBox<Student> allStudentsComboBox;
    @FXML private JFXButton enrollButton;
    @FXML private JFXButton unenrollButton;

    private final AppDAO dao = new AppDAO();
    private final ObservableList<Schedule> scheduleList = FXCollections.observableArrayList();
    private final ObservableList<Student> enrolledStudentList = FXCollections.observableArrayList();
    private final ObservableList<Student> allStudentsList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        setupScheduleTable();
        setupEnrolledStudentsTable();
        setupAllStudentsComboBox();

        // Load data from the database
        loadScheduleData();
        loadAllStudentsData();

        // Add a listener to update the enrolled students when a class is selected
        scheduleTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        loadEnrolledStudents(newSelection.getId());
                        enrollmentLabel.setText("Enrolled in: " + newSelection.getCourseName());
                    } else {
                        enrolledStudentList.clear();
                        enrollmentLabel.setText("Select a Class to See Enrollments");
                    }
                });
    }

    private void setupScheduleTable() {
        // Create and add columns to the schedule table dynamically
        TableColumn<Schedule, String> courseCol = new TableColumn<>("Course Name");
        courseCol.setCellValueFactory(new PropertyValueFactory<>("courseName"));

        TableColumn<Schedule, String> teacherCol = new TableColumn<>("Teacher");
        teacherCol.setCellValueFactory(new PropertyValueFactory<>("teacherName"));

        TableColumn<Schedule, String> dayCol = new TableColumn<>("Day");
        dayCol.setCellValueFactory(new PropertyValueFactory<>("dayOfWeek"));

        TableColumn<Schedule, String> timeCol = new TableColumn<>("Time");
        timeCol.setCellValueFactory(new PropertyValueFactory<>("startTime"));

        scheduleTable.getColumns().setAll(courseCol, teacherCol, dayCol, timeCol);
        scheduleTable.setItems(scheduleList);
    }

    private void setupEnrolledStudentsTable() {
        studentNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        enrolledStudentsTable.setItems(enrolledStudentList);
    }

    private void setupAllStudentsComboBox() {
        allStudentsComboBox.setItems(allStudentsList);
        allStudentsComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Student student) {
                return student == null ? "" : student.getName();
            }

            @Override
            public Student fromString(String string) {
                return allStudentsList.stream().filter(s -> s.getName().equals(string)).findFirst().orElse(null);
            }
        });
    }

    private void loadScheduleData() {
        scheduleList.setAll(dao.getAllSchedules());
    }

    private void loadAllStudentsData() {
        allStudentsList.setAll(dao.getAllStudents());
    }

    private void loadEnrolledStudents(int scheduleId) {
        enrolledStudentList.setAll(dao.getStudentsForSchedule(scheduleId));
    }

    @FXML
    private void handleEnrollButton() {
        Schedule selectedSchedule = scheduleTable.getSelectionModel().getSelectedItem();
        Student selectedStudent = allStudentsComboBox.getSelectionModel().getSelectedItem();

        if (selectedSchedule != null && selectedStudent != null) {
            dao.enrollStudent(selectedStudent.getId(), selectedSchedule.getId());
            loadEnrolledStudents(selectedSchedule.getId()); // Refresh the list
        }
    }

    @FXML
    private void handleUnenrollButton() {
        Schedule selectedSchedule = scheduleTable.getSelectionModel().getSelectedItem();
        Student studentToUnenroll = enrolledStudentsTable.getSelectionModel().getSelectedItem();

        if (selectedSchedule != null && studentToUnenroll != null) {
            dao.unenrollStudent(studentToUnenroll.getId(), selectedSchedule.getId());
            loadEnrolledStudents(selectedSchedule.getId()); // Refresh the list
        }
    }
}