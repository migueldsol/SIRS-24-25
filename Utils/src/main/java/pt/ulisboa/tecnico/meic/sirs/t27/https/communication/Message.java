package main.java.pt.ulisboa.tecnico.meic.sirs.t27.https.communication;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;

import main.java.pt.ulisboa.tecnico.meic.sirs.t27.operations.*;

public class Message {
    public enum MessageType {
        PLAIN_TEXT_ENCRYPTED, PLAIN_TEXT, JSON_ENCRYPTED, JSON_DECRYPTED,
        AUTHENTICATION,
        OWN_TURN_CAR_ON_OFF, OWN_CHECK_BATTERY_LEVEL, OWN_CHECK_CONFIGURATIONS, OWN_MAINTENENCE_MODE, OWN_CHECK_TESTS,
        OWN_CHANGE_CONFIG,
        MAN_UPDATE_FIRMWARE,
        MEC_CHECK_CONFIGURATIONS, MEC_PERFORM_TESTS, MEC_GET_AVAILABLE_FIRMWARE_VERSIONS, MEC_UPDATE_FIRMWARE,
    }

    private MessageType type;
    private String content;
    private String userId;
    private String password;
    private String clienType;
    private String carId;

    // New constructor for authentication messages
    public Message(MessageType type, String userId, String password, String clienType, String content) {
        if (type != MessageType.AUTHENTICATION) {
            throw new IllegalArgumentException("This constructor is only for AUTHENTICATION messages");
        }
        this.type = type;
        this.userId = userId;
        this.password = password;
        this.clienType = clienType;
        this.content = content;
    }

    // New constructor for client operation messages with one parameter
    public Message(MessageType type, String userId, String carId) {
        if (type == MessageType.AUTHENTICATION || type == MessageType.PLAIN_TEXT_ENCRYPTED
                || type == MessageType.JSON_ENCRYPTED || type == MessageType.JSON_DECRYPTED) {
            throw new IllegalArgumentException("This constructor is only for client operation messages");
        }
        this.type = type;
        this.userId = userId;
        this.carId = carId;
    }

    // New constructor for client operation messages with more than one parameter
    public Message(MessageType type, String userId, String carId, String content) {
        if (type == MessageType.AUTHENTICATION || type == MessageType.PLAIN_TEXT_ENCRYPTED
                || type == MessageType.JSON_ENCRYPTED || type == MessageType.JSON_DECRYPTED) {
            throw new IllegalArgumentException("This constructor is only for client operation messages");
        }
        this.type = type;
        this.userId = userId;
        this.carId = carId;
        this.content = content;
    }

    // Getter for type
    public MessageType getType() {
        return type;
    }

    // Getter for userId
    public String getuserId() {
        return userId;
    }

    // Getter for carId
    public String getcarId() {
        return carId;
    }

    // Getter for password
    public String getPassword() {
        return password;
    }

    public String getContent() {
        return content;
    }

    public String getClienType() {
        return clienType;
    }

    public void setContent(String content) {
        this.content = content;
    }

    // Serialize the message into a byte array
    public byte[] toByteArray() {
        String messageString;
        if (type == MessageType.AUTHENTICATION) {
            messageString = type.name() + "::" + userId + "::" + password + "::" + clienType + "::" + content;
            // Format: AUTHENTICATION::userId::PASSWORD::clienType
        } else if (type == MessageType.PLAIN_TEXT ||
                type == MessageType.OWN_CHANGE_CONFIG ||
                type == MessageType.OWN_TURN_CAR_ON_OFF ||
                type == MessageType.OWN_CHECK_BATTERY_LEVEL ||
                type == MessageType.OWN_MAINTENENCE_MODE ||
                type == MessageType.OWN_CHECK_TESTS ||
                type == MessageType.OWN_CHECK_CONFIGURATIONS ||
                type == MessageType.MAN_UPDATE_FIRMWARE ||
                type == MessageType.MEC_GET_AVAILABLE_FIRMWARE_VERSIONS ||
                type == MessageType.MEC_UPDATE_FIRMWARE ||
                type == MessageType.MEC_CHECK_CONFIGURATIONS ||
                type == MessageType.MEC_PERFORM_TESTS) {
            messageString = type.name() + "::" + userId + "::" + carId + "::" + content;
            // Format: TYPE::userId::carId::content
        } else {
            messageString = type.name() + "::" + userId + "::" + content;
            // Format: TYPE::userId::CONTENT
        }
        return messageString.getBytes(StandardCharsets.UTF_8);
    }

