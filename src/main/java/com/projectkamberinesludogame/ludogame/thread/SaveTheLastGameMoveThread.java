package com.projectkamberinesludogame.ludogame.thread;

import com.projectkamberinesludogame.ludogame.model.GameMove;

public class SaveTheLastGameMoveThread extends  AbstractTheLastGameMoveThread  implements Runnable {

    private GameMove gameMove;

    public GameMove getGameMove() {
        return gameMove;
    }

    public void setGameMove(GameMove gameMove) {
        this.gameMove = gameMove;
    }

    public SaveTheLastGameMoveThread(GameMove gameMove) {
        this.gameMove = gameMove;
    }

    @Override
    public void run() {
        saveLastGameMove(gameMove);
    }
}
