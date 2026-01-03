package com.projectkamberinesludogame.ludogame.utils;

import com.projectkamberinesludogame.ludogame.model.GameState;

import java.io.*;

public class GameUtils {
    private static final String SAVE_FILE_PATH = "game.dat";

    public static boolean saveGame(GameState gameState) {
        FileOutputStream fileOut = null;
        ObjectOutputStream objectOut = null;

        try {
            fileOut = new FileOutputStream(SAVE_FILE_PATH);
            objectOut = new ObjectOutputStream(fileOut);

            objectOut.writeObject(gameState);
            objectOut.flush();

            System.out.println("Game saved successfully to " + SAVE_FILE_PATH);
            return true;

        } catch (FileNotFoundException e) {
            System.err.println("Error: Could not create save file - " + e.getMessage());
            return false;
        } catch (IOException e) {
            System.err.println("Error: Failed to save game - " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (objectOut != null) objectOut.close();
                if (fileOut != null) fileOut.close();
            } catch (IOException e) {
                System.err.println("Error closing streams: " + e.getMessage());
            }
        }
    }

    public static GameState loadGame() {
        FileInputStream fileIn = null;
        ObjectInputStream objectIn = null;

        try {
            File saveFile = new File(SAVE_FILE_PATH);

            if (!saveFile.exists()) {
                System.out.println("No save file found at " + SAVE_FILE_PATH);
                return null;
            }

            fileIn = new FileInputStream(SAVE_FILE_PATH);
            objectIn = new ObjectInputStream(fileIn);

            GameState gameState = (GameState) objectIn.readObject();

            System.out.println("Game loaded successfully from " + SAVE_FILE_PATH);
            return gameState;

        } catch (FileNotFoundException e) {
            System.err.println("Error: Save file not found - " + e.getMessage());
            return null;

        } catch (IOException e) {
            System.err.println("Error: Failed to load game - " + e.getMessage());
            e.printStackTrace();
            return null;

        } catch (ClassNotFoundException e) {
            System.err.println("Error: Invalid save file format - " + e.getMessage());
            e.printStackTrace();
            return null;

        } finally {
            try {
                if (objectIn != null) objectIn.close();
                if (fileIn != null) fileIn.close();
            } catch (IOException e) {
                System.err.println("Error closing streams: " + e.getMessage());
            }
        }
    }

//    public static boolean saveFileExists() {
//        File saveFile = new File(SAVE_FILE_PATH);
//        return saveFile.exists();
//    }
//
//    public static boolean deleteSaveFile() {
//        File saveFile = new File(SAVE_FILE_PATH);
//        if (saveFile.exists()) {
//            boolean deleted = saveFile.delete();
//            if (deleted) {
//                System.out.println("Save file deleted successfully");
//            }
//            return deleted;
//        }
//        return false;
//    }
}