package main.java.pt.ulisboa.tecnico.meic.sirs.t27.operations;

import java.nio.file.Files;
import java.nio.file.Paths;
import org.json.JSONObject;

import main.java.pt.ulisboa.tecnico.meic.sirs.t27.utils.AESKeyGenerator;
import main.java.pt.ulisboa.tecnico.meic.sirs.t27.utils.AESUtils;
import main.java.pt.ulisboa.tecnico.meic.sirs.t27.utils.RSAUtils;

public class Protect {

    /**
     * Encrypts a document with AES and then with RSA, and generates an HMAC.
     *
     * @param jsonPath      Path to the JSON file.
     * @param secretKeyPath Path to the AES key.
     * @param publicKeyPath Path to the RSA public key.
     * @param outputDir     Directory for saving results.
     * @param filename      Name for the encrypted file.
     * @throws Exception If encryption fails.
     */
    public static void protectWithSymmetricAndAsymmetric(String jsonPath, String secretKeyPath, String publicKeyPath,
            String outputDir, String filename) throws Exception {

        // Load JSON content
        String jsonContent = new String(Files.readAllBytes(Paths.get(jsonPath)));

        // Encrypt JSON with AES
        String aesEncryptedData = AESUtils.encrypt(jsonContent, secretKeyPath);

        // Encrypt AES key with RSA
        String rsaEncryptedKey = RSAUtils.encrypt(secretKeyPath, publicKeyPath);

        // Create JSON object with encrypted data and encrypted key
        JSONObject outputJson = new JSONObject();
        outputJson.put("data", aesEncryptedData);
        outputJson.put("key", rsaEncryptedKey);

        // Saving the resulting JSON
        Files.write(Paths.get(outputDir, filename + ".protected.json"), outputJson.toString(4).getBytes());
    }

    /**
     * Encrypts a document with AES and then with RSA, and generates an HMAC.
     *
     * @param jsonObject    Json object
     * @param secretKeyPath Path to the secret key
     * @param publicKeyPath Path to the RSA public key.
     * @param password      Path to the encrypted symmetric key
     * @throws Exception If encryption fails.
     */
    public static JSONObject protectWithSymmetricOnly(JSONObject jsonObject, String symmetricKeyPath,
            String privateKeyPath, String privateKeyPassword, String publicKeyPath) throws Exception {

        AESKeyGenerator.decryptKeyFile(symmetricKeyPath, privateKeyPath, privateKeyPassword);
        JSONObject configuration = jsonObject.getJSONObject("configuration");

        String aesEncryptedData = AESUtils.encrypt(configuration.toString(), symmetricKeyPath);
        AESKeyGenerator.encryptKeyFile(symmetricKeyPath, publicKeyPath);
        // Create JSON object with encrypted data and encrypted key
        jsonObject.put("configuration", aesEncryptedData);

        return jsonObject;
    }

    /**
     * Encrypts an already AES-encrypted document with RSA and generates an HMAC.
     *
     * @param jsonPath      Path to the AES-encrypted JSON file.
     * @param publicKeyPath Path to the RSA public key.
     * @param outputDir     Directory for saving results.
     * @param filename      Name for the encrypted file.
     * @throws Exception If encryption fails.
     */
    public static void protectWithAsymmetricOnly(String jsonPath, String publicKeyPath, String outputDir,
            String filename) throws Exception {

        // Load AES-encrypted content
        String aesEncryptedData = new String(Files.readAllBytes(Paths.get(jsonPath)));

        // Encrypt AES-encrypted data with RSA
        String publicKey = new String(Files.readAllBytes(Paths.get(publicKeyPath)));
        String rsaEncryptedData = RSAUtils.encrypt(aesEncryptedData, publicKey);

        // Create JSON object with encrypted data
        JSONObject outputJson = new JSONObject();
        outputJson.put("data", rsaEncryptedData);

        // Saving the resulting JSON
        Files.write(Paths.get(outputDir, filename + ".protected.json"), outputJson.toString(4).getBytes());
    }

    /**
     * Encrypts an already AES-encrypted document with RSA and generates an HMAC.
     *
     * @param jsonObject    Path to the AES-encrypted JSON file.
     * @param publicKeyPath Path to the RSA public key.
     * @throws Exception If encryption fails.
     */
    public static String protectWithAsymmetricOnly(JSONObject jsonObject, String publicKeyPath) throws Exception {
        // Encrypt AES-encrypted data with RSA
        String rsaEncryptedData = RSAUtils.encrypt(jsonObject.toString(), publicKeyPath);

        return rsaEncryptedData;

    }
}
