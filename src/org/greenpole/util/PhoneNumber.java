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
public class PhoneNumber {
    
    /**
     * Checks if phone number is valid.
     * @param phoneNumber the phone number to check
     * @return true, if phone number is valid
     */
    public static boolean phoneIsValid(String phoneNumber) {
        String phone_pattern = "(\\+{0,1})[0-9]+";
        Pattern pattern = Pattern.compile(phone_pattern);
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }
}
