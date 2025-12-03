package com.projectkamberinesludogame.ludogame;

import com.projectkamberinesludogame.ludogame.model.GameState;
import com.projectkamberinesludogame.ludogame.model.Piece;
import com.projectkamberinesludogame.ludogame.utils.GameUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;
import javafx.scene.input.MouseEvent;

import java.util.*;

public class GameController {
    @FXML private Button rollButton;
    @FXML private Label statusLabel;
    @FXML private Label diceLabel;

    // Home circles
    @FXML private Circle redHome0, redHome1, redHome2, redHome3;
    @FXML private Circle blueHome0, blueHome1, blueHome2, blueHome3;

    // Path cells (0-55)
    private Map<Integer, Pane> pathCells = new HashMap<>();

    // finish lanes
    @FXML private Pane redFinish0, redFinish1, redFinish2, redFinish3;
    @FXML private Pane blueFinish0, blueFinish1, blueFinish2, blueFinish3;

    @FXML private Pane cell0, cell1, cell2, cell3, cell4, cell5, cell6, cell7, cell8, cell9;
    @FXML private Pane cell10, cell11, cell12, cell13, cell14, cell15, cell16, cell17, cell18, cell19;
    @FXML private Pane cell20, cell21, cell22, cell23, cell24, cell25, cell26, cell27, cell28, cell29;
    @FXML private Pane cell30, cell31, cell32, cell33, cell34, cell35, cell36, cell37, cell38, cell39;
    @FXML private Pane cell40, cell41, cell42, cell43, cell44, cell45, cell46, cell47, cell48, cell49;
    @FXML private Pane cell50, cell51, cell52, cell53, cell54, cell55;

    private NetworkManager network;
    private GameState state;
    private int playerColor;
    private int diceValue = 0;
    private Map<Integer, Circle> pieceCircles = new HashMap<>();
    private Circle[] redHomeCircles;
    private Circle[] blueHomeCircles;

    // Constants for your board layout
    private static final int RED_START_CELL = 55;  // cell55 is RED START
    private static final int BLUE_START_CELL = 27; // cell27 is BLUE START
    private static final int TOTAL_PATH_CELLS = 56; // cells 0-55
    private static final int HOME_STRETCH_ENTRY = 56; // Position when entering finish lanes
    private static final int FINISH_POSITION = 60; // Final position (56+4 finish cells)

    @FXML private Button saveButton;
    @FXML private Button loadButton;

    @FXML
    private void initialize() {
        state = new GameState();
        setupBoard();
        updateBoard();
    }

    private void setupBoard() {
        pieceCircles = new HashMap<>();

        redHomeCircles = new Circle[]{redHome0, redHome1, redHome2, redHome3};
        for (int i = 0; i < 4; i++) {

            redHomeCircles[i].setFill(Color.RED);
            redHomeCircles[i].setUserData(i);
        }

        blueHomeCircles = new Circle[]{blueHome0, blueHome1, blueHome2, blueHome3};
        for (int i = 0; i < 4; i++) {

            blueHomeCircles[i].setFill(Color.BLUE);
            blueHomeCircles[i].setUserData(i + 4);
        }

        loadPathCells();
    }

