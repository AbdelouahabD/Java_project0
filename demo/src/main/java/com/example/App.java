package com.example;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;  // Ajout de cet import
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Account.fxml"));
        Scene scene = new Scene(loader.load(), 800, 600);

        primaryStage.setTitle("Gestion de Reservation de Est Essaouira");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}