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
import org.greenpole.hibernate.entity.PropertyFtp;
import org.greenpole.hibernate.query.GeneralComponentQuery;
import org.greenpole.hibernate.query.factory.ComponentQueryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author akinwale.agbaje
 */
public class FtpProperties extends Properties {
    
    private static FtpProperties INSTANCE;
    private InputStream instream;
    private final String FTP_HOST = "ftp.host";
    private final String FTP_USERNAME = "ftp.username";
    private final String FTP_PASSWORD = "ftp.password";
    private final String FTP_PORT = "ftp.port";
    private static final Logger logger = LoggerFactory.getLogger(FtpProperties.class);
    private final GeneralComponentQuery gq = ComponentQueryFactory.getGeneralComponentQuery();
    
    private FtpProperties() {
        load();
    }

    public static FtpProperties getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FtpProperties();
        }
        return INSTANCE;
    }
    
    /**
     * Loads a configuration file from the disk
     */
    public final void load() {
        try {
            String config_file = "ftp.properties";
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
                List<PropertyFtp> all = gq.getAllFtpProperty();
                for (PropertyFtp g : all) {
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
            String config_file = "ftp.properties";
            EnvironmentalVariables ev = gq.getVariable("property.path");
            String prop_path = ev.getPath();
            logger.info("Reloading configuration file - {}", config_file);

            File propFile = new File(prop_path + config_file);
            propFile.delete();
            //if property file does not exist in designated location, create file using default file within system classpath
            if (!propFile.exists()) {
                propFile.getParentFile().mkdirs();

                instream = FtpProperties.class.getClassLoader().getResourceAsStream(config_file);
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
            
            setProperty(FTP_HOST, gq.getFtpProperty(FTP_HOST).getPropertyValue());
            setProperty(FTP_PORT, gq.getFtpProperty(FTP_PORT).getPropertyValue());
            setProperty(FTP_USERNAME, gq.getFtpProperty(FTP_USERNAME).getPropertyValue());
            setProperty(FTP_PASSWORD, gq.getFtpProperty(FTP_PASSWORD).getPropertyValue());

            store(changestream, null);
            changestream.close();
        } catch (Exception ex) {
            logger.info("failed to reload configuration file - see error log");
            logger.error("error reloading email config file:", ex);
        }
    }
    
    public String getFtpHost() {
        return getProperty(FTP_HOST);
    }
    
    public String getFtpPort() {
        return getProperty(FTP_PORT);
    }
    
    public String getFtpUsername() {
        return getProperty(FTP_USERNAME);
    }
    
    public String getFtpPassword() {
        return getProperty(FTP_PASSWORD);
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
