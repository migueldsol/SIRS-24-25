package main.java.pt.tecnico.sirs.t27.motorist;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.json.JSONObject;

import main.java.pt.ulisboa.tecnico.meic.sirs.t27.https.communication.Message;
import main.java.pt.ulisboa.tecnico.meic.sirs.t27.https.communication.Message.MessageType;
import main.java.pt.ulisboa.tecnico.meic.sirs.t27.operations.Check;
import main.java.pt.ulisboa.tecnico.meic.sirs.t27.operations.Sign;

public class Mechanic extends Client {

    public Mechanic(String host, int port, Scanner scanner, String clienType, String password) {
        super(host, port, scanner, clienType, password);
    }

    @Override
    public void cli() {
        System.out.println("\nWelcome Mechanic!");
        System.out.println("Please select the action you want to perform:");
        System.out.println("1. Check Car Configuration");
        System.out.println("2. Test Car");
        System.out.println("3. Update Car Firmware");
        System.out.println("4. Exit");
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
                    checkCarConfigurations(scanner);
                    break;
                case 2:
                    testCar(scanner);
                    break;
                case 3:
                    updateCarFirmware(scanner);
                    break;
                case 4:
                    exit = true;
                    System.out.println("Exiting. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
                    break;
            }
        }
    }

    private void checkCarConfigurations(Scanner scanner) {
        String mechanicId = getClientId();

        System.out.print("\nEnter the car identifier (e.g., car123): ");
        String carId = scanner.nextLine();

        if (!carId.matches("car\\d+")) {
            System.out.println("Invalid car identifier format. Please use the format CARx.");
            return;
        }

        Message message = new Message(MessageType.MEC_CHECK_CONFIGURATIONS, mechanicId, carId);

        System.out.println("Checking car configuration...");

        String symmetricKeyPath = System.getProperty("user.dir") + "/../KeysAndCrt/CarsKey/car1Mechanic.key.enc";
        String privateKeyPath = System.getProperty("user.dir") + "/../KeysAndCrt/Mechanic/mechanic.p12";
        String privateKeyPassword = this.getPassword();
        String publicKeyPath = System.getProperty("user.dir") + "/../KeysAndCrt/Mechanic/mechanic_public_key.der";

        if (sendToServer(message)) {
            Message server_response = receiveFromServer(true, symmetricKeyPath, privateKeyPath, privateKeyPassword,
                    publicKeyPath);
            ConfigRecord config = new ConfigRecord(server_response.getContent());
            System.out.println(config.toString());
        } else {
            System.out.println("Failed to check car configuration. Please try again.");
        }
    }

    private void testCar(Scanner scanner) {
        System.out.print("\nEnter the car id to test (e.g., car123): ");
        String carId = scanner.nextLine();

        if (!carId.matches("car\\d+")) {
            System.out.println("Invalid car identifier format. Please use the format CARx.");
            return;
        }

        List<String> acOut1Values = new ArrayList<>();
        List<String> acOut2Values = new ArrayList<>();
        List<String> seatPos1Values = new ArrayList<>();
        List<String> seatPos3Values = new ArrayList<>();

        boolean exit = false;
        while (!exit) {
            System.out.println("\nSelect what you want to test: ");
            System.out.println("1. AC");
            System.out.println("2. Seats");
            System.out.println("3. Finish and send tests");
            System.out.println("4. Cancel all tests");
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
                    testAC(scanner, acOut1Values, acOut2Values);
                    break;
                case 2:
                    testSeats(scanner, seatPos1Values, seatPos3Values);
                    break;
                case 3:
                    if (performTests(carId, acOut1Values, acOut2Values, seatPos1Values, seatPos3Values)) {
                        exit = true;
                    }
                    break;
                case 4:
                    System.out.println("All tests canceled.");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
                    break;
            }
        }
    }

    private void testAC(Scanner scanner, List<String> acOut1Values, List<String> acOut2Values) {
        boolean acExit = false;

        while (!acExit) {
            System.out.println("\nSelect AC output to test:");
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

    private void testSeats(Scanner scanner, List<String> seatPos1Values, List<String> seatPos3Values) {
        boolean seatExit = false;

        while (!seatExit) {
            System.out.println("\nSelect seat position to test:");
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

    private boolean performTests(String carId, List<String> acOut1Values, List<String> acOut2Values,
            List<String> seatPos1Values, List<String> seatPos3Values) {
        String mechanicId = getClientId();
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{");

        if (!acOut1Values.isEmpty() || !acOut2Values.isEmpty()) { // Add AC tests if any exist
            jsonBuilder.append("\"ac\": {");

            if (!acOut1Values.isEmpty()) {
                jsonBuilder.append("\"out1\": [");
                for (String value : acOut1Values) {
                    jsonBuilder.append(String.format("\"%s\",", value));
                }
                jsonBuilder.setLength(jsonBuilder.length() - 1); // Remove trailing comma
                jsonBuilder.append("]");

                if (!acOut2Values.isEmpty()) {
                    jsonBuilder.append(",");
                }
            }

            if (!acOut2Values.isEmpty()) {
                jsonBuilder.append("\"out2\": [");
                for (String value : acOut2Values) {
                    jsonBuilder.append(String.format("\"%s\",", value));
                }
                jsonBuilder.setLength(jsonBuilder.length() - 1); // Remove trailing comma
                jsonBuilder.append("]");
            }

            jsonBuilder.append("}");

            if (!seatPos1Values.isEmpty() || !seatPos3Values.isEmpty()) {
                jsonBuilder.append(",");
            }
        }

        if (!seatPos1Values.isEmpty() || !seatPos3Values.isEmpty()) { // Add seat tests if any exist
            jsonBuilder.append("\"seat\": {");

            if (!seatPos1Values.isEmpty()) {
                jsonBuilder.append("\"pos1\": [");
                for (String value : seatPos1Values) {
                    jsonBuilder.append(String.format("\"%s\",", value));
                }
                jsonBuilder.setLength(jsonBuilder.length() - 1); // Remove trailing comma
                jsonBuilder.append("]");

                if (!seatPos3Values.isEmpty()) {
                    jsonBuilder.append(",");
                }
            }

            if (!seatPos3Values.isEmpty()) {
                jsonBuilder.append("\"pos3\": [");
                for (String value : seatPos3Values) {
                    jsonBuilder.append(String.format("\"%s\",", value));
                }
                jsonBuilder.setLength(jsonBuilder.length() - 1); // Remove trailing comma
                jsonBuilder.append("]");
            }

            jsonBuilder.append("}");
        }

        jsonBuilder.append("}");

        Message message = new Message(MessageType.MEC_PERFORM_TESTS, mechanicId, carId, jsonBuilder.toString());

        System.out.println("\nPerforming tests...");

        if (sendToServer(message)) {
            Message server_response = receiveFromServer(false);
            System.out.println(server_response.getContent());
            return true;
        } else {
            System.out.println("Failed to perform tests. Please try again.");
            return false;
        }
    }

    private void updateCarFirmware(Scanner scanner) {
        String mechanicId = getClientId();
        System.out.print("\nEnter the car identifier (e.g., car123): ");
        String carId = scanner.nextLine();

        if (!carId.matches("car\\d+")) {
            System.out.println("Invalid car identifier format. Please use the format CARx.");
            return;
        }

        Message message1 = new Message(MessageType.MEC_GET_AVAILABLE_FIRMWARE_VERSIONS, mechanicId, carId);

        System.out.println("\nRequesting available firmware versions...");

        if (!sendToServer(message1)) {
            System.out.println("Failed to fetch available firmware versions. Please try again.");
        }
        String publicKeyPath = System.getProperty("user.dir")
                + "/../KeysAndCrt/Manufacturer/manufacturer_public_key.der";

        Message server_response1 = receiveFromServer(false);
        try {
            // Split the concatenated JSON string into individual JSONs
            String[] jsonStrings = server_response1.getContent().split("\\s+"); // Split by comma, handle whitespace

            for (String jsonString : jsonStrings) {
                // Parse each JSON string into a JSONObject
                JSONObject update = new JSONObject(jsonString);

                // Verify the signature of the JSON
                boolean isVerified = Check.verifySignaturePublicKey(update, publicKeyPath);

                if (!isVerified) {
                    System.out
                            .println("Firmware Version " + update.getString("firmwareVersion")
                                    + " is from unidentified origin! Please contact Car Manufacturer!");
                } else {
                    System.out.println("Firmware Version verified: " + update.getString("firmwareVersion"));
                }
            }

        } catch (Exception e) {
            System.err.println("Error processing firmware updates: " + e.getMessage());
        }

        System.out.print("\nEnter the firmware version to download (e.g., 2.1.3): ");

        String firmwareVersion = scanner.nextLine();

        if (!firmwareVersion.matches("\\d+\\.\\d+\\.\\d+")) {
            System.out.println("Invalid firmware version format. Please use the format x.y.z.");
            return;
        }

        String privateKeyPath = System.getProperty("user.dir") + "/../KeysAndCrt/Mechanic/mechanic.p12";
        String privateKeyPassword = this.getPassword();
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("firmwareVersion", firmwareVersion);
            JSONObject signed = Sign.signWithPrivate(jsonObject, privateKeyPath, privateKeyPassword);

            Message message2 = new Message(MessageType.MEC_UPDATE_FIRMWARE, mechanicId, carId, signed.toString());

            System.out.println("Sending firmware update...");

            if (sendToServer(message2)) {
                Message server_response2 = receiveFromServer(false);
                System.out.println(server_response2.getContent());
            } else {
                System.out.println("Failed to send firmware update. Please try again.");
            }
        } catch (Exception e) {
            System.err.println(e);
        }
    }

}
