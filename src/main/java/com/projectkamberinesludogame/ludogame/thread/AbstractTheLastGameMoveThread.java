package com.projectkamberinesludogame.ludogame.thread;

import com.projectkamberinesludogame.ludogame.model.GameMove;
import com.projectkamberinesludogame.ludogame.utils.FileUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class AbstractTheLastGameMoveThread {

    public synchronized void saveLastGameMove(GameMove gameMove) {

        while (FileUtils.FILE_ACCESS_IN_PROGRESS) {
            try{
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        FileUtils.FILE_ACCESS_IN_PROGRESS = true;

        List<GameMove> gameMoves = new ArrayList<>();
        if(Files.exists(Path.of(FileUtils.GAME_MOVE_HISTORY_FILE_NAME))) {
            try (ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream(FileUtils.GAME_MOVE_HISTORY_FILE_NAME)
            )){
                List<GameMove> lastGameMoves = (List<GameMove>) ois.readObject();
                gameMoves.addAll(lastGameMoves);
            } catch (IOException | ClassNotFoundException e) {
                File file = new File(FileUtils.GAME_MOVE_HISTORY_FILE_NAME);
                if (file.exists()) {
                    file.delete();
                }

                System.err.println("Error reading last game moves from file ");
                throw new RuntimeException(e);
            }
        }
        gameMoves.add(gameMove);

        try(ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(FileUtils.GAME_MOVE_HISTORY_FILE_NAME))) {
            oos.writeObject(gameMoves);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        FileUtils.FILE_ACCESS_IN_PROGRESS = false;
        notifyAll();

    }

    public synchronized List<GameMove> loadLastGameMoves() {

        while (FileUtils.FILE_ACCESS_IN_PROGRESS) {
            try{
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        FileUtils.FILE_ACCESS_IN_PROGRESS = true;

        List<GameMove> gameMoves = new ArrayList<>();
        if(Files.exists(Path.of(FileUtils.GAME_MOVE_HISTORY_FILE_NAME))) {
            try(ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream(FileUtils.GAME_MOVE_HISTORY_FILE_NAME)
            )){
                List<GameMove> lastGameMoves = (List<GameMove>) ois.readObject();
                gameMoves.addAll(lastGameMoves);
            } catch (IOException | ClassNotFoundException e) {}
        }

        FileUtils.FILE_ACCESS_IN_PROGRESS = false;
        notifyAll();
        return gameMoves;
    }
}
