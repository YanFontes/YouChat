package org.example;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

//GitHub test
public class Server {
    private static final int PORT = 8080;
    private static final Map<String, ObjectOutputStream> clients = new HashMap<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started. Listening on port: " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket);

                // Create a new thread to handle client communication
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clientHandler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static synchronized void broadcastMessage(String sender, String receiver, String message) {
        try {
            if (clients.containsKey(receiver)) {
                clients.get(receiver).writeObject(sender + ": " + message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static synchronized void addClient(String username, ObjectOutputStream outputStream) {
        clients.put(username, outputStream);
    }

    static synchronized void removeClient(String username) {
        clients.remove(username);
    }
}

