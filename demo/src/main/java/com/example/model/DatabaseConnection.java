package com.example.model;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.mariadb.jdbc.MariaDbDataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DatabaseConnection {
    private static HikariDataSource dataSource;
    
    static {
        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:mysql://localhost:3306/gestion_evenement");
            config.setUsername("root");
            config.setPassword("");
            config.setMaximumPoolSize(10);
            
            dataSource = new HikariDataSource(config);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize database connection", e);
        }
    }
    
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
    
    public static void shutdown() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
    
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                // Log error if needed
            }
        }
    }
}