    private void loadPathCells() {
        pathCells.put(0, cell0); pathCells.put(1, cell1); pathCells.put(2, cell2);
        pathCells.put(3, cell3); pathCells.put(4, cell4); pathCells.put(5, cell5);
        pathCells.put(6, cell6); pathCells.put(7, cell7); pathCells.put(8, cell8);
        pathCells.put(9, cell9); pathCells.put(10, cell10); pathCells.put(11, cell11);
        pathCells.put(12, cell12); pathCells.put(13, cell13); pathCells.put(14, cell14);
        pathCells.put(15, cell15); pathCells.put(16, cell16); pathCells.put(17, cell17);
        pathCells.put(18, cell18); pathCells.put(19, cell19); pathCells.put(20, cell20);
        pathCells.put(21, cell21); pathCells.put(22, cell22); pathCells.put(23, cell23);
        pathCells.put(24, cell24); pathCells.put(25, cell25); pathCells.put(26, cell26);
        pathCells.put(27, cell27); pathCells.put(28, cell28); pathCells.put(29, cell29);
        pathCells.put(30, cell30); pathCells.put(31, cell31); pathCells.put(32, cell32);
        pathCells.put(33, cell33); pathCells.put(34, cell34); pathCells.put(35, cell35);
        pathCells.put(36, cell36); pathCells.put(37, cell37); pathCells.put(38, cell38);
        pathCells.put(39, cell39); pathCells.put(40, cell40); pathCells.put(41, cell41);
        pathCells.put(42, cell42); pathCells.put(43, cell43); pathCells.put(44, cell44);
        pathCells.put(45, cell45); pathCells.put(46, cell46); pathCells.put(47, cell47);
        pathCells.put(48, cell48); pathCells.put(49, cell49); pathCells.put(50, cell50);
        pathCells.put(51, cell51); pathCells.put(52, cell52); pathCells.put(53, cell53);
        pathCells.put(54, cell54); pathCells.put(55, cell55);

        System.out.println("Loaded " + pathCells.size() + " path cells");
    }

    public void startAsHost(int port) {
        playerColor = 0; // RED
        network = new NetworkManager(true, port, this::handleNetworkMessage);
        network.start();
        updateStatus("Waiting for opponent to join...");
        rollButton.setDisable(true);
    }

    public void startAsClient(int port) {
        playerColor = 1; // BLUE
        network = new NetworkManager(false, port, this::handleNetworkMessage);
        network.start();
        updateStatus("Connecting to host...");
        rollButton.setDisable(true);
    }

    private void handleNetworkMessage(String message) {
        Platform.runLater(() -> {
            String[] parts = message.split(":");
            switch (parts[0]) {
                case "CONNECTED":
                    updateStatus("Game started! RED's turn");
                    if (playerColor == 0) {
                        rollButton.setDisable(false);
                    }
                    break;
                case "ROLL":
                    int rollColor = Integer.parseInt(parts[1]);
                    int dice = Integer.parseInt(parts[2]);
                    diceValue = dice;
                    diceLabel.setText("Dice: " + dice);
                    if(rollColor == playerColor) {
                        updateStatus("You rolled " + dice + ". Click a highlighted piece to move.");
                        highlightMovablePieces();
                        state.setCurrentPlayer(playerColor);
                    }
                    break;
                case "MOVE":
                    int pieceId = Integer.parseInt(parts[2]);
                    int steps = Integer.parseInt(parts[3]);
                    if (pieceId == -1) {
                        state.setCurrentPlayer(playerColor);
                        diceValue = 0;
                        rollButton.setDisable(false);
                        updateStatus("Opponent had no valid moves. Your turn!");
                        diceLabel.setText("Dice: -");
                    } else {
                        performMove(pieceId, steps);
                        updateBoard();

                        if (steps == 6) {
                            updateStatus("Opponent got 6 and rolls again. Waiting...");
                            rollButton.setDisable(true);
                        } else {
                            state.setCurrentPlayer(playerColor);
                            diceValue = 0;
                            rollButton.setDisable(false);
                            updateStatus("Your turn! Roll the dice.");
                            diceLabel.setText("Dice: -");
                        }
                    }
                    break;
                case "WIN":
                    showWinner(parts[2]);
                    break;
                case "YOUR_TURN":
                    int turnColor = Integer.parseInt(parts[1]);
                    if (playerColor == turnColor) {

                        state.setCurrentPlayer(playerColor);
                        rollButton.setDisable(false);
                        updateStatus("Your turn! Roll the dice.");
                    } else {

                        state.setCurrentPlayer(1 - playerColor);
                        rollButton.setDisable(true);
                        updateStatus("Opponent's turn. Waiting...");

                    }

            }
        });
    }

