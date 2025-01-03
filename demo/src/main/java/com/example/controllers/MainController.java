package com.example.controllers;

import com.example.model.Evenement;
import com.example.model.Reservation;
import com.example.model.Salle;
import com.example.model.User;
import com.example.model.Terrain;
import com.example.dd.EvenmentsDAO;
import com.example.dd.ReservationDAO;
import com.example.dd.SalleDAO;
import com.example.dd.UtilisateurDAO;
import com.example.dd.TerrainDAO;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
public class MainController {
    
    // région FXML Injections
    @FXML private TabPane mainTabPane;
    
    // User Tab Components
    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, Integer> idColumn;
    @FXML private TableColumn<User, String> nomColumn;
    @FXML private TableColumn<User, String> prenomColumn;
    @FXML private TableColumn<User, String> emailColumn;
    @FXML private TableColumn<User, String> roleColumn;
    @FXML private TextField searchUserField;
    
    // Room Tab Components
    @FXML private TableView<Salle> roomTable;
    @FXML private TableColumn<Salle, Integer> roomIdColumn;
    @FXML private TableColumn<Salle, String> roomNameColumn;
    @FXML private TableColumn<Salle, Integer> roomCapacityColumn;
    @FXML private TextField searchRoomField;
    
    // Terrain Tab Components
    @FXML private TableView<Terrain> terrainTable;
    @FXML private TableColumn<Terrain, Integer> terrainIdColumn;
    @FXML private TableColumn<Terrain, String> terrainNameColumn;
    @FXML private TableColumn<Terrain, String> terrainTypeColumn;
    @FXML private TextField searchTerrainField;

    // Event Tab Components
@FXML private TableView<Evenement> eventTable;
@FXML private TableColumn<Evenement, Integer> eventIdColumn;
@FXML private TableColumn<Evenement, String> eventNameColumn;
@FXML private TableColumn<Evenement, LocalDateTime> eventDateColumn;
@FXML private TableColumn<Evenement, String> eventDescriptionColumn;
@FXML private TableColumn<Evenement, Integer> eventUserIdColumn;
@FXML private TextField searchEventField;

// Reservation Tab Components
@FXML private TableView<Reservation> reservationTable;
@FXML private TableColumn<Reservation, Integer> reservationIdColumnR;
@FXML private TableColumn<Reservation, Integer> userIdColumnR;
@FXML private TableColumn<Reservation, Integer> eventIdColumnR;
@FXML private TableColumn<Reservation, Integer> roomIdColumnR;
@FXML private TableColumn<Reservation, Integer> terrainIdColumnR;
@FXML private TableColumn<Reservation, LocalDateTime> dateReservationColumnR;
@FXML private TextField searchReservationField;

    // Data Access Objects
    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
    private final SalleDAO salleDAO = new SalleDAO();
    private final TerrainDAO terrainDAO = new TerrainDAO();
    private final EvenmentsDAO evenementDAO = new EvenmentsDAO();
    private final ReservationDAO reservationDAO = new ReservationDAO();
    
    // Observable Lists for Tables
    private final ObservableList<User> userList = FXCollections.observableArrayList();
    private final ObservableList<Salle> roomList = FXCollections.observableArrayList();
    private final ObservableList<Terrain> terrainList = FXCollections.observableArrayList();
    private final ObservableList<Evenement> eventList = FXCollections.observableArrayList();
    private final ObservableList<Reservation> reservationList = FXCollections.observableArrayList();

    
    // Filtered Lists for Search
    private FilteredList<User> filteredUsers;
    private FilteredList<Salle> filteredRooms;
    private FilteredList<Terrain> filteredTerrains;
    private FilteredList<Evenement> filteredEvents;
    private FilteredList<Reservation> filteredReservations;

    @FXML
    public void initialize() {
        initializeUserTab();
        initializeRoomTab();
        initializeTerrainTab();
        initializeEventTab();
        initializeReservationTab();
        setupSearchFilters();
        loadAllData();
    }
    
    // région Initialization Methods
    private void initializeReservationTab() {
        reservationIdColumnR.setCellValueFactory(new PropertyValueFactory<>("id_reservation"));
        userIdColumnR.setCellValueFactory(new PropertyValueFactory<>("id_user"));
        eventIdColumnR.setCellValueFactory(new PropertyValueFactory<>("id_event"));
        roomIdColumnR.setCellValueFactory(new PropertyValueFactory<>("id_salle"));
        terrainIdColumnR.setCellValueFactory(new PropertyValueFactory<>("id_terrain"));
        dateReservationColumnR.setCellValueFactory(new PropertyValueFactory<>("date_reservation"));
        
        filteredReservations = new FilteredList<>(reservationList, p -> true);
        reservationTable.setItems(filteredReservations);
    }

