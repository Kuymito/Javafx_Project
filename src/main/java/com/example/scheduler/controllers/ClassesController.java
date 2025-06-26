package com.example.scheduler.controllers;

import com.example.scheduler.dao.AppDAO;
import com.example.scheduler.models.Schedule;
import com.example.scheduler.models.Student;
import com.example.scheduler.models.Teacher;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static com.itextpdf.svg.converter.SvgConverter.createPdf;

public class ClassesController {

    // --- FXML Fields ---
    @FXML private JFXTextField filterCourseField, filterTeacherField, filterSemesterField, filterGroupField;
    @FXML private JFXTextField courseNameField, roomNumberField, semesterField, groupField, promotionField;
    @FXML private JFXComboBox<Teacher> teacherComboBox;
    @FXML private JFXComboBox<String> dayOfWeekComboBox, timeSlotComboBox;
    @FXML private TableView<Schedule> scheduleTable;
    @FXML private TableView<Student> enrolledStudentsTable;
    @FXML private TableColumn<Student, String> studentNameColumn;
    @FXML private Label enrollmentLabel;
    @FXML private JFXComboBox<Student> allStudentsComboBox;
    @FXML private JFXButton enrollButton, unenrollButton;
    @FXML private JFXTextField majorField;
    @FXML private TableColumn<Schedule, String> majorColumn;

    // --- Data Lists ---
    private final AppDAO dao = new AppDAO();
    private final ObservableList<Schedule> masterScheduleList = FXCollections.observableArrayList();
    private FilteredList<Schedule> filteredScheduleList;
    private final ObservableList<Student> enrolledStudentList = FXCollections.observableArrayList();

    // *** FIX: Removed the duplicate declaration. This is the only student list that holds all students. ***
    private final ObservableList<Student> masterStudentList = FXCollections.observableArrayList();
    private final ObservableList<Student> enrollableStudentsList = FXCollections.observableArrayList();

    private final ObservableList<Teacher> allTeachersList = FXCollections.observableArrayList();
    private final ObservableList<String> weekdayTimeSlots = FXCollections.observableArrayList("07:00-10:00", "10:30-13:30", "14:00-17:00", "17:30-20:30");
    private final ObservableList<String> weekendTimeSlots = FXCollections.observableArrayList("07:00-11:00");


