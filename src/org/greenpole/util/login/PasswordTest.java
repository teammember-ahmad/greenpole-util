/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.greenpole.util.login;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 *
 * @author Akinwale.Agbaje
 */
public class PasswordTest {
    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        String password = "Password@1";
        String passwordToCheck = "Password@1";
        
        byte[] salt = Password.generateSalt();
        System.out.println("Generated salt: " + salt);
        
        byte[] encryptedPassword = Password.getEncryptedPassword(password, salt);
        System.out.println("Generated encrypted password: " + encryptedPassword);
        
        boolean checkSame = Password.authenticate(passwordToCheck, encryptedPassword, salt);
        System.out.println("Password the same?: " + checkSame);
    }
}
