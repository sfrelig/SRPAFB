package com.srpafb.connection;

import java.sql.Connection;
import java.sql.DriverManager;

public class MySQLConnection {

    private static final String URL = "jdbc:mysql://localhost:3307/pafb?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "290319";

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (Exception e) {
            throw new RuntimeException("Error al conectar a MySQL", e);
        }
    }
}