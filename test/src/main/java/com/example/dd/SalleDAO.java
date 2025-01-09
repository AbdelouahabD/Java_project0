
package com.example.dd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.example.model.DatabaseConnection;
import com.example.model.Salle;

public class SalleDAO implements GenericDAO<Salle> {

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM salles WHERE id_salle = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Salle supprime avec succes.");
        } catch (SQLException e) {
            System.err.println("erreur lors de la supression de la salle : " + e.getMessage());
        }
    }

    @Override
    public void update(Salle salle) {
        String sql = "UPDATE salles SET nom_salle = ?, capacite = ? WHERE id_salle = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, salle.getNom_salle());
            pstmt.setInt(2, salle.getCapacite());
            pstmt.setInt(3, salle.getId_salle());
            pstmt.executeUpdate();
            System.out.println("salle mise a jour avec succee.");
        } catch (SQLException e) {
            System.err.println("erreur lors de la mise a jour de la salle : " + e.getMessage());
        }
    }

    @Override
    public List<Salle> getAll() {
        List<Salle> rooms = new ArrayList<>();
        String sql = "SELECT * FROM salles";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Salle room = new Salle();
                room.setId_salle(rs.getInt("id_salle"));
                room.setNom_salle(rs.getString("nom_salle"));
                room.setCapacite(rs.getInt("capacite"));
                rooms.add(room);
            }
        } catch (SQLException e) {
            System.err.println("erreur lors de la recupération des salles : " + e.getMessage());
        }
        return rooms;
    }

    @Override
    public Salle get(int id) {
        Salle salle = null;
        String sql = "SELECT * FROM salles WHERE id_salle = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    salle = new Salle();
                    salle.setId_salle(rs.getInt("id_salle"));
                    salle.setNom_salle(rs.getString("nom_salle"));
                    salle.setCapacite(rs.getInt("capacite"));
                }
            }
        } catch (SQLException e) {
            System.err.println("erreur lors de la recupération de la salle : " + e.getMessage());
        }
        return salle;
    }

    @Override
    public void add(Salle salle) {
        String sql = "INSERT INTO salles (nom_salle, capacite) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, salle.getNom_salle());
            pstmt.setInt(2, salle.getCapacite());
            pstmt.executeUpdate();
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    salle.setId_salle(generatedKeys.getInt(1));
                }
            }
            System.out.println("Salle inseree avec succees.");
        } catch (SQLException e) {
            System.err.println("erreur lors de l'insertion de la salle : " + e.getMessage());
        }
    }
}
