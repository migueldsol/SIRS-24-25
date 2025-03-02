package main.java.pt.ulisboa.tecnico.meic.sirs.t27;

import java.security.Key;
import java.util.Base64;

import javax.crypto.SecretKey;

import org.json.JSONObject;

import main.java.pt.ulisboa.tecnico.meic.sirs.t27.operations.Protect;
import main.java.pt.ulisboa.tecnico.meic.sirs.t27.operations.Unprotect;
import main.java.pt.ulisboa.tecnico.meic.sirs.t27.utils.AESKeyGenerator;

public class test {

    public static void main(String[] args) throws Exception {

        String path = System.getProperty("user.dir");

        // AESKeyGenerator.write(path + "/../KeysAndCrt/CarsKey/car1.key");

        AESKeyGenerator.encryptKeyFile(path + "/../KeysAndCrt/CarsKey/car1.key.enc",
                path + "/../KeysAndCrt/User/user_public_key.der");

        // AESKeyGenerator.decryptKeyFile(path + "/../KeysAndCrt/CarsKey/car1.key.enc",
        // path + "/../KeysAndCrt/User/user.p12", "VyXeCrCxpEQDInzt5yAf");

        // Key key1 = AESKeyGenerator.read(path +
        // "/../KeysAndCrt/CarsKey/car1.key.enc");

        // Key key2 = AESKeyGenerator.read(path + "/../KeysAndCrt/CarsKey/car1.key");

        // System.out.println("key encoded is: " +
        // Base64.getEncoder().encodeToString(key1.getEncoded()));

        // System.out.println("key encoded is: " +
        // Base64.getEncoder().encodeToString(key2.getEncoded()));

        String jsonString = "{\"configuration\": {\"ac\": [{\"out2\": \"203\"}], \"seat\": [{\"pos1\": \"5\"}]}}";

        JSONObject jsonObject = new JSONObject(jsonString);

        JSONObject jsonEncrypted = Protect.protectWithSymmetricOnly(jsonObject,
                path + "/../KeysAndCrt/CarsKey/car1.key.enc",
                path + "/../KeysAndCrt/User/user.p12", "VyXeCrCxpEQDInzt5yAf",
                path + "/../KeysAndCrt/User/user_public_key.der");

        System.out.println(jsonEncrypted.toString());

        JSONObject jsonDecrypted = Unprotect.unprotectSymmetricOnly(jsonEncrypted,
                path + "/../KeysAndCrt/CarsKey/car1.key.enc",
                path + "/../KeysAndCrt/User/user.p12", "VyXeCrCxpEQDInzt5yAf",
                path + "/../KeysAndCrt/User/user_public_key.der");

        System.out.println(jsonDecrypted.toString());

    }

}