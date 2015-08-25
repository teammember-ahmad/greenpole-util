/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.greenpole.util.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.greenpole.hibernate.entity.EnvironmentalVariables;
import org.greenpole.hibernate.entity.PropertyThreadpool;
import org.greenpole.hibernate.query.GeneralComponentQuery;
import org.greenpole.hibernate.query.factory.ComponentQueryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Akinwale Agbaje
 * Properties loaded from the threadpool_notifiers.properties file.
 */
public class ThreadPoolProperties extends Properties {
    private static ThreadPoolProperties INSTANCE;
    private InputStream instream;
    private final String THREADPOOL_SIZE_AUTHORISER_QUEUE = "threadpool.size.authoriser.queue";
    private final String THREADPOOL_SIZE_REJECTER_QUEUE = "threadpool.size.rejecter.queue";
    private final String THREADPOOL_SIZE_TEXT_QUEUE = "threadpool.size.text.queue";
    private final String THREADPOOL_SIZE_EMAIL_QUEUE = "threadpool.size.email.queue";
    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolProperties.class);
    private final GeneralComponentQuery gq = ComponentQueryFactory.getGeneralComponentQuery();
    
    private ThreadPoolProperties() {
        load();
    }

    public static ThreadPoolProperties getInstance() {
        if (INSTANCE == null)
            INSTANCE = new ThreadPoolProperties();
        return INSTANCE;
    }
    
    /**
     * Loads a configuration file from the disk
     */
    public final void load() {
        try {
            String config_file = "threadpool_notifiers.properties";
            EnvironmentalVariables ev = gq.getVariable("property.path");
            String prop_path = ev.getPath();
            logger.info("Loading configuration file - {}", config_file);
            
            boolean exists = false;
            File propFile = new File(prop_path + config_file);
            //if property file does not exist in designated location, create file using default file within system classpath
            if (!propFile.exists()) {
                reload();
            } else {
                exists = true;
            }
            
            if (exists) {
                FileInputStream loadstream = new FileInputStream(propFile);
                load(loadstream);
                loadstream.close();
                
                //ensure that all property keys have not been tampered with
                List<PropertyThreadpool> all = gq.getAllThreadProperty();
                for (PropertyThreadpool t : all) {
                    boolean found = false;
                    for (Map.Entry pairs : entrySet()) {
                        String key = (String) pairs.getKey();
                        String value = (String) pairs.getValue();
                        if (t.getPropertyName().equals(key) && t.getPropertyValue().equals(value)) {
                            found = true;
                            break;
                        }
                    }
                    
                    if (!found) {
                        reload();
                        break;
                    }
                }
            }
        } catch (Exception ex) {
            logger.info("failed to load configuration file - see error log");
            logger.error("error loading email config file:", ex);
        }
    }
    
    /**
     * Reloads a configuration file, getting all necessary variables from the database.
     */
    public final void reload() {
        try {
            String config_file = "threadpool_notifiers.properties";
            EnvironmentalVariables ev = gq.getVariable("property.path");
            String prop_path = ev.getPath();
            logger.info("Reloading configuration file - {}", config_file);
            
            File propFile = new File(prop_path + config_file);
            propFile.delete();
            //if property file does not exist in designated location, create file using default file within system classpath
            if (!propFile.exists()) {
                propFile.getParentFile().mkdirs();

                instream = ThreadPoolProperties.class.getClassLoader().getResourceAsStream(config_file);
                FileOutputStream outstream = new FileOutputStream(propFile);

                byte[] buffer = new byte[1024];

                int length;
                while ((length = instream.read(buffer)) > 0) {
                    outstream.write(buffer, 0, length);
                }

                instream.close();
                outstream.close();
            }
            
            FileInputStream loadstream = new FileInputStream(propFile);
            load(loadstream);
            loadstream.close();
            
            FileOutputStream changestream = new FileOutputStream(propFile);
            
            setProperty(THREADPOOL_SIZE_AUTHORISER_QUEUE, gq.getThreadProperty(THREADPOOL_SIZE_AUTHORISER_QUEUE).getPropertyValue());
            setProperty(THREADPOOL_SIZE_REJECTER_QUEUE, gq.getThreadProperty(THREADPOOL_SIZE_REJECTER_QUEUE).getPropertyValue());
            setProperty(THREADPOOL_SIZE_TEXT_QUEUE, gq.getThreadProperty(THREADPOOL_SIZE_TEXT_QUEUE).getPropertyValue());
            setProperty(THREADPOOL_SIZE_EMAIL_QUEUE, gq.getThreadProperty(THREADPOOL_SIZE_EMAIL_QUEUE).getPropertyValue());
            
            store(changestream, null);
            changestream.close();
        } catch (Exception ex) {
            logger.info("failed to reload configuration file - see error log");
            logger.error("error reloading email config file:", ex);
        }
    }
    
    /**
     * Loads the threadpool_notifiers.properties file.
     * @param clz the class whose classloader will be used to load the threadpool notifiers properties file
     */
    /*public ThreadPoolProperties(Class clz) {
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
    }*/
    
    /**
     * Gets the pool size to be used in the thread executor within the Authoriser notifier queue. 
     * @return the pool size
     */
    public String getAuthoriserNotifierQueuePoolSize() {
        return getProperty(THREADPOOL_SIZE_AUTHORISER_QUEUE);
    }
    
    /**
     * Gets the pool size to be used in the thread executor within the Rejecter notifier queue. 
     * @return the pool size
     */
    public String getRejecterNotifierQueuePoolSize() {
        return getProperty(THREADPOOL_SIZE_REJECTER_QUEUE);
    }
    
    /**
     * Gets the pool size to be used in the thread executor within the Text notifier queue.
     * @return the pool size
     */
    public String getTextNotifierQueuePoolSize() {
        return getProperty(THREADPOOL_SIZE_TEXT_QUEUE);
    }
    
    /**
     * Gets the pool size to be used in the thread executor within the Email notifier queue. 
     * @return the pool size
     */
    public String getEmailNotifierQueuePoolSize() {
        return getProperty(THREADPOOL_SIZE_EMAIL_QUEUE);
    }
    
    /**
     * Close input stream.
     */
    private void close() {
        try {
            if (instream != null)
                instream.close();
        } catch (IOException ex) {
            logger.info("failed to close configuration file input stream - see error log");
            logger.error("error closing threadpool config file input stream:", ex);
        }
    }
}
