package org.example;

import java.io.*;
import java.net.*;

public class ClientHandler extends Thread {
    private final Socket clientSocket;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    public void run() {
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());

            // Read the username from the client
            String username = (String) inputStream.readObject();

            Server.addClient(username, outputStream);

            String message;
            do {
                // Read incoming messages from the client
                message = (String) inputStream.readObject();
                if (!message.equals("exit")) {
                    // Parse the message to extract the receiver's username
                    String[] parts = message.split(":");
                    String receiver = parts[0].trim();
                    String text = parts[1].trim();

                    // Broadcast the message to the receiver
                    Server.broadcastMessage(username, receiver, text);
                }
            } while (!message.equals("exit"));

            Server.removeClient(username);
            clientSocket.close();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
