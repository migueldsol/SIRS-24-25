package main.java.pt.tecnico.sirs.t27.motorist;

import java.io.IOException;
import java.util.Scanner;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import main.java.pt.ulisboa.tecnico.meic.sirs.t27.https.communication.Message;
import main.java.pt.ulisboa.tecnico.meic.sirs.t27.https.communication.Message.MessageType;

public abstract class Client {

    private SSLSocket socket;
    private String clientId;
    private String password;

    public Client(String host, int port, Scanner scanner, String clienType, String password) {
        this.password = password;
        try {
            connectToServer(host, port);
            if (authenticate(scanner, clienType)) {
                System.out.println("Successfully Authenticated\n");
                handleCommunications(scanner);
            } else {
                System.out.println("Wrong UserId or Password retry connection\n");
            }
            closeConnection();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public SSLSocket getSocket() {
        return this.socket;
    }

    public String getClientId() {
        return this.clientId;
    }

    public abstract void cli();

    private void connectToServer(String host, int port) throws IOException {
        SocketFactory factory = SSLSocketFactory.getDefault();
        this.socket = (SSLSocket) factory.createSocket(host, port);

        // Configure TLS settings
        socket.setEnabledCipherSuites(new String[] { "TLS_AES_128_GCM_SHA256" });
        socket.setEnabledProtocols(new String[] { "TLSv1.3" });
    }

    private Boolean authenticate(Scanner scanner, String clienType) {
        try {
            System.out.println("Please enter your id: ");
            String id = scanner.nextLine();
            Message request = new Message(MessageType.AUTHENTICATION, id, password, clienType, null);
            Message.sendMessage(socket, request);
            Message response = Message.receiveMessage(socket);
            if (response.getContent().equalsIgnoreCase("OK")) {
                this.clientId = id;
                return true;
            }
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
        return false;
    }

    // Handle communication with the server
    public abstract void handleCommunications(Scanner scanner) throws IOException;

    private void closeConnection() throws IOException {
        System.out.println("Closing connection..." + "\n");
        Message closing_message = new Message(MessageType.PLAIN_TEXT, this.clientId, null, "Exit");
        sendToServer(closing_message);
        receiveFromServer(false);
        if (socket != null && !socket.isClosed())
            socket.close();
        System.out.println("Connection closed." + "\n");
    }

    public boolean sendToServer(Message message) {
        try {
            System.out.println("Sending to server:\n" + message.toString() + "\n");
            Message.sendMessage(socket, message);
            return true;
        } catch (Exception e) {
            System.err.println("Error while communicating with the server: " + e.getMessage() + "\n");
            e.printStackTrace();
            return false;
        }
    }

    public Message receiveFromServer(Boolean receive_encrypted) {
        return receiveFromServer(receive_encrypted, null, null, null, null);
    }

    public Message receiveFromServer(Boolean receive_encrypted, String symmetricKeyPath,
            String privateKeyPath, String privateKeyPassword, String publicKeyPath) {
        Message receivedMessage = null; // Initialize the variable to ensure it's always returned
        try {
            if (receive_encrypted) {
                receivedMessage = Message.receiveMessage(socket);
                // Decrypt the received message using provided password and key path
                try {
                    receivedMessage.decryptJSONConfig(receivedMessage.getContent(), symmetricKeyPath, privateKeyPath,
                            privateKeyPassword, publicKeyPath);
                } catch (Exception decryptionException) {
                    // Handle decryption failure with a user-friendly message
                    System.err.println("Failed to access car configurations. Make sure you have the car key." + "\n");
                    throw decryptionException;
                }
            } else {
                receivedMessage = Message.receiveMessage(socket);
            }
        } catch (Exception e) {
            System.err.println("Error while communicating with the server: " + e.getMessage() + "\n");
            e.printStackTrace();
        }
        return receivedMessage;
    }
}
