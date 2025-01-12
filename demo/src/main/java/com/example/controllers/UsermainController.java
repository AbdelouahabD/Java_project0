package com.example.controllers;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;

public class UsermainController {
      
   
     @FXML
    private TabPane mainTabPane;
    private int id;
    public void setId(int id) {
        this.id = id;
    }

    @FXML
public void showEventView(){
    String tabName = "Evenement";
    String resourcePath = "/userEvent.fxml";

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

        // Retrieve the controller
        UserReservationController controller = loader.getController();
        controller.setUserId(id); // Pass the userId to the controller
        controller.initialize(); // Manually invoke initialization logic if needed

        // Create a new tab
        Tab newTab = new Tab(tabName);
        newTab.setContent(view);
        newTab.setClosable(true);

        // Add the tab to the TabPane
        mainTabPane.getTabs().add(newTab);
        mainTabPane.getSelectionModel().select(newTab);
    } catch (IOException e) {
        e.printStackTrace();
        showError("Erreur", "Impossible de charger la vue : " + tabName);
    }
    
}
@FXML
public void showReservationsView() {
    String tabName = "Réservations";
    String resourcePath = "/userReservation.fxml";

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

        // Retrieve the controller
        UserReservationController controller = loader.getController();
        controller.setUserId(id); // Pass the userId to the controller
        controller.initialize(); // Manually invoke initialization logic if needed

        // Create a new tab
        Tab newTab = new Tab(tabName);
        newTab.setContent(view);
        newTab.setClosable(true);

        // Add the tab to the TabPane
        mainTabPane.getTabs().add(newTab);
        mainTabPane.getSelectionModel().select(newTab);
    } catch (IOException e) {
        e.printStackTrace();
        showError("Erreur", "Impossible de charger la vue : " + tabName);
    }
}



    
//     private void loadTab(String tabName, String resourcePath) {
//         // Check if the tab already exists
//         for (Tab tab : mainTabPane.getTabs()) {
//             if (tab.getText().equals(tabName)) {
//                 mainTabPane.getSelectionModel().select(tab);
//                 return;
//             }
//         }

//         try {
//             // Load the new view
//             FXMLLoader loader = new FXMLLoader(getClass().getResource(resourcePath));
            
//             Pane view = loader.load();

//             // Create a new tab
//             Tab newTab = new Tab(tabName);
//             newTab.setContent(view);
//             newTab.setClosable(true); // Allow the user to close the tab

//             // Add the tab to the tab pane
//             mainTabPane.getTabs().add(newTab);
//             mainTabPane.getSelectionModel().select(newTab);
//         } catch (IOException e) {
//             e.printStackTrace();
//             showError("Erreur", "Impossible de charger la vue : " + tabName);
//         }
//     }
  private void showError(String title, String message) {
        System.err.println(title + ": " + message);
    }
    
}
