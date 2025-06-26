package com.example.scheduler.models;

import javafx.beans.property.*;

public class Schedule {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty courseName = new SimpleStringProperty();
    private final StringProperty major = new SimpleStringProperty(); // New field
    private final StringProperty roomNumber = new SimpleStringProperty();
    private final StringProperty teacherName = new SimpleStringProperty();
    private final StringProperty dayOfWeek = new SimpleStringProperty();
    private final StringProperty startTime = new SimpleStringProperty();
    private final StringProperty endTime = new SimpleStringProperty();
    private final StringProperty semester = new SimpleStringProperty();
    private final StringProperty group = new SimpleStringProperty();
    private final StringProperty promotion = new SimpleStringProperty();

    // Constructor with all fields in the correct order
    public Schedule(int id, String courseName, String major, String roomNumber, String teacherName, String dayOfWeek, String startTime, String endTime, String semester, String group, String promotion) {
        this.setId(id);
        this.setCourseName(courseName);
        this.setMajor(major);
        this.setRoomNumber(roomNumber);
        this.setTeacherName(teacherName);
        this.setDayOfWeek(dayOfWeek);
        this.setStartTime(startTime);
        this.setEndTime(endTime);
        this.setSemester(semester);
        this.setGroup(group);
        this.setPromotion(promotion);
    }

    // --- Getters & Setters ---

    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }
    public IntegerProperty idProperty() { return id; }

    public String getCourseName() { return courseName.get(); }
    public void setCourseName(String courseName) { this.courseName.set(courseName); }
    public StringProperty courseNameProperty() { return courseName; }

    public String getMajor() { return major.get(); }
    public void setMajor(String major) { this.major.set(major); }
    public StringProperty majorProperty() { return major; }

    public String getRoomNumber() { return roomNumber.get(); }
    public void setRoomNumber(String roomNumber) { this.roomNumber.set(roomNumber); }
    public StringProperty roomNumberProperty() { return roomNumber; }

    public String getTeacherName() { return teacherName.get(); }
    public void setTeacherName(String teacherName) { this.teacherName.set(teacherName); }
    public StringProperty teacherNameProperty() { return teacherName; }

    public String getDayOfWeek() { return dayOfWeek.get(); }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek.set(dayOfWeek); }
    public StringProperty dayOfWeekProperty() { return dayOfWeek; }

    public String getStartTime() { return startTime.get(); }
    public void setStartTime(String startTime) { this.startTime.set(startTime); }
    public StringProperty startTimeProperty() { return startTime; }

    public String getEndTime() { return endTime.get(); }
    public void setEndTime(String endTime) { this.endTime.set(endTime); }
    public StringProperty endTimeProperty() { return endTime; }

    public String getSemester() { return semester.get(); }
    public void setSemester(String semester) { this.semester.set(semester); }
    public StringProperty semesterProperty() { return semester; }

    public String getGroup() { return group.get(); }
    public void setGroup(String group) { this.group.set(group); }
    public StringProperty groupProperty() { return group; }

    public String getPromotion() { return promotion.get(); }
    public void setPromotion(String promotion) { this.promotion.set(promotion); }
    public StringProperty promotionProperty() { return promotion; }
}