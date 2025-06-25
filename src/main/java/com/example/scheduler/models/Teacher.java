// File: src/main/java/com/example/scheduler/models/Teacher.java
package com.example.scheduler.models;

import javafx.beans.property.*;

public class Teacher {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty subject = new SimpleStringProperty();

    public Teacher(int id, String name, String subject) {
        setId(id);
        setName(name);
        setSubject(subject);
    }

    public int getId() { return id.get(); }
    public IntegerProperty idProperty() { return id; }
    public void setId(int id) { this.id.set(id); }

    public String getName() { return name.get(); }
    public StringProperty nameProperty() { return name; }
    public void setName(String name) { this.name.set(name); }

    public String getSubject() { return subject.get(); }
    public StringProperty subjectProperty() { return subject; }
    public void setSubject(String subject) { this.subject.set(subject); }
}