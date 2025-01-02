package com.example.controllers;

import com.example.model.Salle;
import com.example.model.User;
import com.example.dd.SalleDAO;
import com.example.dd.UtilisateurDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.util.List;

public class MainController {
    
    @FXML
    private TableView<User> userTable;
    @FXML
    private TableColumn<User, Integer> idColumn;
    @FXML
    private TableColumn<User, String> nomColumn;
    @FXML
    private TableColumn<User, String> prenomColumn;
    @FXML
    private TableColumn<User, String> emailColumn;
    @FXML
    private TableColumn<User, String> roleColumn;
    @FXML
    private TextField searchUserField;
    
    private UtilisateurDAO utilisateurDAO;
    private ObservableList<User> userList;
    
    @FXML
    public void initialize() {
        utilisateurDAO = new UtilisateurDAO();
        userList = FXCollections.observableArrayList();
        
        // Configuration des colonnes
        idColumn.setCellValueFactory(new PropertyValueFactory<>("idUser"));
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prenomColumn.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        // Chargement des données
        loadUsers();
        
        // Configuration de la recherche
        searchUserField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterUsers(newValue);
        });
    }
    
    private void loadUsers() {
        try {
            List<User> users = utilisateurDAO.getAll();
            userList.clear();
            userList.addAll(users);
            userTable.setItems(userList);
        } catch (Exception e) {
            showError("Erreur de chargement", "Impossible de charger les utilisateurs: " + e.getMessage());
        }
    }
    
    @FXML
