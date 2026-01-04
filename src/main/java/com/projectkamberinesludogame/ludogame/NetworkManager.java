package com.projectkamberinesludogame.ludogame;

import java.io.*;
        import java.net.*;
        import java.util.function.Consumer;

public class NetworkManager implements AutoCloseable{
    private boolean isHost;
    private int port;
    private Socket socket;
    private ServerSocket serverSocket;
    private BufferedReader in;
    private PrintWriter out;
    private Consumer<String> messageHandler;
    private Thread connectionThread;

    public NetworkManager(boolean isHost, int port, Consumer<String> messageHandler) {
        this.isHost = isHost;
        this.port = port;
        this.messageHandler = messageHandler;
    }

    public void start() {
        connectionThread = new Thread(() -> {
            try {
                if (isHost) {
                    startServer();
                } else {
                    connectToServer();
                }
                messageHandler.accept("CONNECTED");
                listenForMessages();
            } catch (IOException e) {
                System.err.println("Network error: " + e.getMessage());
                e.printStackTrace();
            }
        });
        connectionThread.setDaemon(true);
        connectionThread.start();
    }

    private void startServer() throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Server started on port " + port);
        socket = serverSocket.accept();
        System.out.println("Client connected");
        setupStreams();
    }

    private void connectToServer() throws IOException {
        for (int i = 0; i < 10; i++) {
            try {
                socket = new Socket("localhost", port);
                System.out.println("Connected to server");
                setupStreams();
                return;
            } catch (ConnectException e) {
                System.out.println("Attempt " + (i+1) + " failed, retrying...");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        throw new IOException("Could not connect to server after 10 attempts");
    }

    private void setupStreams() throws IOException {
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    private void listenForMessages() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                System.out.println("Received: " + message);
                messageHandler.accept(message);
            }
        } catch (IOException e) {
            System.err.println("Connection lost: " + e.getMessage());
        }
    }

    public void send(String message) {
        if (out != null) {
            System.out.println("Sending: " + message);
            out.println(message);
        }
    }

    public void close() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
            if (serverSocket != null) serverSocket.close();
        } catch (IOException e) {
            System.err.println("Error closing connections: " + e.getMessage());
        }
    }
}