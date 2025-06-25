package com.example.scheduler.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    // --- PostgreSQL Connection Details ---
    private static final String HOST = "localhost";
    private static final String PORT = "5432";
    private static final String DB_NAME = "postgres";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "123"; // <-- CHANGE THIS

    private static final String DB_URL = "jdbc:postgresql://" + HOST + ":" + PORT + "/" + DB_NAME;

    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
        }
        return conn;
    }

    public static void initializeDatabase() {
        String createUserTableSql = "CREATE TABLE IF NOT EXISTS users (id SERIAL PRIMARY KEY, username TEXT NOT NULL UNIQUE, password TEXT NOT NULL, profile_picture_path TEXT);";

        // Add semester, class_group, and promotion columns
        String createScheduleTableSql = "CREATE TABLE IF NOT EXISTS schedules ("
                + "id SERIAL PRIMARY KEY, "
                + "course_name TEXT NOT NULL, "
                + "room_number TEXT, "
                + "day_of_week TEXT, "
                + "start_time TIME, "
                + "end_time TIME, "
                + "teacher_id INTEGER REFERENCES teachers(id), "
                + "semester TEXT, "
                + "class_group TEXT, "
                + "promotion TEXT"
                + ");";

        String createTeachersTableSql = "CREATE TABLE IF NOT EXISTS teachers (id SERIAL PRIMARY KEY, name TEXT NOT NULL, subject TEXT NOT NULL);";
        String createStudentsTableSql = "CREATE TABLE IF NOT EXISTS students (id SERIAL PRIMARY KEY, name TEXT NOT NULL, major TEXT NOT NULL);";
        String createEnrollmentsTableSql = "CREATE TABLE IF NOT EXISTS enrollments (schedule_id INTEGER REFERENCES schedules(id) ON DELETE CASCADE, student_id INTEGER REFERENCES students(id) ON DELETE CASCADE, PRIMARY KEY (schedule_id, student_id));";

        String insertDefaultUser = "INSERT INTO users(username, password, profile_picture_path) SELECT 'admin', 'password', '/images/default-profile.png' WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin');";

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            if (conn == null) {
                System.err.println("Cannot initialize database, connection is null.");
                return;
            }
            stmt.execute(createUserTableSql);
            stmt.execute(createTeachersTableSql);
            stmt.execute(createStudentsTableSql);
            stmt.execute(createScheduleTableSql); // This now includes the new columns
            stmt.execute(createEnrollmentsTableSql);
            stmt.execute(insertDefaultUser);
            System.out.println("Database initialization check complete.");
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }
}