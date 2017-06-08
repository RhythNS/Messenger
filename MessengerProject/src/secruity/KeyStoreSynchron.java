package secruity;

import dataManagement.Logger;

import javax.crypto.SecretKey;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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

    public boolean loadKeyStore(char[] password, boolean firsttime){
        try {

            keyStore = KeyStore.getInstance("JCEKS");
            if(firsttime){
                keyStore.load(null,password);
                keyStore.store(new FileOutputStream("myKey"),password);
            }
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
                System.err.println("KeyStoresize 0");
                return null;
            }

            KeyStore.SecretKeyEntry ske = (KeyStore.SecretKeyEntry) keyStore.getEntry("serverKeyAlias",new KeyStore.PasswordProtection(password));
            return ske.getSecretKey();
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableEntryException e) {
            e.printStackTrace();
            return null;
        }
    }


}
