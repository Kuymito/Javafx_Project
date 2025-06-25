package com.example.scheduler.controllers;

import com.example.scheduler.dao.AppDAO;
import com.example.scheduler.models.Schedule;
import com.example.scheduler.models.Student;
import com.example.scheduler.models.Teacher;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

public class ClassesController {

    // --- FXML Fields for Class Management ---
    @FXML private JFXTextField courseNameField;
    @FXML private JFXTextField roomNumberField;
    @FXML private JFXComboBox<Teacher> teacherComboBox;
    @FXML private JFXTextField dayOfWeekField;
    @FXML private JFXTextField startTimeField;
    @FXML private JFXTextField endTimeField;

    // --- FXML Fields for Schedule and Enrollment Tables ---
    @FXML private TableView<Schedule> scheduleTable;
    @FXML private TableView<Student> enrolledStudentsTable;
    @FXML private TableColumn<Student, String> studentNameColumn;
    @FXML private Label enrollmentLabel;
    @FXML private JFXComboBox<Student> allStudentsComboBox;
    @FXML private JFXButton enrollButton;
    @FXML private JFXButton unenrollButton;
    @FXML private JFXTextField semesterField;
    @FXML private JFXTextField groupField;
    @FXML private JFXTextField promotionField;

    // --- Data Access and Lists ---
    private final AppDAO dao = new AppDAO();
    private final ObservableList<Schedule> scheduleList = FXCollections.observableArrayList();
    private final ObservableList<Student> enrolledStudentList = FXCollections.observableArrayList();
    private final ObservableList<Student> allStudentsList = FXCollections.observableArrayList();
    private final ObservableList<Teacher> allTeachersList = FXCollections.observableArrayList();


