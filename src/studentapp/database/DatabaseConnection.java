package studentapp.database;

import java.sql.*;

public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/student_system";
    private static final String USER = "root";
    private static final String PASS = "";

    private static Connection conn;

    public static Connection getConnection() {
        try {
            // IMPORTANT → Load MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            if (conn == null || conn.isClosed()) {
                conn = DriverManager.getConnection(URL, USER, PASS);
            }

        } catch (Exception e) {
            System.out.println("Database Connection Error: " + e.getMessage());
        }
        return conn;
    }
}
