package com.projectkamberinesludogame.ludogame.controller;

import com.projectkamberinesludogame.ludogame.LudoGame;
import com.projectkamberinesludogame.ludogame.model.*;
import com.projectkamberinesludogame.ludogame.rmi.ChatRemoteService;
import com.projectkamberinesludogame.ludogame.rmi.GameRemoteService;
import com.projectkamberinesludogame.ludogame.rmi.RmiServer;
import com.projectkamberinesludogame.ludogame.thread.SaveTheLastGameMoveThread;
import com.projectkamberinesludogame.ludogame.utils.*;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class GameController {
    @FXML
    public Label theLastGameMoveLabelID;
    @FXML
    public Button loadBtn;
    @FXML
    public Button saveBtn;
    @FXML
    public Button docuBtn;
    @FXML
    private Button rollButton;
    @FXML
    private Label statusLabel;
    @FXML
    private Label diceLabel;

    // Home circles
    @FXML
    private Circle redHome0, redHome1, redHome2, redHome3;
    @FXML
    private Circle blueHome0, blueHome1, blueHome2, blueHome3;

    // Path cells (0-55)
    private Map<Integer, Pane> pathCells = new HashMap<>();

    // Finish lanes
    @FXML
    private Pane redFinish0, redFinish1, redFinish2, redFinish3;
    @FXML
    private Pane blueFinish0, blueFinish1, blueFinish2, blueFinish3;

    @FXML
    private Pane cell0, cell1, cell2, cell3, cell4, cell5, cell6, cell7, cell8, cell9;
    @FXML
    private Pane cell10, cell11, cell12, cell13, cell14, cell15, cell16, cell17, cell18, cell19;
    @FXML
    private Pane cell20, cell21, cell22, cell23, cell24, cell25, cell26, cell27, cell28, cell29;
    @FXML
    private Pane cell30, cell31, cell32, cell33, cell34, cell35, cell36, cell37, cell38, cell39;
    @FXML
    private Pane cell40, cell41, cell42, cell43, cell44, cell45, cell46, cell47, cell48, cell49;
    @FXML
    private Pane cell50, cell51, cell52, cell53, cell54, cell55;

    private GameState state;
    private int playerColor;
    private int diceValue = 0;
    private Map<Integer, Circle> pieceCircles = new HashMap<>();
    private Circle[] redHomeCircles;
    private Circle[] blueHomeCircles;

    // Board constants - RED počinje na 55, BLUE na 28, završavaju kad aprijeđu put od 54 polja svaki roll nakon toga je ulazak u kućicu
    private static final int RED_START_CELL = 55;
    private static final int BLUE_START_CELL = 27;
    private static final int TOTAL_PATH_CELLS = 54;
    private static final int HOME_STRETCH_ENTRY = 56;
    private static final int FINISH_POSITION = 59;

    private static final int RED_ENTRY_CELL = 54;   // RED ulazi u finish  nakon cell54
    private static final int BLUE_ENTRY_CELL = 26;  // BLUE ulazi u finish  nakon cell26

    // RMI
    private GameRemoteService gameService;
    private GameCallbackImpl callbackImpl;
    private boolean isSinglePlayer;

    // chat messages
    @FXML
    private TextField chatMessageTextField;

    @FXML
    private TextArea chatMessageTextArea;

    ChatRemoteService chatRemoteService;

    @FXML
    private BorderPane pane;

    @FXML
    private MenuButton themeMenuButton;

    @FXML
    private MenuItem themeDefault, themeDark, themeNature, themeSunset, themeOcean, themePurple;

    private Theme currentTheme = Theme.DEFAULT;

    @FXML
    private void initialize() {
        state = new GameState();
        setupBoard();
        //updateBoard();
        isSinglePlayer = LudoGame.playerType == PlayerType.SINGLE_PLAYER;

        Theme savedTheme = ThemeUtils.loadTheme(LudoGame.playerType);
        applyTheme(savedTheme != null ? savedTheme : Theme.DEFAULT);
        themeMenuButton.setText("Theme: " + savedTheme.getDisplayName());
//
         // scaling btn roll dice
//        rollButton.setStyle("-fx-background-color: purple; -fx-text-fill: white; -fx-font-weight: bold;");
//
//        rollButton.setOnMouseEntered(e -> {
//            rollButton.setScaleX(1.0);
//            rollButton.setScaleY(1.0);
//        });
//
//        rollButton.setOnMouseExited(e -> {
//            rollButton.setScaleX(1.0);
//            rollButton.setScaleY(1.0);
//        });
//
//        rollButton.scaleXProperty().addListener((obs, oldVal, newVal) -> {
//            if (newVal.doubleValue() != 1.0) {
//                rollButton.setScaleX(1.0);
//            }
//        });
//
//        rollButton.scaleYProperty().addListener((obs, oldVal, newVal) -> {
//            if (newVal.doubleValue() != 1.0) {
//                rollButton.setScaleY(1.0);
//            }
//        });

        if (isSinglePlayer) {
//            pane.getLeft().setVisible(chatEnabled);
//            pane.getLeft().prefWidth(0); //ne dela todo

            pane.setLeft(null);

            playerColor = 0; // RED
            state.setCurrentPlayer(0);
            updateStatus("Single Player Mode - RED's turn. Roll the dice!");
            rollButton.setDisable(false);

        } else {
            playerColor = LudoGame.playerType == PlayerType.PLAYER_RED ? 0 : 1;
            System.out.println("=== MULTIPLAYER MODE: " + (playerColor == 0 ? "RED" : "BLUE") + " ===");
            connectToRMIServer();
        }

        if(!isSinglePlayer) {
            try {
                Registry registry = LocateRegistry.getRegistry(RmiServer.HOSTNAME, RmiServer.RMI_PORT);
                chatRemoteService = (ChatRemoteService) registry.lookup(ChatRemoteService.REMOTE_OBJECT_NAME);
            } catch (RemoteException | NotBoundException e) {
                e.printStackTrace();
            }

            Timeline chatMessagesRefreshTimeLine = getChatRefreshTimeline();
            //chatMessagesRefreshTimeLine.setCycleCount(Animation.INDEFINITE);
            chatMessagesRefreshTimeLine.play();
        } else {
            Timeline showTheLastMoveTimeline = FileUtils.getLastGameMove(theLastGameMoveLabelID);
            showTheLastMoveTimeline.play();
        }
    }

    private Timeline getChatRefreshTimeline() {
        Timeline chatMessagesRefreshTimeLine = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            try {
                List<String> chatMessages =  chatRemoteService.getAllMessages();
                StringBuilder textMessagesBuilder = new StringBuilder();
                for(String message : chatMessages) {
                    textMessagesBuilder.append(message).append("\n");
                }

                chatMessageTextArea.setText(textMessagesBuilder.toString());

            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }
        }

        ),
                new KeyFrame(Duration.seconds(1))
        );
        chatMessagesRefreshTimeLine.setCycleCount(Animation.INDEFINITE);
        return chatMessagesRefreshTimeLine;
    }

    private void connectToRMIServer() {
        try {
            Registry registry = LocateRegistry.getRegistry(LudoGame.HOSTNAME, 1099);
            gameService = (GameRemoteService) registry.lookup(GameRemoteService.REMOTE_OBJECT_NAME);

            System.out.println("Connected to RMI server");

            callbackImpl = new GameCallbackImpl();
            GameRemoteService.GameCallback callbackStub =
                    (GameRemoteService.GameCallback) UnicastRemoteObject.exportObject(callbackImpl, 0);

            gameService.registerPlayer(playerColor, callbackStub);
            System.out.println(">>> Registered as " + (playerColor == 0 ? "RED" : "BLUE"));

            updateStatus("Waiting for opponent to connect...");
            rollButton.setDisable(true);

        } catch (Exception e) {
            System.err.println("✗ Failed to connect to RMI server!");
            e.printStackTrace();
            updateStatus("ERROR: Cannot connect to server! Make sure RmiServer is running.");
            DialogUtils.showDialog("Connection Error",
                    "Cannot connect to RMI server!\n\nMake sure RmiServer is running:\n" +
                            "java com.projectkamberinesludogame.ludogame.rmi.RmiServer",
                    Alert.AlertType.ERROR);
        }
    }

    public void sendMessageButtonClick() {
        String chatMessage = chatMessageTextField.getText();
        try {
            chatRemoteService.sendChatMessage(LudoGame.playerType + ": " + chatMessage);
            chatMessageTextField.clear();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void onThemeChange(ActionEvent event) {
        MenuItem source = (MenuItem) event.getSource();
        String themeName = source.getText();

        Theme selectedTheme = null;

        switch (themeName) {
            case "Default":
                selectedTheme = Theme.DEFAULT;
                break;
            case "Dark Mode":
                selectedTheme = Theme.DARK;
                break;
            case "Nature":
                selectedTheme = Theme.NATURE;
                break;
            case "Sunset":
                selectedTheme = Theme.SUNSET;
                break;
            case "Ocean":
                selectedTheme = Theme.OCEAN;
                break;
            case "Purple Dream":
                selectedTheme = Theme.PURPLE;
                break;
        }

        if (selectedTheme != null) {
            applyTheme(selectedTheme);

//            if (!isSinglePlayer && gameService != null) {
//                try {
//                    gameService.sendMove(playerColor, "THEME:" + selectedTheme.name());
//                } catch (RemoteException e) {
//                    System.err.println("Failed to send theme to opponent");
//                    e.printStackTrace();
//                }
//            }
            // each player his theme
            boolean saved = ThemeUtils.saveTheme(LudoGame.playerType, selectedTheme);
            if (saved) {
                themeMenuButton.setText("Theme: " + selectedTheme.getDisplayName());
                System.out.println("Theme saved successfully for " + LudoGame.playerType);
            } else {
                System.err.println("Failed to save theme");
            }
        }
    }

    private void applyTheme(Theme theme) {
        currentTheme = theme;

        pane.setStyle("-fx-background-color: " + theme.getBackgroundGradient() + ";");

        for (Pane cell : pathCells.values()) {
            if (!cell.getId().contains("Finish")) {
                cell.setStyle("-fx-background-color: " + theme.getCellColor() +
                        "; -fx-border-color: " + theme.getCellBorderColor() + ";");
            }
        }

        System.out.println("Applied theme: " + theme.getDisplayName());
    }

    private class GameCallbackImpl implements GameRemoteService.GameCallback {

        @Override
        public void onGameStart() throws RemoteException {
            System.out.println(">>> Game starting! Both players connected.");
            Platform.runLater(() -> {
                if (playerColor == 0) {

                    state.setCurrentPlayer(0);
                    rollButton.setDisable(false);
                    updateStatus("Game started! Your turn (RED). Roll the dice!");

                } else {

                    state.setCurrentPlayer(0);
                    rollButton.setDisable(true);
                    updateStatus("Game started! Waiting for RED to play...");

                }
            });
        }

        @Override
        public void onMoveReceived(String moveData) throws RemoteException {
            System.out.println(">>> Received move: " + moveData);
            Platform.runLater(() -> handleOpponentMove(moveData));
        }
    }

    private void handleOpponentMove(String moveData) {
        String[] parts = moveData.split(":");
        String action = parts[0];

        if ("ROLL".equals(action)) {

            int dice = Integer.parseInt(parts[1]);
            diceValue = dice;
            diceLabel.setText("Opponent rolled: " + dice);
            updateStatus("Opponent rolled " + dice + "...");

        } else if ("MOVE".equals(action)) {
            int pieceId = Integer.parseInt(parts[1]);
            int steps = Integer.parseInt(parts[2]);

            if (pieceId == -1) {
                updateStatus("Opponent had no valid moves. Your turn!");
                state.setCurrentPlayer(playerColor);
                rollButton.setDisable(false);
                diceValue = 0;
                diceLabel.setText("Dice: -");
            } else {
                performMove(pieceId, steps);
                updateBoard();

                if (checkWin(1 - playerColor)) {
                    String winner = playerColor == 0 ? "BLUE" : "RED";
                    showWinner(winner);
                    return;
                }

                if (steps == 6) {
                    updateStatus("Opponent got 6 and rolls again...");
                    rollButton.setDisable(true);
                } else {
                    state.setCurrentPlayer(playerColor);
                    rollButton.setDisable(false);
                    updateStatus("Your turn! Roll the dice.");
                    diceValue = 0;
                    diceLabel.setText("Dice: -");
                }
            }
        }
    }

    private void setupBoard() {
        pieceCircles = new HashMap<>();

        redHomeCircles = new Circle[]{redHome0, redHome1, redHome2, redHome3};
        for (int i = 0; i < 4; i++) {
            redHomeCircles[i].setFill(Color.RED);
            redHomeCircles[i].setUserData(i);
            redHomeCircles[i].setOnMouseClicked(this::onPieceClick);
        }

        blueHomeCircles = new Circle[]{blueHome0, blueHome1, blueHome2, blueHome3};
        for (int i = 0; i < 4; i++) {
            blueHomeCircles[i].setFill(Color.BLUE);
            blueHomeCircles[i].setUserData(i + 4);
            blueHomeCircles[i].setOnMouseClicked(this::onPieceClick);
        }

        loadPathCells();
    }

    private void loadPathCells() {
        pathCells.put(0, cell0);pathCells.put(1, cell1);pathCells.put(2, cell2);pathCells.put(3, cell3);pathCells.put(4, cell4);pathCells.put(5, cell5);pathCells.put(6, cell6);pathCells.put(7, cell7);pathCells.put(8, cell8);pathCells.put(9, cell9);pathCells.put(10, cell10);
        pathCells.put(11, cell11);pathCells.put(12, cell12);pathCells.put(13, cell13);pathCells.put(14, cell14);pathCells.put(15, cell15);pathCells.put(16, cell16);pathCells.put(17, cell17);pathCells.put(18, cell18);pathCells.put(19, cell19);pathCells.put(20, cell20);
        pathCells.put(21, cell21);pathCells.put(22, cell22);pathCells.put(23, cell23);pathCells.put(24, cell24);pathCells.put(25, cell25);pathCells.put(26, cell26);pathCells.put(27, cell27);pathCells.put(28, cell28);pathCells.put(29, cell29);pathCells.put(30, cell30);
        pathCells.put(31, cell31);pathCells.put(32, cell32);pathCells.put(33, cell33);pathCells.put(34, cell34);pathCells.put(35, cell35);pathCells.put(36, cell36);pathCells.put(37, cell37);pathCells.put(38, cell38);pathCells.put(39, cell39);pathCells.put(40, cell40);
        pathCells.put(41, cell41);pathCells.put(42, cell42);pathCells.put(43, cell43);pathCells.put(44, cell44);pathCells.put(45, cell45);pathCells.put(46, cell46);pathCells.put(47, cell47);pathCells.put(48, cell48);pathCells.put(49, cell49);pathCells.put(50, cell50);
        pathCells.put(51, cell51);pathCells.put(52, cell52);pathCells.put(53, cell53);pathCells.put(54, cell54);pathCells.put(55, cell55);
    }

    @FXML
    private void onRollDice() {
        int dice = (int) (Math.random() * 6) + 1;
        diceValue = dice;
        diceLabel.setText("Dice: " + dice);
        rollButton.setDisable(true);

        System.out.println((playerColor == 0 ? "RED" : "BLUE") + " rolled " + dice);

        if (!isSinglePlayer && gameService != null) {
            try {
                gameService.sendMove(playerColor, "ROLL:" + dice);
            } catch (RemoteException e) {
                System.err.println("Failed to send roll to opponent");
                e.printStackTrace();
            }
        }

        updateStatus("You rolled " + dice + ". Click a highlighted piece to move.");
        highlightMovablePieces();
    }

    @FXML
    private void onPieceClick(MouseEvent event) {
        if (state.getCurrentPlayer() != playerColor || diceValue == 0) {
            return;
        }

        Circle clicked = (Circle) event.getSource();
        Integer pieceId = (Integer) clicked.getUserData();

        if (pieceId == null) return;

        Piece piece = state.pieces.get(pieceId);
        if (piece == null || piece.color != playerColor) {
            return;
        }

        if (canMovePiece(piece)) {
            movePiece(pieceId);
        } else {
            updateStatus("Invalid move for this piece!");
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
                    if (p.color == playerColor && p.position >= 0 && p.position < HOME_STRETCH_ENTRY) {
                        int pieceCell = getCellForPosition(p.position, p.color);
                        if (pieceCell == cellIndex && canMovePiece(p)) {
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

            int stepsToEntry = getStepsToEntry(piece.position, piece.color);

            if (diceValue <= stepsToEntry) {
                return true;
            } else {
                int stepsIntoFinish = diceValue - stepsToEntry;

                if (stepsIntoFinish > 4) {
                    return false;
                }


                int targetFinishSlot = stepsIntoFinish - 1;
                return isFinishSlotEmpty(piece.color, targetFinishSlot);
            }
        } else if (piece.position >= HOME_STRETCH_ENTRY && piece.position < FINISH_POSITION) {

            int currentSlot = piece.position - HOME_STRETCH_ENTRY;
            int targetSlot = currentSlot + diceValue;

            if (targetSlot > 3) {
                return false;
            }


            return isFinishSlotEmpty(piece.color, targetSlot);
        }

        return false;
    }

    private int getStepsToEntry(int currentPos, int color) {

        int stepsToEntry = 55 - currentPos;

        if(stepsToEntry < 0) {
            return 0;
        }
        return stepsToEntry;
    }

    private boolean isFinishSlotEmpty(int color, int slotIndex) {
        if (slotIndex < 0 || slotIndex > 3) return false;

        int targetPosition = HOME_STRETCH_ENTRY + slotIndex;

        for (Piece p : state.pieces.values()) {
            if (p.color == color && p.position == targetPosition) {
                return false; // zauzet
            }
        }
        return true;
    }

    private void movePiece(int pieceId) {
        int moveSteps = diceValue;

        performMove(pieceId, moveSteps);

        if (!isSinglePlayer && gameService != null) {
            try {
                gameService.sendMove(playerColor, "MOVE:" + pieceId + ":" + moveSteps);
            } catch (RemoteException e) {
                System.err.println("Failed to send move to opponent");
                e.printStackTrace();
            }
        }

        updateBoard();

        if (checkWin(playerColor)) {
            String winner = playerColor == 0 ? "RED" : "BLUE";
            showWinner(winner);
            return;
        }

        diceLabel.setText("Dice: -");

        Optional<Position> lastMovedPosition;
        Piece movedPiece = state.pieces.get(pieceId);

        lastMovedPosition = FileUtils.piecesPosition(movedPiece.position);

        if (lastMovedPosition.isPresent()) {
            PlayerType enumPlayerType =
                    state.getCurrentPlayer() == 1 ? PlayerType.PLAYER_RED : PlayerType.PLAYER_BLUE;

            GameMove gameMove = new GameMove(enumPlayerType, lastMovedPosition.get());

            new Thread(new SaveTheLastGameMoveThread(gameMove)).start();
        }


        if (moveSteps == 6) {
            state.setCurrentPlayer(playerColor);
            diceValue = 0;
            rollButton.setDisable(false);
            updateStatus("You got 6! Roll again.");
        } else {
            if (isSinglePlayer) {
                state.setCurrentPlayer(1 - playerColor);
                playerColor = 1 - playerColor;
                diceValue = 0;
                rollButton.setDisable(false);
                String colorName = playerColor == 0 ? "RED" : "BLUE";
                updateStatus(colorName + "'s turn. Roll the dice!");
            } else {

                diceValue = 0;
                rollButton.setDisable(true);
                state.setCurrentPlayer(1 - playerColor);
                updateStatus("Opponent's turn. Waiting...");
            }
        }
    }

    private void performMove(int pieceId, int steps) {
        Piece piece = state.pieces.get(pieceId);
        if (piece == null) return;

        if (piece.position == -1 && steps == 6) {
            piece.position = 0;
            System.out.println("Piece " + pieceId + " (" + (piece.color == 0 ? "RED" : "BLUE") +
                    ") enters board at position 0");

            int startCell = getCellForPosition(0, piece.color);
            checkAndCaptureAt(startCell, piece.color, pieceId);

        } else if (piece.position >= 0 && piece.position < HOME_STRETCH_ENTRY) {
            int stepsToEntry = getStepsToEntry(piece.position, piece.color);

            if (steps <= stepsToEntry) {
                piece.position += steps;
                int movedToCell = getCellForPosition(piece.position, piece.color);
                checkAndCaptureAt(movedToCell, piece.color, pieceId);
                System.out.println("Piece " + pieceId + " moved to position " + piece.position);
            } else {
                int stepsIntoFinish = steps - stepsToEntry;
                piece.position = HOME_STRETCH_ENTRY + (stepsIntoFinish - 1);
                System.out.println((piece.color == 0 ? "RED" : "BLUE") + " piece " + pieceId +
                        " enters finish lane at position " + piece.position);
            }

        } else if (piece.position >= HOME_STRETCH_ENTRY && piece.position < FINISH_POSITION) {
            piece.position += steps;
            System.out.println("Piece " + pieceId + " in finish lane at position " + piece.position);
        }
    }

    private void checkAndCaptureAt(int cellNum, int attackerColor, int attackerPieceId) {
        for (Piece p : state.pieces.values()) {

            if (p.id == attackerPieceId) continue;

            if (p.position < 0 || p.position >= HOME_STRETCH_ENTRY) continue;

            if (p.color == attackerColor) continue;

            int pCell = getCellForPosition(p.position, p.color);
            if (pCell == cellNum) {
                System.out.println(">>> CAPTURE! Piece " + attackerPieceId + " captured opponent piece " + p.id);
                p.position = -1;
            }
        }
    }

    private int getCellForPosition(int position, int color) {
        if (position == -1) return -1; // Home
        if (position >= HOME_STRETCH_ENTRY) return -2;

        if (color == 0) {

            if (position == 0) {
                return RED_START_CELL; // 55
            } else {
                return (position - 1) % TOTAL_PATH_CELLS;
            }
        } else {
            return (BLUE_START_CELL + position) % TOTAL_PATH_CELLS;
        }
    }

    private void highlightMovablePieces() {
        boolean hasMove = false;

        for (Circle c : redHomeCircles) {
            c.setStroke(Color.GRAY);
            c.setStrokeWidth(2);
        }
        for (Circle c : blueHomeCircles) {
            c.setStroke(Color.GRAY);
            c.setStrokeWidth(2);
        }
        for (Circle c : pieceCircles.values()) {
            c.setStroke(Color.GRAY);
            c.setStrokeWidth(2);
        }

        for (Piece p : state.pieces.values()) {
            if (p.color == playerColor && canMovePiece(p)) {
                hasMove = true;
                Circle c = getCircleForPiece(p.id);
                if (c != null) {
                    c.setStroke(Color.YELLOW);
                    c.setStrokeWidth(4);
                }
            }
        }

        if (!hasMove) {
            System.out.println("No valid moves for " + (playerColor == 0 ? "RED" : "BLUE"));
            updateStatus("No valid moves! Passing turn...");

            if (!isSinglePlayer && gameService != null) {
                try {
                    gameService.sendMove(playerColor, "MOVE:-1:0");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            if (isSinglePlayer) {
                state.setCurrentPlayer(1 - playerColor);
                playerColor = 1 - playerColor;
                String colorName = playerColor == 0 ? "RED" : "BLUE";
                updateStatus(colorName + "'s turn. Roll the dice!");
                rollButton.setDisable(false);
            } else {
                state.setCurrentPlayer(1 - playerColor);
                rollButton.setDisable(true);
                updateStatus("Opponent's turn. Waiting...");
            }

            diceValue = 0;
            diceLabel.setText("Dice: -");
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
        piece.setStroke(Color.GRAY);
        piece.setStrokeWidth(2);
        piece.setUserData(p.id);
        piece.setOnMouseClicked(this::onPieceClick);
        piece.setLayoutX(25);
        piece.setLayoutY(25);
        return piece;
    }

    private Pane getRedFinishCell(int index) {
        switch (index) {
            case 0:
                return redFinish0;
            case 1:
                return redFinish1;
            case 2:
                return redFinish2;
            case 3:
                return redFinish3;
            default:
                return null;
        }
    }

    private Pane getBlueFinishCell(int index) {
        switch (index) {
            case 0:
                return blueFinish0;
            case 1:
                return blueFinish1;
            case 2:
                return blueFinish2;
            case 3:
                return blueFinish3;
            default:
                return null;
        }
    }

    private boolean checkWin(int color) {
        int count = 0;

        for (Piece p : state.pieces.values()) {
            if (p.color == color && p.position >= HOME_STRETCH_ENTRY && p.position <= FINISH_POSITION) {
                count++;
            }
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

        boolean success = GameUtils.saveGame(gameState);
        if (success) {
            DialogUtils.showDialog("Save Successful", "Game saved successfully!", Alert.AlertType.INFORMATION);
        } else {
            DialogUtils.showDialog("Save Failed", "Failed to save game!", Alert.AlertType.ERROR);
        }
    }

    public void onLoadClick() {
        GameState lastGameState = GameUtils.loadGame();
        if (lastGameState != null) {

            state.pieces.putAll(lastGameState.pieces);
            state.setLastDiceValue(lastGameState.getLastDiceValue());
            state.setCurrentPlayer(lastGameState.getCurrentPlayer());
            playerColor = lastGameState.getCurrentPlayer();
            updateBoard();
            DialogUtils.showDialog("Load Successful", "Game loaded successfully!", Alert.AlertType.INFORMATION);
        } else {
            DialogUtils.showDialog("Load Failed", "No saved game found!", Alert.AlertType.ERROR);
        }
    }

    public void generateDocumentation() {
        try {
            DocumentationUtils.generateHtmlDocumentationFile();
            DialogUtils.showDialog("Uspješno generirana dokumentacija!",
                    "HTML dokumentacija za aplikaciju je uspješno generirana!",
                    Alert.AlertType.INFORMATION);
        } catch (IOException e) {
            String message = "Došlo je do pogreške kod generiranja HTML dokumentacije!";
            DialogUtils.showDialog("Pogreška!",
                    message,
                    Alert.AlertType.ERROR);
        }
    }
}