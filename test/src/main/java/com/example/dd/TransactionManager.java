package com.example.dd;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TransactionManager {
    private static final String URL = "jdbc:mysql://localhost:3306/projet_java?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root"; 
    private static final String PASSWORD = ""; 
    public void executeInTransaction(Runnable operation) {
    
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
        conn.setAutoCommit(false);
        operation.run();
        conn.commit();
        } catch (SQLException e) {
            System.err.println("erreur lors de la connexion : " + e.getMessage());
            throw new RuntimeException(e);
        }
        }
}