    private void initializeEventTab() {
        eventIdColumn.setCellValueFactory(new PropertyValueFactory<>("idEvent"));
        eventNameColumn.setCellValueFactory(new PropertyValueFactory<>("nomEvent"));
        eventDateColumn.setCellValueFactory(new PropertyValueFactory<>("dateEvent"));
        eventDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        eventUserIdColumn.setCellValueFactory(new PropertyValueFactory<>("idUser"));
        
        filteredEvents = new FilteredList<>(eventList, p -> true);
        eventTable.setItems(filteredEvents);
    }

    private void initializeUserTab() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("idUser"));
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prenomColumn.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        
        filteredUsers = new FilteredList<>(userList, p -> true);
        userTable.setItems(filteredUsers);
    }
    
    private void initializeRoomTab() {
        roomIdColumn.setCellValueFactory(new PropertyValueFactory<>("id_salle"));
        roomNameColumn.setCellValueFactory(new PropertyValueFactory<>("nom_salle"));
        roomCapacityColumn.setCellValueFactory(new PropertyValueFactory<>("capacite"));
        
        filteredRooms = new FilteredList<>(roomList, p -> true);
        roomTable.setItems(filteredRooms);
    }
    
    private void initializeTerrainTab() {
        terrainIdColumn.setCellValueFactory(new PropertyValueFactory<>("id_terrain"));
        terrainNameColumn.setCellValueFactory(new PropertyValueFactory<>("nom_terrain"));
        terrainTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        
        filteredTerrains = new FilteredList<>(terrainList, p -> true);
        terrainTable.setItems(filteredTerrains);
    }
    
    private void setupSearchFilters() {
        // Users Search
        searchUserField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredUsers.setPredicate(user -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lowerCaseFilter = newValue.toLowerCase();
                return user.getNom().toLowerCase().contains(lowerCaseFilter) ||
                       user.getPrenom().toLowerCase().contains(lowerCaseFilter) ||
                       user.getEmail().toLowerCase().contains(lowerCaseFilter) ||
                       user.getType().toLowerCase().contains(lowerCaseFilter);
            });
        });
        // Event Search
        searchEventField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredEvents.setPredicate(event -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lowerCaseFilter = newValue.toLowerCase();
                return event.getNomEvent().toLowerCase().contains(lowerCaseFilter) ||
                       event.getDescription().toLowerCase().contains(lowerCaseFilter);
            });
        });
        // Rooms Search
        searchRoomField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredRooms.setPredicate(room -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lowerCaseFilter = newValue.toLowerCase();
                return room.getNom_salle().toLowerCase().contains(lowerCaseFilter);
            });
        });
        
        // Terrains Search
        searchTerrainField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredTerrains.setPredicate(terrain -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lowerCaseFilter = newValue.toLowerCase();
                return terrain.getNom_terrain().toLowerCase().contains(lowerCaseFilter) ||
                       terrain.getType().toLowerCase().contains(lowerCaseFilter);
            });
        });

        // reservation Search
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
    
    private void loadAllData() {
        loadUsers();
        loadRooms();
        loadTerrains();
        loadEvents();
        loadReservations();
    }
    
    // CRUD Operations for Reservations
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
    // région CRUD Operations - Users
    @FXML
    private void addUser() {
        Dialog<User> dialog = createUserDialog("Ajouter un utilisateur", null);
        dialog.showAndWait().ifPresent(user -> {
            try {
                utilisateurDAO.add(user);
                loadUsers();
                showInfo("Succès", "Utilisateur ajouté avec succès");
            } catch (Exception e) {
                showError("Erreur d'ajout", "Impossible d'ajouter l'utilisateur : " + e.getMessage());
            }
        });
    }
    
    @FXML
    private void editUser() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Sélection requise", "Veuillez sélectionner un utilisateur à modifier");
            return;
        }
        
        Dialog<User> dialog = createUserDialog("Modifier l'utilisateur", selected);
        dialog.showAndWait().ifPresent(user -> {
            try {
                utilisateurDAO.update(user);
                loadUsers();
                showInfo("Succès", "Utilisateur modifié avec succès");
            } catch (Exception e) {
                showError("Erreur de modification", "Impossible de modifier l'utilisateur : " + e.getMessage());
            }
        });
    }
    
    @FXML
    private void deleteUser() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Sélection requise", "Veuillez sélectionner un utilisateur à supprimer");
            return;
        }
        
        if (showConfirmation("Confirmation", "Voulez-vous vraiment supprimer cet utilisateur ?")) {
            try {
                utilisateurDAO.delete(selected.getIdUser());
                loadUsers();
                showInfo("Succès", "Utilisateur supprimé avec succès");
            } catch (Exception e) {
                showError("Erreur de suppression", "Impossible de supprimer l'utilisateur : " + e.getMessage());
            }
        }
    }
    
    // région CRUD Operations - Rooms
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
     // Crud for Event

     // CRUD Operations for Events
