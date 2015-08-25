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
import org.greenpole.hibernate.entity.PropertySms;
import org.greenpole.hibernate.query.GeneralComponentQuery;
import org.greenpole.hibernate.query.factory.ComponentQueryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Akinwale Agbaje
 */
public class SMSProperties extends Properties {
    private static SMSProperties INSTANCE;
    private InputStream instream;
    private final String SMS_API_USERNAME = "sms.api.username";
    private final String SMS_API_PASSWORD = "sms.api.password";
    private final String TEXT_MERGE = "text.merge";
    private final String TEXT_CHANGE_ADDRESS = "text.change.address";
    private final String TEXT_CHANGE_NAME = "text.change.name";
    private final String TEXT_CHANGE_CHN = "text.change.chn";
    private final String TEXT_RATE = "text.rate";
    private final String TEXT_MERGE_SEND = "text.merge.send";
    private final String TEXT_CHANGE_ADDRESS_SEND = "text.change.address.send";
    private final String TEXT_CHANGE_NAME_SEND = "text.change.name.send";
    private final String TEXT_CHANGE_CHN_SEND = "text.change.chn.send";
    private final String TEXT_IPO_PROCESSING = "text.ipo.processing";
    private final String TEXT_IPO_PAYMENT_SUCCESS = "text.ipo.payment.success";
    private final String TEXT_IPO_PAYMENT_FAILURE  = "text.ipo.payment.failure";
    private final String TEXT_PLACEMENT_PROCESSING = "text.placement.processing";
    private final String TEXT_PLACEMENT_PAYMENT_SUCCESS  = "text.placement.payment.success";
    private final String TEXT_PLACEMENT_PAYMENT_FAILURE  = "text.placement.payment.failure";
    private final String TEXT_RIGHTS_PROCESSING = "text.rights.processing";
    private final String TEXT_RIGHTS_PAYMENT_SUCCESS  = "text.rights.payment.success";
    private final String TEXT_RIGHTS_PAYMENT_FAILURE = "text.rights.payment.failure";
    private final String TEXT_IPO_SEND = "text.ipo.send";
    private final String TEXT_RIGHTS_SEND = "text.rights.send";
    private final String TEXT_PLACEMENT_SEND = "text.placement.send";
    private final String TEXT_IPO_CANCEL_PROCESSING = "text.ipo.cancel.processing";
    private final String TEXT_IPO_CANCEL_CONFIRM = "text.ipo.cancel.confirm";
    private final String TEXT_PLACEMENT_CANCEL_PROCESSING = "text.placement.cancel.processing";
    private final String TEXT_PLACEMENT_CANCEL_CONFIRM = "text.placement.cancel.confirm";
    private final String TEXT_RIGHTS_CANCEL_PROCESSING = "text.rights.cancel.processing";
    private final String TEXT_RIGHTS_CANCEL_CONFIRM = "text.rights.cancel.confirm";
    
    private static final Logger logger = LoggerFactory.getLogger(SMSProperties.class);
    private final GeneralComponentQuery gq = ComponentQueryFactory.getGeneralComponentQuery();
    
    private SMSProperties() {
        load();
    }

    public static SMSProperties getInstance() {
        if (INSTANCE == null)
            INSTANCE = new SMSProperties();
        return INSTANCE;
    }
    
