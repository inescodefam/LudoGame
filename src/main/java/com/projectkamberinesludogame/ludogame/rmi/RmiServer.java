//package com.projectkamberinesludogame.ludogame.rmi;
//
//import com.projectkamberinesludogame.ludogame.jndi.ConfigurationKey;
//import com.projectkamberinesludogame.ludogame.jndi.ConfigurationReader;
//
//import java.rmi.RemoteException;
//import java.rmi.registry.LocateRegistry;
//import java.rmi.registry.Registry;
//import java.rmi.server.UnicastRemoteObject;
//
//public class RmiServer {
//
//    private static final int RANDOM_PORT_HINT = 0;
//    //public static final int RMI_PORT = ConfigurationReader.getIntegerForKey(ConfigurationKey.RMI_PORT);
//    public static final int RMI_PORT = 1099;
//    public static final String HOSTNAME =  ConfigurationReader.getStringForKey(ConfigurationKey.HOSTNAME);
//
//
//    public static void main(String[] args) {
//        try {
//            Registry registry = LocateRegistry.createRegistry(RMI_PORT);
//            ChatRemoteService chatRemoteService = new ChatRemoteServiceImpl();
//            ChatRemoteService skeleton = (ChatRemoteService) UnicastRemoteObject.exportObject(chatRemoteService,
//                    RANDOM_PORT_HINT);
//            registry.rebind(ChatRemoteService.REMOTE_OBJECT_NAME, skeleton);
//            System.err.println("Object registered in RMI registry");
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
//
//    }
//}

package com.projectkamberinesludogame.ludogame.rmi;

import com.projectkamberinesludogame.ludogame.jndi.ConfigurationKey;
import com.projectkamberinesludogame.ludogame.jndi.ConfigurationReader;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;


public class RmiServer {
    private static final int RANDOM_PORT_HINT = 0;
    public static final int RMI_PORT = ConfigurationReader.getIntegerForKey(ConfigurationKey.RMI_PORT);
    public static final String HOSTNAME = ConfigurationReader.getStringForKey(ConfigurationKey.HOSTNAME);

    public static void main(String[] args) {
        try {

            String hostname = "localhost";
            try {
                hostname = ConfigurationReader.getStringForKey(ConfigurationKey.HOSTNAME);
            } catch (Exception e) {
                System.out.println("Could not read hostname from config, using localhost");
            }


            System.setProperty("java.rmi.server.hostname", hostname);

            System.out.println("Starting RMI Server on " + hostname + ":" + RMI_PORT);


            Registry registry = LocateRegistry.createRegistry(RMI_PORT);

            ChatRemoteService chatRemoteService = new ChatRemoteServiceImpl();
            ChatRemoteService chatSkeleton = (ChatRemoteService) UnicastRemoteObject.exportObject(
                    chatRemoteService, RANDOM_PORT_HINT);
            registry.rebind(ChatRemoteService.REMOTE_OBJECT_NAME, chatSkeleton);
            System.out.println("✓ Chat service registered");

            GameRemoteService gameRemoteService = new GameRemoteServiceImpl();
            GameRemoteService gameSkeleton = (GameRemoteService) UnicastRemoteObject.exportObject(
                    gameRemoteService, RANDOM_PORT_HINT);
            registry.rebind(GameRemoteService.REMOTE_OBJECT_NAME, gameSkeleton);
            System.out.println("✓ Game service registered");

            System.out.println("\n========================================");
            System.out.println("RMI Server is running!");
            System.out.println("Waiting for players to connect...");
            System.out.println("========================================\n");

        } catch (RemoteException e) {
            System.err.println("Failed to start RMI Server!");
            e.printStackTrace();
        }
    }
}