package main.java.pt.ulisboa.tecnico.meic.sirs.t27.cli.commands;

public class HelpCommand {
    public static void main() {
        String separator = "\n========================================\n";
        String header = "\033[1;34m#############################################################\033[0m\n" +
                "\033[1;34m#                   CRYPTOLIB - HELP MENU                  #\033[0m\n" +
                "\033[1;34m#############################################################\033[0m\n";

        System.out.println(header);
        System.out.println("\033[1;36mWelcome to Cryptolib - Your Cryptographic Library\033[0m");
        System.out.println("Use the following commands to interact with the library:");
        System.out.println(separator);

        System.out.println("\033[1;33mAvailable Commands:\033[0m\n");

        System.out.println("\033[1;32mcryptolib help\033[0m");
        System.out.println("  Displays this help message.");
        System.out.println(separator);

        System.out.println(
                "\033[1;32mcryptolib protect <path-to-json> <path-to-secret-key> <path-to-private-key> <path-to-write-results> <filename>\033[0m");
        System.out.println(
                "  Protects a JSON document using symmetric encryption (AES) followed by protecting the secret key with asymmetric encryption (RSA).");
        System.out.println();

        System.out.println(
                "\033[1;32mcryptolib protect <path-to-json> <path-to-private-key> <path-to-write-results> <filename>\033[0m");
        System.out.println("  Protects a JSON document using only asymmetric encryption (RSA).");
        System.out.println(separator);

        System.out.println(
                "\033[1;32mcryptolib unprotect <path-to-protected-json> <path-to-private-key> <path-to-write-results> <filename> <1|2>\033[0m");
        System.out.println(
                "  Unprotects a document that was secured with symmetric (AES) and asymmetric (RSA) encryption.");
        System.out.println("    Mode 1: Unprotects a document protected with only asymmetric encryption (RSA).");
        System.out.println("    Mode 2: Unprotects a document protected with symmetric and asymmetric encryption.");
        System.out.println(separator);

        System.out.println("\033[1;32mcryptolib check <path-to-json> <path-to-public-key>\033[0m");
        System.out.println("  Verifies the authenticity of a signed document.");
        System.out.println(separator);

        System.out.println("\033[1;32mcryptolib sign <path-to-json> <path-to-p12> <password> <output-path>\033[0m");
        System.out.println("  Signs a JSON document using a private key.");
        System.out.println(separator);

        String footer = "\033[1;34m#############################################################\033[0m\n" +
                "\033[1;34m#            Thank you for using Cryptolib!                #\033[0m\n" +
                "\033[1;34m#############################################################\033[0m\n";
        System.out.println(footer);
    }
}
