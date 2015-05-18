/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.greenpole.util.properties;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Akin
 * Properties loaded from the greenpole_engine.properties file.
 */
public class GreenpoleProperties extends Properties {
    private final String DATE_FORMAT = "date.format";
    private final String HOLDER_SIGNATURE_PATH = "holder.signature.dir";
    private final String POWER_OF_ATTORNEY_PATH = "holder.powerofattorney.dir";
    private final String ATTORNEY_SIZE = "attorney.size";
    private final String SIGNATURE_SIZE = "signature.size";
    private static final Logger logger = LoggerFactory.getLogger(GreenpoleProperties.class);

    /**
     * Loads the greenpole_engine.properties file.
     * @param clz the class whose classloader will be used to load the greenpole engine properties file
     */
    public GreenpoleProperties(Class clz) {
        String config_file = "greenpole_engine.properties";
        InputStream input = clz.getClassLoader().getResourceAsStream(config_file);
        logger.info("Loading configuration file - {}", config_file);
        try {
            load(input);
        } catch (IOException ex) {
            logger.info("failed to load configuration file - see error log");
            logger.error("error loading greenpole engine config file:", ex);
        }
    }
    
    /**
     * Gets the date format.
     * @return the date format
     */
    public String getDateFormat() {
        return getProperty(DATE_FORMAT);
    }
    
    /**
     * Gets the signature path.
     * @return Gets the signature path
     */
    public String getSignaturePath() {
        return getProperty(HOLDER_SIGNATURE_PATH);
    }
    
    /**
     * Gets the power of attorney path.
     * @return the power of attorney path
     */
    public String getPowerOfAttorneyPath() {
        return getProperty(POWER_OF_ATTORNEY_PATH);
    }
    
    /**
     * Gets the default size of the power of attorney.
     * @return the default size of the power of attorney
     */
    public String getPowerOfAttorneySize() {
        return getProperty(ATTORNEY_SIZE);
    }
    
    /**
     * Gets the default size of the signature.
     * @return the default size of the signature
     */
    public String getSignatureSize() {
        return getProperty(SIGNATURE_SIZE);
    }
}