    @FXML
    private void initialize() {
        // Setup for all tables and combo boxes
        setupScheduleTable();
        setupEnrolledStudentsTable();
        setupAllStudentsComboBox();
        setupTeacherComboBox();

        // Load initial data
        loadAllData();

        // Listener to update details when a class is selected
        scheduleTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        // Populate the management form and the enrollment list
                        populateForm(newSelection);
                        loadEnrolledStudents(newSelection.getId());
                        enrollmentLabel.setText("Enrolled in: " + newSelection.getCourseName());
                    } else {
                        // Clear details if no class is selected
                        clearForm();
                        enrolledStudentList.clear();
                        enrollmentLabel.setText("Select a Class");
                    }
                });
    }

    private void loadAllData() {
        loadScheduleData();
        loadAllStudentsData();
        loadAllTeachersData();
    }

    // --- Setup Methods ---
    private void setupScheduleTable() {
        TableColumn<Schedule, String> courseCol = new TableColumn<>("Course Name");
        courseCol.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        TableColumn<Schedule, String> teacherCol = new TableColumn<>("Teacher");
        teacherCol.setCellValueFactory(new PropertyValueFactory<>("teacherName"));
        TableColumn<Schedule, String> dayCol = new TableColumn<>("Day");
        dayCol.setCellValueFactory(new PropertyValueFactory<>("dayOfWeek"));
        TableColumn<Schedule, String> timeCol = new TableColumn<>("Time");
        timeCol.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        TableColumn<Schedule, String> semesterCol = new TableColumn<>("Semester");
        semesterCol.setCellValueFactory(new PropertyValueFactory<>("semester"));
        TableColumn<Schedule, String> groupCol = new TableColumn<>("Group");
        groupCol.setCellValueFactory(new PropertyValueFactory<>("group"));
        TableColumn<Schedule, String> promotionCol = new TableColumn<>("Promotion");
        promotionCol.setCellValueFactory(new PropertyValueFactory<>("promotion"));

        scheduleTable.getColumns().setAll(courseCol, teacherCol, dayCol, timeCol, semesterCol, groupCol, promotionCol);
        scheduleTable.setItems(scheduleList);
    }

    private void setupEnrolledStudentsTable() {
        studentNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        enrolledStudentsTable.setItems(enrolledStudentList);
    }

    private void setupAllStudentsComboBox() {
        allStudentsComboBox.setItems(allStudentsList);
        allStudentsComboBox.setConverter(new StringConverter<>() {
            @Override public String toString(Student student) { return student == null ? "" : student.getName(); }
            @Override public Student fromString(String string) { return null; }
        });
    }

    private void setupTeacherComboBox() {
        teacherComboBox.setItems(allTeachersList);
        teacherComboBox.setConverter(new StringConverter<>() {
            @Override public String toString(Teacher teacher) { return teacher == null ? "" : teacher.getName(); }
            @Override public Teacher fromString(String string) { return null; }
        });
    }


    // --- Data Loading Methods ---
    private void loadScheduleData() { scheduleList.setAll(dao.getAllSchedules()); }
    private void loadAllStudentsData() { allStudentsList.setAll(dao.getAllStudents()); }
    private void loadAllTeachersData() { allTeachersList.setAll(dao.getAllTeachers()); }
    private void loadEnrolledStudents(int scheduleId) { enrolledStudentList.setAll(dao.getStudentsForSchedule(scheduleId)); }

    // --- Class Management Handlers ---
    @FXML
    private void handleAddButton() {
        Teacher selectedTeacher = teacherComboBox.getSelectionModel().getSelectedItem();
        if (selectedTeacher == null || courseNameField.getText().isEmpty()) {
            // Add user feedback here (e.g., an alert)
            return;
        }
        Schedule newSchedule = new Schedule(0, courseNameField.getText(), roomNumberField.getText(), null, dayOfWeekField.getText(), startTimeField.getText(), endTimeField.getText(), semesterField.getText(), groupField.getText(), promotionField.getText());
        dao.addSchedule(newSchedule, selectedTeacher.getId());
        loadScheduleData();
        clearForm();
    }

    @FXML
    private void handleUpdateButton() {
        Schedule selectedSchedule = scheduleTable.getSelectionModel().getSelectedItem();
        Teacher selectedTeacher = teacherComboBox.getSelectionModel().getSelectedItem();
        if (selectedSchedule == null || selectedTeacher == null) return;

        Schedule updatedSchedule = new Schedule(selectedSchedule.getId(), courseNameField.getText(), roomNumberField.getText(), null, dayOfWeekField.getText(), startTimeField.getText(), endTimeField.getText(), semesterField.getText(), groupField.getText(), promotionField.getText());
        dao.updateSchedule(updatedSchedule, selectedTeacher.getId());
        loadScheduleData();
        clearForm();
    }

    @FXML
    private void handleDeleteButton() {
        Schedule selected = scheduleTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            dao.deleteSchedule(selected.getId());
            loadScheduleData();
        }
    }

    // --- Enrollment Handlers ---
    @FXML
    private void handleEnrollButton() {
        Schedule selectedSchedule = scheduleTable.getSelectionModel().getSelectedItem();
        Student selectedStudent = allStudentsComboBox.getSelectionModel().getSelectedItem();
        if (selectedSchedule != null && selectedStudent != null) {
            dao.enrollStudent(selectedStudent.getId(), selectedSchedule.getId());
            loadEnrolledStudents(selectedSchedule.getId());
        }
    }

    @FXML
    private void handleUnenrollButton() {
        Schedule selectedSchedule = scheduleTable.getSelectionModel().getSelectedItem();
        Student studentToUnenroll = enrolledStudentsTable.getSelectionModel().getSelectedItem();
        if (selectedSchedule != null && studentToUnenroll != null) {
            dao.unenrollStudent(studentToUnenroll.getId(), selectedSchedule.getId());
            loadEnrolledStudents(selectedSchedule.getId());
        }
    }

    // --- Form Helper Methods ---
    private void populateForm(Schedule schedule) {
        if (schedule == null) {
            clearForm();
            return;
        }
        courseNameField.setText(schedule.getCourseName());
        roomNumberField.setText(schedule.getRoomNumber());
        dayOfWeekField.setText(schedule.getDayOfWeek());
        startTimeField.setText(schedule.getStartTime());
        endTimeField.setText(schedule.getEndTime());
        teacherComboBox.getSelectionModel().select(
                allTeachersList.stream()
                        .filter(t -> t.getName().equals(schedule.getTeacherName()))
                        .findFirst()
                        .orElse(null)
        );
        semesterField.setText(schedule.getSemester());
        groupField.setText(schedule.getGroup());
        promotionField.setText(schedule.getPromotion());
    }

    @FXML
    private void clearForm() {
        courseNameField.clear();
        roomNumberField.clear();
        dayOfWeekField.clear();
        startTimeField.clear();
        endTimeField.clear();
        teacherComboBox.getSelectionModel().clearSelection();
        scheduleTable.getSelectionModel().clearSelection();
        semesterField.clear();
        groupField.clear();
        promotionField.clear();
    }
}