    @FXML
    private void onRollDice() {
        int dice = (int)(Math.random() * 6) + 1;
        diceValue = dice;
        diceLabel.setText("Dice: " + dice);
        rollButton.setDisable(true);

        network.send("ROLL:" + playerColor + ":" + dice);
        updateStatus("You rolled " + dice + ". Click a highlighted piece to move.");
        highlightMovablePieces();
    }

    @FXML
    private void onPieceClick(MouseEvent event) {
        if (state.getCurrentPlayer() != playerColor || diceValue == 0) return;

        Circle clicked = (Circle) event.getSource();
        Integer pieceId = (Integer) clicked.getUserData();

        if (pieceId == null) return;

        Piece piece = state.pieces.get(pieceId);
        if (piece == null || piece.color != playerColor) return;

        if (piece.position == -1 && diceValue != 6) {
            updateStatus("You need to roll 6 to leave home!");
            return;
        }

        if (canMovePiece(piece)) {
            movePiece(pieceId);
        }
    }

    @FXML
    private void onCellClick(MouseEvent event) {
        if (state.getCurrentPlayer() != playerColor || diceValue == 0) return;

        Pane clicked = (Pane) event.getSource();

        for (Map.Entry<Integer, Pane> entry : pathCells.entrySet()) {
            if (entry.getValue() == clicked) {
                int cellIndex = entry.getKey();


                for (Piece p : state.pieces.values()) {
                    if (p.color == playerColor && getCellForPosition(p.position, p.color) == cellIndex) {
                        if (canMovePiece(p)) {
                            movePiece(p.id);
                            return;
                        }
                    }
                }
                break;
            }
        }
    }

    private boolean canMovePiece(Piece piece) {
        if (piece.position == -1) {

            return diceValue == 6;
        } else if (piece.position < HOME_STRETCH_ENTRY) {

            return true;
        } else if (piece.position < FINISH_POSITION) { // removala piece.position >= HOME_STRETCH_ENTRY &&

            return piece.position + diceValue <= FINISH_POSITION;
        }
        return false;
    }

    private void movePiece(int pieceId) {
        int moveSteps = diceValue;
        performMove(pieceId, moveSteps);
        network.send("MOVE:" + playerColor + ":" + pieceId + ":" + moveSteps);

        updateBoard();

        if (checkWin(playerColor)) {
            String winner = playerColor == 0 ? "RED" : "BLUE";
            network.send("WIN:" + playerColor + ":"+ winner);
            showWinner(winner);
            return;
        }

        diceLabel.setText("Dice: -");

        if (moveSteps == 6) {
            state.setCurrentPlayer(playerColor);
            diceValue = 0;
            rollButton.setDisable(false);
            updateStatus("You got 6! Roll again.");
        } else {
            diceValue = 0;
            rollButton.setDisable(true);
            updateStatus("Opponent's turn. Waiting...");
            network.send("YOUR_TURN:" + (1-playerColor) );
        }
    }

