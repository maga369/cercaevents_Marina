package com.example.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Proporciona connexions JDBC a la base de dades H2 de l'aplicacio.
 */
public class DBConnection {

    private static final String URL = "jdbc:h2:./dades/eventsdb";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    /**
     * Obre una nova connexio amb la base de dades.
     *
     * @return connexio JDBC oberta
     * @throws SQLException si no es pot establir la connexio
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
