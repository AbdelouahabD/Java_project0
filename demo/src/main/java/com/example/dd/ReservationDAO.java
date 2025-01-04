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

import com.example.model.DatabaseConnection;
import com.example.model.Reservation;

public class ReservationDAO implements GenericDAO<Reservation> {

    private static final String DELETE_RESERVATION_SQL = "DELETE FROM reservations WHERE id_reservation = ?";
    private static final String UPDATE_RESERVATION_SQL = "UPDATE reservations SET id_user = ?, id_event = ?, id_salle = ?, id_terrain = ?, date_reservation = ?,duree=? WHERE id_reservation = ?";
    private static final String GET_ALL_RESERVATIONS_SQL = "SELECT * FROM reservations";
    private static final String GET_RESERVATION_BY_ID_SQL = "SELECT * FROM reservations WHERE id_reservation = ?";
    private static final String INSERT_RESERVATION_SQL = "INSERT INTO reservations (id_user, id_event, id_salle, id_terrain, date_reservation,duree) VALUES (?, ?, ?, ?, ?,?)";

    @Override
    public void delete(int id) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(DELETE_RESERVATION_SQL)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Reservation supprimee avec succes.");
        } catch (SQLException e) {
            System.err.println("erreur lors de la suppression de la reservation : " + e.getMessage());
        }
    }

    @Override
    public void update(Reservation reservation) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(UPDATE_RESERVATION_SQL)) {
            pstmt.setInt(1, reservation.getId_user());
            pstmt.setInt(2, reservation.getId_event());
            pstmt.setInt(3, reservation.getId_salle());
            pstmt.setInt(4, reservation.getId_terrain());
            pstmt.setTimestamp(5, Timestamp.valueOf(reservation.getDate_reservation()));
            pstmt.setInt(6, reservation.getDuree());
            pstmt.setInt(7, reservation.getId_reservation());
            pstmt.executeUpdate();
            System.out.println("reservation mise à jour avec succes.");
        } catch (SQLException e) {
            System.err.println("erreur lors de la mise a jour de la reservation : " + e.getMessage());
        }
    }

    @Override
    public List<Reservation> getAll() {
        List<Reservation> reservations = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(GET_ALL_RESERVATIONS_SQL)) {
            while (rs.next()) {
                Reservation reservation = new Reservation();
                reservation.setId_reservation(rs.getInt("id_reservation"));
                reservation.setId_user(rs.getInt("id_user"));
                reservation.setId_event(rs.getInt("id_event"));
                reservation.setId_salle(rs.getInt("id_salle"));
                reservation.setId_terrain(rs.getInt("id_terrain"));
                reservation.setDuree(rs.getInt("duree"));
                reservation.setDate_reservation(rs.getTimestamp("date_reservation").toLocalDateTime());
                reservations.add(reservation);
            }
        } catch (SQLException e) {
            System.err.println("erreur lors de la selection des reservation : " + e.getMessage());
        }
        return reservations;
    }

    @Override
    public Reservation get(int id) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(GET_RESERVATION_BY_ID_SQL)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Reservation reservation = new Reservation();
                    reservation.setId_reservation(rs.getInt("id_reservation"));
                    reservation.setId_user(rs.getInt("id_user"));
                    reservation.setId_event(rs.getInt("id_event"));
                    reservation.setId_salle(rs.getInt("id_salle"));
                    reservation.setId_terrain(rs.getInt("id_terrain"));
                    reservation.setDuree(rs.getInt("duree"));
                    reservation.setDate_reservation(rs.getTimestamp("date_reservation").toLocalDateTime());
                    return reservation;
                }
            }
        } catch (SQLException e) {
            System.err.println("erreur lors de la selection des reservation : " + e.getMessage());
        }
        return null;
    }

    @Override
    public void add(Reservation reservation) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(INSERT_RESERVATION_SQL, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, reservation.getId_user());
            pstmt.setInt(2, reservation.getId_event());
            pstmt.setInt(3, reservation.getId_salle());
            pstmt.setInt(4, reservation.getId_terrain());
            pstmt.setTimestamp(5, Timestamp.valueOf(reservation.getDate_reservation()));
            pstmt.setInt(6, reservation.getDuree());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    reservation.setId_reservation(generatedKeys.getInt(1));
                }
            }
            System.out.println("reservation inséree avec succes.");
        } catch (SQLException e) {
            System.err.println("erreur lors de l'insertion de la reservation : " + e.getMessage());
        }
    }
    public boolean checkSalle(int salleId, LocalDateTime start, LocalDateTime end) {
        String query = "SELECT * FROM reservations WHERE id_salle = ? AND " +
                      "((date_reservation BETWEEN ? AND ?) OR " +  // New reservation starts during existing
                      "(DATE_ADD(date_reservation, INTERVAL duree MINUTE) BETWEEN ? AND ?) OR " +  // New reservation ends during existing
                      "(date_reservation <= ? AND DATE_ADD(date_reservation, INTERVAL duree MINUTE) >= ?))"; // New reservation spans existing
    
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, salleId);
            pstmt.setTimestamp(2, Timestamp.valueOf(start));
            pstmt.setTimestamp(3, Timestamp.valueOf(end));
            pstmt.setTimestamp(4, Timestamp.valueOf(start));
            pstmt.setTimestamp(5, Timestamp.valueOf(end));
            pstmt.setTimestamp(6, Timestamp.valueOf(start));
            pstmt.setTimestamp(7, Timestamp.valueOf(end));
    
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // Returns true if there's a conflict
            }
            
        } catch (SQLException e) {
            e.printStackTrace(); // You should properly handle this exception
            return false;
        }
    }}