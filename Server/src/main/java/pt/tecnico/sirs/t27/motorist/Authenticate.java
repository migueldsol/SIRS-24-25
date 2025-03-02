package main.java.pt.tecnico.sirs.t27.motorist;

import java.net.Socket;

import main.java.pt.ulisboa.tecnico.meic.sirs.t27.https.communication.Message;

public class Authenticate {
    public static Message authenticate(Socket database, Message authentication) {
        try {
            Message.sendMessage(database, authentication);
            Message response = Message.receiveMessage(database);
            return response;
        } catch (Exception e) {
            System.out.println(e);
        }

        return null;
    }
}
