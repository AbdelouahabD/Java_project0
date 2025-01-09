package com.example.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import com.example.utils.PasswordEncryptionUtil;

import java.io.IOException;

import com.example.dd.UtilisateurDAO;
import com.example.model.User;

public class AccountController {

    private static final String ENCRYPTION_KEY = "oiurhsdjkhskhffsklhfsikhhtisufgrifiuytiowfr";
private final UtilisateurDAO userda0=new UtilisateurDAO();
    @FXML
    private TextField nom;

    @FXML
    private TextField prenom;

    @FXML
    private TextField email;

    @FXML
    private ComboBox<String> roleComboBox;

    @FXML
    private PasswordField motDePass;

    @FXML
    private PasswordField confirmMotDePasse;

    @FXML
    private Button createAccountButton;

    @FXML
    private Hyperlink loginLink;

    @FXML
    private void initialize() {
        // Initialize the ComboBox with values
        roleComboBox.setItems(FXCollections.observableArrayList("ETUDIANT", "PROFESSEUR"));
    }

    @FXML
    private void handleCreateAccountButtonAction() throws Exception {
        // Handle "Create Account" button action
        String nomValue = nom.getText();
        String prenomValue = prenom.getText();
        String emailValue = email.getText();
        String password = motDePass.getText();
        String confirmPassword = confirmMotDePasse.getText();
        String role = roleComboBox.getValue();

        
        if (validateFields(nomValue, prenomValue, emailValue, password, confirmPassword, role)) {
            System.out.println("Account Created Successfully");
            String encryptedPassword = PasswordEncryptionUtil.encrypt(password, ENCRYPTION_KEY);
            System.out.println(encryptedPassword);
            User user=new User(nomValue,prenomValue,emailValue,role,encryptedPassword);
            userda0.add(user);
            showSuccessDialog("success","your account has been added succesfuly");
            Stage currentStage = (Stage) email.getScene().getWindow(); // emailField is your TextField's fx:id
        
        try {
            // Load the new FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Parent root = loader.load();
            
            // Create new scene
            Scene scene = new Scene(root);
            
            // Set the scene on current stage
            currentStage.setScene(scene);
            currentStage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
            showWarningDialog("Error", "Could not load the next page");
        }
        }
    }

    @FXML
    private void handleLoginLinkAction() {
        // Handle "Log in" hyperlink action
        System.out.println("Redirecting to login...");
        Stage currentStage = (Stage) loginLink.getScene().getWindow();
        try {
            // Load the new FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Parent root = loader.load();
            
            // Create new scene
            Scene scene = new Scene(root);
            
            // Set the scene on current stage
            currentStage.setScene(scene);
            currentStage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
            showWarningDialog("Error", "Could not load the next page");
        }
        // Add logic for navigation to the login view
    }

    private boolean validateFields(String nom, String prenom, String email, String password, String confirmPassword, String role) {
        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || role == null) {
            showWarningDialog("Incomplete Form", "Please fill out all fields before submitting.");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            System.out.println("Passwords do not match!");
            return false;
        }

        // Add further validations (e.g., email format, password strength) if needed
        return true;
    }
    private void showWarningDialog(String title, String content) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null); // No header text
        alert.setContentText(content);
        alert.showAndWait();
    }
    private void showSuccessDialog(String title, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null); // No header text
        alert.setContentText(content);
        alert.showAndWait();
    }
}