    /**
     * Loads a configuration file from the disk
     */
    public final void load() {
        try {
            String config_file = "sms.properties";
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
                List<PropertySms> all = gq.getAllSmsProperty();
                for (PropertySms s : all) {
                    boolean found = false;
                    for (Map.Entry pairs : entrySet()) {
                        String key = (String) pairs.getKey();
                        String value = (String) pairs.getValue();
                        if (s.getPropertyName().equals(key) && s.getPropertyValue().equals(value)) {
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
            String config_file = "sms.properties";
            EnvironmentalVariables ev = gq.getVariable("property.path");
            String prop_path = ev.getPath();
            logger.info("Reloading configuration file - {}", config_file);
            
            File propFile = new File(prop_path + config_file);
            propFile.delete();
            //if property file does not exist in designated location, create file using default file within system classpath
            if (!propFile.exists()) {
                propFile.getParentFile().mkdirs();

                instream = SMSProperties.class.getClassLoader().getResourceAsStream(config_file);
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
            
            setProperty(SMS_API_USERNAME, gq.getSmsProperty(SMS_API_USERNAME).getPropertyValue());
            setProperty(SMS_API_PASSWORD, gq.getSmsProperty(SMS_API_PASSWORD).getPropertyValue());
            setProperty(TEXT_MERGE, gq.getSmsProperty(TEXT_MERGE).getPropertyValue());
            setProperty(TEXT_CHANGE_ADDRESS, gq.getSmsProperty(TEXT_CHANGE_ADDRESS).getPropertyValue());
            setProperty(TEXT_CHANGE_NAME, gq.getSmsProperty(TEXT_CHANGE_NAME).getPropertyValue());
            setProperty(TEXT_CHANGE_CHN, gq.getSmsProperty(TEXT_CHANGE_CHN).getPropertyValue());
            setProperty(TEXT_RATE, gq.getSmsProperty(TEXT_RATE).getPropertyValue());
            setProperty(TEXT_MERGE_SEND, gq.getSmsProperty(TEXT_MERGE_SEND).getPropertyValue());
            setProperty(TEXT_CHANGE_ADDRESS_SEND, gq.getSmsProperty(TEXT_CHANGE_ADDRESS_SEND).getPropertyValue());
            setProperty(TEXT_CHANGE_NAME_SEND, gq.getSmsProperty(TEXT_CHANGE_NAME_SEND).getPropertyValue());
            setProperty(TEXT_CHANGE_CHN_SEND, gq.getSmsProperty(TEXT_CHANGE_CHN_SEND).getPropertyValue());
            setProperty(TEXT_IPO_PROCESSING, gq.getSmsProperty(TEXT_IPO_PROCESSING).getPropertyValue());
            setProperty(TEXT_IPO_PAYMENT_SUCCESS, gq.getSmsProperty(TEXT_IPO_PAYMENT_SUCCESS).getPropertyValue());
            setProperty(TEXT_IPO_PAYMENT_FAILURE, gq.getSmsProperty(TEXT_IPO_PAYMENT_FAILURE).getPropertyValue());
            setProperty(TEXT_PLACEMENT_PROCESSING, gq.getSmsProperty(TEXT_PLACEMENT_PROCESSING).getPropertyValue());
            setProperty(TEXT_PLACEMENT_PAYMENT_SUCCESS, gq.getSmsProperty(TEXT_PLACEMENT_PAYMENT_SUCCESS).getPropertyValue());
            setProperty(TEXT_PLACEMENT_PAYMENT_FAILURE, gq.getSmsProperty(TEXT_PLACEMENT_PAYMENT_FAILURE).getPropertyValue());
            setProperty(TEXT_RIGHTS_PROCESSING, gq.getSmsProperty(TEXT_RIGHTS_PROCESSING).getPropertyValue());
            setProperty(TEXT_RIGHTS_PAYMENT_SUCCESS, gq.getSmsProperty(TEXT_RIGHTS_PAYMENT_SUCCESS).getPropertyValue());
            setProperty(TEXT_RIGHTS_PAYMENT_FAILURE, gq.getSmsProperty(TEXT_RIGHTS_PAYMENT_FAILURE).getPropertyValue());
            setProperty(TEXT_IPO_SEND, gq.getSmsProperty(TEXT_IPO_SEND).getPropertyValue());
            setProperty(TEXT_PLACEMENT_SEND, gq.getSmsProperty(TEXT_PLACEMENT_SEND).getPropertyValue());
            setProperty(TEXT_RIGHTS_SEND, gq.getSmsProperty(TEXT_RIGHTS_SEND).getPropertyValue());
            setProperty(TEXT_IPO_CANCEL_PROCESSING, gq.getSmsProperty(TEXT_IPO_CANCEL_PROCESSING).getPropertyValue());
            setProperty(TEXT_IPO_CANCEL_CONFIRM, gq.getSmsProperty(TEXT_IPO_CANCEL_CONFIRM).getPropertyValue());
            setProperty(TEXT_PLACEMENT_CANCEL_PROCESSING, gq.getSmsProperty(TEXT_PLACEMENT_CANCEL_PROCESSING).getPropertyValue());
            setProperty(TEXT_PLACEMENT_CANCEL_CONFIRM, gq.getSmsProperty(TEXT_PLACEMENT_CANCEL_CONFIRM).getPropertyValue());
            setProperty(TEXT_RIGHTS_CANCEL_PROCESSING, gq.getSmsProperty(TEXT_RIGHTS_CANCEL_PROCESSING).getPropertyValue());
            setProperty(TEXT_RIGHTS_CANCEL_CONFIRM, gq.getSmsProperty(TEXT_RIGHTS_CANCEL_CONFIRM).getPropertyValue());
            
            store(changestream, null);
            changestream.close();
        } catch (Exception ex) {
            logger.info("failed to reload configuration file - see error log");
            logger.error("error reloading email config file:", ex);
        }
    }

    /**
     * Loads the sms.properties file.
     * @param clz the class whose classloader will be used to load the sms properties file
     */
    /*public SMSProperties(Class clz) {
    String config_file = "sms.properties";
    input = clz.getClassLoader().getResourceAsStream(config_file);
    logger.info("Loading configuration file - {}", config_file);
    try {
    load(input);
    close();
    } catch (IOException ex) {
    logger.info("failed to load configuration file - see error log");
    logger.error("error loading sms config file:", ex);
    }
    }*/
    
    /**
     * Gets the sms api username.
     * @return the sms api username
     */
    public String getAPIUsername() {
        return getProperty(SMS_API_USERNAME);
    }
    
    /**
     * Gets the sms api password.
     * @return the sms api password
     */
    public String getAPIPassword() {
        return getProperty(SMS_API_PASSWORD);
    }
    
    /**
     * Gets the merge text.
     * @return the merge text
     */
    public String getTextMerge() {
        return getProperty(TEXT_MERGE);
    }
    
    /**
     * Gets the change address text.
     * @return the change address text
     */
    public String getTextChangeAddress() {
        return getProperty(TEXT_CHANGE_ADDRESS);
    }
    
    /**
     * Gets the change name text.
     * @return the change name text
     */
    public String getTextChangeName() {
        return getProperty(TEXT_CHANGE_NAME);
    }
    
    /**
     * Gets the change chn text.
     * @return the change chn text
     */
    public String getTextChangeChn() {
        return getProperty(TEXT_CHANGE_CHN);
    }
    
    /**
     * Gets the price of a text.
     * @return the price of a text
     */
    public String getTextRate() {
        return getProperty(TEXT_RATE);
    }
    
    /**
     * Gets the merge text send status.
     * @return the merge text
     */
    public String getTextMergeSend() {
        return getProperty(TEXT_MERGE_SEND);
    }
    
    /**
     * Gets the change address text send status.
     * @return the change address text
     */
    public String getTextChangeAddressSend() {
        return getProperty(TEXT_CHANGE_ADDRESS_SEND);
    }
    
    /**
     * Gets the change name text send status.
     * @return the change name text
     */
    public String getTextChangeNameSend() {
        return getProperty(TEXT_CHANGE_NAME_SEND);
    }
    
    /**
     * Gets the change chn text send status.
     * @return the change chn text
     */
    public String getTextChangeChnSend() {
        return getProperty(TEXT_CHANGE_CHN_SEND);
    }
    
    public String getTextIpoProcessing() {
        return getProperty(TEXT_IPO_PROCESSING);
    }
    
    public String getTextIpoPaymentSuccess() {
        return getProperty(TEXT_IPO_PAYMENT_SUCCESS);
    }
    
    public String getTextIpoPaymentFailure() {
        return getProperty(TEXT_IPO_PAYMENT_FAILURE);
    }
    
    public String getTextPlacementProcessing() {
        return getProperty(TEXT_PLACEMENT_PROCESSING);
    }
    
    public String getTextPlacementPaymentSuccess() {
        return getProperty(TEXT_PLACEMENT_PAYMENT_SUCCESS);
    }
    
    public String getTextPlacementPaymentFailure() {
        return getProperty(TEXT_PLACEMENT_PAYMENT_FAILURE);
    }
    
    public String getTextRightsProcessing() {
        return getProperty(TEXT_RIGHTS_PROCESSING);
    }
    
    public String getTextRightsPaymentSuccess() {
        return getProperty(TEXT_RIGHTS_PAYMENT_SUCCESS);
    }
    
    public String getTextRightsPaymentFailure() {
        return getProperty(TEXT_RIGHTS_PAYMENT_FAILURE);
    }
    
    public String getTextIpoSend() {
        return getProperty(TEXT_IPO_SEND);
    }
    
    public String getTextRightsSend() {
        return getProperty(TEXT_RIGHTS_SEND);
    }
    
    public String getTextPlacementSend() {
        return getProperty(TEXT_PLACEMENT_SEND);
    }
    
    public String getTextIpoCancelProcessing() {
        return getProperty(TEXT_IPO_CANCEL_PROCESSING);
    }
    
    public String getTextIpoCancelConfirm() {
        return getProperty(TEXT_IPO_CANCEL_CONFIRM);
    }
    
    public String getTextPlacementCancelProcessing() {
        return getProperty(TEXT_PLACEMENT_CANCEL_PROCESSING);
    }
    
    public String getTextPlacementCancelConfirm() {
        return getProperty(TEXT_PLACEMENT_CANCEL_CONFIRM);
    }
    
    public String getTextRightsCancelProcessing() {
        return getProperty(TEXT_RIGHTS_CANCEL_PROCESSING);
    }
    
    public String getTextRightsCancelConfirm() {
        return getProperty(TEXT_RIGHTS_CANCEL_CONFIRM);
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
            logger.error("error closing sms config file input stream:", ex);
        }
    }
    
    
    
}
