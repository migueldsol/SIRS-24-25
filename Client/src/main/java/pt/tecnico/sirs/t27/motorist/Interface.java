package main.java.pt.tecnico.sirs.t27.motorist;

import java.io.IOException;
import java.util.Scanner;

public class Interface {

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\nWelcome to the System!");
            System.out.println("Please select your client type:");
            System.out.println("1. User");
            System.out.println("2. Manufacturer");
            System.out.println("3. Mechanic");
            System.out.println("4. Exit");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            String path = System.getProperty("user.dir") + "/../KeysAndCrt/";
            String host = "192.168.0.50";
            switch (choice) {
                case 1:
                    try {
                        String password = handleLogin(scanner, "User");
                        System.setProperty("javax.net.ssl.keyStore", path + "User/user.p12");
                        System.setProperty("javax.net.ssl.keyStorePassword", password);
                        System.setProperty("javax.net.ssl.trustStore", path + "User/usertruststore.jks");
                        System.setProperty("javax.net.ssl.trustStorePassword", "USERSIRS2425");
                        new User(host, 5000, scanner, "User", password);
                    } catch (SecurityException e) {
                        System.out.println(e);
                    }
                    break;
                case 2:
                    try {
                        String password = handleLogin(scanner, "Manufacturer");
                        System.setProperty("javax.net.ssl.keyStore", path + "Manufacturer/manufacturer.p12");
                        System.setProperty("javax.net.ssl.keyStorePassword", password);
                        System.setProperty("javax.net.ssl.trustStore",
                                path + "Manufacturer/manufacturertruststore.jks");
                        System.setProperty("javax.net.ssl.trustStorePassword", "MANUFACTURERSIRS2425");
                        new Manufacturer(host, 5000, scanner, "Manufacturer", password);
                    } catch (SecurityException e) {
                        System.out.println(e);
                    }
                    break;
                case 3:
                    try {
                        String password = handleLogin(scanner, "Mechanic");
                        System.setProperty("javax.net.ssl.keyStore", path + "Mechanic/mechanic.p12");
                        System.setProperty("javax.net.ssl.keyStorePassword", password);
                        System.setProperty("javax.net.ssl.trustStore", path + "Mechanic/mechanictruststore.jks");
                        System.setProperty("javax.net.ssl.trustStorePassword", "MECHANICSIRS2425");
                        new Mechanic(host, 5000, scanner, "Mechanic", password);
                    } catch (SecurityException e) {
                        System.out.println(e);
                    }
                    break;
                case 4:
                    System.out.println("Exiting the system. Goodbye!");
                    scanner.close();
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }

        }
    }

    private static String handleLogin(Scanner scanner, String clientType) {
        System.out.println("You selected: " + clientType);
        System.out.print("Please enter your password: ");
        String password = scanner.nextLine();
        return password;
    }
}
