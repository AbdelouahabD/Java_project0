package com.example.controllers;


import com.example.model.Reservation;
import com.example.dd.ReservationDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
public class ReservationController {
    // Reservation Tab Components
@FXML private TableView<Reservation> reservationTable;
@FXML private TableColumn<Reservation, Integer> reservationIdColumn;
@FXML private TableColumn<Reservation, Integer> userIdColumn;
@FXML private TableColumn<Reservation, Integer> eventIdColumn;
@FXML private TableColumn<Reservation, Integer> roomIdColumn;
@FXML private TableColumn<Reservation, Integer> terrainIdColumn;
@FXML private TableColumn<Reservation, LocalDateTime> dateReservationColumn;
@FXML private TextField searchReservationField;

    // Data Access Objects
    private final ReservationDAO reservationDAO = new ReservationDAO();
    // Observable Lists for Tables
    private final ObservableList<Reservation> reservationList = FXCollections.observableArrayList();
    // Filtered Lists for Search
    private FilteredList<Reservation> filteredReservations;

    @FXML
    public void initialize() {
        initializeTable();
        setupSearch();
        loadReservations();
    }
    private void loadReservations() {
        try {
            reservationList.setAll(reservationDAO.getAll());
        } catch (Exception e) {
            showError("Erreur de chargement", "Impossible de charger les réservations : " + e.getMessage());
        }
    }
    private void initializeTable() {
        reservationIdColumn.setCellValueFactory(new PropertyValueFactory<>("id_reservation"));
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("id_user"));
        eventIdColumn.setCellValueFactory(new PropertyValueFactory<>("id_event"));
        roomIdColumn.setCellValueFactory(new PropertyValueFactory<>("id_salle"));
        terrainIdColumn.setCellValueFactory(new PropertyValueFactory<>("id_terrain"));
        dateReservationColumn.setCellValueFactory(new PropertyValueFactory<>("date_reservation"));
        
        filteredReservations = new FilteredList<>(reservationList, p -> true);
        reservationTable.setItems(filteredReservations);
    }
    
    
    private void setupSearch() {
        searchReservationField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredReservations.setPredicate(reservation -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lowerCaseFilter = newValue.toLowerCase();
                return String.valueOf(reservation.getId_user()).contains(lowerCaseFilter) ||
                       String.valueOf(reservation.getId_event()).contains(lowerCaseFilter) ||
                       String.valueOf(reservation.getId_salle()).contains(lowerCaseFilter) ||
                       String.valueOf(reservation.getId_terrain()).contains(lowerCaseFilter);
            });
        });

    }


    @FXML
private void addReservation() {
    Dialog<Reservation> dialog = createReservationDialog("Ajouter une réservation", null);
    dialog.showAndWait().ifPresent(reservation -> {
        try {
            reservationDAO.add(reservation);
            loadReservations();
            showInfo("Succès", "Réservation ajoutée avec succès");
        } catch (Exception e) {
            showError("Erreur d'ajout", "Impossible d'ajouter la réservation : " + e.getMessage());
        }
    });
}

@FXML
private void editReservation() {
    Reservation selected = reservationTable.getSelectionModel().getSelectedItem();
    if (selected == null) {
        showWarning("Sélection requise", "Veuillez sélectionner une réservation à modifier");
        return;
    }
    
    Dialog<Reservation> dialog = createReservationDialog("Modifier la réservation", selected);
    dialog.showAndWait().ifPresent(reservation -> {
        try {
            reservationDAO.update(reservation);
            loadReservations();
            showInfo("Succès", "Réservation modifiée avec succès");
        } catch (Exception e) {
            showError("Erreur de modification", "Impossible de modifier la réservation : " + e.getMessage());
        }
    });
}

@FXML
private void deleteReservation() {
    Reservation selected = reservationTable.getSelectionModel().getSelectedItem();
    if (selected == null) {
        showWarning("Sélection requise", "Veuillez sélectionner une réservation à supprimer");
        return;
    }
    
    if (showConfirmation("Confirmation", "Voulez-vous vraiment supprimer cette réservation ?")) {
        try {
            reservationDAO.delete(selected.getId_reservation());
            loadReservations();
            showInfo("Succès", "Réservation supprimée avec succès");
        } catch (Exception e) {
            showError("Erreur de suppression", "Impossible de supprimer la réservation : " + e.getMessage());
        }
    }
}


private Dialog<Reservation> createReservationDialog(String title, Reservation reservation) {
    Dialog<Reservation> dialog = new Dialog<>();
    dialog.setTitle(title);
    
    ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
    
    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    
    TextField userIdField = new TextField();
    TextField eventIdField = new TextField();
    TextField salleIdField = new TextField();
    TextField terrainIdField = new TextField();
    DatePicker datePicker = new DatePicker();
    TextField timeField = new TextField();
    
    if (reservation != null) {
        userIdField.setText(String.valueOf(reservation.getId_user()));
        eventIdField.setText(String.valueOf(reservation.getId_event()));
        salleIdField.setText(String.valueOf(reservation.getId_salle()));
        terrainIdField.setText(String.valueOf(reservation.getId_terrain()));
        datePicker.setValue(reservation.getDate_reservation().toLocalDate());
        timeField.setText(reservation.getDate_reservation().toLocalTime().toString());
    }
    
    grid.add(new Label("ID Utilisateur:"), 0, 0);
    grid.add(userIdField, 1, 0);
    grid.add(new Label("ID Événement:"), 0, 1);
    grid.add(eventIdField, 1, 1);
    grid.add(new Label("ID Salle:"), 0, 2);
    grid.add(salleIdField, 1, 2);
    grid.add(new Label("ID Terrain:"), 0, 3);
    grid.add(terrainIdField, 1, 3);
    grid.add(new Label("Date:"), 0, 4);
    grid.add(datePicker, 1, 4);
    grid.add(new Label("Heure:"), 0, 5);
    grid.add(timeField, 1, 5);
    
    dialog.getDialogPane().setContent(grid);
    
    dialog.setResultConverter(dialogButton -> {
        if (dialogButton == saveButtonType) {
            try {
                LocalDateTime dateTime = LocalDateTime.of(
                    datePicker.getValue(),
                    LocalTime.parse(timeField.getText())
                );
                
                if (reservation == null) {
                    return new Reservation(
                        Integer.parseInt(userIdField.getText()),
                        Integer.parseInt(eventIdField.getText()),
                        Integer.parseInt(salleIdField.getText()),
                        Integer.parseInt(terrainIdField.getText()),
                        dateTime
                    );
                } else {
                    reservation.setId_user(Integer.parseInt(userIdField.getText()));
                    reservation.setId_event(Integer.parseInt(eventIdField.getText()));
                    reservation.setId_salle(Integer.parseInt(salleIdField.getText()));
                    reservation.setId_terrain(Integer.parseInt(terrainIdField.getText()));
                    reservation.setDate_reservation(dateTime);
                    return reservation;
                }
            } catch (NumberFormatException e) {
                showError("Erreur de saisie", "Les IDs doivent être des nombres entiers");
                return null;
            } catch (DateTimeParseException e) {
                showError("Erreur de saisie", "Format d'heure invalide (utilisez HH:mm)");
                return null;
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
