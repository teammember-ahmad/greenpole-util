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
 * Properties loaded from the threadpool_notifiers.properties file.
 */
public class ThreadPoolProperties extends Properties {
    private InputStream input;
    private final String THREADPOOL_SIZE_AUTHORISER_NOTIFIER = "threadpool.size.authoriser.notifier";
    private final String THREADPOOL_SIZE_AUTHORISER_QUEUE = "threadpool.size.authoriser.queue";
    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolProperties.class);
    
    /**
     * Loads the threadpool_notifiers.properties file.
     * @param clz the class whose classloader will be used to load the threadpool notifiers properties file
     */
    public ThreadPoolProperties(Class clz) {
        String config_file = "threadpool_notifiers.properties";
        input = clz.getClassLoader().getResourceAsStream(config_file);
        logger.info("Loading configuration file - {}", config_file);
        try {
            load(input);
            close();
        } catch (IOException ex) {
            logger.info("failed to load configuration file - see error log");
            logger.error("error loading threadpool config file:", ex);
        }
    }
    
    /**
     * Gets the pool size to be used in the thread executor within the Authoriser notifier.
     * @return the pool size
     */
    public String getAuthoriserNotifierPoolSize() {
        return getProperty(THREADPOOL_SIZE_AUTHORISER_NOTIFIER);
    }
    
    /**
     * Gets the pool size to be used in the thread executor within the Authoriser notifier queue. 
     * @return the pool size
     */
    public String getAuthoriserNotifierQueuePoolSize() {
        return getProperty(THREADPOOL_SIZE_AUTHORISER_QUEUE);
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
            logger.error("error closing threadpool config file input stream:", ex);
        }
    }
}
