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
import org.greenpole.hibernate.entity.PropertyEmail;
import org.greenpole.hibernate.entity.PropertyNotifiers;
import org.greenpole.hibernate.query.GeneralComponentQuery;
import org.greenpole.hibernate.query.factory.ComponentQueryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Akinwale Agbaje
 * Properties loaded from the notifiers.properties file.
 */
public class NotifierProperties extends Properties {
    private static NotifierProperties INSTANCE;
    private InputStream instream;
    private final String AUTHORISER_NOTIFIER_QUEUE_NAME = "authoriser.notifier.queue.name";
    private final String NOTIFIER_QUEUE_FACTORY = "notifier.queue.factory";
    private final String EMAIL_NOTIFIER_QUEUE_NAME = "email.notifier.queue.name";
    private final String REJECT_NOTIFIER_QUEUE_NAME = "reject.notifier.queue.name";
    private final String INFORMATION_NOTIFIER_QUEUE_NAME = "information.notifier.queue.name";
    private static final Logger logger = LoggerFactory.getLogger(NotifierProperties.class);
    private final GeneralComponentQuery gq = ComponentQueryFactory.getGeneralComponentQuery();
    
    private NotifierProperties() {
        load();
    }

    public static NotifierProperties getInstance() {
        if (INSTANCE == null)
            INSTANCE = new NotifierProperties();
        return INSTANCE;
    }
    
    /**
     * Loads a configuration file from the disk
     */
    public final void load() {
        try {
            String config_file = "notifiers.properties";
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
                List<PropertyNotifiers> all = gq.getAllNotifiersProperty();
                for (PropertyNotifiers n : all) {
                    boolean found = false;
                    for (Map.Entry pairs : entrySet()) {
                        String key = (String) pairs.getKey();
                        String value = (String) pairs.getValue();
                        if (n.getPropertyName().equals(key) && n.getPropertyValue().equals(value)) {
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
            String config_file = "notifiers.properties";
            EnvironmentalVariables ev = gq.getVariable("property.path");
            String prop_path = ev.getPath();
            logger.info("Reloading configuration file - {}", config_file);
            
            File propFile = new File(prop_path + config_file);
            propFile.delete();
            //if property file does not exist in designated location, create file using default file within system classpath
            if (!propFile.exists()) {
                propFile.getParentFile().mkdirs();

                instream = NotifierProperties.class.getClassLoader().getResourceAsStream(config_file);
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
            
            setProperty(AUTHORISER_NOTIFIER_QUEUE_NAME, gq.getNotifiersProperty(AUTHORISER_NOTIFIER_QUEUE_NAME).getPropertyValue());
            setProperty(NOTIFIER_QUEUE_FACTORY, gq.getNotifiersProperty(NOTIFIER_QUEUE_FACTORY).getPropertyValue());
            setProperty(EMAIL_NOTIFIER_QUEUE_NAME, gq.getNotifiersProperty(EMAIL_NOTIFIER_QUEUE_NAME).getPropertyValue());
            setProperty(REJECT_NOTIFIER_QUEUE_NAME, gq.getNotifiersProperty(REJECT_NOTIFIER_QUEUE_NAME).getPropertyValue());
            setProperty(INFORMATION_NOTIFIER_QUEUE_NAME, gq.getNotifiersProperty(INFORMATION_NOTIFIER_QUEUE_NAME).getPropertyValue());
            
            store(changestream, null);
            changestream.close();
        } catch (Exception ex) {
            logger.info("failed to reload configuration file - see error log");
            logger.error("error reloading email config file:", ex);
        }
    }
    
    /**
     * Loads the notifiers.properties file.
     * @param clz the class whose classloader will be used to load the notifiers properties file
     */
    /*public NotifierProperties(Class clz) {
    String config_file = "notifiers.properties";
    input = clz.getClassLoader().getResourceAsStream(config_file);
    logger.info("Loading configuration file - {}", config_file);
    try {
    load(input);
    close();
    } catch (IOException ex) {
    logger.info("failed to load configuration file - see error log");
    logger.error("error loading notifier config file:", ex);
    }
    }*/
    
    /**
     * Gets the queue name.
     * @return the authoriser notifier's queue name
     */
    public String getAuthoriserNotifierQueueName() {
        return getProperty(AUTHORISER_NOTIFIER_QUEUE_NAME);
    }
    
    /**
     * Gets the queue name.
     * @return the text message notifier queue name
     */
    public String getEmailNotifierQueueName() {
        return getProperty(EMAIL_NOTIFIER_QUEUE_NAME);
    }
    
    /**
     * Gets the queue name.
     * @return the reject notifier queue name
     */
    public String getRejectNotifierQueueName() {
        return getProperty(REJECT_NOTIFIER_QUEUE_NAME);
    }
    
    /**
     * Gets the queue name
     * @return the information notifier queue name
     */
    public String getInformationNotifierQueueName() {
        return getProperty(INFORMATION_NOTIFIER_QUEUE_NAME);
    }
    
    /**
     * Gets the queue factory.
     * @return the authoriser notifier's queue factory
     */
    public String getNotifierQueueFactory() {
        return getProperty(NOTIFIER_QUEUE_FACTORY);
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
            logger.error("error closing notifier config file input stream:", ex);
        }
    }
}
