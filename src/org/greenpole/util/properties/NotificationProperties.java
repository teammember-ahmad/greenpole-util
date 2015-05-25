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
 * Properties loaded from notifications.properties
 */
public class NotificationProperties extends Properties {
    private InputStream input;
    private final String NOTIFICATION_LOCATION = "notification.location";
    private static final Logger logger = LoggerFactory.getLogger(org.greenpole.util.properties.NotifierProperties.class);
    
    /**
     * Loads the notifications.properties file.
     * @param clz the class whose classloader will be used to load the notification properties file
     */
    public NotificationProperties(Class clz) {
        String config_file = "notifications.properties";
        input = clz.getClassLoader().getResourceAsStream(config_file);
        logger.info("Loading configuration file - {}", config_file);
        try {
            load(input);
            close();
        } catch (IOException ex) {
            logger.info("failed to load configuration file - see error log");
            logger.error("error loading notification config file:", ex);
        }
    }
    
    /**
     * Gets the location.
     * @return the notification location
     */
    public String getNotificationLocation() {
        return getProperty(NOTIFICATION_LOCATION);
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
            logger.error("error closing notification config file input stream:", ex);
        }
    }
}
