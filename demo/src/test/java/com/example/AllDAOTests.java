package com.example;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import com.example.model.*;
import com.example.dd.*;


public class AllDAOTests {
    private ReservationDAO reservationDAO;
    private SalleDAO salleDAO;
    private TerrainDAO terrainDAO;
    private UtilisateurDAO utilisateurDAO;
    private EvenmentsDAO evenementsDAO;

    private User testUser;
    private Evenement testEvent;
    private Salle testSalle;
    private Terrain testTerrain;
    private Reservation testReservation;

    @Before
    public void setUp() {
        reservationDAO = new ReservationDAO();
        salleDAO = new SalleDAO();
        terrainDAO = new TerrainDAO();
        utilisateurDAO = new UtilisateurDAO();
        evenementsDAO = new EvenmentsDAO();

        setupTestData();
    }

    private void setupTestData() {
        testUser = new User();
        testUser.setNom("Test");
        testUser.setPrenom("User");
        testUser.setEmail("test@example.com");
        testUser.setType("normal");
        utilisateurDAO.add(testUser);

        testEvent = new Evenement();
        testEvent.setNomEvent("Test Event");
        testEvent.setDateEvent(LocalDateTime.now());
        testEvent.setDescription("Test Description");
        testEvent.setIdUser(testUser.getIdUser());
        evenementsDAO.add(testEvent);

        testSalle = new Salle();
        testSalle.setNom_salle("Test Salle");
        testSalle.setCapacite(100);
        salleDAO.add(testSalle);

        testTerrain = new Terrain();
        testTerrain.setNom_terrain("Test Terrain");
        testTerrain.setType("Football");
        terrainDAO.add(testTerrain);

        testReservation = new Reservation();
        testReservation.setId_user(testUser.getIdUser());
        testReservation.setId_event(testEvent.getIdEvent());
        testReservation.setId_salle(testSalle.getId_salle());
        testReservation.setId_terrain(testTerrain.getId_terrain());
        testReservation.setDate_reservation(LocalDateTime.now());
    }

    @After
    public void tearDown() {
        if (testReservation.getId_reservation() != 0) {
            reservationDAO.delete(testReservation.getId_reservation());
        }
        if (testSalle.getId_salle() != 0) {
            salleDAO.delete(testSalle.getId_salle());
        }
        if (testTerrain.getId_terrain() != 0) {
            terrainDAO.delete(testTerrain.getId_terrain());
        }
        if (testEvent.getIdEvent() != 0) {
            evenementsDAO.delete(testEvent.getIdEvent());
        }
        if (testUser.getIdUser() != 0) {
            utilisateurDAO.delete(testUser.getIdUser());
        }
    }

    @Test
    public void testSalleCRUD() {
        Salle newSalle = new Salle();
        newSalle.setNom_salle("Nouvelle Salle");
        newSalle.setCapacite(50);
        salleDAO.add(newSalle);
        assertTrue("L'ID de la salle devrait être défini", newSalle.getId_salle() > 0);

        Salle retrievedSalle = salleDAO.get(newSalle.getId_salle());
        assertNotNull("La salle récupérée ne devrait pas être null", retrievedSalle);
        assertEquals("Le nom devrait correspondre", "Nouvelle Salle", retrievedSalle.getNom_salle());

        retrievedSalle.setCapacite(75);
        salleDAO.update(retrievedSalle);
        Salle updatedSalle = salleDAO.get(newSalle.getId_salle());
        assertEquals("La capacité devrait être mise à jour", 75, (int) updatedSalle.getCapacite());

        salleDAO.delete(newSalle.getId_salle());
        assertNull("La salle devrait être supprimée", salleDAO.get(newSalle.getId_salle()));
    }

    @Test
    public void testTerrainCRUD() {
        Terrain newTerrain = new Terrain();
        newTerrain.setNom_terrain("Nouveau Terrain");
        newTerrain.setType("Basketball");
        terrainDAO.add(newTerrain);
        assertTrue("L'ID du terrain devrait être défini", newTerrain.getId_terrain() > 0);

        Terrain retrievedTerrain = terrainDAO.get(newTerrain.getId_terrain());
        assertNotNull("Le terrain récupéré ne devrait pas être null", retrievedTerrain);
        assertEquals("Le type devrait correspondre", "Basketball", retrievedTerrain.getType());

        retrievedTerrain.setType("Tennis");
        terrainDAO.update(retrievedTerrain);
        Terrain updatedTerrain = terrainDAO.get(newTerrain.getId_terrain());
        assertEquals("Le type devrait être mis à jour", "Tennis", updatedTerrain.getType());

        terrainDAO.delete(newTerrain.getId_terrain());
        assertNull("Le terrain devrait être supprimé", terrainDAO.get(newTerrain.getId_terrain()));
    }
}
