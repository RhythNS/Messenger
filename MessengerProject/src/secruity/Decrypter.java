package secruity;

import sun.misc.BASE64Decoder;

import javax.crypto.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.*;

public class Decrypter {


    private static Cipher cipher;
    private static final String RSA = "RSA";
    private static final String AES = "AES";


    /**
     *
     * @param toDecode byte[] array which will be decrypted
     * @param key Assynchron Key to decode
     * @return returns the decrypted Thing as a String
     */
    public static String decryptAssynchronToString(byte[] toDecode, Key key){
        return new String(decrypt(toDecode,key,RSA));
    }

    public static byte[] decryptAssynchronToBytes(String toDecode,Key key){
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            return decrypt(decoder.decodeBuffer(toDecode),key,RSA);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String decryptAssynchronToString(String toDecode,Key key){
        BASE64Decoder decoder = new BASE64Decoder();
        byte[] toDecodebytes = null;
        try {
            toDecodebytes = decoder.decodeBuffer(toDecode);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] decrypted = decrypt(toDecodebytes, key, RSA);

        try {
            return new String(decrypted,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] decryptSynchronToBytes(String toDecrypt, Key key){
        byte[] res = null;
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            res = decoder.decodeBuffer(toDecrypt);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return decrypt(res,key,AES);
    }

    /**
     *
     * @param toDecrypt String what you want to decrypt
     * @param key the SecretKey which you can decrypt with
     * @return the decoded String
     */
    public static String decryptSynchronToString(String toDecrypt, Key key){
        BASE64Decoder decoder = new BASE64Decoder();
        byte[] decodedbytes = new byte[0];
        try {
            decodedbytes = decoder.decodeBuffer(toDecrypt);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] decrypted = decrypt(decodedbytes, key, AES);

        try {
            return new String(decrypted,"UTF8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Main Methode to decrypt: Will decrypt the byte Array
     * to an byte Array with the String parameter
     *
     * @param decrypt   is the byteArray which needs to get decrypted
     * @param key       is the SecretKey or the PublicKey to decrypt
     * @param algorythm is the algorythm RSA or AES
     * @return          the decryted byteArray
     */
    private static byte[] decrypt(byte[] decrypt,Key  key,String algorythm){
        byte[] res = null;
        try {
            cipher = Cipher.getInstance(algorythm);
            cipher.init(Cipher.DECRYPT_MODE,key);
            res = cipher.doFinal(decrypt);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        return res;
    }
}