package main.java.pt.ulisboa.tecnico.meic.sirs.t27.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/*
* AESUtils provides utility methods for symmetric encrypting and decrypting data using AES.
*/

public class AESUtils {
    public static void main(String[] args) throws Exception {

        // check args
        if (args.length < 2) {
            System.err.println("Usage: aes-key-gen [e|d] <file> <key>");
            return;
        }

        final String mode = args[0];
        final String data = args[1];
        final String keyPath = args[2];

        if (mode.toLowerCase().startsWith("e")) {
            encrypt(data, keyPath);
        } else {
            decrypt(data, keyPath);
        }
    }

    /**
     * Encrypts the given data using AES with a given base64 encoded key and returns
     * the encrypted data as a base64 encoded string.
     *
     * @param data      the data to encrypt
     * @param base64Key the base64 encoded key to use for encryption
     * @return the encrypted data as a base64 encoded string
     * @throws Exception if there was an error encrypting the data
     */
    public static String encrypt(String data, String base64Key) throws Exception {
        SecretKeySpec keySpec = (SecretKeySpec) AESKeyGenerator.read(base64Key);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] iv = new byte[16];
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        byte[] encrypted = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }

    /**
     * Decrypts the given encrypted data using the given base64 key.
     * 
     * @param encryptedData the encrypted data to decrypt
     * @param base64Key     the key to decrypt the data with
     * @return the decrypted String
     * @throws Exception if there was an error decrypting the data
     */

    public static String decrypt(String encryptedData, String base64Key) throws Exception {
        SecretKeySpec keySpec = (SecretKeySpec) AESKeyGenerator.read(base64Key);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] iv = new byte[16];
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        return new String(decrypted);
    }
}
