package main.java.pt.ulisboa.tecnico.meic.sirs.t27.cli.commands;

import main.java.pt.ulisboa.tecnico.meic.sirs.t27.operations.Sign;
import org.json.JSONObject;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SignCommand {
    public static void main(String[] args) throws Exception {

        if (args.length != 4) {
            System.out.println("Usage : cryptolib sign <path-to-json> <path-to-p12> <password> <output-path>");
            return;
        }

        final String jsonPath = args[0];
        final String p12Path = args[1];
        final String password = args[2];
        final String outputPath = args[3];

        String jsonContent = new String(Files.readAllBytes(Paths.get(jsonPath)));
        JSONObject jsonObject = new JSONObject(jsonContent);

        JSONObject signedJson = Sign.signWithPrivate(jsonObject, p12Path, password);

        Files.write(Paths.get(outputPath), signedJson.toString(4).getBytes());
    }
}
