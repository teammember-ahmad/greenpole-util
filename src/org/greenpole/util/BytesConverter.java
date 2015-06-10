/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.greenpole.util;

import java.util.Base64;

/**
 *
 * @author Akin
 * Used to convert from string to bytes, and vice-versa
 */
public class BytesConverter {
    
    /**
     * Decodes a file's contents in string format to a byte array.
     * @param fileContents the byte contents of a file in string format
     * @return the byte array representation
     */
    public byte[] decodeToBytes(String fileContents) {
        //return Base64.getUrlDecoder().decode(fileContents);
        return Base64.getDecoder().decode(fileContents);
    }
    
    /**
     * Encodes a file's contents in bytes to a string.
     * @param bytes the byte content of a file
     * @return the string representation
     */
    public String encodeToString(byte[] bytes) {
        //return Base64.getUrlEncoder().encodeToString(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }
}
