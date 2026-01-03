package com.projectkamberinesludogame.ludogame.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GameRemoteService extends Remote {
    String REMOTE_OBJECT_NAME = "com.projectkamberinesludogame.ludogame.rmi.gameservice";

    void registerPlayer(int playerColor, GameCallback callback) throws RemoteException;
    void sendMove(int playerColor, String moveData) throws RemoteException;
   // boolean isOpponentReady(int playerColor) throws RemoteException;
  //  void sendGameState(String serializedGameState) throws RemoteException;

    interface GameCallback extends Remote {
        void onMoveReceived(String moveData) throws RemoteException;
        void onGameStart() throws RemoteException;
        //void onGameStateReceived(String serializedGameState) throws RemoteException;
    }
}