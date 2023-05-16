package org.example;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.*;
import java.net.*;

//uat test commit

public class Client extends JFrame {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int PORT = 8080;

    private final JTextArea messageArea;
    private final JTextField receiverField;
    private final JTextField messageField;

    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;

    public Client() {
        super("YouChat");

        // Set up the GUI components
        messageArea = new JTextArea(10, 30);
        messageArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(messageArea);

        receiverField = new JTextField(10);
        messageField = new JTextField(20);
        JButton sendButton = new JButton("Send");

        // Create a main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Add some padding

        // Create a panel for the input fields
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(new EmptyBorder(0, 0, 10, 0)); // Add bottom margin

        // Create a panel for the labels and fields
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setOpaque(false); // Make the panel transparent

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 5, 5); // Add some spacing

        fieldsPanel.add(new JLabel("Receiver:"), gbc);
        gbc.gridx++;
        fieldsPanel.add(receiverField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        fieldsPanel.add(new JLabel("Message:"), gbc);
        gbc.gridx++;
        fieldsPanel.add(messageField, gbc);

        // Add the labels and fields panel to the input panel
        inputPanel.add(fieldsPanel, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        // Add the scroll pane and input panel to the main panel
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(inputPanel, BorderLayout.SOUTH);

        // Set up the layout
        setContentPane(mainPanel); // Use setContentPane() instead of setLayout()
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

            // Start a new thread to receive messages from the server
            new Thread(() -> {
                try {
                    String message;
                    while ((message = (String) inputStream.readObject()) != null) {
                        messageArea.append(message + "\n");
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage() {
        String receiver = receiverField.getText().trim();
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
