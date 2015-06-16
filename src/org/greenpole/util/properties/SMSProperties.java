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
 * @author Akinwale Agbaje
 */
public class SMSProperties extends Properties {
    private InputStream input;
    private final String SMS_USERNAME = "sms.username";
    private final String SMS_PASSWORD = "sms.password";
    private static final Logger logger = LoggerFactory.getLogger(EmailProperties.class);

    /**
     * Loads the sms.properties file.
     * @param clz the class whose classloader will be used to load the sms properties file
     */
    public SMSProperties(Class clz) {
        String config_file = "sms.properties";
        input = clz.getClassLoader().getResourceAsStream(config_file);
        logger.info("Loading configuration file - {}", config_file);
        try {
            load(input);
            close();
        } catch (IOException ex) {
            logger.info("failed to load configuration file - see error log");
            logger.error("error loading sms config file:", ex);
        }
    }
    
    /**
     * Gets the sms api username.
     * @return the sms api username
     */
    public String getUsername() {
        return getProperty(SMS_USERNAME);
    }
    
    /**
     * Gets the sms api password.
     * @return the sms api password
     */
    public String getPassword() {
        return getProperty(SMS_PASSWORD);
    }
    
    /**
     * Close input stream.
     */
    private void close() {
        try {
            if (input != null)
                    input.close();
        } catch (IOException ex) {
            logger.info("failed to close configuration file input stream - see error log");
            logger.error("error closing sms config file input stream:", ex);
        }
    }
    
    
    
}
