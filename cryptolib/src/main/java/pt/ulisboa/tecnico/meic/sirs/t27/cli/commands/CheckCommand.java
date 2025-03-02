package main.java.pt.ulisboa.tecnico.meic.sirs.t27.cli.commands;

import main.java.pt.ulisboa.tecnico.meic.sirs.t27.operations.Check;
import org.json.JSONObject;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CheckCommand {
    public static void main(String[] args) throws Exception {

        if (args.length != 2) {
            System.out.println("Usage : cryptolib check <path-to-json> <path-to-public-key>");
            return;
        }

        final String jsonPath = args[0];
        final String publicKeyPath = args[1];

        JSONObject jsonObject = new JSONObject(new String(Files.readAllBytes(Paths.get(jsonPath))));
        Boolean result = Check.verifySignaturePublicKey(jsonObject, publicKeyPath);
        System.out.println("Signature verification result: " + result);

    }
}
