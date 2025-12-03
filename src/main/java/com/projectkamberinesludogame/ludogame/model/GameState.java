package com.projectkamberinesludogame.ludogame.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class GameState implements Serializable {
    private static final long serialVersionUID = 1L;

    public Map<Integer, Piece> pieces = new HashMap<>();
    private int currentPlayer = 0;
    int lastDiceValue = 0;
    int playerWhoSaved = -1;

    public GameState() {
        for (int i = 0; i < 4; i++) {
            pieces.put(i, new Piece(i, 0, -1));
            pieces.put(i + 4, new Piece(i + 4, 1, -1));
        }
    }

    public Map<Integer, Piece> getPieces() {
        return pieces;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public int getLastDiceValue() {
        return lastDiceValue;
    }

    public int getPlayerWhoSaved() {
        return playerWhoSaved;
    }

    public void setCurrentPlayer(int currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public void setLastDiceValue(int lastDiceValue) {
        this.lastDiceValue = lastDiceValue;
    }

    public void setPlayerWhoSaved(int playerWhoSaved) {
        this.playerWhoSaved = playerWhoSaved;
    }
}