@FXML
private void addEvent() {
    Dialog<Evenement> dialog = createEventDialog("Ajouter un événement", null);
    dialog.showAndWait().ifPresent(event -> {
        try {
            evenementDAO.add(event);
            loadEvents();
            showInfo("Succès", "Événement ajouté avec succès");
        } catch (Exception e) {
            showError("Erreur d'ajout", "Impossible d'ajouter l'événement : " + e.getMessage());
        }
    });
}

@FXML
private void editEvent() {
    Evenement selected = eventTable.getSelectionModel().getSelectedItem();
    if (selected == null) {
        showWarning("Sélection requise", "Veuillez sélectionner un événement à modifier");
        return;
    }
    
    Dialog<Evenement> dialog = createEventDialog("Modifier l'événement", selected);
    dialog.showAndWait().ifPresent(event -> {
        try {
            evenementDAO.update(event);
            loadEvents();
            showInfo("Succès", "Événement modifié avec succès");
        } catch (Exception e) {
            showError("Erreur de modification", "Impossible de modifier l'événement : " + e.getMessage());
        }
    });
}

@FXML
private void deleteEvent() {
    Evenement selected = eventTable.getSelectionModel().getSelectedItem();
    if (selected == null) {
        showWarning("Sélection requise", "Veuillez sélectionner un événement à supprimer");
        return;
    }
    
    if (showConfirmation("Confirmation", "Voulez-vous vraiment supprimer cet événement ?")) {
        try {
            evenementDAO.delete(selected.getIdEvent());
            loadEvents();
            showInfo("Succès", "Événement supprimé avec succès");
        } catch (Exception e) {
            showError("Erreur de suppression", "Impossible de supprimer l'événement : " + e.getMessage());
        }
    }
}
    
    // région Data Loading Methods

    private void loadReservations() {
        try {
            reservationList.setAll(reservationDAO.getAll());
        } catch (Exception e) {
            showError("Erreur de chargement", "Impossible de charger les réservations : " + e.getMessage());
        }
    }

    private void loadEvents() {
        try {
            eventList.setAll(evenementDAO.getAll());
        } catch (Exception e) {
            showError("Erreur de chargement", "Impossible de charger les événements : " + e.getMessage());
        }
    }

    private void loadUsers() {
        try {
            userList.setAll(utilisateurDAO.getAll());
        } catch (Exception e) {
            showError("Erreur de chargement", "Impossible de charger les utilisateurs : " + e.getMessage());
        }
    }
    
    private void loadRooms() {
        try {
            roomList.setAll(salleDAO.getAll());
        } catch (Exception e) {
            showError("Erreur de chargement", "Impossible de charger les salles : " + e.getMessage());
        }
    }
    
    private void loadTerrains() {
        try {
            terrainList.setAll(terrainDAO.getAll());
        } catch (Exception e) {
            showError("Erreur de chargement", "Impossible de charger les terrains : " + e.getMessage());
        }
    }
    
    // région Dialog Creation Methods

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

    private Dialog<Evenement> createEventDialog(String title, Evenement event) {
        Dialog<Evenement> dialog = new Dialog<>();
        dialog.setTitle(title);
        
        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        
        TextField nomField = new TextField();
        DatePicker datePicker = new DatePicker();
        TextField timeField = new TextField();
        TextArea descriptionArea = new TextArea();
        TextField userIdField = new TextField();
        
        if (event != null) {
            nomField.setText(event.getNomEvent());
            datePicker.setValue(event.getDateEvent().toLocalDate());
            timeField.setText(event.getDateEvent().toLocalTime().toString());
            descriptionArea.setText(event.getDescription());
            userIdField.setText(String.valueOf(event.getIdUser()));
        }
        
        grid.add(new Label("Nom:"), 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(new Label("Date:"), 0, 1);
        grid.add(datePicker, 1, 1);
        grid.add(new Label("Heure:"), 0, 2);
        grid.add(timeField, 1, 2);
        grid.add(new Label("Description:"), 0, 3);
        grid.add(descriptionArea, 1, 3);
        grid.add(new Label("ID Utilisateur:"), 0, 4);
        grid.add(userIdField, 1, 4);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    LocalDateTime dateTime = LocalDateTime.of(
                        datePicker.getValue(),
                        LocalTime.parse(timeField.getText())
                    );
                    
                    if (event == null) {
                        return new Evenement(
                            nomField.getText(),
                            dateTime,
                            descriptionArea.getText(),
                            Integer.parseInt(userIdField.getText())
                        );
                    } else {
                        event.setNomEvent(nomField.getText());
                        event.setDateEvent(dateTime);
                        event.setDescription(descriptionArea.getText());
                        event.setIdUser(Integer.parseInt(userIdField.getText()));
                        return event;
                    }
                } catch (NumberFormatException e) {
                    showError("Erreur de saisie", "L'ID utilisateur doit être un nombre entier");
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
    


    private Dialog<User> createUserDialog(String title, User user) {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle(title);
        
        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        
        TextField nomField = new TextField();
        TextField prenomField = new TextField();
        TextField emailField = new TextField();
        TextField roleField = new TextField();
        
        if (user != null) {
            nomField.setText(user.getNom());
            prenomField.setText(user.getPrenom());
            emailField.setText(user.getEmail());
            roleField.setText(user.getType());
        }
        
        grid.add(new Label("Nom:"), 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(new Label("Prénom:"), 0, 1);
        grid.add(prenomField, 1, 1);
        grid.add(new Label("Email:"), 0, 2);
        grid.add(emailField, 1, 2);
        grid.add(new Label("Rôle:"), 0, 3);
        grid.add(roleField, 1, 3);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                if (user == null) {
                    return new User(nomField.getText(), prenomField.getText(), 
                                  emailField.getText(), roleField.getText());
                } else {
                    user.setNom(nomField.getText());
                    user.setPrenom(prenomField.getText());
                    user.setEmail(emailField.getText());
                    user.setType(roleField.getText());
                    return user;
                }
            }
            return null;
        });
        
        return dialog;
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

    // région CRUD Operations - Terrains
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

    // région Utility Methods - Dialogs
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


    @FXML
public void showEventsView(ActionEvent event) {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/events.fxml"));
        Parent eventsView = loader.load();

        Tab eventsTab = new Tab("Événements");
        eventsTab.setContent(eventsView);
        
        mainTabPane.getTabs().clear();
        mainTabPane.getTabs().add(eventsTab);
    } catch (IOException e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setContentText("Impossible de charger la vue des événements: " + e.getMessage());
        alert.showAndWait();
    }
}

    @FXML
