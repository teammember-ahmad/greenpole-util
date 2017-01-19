/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.greenpole.util.agm;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.PublicKey;
import org.greenpole.util.BytesConverter;
import org.greenpole.util.login.Decipher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Akinwale.Agbaje
 */
public class PasswordPackager {
    private static final Logger logger = LoggerFactory.getLogger(PasswordPackager.class);
    
    /**
     * Encrypts a password with a public key and converts it to base 64.
     * @param password the password to encrypt
     * @param pathToPublicKey the path to the public key
     * @return the encrypted and base 64 converted password
     */
    public static String packagePasswordForLogin(String password, String pathToPublicKey) {
        BytesConverter converter = new BytesConverter();
        Decipher decipher = new Decipher("RSA");
        
        String convertedEncryptedPassword = "";
        ObjectInputStream inputStream = null;
        try {
            // Encrypt the string using the public key
            inputStream = new ObjectInputStream(new FileInputStream(pathToPublicKey));
            PublicKey publicKey = (PublicKey) inputStream.readObject();
            byte[] encryptedPassword = decipher.encryptText(password, publicKey);
            
            //convert encrypted text to string
            convertedEncryptedPassword = converter.encodeToString(encryptedPassword);
        } catch (FileNotFoundException ex) {
            logger.info("Error thrown. The file for the public key was not found. See error log");
            logger.error("Error thrown. The file for the public key was not found", ex);
        } catch (IOException ex) {
            logger.info("Error thrown. The file for the public key could not be read properly. See error log");
            logger.error("Error thrown. The file for the public key could not be read properly", ex);
        } catch (ClassNotFoundException ex) {
            logger.info("Error thrown. Class not found. See error log");
            logger.error("Error thrown. Class not found", ex);
        } finally {
            try {
                inputStream.close();
            } catch (IOException ex) {
                logger.info("Error thrown. Inputstream could not be closed. See error log");
                logger.error("Error thrown. Inputstream could not be closed", ex);
            }
        }
        return convertedEncryptedPassword;
    }
}
