package main.java.pt.tecnico.sirs.t27.motorist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

import main.java.pt.ulisboa.tecnico.meic.sirs.t27.https.communication.Message;
import main.java.pt.ulisboa.tecnico.meic.sirs.t27.https.communication.Message.MessageType;
import main.java.pt.ulisboa.tecnico.meic.sirs.t27.operations.Sign;

public class User extends Client {

    public User(String host, Integer port, Scanner scanner, String clientType, String password) {
        super(host, port, scanner, clientType, password);
    }

    @Override
    public void cli() {
        System.out.println("\nWelcome User!");
        System.out.println("Please what you want to do:");
        System.out.println("1. Turn Car On/Off");
        System.out.println("2. Check Car Battery Level");
        System.out.println("3. Check Car Configuration");
        System.out.println("4. Change Car Configuration");
        System.out.println("5. Put/Remove Maintenence Mode");
        System.out.println("6. Check Performed Tests");
        System.out.println("7. Exit");
        System.out.print("Your choice: ");
    }

    @Override
    public void handleCommunications(Scanner scanner) {
        boolean exit = false;

        while (!exit) {
            cli();
            int choice;

            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }

            switch (choice) {
                case 1:
                    turnCarOnOff(scanner);
                    break;
                case 2:
                    checkCarBatteryLevel(scanner);
                    break;
                case 3:
                    checkCarConfig(scanner);
                    break;
                case 4:
                    changeCarConfig(scanner);
                    break;
                case 5:
                    maintenenceMode(scanner);
                    break;
                case 6:
                    checkCarTests(scanner);
                    break;
                case 7:
                    exit = true;
                    System.out.println("Exiting. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
                    break;
            }
        }
    }

    public void turnCarOnOff(Scanner scanner) {
        String userId = getClientId();
        System.out.print("\nEnter the car identifier (e.g., car123): ");
        String carId = scanner.nextLine();

        if (!carId.matches("car\\d+")) {
            System.out.println("Invalid car identifier format. Please use the format CARx.");
            return;
        }

        Message message = new Message(MessageType.OWN_TURN_CAR_ON_OFF, userId, carId);

        System.out.println("\nTurning car on/off...");

        if (sendToServer(message)) {
            Message server_response = receiveFromServer(false);
            System.out.println(server_response.getContent());
        } else {
            System.out.println("Failed to turn car on/off. Please try again.");
        }
    }

    public void checkCarBatteryLevel(Scanner scanner) {
        String userId = getClientId();
        System.out.print("\nEnter the car identifier (e.g., car123): ");
        String carId = scanner.nextLine();

        if (!carId.matches("car\\d+")) {
            System.out.println("Invalid car identifier format. Please use the format carx.");
            return;
        }

        Message message = new Message(MessageType.OWN_CHECK_BATTERY_LEVEL, userId, carId);

        System.out.println("\nChecking car battery level...");

        if (sendToServer(message)) {
            Message server_response = receiveFromServer(false);
            if (server_response.equals(null)) {
                System.out.println("Failed to check battery level. Please try again.");
            } else {
                System.out.println(server_response.getContent());
            }
        } else {
            System.out.println("Failed to check battery level. Please try again.");
        }
    }

    public String receiveConfig(String userId, String carId) {
        Message message = new Message(MessageType.OWN_CHECK_CONFIGURATIONS, userId, carId);

        System.out.println("\nChecking car configuration...");

        String symmetricKeyPath = System.getProperty("user.dir") + "/../KeysAndCrt/CarsKey/car1.key.enc";
        String privateKeyPath = System.getProperty("user.dir") + "/../KeysAndCrt/User/user.p12";
        String privateKeyPassword = this.getPassword();
        String publicKeyPath = System.getProperty("user.dir") + "/../KeysAndCrt/User/user_public_key.der";

        if (sendToServer(message)) {
            Message server_response = receiveFromServer(true, symmetricKeyPath, privateKeyPath, privateKeyPassword,
                    publicKeyPath);
            return server_response.getContent();
        }
        return "";
    }

    public void checkCarConfig(Scanner scanner) {
        String userId = getClientId();
        System.out.print("\nEnter the car identifier (e.g., car123): ");
        String carId = scanner.nextLine();

        if (!carId.matches("car\\d+")) {
            System.out.println("Invalid car identifier format. Please use the format carx.");
            return;
        }

        String current_config = receiveConfig(userId, carId);

        if (current_config.length() != 0) {
            ConfigRecord config = new ConfigRecord(current_config);
            System.out.println(config.toString());
        } else {
            System.out.println("Failed to receive current car configuration. Please try again.");
        }
    }

