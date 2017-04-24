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
import org.greenpole.hibernate.entity.PropertyUba;
import org.greenpole.hibernate.query.GeneralComponentQuery;
import org.greenpole.hibernate.query.factory.ComponentQueryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author akinwale.agbaje
 */
public class UbaProperties extends Properties {
    
    private static UbaProperties INSTANCE;
    private InputStream instream;
    private final String UBA_PROVIDER_IP = "uba.provider.ip";
    private final String UBA_PROVIDER_USERNAME = "uba.provider.username";
    private final String UBA_PROVIDER_PASSWORD = "uba.provider.password";
    private final String UBA_ENDPOINT_NAME = "uba.endpoint.name";
    private final String UBA_DATE_FORMAT = "uba.date.format";
    private static final Logger logger = LoggerFactory.getLogger(UbaProperties.class);
    private final GeneralComponentQuery gq = ComponentQueryFactory.getGeneralComponentQuery();
    
    private UbaProperties() {
        load();
    }

    public static UbaProperties getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new UbaProperties();
        }
        return INSTANCE;
    }

    /**
     * Loads a configuration file from the disk
     */
    public final void load() {
        try {
            String config_file = "uba.properties";
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
                List<PropertyUba> all = gq.getAllUbaProperty();
                for (PropertyUba g : all) {
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
            String config_file = "uba.properties";
            EnvironmentalVariables ev = gq.getVariable("property.path");
            String prop_path = ev.getPath();
            logger.info("Reloading configuration file - {}", config_file);

            File propFile = new File(prop_path + config_file);
            propFile.delete();
            //if property file does not exist in designated location, create file using default file within system classpath
            if (!propFile.exists()) {
                propFile.getParentFile().mkdirs();

                instream = UbaProperties.class.getClassLoader().getResourceAsStream(config_file);
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
            
            setProperty(UBA_PROVIDER_IP, gq.getEngineProperty(UBA_PROVIDER_IP).getPropertyValue());
            setProperty(UBA_PROVIDER_USERNAME, gq.getEngineProperty(UBA_PROVIDER_USERNAME).getPropertyValue());
            setProperty(UBA_PROVIDER_PASSWORD, gq.getEngineProperty(UBA_PROVIDER_PASSWORD).getPropertyValue());
            setProperty(UBA_ENDPOINT_NAME, gq.getEngineProperty(UBA_ENDPOINT_NAME).getPropertyValue());
            setProperty(UBA_DATE_FORMAT, gq.getEngineProperty(UBA_DATE_FORMAT).getPropertyValue());

            store(changestream, null);
            changestream.close();
        } catch (Exception ex) {
            logger.info("failed to reload configuration file - see error log");
            logger.error("error reloading email config file:", ex);
        }
    }
    
    public String getUbaProviderIp() {
        return getProperty(UBA_PROVIDER_IP);
    }

    public String getUbaProviderUsername() {
        return getProperty(UBA_PROVIDER_USERNAME);
    }

    public String getUbaProviderPassword() {
        return getProperty(UBA_PROVIDER_PASSWORD);
    }

    public String getUbaEndpointName() {
        return getProperty(UBA_ENDPOINT_NAME);
    }

    public String getUbaDateFormat() {
        return getProperty(UBA_DATE_FORMAT);
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
            logger.error("error closing greenpole config file input stream:", ex);
        }
    }
}
