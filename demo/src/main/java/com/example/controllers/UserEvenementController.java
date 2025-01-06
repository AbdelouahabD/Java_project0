package com.example.controllers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

import com.example.model.Evenement;
import com.example.model.Reservation;
import com.example.dd.EvenmentsDAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

public class UserEvenementController {
    private EvenmentsDAO evenementDAO = new EvenmentsDAO();
    private int userId;

    @FXML
    private TextField searchReservationField;

    @FXML
    private ListView<Evenement> eventListView;

    private ObservableList<Evenement> events = FXCollections.observableArrayList();

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @FXML
    public void initialize() {
        System.out.println("Initializing UserEvenementController");
        setUserId(GlobalState.getInstance().getUserId());
        System.out.println("User ID set to: " + userId);

        eventListView.setItems(events);
        loadUserEvents();

        searchReservationField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterEvents(newValue);
        });

        eventListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Evenement event, boolean empty) {
                super.updateItem(event, empty);
                if (empty || event == null) {
                    setText(null);
                } else {
                    setText(event.getNomEvent() + " - " + event.getDateEvent());
                }
            }
        });
    }

    private void loadUserEvents() {
        try {
            List<Evenement> userEvents = evenementDAO.getUserEvents(userId);
            events.setAll(userEvents);
        } catch (Exception e) {
            showError("Error Loading Events", "Could not load events: " + e.getMessage());
        }
    }

    private void filterEvents(String filter) {
        if (filter == null || filter.isEmpty()) {
            eventListView.setItems(events);
        } else {
            String lowerCaseFilter = filter.toLowerCase();
            ObservableList<Evenement> filteredEvents = events.filtered(event ->
                    event.getNomEvent().toLowerCase().contains(lowerCaseFilter) ||
                    event.getDescription().toLowerCase().contains(lowerCaseFilter)
            );
            eventListView.setItems(filteredEvents);
        }
    }

    @FXML
    private void addEvent() {
        Dialog<Evenement> dialog = createEventDialog("Add Event", null);
        dialog.showAndWait().ifPresent(event -> {
            try {
                evenementDAO.add(event);
                loadUserEvents();
                showInfo("Success", "Event added successfully.");
            } catch (Exception e) {
                showError("Add Event Error", "Could not add event: " + e.getMessage());
            }
        });
    }

    @FXML
    private void deleteEvent() {
        Evenement selectedEvent = eventListView.getSelectionModel().getSelectedItem();
        if (selectedEvent == null) {
            showWarning("No Selection", "Please select an event to delete.");
            return;
        }

        boolean confirmation = showConfirmation("Delete Event", "Are you sure you want to delete this event?");
        if (confirmation) {
            try {
                evenementDAO.delete(selectedEvent.getIdEvent());
                loadUserEvents();
                showInfo("Success", "Event deleted successfully.");
            } catch (Exception e) {
                showError("Delete Event Error", "Could not delete event: " + e.getMessage());
            }
        }
    }
    @FXML void editEvent(){
        Evenement selectedEvenement = eventListView.getSelectionModel().getSelectedItem();
        if (selectedEvenement != null) {
            Dialog<Evenement> dialog = createEventDialog("Modifier la réservation", selectedEvenement);
        dialog.showAndWait().ifPresent(event -> {
            try {
                evenementDAO.update(event);
                loadUserEvents();
                showInfo("Succès", "Réservation modifiée avec succès");
            } catch (Exception e) {
                showError("Erreur de modification", "Impossible de modifier la réservation : " + e.getMessage());
            }
        });
        } else {
            showAlert("No Selection", "Please select a reservation to edit.");
        }
    }

    private Dialog<Evenement> createEventDialog(String title, Evenement event) {
        Dialog<Evenement> dialog = new Dialog<>();
        dialog.setTitle(title);

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField nomField = new TextField();
        DatePicker datePicker = new DatePicker();
        ComboBox<String> timeComboBox = new ComboBox<>();
        TextArea descriptionArea = new TextArea();

        timeComboBox.getItems().addAll(
                "08:30", "09:00", "09:30", "10:00", "10:30", "11:00", "11:30", "12:00",
                "13:00", "13:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30"
        );

        if (event != null) {
            nomField.setText(event.getNomEvent());
            datePicker.setValue(event.getDateEvent().toLocalDate());
            timeComboBox.setValue(event.getDateEvent().toLocalTime().toString());
            descriptionArea.setText(event.getDescription());
        }

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(new Label("Date:"), 0, 1);
        grid.add(datePicker, 1, 1);
        grid.add(new Label("Time:"), 0, 2);
        grid.add(timeComboBox, 1, 2);
        grid.add(new Label("Description:"), 0, 3);
        grid.add(descriptionArea, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    LocalDateTime dateTime = LocalDateTime.of(
                            datePicker.getValue(),
                            LocalTime.parse(timeComboBox.getSelectionModel().getSelectedItem())
                    );
                    if (event == null) {
                        return new Evenement(nomField.getText(), dateTime, descriptionArea.getText(), userId);
                    } else {
                        event.setNomEvent(nomField.getText());
                        event.setDateEvent(dateTime);
                        event.setDescription(descriptionArea.getText());
                        return event;
                    }
                } catch (DateTimeParseException e) {
                    showError("Input Error", "Invalid date or time format.");
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
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showInfo(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showWarning(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private boolean showConfirmation(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
