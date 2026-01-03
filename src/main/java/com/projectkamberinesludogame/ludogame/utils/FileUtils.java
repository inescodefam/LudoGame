package com.projectkamberinesludogame.ludogame.utils;

import com.projectkamberinesludogame.ludogame.model.Position;
import com.projectkamberinesludogame.ludogame.thread.ReadTheLastGameMoveThread;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.util.Duration;

import java.util.Optional;

public class FileUtils {

    public  static Boolean FILE_ACCESS_IN_PROGRESS = false;
    public static final String GAME_MOVE_HISTORY_FILE_NAME = "doc/gameMoves.dat";

    public static Optional<Position> piecesPosition(int position) { //  u javi optional znaći da nešto može biti nullable ili moze ne postojati
        Optional<Position> pos = position == 0 ? Optional.empty() : Optional.of(new Position(position));
        return pos;
    }

    public static Timeline getLastGameMove(Label theLastGameMoveLabelID) {
        Timeline showLastGameMove = new Timeline(
                new KeyFrame(Duration.ZERO, e -> {
                    new Thread(new ReadTheLastGameMoveThread(theLastGameMoveLabelID)).start();
                }),
                new KeyFrame(Duration.seconds(2)) // koliko cesto ce se dogoditi
        );

        showLastGameMove.setCycleCount(Animation.INDEFINITE); // infinity
        return showLastGameMove;
    }
}
