// File: src/main/java/com/example/scheduler/controllers/ClassesController.java
package com.example.scheduler.controllers;

import com.example.scheduler.dao.AppDAO;
import com.example.scheduler.models.Schedule;
import com.example.scheduler.models.Teacher;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

public class ClassesController {

    @FXML private TableView<Schedule> scheduleTable;
    @FXML private TextField courseNameField;
    @FXML private TextField roomNumberField;
    @FXML private TextField dayOfWeekField;
    @FXML private TextField startTimeField;
    @FXML private TextField endTimeField;
    @FXML private ComboBox<Teacher> teacherComboBox; // ComboBox to select a teacher

    private final AppDAO dao = new AppDAO();
    private final ObservableList<Schedule> scheduleList = FXCollections.observableArrayList();
    private final ObservableList<Teacher> teacherList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // Setup table columns - Use the actual property names from your Schedule model
        setupTableColumns();

        // Load data from the database
        loadScheduleData();
        loadTeacherData();

        // Populate the ComboBox
        teacherComboBox.setItems(teacherList);
        setupTeacherComboBoxConverter();

        // Populate the table
        scheduleTable.setItems(scheduleList);

        // Add a listener to populate the form when a row is selected
        scheduleTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> populateForm(newSelection));
    }

    private void setupTableColumns() {
        // Create columns dynamically. Assumes your Schedule model has these properties.
        TableColumn<Schedule, String> courseCol = new TableColumn<>("Course Name");
        courseCol.setCellValueFactory(new PropertyValueFactory<>("courseName"));

        TableColumn<Schedule, String> roomCol = new TableColumn<>("Room No.");
        roomCol.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));

        // This is important: it assumes your Schedule model has a teacherName property for display
        TableColumn<Schedule, String> teacherCol = new TableColumn<>("Teacher");
        teacherCol.setCellValueFactory(new PropertyValueFactory<>("teacherName"));

        TableColumn<Schedule, String> dayCol = new TableColumn<>("Day of Week");
        dayCol.setCellValueFactory(new PropertyValueFactory<>("dayOfWeek"));

        TableColumn<Schedule, String> startCol = new TableColumn<>("Start Time");
        startCol.setCellValueFactory(new PropertyValueFactory<>("startTime"));

        TableColumn<Schedule, String> endCol = new TableColumn<>("End Time");
        endCol.setCellValueFactory(new PropertyValueFactory<>("endTime"));

        scheduleTable.getColumns().setAll(courseCol, roomCol, teacherCol, dayCol, startCol, endCol);
        scheduleTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void setupTeacherComboBoxConverter() {
        // This tells the ComboBox how to display a Teacher object (by showing its name)
        teacherComboBox.setConverter(new StringConverter<Teacher>() {
            @Override
            public String toString(Teacher teacher) {
                return teacher == null ? "" : teacher.getName();
            }

            @Override
            public Teacher fromString(String string) {
                return teacherComboBox.getItems().stream()
                        .filter(t -> t.getName().equals(string))
                        .findFirst()
                        .orElse(null);
            }
        });
    }

    private void loadScheduleData() {
        // IMPORTANT: You need to update your DAO to fetch schedules with teacher names (using a JOIN)
        // For now, we assume AppDAO.getAllSchedules() returns what we need.
        scheduleList.setAll(dao.getAllSchedules());
    }

    private void loadTeacherData() {
        teacherList.setAll(dao.getAllTeachers());
    }

    private void populateForm(Schedule schedule) {
        if (schedule == null) {
            clearForm();
            return;
        }
        courseNameField.setText(schedule.getCourseName());
        roomNumberField.setText(schedule.getRoomNumber());
        dayOfWeekField.setText(schedule.getDayOfWeek());
        // You might need to format time fields if they are not strings
        startTimeField.setText(schedule.getStartTime());
        endTimeField.setText(schedule.getEndTime());

        // Find and select the correct teacher in the ComboBox
        teacherComboBox.getSelectionModel().select(
                teacherList.stream()
                        .filter(t -> t.getName().equals(schedule.getTeacherName()))
                        .findFirst()
                        .orElse(null)
        );
    }

    @FXML
    private void handleAddButton() {
        Teacher selectedTeacher = teacherComboBox.getSelectionModel().getSelectedItem();
        if (selectedTeacher == null) {
            // You should show an alert to the user here
            System.err.println("Add Error: No teacher selected.");
            return;
        }
        if (courseNameField.getText().isEmpty() || startTimeField.getText().isEmpty() || endTimeField.getText().isEmpty()) {
            System.err.println("Add Error: Required fields are empty.");
            return;
        }

        // We no longer store the teacher's name in the schedule object itself for adding.
        // The name is handled by the JOIN query when displaying.
        Schedule newSchedule = new Schedule(0,
                courseNameField.getText(),
                roomNumberField.getText(),
                null, // Teacher name is null here, it will be fetched by the JOIN
                dayOfWeekField.getText(),
                startTimeField.getText(),
                endTimeField.getText());

        // This is the corrected method call, passing the schedule and the teacher's ID
        dao.addSchedule(newSchedule, selectedTeacher.getId());

        loadScheduleData(); // Refresh the table
        clearForm();
    }

    @FXML
    private void handleUpdateButton() {
        Schedule selectedSchedule = scheduleTable.getSelectionModel().getSelectedItem();
        Teacher selectedTeacher = teacherComboBox.getSelectionModel().getSelectedItem();

        if (selectedSchedule == null || selectedTeacher == null) {
            System.err.println("Update Error: No schedule or teacher selected.");
            return;
        }

        Schedule updatedSchedule = new Schedule(
                selectedSchedule.getId(), // Keep the original ID
                courseNameField.getText(),
                roomNumberField.getText(),
                null, // Teacher name is null, will be fetched by JOIN
                dayOfWeekField.getText(),
                startTimeField.getText(),
                endTimeField.getText());

        // Corrected call for the update method
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
            clearForm();
        }
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
    }
}