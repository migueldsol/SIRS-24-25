package main.java.pt.ulisboa.tecnico.meic.sirs.t27;

import java.io.IOException;
import java.util.List;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.json.JSONObject;

import main.java.pt.ulisboa.tecnico.meic.sirs.t27.https.communication.Message;
import main.java.pt.ulisboa.tecnico.meic.sirs.t27.https.communication.Message.MessageType;

public class DB {

    public static void startDB(String host, int port) throws IOException {
        SocketFactory factory = SSLSocketFactory.getDefault();

        try (SSLSocket socket = (SSLSocket) factory.createSocket(host, port)) {

            socket.setEnabledCipherSuites(new String[] { "TLS_AES_128_GCM_SHA256" });
            socket.setEnabledProtocols(new String[] { "TLSv1.3" });
            // Process requests as long as the server connection is active
            while (true) {
                // Receive request from the server
                Message clientMessage = Message.receiveMessage(socket);
                System.out.println("Received from Server:\n" + clientMessage.toString() + "\n");

                // Handle the received request
                String databaseResponse = handleRequest(clientMessage);

                // Send the response to the server
                System.out.println("Sent to Server:\n" + databaseResponse.toString() + "\n");
                clientMessage.setContent(databaseResponse);
                Message.sendMessage(socket, clientMessage);
            }
        } catch (IOException e) {
            System.err.println("Error handling database socket: " + e.getMessage() + "\n");
            e.printStackTrace();
        }
    }

    private static String handleRequest(Message request) {
        MessageType request_type = request.getType();

        /* --------------------- Authentication Commands ----------------------- */

        if (request_type == MessageType.AUTHENTICATION) {
            String clienType = request.getClienType();
            switch (clienType) {
                case "Mechanic":
                    return DatabaseOperations.authenticateMechanic(request.getuserId(), request.getPassword());

                case "User":
                    return DatabaseOperations.authenticateCarOwner(request.getuserId(), request.getPassword());

                case "Manufacturer":
                    return DatabaseOperations.authenticateManufcaturer(request.getuserId(), request.getPassword());

                default:
                    return "Unknown User";
            }
        }

        String user_id = request.getuserId();
        String car_id = request.getcarId();

        /* --------------------- Owner Commands ----------------------- */
        if (request_type == MessageType.OWN_TURN_CAR_ON_OFF) {
            Boolean car_state = DatabaseOperations.getCarOnOff(car_id);

            DatabaseOperations.setCarOnOff(car_id, !car_state);

            if (car_state) {
                return "Car successfully turned OFF.";
            }

            return "Car successfully turned ON.";
        }

        if (request_type == MessageType.OWN_CHECK_BATTERY_LEVEL) {
            int battery_level = DatabaseOperations.getCarBatteryLevel(car_id);

            return "Car battery level: " + battery_level + "%.";
        }

        if (request_type == MessageType.OWN_MAINTENENCE_MODE) {
            Boolean maintence_mode = DatabaseOperations.getMaintenanceMode(car_id);

            DatabaseOperations.setMaintenanceMode(car_id, !maintence_mode);

            if (maintence_mode) {
                return "Maintenence mode successfully turned OFF.";
            }

            return "Maintenence mode successfully turned ON.";
        }

        if (request_type == MessageType.OWN_CHECK_CONFIGURATIONS) {
            String config = DatabaseOperations.getCarConfigurations(car_id);

            return config;
        }

        if (request_type == MessageType.OWN_CHECK_TESTS) {
            List<String> tests = DatabaseOperations.getTests(car_id);

            StringBuilder result = new StringBuilder("Tests performed:\n");
            for (String test : tests) {
                result.append(test).append("\n");
            }

            return result.toString();
        }

        if (request_type == MessageType.OWN_CHANGE_CONFIG) {
            String new_config = request.getContent();

            DatabaseOperations.setCarConfigurations(car_id, user_id, new_config);

            return "Configuration changed successfully.";
        }

        /* --------------------- Manufacturer Commands ----------------------- */
        if (request_type == MessageType.MAN_UPDATE_FIRMWARE) {
            String firmware_version = request.getContent();

            DatabaseOperations.setFirmwareUpdate(car_id, firmware_version, user_id, false);

            return "Firmware updated successfully.\nNew version: " + firmware_version;
        }

        /* --------------------- Mechanic Commands ----------------------- */
        if (request_type == MessageType.MEC_CHECK_CONFIGURATIONS) {
            Boolean maintenanceMode = DatabaseOperations.getMaintenanceMode(car_id);

            if (maintenanceMode) {
                String defaultConfigDetails = DatabaseOperations.getDefaultCarConfigurations(car_id);
                return defaultConfigDetails;
            } else {
                String configDetails = DatabaseOperations.getCarConfigurations(car_id);
                return configDetails;
            }
        }

        if (request_type == MessageType.MEC_GET_AVAILABLE_FIRMWARE_VERSIONS) {
            List<String> firmwareVersions = DatabaseOperations.getFirmwareUpdates(car_id);
            if (firmwareVersions.isEmpty()) {
                return "Error: No available firmware versions found for car.";
            }
            return String.join(" ", firmwareVersions);
        }

        if (request_type == MessageType.MEC_UPDATE_FIRMWARE) {
            String firmwareVersion = request.getContent();
            JSONObject jsonObject = new JSONObject(request.getContent());

            List<String> firmwareVersions = DatabaseOperations.getFirmwareUpdates(car_id);
            for (String jsonString : firmwareVersions) {
                JSONObject tempJson = new JSONObject(jsonString);
                if (tempJson.getString("firmwareVersion").equalsIgnoreCase(jsonObject.getString("firmwareVersion"))) {
                    DatabaseOperations.setFirmwareUpdate(car_id, firmwareVersion, user_id, true);
                    return "Firmware update installed successfully.\nNew installed version: "
                            + jsonObject.getString("firmwareVersion") + ".";

                }
                // TODO: signature
                return "Error: Firmware version not available for car.";
            }
        }

        if (request_type == MessageType.MEC_PERFORM_TESTS) {
            String tests = request.getContent();
            // TODO: signature
            DatabaseOperations.setTest(car_id, user_id, tests);

            return "Tests performed successfully.";
        }

        // If the command is not recognized, return an error
        return "Error: Unknown command.";
    }

    public static void main(String args[]) throws IOException {
        String path = System.getProperty("user.dir") + "/../KeysAndCrt/";
        System.setProperty("javax.net.ssl.keyStore", path + "DB/db.p12");
        System.setProperty("javax.net.ssl.keyStorePassword", "Y9Aqz2DPGRSi0q1yEuxs");
        System.setProperty("javax.net.ssl.trustStore", path + "DB/dbtruststore.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "DBSIRS2425");
        startDB("192.168.1.20", 5000);
    }
}
