package main.java.pt.ulisboa.tecnico.meic.sirs.t27.operations;

import java.security.*;
import java.util.Base64;
import java.nio.charset.StandardCharsets;

import org.json.JSONObject;

import main.java.pt.ulisboa.tecnico.meic.sirs.t27.utils.RSAKeyGenerator;

public class Check {
    public static Boolean verifySignaturePublicKey(JSONObject jsonObject, String publicKeyPath) throws Exception {
        PublicKey publicKey = RSAKeyGenerator.readPublicKey(publicKeyPath);
        byte[] signature = Base64.getDecoder().decode(jsonObject.getString("signature"));
        JSONObject verify = jsonObject;
        verify.remove("signature");
        // Let's check the signature
        Signature publicSignature = Signature.getInstance("SHA256withRSA");
        try {
            publicSignature.initVerify(publicKey);
        } catch (InvalidKeyException e) {
            System.err.println(e);
        }
        publicSignature.update(verify.toString().getBytes(StandardCharsets.UTF_8));
        return publicSignature.verify(signature);
    }
}
