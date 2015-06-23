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
import org.greenpole.entity.exception.ConfigNotFoundException;
import org.greenpole.hibernate.entity.EnvironmentalVariables;
import org.greenpole.hibernate.entity.PropertyQueueConfig;
import org.greenpole.hibernate.query.GeneralComponentQuery;
import org.greenpole.hibernate.query.factory.ComponentQueryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Akinwale Agbaje
 */
public class QueueConfigProperties extends Properties {
    private static QueueConfigProperties INSTANCE;
    private InputStream input;
    private static final Logger logger = LoggerFactory.getLogger(QueueConfigProperties.class);
    private final GeneralComponentQuery gq = ComponentQueryFactory.getGeneralComponentQuery();
    
    private QueueConfigProperties() {
        load();
    }

    public static QueueConfigProperties getInstance() {
        if (INSTANCE == null)
            INSTANCE = new QueueConfigProperties();
        return INSTANCE;
    }
    
    /**
     * Loads a configuration file from the disk
     */
    public final void load() {
        try {
            String config_file = "queue_config.properties";
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
                for (Map.Entry pairs : entrySet()) {
                    String key = (String) pairs.getKey();
                    List<PropertyQueueConfig> all = gq.getAllQueueConfigProperty();
                    boolean found = false;
                    for (PropertyQueueConfig q : all) {
                        if (key.equals(q.getPropertyName())) {
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
            String config_file = "queue_config.properties";
            EnvironmentalVariables ev = gq.getVariable("property.path");
            String prop_path = ev.getPath();
            logger.info("Reloading configuration file - {}", config_file);
            
            File defaultFile = new File(QueueConfigProperties.class.getClassLoader().getResource(config_file).getPath());
            File propFile = new File(prop_path + config_file);
            propFile.delete();
            //if property file does not exist in designated location, create file using default file within system classpath
            if (!propFile.exists()) {
                propFile.getParentFile().mkdirs();

                FileInputStream instream = new FileInputStream(defaultFile);
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
            
            setProperty("java.naming.factory.initial", gq.getQueueConfigProperty("java.naming.factory.initial").getPropertyValue());
            setProperty("java.naming.factory.url.pkgs", gq.getQueueConfigProperty("java.naming.factory.url.pkgs").getPropertyValue());
            setProperty("java.naming.provider.url", gq.getQueueConfigProperty("java.naming.provider.url").getPropertyValue());
            
            store(changestream, null);
            changestream.close();
        } catch (Exception ex) {
            logger.info("failed to reload configuration file - see error log");
            logger.error("error reloading email config file:", ex);
        }
    }
    
     /**
     * Loads the queue config.properties file.
     * @param clz the class whose classloader will be used to load the notifiers properties file
     */
    /*public QueueConfigProperties(Class clz) {
    String config_file = "queue_config.properties";
    input = clz.getClassLoader().getResourceAsStream(config_file);
    logger.info("Loading configuration file - {}", config_file);
    try {
    if (input == null) {
    logger.info("Failure to load configuration file - {}", config_file);
    throw new ConfigNotFoundException("queue_config.properties file missing from classpath");
    }
    load(input);
    close();
    } catch (IOException | ConfigNotFoundException ex) {
    logger.info("failed to load configuration file - see error log");
    logger.error("error loading notifier config file:", ex);
    }
    }*/
    
    /*
     * Close input stream.
     */
    private void close() {
        try {
            if (input != null)
                    input.close();
        } catch (IOException ex) {
            logger.info("failed to close configuration file input stream - see error log");
            logger.error("error closing notifier config file input stream:", ex);
        }
    }
}
