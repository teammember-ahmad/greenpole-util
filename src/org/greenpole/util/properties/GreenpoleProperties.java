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
import org.greenpole.hibernate.entity.PropertyGreenpoleEngine;
import org.greenpole.hibernate.query.GeneralComponentQuery;
import org.greenpole.hibernate.query.factory.ComponentQueryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Akin
 * Properties loaded from the greenpole_engine.properties file.
 */
public class GreenpoleProperties extends Properties {
    private static GreenpoleProperties INSTANCE;
    private InputStream instream;
    private final String DATE_FORMAT = "date.format";
    private final String HOLDER_SIGNATURE_PATH = "holder.signature.dir";
    private final String POWER_OF_ATTORNEY_PATH = "holder.powerofattorney.dir";
    private final String ATTORNEY_SIZE = "attorney.size";
    private final String SIGNATURE_SIZE = "signature.size";
    private final String REGISTRAR_CODE = "registrar.code";
    private final String WITHHOLDING_TAX = "withholding.tax";
    private final String SYSTEM_ADMIN = "system.admin";
    private final String TEXT_MONITOR_LOCATION = "text.monitor.location";
    private final String EMAIL_MONITOR_LOCATION = "email.monitor.location";
    private final String RIGHTS_DISTRIBUTOR_RECIPIENT = "rights.distributor.recipient";
    private final String BONUS_DISTRIBUTOR_RECIPIENT = "bonus.distributor.recipient";
    private final String DIVIDEND_DISTRIBUTOR_RECIPIENT = "dividend.distributor.recipient";
    private final String Dividend_Notification_Recipient = "dividend.notification.recipient";
    private final String Dividend_Monitor_Recipient = "dividend.monitor.recipient";
    private static final Logger logger = LoggerFactory.getLogger(GreenpoleProperties.class);
    private final GeneralComponentQuery gq = ComponentQueryFactory.getGeneralComponentQuery();
    
    private GreenpoleProperties() {
        load();
    }

    public static GreenpoleProperties getInstance() {
        if (INSTANCE == null)
            INSTANCE = new GreenpoleProperties();
        return INSTANCE;
    }
    