    @FXML
    private void initialize() {
        loadAllData();
        setupFiltering();
        setupScheduleTable();
        setupEnrolledStudentsTable();
        setupAllStudentsComboBox();
        setupTeacherComboBox();
        setupDayAndTimeComboBoxes();

        scheduleTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populateForm(newSelection);
                loadEnrolledStudents(newSelection.getId());
                filterEnrollableStudents(newSelection.getSemester(), newSelection.getMajor());
                enrollmentLabel.setText("Enrolled in: " + newSelection.getCourseName());
            } else {
                clearForm();
                enrolledStudentList.clear();
                enrollableStudentsList.clear();
                enrollmentLabel.setText("Select a Class");
            }
        });
    }

    private void setupDayAndTimeComboBoxes() {
        dayOfWeekComboBox.setItems(FXCollections.observableArrayList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"));
        dayOfWeekComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldDay, newDay) -> {
            if (newDay != null) {
                timeSlotComboBox.setItems(newDay.equals("Saturday") || newDay.equals("Sunday") ? weekendTimeSlots : weekdayTimeSlots);
            }
        });
    }

    private void loadAllData() {
        loadScheduleData();
        loadAllStudentsData();
        loadAllTeachersData();
    }

    private void setupScheduleTable() {
        // This method should be fully implemented to define your scheduleTable columns
        TableColumn<Schedule, String> courseCol = new TableColumn<>("Course");
        courseCol.setCellValueFactory(new PropertyValueFactory<>("courseName"));

        TableColumn<Schedule, String> teacherCol = new TableColumn<>("Teacher");
        teacherCol.setCellValueFactory(new PropertyValueFactory<>("teacherName"));

        TableColumn<Schedule, String> semesterCol = new TableColumn<>("Semester");
        semesterCol.setCellValueFactory(new PropertyValueFactory<>("semester"));

        TableColumn<Schedule, String> majorCol = new TableColumn<>("Major");
        majorCol.setCellValueFactory(new PropertyValueFactory<>("major"));

        TableColumn<Schedule, String> groupCol = new TableColumn<>("Group");
        groupCol.setCellValueFactory(new PropertyValueFactory<>("group"));

        TableColumn<Schedule, String> promotionCol = new TableColumn<>("Promotion");
        promotionCol.setCellValueFactory(new PropertyValueFactory<>("promotion"));

        TableColumn<Schedule, String> dayCol = new TableColumn<>("Day");
        dayCol.setCellValueFactory(new PropertyValueFactory<>("dayOfWeek"));

        TableColumn<Schedule, String> timeCol = new TableColumn<>("Time");
        timeCol.setCellValueFactory(new PropertyValueFactory<>("startTime"));


        scheduleTable.getColumns().setAll(courseCol, teacherCol, majorCol, semesterCol, groupCol, promotionCol, dayCol, timeCol);
        scheduleTable.setItems(filteredScheduleList);
    }

    private void setupEnrolledStudentsTable() {
        studentNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        enrolledStudentsTable.setItems(enrolledStudentList);
    }

    private void setupTeacherComboBox() {
        teacherComboBox.setItems(allTeachersList);
        teacherComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Teacher teacher) {
                return teacher == null ? "" : teacher.getFullName();
            }
            @Override
            public Teacher fromString(String string) { return null; }
        });
    }

    private void setupAllStudentsComboBox() {
        allStudentsComboBox.setItems(enrollableStudentsList);
        allStudentsComboBox.setConverter(new StringConverter<>() {
            @Override public String toString(Student student) { return student == null ? "" : student.getFullName(); }
            @Override public Student fromString(String string) { return null; }
        });
    }


    // --- Data Loading Methods ---
    private void loadScheduleData() { masterScheduleList.setAll(dao.getAllSchedules()); }
    private void loadAllStudentsData() { masterStudentList.setAll(dao.getAllStudents()); }
    private void loadAllTeachersData() { allTeachersList.setAll(dao.getAllTeachers()); }
    private void loadEnrolledStudents(int scheduleId) { enrolledStudentList.setAll(dao.getStudentsForSchedule(scheduleId)); }


    @FXML
    private void handleAddButton() {
        Teacher selectedTeacher = teacherComboBox.getSelectionModel().getSelectedItem();
        String timeSlot = timeSlotComboBox.getSelectionModel().getSelectedItem();
        if (selectedTeacher == null || courseNameField.getText().isEmpty() || timeSlot == null) return;

        String[] times = timeSlot.split("-");
        Schedule newSchedule = new Schedule(0, courseNameField.getText(), majorField.getText(), roomNumberField.getText(), null, dayOfWeekComboBox.getValue(), times[0], times[1], semesterField.getText(), groupField.getText(), promotionField.getText());
        dao.addSchedule(newSchedule, selectedTeacher.getId());
        loadScheduleData();
        clearForm();
    }

    @FXML
    private void handleUpdateButton() {
        Schedule selectedSchedule = scheduleTable.getSelectionModel().getSelectedItem();
        Teacher selectedTeacher = teacherComboBox.getSelectionModel().getSelectedItem();
        String timeSlot = timeSlotComboBox.getSelectionModel().getSelectedItem();
        if (selectedSchedule == null || selectedTeacher == null || timeSlot == null) return;

        String[] times = timeSlot.split("-");
        Schedule updatedSchedule = new Schedule(selectedSchedule.getId(), courseNameField.getText(),majorField.getText(), roomNumberField.getText(), null, dayOfWeekComboBox.getValue(), times[0], times[1], semesterField.getText(), groupField.getText(), promotionField.getText());
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

    @FXML
    private void handleEnrollButton() {
        Schedule selectedSchedule = scheduleTable.getSelectionModel().getSelectedItem();
        Student selectedStudent = allStudentsComboBox.getSelectionModel().getSelectedItem();

        if (selectedSchedule != null && selectedStudent != null) {
            String classSemester = selectedSchedule.getSemester();
            String studentSemester = selectedStudent.getSemester();

            // Check if both semesters are not null/empty and if they match
            if (classSemester != null && !classSemester.isEmpty() &&
                    studentSemester != null && studentSemester.equalsIgnoreCase(classSemester)) {

                // If they match, proceed with enrollment
                dao.enrollStudent(selectedStudent.getId(), selectedSchedule.getId());
                loadEnrolledStudents(selectedSchedule.getId()); // Refresh the list
            } else {
                // If they don't match, show an error alert
                showAlert(Alert.AlertType.ERROR, "Enrollment Error",
                        "Enrollment failed. Student is in semester '" + (studentSemester != null ? studentSemester : "N/A") +
                                "' but the class is for semester '" + classSemester + "'.");
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Selection Missing", "Please select a class and a student to enroll.");
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

    private void populateForm(Schedule schedule) {
        if (schedule == null) {
            clearForm();
            return;
        }
        courseNameField.setText(schedule.getCourseName());
        roomNumberField.setText(schedule.getRoomNumber());
        semesterField.setText(schedule.getSemester());
        groupField.setText(schedule.getGroup());
        promotionField.setText(schedule.getPromotion());
        teacherComboBox.getSelectionModel().select(
                allTeachersList.stream()
                        .filter(t -> t.getFullName().equals(schedule.getTeacherName()))
                        .findFirst()
                        .orElse(null)
        );
        dayOfWeekComboBox.setValue(schedule.getDayOfWeek());
        if (schedule.getStartTime() != null && !schedule.getStartTime().isEmpty()) {
            timeSlotComboBox.setValue(schedule.getStartTime() + "-" + schedule.getEndTime());
        }
    }

    @FXML
    private void clearForm() {
        courseNameField.clear();
        roomNumberField.clear();
        semesterField.clear();
        groupField.clear();
        promotionField.clear();
        teacherComboBox.getSelectionModel().clearSelection();
        dayOfWeekComboBox.getSelectionModel().clearSelection();
        timeSlotComboBox.getSelectionModel().clearSelection();
        timeSlotComboBox.getItems().clear();
        scheduleTable.getSelectionModel().clearSelection();
        majorField.clear();
    }

    @FXML
    private void handleExportToPDF() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Schedule as PDF");
        fileChooser.setInitialFileName("Class_Schedule.pdf");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));

        File file = fileChooser.showSaveDialog(scheduleTable.getScene().getWindow());

        if (file != null) {
            try {
                createPdf(file.getAbsolutePath());
                showAlert(Alert.AlertType.INFORMATION, "Export Successful", "The schedule has been exported to:\n" + file.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Export Failed", "An error occurred while exporting the PDF.");
            }
        }
    }

    /**
     * Creates a PDF document with the class schedule.
     * @param dest The destination path for the PDF file.
     * @throws IOException If an I/O error occurs.
     */
    private void createPdf(String dest) throws IOException {
        // Initialize PDF writer and document
        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Add a title
        document.add(new Paragraph("University Class Schedule")
                .setTextAlignment(TextAlignment.CENTER)
                .setBold()
                .setFontSize(20)
                .setMarginBottom(20));

        // Define table columns
        float[] columnWidths = {3, 2, 1.5f, 1, 1, 1, 1.5f};
        Table table = new Table(UnitValue.createPercentArray(columnWidths));
        table.setWidth(UnitValue.createPercentValue(100));

        // Add table headers
        table.addHeaderCell(createHeaderCell("Course"));
        table.addHeaderCell(createHeaderCell("Teacher"));
        table.addHeaderCell(createHeaderCell("Semester"));
        table.addHeaderCell(createHeaderCell("Group"));
        table.addHeaderCell(createHeaderCell("Promo"));
        table.addHeaderCell(createHeaderCell("Day"));
        table.addHeaderCell(createHeaderCell("Time"));

        // Add data rows
        for (Schedule schedule : filteredScheduleList) {
            table.addCell(new Cell().add(new Paragraph(schedule.getCourseName() != null ? schedule.getCourseName() : "")));
            table.addCell(new Cell().add(new Paragraph(schedule.getTeacherName() != null ? schedule.getTeacherName() : "")));
            table.addCell(new Cell().add(new Paragraph(schedule.getSemester() != null ? schedule.getSemester() : "")));
            table.addCell(new Cell().add(new Paragraph(schedule.getGroup() != null ? schedule.getGroup() : "")));
            table.addCell(new Cell().add(new Paragraph(schedule.getPromotion() != null ? schedule.getPromotion() : "")));
            table.addCell(new Cell().add(new Paragraph(schedule.getDayOfWeek() != null ? schedule.getDayOfWeek() : "")));
            table.addCell(new Cell().add(new Paragraph(schedule.getStartTime() + " - " + schedule.getEndTime())));
        }

        // Add the table to the document and close
        document.add(table);
        document.close();
    }

    /**
     * Helper method to create styled header cells for the PDF table.
     */
    private Cell createHeaderCell(String text) {
        return new Cell().add(new Paragraph(text)).setBold().setTextAlignment(TextAlignment.CENTER);
    }

    /**
     * Helper method to show an alert dialog.
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    private void applyFilters() {
        String courseFilter = filterCourseField.getText().toLowerCase();
        String teacherFilter = filterTeacherField.getText().toLowerCase();
        String semesterFilter = filterSemesterField.getText().toLowerCase();
        String groupFilter = filterGroupField.getText().toLowerCase();

        filteredScheduleList.setPredicate(schedule -> {
            boolean courseMatch = courseFilter.isEmpty() || schedule.getCourseName().toLowerCase().contains(courseFilter);
            boolean teacherMatch = teacherFilter.isEmpty() || (schedule.getTeacherName() != null && schedule.getTeacherName().toLowerCase().contains(teacherFilter));
            boolean semesterMatch = semesterFilter.isEmpty() || (schedule.getSemester() != null && schedule.getSemester().toLowerCase().contains(semesterFilter));
            boolean groupMatch = groupFilter.isEmpty() || (schedule.getGroup() != null && schedule.getGroup().toLowerCase().contains(groupFilter));

            return courseMatch && teacherMatch && semesterMatch && groupMatch;
        });
    }

    private void filterEnrollableStudents(String semester, String major) {
        enrollableStudentsList.clear();
        if (semester != null && !semester.trim().isEmpty() && major != null && !major.trim().isEmpty()) {
            List<Student> matchingStudents = masterStudentList.stream()
                    .filter(student ->
                            semester.equalsIgnoreCase(student.getSemester()) &&
                                    major.equalsIgnoreCase(student.getMajor())
                    )
                    .collect(Collectors.toList());
            enrollableStudentsList.setAll(matchingStudents);
        }
    }

    private void setupFiltering() {
        filteredScheduleList = new FilteredList<>(masterScheduleList, p -> true);
        filterCourseField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        filterTeacherField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        filterSemesterField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        filterGroupField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
    }

}