package main.java.pt.ulisboa.tecnico.meic.sirs.t27.cli.commands;

import main.java.pt.ulisboa.tecnico.meic.sirs.t27.operations.Unprotect;

public class UnprotectCommand {
    public static void main(String[] args) throws Exception {

        if (args.length != 4 && args.length != 5) {
            System.out.println(
                    "  Usage: cryptolib unprotect <path-to-protected-json> <path-to-private-key> <path-to-write-results> <filename> <1|2>");
            return;
        }

        final String inputPath = args[0];
        final String privateKeyPath = args[1];
        final String outputDir = args[2];
        final String filename = args[3];
        final int mode = Integer.parseInt(args[4]);

        if (mode == 1) {
            Unprotect.unprotectAsymmetricOnly(inputPath, privateKeyPath, outputDir, filename);
        } else if (mode == 2) {
            Unprotect.unprotectSymmetricAndAsymmetric(inputPath, privateKeyPath, outputDir, filename);
        } else {
            System.out.println("Invalid mode. Use 1 or 2.");
        }

    }
}
