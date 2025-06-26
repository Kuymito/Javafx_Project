package com.example.scheduler.dao;

import com.example.scheduler.db.DatabaseConnection;
import com.example.scheduler.models.Schedule;
import com.example.scheduler.models.Student;
import com.example.scheduler.models.Teacher;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for the entire application.
 * Handles all database operations (CRUD) for all entities.
 */
public class AppDAO {

    // --- User Validation ---

    // --- User Validation ---
    /**
     * Validates a user's credentials against the database.
     * @param username The username to check.
     * @param password The password to check.
     * @return The profile picture path if credentials are valid, null otherwise.
     */
    public String validateUser(String username, String password) {
        String sql = "SELECT profile_picture_path FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("profile_picture_path");
                }
            }
        } catch (SQLException e) {
            System.err.println("User validation error: " + e.getMessage());
        }
        return null; // Return null if validation fails
    }


    // --- Schedule (Class) CRUD Methods ---

    public List<Schedule> getAllSchedules() {
        // *** THE FIX IS IN THIS SQL QUERY ***
        // We now select first_name and last_name and concatenate them for the teacher's full name.
        String sql = "SELECT s.id, s.course_name, s.room_number, s.day_of_week, s.start_time, s.end_time, " +
                "t.first_name, t.last_name, " + // Select the new columns
                "s.semester, s.class_group, s.promotion " +
                "FROM schedules s LEFT JOIN teachers t ON s.teacher_id = t.id " +
                "ORDER BY s.day_of_week, s.start_time";

        List<Schedule> schedules = new ArrayList<>();
        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                // Combine first and last name into a full name for the Schedule object
                String teacherFullName = rs.getString("first_name") + " " + rs.getString("last_name");

                schedules.add(new Schedule(
                        rs.getInt("id"),
                        rs.getString("course_name"),
                        rs.getString("room_number"),
                        teacherFullName, // Use the combined full name
                        rs.getString("day_of_week"),
                        rs.getString("start_time") != null ? rs.getString("start_time").substring(0, 5) : "",
                        rs.getString("end_time") != null ? rs.getString("end_time").substring(0, 5) : "",
                        rs.getString("semester"),
                        rs.getString("class_group"),
                        rs.getString("promotion")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching schedules: " + e.getMessage());
        }
        return schedules;
    }

    public void addSchedule(Schedule schedule, int teacherId) {
        String sql = "INSERT INTO schedules(course_name, room_number, day_of_week, start_time, end_time, teacher_id, semester, class_group, promotion) VALUES(?,?,?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // ... (set existing parameters)
            pstmt.setString(1, schedule.getCourseName());
            pstmt.setString(2, schedule.getRoomNumber());
            pstmt.setString(3, schedule.getDayOfWeek());
            pstmt.setTime(4, schedule.getStartTime().isEmpty() ? null : java.sql.Time.valueOf(schedule.getStartTime() + ":00"));
            pstmt.setTime(5, schedule.getEndTime().isEmpty() ? null : java.sql.Time.valueOf(schedule.getEndTime() + ":00"));
            pstmt.setInt(6, teacherId);
            // Set new parameters
            pstmt.setString(7, schedule.getSemester());
            pstmt.setString(8, schedule.getGroup());
            pstmt.setString(9, schedule.getPromotion());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error adding schedule: " + e.getMessage());
        }
    }

    public void updateSchedule(Schedule schedule, int teacherId) {
        String sql = "UPDATE schedules SET course_name = ?, room_number = ?, day_of_week = ?, start_time = ?, end_time = ?, teacher_id = ?, semester = ?, class_group = ?, promotion = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // ... (set existing parameters)
            pstmt.setString(1, schedule.getCourseName());
            pstmt.setString(2, schedule.getRoomNumber());
            pstmt.setString(3, schedule.getDayOfWeek());
            pstmt.setTime(4, schedule.getStartTime().isEmpty() ? null : java.sql.Time.valueOf(schedule.getStartTime() + ":00"));
            pstmt.setTime(5, schedule.getEndTime().isEmpty() ? null : java.sql.Time.valueOf(schedule.getEndTime() + ":00"));
            pstmt.setInt(6, teacherId);
            // Set new parameters
            pstmt.setString(7, schedule.getSemester());
            pstmt.setString(8, schedule.getGroup());
            pstmt.setString(9, schedule.getPromotion());
            pstmt.setInt(10, schedule.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating schedule: " + e.getMessage());
        }
    }

    public void deleteSchedule(int id) {
        String sql = "DELETE FROM schedules WHERE id = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting schedule: " + e.getMessage());
        }
    }


    // --- Teacher CRUD Methods ---

    public List<Teacher> getAllTeachers() {
        String sql = "SELECT * FROM teachers ORDER BY first_name, last_name";
        List<Teacher> teachers = new ArrayList<>();
        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                teachers.add(new Teacher(rs.getInt("id"), rs.getString("first_name"), rs.getString("last_name"), rs.getString("email"), rs.getString("phone_number"), rs.getString("degree"), rs.getString("major"), rs.getString("address")));
            }
        } catch (SQLException e) { System.err.println("Error fetching teachers: " + e.getMessage()); }
        return teachers;
    }

    public void addTeacher(Teacher teacher) {
        String sql = "INSERT INTO teachers(first_name, last_name, email, phone_number, degree, major, address) VALUES(?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, teacher.getFirstName());
            pstmt.setString(2, teacher.getLastName());
            pstmt.setString(3, teacher.getEmail());
            pstmt.setString(4, teacher.getPhoneNumber());
            pstmt.setString(5, teacher.getDegree());
            pstmt.setString(6, teacher.getMajor());
            pstmt.setString(7, teacher.getAddress());
            pstmt.executeUpdate();
        } catch (SQLException e) { System.err.println("Error adding teacher: " + e.getMessage()); }
    }

    public void updateTeacher(Teacher teacher) {
        String sql = "UPDATE teachers SET first_name = ?, last_name = ?, email = ?, phone_number = ?, degree = ?, major = ?, address = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, teacher.getFirstName());
            pstmt.setString(2, teacher.getLastName());
            pstmt.setString(3, teacher.getEmail());
            pstmt.setString(4, teacher.getPhoneNumber());
            pstmt.setString(5, teacher.getDegree());
            pstmt.setString(6, teacher.getMajor());
            pstmt.setString(7, teacher.getAddress());
            pstmt.setInt(8, teacher.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) { System.err.println("Error updating teacher: " + e.getMessage()); }
    }

    public void deleteTeacher(int id) {
        String sql = "DELETE FROM teachers WHERE id = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting teacher: " + e.getMessage());
        }
    }


    // --- Student CRUD Methods ---

    public List<Student> getAllStudents() {
        String sql = "SELECT * FROM students ORDER BY first_name, last_name";
        List<Student> students = new ArrayList<>();
        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                students.add(new Student(rs.getInt("id"), rs.getString("first_name"), rs.getString("last_name"), rs.getString("email"), rs.getString("phone_number"), rs.getString("major"), rs.getString("semester"), rs.getString("address")));
            }
        } catch (SQLException e) { System.err.println("Error fetching students: " + e.getMessage()); }
        return students;
    }


    public void addStudent(Student student) {
        String sql = "INSERT INTO students(first_name, last_name, email, phone_number, major, semester, address) VALUES(?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, student.getFirstName());
            pstmt.setString(2, student.getLastName());
            pstmt.setString(3, student.getEmail());
            pstmt.setString(4, student.getPhoneNumber());
            pstmt.setString(5, student.getMajor());
            pstmt.setString(6, student.getSemester());
            pstmt.setString(7, student.getAddress());
            pstmt.executeUpdate();
        } catch (SQLException e) { System.err.println("Error adding student: " + e.getMessage()); }
    }

    public void updateStudent(Student student) {
        String sql = "UPDATE students SET first_name = ?, last_name = ?, email = ?, phone_number = ?, major = ?, semester = ?, address = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, student.getFirstName());
            pstmt.setString(2, student.getLastName());
            pstmt.setString(3, student.getEmail());
            pstmt.setString(4, student.getPhoneNumber());
            pstmt.setString(5, student.getMajor());
            pstmt.setString(6, student.getSemester());
            pstmt.setString(7, student.getAddress());
            pstmt.setInt(8, student.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) { System.err.println("Error updating student: " + e.getMessage()); }
    }

    public void deleteStudent(int id) {
        String sql = "DELETE FROM students WHERE id = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting student: " + e.getMessage());
        }
    }

    /**
     * Gets all students enrolled in a specific schedule.
     * @param scheduleId The ID of the schedule.
     * @return A list of enrolled students.
     */
    public List<Student> getStudentsForSchedule(int scheduleId) {
        // *** THE FIX IS IN THIS SQL QUERY ***
        // Select all columns from the students table instead of specific old ones.
        String sql = "SELECT s.* FROM students s " +
                "JOIN enrollments e ON s.id = e.student_id " +
                "WHERE e.schedule_id = ?";
        List<Student> enrolledStudents = new ArrayList<>();
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, scheduleId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // Construct the Student object using all the new fields
                    enrolledStudents.add(new Student(
                            rs.getInt("id"),
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("email"),
                            rs.getString("phone_number"),
                            rs.getString("major"),
                            rs.getString("semester"),
                            rs.getString("address")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching students for schedule: " + e.getMessage());
        }
        return enrolledStudents;
    }

    /**
     * Enrolls a student in a schedule.
     * @param studentId The ID of the student.
     * @param scheduleId The ID of the schedule.
     */
    public void enrollStudent(int studentId, int scheduleId) {
        String sql = "INSERT INTO enrollments(student_id, schedule_id) VALUES(?,?)";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, scheduleId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            // It's okay if it fails because the student is already enrolled (due to PRIMARY KEY constraint)
            if (!e.getSQLState().equals("23505")) { // 23505 is unique_violation
                System.err.println("Error enrolling student: " + e.getMessage());
            }
        }
    }

    /**
     * Unenrolls a student from a schedule.
     * @param studentId The ID of the student.
     * @param scheduleId The ID of the schedule.
     */
    public void unenrollStudent(int studentId, int scheduleId) {
        String sql = "DELETE FROM enrollments WHERE student_id = ? AND schedule_id = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, scheduleId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error unenrolling student: " + e.getMessage());
        }
    }
}