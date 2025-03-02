package main.java.pt.ulisboa.tecnico.meic.sirs.t27.https.communication;

import java.io.IOException;
import org.mindrot.jbcrypt.BCrypt;

public class generateHash {
    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.println("Usage:");
            System.out.println("  To hash a password: java generateHash <password>");
            System.out.println("  To verify a password: java generateHash <password> <hashedPassword>");
            return;
        }

        if (args.length == 1) {
            // Hash a password
            System.out.println("Hashed Password: " + BCrypt.hashpw(args[0], BCrypt.gensalt()));
        } else if (args.length == 2) {
            // Verify the password
            System.out.println(args[1]);
            boolean isMatch = BCrypt.checkpw(args[0], args[1]);
            if (isMatch) {
                System.out.println("Password matches the hash!");
            } else {
                System.out.println("Invalid password!");
            }
        } else {
            System.out.println("Invalid arguments.");
        }
    }
}
