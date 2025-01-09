package com.example.dd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.example.model.DatabaseConnection;
import com.example.model.Terrain;

public class TerrainDAO implements GenericDAO<Terrain> {

    private static final String DELETE_SQL = "DELETE FROM terrains WHERE id_terrain = ?";
    private static final String UPDATE_SQL = "UPDATE terrains SET nom_terrain = ?, type = ? WHERE id_terrain = ?";
    private static final String SELECT_ALL_SQL = "SELECT * FROM terrains";
    private static final String SELECT_BY_ID_SQL = "SELECT * FROM terrains WHERE id_terrain = ?";
    private static final String INSERT_SQL = "INSERT INTO terrains (nom_terrain, type) VALUES (?, ?)";

    private static final Logger LOGGER = Logger.getLogger(TerrainDAO.class.getName());

    @Override
    public void delete(int id) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(DELETE_SQL)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            LOGGER.log(Level.INFO, "Terrain deleted successfully.");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting terrain: {0}", e.getMessage());
        }
    }

    @Override
    public void update(Terrain terrain) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(UPDATE_SQL)) {
            pstmt.setString(1, terrain.getNom_terrain());
            pstmt.setString(2, terrain.getType());
            pstmt.setInt(3, terrain.getId_terrain());
            pstmt.executeUpdate();
            LOGGER.log(Level.INFO, "Terrain updated successfully.");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating terrain: {0}", e.getMessage());
        }
    }

    @Override
    public List<Terrain> getAll() {
        List<Terrain> terrains = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_SQL)) {
            while (rs.next()) {
                Terrain terrain = new Terrain();
                terrain.setId_terrain(rs.getInt("id_terrain"));
                terrain.setNom_terrain(rs.getString("nom_terrain"));
                terrain.setType(rs.getString("type"));
                terrains.add(terrain);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error selecting terrains: {0}", e.getMessage());
        }
        return terrains;
    }

    @Override
    public Terrain get(int id) {
        Terrain terrain = null;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_BY_ID_SQL)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    terrain = new Terrain();
                    terrain.setId_terrain(rs.getInt("id_terrain"));
                    terrain.setNom_terrain(rs.getString("nom_terrain"));
                    terrain.setType(rs.getString("type"));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error selecting terrain: {0}", e.getMessage());
        }
        return terrain;
    }

    @Override
    public void add(Terrain terrain) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, terrain.getNom_terrain());
            pstmt.setString(2, terrain.getType());
            pstmt.executeUpdate();
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    terrain.setId_terrain(generatedKeys.getInt(1));
                }
            }
            LOGGER.log(Level.INFO, "Terrain inserted successfully.");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error inserting terrain: {0}", e.getMessage());
        }
    }
}