    private void performMove(int pieceId, int steps) {
        Piece piece = state.pieces.get(pieceId);
        if (piece == null) return;

        if (piece.position == -1 && steps == 6) {
                piece.position = 0; //  starts at position 0
            System.out.println("Piece " + pieceId + " (" + (piece.color == 0 ? "RED" : "BLUE") + ") enters board at position " + piece.position);
        } else if (piece.position >= 0 && piece.position < HOME_STRETCH_ENTRY) {

            int oldPos = piece.position;
            piece.position += steps;

            if (piece.position >= 51) {
                int overflow = piece.position - 51;
                piece.position = HOME_STRETCH_ENTRY + overflow;
                System.out.println("Piece " + pieceId + " enters home stretch at position " + piece.position);
            } else {
                int movedToCell = getCellForPosition(piece.position, piece.color);

                for (Piece p : state.pieces.values()) {
                    if (p.id != pieceId &&
                            p.position == piece.position &&
                            p.position >= 0 &&
                            p.position < HOME_STRETCH_ENTRY &&
                            getCellForPosition(p.position, p.color) == movedToCell &&
                            p.color != piece.color
                    ) {
                        p.position = -1;
                        if (p.color == piece.color) {
                            System.out.println("Piece " + pieceId + " bumped own piece " + p.id + " back home!");
                        } else {
                            System.out.println("Piece " + pieceId + " captured opponent piece " + p.id + "!");
                        }
                    }
                }
            }

            System.out.println("Piece " + pieceId + " moved from " + oldPos + " to " + piece.position);
        } else if (piece.position >= HOME_STRETCH_ENTRY) {

            piece.position += steps;
            if (piece.position > FINISH_POSITION) {
                piece.position = FINISH_POSITION;
            }
            System.out.println("Piece " + pieceId + " in home stretch at position " + piece.position);
        }
    }

    private int getCellForPosition(int position, int color) {

        if (position == -1) return -1; // home

        if (position < HOME_STRETCH_ENTRY) {

            if (color == 0) {

                if (position == 0) return RED_START_CELL; // cell55
                else return (RED_START_CELL + position) % TOTAL_PATH_CELLS; // Wrap around
            } else {

                if (position == 0) return BLUE_START_CELL; // cell27
                else return (BLUE_START_CELL + position) % TOTAL_PATH_CELLS; // Wrap around
            }
        }

        return -2;
    }

    private void highlightMovablePieces() {
        boolean hasMove = false;

        if(playerColor == 0){
            for (Circle c : redHomeCircles) {
                c.setStroke(Color.web("#ddd291"));
                c.setStrokeWidth(2);
            }
            for (Circle c : pieceCircles.values()) {
                c.setStroke(Color.BLACK);
                c.setStrokeWidth(2);
            }
        }

        if(playerColor == 1){
            for (Circle c : blueHomeCircles) {
                c.setStroke(Color.web("#ddd291"));
                c.setStrokeWidth(2);
            }
            for (Circle c : pieceCircles.values()) {
                c.setStroke(Color.BLACK);
                c.setStrokeWidth(2);
            }
        }


        for (Piece p : state.pieces.values()) {
            if (p.color == playerColor) {
                boolean canMove = false;

                if (p.position == -1 && diceValue == 6) {
                    canMove = true;
                }
                else if (p.position >= 0 && p.position < HOME_STRETCH_ENTRY) {
                    canMove = true;
                }
                else if (p.position >= HOME_STRETCH_ENTRY && p.position < FINISH_POSITION) {
                    if (p.position + diceValue <= FINISH_POSITION) {
                        canMove = true;
                    }
                }

                if (canMove) {
                    hasMove = true;
                    Circle c = getCircleForPiece(p.id);
                    if (c != null) {
                        c.setStroke(Color.YELLOW);
                        c.setStrokeWidth(4);
                    }
                }
            }
        }

        if (!hasMove) {
            updateStatus("No valid moves! Passing turn...");
            network.send("MOVE:"+ playerColor +":-1:0");
            state.setCurrentPlayer(1 - playerColor);
            diceValue = 0;
            diceLabel.setText("Dice: -");
            rollButton.setDisable(true);
            network.send("YOUR_TURN:" + (1 - playerColor) );
        }
    }

    private Circle getCircleForPiece(int pieceId) {
        Piece p = state.pieces.get(pieceId);
        if (p == null) return null;

        if (p.position == -1) {
            if (p.color == 0) {
                return redHomeCircles[pieceId];
            } else {
                return blueHomeCircles[pieceId - 4];
            }
        } else {
            return pieceCircles.get(pieceId);
        }
    }

