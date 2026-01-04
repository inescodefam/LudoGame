//package com.projectkamberinesludogame.ludogame.rmi;
//
//import java.rmi.RemoteException;
//import java.util.HashMap;
//import java.util.Map;
//
//public class GameRemoteServiceImpl implements GameRemoteService {
//
//    private Map<Integer, GameCallback> players = new HashMap<>();
//
//    @Override
//    public synchronized void registerPlayer(int playerColor, GameCallback callback) throws RemoteException {
//        players.put(playerColor, callback);
//        System.out.println("Player " + (playerColor == 0 ? "RED" : "BLUE") + " registered");
//
//        if (players.size() == 2) {
//            System.out.println("Both players connected! Starting game...");
//            for (GameCallback cb : players.values()) {
//                try {
//                    cb.onGameStart();
//                } catch (RemoteException e) {
//                    System.err.println("Failed to notify player: " + e.getMessage());
//                }
//            }
//        } else {
//            System.out.println("Waiting for second player...");
//        }
//    }
//
//    @Override
//    public synchronized void sendMove(int playerColor, String moveData) throws RemoteException {
//        System.out.println("Move from " + (playerColor == 0 ? "RED" : "BLUE") + ": " + moveData);
//
//
//        int opponentColor = 1 - playerColor;
//        GameCallback opponentCallback = players.get(opponentColor);
//
//        if (opponentCallback != null) {
//            try {
//                opponentCallback.onMoveReceived(moveData);
//                System.out.println("Move forwarded to " + (opponentColor == 0 ? "RED" : "BLUE"));
//            } catch (RemoteException e) {
//                System.err.println("Failed to send move to opponent: " + e.getMessage());
//                players.remove(opponentColor);
//            }
//        } else {
//            System.err.println("Opponent not found!");
//        }
//    }
//}