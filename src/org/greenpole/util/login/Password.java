/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.greenpole.util.login;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 *
 * @author Akinwale.Agbaje
 */
public class Password {
    
    /**
     * Checks if a string password is the same as the supplied encrypted password.
     * @param passwordToCheck the password to check
     * @param encryptedPassword the encrypted password
     * @param salt the salt
     * @return true, if both passwords are the same
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException 
     */
    public static boolean authenticate(String passwordToCheck, byte[] encryptedPassword, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] encryptedPasswordToCheck = getEncryptedPassword(passwordToCheck, salt);
        boolean same = Arrays.equals(encryptedPassword, encryptedPasswordToCheck);
        return same;
    }
    
    /**
     * Encrypts a password with a salt.
     * @param password the password to encrypt
     * @param salt the salt to encrypt the password with
     * @return the encrypted password
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException 
     */
    public static byte[] getEncryptedPassword(String password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String algorithm = "PBKDF2WithHmacSHA1";
        int keyLength = 160;
        int passwordIterations = 30000;
        
        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, passwordIterations, keyLength);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(algorithm);
        
        byte[] encryptedPassword = keyFactory.generateSecret(keySpec).getEncoded();
        
        return encryptedPassword;
    }
    
    /**
     * Generates the salt to be paired with a user's password.
     * @return the password salt
     * @throws NoSuchAlgorithmException 
     */
    public static byte[] generateSalt() throws NoSuchAlgorithmException {
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[8];
        random.nextBytes(salt);
        
        return salt;
    }
}
