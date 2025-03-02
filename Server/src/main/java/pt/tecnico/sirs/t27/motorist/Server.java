package main.java.pt.tecnico.sirs.t27.motorist;

import java.io.*;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import main.java.pt.ulisboa.tecnico.meic.sirs.t27.https.communication.Message;
import main.java.pt.ulisboa.tecnico.meic.sirs.t27.https.communication.Message.MessageType;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLServerSocketFactory;

public class Server {

    public static void startServer(int port) throws IOException {
        ServerSocketFactory factory = SSLServerSocketFactory.getDefault();

        // Set up server socket
        try (SSLServerSocket serverSocket = (SSLServerSocket) factory.createServerSocket(port)) {
            serverSocket.setNeedClientAuth(true);
            serverSocket.setEnabledCipherSuites(new String[] { "TLS_AES_128_GCM_SHA256" });
            serverSocket.setEnabledProtocols(new String[] { "TLSv1.3" });

            System.out.println("Waiting for connections...");

            RoleBasedSocket clientSocket = new RoleBasedSocket(null, null);
            RoleBasedSocket databaseSocket = new RoleBasedSocket(null, null);
            ;

            while (true) {
                SSLSocket socket = (SSLSocket) serverSocket.accept();
                String role = identifyConnectionRole(socket);

                if ((role.equals("user") || role.equals("manufacturer") || role.equals("mechanic"))
                        && databaseSocket.getSocket() != null
                        && clientSocket.getSocket() == null) {
                    clientSocket.setSocket(socket);
                    clientSocket.setRole(role);
                    // Message message = new Message(Message.MessageType.PLAIN_TEXT,
                    // "welcome " + role + " please send your userId and password");
                    // Message.sendMessage(clientSocket.getSocket(), message);
                    System.out.println("Waiting for authentication");
                    Message authentication = Message.receiveMessage(socket);
                    Message response = Authenticate.authenticate(databaseSocket.getSocket(), authentication);
                    if (response.getContent().equalsIgnoreCase("OK")) {
                        Message.sendMessage(clientSocket.getSocket(), response);
                        System.out.println(role + " Authenticated");
                    } else {
                        System.out.println("userId or Password incorrect");
                        clientSocket.close();
                    }
                } else if (role.equals("database") && databaseSocket.getSocket() == null) {
                    databaseSocket.setSocket(socket);
                    databaseSocket.setRole(role);
                    System.out.println("Database connected.");
                } else {
                    System.out.println("Unknown role or duplicate connection; closing socket.");
                    socket.close();
                }

                // Process requests once both connections are established
                try {
                    if (clientSocket.getSocket() != null && databaseSocket.getSocket() != null) {
                        processRequests(clientSocket, databaseSocket);
                    }
                } catch (NullPointerException e) {

                }

            }
        }
    }

    private static String identifyConnectionRole(SSLSocket socket) {
        try {
            // Retrieve peer certificates
            Certificate[] certs = socket.getSession().getPeerCertificates();
            System.out.println("Peer certs: " + certs);
            if (certs.length > 0 && certs[0] instanceof X509Certificate) {
                X509Certificate x509Cert = (X509Certificate) certs[0];

                // Extract information from the certificate's Subject
                String subjectDN = x509Cert.getSubjectX500Principal().getName();
                System.out.println("Connected entity's certificate subject: " + subjectDN);

                // Logic to determine role based on certificate's subject
                if (subjectDN.contains("CN=USER")) {
                    return "user";
                } else if (subjectDN.contains("CN=DB")) {
                    return "database";
                } else if (subjectDN.contains("CN=MANUFACTURER")) {
                    return "manufacturer";
                } else if (subjectDN.contains("CN=MECHANIC")) {
                    return "mechanic";
                }
            }
        } catch (Exception e) {
            System.out.println("Error identifying connection role: " + e.getMessage());
        }
        return "unknown";
    }

    private static void processRequests(RoleBasedSocket clientSocket, RoleBasedSocket databaseSocket)
            throws IOException {
        while (true) {
            // Read client request
            Message clientMessage = Message.receiveMessage(clientSocket.getSocket());
            String request = clientMessage.getContent();
            System.out.println("received the following message: \n" + clientMessage.toString() + "\n");

            if (request != null && request.equalsIgnoreCase("Exit")) {
                System.out.println("Preparing to close connection...");
                Message message = new Message(Message.MessageType.PLAIN_TEXT, null, "Closing connection");
                Message.sendMessage(clientSocket.getSocket(), message);
                clientSocket.close();
                System.out.println("Closing " + clientSocket.getRole() + " connection");
                break;
            }

            // handle and forward request to the database
            Message.sendMessage(databaseSocket.getSocket(), clientMessage);
            System.out.println("Sent to database:\n" + clientMessage.toString() + "\n");
            Message databaseResponse = Message.receiveMessage(databaseSocket.getSocket());
            System.out.println("Received from database and sent to client:\n" + databaseResponse.toString() + "\n");
            // Send the database response or error back to the client
            Message.sendMessage(clientSocket.getSocket(), databaseResponse);
        }
    }

    public static void main(String[] args) throws IOException {
        String path = System.getProperty("user.dir") + "/../KeysAndCrt/Server/";

        System.setProperty("javax.net.ssl.keyStore",
                path + "server.p12");
        System.setProperty("javax.net.ssl.keyStorePassword", "4hJeQcAmJntQ6iiZOS9S");
        System.setProperty("javax.net.ssl.trustStore",
                path + "servertruststore.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "SERVERSIRS2425");

        // Listen on a single port for both client and database connections
        int port = 5000; // Example port
        startServer(port);
    }
}
