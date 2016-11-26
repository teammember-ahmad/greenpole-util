/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.greenpole.util.login;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.PublicKey;
import org.greenpole.util.properties.GreenpoleProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Akinwale.Agbaje
 */
public class PublicKeyReader {
    private static PublicKeyReader INSTANCE;
    private PublicKey publicKey;
    
    private final GreenpoleProperties greenProp = GreenpoleProperties.getInstance();
    private static final Logger logger = LoggerFactory.getLogger(PublicKeyReader.class);

    public PublicKeyReader() {
        loadFile();
    }
    
    public static PublicKeyReader getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PublicKeyReader();
        }
        return INSTANCE;
    }
    
    private void loadFile() {
        logger.info("reading public key file into memory");
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(greenProp.getPublicKeyFile()))) {
            publicKey = (PublicKey) inputStream.readObject();
        } catch (FileNotFoundException ex) {
            logger.info("public key file not found. See error log");
            logger.error("public key file not found", ex);
        } catch (IOException ex) {
            logger.info("I/O error reading public key file. See error log");
            logger.error("I/O error reading public key file", ex);
        } catch (ClassNotFoundException ex) {
            logger.info("Class not found error thrown while reading public key file. See error log");
            logger.error("Class not found error thrown while reading public key file", ex);
        }
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }
}
