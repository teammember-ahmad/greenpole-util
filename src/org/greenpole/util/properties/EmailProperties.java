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
import org.greenpole.hibernate.entity.PropertyEmail;
import org.greenpole.hibernate.query.GeneralComponentQuery;
import org.greenpole.hibernate.query.factory.ComponentQueryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Akinwale.Agbaje
 * Properties loaded from the email.properties file.
 */
public class EmailProperties extends Properties {
    private InputStream input;
    private static EmailProperties INSTANCE;
    private final String MAIL_TRANSPORT_PROTOCOL = "mail.transport.protocol";
    private final String MAIL_HOST = "mail.host";
    private final String MAIL_SMTP_PORT = "mail.smtp.port";
    private final String MAIL_SMTP_AUTH = "mail.smtp.auth";
    private final String MAIL_SMTP_STARTTLS_ENABLE = "mail.smtp.starttls.enable";
    private final String MAIL_USER = "mail.user";
    private final String MAIL_PASSWORD = "mail.password";
    private final String MAIL_SENDER = "mail.sender";
    private final String AUTHORISATION_MAIL_TEMPLATE = "authorisation.mail.template";
    private final String WARNING_MAIL_TEMPLATE = "warning.mail.template";
    private static final Logger logger = LoggerFactory.getLogger(EmailProperties.class);
    private final GeneralComponentQuery gq = ComponentQueryFactory.getGeneralComponentQuery();
    
    private EmailProperties() {
        load();
    }

    public static EmailProperties getInstance() {
        if (INSTANCE == null)
            INSTANCE = new EmailProperties();
        return INSTANCE;
    }
    
    /**
     * Loads a configuration file from the disk
     */
    public final void load() {
        try {
            String config_file = "email.properties";
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
                    List<PropertyEmail> all = gq.getAllEmailProperty();
                    boolean found = false;
                    for (PropertyEmail e : all) {
                        if (key.equals(e.getPropertyName())) {
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
            String config_file = "email.properties";
            EnvironmentalVariables ev = gq.getVariable("property.path");
            String prop_path = ev.getPath();
            logger.info("Reloading configuration file - {}", config_file);
            
            File defaultFile = new File(EmailProperties.class.getClassLoader().getResource(config_file).getPath());
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
            
            setProperty(MAIL_TRANSPORT_PROTOCOL, gq.getEmailProperty(MAIL_TRANSPORT_PROTOCOL).getPropertyValue());
            setProperty(MAIL_HOST, gq.getEmailProperty(MAIL_HOST).getPropertyValue());
            setProperty(MAIL_SMTP_PORT, gq.getEmailProperty(MAIL_SMTP_PORT).getPropertyValue());
            setProperty(MAIL_SMTP_AUTH, gq.getEmailProperty(MAIL_SMTP_AUTH).getPropertyValue());
            setProperty(MAIL_SMTP_STARTTLS_ENABLE, gq.getEmailProperty(MAIL_SMTP_STARTTLS_ENABLE).getPropertyValue());
            setProperty(MAIL_USER, gq.getEmailProperty(MAIL_USER).getPropertyValue());
            setProperty(MAIL_PASSWORD, gq.getEmailProperty(MAIL_PASSWORD).getPropertyValue());
            setProperty(MAIL_SENDER, gq.getEmailProperty(MAIL_SENDER).getPropertyValue());
            setProperty(AUTHORISATION_MAIL_TEMPLATE, gq.getEmailProperty(AUTHORISATION_MAIL_TEMPLATE).getPropertyValue());
            setProperty(WARNING_MAIL_TEMPLATE, gq.getEmailProperty(WARNING_MAIL_TEMPLATE).getPropertyValue());
            
            store(changestream, null);
            changestream.close();
        } catch (Exception ex) {
            logger.info("failed to reload configuration file - see error log");
            logger.error("error reloading email config file:", ex);
        }
    }
    
    /**
     * Loads the email.properties file.
     * @param clz the class whose classloader will be used to load the email properties file
     */
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
    
    /**
     * Gets the mail transport protocol
     * @return the mail transport protocol
     */
    public String getMailTransportProtocol() {
        return getProperty(MAIL_TRANSPORT_PROTOCOL);
    }
    
    /**
     * Gets the mail host.
     * @return the mail host
     */
    public String getMailHost() {
        return getProperty(MAIL_HOST);
    }
    
    /**
     * Gets the mail smtp port.
     * @return the mail smtp port
     */
    public String getMailSmtpPort() {
        return getProperty(MAIL_SMTP_PORT);
    }
    
    /**
     * Gets the mail smtp authentication enable value.
     * @return the mail smtp authentication enable value
     */
    public String getMailSmtpAuth() {
        return getProperty(MAIL_SMTP_AUTH);
    }
    
    /**
     * Gets the mail smtp start-tls enable value.
     * @return the mail smtp start-tls enable value
     */
    public String getMailSmtpStarttlsEnable() {
        return getProperty(MAIL_SMTP_STARTTLS_ENABLE);
    }
    
    /**
     * Gets the username of the mail sender.
     * @return the mail user
     */
    public String getMailUser() {
        return getProperty(MAIL_USER);
    }
    
    /**
     * Gets the password of the mail sender.
     * @return the mail password
     */
    public String getMailPassword() {
        return getProperty(MAIL_PASSWORD);
    }
    
    /**
     * Gets the default sender's email address.
     * @return the sender's email address
     */
    public String getMailSender() {
        return getProperty(MAIL_SENDER);
    }
    
    /**
     * Gets the default authorisation email template location.
     * @return the email authorisation template location
     */
    public String getAuthorisationMailTemplate() {
        return getProperty(AUTHORISATION_MAIL_TEMPLATE);
    }
    
    /**
     * Gets the default warning email template location.
     * @return the email warning template location
     */
    public String getWarningMailTemplate() {
        return getProperty(WARNING_MAIL_TEMPLATE);
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
            logger.error("error closing email config file input stream:", ex);
        }
    }
}
