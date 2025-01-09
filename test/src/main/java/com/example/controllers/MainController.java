package com.example.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class MainController {
    @FXML
    private TabPane mainTabPane;

    @FXML
    public void showUsersView() {
        loadTab("Utilisateurs", "/users.fxml");
    }

    @FXML
    public void showRoomsView() {
        loadTab("Salles", "/rooms.fxml");
    }

    @FXML
    public void showTerrainView() {
        loadTab("Terrains", "/terrains.fxml");
    }

    
    @FXML
public void showEventView(){
    loadTab("Evènements", "/evenements.fxml");
    
}
@FXML
public void showReservationsView(){
    loadTab("Réservations", "/reservations.fxml");
    
}

    /**
     * Loads the specified FXML file into a new or existing tab.
     * 
     * @param tabName The name of the tab.
     * @param resourcePath The path to the FXML file to load.
     */
    private void loadTab(String tabName, String resourcePath) {
        // Check if the tab already exists
        for (Tab tab : mainTabPane.getTabs()) {
            if (tab.getText().equals(tabName)) {
                mainTabPane.getSelectionModel().select(tab);
                return;
            }
        }

        try {
            // Load the new view
            FXMLLoader loader = new FXMLLoader(getClass().getResource(resourcePath));
            Pane view = loader.load();

            // Create a new tab
            Tab newTab = new Tab(tabName);
            newTab.setContent(view);
            newTab.setClosable(true); // Allow the user to close the tab

            // Add the tab to the tab pane
            mainTabPane.getTabs().add(newTab);
            mainTabPane.getSelectionModel().select(newTab);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Erreur", "Impossible de charger la vue : " + tabName);
        }
    }

    /**
     * Displays an error message in case of a problem.
     * 
     * @param title The title of the error message.
     * @param message The content of the error message.
     */
    private void showError(String title, String message) {
        System.err.println(title + ": " + message);
    }
}
