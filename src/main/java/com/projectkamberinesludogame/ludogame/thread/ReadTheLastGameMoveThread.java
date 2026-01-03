package com.projectkamberinesludogame.ludogame.thread;

import com.projectkamberinesludogame.ludogame.model.GameMove;
import javafx.application.Platform;
import javafx.scene.control.Label;

import java.util.List;

public class ReadTheLastGameMoveThread extends AbstractTheLastGameMoveThread implements Runnable {

    private Label label;

    public Label getLabel() {
        return label;
    }

    public void setLabel(Label label) {
        this.label = label;
    }

    public ReadTheLastGameMoveThread(Label label) {
        this.label = label;
    }

    @Override
   public void run() {
    try {
        List<GameMove> gameMoves = loadLastGameMoves();

        if (!gameMoves.isEmpty()) {
            GameMove lastGameMove = gameMoves.getLast();
            Platform.runLater(() ->
                    label.setText(
                            lastGameMove.getPlayerType() +
                                    " position: " +
                                    lastGameMove.getPosition()
                    )
            );
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    }
}
