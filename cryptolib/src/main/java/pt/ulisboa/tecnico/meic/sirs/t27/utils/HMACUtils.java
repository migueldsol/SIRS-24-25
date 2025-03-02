package main.java.pt.ulisboa.tecnico.meic.sirs.t27.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class HMACUtils {
    public static String generateHMAC(String data, String base64Key) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(Base64.getDecoder().decode(base64Key), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(keySpec);
        byte[] hmac = mac.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(hmac);
    }

    public static boolean verifyHMAC(String data, String hmac, String base64Key) throws Exception {
        String generatedHMAC = generateHMAC(data, base64Key);
        return generatedHMAC.equals(hmac);
    }
}
