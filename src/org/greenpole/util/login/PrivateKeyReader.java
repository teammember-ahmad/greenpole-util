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
import java.security.PrivateKey;
import org.greenpole.util.properties.GreenpoleProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Akinwale.Agbaje
 */
public class PrivateKeyReader {
    private static PrivateKeyReader INSTANCE;
    private PrivateKey privateKey;
    
    private final GreenpoleProperties greenProp = GreenpoleProperties.getInstance();
    private static final Logger logger = LoggerFactory.getLogger(PrivateKeyReader.class);

    public PrivateKeyReader() {
        loadFile();
    }
    
    public static PrivateKeyReader getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PrivateKeyReader();
        }
        return INSTANCE;
    }
    
    private void loadFile() {
        logger.info("reading private key file into memory");
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(greenProp.getPrivateKeyFile()))) {
            privateKey = (PrivateKey) inputStream.readObject();
        } catch (FileNotFoundException ex) {
            logger.info("private key file not found. See error log");
            logger.error("private key file not found", ex);
        } catch (IOException ex) {
            logger.info("I/O error reading private key file. See error log");
            logger.error("I/O error reading private key file", ex);
        } catch (ClassNotFoundException ex) {
            logger.info("Class not found error thrown while reading private key file. See error log");
            logger.error("Class not found error thrown while reading private key file", ex);
        }
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }
}
