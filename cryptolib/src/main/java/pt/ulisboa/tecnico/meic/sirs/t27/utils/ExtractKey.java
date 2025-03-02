package main.java.pt.ulisboa.tecnico.meic.sirs.t27.utils;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;

public class ExtractKey {

    private PrivateKey privateKey;

    public ExtractKey(String p12FilePath, String passwordp12, String alias) {
        try {
            // Path to the .p12 file
            char[] password = passwordp12.toCharArray();

            // Load the KeyStore
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            FileInputStream fis = new FileInputStream(p12FilePath);
            keyStore.load(fis, password);

            // Check if the alias contains a private key
            if (!keyStore.isKeyEntry(alias)) {
                throw new Exception("No private key found for alias: " + alias);
            }

            // Retrieve the private key
            this.privateKey = (PrivateKey) keyStore.getKey(alias, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }
}