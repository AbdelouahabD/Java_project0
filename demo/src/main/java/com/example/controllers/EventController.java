package com.example.controllers;

import com.example.model.Evenement;
import com.example.dd.EvenmentsDAO;
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

public class EventController {
    @FXML private TableView<Evenement> eventTable;
    @FXML private TableColumn<Evenement, Integer> eventIdColumn;
    @FXML private TableColumn<Evenement, String> eventNameColumn;
    @FXML private TableColumn<Evenement, LocalDateTime> eventDateColumn;
    @FXML private TableColumn<Evenement, String> eventDescriptionColumn;
    @FXML private TableColumn<Evenement, Integer> eventUserIdColumn;
    @FXML private TextField searchEventField;
    
    private final EvenmentsDAO evenementDAO = new EvenmentsDAO();
    private final ObservableList<Evenement> eventList = FXCollections.observableArrayList();
    private FilteredList<Evenement> filteredEvents;
    
    @FXML
    public void initialize() {
        initializeTable();
        setupSearch();
        loadEvents();
    }
    
    private void initializeTable() {
        eventIdColumn.setCellValueFactory(new PropertyValueFactory<>("idEvent"));
        eventNameColumn.setCellValueFactory(new PropertyValueFactory<>("nomEvent"));
        eventDateColumn.setCellValueFactory(new PropertyValueFactory<>("dateEvent"));
        eventDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        eventUserIdColumn.setCellValueFactory(new PropertyValueFactory<>("idUser"));
        
        filteredEvents = new FilteredList<>(eventList, p -> true);
        eventTable.setItems(filteredEvents);
    }
    
    private void setupSearch() {
        searchEventField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredEvents.setPredicate(event -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lowerCaseFilter = newValue.toLowerCase();
                return event.getNomEvent().toLowerCase().contains(lowerCaseFilter) ||
                       event.getDescription().toLowerCase().contains(lowerCaseFilter);
            });
        });
    }
    
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
    
    private void loadEvents() {
        try {
            eventList.setAll(evenementDAO.getAll());
        } catch (Exception e) {
            showError("Erreur de chargement", "Impossible de charger les événements : " + e.getMessage());
        }
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