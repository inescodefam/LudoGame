package com.projectkamberinesludogame.ludogame.model;

import java.io.Serializable;

public class GameMove implements Serializable {

    private static final long serialVersionUID = 1L;

    public GameMove(PlayerType playerType, Position position) {
        this.playerType = playerType;
        this.position = position;
    }

    public GameMove() {}

    public PlayerType getPlayerType() {
        return playerType;
    }

    public Position getPosition() {
        return position;
    }

    private PlayerType playerType;
    private Position position;
}
