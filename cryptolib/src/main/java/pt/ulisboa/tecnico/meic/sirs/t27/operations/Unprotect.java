package main.java.pt.ulisboa.tecnico.meic.sirs.t27.operations;

import java.nio.file.Files;
import java.nio.file.Paths;
import org.json.JSONObject;

import main.java.pt.ulisboa.tecnico.meic.sirs.t27.utils.AESKeyGenerator;
import main.java.pt.ulisboa.tecnico.meic.sirs.t27.utils.AESUtils;
import main.java.pt.ulisboa.tecnico.meic.sirs.t27.utils.ExtractKey;
import main.java.pt.ulisboa.tecnico.meic.sirs.t27.utils.RSAUtils;

public class Unprotect {

    /**
     * Decrypts a document encrypted with AES and RSA.
     *
     * @param inputPath      Path to the protected JSON file.
     * @param privateKeyPath Path to the RSA private key.
     * @param outputDir      Directory to save decrypted results.
     * @param filename       Name for the decrypted file.
     * @throws Exception If decryption fails.
     */
    public static void unprotectSymmetricAndAsymmetric(String inputPath, String privateKeyPath, String outputDir,
            String filename) throws Exception {

        // Load protected JSON
        String protectedContent = new String(Files.readAllBytes(Paths.get(inputPath)));
        JSONObject protectedJson = new JSONObject(protectedContent);

        // Extract encrypted AES key and encrypted data
        String rsaEncryptedKey = protectedJson.getString("key");
        String aesEncryptedData = protectedJson.getString("data");

        // Decrypt the AES key using RSA
        String aesKey = RSAUtils.decrypt(rsaEncryptedKey, privateKeyPath);

        // Decrypt JSON content with AES
        String decryptedData = AESUtils.decrypt(aesEncryptedData, aesKey);

        // Saving decrypted JSON
        Files.write(Paths.get(outputDir, filename + ".unprotected.json"), decryptedData.getBytes());
    }

    /**
     * Decrypts a document encrypted with AES and RSA.
     *
     * @param inputPath      Path to the protected JSON file.
     * @param privateKeyPath Path to the RSA private key.
     * @param outputDir      Directory to save decrypted results.
     * @param filename       Name for the decrypted file.
     * @throws Exception If decryption fails.
     */
    public static JSONObject unprotectSymmetricOnly(JSONObject jsonObject, String symmetricKeyPath,
            String privateKeyPath,
            String privateKeyPassword, String publicKeyPath)
            throws Exception {

        AESKeyGenerator.decryptKeyFile(symmetricKeyPath, privateKeyPath, privateKeyPassword);
        String aesEncryptedData = jsonObject.getString("configuration");

        // Decrypt JSON content with AES
        String decryptedData = AESUtils.decrypt(aesEncryptedData, symmetricKeyPath);
        AESKeyGenerator.encryptKeyFile(symmetricKeyPath, publicKeyPath);

        // Saving decrypted JSON Field
        jsonObject.put("configuration", new JSONObject(decryptedData));

        return jsonObject;
    }

    /**
     * Decrypts a document encrypted only with RSA.
     *
     * @param inputPath      Path to the protected JSON file.
     * @param privateKeyPath Path to the RSA private key.
     * @param outputDir      Directory to save decrypted results.
     * @param filename       Name for the decrypted file.
     * @throws Exception If decryption fails.
     */
    public static void unprotectAsymmetricOnly(String inputPath, String privateKeyPath, String outputDir,
            String filename) throws Exception {

        // Create output directory if it doesn't exist
        Files.createDirectories(Paths.get(outputDir));

        // Load protected JSON
        String protectedContent = new String(Files.readAllBytes(Paths.get(inputPath)));
        JSONObject protectedJson = new JSONObject(protectedContent);

        String rsaEncryptedData = protectedJson.getString("data");

        // Decrypting with RSA
        String decryptedData = RSAUtils.decrypt(rsaEncryptedData,
                new String(Files.readAllBytes(Paths.get(privateKeyPath))));

        // Saving the decrypted data
        Files.write(Paths.get(outputDir, filename + ".unprotected.json"), decryptedData.getBytes());
    }

    /**
     * Decrypts a document encrypted only with RSA.
     *
     * @param inputPath      Path to the protected JSON file.
     * @param privateKeyPath Path to the RSA private key.
     * @param p12filepath    Path to p12 private key
     * @param passwordp12    Password to access
     * @throws Exception If decryption fails.
     */
    public static JSONObject unprotectAsymmetricOnly(String jsonEncrypted, String p21FilePath, String passwordp12)
            throws Exception {

        ExtractKey privateKey = new ExtractKey(p21FilePath, passwordp12, "1");
        // Decrypting with RSA
        String decryptedData = RSAUtils.decrypt(jsonEncrypted, privateKey.getPrivateKey());
        JSONObject jsonObject = new JSONObject(decryptedData);

        return jsonObject;
    }
}
