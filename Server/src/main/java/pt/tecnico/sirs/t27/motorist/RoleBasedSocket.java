package main.java.pt.tecnico.sirs.t27.motorist;

import java.io.*;
import java.net.Socket;

import main.java.pt.ulisboa.tecnico.meic.sirs.t27.https.communication.Message;
import main.java.pt.ulisboa.tecnico.meic.sirs.t27.https.communication.Message.MessageType;

public class RoleBasedSocket {
    private Socket socket;
    private String role;
    private String id;

    // public Message handleRequest(Message request, Socket database) {
    // if(role.equalsIgnoreCase("user")){
    // if(request.getType().equals(Message.MessageType.OWN_TURN_CAR_ON_OFF)){

    // }

    // } else if(role.equalsIgnoreCase("Mechanic")){

    // }else if(role.equalsIgnoreCase("Manufacturer")){

    // }
    // }

    public RoleBasedSocket(Socket socket, String role) {
        this.socket = socket;
        this.role = role;
        this.id = null;
    }

    public String getId() {
        return this.id;
    }

    public void changeId(String id) {
        this.id = id;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // Getters
    public Socket getSocket() {
        return socket;
    }

    public String getRole() {
        return role;
    }

    // Check if socket is active
    public boolean isActive() {
        return !socket.isClosed() && socket.isConnected();
    }

    // Close the socket
    public void close() throws IOException {
        if (socket != null && !socket.isClosed()) {
            socket.close();
            this.socket = null;
        }
    }
}
