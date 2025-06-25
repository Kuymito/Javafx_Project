package com.example.scheduler.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    // --- PostgreSQL Connection Details ---
    // Make sure the database name matches the one you created.
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
        // PostgreSQL uses "SERIAL PRIMARY KEY" for auto-incrementing integers.
        String createUserTableSql = "CREATE TABLE IF NOT EXISTS users (id SERIAL PRIMARY KEY, username TEXT NOT NULL UNIQUE, password TEXT NOT NULL);";
        String createScheduleTableSql = "CREATE TABLE IF NOT EXISTS schedules (id SERIAL PRIMARY KEY, courseName TEXT NOT NULL, roomNumber TEXT NOT NULL, teacherName TEXT NOT NULL, dayOfWeek TEXT NOT NULL, startTime TEXT NOT NULL, endTime TEXT NOT NULL);";
        String createTeachersTableSql = "CREATE TABLE IF NOT EXISTS teachers (id SERIAL PRIMARY KEY, name TEXT NOT NULL, subject TEXT NOT NULL);";
        String createStudentsTableSql = "CREATE TABLE IF NOT EXISTS students (id SERIAL PRIMARY KEY, name TEXT NOT NULL, major TEXT NOT NULL);";

        String insertDefaultUser = "INSERT INTO users(username, password) SELECT 'admin', 'password' WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin');";

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            if (conn == null) {
                System.err.println("Cannot initialize database, connection is null.");
                return;
            }
            stmt.execute(createUserTableSql);
            stmt.execute(createScheduleTableSql);
            stmt.execute(createTeachersTableSql);
            stmt.execute(createStudentsTableSql);
            stmt.execute(insertDefaultUser);
            System.out.println("Database initialization check complete.");
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }
}