    public void maintenenceMode(Scanner scanner) {
        String userId = getClientId();
        System.out.print("\nEnter the car identifier (e.g., car123): ");
        String carId = scanner.nextLine();

        if (!carId.matches("car\\d+")) {
            System.out.println("Invalid car identifier format. Please use the format carx.");
            return;
        }

        Message message = new Message(MessageType.OWN_MAINTENENCE_MODE, userId, carId);

        System.out.println("\nTurning maintence mode on/off...");

        if (sendToServer(message)) {
            Message server_response = receiveFromServer(false);
            System.out.println(server_response.getContent());
        } else {
            System.out.println("Failed to turn maintence mode on/off. Please try again.");
        }
    }

    public void checkCarTests(Scanner scanner) {
        String userId = getClientId();
        System.out.print("\nEnter the car identifier (e.g., car123): ");
        String carId = scanner.nextLine();

        if (!carId.matches("car\\d+")) {
            System.out.println("Invalid car identifier format. Please use the format carx.");
            return;
        }

        Message message = new Message(MessageType.OWN_CHECK_TESTS, userId, carId);

        System.out.println("\nChecking tests performed to car...");

        if (sendToServer(message)) {
            Message server_response = receiveFromServer(false);
            System.out.println(server_response.getContent());

        } else {
            System.out.println("Failed to turn maintence mode on/off. Please try again.");
        }
    }