    private void updateBoard() {
        for (Pane cell : pathCells.values()) {
            cell.getChildren().clear();
        }

        if (redFinish0 != null) redFinish0.getChildren().clear();
        if (redFinish1 != null) redFinish1.getChildren().clear();
        if (redFinish2 != null) redFinish2.getChildren().clear();
        if (redFinish3 != null) redFinish3.getChildren().clear();

        if (blueFinish0 != null) blueFinish0.getChildren().clear();
        if (blueFinish1 != null) blueFinish1.getChildren().clear();
        if (blueFinish2 != null) blueFinish2.getChildren().clear();
        if (blueFinish3 != null) blueFinish3.getChildren().clear();

        pieceCircles.clear();

        for (int i = 0; i < 4; i++) {
            redHomeCircles[i].setVisible(false);
            blueHomeCircles[i].setVisible(false);
        }


        for (Piece p : state.pieces.values()) {
            if (p.position == -1) {

                if (p.color == 0) {
                    redHomeCircles[p.id].setVisible(true);
                } else {
                    blueHomeCircles[p.id - 4].setVisible(true);
                }
            } else if (p.position < HOME_STRETCH_ENTRY) {

                int cellNum = getCellForPosition(p.position, p.color);
                Pane cell = pathCells.get(cellNum);
                System.out.println((p.color == 0 ? "RED" : "BLUE") + " piece " + p.id + " at position " + p.position + " -> cell " + cellNum);

                if (cell != null) {
                    Circle piece = createPieceCircle(p);
                    cell.getChildren().add(piece);
                    pieceCircles.put(p.id, piece);
                }
            } else {

                int finishIndex = p.position - HOME_STRETCH_ENTRY;
                Pane finishCell = p.color == 0 ? getRedFinishCell(finishIndex) : getBlueFinishCell(finishIndex);
                if (finishCell != null) {
                    Circle piece = createPieceCircle(p);
                    finishCell.getChildren().add(piece);
                    pieceCircles.put(p.id, piece);
                }
            }
        }
    }

    private Circle createPieceCircle(Piece p) {
        Circle piece = new Circle(15);
        piece.setFill(p.color == 0 ? Color.RED : Color.BLUE);
        piece.setStroke(Color.BLACK);
        piece.setStrokeWidth(2);
        piece.setUserData(p.id);
        piece.setOnMouseClicked(this::onPieceClick);
        piece.setLayoutX(25);
        piece.setLayoutY(25);
        return piece;
    }

    private Pane getRedFinishCell(int index) {
        switch (index) {
            case 0: return redFinish0;
            case 1: return redFinish1;
            case 2: return redFinish2;
            case 3: return redFinish3;
            default: return null;
        }
    }

    private Pane getBlueFinishCell(int index) {
        switch (index) {
            case 0: return blueFinish0;
            case 1: return blueFinish1;
            case 2: return blueFinish2;
            case 3: return blueFinish3;
            default: return null;
        }
    }

    private boolean checkWin(int color) {
        int count = 0;
        for (Piece p : state.pieces.values()) {
            if (p.color == color && p.position == FINISH_POSITION) count++;
        }
        return count == 4;
    }

    private void showWinner(String winner) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText(winner + " WINS!");
        alert.setContentText("Congratulations!");
        alert.showAndWait();
        rollButton.setDisable(true);
    }

    private void updateStatus(String msg) {
        statusLabel.setText(msg);
    }

    public void onSaveClick() {
        GameState gameState = new GameState();
        gameState.pieces.putAll(state.pieces);
        gameState.setLastDiceValue(diceValue);
        gameState.setPlayerWhoSaved(state.getCurrentPlayer());
        gameState.setCurrentPlayer(state.getCurrentPlayer());

        GameUtils.saveGame(gameState);
    }

    public void onLoadClick() {
        GameState lastGameState = GameUtils.loadGame();
        state.pieces.putAll(lastGameState.pieces);
        state.setLastDiceValue(lastGameState.getLastDiceValue());
        state.setCurrentPlayer(lastGameState.getCurrentPlayer());

        updateBoard();

    }
}

