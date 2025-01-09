package com.example.model;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnection {
   private static HikariDataSource dataSource;

   static {
      try {
         // Explicitly load PostgreSQL driver
         Class.forName("org.postgresql.Driver");

         HikariConfig config = new HikariConfig();
         config.setJdbcUrl("jdbc:postgresql://localhost:5432/gestion_evenements");
         config.setUsername("postgres");
         config.setPassword("2004");
         config.setMaximumPoolSize(10);
         dataSource = new HikariDataSource(config);
      } catch (Exception e) {
         throw new RuntimeException("Failed to initialize database connection", e);
      }
   }

   public DatabaseConnection() {
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
         } catch (SQLException var2) {
         }
      }
   }
}
