// File: src/main/java/com/example/scheduler/models/Schedule.java
package com.example.scheduler.models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Schedule {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty courseName = new SimpleStringProperty();
    private final StringProperty roomNumber = new SimpleStringProperty();
    private final StringProperty teacherName = new SimpleStringProperty();
    private final StringProperty dayOfWeek = new SimpleStringProperty();
    private final StringProperty startTime = new SimpleStringProperty();
    private final StringProperty endTime = new SimpleStringProperty();

    public Schedule(int id, String courseName, String roomNumber, String teacherName, String dayOfWeek, String startTime, String endTime) {
        this.setId(id);
        this.setCourseName(courseName);
        this.setRoomNumber(roomNumber);
        this.setTeacherName(teacherName);
        this.setDayOfWeek(dayOfWeek);
        this.setStartTime(startTime);
        this.setEndTime(endTime);
    }

    // --- ID Property ---
    public int getId() { return id.get(); }
    public IntegerProperty idProperty() { return id; }
    public void setId(int id) { this.id.set(id); }

    // --- Course Name Property ---
    public String getCourseName() { return courseName.get(); }
    public StringProperty courseNameProperty() { return courseName; }
    public void setCourseName(String courseName) { this.courseName.set(courseName); }

    // --- Room Number Property ---
    public String getRoomNumber() { return roomNumber.get(); }
    public StringProperty roomNumberProperty() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber.set(roomNumber); }

    // --- Teacher Name Property ---
    public String getTeacherName() { return teacherName.get(); }
    public StringProperty teacherNameProperty() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName.set(teacherName); }

    // --- Day of Week Property ---
    public String getDayOfWeek() { return dayOfWeek.get(); }
    public StringProperty dayOfWeekProperty() { return dayOfWeek; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek.set(dayOfWeek); }

    // --- Start Time Property ---
    public String getStartTime() { return startTime.get(); }
    public StringProperty startTimeProperty() { return startTime; }
    public void setStartTime(String startTime) { this.startTime.set(startTime); }

    // --- End Time Property ---
    public String getEndTime() { return endTime.get(); }
    public StringProperty endTimeProperty() { return endTime; }
    public void setEndTime(String endTime) { this.endTime.set(endTime); }
}