    /**
     * Loads a configuration file from the disk
     */
    public final void load() {
        try {
            String config_file = "greenpole_engine.properties";
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
                List<PropertyGreenpoleEngine> all = gq.getAllEngineProperty();
                for (PropertyGreenpoleEngine g : all) {
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
     * Reloads a configuration file, getting all necessary variables from the database.
     */
    public final void reload() {
        try {
            String config_file = "greenpole_engine.properties";
            EnvironmentalVariables ev = gq.getVariable("property.path");
            String prop_path = ev.getPath();
            logger.info("Reloading configuration file - {}", config_file);
            
            File propFile = new File(prop_path + config_file);
            propFile.delete();
            //if property file does not exist in designated location, create file using default file within system classpath
            if (!propFile.exists()) {
                propFile.getParentFile().mkdirs();

                instream = GreenpoleProperties.class.getClassLoader().getResourceAsStream(config_file);
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
            
            setProperty(DATE_FORMAT, gq.getEngineProperty(DATE_FORMAT).getPropertyValue());
            setProperty(HOLDER_SIGNATURE_PATH, gq.getEngineProperty(HOLDER_SIGNATURE_PATH).getPropertyValue());
            setProperty(POWER_OF_ATTORNEY_PATH, gq.getEngineProperty(POWER_OF_ATTORNEY_PATH).getPropertyValue());
            setProperty(ATTORNEY_SIZE, gq.getEngineProperty(ATTORNEY_SIZE).getPropertyValue());
            setProperty(SIGNATURE_SIZE, gq.getEngineProperty(SIGNATURE_SIZE).getPropertyValue());
            setProperty(REGISTRAR_CODE, gq.getEngineProperty(REGISTRAR_CODE).getPropertyValue());
            setProperty(WITHHOLDING_TAX, gq.getEngineProperty(WITHHOLDING_TAX).getPropertyValue());
            setProperty(SYSTEM_ADMIN, gq.getEngineProperty(SYSTEM_ADMIN).getPropertyValue());
            setProperty(TEXT_MONITOR_LOCATION, gq.getEngineProperty(TEXT_MONITOR_LOCATION).getPropertyValue());
            setProperty(EMAIL_MONITOR_LOCATION, gq.getEngineProperty(EMAIL_MONITOR_LOCATION).getPropertyValue());
            setProperty(RIGHTS_DISTRIBUTOR_RECIPIENT, gq.getEngineProperty(RIGHTS_DISTRIBUTOR_RECIPIENT).getPropertyValue());
            setProperty(BONUS_DISTRIBUTOR_RECIPIENT, gq.getEngineProperty(BONUS_DISTRIBUTOR_RECIPIENT).getPropertyValue());
            setProperty(DIVIDEND_DISTRIBUTOR_RECIPIENT, "");
            setProperty(Dividend_Notification_Recipient, "");
            setProperty(Dividend_Monitor_Recipient, "");
            
            store(changestream, null);
            changestream.close();
        } catch (Exception ex) {
            logger.info("failed to reload configuration file - see error log");
            logger.error("error reloading email config file:", ex);
        }
    }
    
    /**
     * Loads the greenpole_engine.properties file.
     * @param clz the class whose classloader will be used to load the greenpole engine properties file
     */
    /*public GreenpoleProperties(Class clz) {
    String config_file = "greenpole_engine.properties";
    input = clz.getClassLoader().getResourceAsStream(config_file);
    logger.info("Loading configuration file - {}", config_file);
    try {
    load(input);
    close();
    } catch (IOException ex) {
    logger.info("failed to load configuration file - see error log");
    logger.error("error loading greenpole engine config file:", ex);
    }
    }*/
    
    /**
     * Gets the date format.
     * @return the date format
     */
    public String getDateFormat() {
        return getProperty(DATE_FORMAT);
    }
    
    /**
     * Gets the signature path.
     * @return Gets the signature path
     */
    public String getSignaturePath() {
        return getProperty(HOLDER_SIGNATURE_PATH);
    }
    
    /**
     * Gets the power of attorney path.
     * @return the power of attorney path
     */
    public String getPowerOfAttorneyPath() {
        return getProperty(POWER_OF_ATTORNEY_PATH);
    }
    
    /**
     * Gets the default size of the power of attorney.
     * @return the default size of the power of attorney
     */
    public String getPowerOfAttorneySize() {
        return getProperty(ATTORNEY_SIZE);
    }
    
    /**
     * Gets the default size of the signature.
     * @return the default size of the signature
     */
    public String getSignatureSize() {
        return getProperty(SIGNATURE_SIZE);
    }
    
    /**
     * Gets the registrar's code.
     * @return the registrar's code
     */
    public String getRegistarCode() {
        return getProperty(REGISTRAR_CODE);
    }
    
    /**
     * Gets the withholding tax.
     * @return the withholding tax
     */
    public String getWithholding() {
        return getProperty(WITHHOLDING_TAX);
    }
    
    public String getSystemAdmin() {
        return getProperty(SYSTEM_ADMIN);
    }
    
    public String getTextMonitorLocation() {
        return getProperty(TEXT_MONITOR_LOCATION);
    }
    
    public String getEmailMonitorLocation() {
        return getProperty(EMAIL_MONITOR_LOCATION);
    }
    
    public String getRightsDistributorRecipient() {
        return getProperty(RIGHTS_DISTRIBUTOR_RECIPIENT);
    }
    
    public String getBonusDistributorRecipient() {
        return getProperty(BONUS_DISTRIBUTOR_RECIPIENT);
    }
  
    
    public String getDIVIDENDDISTRIBUTORRECIPIENT() {
        return getProperty(DIVIDEND_DISTRIBUTOR_RECIPIENT);
    }

    public String getDividendNotificationRecipient() {
        return getProperty(Dividend_Notification_Recipient);
    }

    public String getDividendMonitorRecipient() {
        return getProperty(Dividend_Monitor_Recipient);
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
            logger.error("error closing greenpole config file input stream:", ex);
        }
    }
}
