package ua.kiev.prog;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Hello world!
 */
public class App {
    static final String DB_CONNECTION = "jdbc:mysql://localhost:3306/weedb?serverTimezone=Europe/Kiev";
    static final String DB_USER = "root";
    static final String DB_PASSWORD = "password";

    static Connection conn;


    public static void main(String[] args) {
        try {
            conn = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
            new Apartments(conn).chooseAction();
        } catch (SQLException throwables) {
            System.out.println("Connection failed");
            throwables.printStackTrace();
        }

    }
}
