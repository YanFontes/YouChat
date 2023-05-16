package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class Client extends JFrame {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int PORT = 8080;

    private final JTextArea messageArea;
    private final JComboBox<String> receiverComboBox;
    private final JTextField messageField;

    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private List<String> activeUsers;

    public Client() {
        super("YouChat");

        // Set up the GUI components
        messageArea = new JTextArea(10, 30);
        messageArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(messageArea);

        receiverComboBox = new JComboBox<>();
        messageField = new JTextField(20);
        JButton sendButton = new JButton("Send");

        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Receiver:"));
        inputPanel.add(receiverComboBox);
        inputPanel.add(new JLabel("Message:"));
        inputPanel.add(messageField);
        inputPanel.add(sendButton);

        // Set up the layout
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);
        pack();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Set up action listeners
        sendButton.addActionListener(e -> sendMessage());

        messageField.addActionListener(e -> sendMessage());
    }

    public void connectToServer() {
        try {
            Socket socket = new Socket(SERVER_ADDRESS, PORT);
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());

            // Read the username from the user
            String username = JOptionPane.showInputDialog(this, "Enter your username:");
            outputStream.writeObject(username);

            activeUsers = new ArrayList<>();

            new Thread(() -> {
                try {
                    String message;
                    while ((message = (String) inputStream.readObject()) != null) {
                        if (message.startsWith("ACTIVE_USERS:")) {
                            updateActiveUsers(message.substring(13));
                        } else {
                            messageArea.append(message + "\n");
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateActiveUsers(String userList) {
        String[] users = userList.split(",");
        activeUsers.clear();
        receiverComboBox.removeAllItems();
        for (String user : users) {
            activeUsers.add(user);
            receiverComboBox.addItem(user);
        }
    }

    public void sendMessage() {
        String receiver = (String) receiverComboBox.getSelectedItem();
        String text = messageField.getText().trim();

        if (!receiver.isEmpty() && !text.isEmpty()) {
            String message = receiver + ": " + text;
            try {
                outputStream.writeObject(message);
                messageField.setText("");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Client client = new Client();
            client.setVisible(true);
            client.connectToServer();
        });
    }
}
