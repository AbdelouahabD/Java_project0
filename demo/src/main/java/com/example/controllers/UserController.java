package com.example.controllers;

import com.example.model.User;
import com.example.dd.UtilisateurDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

public class UserController {
    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, Integer> idColumn;
    @FXML private TableColumn<User, String> nomColumn;
    @FXML private TableColumn<User, String> prenomColumn;
    @FXML private TableColumn<User, String> emailColumn;
    @FXML private TableColumn<User, String> roleColumn;
    @FXML private TextField searchUserField;
    
    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
    private final ObservableList<User> userList = FXCollections.observableArrayList();
    private FilteredList<User> filteredUsers;
    
    @FXML
    public void initialize() {
        initializeTable();
        setupSearch();
        loadUsers();
    }
    
    private void initializeTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("idUser"));
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prenomColumn.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        
        filteredUsers = new FilteredList<>(userList, p -> true);
        userTable.setItems(filteredUsers);
    }
    
    private void setupSearch() {
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
    }
    
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
    
    private void loadUsers() {
        try {
            userList.setAll(utilisateurDAO.getAll());
        } catch (Exception e) {
            showError("Erreur de chargement", "Impossible de charger les utilisateurs : " + e.getMessage());
        }
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