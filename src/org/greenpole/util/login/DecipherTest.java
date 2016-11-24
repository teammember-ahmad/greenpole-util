/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.greenpole.util.login;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.security.PrivateKey;
import java.security.PublicKey;
import org.greenpole.util.BytesConverter;

/**
 *
 * @author Akinwale.Agbaje
 */
public class DecipherTest {

    public static void main(String[] args) {
        try {
            BytesConverter converter = new BytesConverter();
            
            String private_key = "C:/foldernew/private.key";
            String public_key = "C:/foldernew/public.key";
            Decipher decipher = new Decipher("RSA", private_key, public_key);
            
            if (!decipher.areKeysPresent()) {
                decipher.generateKey();
            }

            String originalText = "I am AWESOME!!!";
            ObjectInputStream inputStream = null;
            System.out.println("Original Text: " + originalText);
            
            // Encrypt the string using the public key
            inputStream = new ObjectInputStream(new FileInputStream(public_key));
            PublicKey publicKey = (PublicKey) inputStream.readObject();
            byte[] encryptedText = decipher.encryptText(originalText, publicKey);
            
            //convert encrypted text to string
            String convertedEncryptedText = converter.encodeToString(encryptedText);
            System.out.println("Encrypted Text, converted to string: " + convertedEncryptedText);
            
            //convert string back to encrypted text, for use on engine side
            encryptedText = converter.decodeToBytes(convertedEncryptedText);
            
            // Decrypt the cipher text using the private key.
            inputStream = new ObjectInputStream(new FileInputStream(private_key));
            PrivateKey privateKey = (PrivateKey) inputStream.readObject();
            String plainText = decipher.decryptEncryptedText(encryptedText, privateKey);
            
            // Printing the Original, Encrypted and Decrypted Text
            
            
            System.out.println("Decrypted Text: " + plainText);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
