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
    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolProperties.class);

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
            logger.error("error loading notifier config file:", ex);
        }
    }
    
    /**
     * Gets the date format.
     * @return the date format
     */
    public String getDateFormat() {
        return getProperty("date.format");
    }
    
}
