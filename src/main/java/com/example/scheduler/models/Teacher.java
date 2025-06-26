package com.example.scheduler.models;

import javafx.beans.property.*;

public class Teacher {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty firstName = new SimpleStringProperty();
    private final StringProperty lastName = new SimpleStringProperty();
    private final StringProperty email = new SimpleStringProperty();
    private final StringProperty phoneNumber = new SimpleStringProperty();
    private final StringProperty degree = new SimpleStringProperty();
    private final StringProperty major = new SimpleStringProperty();
    private final StringProperty address = new SimpleStringProperty();

    public Teacher(int id, String firstName, String lastName, String email, String phoneNumber, String degree, String major, String address) {
        this.setId(id);
        this.setFirstName(firstName);
        this.setLastName(lastName);
        this.setEmail(email);
        this.setPhoneNumber(phoneNumber);
        this.setDegree(degree);
        this.setMajor(major);
        this.setAddress(address);
    }

    // Getter for full name for ComboBox display
    public String getFullName() { return getFirstName() + " " + getLastName(); }

    // --- Getters & Setters for all properties ---
    public int getId() { return id.get(); }
    public IntegerProperty idProperty() { return id; }
    public void setId(int id) { this.id.set(id); }

    public String getFirstName() { return firstName.get(); }
    public StringProperty firstNameProperty() { return firstName; }
    public void setFirstName(String firstName) { this.firstName.set(firstName); }

    public String getLastName() { return lastName.get(); }
    public StringProperty lastNameProperty() { return lastName; }
    public void setLastName(String lastName) { this.lastName.set(lastName); }

    public String getEmail() { return email.get(); }
    public StringProperty emailProperty() { return email; }
    public void setEmail(String email) { this.email.set(email); }

    public String getPhoneNumber() { return phoneNumber.get(); }
    public StringProperty phoneNumberProperty() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber.set(phoneNumber); }

    public String getDegree() { return degree.get(); }
    public StringProperty degreeProperty() { return degree; }
    public void setDegree(String degree) { this.degree.set(degree); }

    public String getMajor() { return major.get(); }
    public StringProperty majorProperty() { return major; }
    public void setMajor(String major) { this.major.set(major); }

    public String getAddress() { return address.get(); }
    public StringProperty addressProperty() { return address; }
    public void setAddress(String address) { this.address.set(address); }

    public String setFullName() { return getFirstName() + " " + getLastName(); }
}