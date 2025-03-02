package main.java.pt.ulisboa.tecnico.meic.sirs.t27.operations;

import java.nio.charset.StandardCharsets;
import java.security.Signature;
import java.util.Base64;

import org.json.JSONObject;

import main.java.pt.ulisboa.tecnico.meic.sirs.t27.utils.ExtractKey;

public class Sign {

    public static JSONObject signWithPrivate(JSONObject jsonObject, String p21FilePath, String passwordp12)
            throws Exception {

        ExtractKey privateKey = new ExtractKey(p21FilePath, passwordp12, "1");
        // Let's sign our message
        Signature privateSignature = Signature.getInstance("SHA256withRSA");
        privateSignature.initSign(privateKey.getPrivateKey());
        privateSignature.update(jsonObject.toString().getBytes(StandardCharsets.UTF_8));
        byte[] signature = privateSignature.sign();
        jsonObject.put("signature", Base64.getEncoder().encodeToString(signature));

        return jsonObject;
    }
}
