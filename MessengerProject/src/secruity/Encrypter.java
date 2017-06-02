package user;

import com.sun.org.apache.xpath.internal.SourceTree;
import sun.misc.BASE64Encoder;

import javax.crypto.*;
import java.security.*;
import java.util.Base64;

public class Encrypter {

    private static Cipher cipher;

    public static String encryptAssynchron(String encrypt,Key key){
        return encrypt(encrypt.getBytes(),key,"RSA");
    }

    public static String encryptAssynchron(byte[] encrypt,Key key){
        return encrypt(encrypt,key,"RSA");
    }

    public static String encryptSynchron(byte[] encrypt,Key key){
        return encrypt(encrypt,key,"AES");
    }

    public static String encryptSynchron(String encrypt,Key key){
        return encrypt(encrypt.getBytes(),key,"AES");
    }

    private static String encrypt(byte[] encrypt,Key key,String verfahren){
        try {
            cipher = Cipher.getInstance(verfahren);
            cipher.init(Cipher.ENCRYPT_MODE,key);

            byte[] encrypted = cipher.doFinal(encrypt);

            BASE64Encoder base64 = new BASE64Encoder();
            return base64.encode(encrypted);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }
}