public void showRoomsView(ActionEvent event) {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/rooms.fxml"));
        Parent roomsView = loader.load();

        // Get the Tab for rooms view
        Tab roomsTab = new Tab("Salles");
        roomsTab.setContent(roomsView);
        
        // Add or update the tab in TabPane
        mainTabPane.getTabs().clear();  // Clear existing tabs
        mainTabPane.getTabs().add(roomsTab);  // Add new room tab
    } catch (IOException e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setContentText("Impossible de charger la vue des salles: " + e.getMessage());
        alert.showAndWait();
    }
}

@FXML
public void showUsersView(ActionEvent event) {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/users.fxml"));
        Parent usersView = loader.load();

        // Get the Tab for users view
        Tab usersTab = new Tab("Utilisateurs");
        usersTab.setContent(usersView);
        
        // Add or update the tab in TabPane
        mainTabPane.getTabs().clear();  // Clear existing tabs
        mainTabPane.getTabs().add(usersTab);  // Add new users tab
    } catch (IOException e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setContentText("Impossible de charger la vue des utilisateurs: " + e.getMessage());
        alert.showAndWait();
    }
}

@FXML
public void showTerrainView(ActionEvent event) {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/terrains.fxml"));
        Parent terrainsView = loader.load();

        // Créer un onglet pour la vue des terrains
        Tab terrainsTab = new Tab("Terrains");
        terrainsTab.setContent(terrainsView);
        
        // Ajouter ou mettre à jour l'onglet dans le TabPane
        mainTabPane.getTabs().clear();  // Supprimer les onglets existants
        mainTabPane.getTabs().add(terrainsTab);  // Ajouter l'onglet des terrains
    } catch (IOException e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setContentText("Impossible de charger la vue des terrains: " + e.getMessage());
        alert.showAndWait();
    }
}

@FXML
public void showReservationsView(ActionEvent event) {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/reservations.fxml"));
        Parent reservationsView = loader.load();

        Tab reservationsTab = new Tab("Réservations");
        reservationsTab.setContent(reservationsView);
        
        mainTabPane.getTabs().clear();
        mainTabPane.getTabs().add(reservationsTab);
    } catch (IOException e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setContentText("Impossible de charger la vue des réservations: " + e.getMessage());
        alert.showAndWait();
    }
}


}