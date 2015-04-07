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
public class NotifierProperties extends Properties {
    private final String AUTHORISER_NOTIFIER_QUEUE_NAME = "authoriser.notifier.queue.name";
    private final String AUTHORISER_NOTIFIER_QUEUE_FACTORY = "authoriser.notifier.queue.factory";
    private static final Logger logger = LoggerFactory.getLogger(NotifierProperties.class);
    
    public NotifierProperties(Class clz) {
        String config_file = "notifiers.properties";
        InputStream input = clz.getClassLoader().getResourceAsStream(config_file);
        logger.info("Loading configuration file - {}", config_file);
        try {
            load(input);
        } catch (IOException ex) {
            logger.info("failed to load configuration file - see error log");
            logger.error("error loading notifier config file:", ex);
        }
    }
    
    public String getAuthoriserNotifierQueueName() {
        return getProperty(AUTHORISER_NOTIFIER_QUEUE_NAME);
    }
    
    public String getAuthoriserNotifierQueueFactory() {
        return getProperty(AUTHORISER_NOTIFIER_QUEUE_FACTORY);
    }
}
