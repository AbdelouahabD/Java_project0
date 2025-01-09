
package com.example.dd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// import com.example.model.DatabaseConnection;
import com.example.model.*;

public class EvenmentsDAO implements GenericDAO<Evenement> {

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM evenemant WHERE id_event = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Événement supprimé avec succès.");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'événement : " + e.getMessage());
        }
    }

    @Override
    public void update(Evenement evenement) {
        String sql = "UPDATE evenemant SET nom_event = ?, date_event = ?, description = ?, id_user = ? WHERE id_event = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, evenement.getNomEvent());
            pstmt.setTimestamp(2, Timestamp.valueOf(evenement.getDateEvent()));
            pstmt.setString(3, evenement.getDescription());
            pstmt.setInt(4, evenement.getIdUser());
            pstmt.setInt(5, evenement.getIdEvent());
            pstmt.executeUpdate();
            System.out.println("Événement mis à jour avec succès.");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de l'événement : " + e.getMessage());
        }
    }

    @Override
    public List<Evenement> getAll() {
        List<Evenement> events = new ArrayList<>();
        String sql = "SELECT * FROM evenemant";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Evenement event = new Evenement();
                event.setIdEvent(rs.getInt("id_event"));
                event.setNomEvent(rs.getString("nom_event"));
                event.setDateEvent(rs.getTimestamp("date_event").toLocalDateTime());
                event.setDescription(rs.getString("description"));
                event.setIdUser(rs.getInt("id_user"));
                events.add(event);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la sélection des événements : " + e.getMessage());
        }
        return events;
    }

    @Override
    public Evenement get(int id) {
        String sql = "SELECT * FROM evenemant WHERE id_event = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Evenement event = new Evenement();
                    event.setIdEvent(rs.getInt("id_event"));
                    event.setNomEvent(rs.getString("nom_event"));
                    event.setDateEvent(rs.getTimestamp("date_event").toLocalDateTime());
                    event.setDescription(rs.getString("description"));
                    event.setIdUser(rs.getInt("id_user"));
                    return event;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de l'événement : " + e.getMessage());
        }
        return null;
    }

    @Override
    public void add(Evenement evenement) {
        String sql = "INSERT INTO evenemant (nom_event, date_event, description, id_user) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (evenement.getDateEvent() == null) {
                throw new IllegalArgumentException("La date de l'événement ne peut pas être nulle.");
            }
            pstmt.setString(1, evenement.getNomEvent());
            pstmt.setTimestamp(2, Timestamp.valueOf(evenement.getDateEvent()));
            pstmt.setString(3, evenement.getDescription());
            pstmt.setInt(4, evenement.getIdUser());
            pstmt.executeUpdate();
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    evenement.setIdEvent(generatedKeys.getInt(1));
                }
            }
            System.out.println("Événement inséré avec succès.");
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'insertion de l'événement : " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Erreur : " + e.getMessage());
        }
    }



public List<Evenement> getUserEvents(int id) {
    String query = "SELECT * FROM evenemant WHERE id_user = ?";
    List<Evenement> Evenements = new ArrayList<>();
    
    // Use PreparedStatement instead of Statement for parameterized queries
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(query)) {  // Changed from Statement to PreparedStatement
        
        pstmt.setInt(1, id); 
        ResultSet rs = pstmt.executeQuery();
        
        while (rs.next()) {
            Evenement Evenement = new Evenement();
            Evenement.setIdEvent(rs.getInt("id_event"));
            Evenement.setIdUser(rs.getInt("id_user"));
            Evenement.setNomEvent(rs.getString("nom_event"));
            Evenement.setDateEvent(rs.getTimestamp("date_event").toLocalDateTime());
            Evenement.setDescription(rs.getString("description"));
            Evenements.add(Evenement);
        }
        
    } catch (SQLException e) {
        System.err.println("erreur lors de la selection des Evenements : " + e.getMessage());
    }
    
    return Evenements;
}
}
