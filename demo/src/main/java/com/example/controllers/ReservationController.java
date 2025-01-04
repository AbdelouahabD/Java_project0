package com.example.controllers;



import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

import com.example.model.*;
import com.example.dd.*;
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
    
    ComboBox<User> userIdField = new ComboBox<>();
    ComboBox<Evenement> eventIdField = new ComboBox<>();
    ComboBox<Salle> salleIdField = new ComboBox<>();
    ComboBox<Terrain> terrainIdField = new ComboBox<>();
    DatePicker datePicker = new DatePicker();
    ComboBox<String> timeField = new ComboBox<>();
    ComboBox<Time> duree=new ComboBox<>();
    Time t1=new Time("30 min",30);
    Time t2=new Time("1h",60);
    Time t3=new Time("1h 30 min",90);
    Time t4=new Time("2h",120);
    Time t5=new Time("2h 30 min",150);
    Time t6=new Time("3h",180);

    duree.getItems().addAll(t1,t2,t3,t4,t5,t6);
    datePicker.setDayCellFactory(picker -> new javafx.scene.control.DateCell() {
        @Override
        public void updateItem(LocalDate item, boolean empty) {
            super.updateItem(item, empty);
            // Disable past dates
            if (item.isBefore(LocalDate.now())) {
                setDisable(true);
                setStyle("-fx-background-color: #f0f0f0;"); // Optional: Style to show disabled dates
            }
        }
    });
    // Store all times in a List for reuse
List<String> allTimes = Arrays.asList(
    "08:30","08:45","09:00","09:15","09:30","09:45",
    "10:00","10:15","10:30","10:45","11:00","11:15",
    "11:30","11:45","12:00","12:15","12:30","12:45",
    "13:00","13:15","13:30","13:45","14:00","14:15",
    "14:30","14:45","15:00","15:15","15:30","15:45",
    "16:00","16:15","16:30","16:45","17:00","17:15",
    "17:30","17:45","18:00","18:15","18:30","18:45",
    "19:00","19:15","19:30","19:45","20:00","20:15",
    "20:30","20:45","21:00","21:15","21:30","21:45",
    "22:00"
);

datePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
    // Clear previous time selections
    timeField.getItems().clear();
     long currentTimeMillis = System.currentTimeMillis();
    LocalTime currentTime = LocalTime.now(Clock.fixed(Instant.ofEpochMilli(currentTimeMillis), ZoneId.systemDefault()));
    if (newValue == null) return;
    
    // If the selected date is today, filter times after the current time
    if (newValue.equals(LocalDate.now())) {
        // Force a fresh read of the current time by creating a new instance
       
        System.out.println(currentTime);
        
        for (String time : allTimes) {
            // Extract time from string (e.g., "08:30" -> 08:30)
            String[] parts = time.split(":");
            LocalTime timeOption = LocalTime.of(
                Integer.parseInt(parts[0]), 
                Integer.parseInt(parts[1])
            );
            
            // Add time options greater than the current time
            if (timeOption.isAfter(currentTime)) {
                timeField.getItems().add(time);
            }
        }
    } else {
        // Add all time options if the date is not today
        timeField.getItems().addAll(allTimes);
    }
});
                            datePicker.setValue(LocalDate.now());
                    
    if (reservation != null) {
        userIdField.setValue(new UtilisateurDAO().get(reservation.getId_user()));
        eventIdField.setValue(new EvenmentsDAO().get(reservation.getId_event()));
        salleIdField.setValue(new SalleDAO().get(reservation.getId_salle()));
        terrainIdField.setValue(new TerrainDAO().get(reservation.getId_terrain()));
        datePicker.setValue(reservation.getDate_reservation().toLocalDate());
        timeField.setValue(reservation.getDate_reservation().toLocalTime().toString());
    }
    for (User user : new UtilisateurDAO().getAll()) {
        userIdField.getItems().add(user);
       }
       
       for (Evenement event : new EvenmentsDAO().getAll()) {
        eventIdField.getItems().add(event);
       }
       for (Salle salle : new SalleDAO().getAll()) {
        salleIdField.getItems().add(salle);
       }

       for (Terrain terrain : new TerrainDAO().getAll()) {
        terrainIdField.getItems().add(terrain);
       }
    
    grid.add(new Label("Utilisateur:"), 0, 0);
    grid.add(userIdField, 1, 0);
    grid.add(new Label("Événement:"), 0, 1);
    grid.add(eventIdField, 1, 1);
    grid.add(new Label("Salle:"), 0, 2);
    grid.add(salleIdField, 1, 2);
    grid.add(new Label("Terrain:"), 0, 3);
    grid.add(terrainIdField, 1, 3);
    grid.add(new Label("Date:"), 0, 4);
    grid.add(datePicker, 1, 4);
    grid.add(new Label("Heure:"), 0, 5);
    grid.add(timeField, 1, 5);
    grid.add(new Label("Duree:"), 0, 6);
    grid.add(duree, 1, 6);
    
    dialog.getDialogPane().setContent(grid);
    
    dialog.setResultConverter(dialogButton -> {
        if (dialogButton == saveButtonType) {
            try {
                LocalDateTime dateTime = LocalDateTime.of(
                    datePicker.getValue(),
                    LocalTime.parse(timeField.getSelectionModel().getSelectedItem())
                );
                
                if (reservation == null) {
                    LocalDateTime startDateTime = dateTime;
                    LocalDateTime endDateTime = dateTime.plusMinutes(duree.getSelectionModel().getSelectedItem().getInteger());
                    System.out.println(startDateTime);
                    System.out.println(endDateTime);
                              if(new ReservationDAO().checkSalle(salleIdField.getSelectionModel().getSelectedItem().getId_salle(), startDateTime, endDateTime)){
                        showError("Erreur de reservation", "le salle est reserve dans ce temps ");
                return null;
                    }
                    return new Reservation(
                        userIdField.getSelectionModel().getSelectedItem().getIdUser(),
                        eventIdField.getSelectionModel().getSelectedItem().getIdEvent(),
                        salleIdField.getSelectionModel().getSelectedItem().getId_salle(),
                        terrainIdField.getSelectionModel().getSelectedItem().getId_terrain(),
                        dateTime,
                        duree.getSelectionModel().getSelectedItem().getInteger()
                    );
                } else {
                    reservation.setId_user(userIdField.getSelectionModel().getSelectedItem().getIdUser());
                    reservation.setId_event(eventIdField.getSelectionModel().getSelectedItem().getIdEvent());
                    reservation.setId_salle(salleIdField.getSelectionModel().getSelectedItem().getId_salle());
                    reservation.setId_terrain(terrainIdField.getSelectionModel().getSelectedItem().getId_terrain());
                    reservation.setDate_reservation(dateTime);
                    reservation.setDuree(duree.getSelectionModel().getSelectedItem().getInteger());
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
