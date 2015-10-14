/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.greenpole.util;

/**
 *
 * @author akinwale.agbaje
 */
public class UpperCase {
    
    /**
     * Changes the first character in a string to upper case.
     * @param value the string to change
     * @return the changed string
     */
    public static String toUpperCase(String value) {
        char[] characters = value.toCharArray();
        characters[0] = Character.toUpperCase(characters[0]);
        return new String(characters);
    }
}
