package main.java.pt.tecnico.sirs.t27.motorist;

import java.util.Scanner;

import org.json.JSONObject;

import main.java.pt.ulisboa.tecnico.meic.sirs.t27.https.communication.Message;
import main.java.pt.ulisboa.tecnico.meic.sirs.t27.https.communication.Message.MessageType;
import main.java.pt.ulisboa.tecnico.meic.sirs.t27.operations.Sign;

public class Manufacturer extends Client {

    public Manufacturer(String host, Integer port, Scanner scanner, String clienType, String password) {
        super(host, port, scanner, clienType, password);
    }

    @Override
    public void cli() {
        System.out.println("\nWelcome Manufacturer!");
        System.out.println("Please select the action you want to perform:");
        System.out.println("1. Send Firmware Update");
        System.out.println("2. Exit");
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
                    sendFirmwareUpdate(scanner);
                    break;
                case 2:
                    exit = true;
                    System.out.println("Exiting. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
                    break;
            }
        }
    }

    private void sendFirmwareUpdate(Scanner scanner) {
        String manufacturerId = getClientId();

        System.out.print("\nEnter the new firmware version (e.g., 2.1.3): ");
        String firmwareVersion = scanner.nextLine();

        if (!firmwareVersion.matches("\\d+\\.\\d+\\.\\d+")) {
            System.out.println("Invalid firmware version format. Please use the format x.y.z.");
            return;
        }

        System.out.print("\nEnter the car identifier (e.g., car123): ");
        String carId = scanner.nextLine();

        if (!carId.matches("car\\d+")) {
            System.out.println("Invalid car identifier format. Please use the format CARx.");
            return;
        }

        String privateKeyPath = System.getProperty("user.dir") + "/../KeysAndCrt/Manufacturer/manufacturer.p12";
        String privateKeyPassword = this.getPassword();
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("firmwareVersion", firmwareVersion);
            JSONObject signed = Sign.signWithPrivate(jsonObject, privateKeyPath, privateKeyPassword);

            Message message = new Message(MessageType.MAN_UPDATE_FIRMWARE, manufacturerId, carId, signed.toString());

            System.out.println("Sending firmware update...");

            if (sendToServer(message)) {
                Message server_response = receiveFromServer(false);
                System.out.println(server_response.getContent());
            } else {
                System.out.println("Failed to turn maintence mode on/off. Please try again.");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
