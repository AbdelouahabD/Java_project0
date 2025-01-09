package com.example.controllers;

import com.example.model.Salle;
import com.example.dd.SalleDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

public class RoomController {
    @FXML private TableView<Salle> roomTable;
    @FXML private TableColumn<Salle, Integer> roomIdColumn;
    @FXML private TableColumn<Salle, String> roomNameColumn;
    @FXML private TableColumn<Salle, Integer> roomCapacityColumn;
    @FXML private TextField searchRoomField;
    
    private final SalleDAO salleDAO = new SalleDAO();
    private final ObservableList<Salle> roomList = FXCollections.observableArrayList();
    private FilteredList<Salle> filteredRooms;
    
    @FXML
    public void initialize() {
        initializeTable();
        setupSearch();
        loadRooms();
    }
    
    private void initializeTable() {
        roomIdColumn.setCellValueFactory(new PropertyValueFactory<>("id_salle"));
        roomNameColumn.setCellValueFactory(new PropertyValueFactory<>("nom_salle"));
        roomCapacityColumn.setCellValueFactory(new PropertyValueFactory<>("capacite"));
        
        filteredRooms = new FilteredList<>(roomList, p -> true);
        roomTable.setItems(filteredRooms);
    }
    
    private void setupSearch() {
        searchRoomField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredRooms.setPredicate(room -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lowerCaseFilter = newValue.toLowerCase();
                return room.getNom_salle().toLowerCase().contains(lowerCaseFilter);
            });
        });
    }
    
    @FXML
    private void addRoom() {
        Dialog<Salle> dialog = createRoomDialog("Ajouter une salle", null);
        dialog.showAndWait().ifPresent(room -> {
            try {
                salleDAO.add(room);
                loadRooms();
                showInfo("Succès", "Salle ajoutée avec succès");
            } catch (Exception e) {
                showError("Erreur d'ajout", "Impossible d'ajouter la salle : " + e.getMessage());
            }
        });
    }
    
    @FXML
    private void editRoom() {
        Salle selected = roomTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Sélection requise", "Veuillez sélectionner une salle à modifier");
            return;
        }
        
        Dialog<Salle> dialog = createRoomDialog("Modifier la salle", selected);
        dialog.showAndWait().ifPresent(room -> {
            try {
                salleDAO.update(room);
                loadRooms();
                showInfo("Succès", "Salle modifiée avec succès");
            } catch (Exception e) {
                showError("Erreur de modification", "Impossible de modifier la salle : " + e.getMessage());
            }
        });
    }
    
    @FXML
    private void deleteRoom() {
        Salle selected = roomTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Sélection requise", "Veuillez sélectionner une salle à supprimer");
            return;
        }
        
        if (showConfirmation("Confirmation", "Voulez-vous vraiment supprimer cette salle ?")) {
            try {
                salleDAO.delete(selected.getId_salle());
                loadRooms();
                showInfo("Succès", "Salle supprimée avec succès");
            } catch (Exception e) {
                showError("Erreur de suppression", "Impossible de supprimer la salle : " + e.getMessage());
            }
        }
    }
    
    private void loadRooms() {
        try {
            roomList.setAll(salleDAO.getAll());
        } catch (Exception e) {
            showError("Erreur de chargement", "Impossible de charger les salles : " + e.getMessage());
        }
    }
    
    private Dialog<Salle> createRoomDialog(String title, Salle room) {
        Dialog<Salle> dialog = new Dialog<>();
        dialog.setTitle(title);
        
        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        
        TextField nomField = new TextField();
        TextField capaciteField = new TextField();
        
        if (room != null) {
            nomField.setText(room.getNom_salle());
            capaciteField.setText(String.valueOf(room.getCapacite()));
        }
        
        grid.add(new Label("Nom:"), 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(new Label("Capacité:"), 0, 1);
        grid.add(capaciteField, 1, 1);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    int capacite = Integer.parseInt(capaciteField.getText());
                    if (room == null) {
                        return new Salle(nomField.getText(), capacite);
                    } else {
                        room.setNom_salle(nomField.getText());
                        room.setCapacite(capacite);
                        return room;
                    }
                } catch (NumberFormatException e) {
                    showError("Erreur de saisie", "La capacité doit être un nombre entier");
                    return null;
                }
            }
            return null;
        });
        
        return dialog;
    }
    
    // Utility methods for showing dialogs (same as in UserController)
    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showWarning(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showInfo(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private boolean showConfirmation(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }
}