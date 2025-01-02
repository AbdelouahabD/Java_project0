package com.example.controllers;

import com.example.model.*;
import com.example.dd.*;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

public class RoomsController {

    @FXML
    private TableView<Salle> salleTable;
    @FXML
    private TableColumn<Salle, Integer> idColumn;
    @FXML
    private TableColumn<Salle, String> nomColumn;
    @FXML
    private TableColumn<Salle, Integer> capaciteColumn;

    private SalleDAO roomDAO;
    private ObservableList<Salle> roomList;

    @FXML
    public void initialize() {
        roomDAO = new SalleDAO();
        roomList = FXCollections.observableArrayList();

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id_salle"));
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom_salle"));
        capaciteColumn.setCellValueFactory(new PropertyValueFactory<>("capacite"));

        loadRooms();
    }

    private void loadRooms() {
        try {
            roomList.setAll(roomDAO.getAll());
            salleTable.setItems(roomList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void addRoom() {
        // Créez une boîte de dialogue pour saisir les informations de la salle
        Dialog<Salle> dialog = new Dialog<>();
        dialog.setTitle("Ajouter une salle");
        dialog.setHeaderText("Entrez les informations de la salle");

        // Configurez les boutons OK et Annuler
        ButtonType okButtonType = new ButtonType("Ajouter", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        // Créez les champs de formulaire
        TextField nomSalleField = new TextField();
        nomSalleField.setPromptText("Nom de la salle");
        TextField capaciteField = new TextField();
        capaciteField.setPromptText("Capacité");

        // Ajoutez les champs au formulaire
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Nom de la salle:"), 0, 0);
        grid.add(nomSalleField, 1, 0);
        grid.add(new Label("Capacité:"), 0, 1);
        grid.add(capaciteField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        // Convertissez les résultats de la boîte de dialogue en objet Salle
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                return new Salle(nomSalleField.getText(), Integer.parseInt(capaciteField.getText()));
            }
            return null;
        });

        // Affichez la boîte de dialogue et récupérez les résultats
        dialog.showAndWait().ifPresent(salle -> {
            try {
                roomDAO.add(salle); // Méthode pour insérer dans la base de données
                loadRooms(); // Rechargez les salles après ajout
            } catch (Exception e) {
                showError("Erreur d'ajout", "Impossible d'ajouter la salle : " + e.getMessage());
            }
        });
    }

    @FXML
    private void editRoom() {
        Salle selected = salleTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Sélection requise", "Veuillez sélectionner une salle à modifier");
            return;
        }

        Dialog<Salle> dialog = new Dialog<>();
        dialog.setTitle("Modifier la salle");
        dialog.setHeaderText("Modifiez les informations de la salle");

        ButtonType okButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        TextField nomSalleField = new TextField(selected.getNom_salle());
        TextField capaciteField = new TextField(String.valueOf(selected.getCapacite()));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Nom de la salle:"), 0, 0);
        grid.add(nomSalleField, 1, 0);
        grid.add(new Label("Capacité:"), 0, 1);
        grid.add(capaciteField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                selected.setNom_salle(nomSalleField.getText());
                selected.setCapacite(Integer.parseInt(capaciteField.getText()));
                return selected;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(salle -> {
            try {
                roomDAO.update(salle); // Méthode pour mettre à jour dans la base de données
                loadRooms();
            } catch (Exception e) {
                showError("Erreur de modification", "Impossible de modifier la salle : " + e.getMessage());
            }
        });
    }

    @FXML
    private void deleteRoom() {
        Salle selected = salleTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Sélection requise", "Veuillez sélectionner une salle à supprimer");
            return;
        }

        if (showConfirmation("Confirmation", "Voulez-vous vraiment supprimer cette salle ?")) {
            try {
                roomDAO.delete(selected.getId_salle());
                loadRooms();
            } catch (Exception e) {
                showError("Erreur de suppression", "Impossible de supprimer la salle : " + e.getMessage());
            }
        }
    }

    private void showError(String title, String message) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(message);

        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(okButton);

        dialog.showAndWait();
    }

    private void showWarning(String title, String message) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(message);

        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(okButton);

        dialog.showAndWait();
    }

    private boolean showConfirmation(String title, String message) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(message);

        ButtonType confirmButton = new ButtonType("Confirmer", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);

        dialog.getDialogPane().getButtonTypes().addAll(confirmButton, cancelButton);

        return dialog.showAndWait().filter(response -> response == confirmButton).isPresent();
    }
}