private void addUser() {
    // Créez une boîte de dialogue pour saisir les informations de l'utilisateur
    Dialog<User> dialog = new Dialog<>();
    dialog.setTitle("Ajouter un utilisateur");
    dialog.setHeaderText("Entrez les informations de l'utilisateur");

    // Configurez les boutons OK et Annuler
    ButtonType okButtonType = new ButtonType("Ajouter", ButtonBar.ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

    // Créez les champs de formulaire
    TextField nomField = new TextField();
    nomField.setPromptText("Nom");
    TextField prenomField = new TextField();
    prenomField.setPromptText("Prénom");
    TextField emailField = new TextField();
    emailField.setPromptText("Email");
    TextField roleField = new TextField();
    roleField.setPromptText("Rôle");

    // Ajoutez les champs au formulaire
    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.add(new Label("Nom:"), 0, 0);
    grid.add(nomField, 1, 0);
    grid.add(new Label("Prénom:"), 0, 1);
    grid.add(prenomField, 1, 1);
    grid.add(new Label("Email:"), 0, 2);
    grid.add(emailField, 1, 2);
    grid.add(new Label("Rôle:"), 0, 3);
    grid.add(roleField, 1, 3);

    dialog.getDialogPane().setContent(grid);

    // Convertissez les résultats de la boîte de dialogue en objet User
    dialog.setResultConverter(dialogButton -> {
        if (dialogButton == okButtonType) {
            return new User( nomField.getText(), prenomField.getText(), emailField.getText(), roleField.getText());
        }
        return null;
    });

    // Affichez la boîte de dialogue et récupérez les résultats
    dialog.showAndWait().ifPresent(user -> {
        try {
            utilisateurDAO.add(user); // Méthode pour insérer dans la base de données
            loadUsers(); // Rechargez les utilisateurs après ajout
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

    Dialog<User> dialog = new Dialog<>();
    dialog.setTitle("Modifier l'utilisateur");
    dialog.setHeaderText("Modifiez les informations de l'utilisateur");

    ButtonType okButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

    TextField nomField = new TextField(selected.getNom());
    TextField prenomField = new TextField(selected.getPrenom());
    TextField emailField = new TextField(selected.getEmail());
    TextField roleField = new TextField(selected.getType());

    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
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
        if (dialogButton == okButtonType) {
            selected.setNom(nomField.getText());
            selected.setPrenom(prenomField.getText());
            selected.setEmail(emailField.getText());
            selected.setType(roleField.getText());
            return selected;
        }
        return null;
    });

    dialog.showAndWait().ifPresent(user -> {
        try {
            utilisateurDAO.update(user); // Méthode pour mettre à jour dans la base de données
            loadUsers();
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
            } catch (Exception e) {
                showError("Erreur de suppression", "Impossible de supprimer l'utilisateur: " + e.getMessage());
            }
        }
    }
    
    private void filterUsers(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            userTable.setItems(userList);
        } else {
            ObservableList<User> filteredList = FXCollections.observableArrayList();
            for (User user : userList) {
                if (matchesSearch(user, searchText.toLowerCase())) {
                    filteredList.add(user);
                }
            }
            userTable.setItems(filteredList);
        }
    }
    
    private boolean matchesSearch(User user, String searchText) {
        return user.getNom().toLowerCase().contains(searchText) ||
               user.getPrenom().toLowerCase().contains(searchText) ||
               user.getEmail().toLowerCase().contains(searchText) ||
               user.getType().toLowerCase().contains(searchText);
    }
    
    // Méthodes utilitaires pour les dialogues
    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
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
    
    private void showInfo(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
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
    @FXML
    public void showEventsView(ActionEvent event) {
    // Handle the event
}
@FXML
public void showReservationsView() {
    // Code pour afficher la vue des réservations
}


@FXML
private TabPane mainTabPane;

@FXML
private TableView<Salle> roomTable;
@FXML
private TableColumn<Salle, Integer> roomIdColumn;
@FXML
private TableColumn<Salle, String> roomNameColumn;
@FXML
private TableColumn<Salle, Integer> roomCapacityColumn;

private SalleDAO roomDAO;
private ObservableList<Salle> roomList;
@FXML
public void initializes() {
    roomDAO = new SalleDAO();
    roomList = FXCollections.observableArrayList();

    roomIdColumn.setCellValueFactory(new PropertyValueFactory<>("id_salle"));
    roomNameColumn.setCellValueFactory(new PropertyValueFactory<>("nom_salle"));
    roomCapacityColumn.setCellValueFactory(new PropertyValueFactory<>("capacite"));

    loadRooms();
}

private void loadRooms() {
    try {
        roomList.setAll(roomDAO.getAll());
        roomTable.setItems(roomList);
    } catch (Exception e) {
        e.printStackTrace();
    }
}
@FXML
    private void addRoom() {
        Dialog<Salle> dialog = new Dialog<>();
        dialog.setTitle("Ajouter une salle");
        dialog.setHeaderText("Entrez les informations de la salle");

        ButtonType okButtonType = new ButtonType("Ajouter", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        TextField nomSalleField = new TextField();
        nomSalleField.setPromptText("Nom de la salle");
        TextField capaciteField = new TextField();
        capaciteField.setPromptText("Capacité");

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
                return new Salle(nomSalleField.getText(), Integer.parseInt(capaciteField.getText()));
            }
            return null;
        });

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
        Salle selected = roomTable.getSelectionModel().getSelectedItem();
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
        Salle selected = roomTable.getSelectionModel().getSelectedItem();
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

// @FXML
//     private void addRoom() {
//         // Implémentez ici la logique pour ajouter une chambre
//         System.out.println("Chambre ajoutée !");
//     }
//     @FXML
//     public void editRoom(ActionEvent event) {
//         // your code here
//     }
    //  @FXML
    // public void deleteRoom(ActionEvent event) {
    //     // Logic for deleting the room
    // }











// @FXML
// public void showRoomsView(ActionEvent event) {
//     // Implement the logic here
// }


//     @FXML
// public void showUsersView() {
//     try {
//         FXMLLoader loader = new FXMLLoader(getClass().getResource("/users.fxml"));
//         Parent usersView = loader.load();
        
//         // Get the current stage
//         Stage stage = (Stage) userTable.getScene().getWindow();
        
//         // Create new scene with users view
//         Scene scene = new Scene(usersView);
        
//         // Set the new scene
//         stage.setScene(scene);
//         stage.setTitle("Gestion des Utilisateurs");
//         stage.show();
        
//     } catch (IOException e) {
//         Alert alert = new Alert(Alert.AlertType.ERROR);
//         alert.setTitle("Erreur");
//         alert.setContentText("Impossible de charger la vue des utilisateurs: " + e.getMessage());
//         alert.showAndWait();
//     }
// }
}