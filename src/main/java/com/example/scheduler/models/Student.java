// File: src/main/java/com/example/scheduler/models/Student.java
package com.example.scheduler.models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Student {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty major = new SimpleStringProperty();

    public Student(int id, String name, String major) {
        setId(id);
        setName(name);
        setMajor(major);
    }

    // --- ID Property ---
    public int getId() { return id.get(); }
    public IntegerProperty idProperty() { return id; }
    public void setId(int id) { this.id.set(id); }

    // --- Name Property ---
    public String getName() { return name.get(); }
    public StringProperty nameProperty() { return name; }
    public void setName(String name) { this.name.set(name); }

    // --- Major Property ---
    public String getMajor() { return major.get(); }
    public StringProperty majorProperty() { return major; }
    public void setMajor(String major) { this.major.set(major); }
}