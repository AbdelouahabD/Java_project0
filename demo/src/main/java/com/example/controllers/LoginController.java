package com.example.controllers;

import com.example.utils.PasswordEncryptionUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.PasswordField;

import java.io.IOException;

import com.example.dd.UtilisateurDAO;
public class LoginController {
    private UtilisateurDAO userdd=new UtilisateurDAO();
    private static final String ENCRYPTION_KEY = "oiurhsdjkhskhffsklhfsikhhtisufgrifiuytiowfr";
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
     @FXML
    private void initialize() {
       
    }
    public void handleloginButtonAction()throws Exception{
        
String email=emailField.getText();
String password=passwordField.getText();
if(validateFields(email,password )){
authenticate(email, password);
}
    }
    public void authenticate(String email,String password) throws Exception{
String encryptedPassword = PasswordEncryptionUtil.encrypt(password, ENCRYPTION_KEY);
if(userdd.authenticate(email, encryptedPassword)){
    String role=userdd.role(email, encryptedPassword);
showSuccessDialog("success", "you are logged in");
Stage currentStage = (Stage) emailField.getScene().getWindow();
int userId=userdd.getID(email, encryptedPassword);
GlobalState.getInstance().setUserId(userId);
if(role.equals("admin")){
 // emailField is your TextField's fx:id
        
        try {
            // Load the new FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main.fxml"));
            Parent root = loader.load();
            
            // Create new scene
            Scene scene = new Scene(root);
            
            // Set the scene on current stage
            currentStage.setScene(scene);
            currentStage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
            showWarningDialog("Error", "Could not load the next page");
        }}else{
            try {
                // Load the new FXML
                
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/usermain.fxml"));
                Parent root = loader.load();
                
                // Create new scene
                Scene scene = new Scene(root);
                // Set the user ID
                currentStage.setScene(scene);
                currentStage.show();
                
                
            } catch (IOException e) {
                e.printStackTrace();
                showWarningDialog("Error", "Could not load the next page");
            }
        }
}else{
    showWarningDialog("faillure","you made a mistake");
}

    }
    public boolean validateFields(String email,String password){
        if(email.isEmpty() || password.isEmpty()){
            showWarningDialog("error","All fields are needed");
            return false;
        }
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
