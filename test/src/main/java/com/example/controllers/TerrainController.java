package com.example.controllers;

import com.example.model.Terrain;
import com.example.dd.TerrainDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

public class TerrainController {
    @FXML private TableView<Terrain> terrainTable;
    @FXML private TableColumn<Terrain, Integer> terrainIdColumn;
    @FXML private TableColumn<Terrain, String> terrainNameColumn;
    @FXML private TableColumn<Terrain, String> terrainTypeColumn;
    @FXML private TextField searchTerrainField;
    
    private final TerrainDAO terrainDAO = new TerrainDAO();
    private final ObservableList<Terrain> terrainList = FXCollections.observableArrayList();
    private FilteredList<Terrain> filteredTerrains;
    
    @FXML
    public void initialize() {
        initializeTable();
        setupSearch();
        loadTerrains();
    }
    
    private void initializeTable() {
        terrainIdColumn.setCellValueFactory(new PropertyValueFactory<>("id_terrain"));
        terrainNameColumn.setCellValueFactory(new PropertyValueFactory<>("nom_terrain"));
        terrainTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        
        filteredTerrains = new FilteredList<>(terrainList, p -> true);
        terrainTable.setItems(filteredTerrains);
    }
    
    private void setupSearch() {
        searchTerrainField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredTerrains.setPredicate(terrain -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lowerCaseFilter = newValue.toLowerCase();
                return terrain.getNom_terrain().toLowerCase().contains(lowerCaseFilter) ||
                       terrain.getType().toLowerCase().contains(lowerCaseFilter);
            });
        });
    }
    
    @FXML
    private void addTerrain() {
        Dialog<Terrain> dialog = createTerrainDialog("Ajouter un terrain", null);
        dialog.showAndWait().ifPresent(terrain -> {
            try {
                terrainDAO.add(terrain);
                loadTerrains();
                showInfo("Succès", "Terrain ajouté avec succès");
            } catch (Exception e) {
                showError("Erreur d'ajout", "Impossible d'ajouter le terrain : " + e.getMessage());
            }
        });
    }
    
    @FXML
    private void editTerrain() {
        Terrain selected = terrainTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Sélection requise", "Veuillez sélectionner un terrain à modifier");
            return;
        }
        
        Dialog<Terrain> dialog = createTerrainDialog("Modifier le terrain", selected);
        dialog.showAndWait().ifPresent(terrain -> {
            try {
                terrainDAO.update(terrain);
                loadTerrains();
                showInfo("Succès", "Terrain modifié avec succès");
            } catch (Exception e) {
                showError("Erreur de modification", "Impossible de modifier le terrain : " + e.getMessage());
            }
        });
    }
    
    @FXML
    private void deleteTerrain() {
        Terrain selected = terrainTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Sélection requise", "Veuillez sélectionner un terrain à supprimer");
            return;
        }
        
        if (showConfirmation("Confirmation", "Voulez-vous vraiment supprimer ce terrain ?")) {
            try {
                terrainDAO.delete(selected.getId_terrain());
                loadTerrains();
                showInfo("Succès", "Terrain supprimé avec succès");
            } catch (Exception e) {
                showError("Erreur de suppression", "Impossible de supprimer le terrain : " + e.getMessage());
            }
        }
    }
    
    private void loadTerrains() {
        try {
            terrainList.setAll(terrainDAO.getAll());
        } catch (Exception e) {
            showError("Erreur de chargement", "Impossible de charger les terrains : " + e.getMessage());
        }
    }
    
    private Dialog<Terrain> createTerrainDialog(String title, Terrain terrain) {
        Dialog<Terrain> dialog = new Dialog<>();
        dialog.setTitle(title);
        
        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        
        TextField nomField = new TextField();
        TextField typeField = new TextField();
        
        if (terrain != null) {
            nomField.setText(terrain.getNom_terrain());
            typeField.setText(terrain.getType());
        }
        
        grid.add(new Label("Nom:"), 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(new Label("Type:"), 0, 1);
        grid.add(typeField, 1, 1);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                if (terrain == null) {
                    return new Terrain(nomField.getText(), typeField.getText());
                } else {
                    terrain.setNom_terrain(nomField.getText());
                    terrain.setType(typeField.getText());
                    return terrain;
                }
            }
            return null;
        });
        
        return dialog;
    }
    
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