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
        String sql = "SELECT s.id, s.course_name, s.room_number, s.day_of_week, s.start_time, s.end_time, " +
                "t.name AS teacher_name, s.semester, s.class_group, s.promotion " + // Add new columns
                "FROM schedules s LEFT JOIN teachers t ON s.teacher_id = t.id " +
                "ORDER BY s.day_of_week, s.start_time";

        List<Schedule> schedules = new ArrayList<>();
        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                schedules.add(new Schedule(
                        rs.getInt("id"),
                        rs.getString("course_name"),
                        rs.getString("room_number"),
                        rs.getString("teacher_name"),
                        rs.getString("day_of_week"),
                        rs.getString("start_time") != null ? rs.getString("start_time").substring(0, 5) : "",
                        rs.getString("end_time") != null ? rs.getString("end_time").substring(0, 5) : "",
                        rs.getString("semester"),      // Get semester
                        rs.getString("class_group"),   // Get group
                        rs.getString("promotion")      // Get promotion
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
        String sql = "SELECT * FROM teachers ORDER BY name";
        List<Teacher> teachers = new ArrayList<>();
        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                teachers.add(new Teacher(rs.getInt("id"), rs.getString("name"), rs.getString("subject")));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching teachers: " + e.getMessage());
        }
        return teachers;
    }

    public void addTeacher(Teacher teacher) {
        String sql = "INSERT INTO teachers(name, subject) VALUES(?,?)";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, teacher.getName());
            pstmt.setString(2, teacher.getSubject());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error adding teacher: " + e.getMessage());
        }
    }

    public void updateTeacher(Teacher teacher) {
        String sql = "UPDATE teachers SET name = ?, subject = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, teacher.getName());
            pstmt.setString(2, teacher.getSubject());
            pstmt.setInt(3, teacher.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating teacher: " + e.getMessage());
        }
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
        String sql = "SELECT * FROM students ORDER BY name";
        List<Student> students = new ArrayList<>();
        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                students.add(new Student(rs.getInt("id"), rs.getString("name"), rs.getString("major")));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching students: " + e.getMessage());
        }
        return students;
    }

    public void addStudent(Student student) {
        String sql = "INSERT INTO students(name, major) VALUES(?,?)";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, student.getName());
            pstmt.setString(2, student.getMajor());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error adding student: " + e.getMessage());
        }
    }

    public void updateStudent(Student student) {
        String sql = "UPDATE students SET name = ?, major = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, student.getName());
            pstmt.setString(2, student.getMajor());
            pstmt.setInt(3, student.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating student: " + e.getMessage());
        }
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
        String sql = "SELECT s.id, s.name, s.major FROM students s " +
                "JOIN enrollments e ON s.id = e.student_id " +
                "WHERE e.schedule_id = ?";
        List<Student> enrolledStudents = new ArrayList<>();
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, scheduleId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    enrolledStudents.add(new Student(rs.getInt("id"), rs.getString("name"), rs.getString("major")));
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