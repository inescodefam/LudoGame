package com.projectkamberinesludogame.ludogame;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import java.io.IOException;

public class MenuController {
    @FXML
    private Button hostButton;

    @FXML
    private Button joinButton;

    @FXML
    private TextField portField;

    @FXML
    private void initialize() {
        portField.setText("5000");
    }

    @FXML
    private void onHostGame() {
        int port = getPort();
        if (port == -1) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("game-view.fxml"));
            Scene scene = new Scene(loader.load(), 800, 900);

            GameController controller = loader.getController();
            controller.startAsHost(port);

            Stage stage = (Stage) hostButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Ludo Game - Host (Port: " + port + ")");
        } catch (IOException e) {
            showError("Failed to start game: " + e.getMessage());
        }
    }

    @FXML
    private void onJoinGame() {
        int port = getPort();
        if (port == -1) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("game-view.fxml"));
            Scene scene = new Scene(loader.load(), 800, 900);

            GameController controller = loader.getController();
            controller.startAsClient(port);

            Stage stage = (Stage) joinButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Ludo Game - Client (Port: " + port + ")");
        } catch (IOException e) {
            showError("Failed to join game: " + e.getMessage());
        }
    }

    private int getPort() {
        try {
            int port = Integer.parseInt(portField.getText());
            if (port < 1024 || port > 65535) {
                showError("Port must be between 1024 and 65535");
                return -1;
            }
            return port;
        } catch (NumberFormatException e) {
            showError("Invalid port number");
            return -1;
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
