package secruity;

import dataManagement.Logger;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.*;
import java.security.*;
import java.security.KeyStore.ProtectionParameter;
import java.security.cert.CertificateException;

/**
 * Created by Benni on 22.05.2017.
 */
public class KeyStoreSynchron {


    private static KeyStoreSynchron instance;
    private KeyStore keyStore;


    private KeyStoreSynchron(){}

    public static KeyStoreSynchron getInstance() {
        if(instance == null){
            instance = new KeyStoreSynchron();
        }
        return instance;
    }

    public boolean loadKeyStore(char[] password){
        try {
            keyStore = KeyStore.getInstance("JCEKS");
            keyStore.load(new FileInputStream("myKey"), password);
        } catch (KeyStoreException | CertificateException | IOException | NoSuchAlgorithmException e) {
            Logger.getInstance().log("KSS001 cannot load the KeyStore!");
            e.printStackTrace();
            return false;
        }
        return true;
    }


    public void saveKey(char[] password, Key key){
        if(keyStore == null || password == null || key == null){
            return;
        }

        ProtectionParameter protectionParameter = new KeyStore.PasswordProtection(password);

        if(key instanceof SecretKey) {
            KeyStore.SecretKeyEntry secretKeyEntry = new KeyStore.SecretKeyEntry((SecretKey) key);

            try {
                loadKeyStore(password);

                keyStore.setEntry("serverKeyAlias", secretKeyEntry, protectionParameter);
                keyStore.store(new FileOutputStream("myKey"),password);
            } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    public SecretKey getKey(char[] password){
        try {

            if( keyStore == null || keyStore.size() == 0) {
                if (keyStore == null) {
                    if(instance == null)
                        loadKeyStore(password);
                    else
                        return null;
                } else {
                    System.err.println("KeyStoresize 0");
                    return null;
                }
            }
            KeyStore.SecretKeyEntry ske = (KeyStore.SecretKeyEntry) keyStore.getEntry("serverKeyAlias",new KeyStore.PasswordProtection(password));
            return ske.getSecretKey();
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableEntryException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static void main(String[] args) {

        char[] password = {'p','a','s','s','w','o','r','d'};
        KeyStoreSynchron keyStoreSynchron = KeyStoreSynchron.getInstance();
        keyStoreSynchron.loadKeyStore(password);

        try {
            SecretKey s = KeyGenerator.getInstance("AES").generateKey();
            keyStoreSynchron.saveKey(password, s );

            SecretKey q = KeyGenerator.getInstance("AES").generateKey();
            keyStoreSynchron.saveKey(password,q);

            SecretKey g = keyStoreSynchron.getKey(password);
            System.out.println(s.equals(g));
            System.out.println(s.equals(q));

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        /*
        Key[] keys = KeyConverter.generateAssynchronPair();

        File f = new File("out/production/MessengerProjekt/bob.png.png");

        try {
            assert keys != null;
            byte[] bytesbefore  = Files.readAllBytes(f.toPath());


            System.out.println(bytesbefore.length);
            //String s = Encrypter.encryptAssynchron(Files.readAllBytes(f.toPath()),keys[0]);

            SecretKey secretKey =KeyConverter.generateSynchronKey();

            String s = Encrypter.encryptSynchron(bytesbefore,secretKey);
            byte[] synchBytes = Decrypter.decryptSynchronToBytes(s,secretKey);


            FileOutputStream fileOutputStream = new FileOutputStream("bob2.png");
            fileOutputStream.write(synchBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    */
    }
}
