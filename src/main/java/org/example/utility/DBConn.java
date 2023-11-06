package org.example.utility;

import org.example.attendance.DemoAttendance;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBConn {

    private static Connection connection;

    // JDBC URL, username, and password of the database
    private static final String JDBC_URL = "jdbc:mysql://localhost/attendancedb";
    private static final String JDBC_USER = "indika";
    private static final String JDBC_PASSWORD = "root";
    private DBConn(){

    }

    public static Connection getConnection (){
        try {
            if(connection == null){
            }
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
        } catch (ClassNotFoundException | SQLException ce) {
            Logger.getLogger(DBConn.class.getName()).log(Level.SEVERE, null, ce);
        }
        return connection;
    }
}
