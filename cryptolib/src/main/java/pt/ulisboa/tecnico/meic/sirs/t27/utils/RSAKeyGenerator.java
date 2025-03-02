package main.java.pt.ulisboa.tecnico.meic.sirs.t27.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class RSAKeyGenerator {

    public static void main(String[] args) throws Exception {

        // check args
        if (args.length < 3) {
            System.err.println("Usage: rsa-key-gen [r|w] <priv-key-file> <pub-key-file>");
            return;
        }

        final String mode = args[0];
        final String privkeyPath = args[1];
        final String pubkeyPath = args[2];

        if (mode.toLowerCase().startsWith("w")) {
            write(pubkeyPath, privkeyPath);
        } else {
            readPublicKey(pubkeyPath);
        }
    }

    public static void write(String publicKeyPath, String privateKeyPath) throws GeneralSecurityException, IOException {
        // get an AES private key
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(1024);
        KeyPair keys = keyGen.generateKeyPair();

        PrivateKey privKey = keys.getPrivate();
        byte[] privKeyEncoded = privKey.getEncoded();

        PublicKey pubKey = keys.getPublic();
        byte[] pubKeyEncoded = pubKey.getEncoded();

        FileOutputStream privFos = new FileOutputStream(privateKeyPath);
        privFos.write(privKeyEncoded);
        privFos.close();

        FileOutputStream pubFos = new FileOutputStream(publicKeyPath);
        pubFos.write(pubKeyEncoded);
        pubFos.close();
    }

    public static KeyPair read(String publicKeyPath, String privateKeyPath)
            throws GeneralSecurityException, IOException {
        FileInputStream pubFis = new FileInputStream(publicKeyPath);
        byte[] pubEncoded = new byte[pubFis.available()];
        pubFis.read(pubEncoded);
        pubFis.close();

        X509EncodedKeySpec pubSpec = new X509EncodedKeySpec(pubEncoded);
        KeyFactory keyFacPub = KeyFactory.getInstance("RSA");
        PublicKey pub = keyFacPub.generatePublic(pubSpec);

        FileInputStream privFis = new FileInputStream(privateKeyPath);
        byte[] privEncoded = new byte[privFis.available()];
        privFis.read(privEncoded);
        privFis.close();

        PKCS8EncodedKeySpec privSpec = new PKCS8EncodedKeySpec(privEncoded);
        KeyFactory keyFacPriv = KeyFactory.getInstance("RSA");
        PrivateKey priv = keyFacPriv.generatePrivate(privSpec);

        KeyPair keys = new KeyPair(pub, priv);
        return keys;
    }

    public static PublicKey readPublicKey(String publicKeyPath)
            throws GeneralSecurityException, IOException {
        FileInputStream pubFis = new FileInputStream(publicKeyPath);
        byte[] pubEncoded = new byte[pubFis.available()];
        pubFis.read(pubEncoded);
        pubFis.close();

        X509EncodedKeySpec pubSpec = new X509EncodedKeySpec(pubEncoded);
        KeyFactory keyFacPub = KeyFactory.getInstance("RSA");
        PublicKey pub = keyFacPub.generatePublic(pubSpec);

        return pub;
    }

    public static PrivateKey readPrivateKey(String privateKeyPath)
            throws GeneralSecurityException, IOException {
        FileInputStream privFis = new FileInputStream(privateKeyPath);
        byte[] privEncoded = new byte[privFis.available()];
        privFis.read(privEncoded);
        privFis.close();

        PKCS8EncodedKeySpec privSpec = new PKCS8EncodedKeySpec(privEncoded);
        KeyFactory keyFacPriv = KeyFactory.getInstance("RSA");
        PrivateKey priv = keyFacPriv.generatePrivate(privSpec);

        return priv;
    }
}