    // Static method to deserialize a message from a byte array
    public static Message fromByteArray(byte[] data, int length, String p12FilePath, String p12Password)
            throws IOException {
        String received = new String(data, 0, length, StandardCharsets.UTF_8);
        if (!received.contains("::")) {
            throw new IOException("Invalid message format");
        }

        String[] parts = received.split("::", 5); // Split into type and content
        MessageType type = MessageType.valueOf(parts[0]);

        if (type == MessageType.AUTHENTICATION) {
            if (parts.length < 5) {
                throw new IOException("Invalid AUTHENTICATION message format");
            }
            String userId = parts[1];
            String password = parts[2];
            String clienType = parts[3];
            String content = parts[4];

            return new Message(type, userId, password, clienType, content);
        } else if (type == MessageType.PLAIN_TEXT ||
                type == MessageType.OWN_TURN_CAR_ON_OFF ||
                type == MessageType.OWN_CHANGE_CONFIG ||
                type == MessageType.OWN_CHECK_BATTERY_LEVEL ||
                type == MessageType.OWN_MAINTENENCE_MODE ||
                type == MessageType.OWN_CHECK_TESTS ||
                type == MessageType.OWN_CHECK_CONFIGURATIONS ||
                type == MessageType.MAN_UPDATE_FIRMWARE ||
                type == MessageType.MEC_GET_AVAILABLE_FIRMWARE_VERSIONS ||
                type == MessageType.MEC_UPDATE_FIRMWARE ||
                type == MessageType.MEC_CHECK_CONFIGURATIONS ||
                type == MessageType.MEC_PERFORM_TESTS) {
            if (parts.length < 2) {
                throw new IOException("Invalid message type");
            }
            String userId = parts[1];
            String carId = parts[2];
            String content = parts[3];
            return new Message(type, userId, carId, content);
        }

        String userId = parts[1];
        String content = parts[2];

        if (type == MessageType.JSON_ENCRYPTED) {
            if (p12FilePath == null || p12Password == null) {
                throw new IllegalArgumentException("Decryption requires privateKeyPath and outputDir");
            }
            try {
                JSONObject decrypted = Unprotect.unprotectAsymmetricOnly(content, p12FilePath, p12Password);
                content = decrypted.toString();
            } catch (Exception e) {
                System.out.println(e);
            }
        }

        return new Message(type, userId, content);
    }

    // Static method to decrypt JSON_ENCRYPTED_CONFIG content
    public void decryptJSONConfig(String encryptedContent, String symmetricKeyPath, String privateKeyPath,
            String privateKeyPassword, String publicKeyPath) throws IOException {
        try {
            JSONObject unprotectedJson = Unprotect.unprotectSymmetricOnly(new JSONObject(this.content),
                    symmetricKeyPath, privateKeyPath,
                    privateKeyPassword, publicKeyPath);
            this.content = unprotectedJson.toString();
        } catch (Exception e) {
            throw new IOException("Failed to decrypt the message: " + e.getMessage(), e);
        }
    }

    // Static method to encrypt JSON content
    public void encryptJSONConfig(String content, String symmetricKeyPath, String privateKeyPath,
            String privateKeyPassword, String publicKeyPath) throws IOException {
        try {
            JSONObject protectedJson = Protect.protectWithSymmetricOnly(new JSONObject(this.content), symmetricKeyPath,
                    privateKeyPath, privateKeyPassword, publicKeyPath);
            this.content = protectedJson.toString();
        } catch (Exception e) {
            throw new IOException("Failed to encrypt the message: " + e.getMessage(), e);
        }
    }

    // Static method to send a message using a socket
    public static void sendMessage(Socket socket, Message message) throws IOException {
        OutputStream os = socket.getOutputStream();
        byte[] data = message.toByteArray();
        os.write(data);
        os.flush();
    }

    // Static method to receive a message from a socket without decryption
    // parameters
    public static Message receiveMessage(Socket socket) throws IOException {
        return receiveMessage(socket, null, null);
    }

    // Static method to receive a message using a socket
    public static Message receiveMessage(Socket socket, String p12Path, String p12Password) throws IOException {
        InputStream is = socket.getInputStream();
        byte[] buffer = new byte[2048];
        int len = is.read(buffer);
        if (len == -1) {
            throw new IOException("Connection closed by peer");
        }

        return fromByteArray(buffer, len, p12Path, p12Password);
    }

    @Override
    public String toString() {
        return "Message{" +
                "type=" + type +
                ", content='" + content + '\'' +
                '}';
    }
}
