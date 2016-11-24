/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.greenpole.util.login;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Akinwale.Agbaje
 */
public class Decipher {
    private final String algorithm;
    private final String private_key_file;
    private final String public_key_file;
    private static final Logger logger = LoggerFactory.getLogger(Decipher.class);
    
    public Decipher(String algorithm, String private_key_file, String public_key_file) {
        this.algorithm = algorithm;
        this.private_key_file = private_key_file;
        this.public_key_file = public_key_file;
    }
    
    /**
     * Generates the public and private keys to be used by the system for sending / receiving
     * encrypted messages
     */
    public void generateKey() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance(algorithm);
            keyGen.initialize(1024);
            KeyPair keyPair = keyGen.generateKeyPair();
            
            File privateKeyFile = new File(private_key_file);
            File publicKeyFile = new File(public_key_file);

            // Create files to store public and private key
            if (privateKeyFile.getParentFile() != null) {//to ensure that the private / public key variables are not empty
                privateKeyFile.getParentFile().mkdirs();
                System.out.println("private key not null");
            }
            privateKeyFile.createNewFile();

            if (publicKeyFile.getParentFile() != null) {//to ensure that the private / public key variables are not empty
                publicKeyFile.getParentFile().mkdirs();
                System.out.println("public key not null");
            }
            publicKeyFile.createNewFile();

            // Saving the Public key in a file
            ObjectOutputStream publicKeyOS = new ObjectOutputStream(new FileOutputStream(publicKeyFile));
            publicKeyOS.writeObject(keyPair.getPublic());
            publicKeyOS.close();

            // Saving the Private key in a file
            ObjectOutputStream privateKeyOS = new ObjectOutputStream(new FileOutputStream(privateKeyFile));
            privateKeyOS.writeObject(keyPair.getPrivate());
            privateKeyOS.close();
            System.out.println("keys created!");
        } catch (IOException | NoSuchAlgorithmException ex) {
            logger.info("failed to generate public and private keys - see error log");
            logger.error("error generating public and private keys:", ex);
        }
    }
    
    /**
     * Checks if the public and private keys are present.
     * @return true, if they are present
     */
    public boolean areKeysPresent() {
        File privateKey = new File(private_key_file);
        File publicKey = new File(public_key_file);
        return privateKey.exists() && publicKey.exists();
    }
    
    /**
     * Encrypts with the text with the public key.
     * @param textToEncrypt the text to encrypt
     * @param key the public key to encrypt the text with
     * @return the encrypted text
     */
    public byte[] encryptText(String textToEncrypt, PublicKey key) {
        byte[] encryptedText = null;
        try {
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, key); // encrypt the plain text using the public key
            encryptedText = cipher.doFinal(textToEncrypt.getBytes());
        } catch (InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException ex) {
            logger.info("failed to encrypt the supplied text - see error log");
            logger.error("failed to encrypt the supplied text:", ex);
        }
        return encryptedText;
    }
    
    public String decryptEncryptedText(byte[] encryptedText, PrivateKey key) {
        byte[] decryptedTextInBytes = null;
        try {
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.DECRYPT_MODE, key); //decrypt the encrypted text using the private key
            decryptedTextInBytes = cipher.doFinal(encryptedText);
        } catch (InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException ex) {
            logger.info("failed to decrypt the supplied text - see error log");
            logger.error("failed to decrypt the supplied text:", ex);
        }
        return new String(decryptedTextInBytes);
    }
}
