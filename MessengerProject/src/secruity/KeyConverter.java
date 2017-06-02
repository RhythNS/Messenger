package user;

import com.sun.xml.internal.messaging.saaj.util.Base64;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import static com.sun.xml.internal.org.jvnet.fastinfoset.EncodingAlgorithmIndexes.BASE64;


/**
 * Created by Benni on 19.05.2017.
 */
public class KeyConverter {

    /**
     *
     * @return Key[0] = Public Key
     *         Key[1] = Private Key
     */


    public static Key[] generateAssynchronPair(){
        try {

            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(1024);

            KeyPair kp = keyPairGenerator.generateKeyPair();

            return new Key[]{kp.getPublic(),kp.getPrivate()};

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static SecretKey generateSynchronKey(){
        KeyGenerator keyGenerator;
        try {

            keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(128);

            return keyGenerator.generateKey();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static SecretKey generateSecretKeyFromString(String keystring){

        BASE64Decoder decoder = new BASE64Decoder();
        try {
            byte[] decodedString = decoder.decodeBuffer(keystring);

            return new SecretKeySpec(decodedString,0,decodedString.length,"AES");

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static PublicKey generatePublicKeyFromString(String keystring){

        BASE64Decoder decoder = new BASE64Decoder();
        try {
            byte[] keybytes = decoder.decodeBuffer(keystring);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keybytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            return keyFactory.generatePublic(keySpec);

        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String convertKeyToString(Key k){

        byte[] encodedKey = k.getEncoded();
        BASE64Encoder encoder = new BASE64Encoder();

        return encoder.encode(encodedKey);
    }




}
