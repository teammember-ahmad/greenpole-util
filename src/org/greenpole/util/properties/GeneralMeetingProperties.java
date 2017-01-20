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
import org.greenpole.hibernate.entity.PropertyGm;
import org.greenpole.hibernate.query.GeneralComponentQuery;
import org.greenpole.hibernate.query.factory.ComponentQueryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Akinwale.Agbaje Properties loaded from the gm.properties file
 */
public class GeneralMeetingProperties extends Properties {
    private InputStream instream;
    private static GeneralMeetingProperties INSTANCE;
    private final String GM_WEBSERVICE_HOST = "gm.webservice.host";

    private static final Logger logger = LoggerFactory.getLogger(GeneralMeetingProperties.class);
    private final GeneralComponentQuery gq = ComponentQueryFactory.getGeneralComponentQuery();

    private GeneralMeetingProperties() {
        load();
    }

    public static GeneralMeetingProperties getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GeneralMeetingProperties();
        }
        return INSTANCE;
    }

    /**
     * Loads a configuration file from the disk
     */
    public final void load() {
        try {
            String config_file = "gm.properties";
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
                List<PropertyGm> all = gq.getAllGmProperty();
                for (PropertyGm g : all) {
                    boolean found = false;
                    for (Map.Entry pairs : entrySet()) {
                        String key = (String) pairs.getKey();
                        String value = (String) pairs.getValue();
                        if (g.getPropertyName().equals(key) && g.getPropertyValue().equals(value)) {
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
     * Reloads a configuration file, getting all necessary variables from the
     * database.
     */
    public final void reload() {
        try {
            String config_file = "gm.properties";
            EnvironmentalVariables ev = gq.getVariable("property.path");
            String prop_path = ev.getPath();
            logger.info("Reloading configuration file - {}", config_file);

            File propFile = new File(prop_path + config_file);
            propFile.delete();
            //if property file does not exist in designated location, create file using default file within system classpath
            if (!propFile.exists()) {
                propFile.getParentFile().mkdirs();

                instream = GeneralMeetingProperties.class.getClassLoader().getResourceAsStream(config_file);
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

            setProperty(GM_WEBSERVICE_HOST, gq.getEmailProperty(GM_WEBSERVICE_HOST).getPropertyValue());
            
            store(changestream, null);
            changestream.close();
        } catch (Exception ex) {
            logger.info("failed to reload configuration file - see error log");
            logger.error("error reloading email config file:", ex);
        }
    }
    
    /*public EmailProperties(Class clz) {
     String config_file = "email.properties";
     input = clz.getClassLoader().getResourceAsStream(config_file);
     logger.info("Loading configuration file - {}", config_file);
     try {
     load(input);
     close();
     } catch (IOException ex) {
     logger.info("failed to load configuration file - see error log");
     logger.error("error loading email config file:", ex);
     }
     }*/
    
    public String getGmWebserviceHost() {
        return getProperty(GM_WEBSERVICE_HOST);
    }

    /**
     * Close input stream.
     */
    private void close() {
        try {
            if (instream != null) {
                instream.close();
            }
        } catch (IOException ex) {
            logger.info("failed to close configuration file input stream - see error log");
            logger.error("error closing email config file input stream:", ex);
        }
    }
}
