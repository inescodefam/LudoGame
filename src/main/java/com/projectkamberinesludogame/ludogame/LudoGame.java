package com.projectkamberinesludogame.ludogame;

import com.projectkamberinesludogame.ludogame.jndi.ConfigurationKey;
import com.projectkamberinesludogame.ludogame.jndi.ConfigurationReader;
import com.projectkamberinesludogame.ludogame.model.PlayerType;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.IOException;

public class LudoGame extends Application {
    public static PlayerType playerType;
    public static String HOSTNAME;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(LudoGame.class.getResource("game-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1190, 914);
        stage.setTitle("Ludo Game - " + playerType.toString());
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {

        try {
            HOSTNAME = ConfigurationReader.getStringForKey(ConfigurationKey.HOSTNAME);
        } catch (Exception e) {
            System.out.println("Could not read hostname from config, using localhost");
            HOSTNAME = "localhost";
        }

        if (args.length == 0) {
            System.out.println("Usage: java LudoGame <SINGLE_PLAYER|PLAYER_RED|PLAYER_BLUE>");
            System.out.println("\nExamples:");
            System.out.println("  Single player: java LudoGame SINGLE_PLAYER");
            System.out.println("  Multiplayer RED: java LudoGame PLAYER_RED");
            System.out.println("  Multiplayer BLUE: java LudoGame PLAYER_BLUE");

            JOptionPane.showMessageDialog(null,
                    "Please provide player type:\n\n" +
                            "SINGLE_PLAYER - Play alone\n" +
                            "PLAYER_RED - Multiplayer as RED\n" +
                            "PLAYER_BLUE - Multiplayer as BLUE\n\n" +
                            "For multiplayer, RmiServer must be running!",
                    "Player Type Required",
                    JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }

        String firstCommandLineArg = args[0];
        boolean playerTypeExists = false;

        for (PlayerType type : PlayerType.values()) {
            if (firstCommandLineArg.equals(type.toString())) {
                playerTypeExists = true;
                break;
            }
        }

        if (!playerTypeExists) {
            System.out.println("Invalid player type: " + firstCommandLineArg);
            System.out.println("Valid options: SINGLE_PLAYER, PLAYER_RED, PLAYER_BLUE");

            JOptionPane.showMessageDialog(null,
                    "Invalid player type: " + firstCommandLineArg + "\n\n" +
                            "Valid options:\n" +
                            "- SINGLE_PLAYER\n" +
                            "- PLAYER_RED\n" +
                            "- PLAYER_BLUE",
                    "Invalid Player Type",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        } else {
            playerType = PlayerType.valueOf(firstCommandLineArg);
            System.out.println("Starting Ludo Game as " + playerType);
            System.out.println("RMI Server: " + HOSTNAME);
            launch(args);
        }
    }
}
