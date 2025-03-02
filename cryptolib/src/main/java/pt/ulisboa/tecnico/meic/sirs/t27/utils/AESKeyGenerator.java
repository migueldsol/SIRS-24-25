package main.java.pt.ulisboa.tecnico.meic.sirs.t27.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.util.Base64;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AESKeyGenerator {

    public static void main(String[] args) throws Exception {

        // check args
        if (args.length < 2) {
            System.err.println("Usage: aes-key-gen [r|w] <key-file>");
            return;
        }

        final String mode = args[0];
        final String keyPath = args[1];

        if (mode.toLowerCase().startsWith("w")) {
            write(keyPath);
        } else {
            read(keyPath);
        }
    }

    public static void write(String keyPath) throws GeneralSecurityException, IOException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);
        Key key = keyGen.generateKey();
        byte[] encoded = key.getEncoded();
        FileOutputStream fos = new FileOutputStream(keyPath);
        fos.write(encoded);
        fos.close();
    }

    public static String generateKey() throws GeneralSecurityException {
        // Generate AES key
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);
        SecretKey key = keyGen.generateKey();
        // Convert the AES key to a Base64-encoded string
        String base64Key = Base64.getEncoder().encodeToString(key.getEncoded());
        return base64Key;
    }

    public static void encryptKeyFile(String keyPath, String publicKeypath)
            throws GeneralSecurityException, IOException {

        Key key = read(keyPath);

        String keyAsString = Base64.getEncoder().encodeToString(key.getEncoded());

        try {
            // Encrypt the Base64-encoded key string using RSA
            String encryptedKey = RSAUtils.encrypt(keyAsString, publicKeypath);
            FileOutputStream fos = new FileOutputStream(keyPath);
            fos.write(encryptedKey.getBytes());
            fos.close();
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public static void decryptKeyFile(String keyPath, String privateKeyPath, String privateKeyPassword)
            throws GeneralSecurityException, IOException {
        // Read the encrypted key from the file
        byte[] encryptedKeyBytes;
        try (FileInputStream fis = new FileInputStream(keyPath)) {
            encryptedKeyBytes = fis.readAllBytes();
        }

        String keyAsString = new String(encryptedKeyBytes);

        // Get the private Key
        ExtractKey privateKey = new ExtractKey(privateKeyPath, privateKeyPassword, "1");
        try {
            // Decrypt the encrypted key using the RSA private key
            String base64Key = RSAUtils.decrypt(keyAsString, privateKey.getPrivateKey());
            // Write the encrypted key (now a string) to the file
            byte[] decodedKeyBytes = Base64.getDecoder().decode(base64Key);
            FileOutputStream fos = new FileOutputStream(keyPath);
            fos.write(decodedKeyBytes);
            fos.close();
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public static Key read(String keyPath) throws GeneralSecurityException, IOException {
        FileInputStream fis = new FileInputStream(keyPath);
        byte[] encoded = new byte[fis.available()];
        fis.read(encoded);
        fis.close();
        SecretKeySpec secretKeySpec = new SecretKeySpec(encoded, "AES");
        return secretKeySpec;
    }

}
