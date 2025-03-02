package main.java.pt.ulisboa.tecnico.meic.sirs.t27.cli.commands;

import main.java.pt.ulisboa.tecnico.meic.sirs.t27.operations.Protect;

public class ProtectCommand {
    public static void main(String[] args) throws Exception {

        if (args.length != 4 && args.length != 5) {
            System.out.println(
                    "Usage : cryptolib protect <path-to-json> <path-to-private-key> <path-to-write-results> <filename>");
            System.out.println(
                    "Usage : cryptolib protect <path-to-json> <path-to-secret-key> <path-to-private-key> <path-to-write-results> <filename>");
            return;
        }

        final String jsonPath = args[0];
        final String pathToWriteResults = args[args.length - 2];
        final String filename = args[args.length - 1];

        if (args.length == 4) {
            final String publicKeyPath = args[1];
            Protect.protectWithAsymmetricOnly(jsonPath, publicKeyPath, pathToWriteResults, filename);
        } else {
            final String secretKeyPath = args[1];
            final String publicKeyPath = args[2];
            Protect.protectWithSymmetricAndAsymmetric(jsonPath, secretKeyPath, publicKeyPath, pathToWriteResults,
                    filename);
        }
    }
}