    public void changeCarConfig(Scanner scanner) {
        String userId = getClientId();
        System.out.print("\nEnter the car id (e.g., car123): ");
        String carId = scanner.nextLine();

        if (!carId.matches("car\\d+")) {
            System.out.println("Invalid car identifier format. Please use the format carx.");
            return;
        }

        String current_config = receiveConfig(userId, carId);
        ConfigRecord config = new ConfigRecord(current_config);
        System.out.println(config.toString());

        List<String> acOut1Values = new ArrayList<>();
        List<String> acOut2Values = new ArrayList<>();
        List<String> seatPos1Values = new ArrayList<>();
        List<String> seatPos3Values = new ArrayList<>();

        boolean exit = false;
        while (!exit) {
            System.out.println("\nSelect what you want to change: ");
            System.out.println("1. AC");
            System.out.println("2. Seats");
            System.out.println("3. Finish and send new configuration");
            System.out.println("4. Cancel all changes");
            System.out.print("Your choice: ");

            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }

            switch (choice) {
                case 1:
                    changeAC(scanner, acOut1Values, acOut2Values);
                    break;
                case 2:
                    changeSeats(scanner, seatPos1Values, seatPos3Values);
                    break;
                case 3:
                    if (performchanges(carId, acOut1Values, acOut2Values, seatPos1Values, seatPos3Values,
                            current_config)) {
                        exit = true;
                    }
                    break;
                case 4:
                    System.out.println("All changes canceled.");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
                    break;
            }
        }
    }

    private void changeAC(Scanner scanner, List<String> acOut1Values, List<String> acOut2Values) {
        boolean acExit = false;

        while (!acExit) {
            System.out.println("\nSelect AC output to change:");
            System.out.println("1. AC out 1");
            System.out.println("2. AC out 2");
            System.out.println("3. Go back");
            System.out.print("Your choice: ");

            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }

            switch (choice) {
                case 1:
                    System.out.print("Enter value for AC out 1: ");
                    String acOut1 = scanner.nextLine();
                    acOut1Values.add(acOut1);
                    break;
                case 2:
                    System.out.print("Enter value for AC out 2: ");
                    String acOut2 = scanner.nextLine();
                    acOut2Values.add(acOut2);
                    break;
                case 3:
                    acExit = true;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
                    break;
            }
        }
    }

    private void changeSeats(Scanner scanner, List<String> seatPos1Values, List<String> seatPos3Values) {
        boolean seatExit = false;

        while (!seatExit) {
            System.out.println("\nSelect seat position to change:");
            System.out.println("1. Seat position 1");
            System.out.println("2. Seat position 3");
            System.out.println("3. Go back");
            System.out.print("Your choice: ");

            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }

            switch (choice) {
                case 1:
                    System.out.print("Enter value for Seat position 1: ");
                    String pos1 = scanner.nextLine();
                    seatPos1Values.add(pos1);
                    System.out.println("Seat position 1 value added: " + pos1);
                    break;
                case 2:
                    System.out.print("Enter value for Seat position 3: ");
                    String pos3 = scanner.nextLine();
                    seatPos3Values.add(pos3);
                    System.out.println("Seat position 3 value added: " + pos3);
                    break;
                case 3:
                    seatExit = true;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
                    break;
            }
        }
    }

    private boolean performchanges(String carId, List<String> acOut1Values, List<String> acOut2Values,
            List<String> seatPos1Values, List<String> seatPos3Values, String currentConfig) {
        String userId = getClientId();

        JSONObject existingConfig;
        try {
            existingConfig = new JSONObject(currentConfig).getJSONObject("configuration"); // Extract the
                                                                                           // "configuration" field
        } catch (Exception e) {
            System.out.println("Error parsing existing configuration: " + e.getMessage());
            return false;
        }

        // Update AC configuration
        JSONArray acArray = existingConfig.optJSONArray("ac"); // Get AC configuration as JSONArray
        JSONArray updatedAcArray = new JSONArray();

        if (acArray != null) {
            for (int i = 0; i < acArray.length(); i++) {
                JSONObject acObject = acArray.getJSONObject(i);

                if (acObject.has("out1") && !acOut1Values.isEmpty()) {
                    acObject.put("out1", acOut1Values.get(0)); // Update out1 if modified
                }

                if (acObject.has("out2") && !acOut2Values.isEmpty()) {
                    acObject.put("out2", acOut2Values.get(0)); // Update out2 if modified
                }

                updatedAcArray.put(acObject); // Add updated AC object
            }
        }

        existingConfig.put("ac", updatedAcArray); // Update AC values in configuration

        // Update Seat configuration
        JSONArray seatArray = existingConfig.optJSONArray("seat"); // Get Seat configuration as JSONArray
        JSONArray updatedSeatArray = new JSONArray();

        if (seatArray != null) {
            for (int i = 0; i < seatArray.length(); i++) {
                JSONObject seatObject = seatArray.getJSONObject(i);

                if (seatObject.has("pos1") && !seatPos1Values.isEmpty()) {
                    seatObject.put("pos1", seatPos1Values.get(0)); // Update pos1 if modified
                }

                if (seatObject.has("pos3") && !seatPos3Values.isEmpty()) {
                    seatObject.put("pos3", seatPos3Values.get(0)); // Update pos3 if modified
                }

                updatedSeatArray.put(seatObject); // Add updated Seat object
            }
        }

        existingConfig.put("seat", updatedSeatArray); // Update Seat values in configuration

        String symmetricKeyPath = System.getProperty("user.dir") + "/../KeysAndCrt/CarsKey/car1.key.enc";
        String privateKeyPath = System.getProperty("user.dir") + "/../KeysAndCrt/User/user.p12";
        String privateKeyPassword = this.getPassword();
        String publicKeyPath = System.getProperty("user.dir") + "/../KeysAndCrt/User/user_public_key.der";

        Message message = null;

        try {
            // Wrap the updated configuration back into the "configuration" object
            JSONObject updatedConfigWrapper = new JSONObject();
            updatedConfigWrapper.put("configuration", existingConfig);
            JSONObject signedJson = Sign.signWithPrivate(updatedConfigWrapper, privateKeyPath, privateKeyPassword);

            String updatedConfigContent = signedJson.toString();

            // Send the updated configuration to the server
            System.out.println("Owner changed the config to: " + updatedConfigContent);

            message = new Message(MessageType.OWN_CHANGE_CONFIG, userId, carId, updatedConfigContent);

            message.encryptJSONConfig(updatedConfigContent, symmetricKeyPath, privateKeyPath, privateKeyPassword,
                    publicKeyPath);
            System.out.println("Encrypted new config to send to database: " + message.getContent());
        } catch (Exception e) {
            System.out.println("Error encrypting configuration: " + e.getMessage());
            return false;
        }

        System.out.println("\nChanging car configuration...");

        if (sendToServer(message)) {
            System.out.println("Sending to server the following configuration: " + message.getContent());
            Message serverResponse = receiveFromServer(false);
            System.out.println(serverResponse.getContent());
            return true;
        } else {
            System.out.println("Failed to change configuration. Please try again.");
            return false;
        }
    }
}
