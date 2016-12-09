/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.greenpole.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Akinwale.Agbaje
 */
public class EmailAddress {
    
    /**
     * Checks if email is valid.
     * @param emailAddress the email address to check
     * @return true, if email is valid
     */
    public static boolean emailIsValid(String emailAddress) {
        String email_pattern = "[a-zA-Z0-9]+(\\.[_a-zA-Z0-9]+)*@[a-zA-Z0-9]+(\\.[a-zA-Z]{2,})*(\\.[a-zA-Z]{2,})";
        Pattern pattern = Pattern.compile(email_pattern);
        Matcher matcher = pattern.matcher(emailAddress);
        return matcher.matches();
    }
}
