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
 * @author Akinwale.Agbaje
 * Properties loaded from the email.properties file.
 */
public class EmailProperties extends Properties {
    private final String MAIL_TRANSPORT_PROTOCOL = "mail.transport.protocol";
    private final String MAIL_HOST = "mail.host";
    private final String MAIL_SMTP_PORT = "mail.smtp.port";
    private final String MAIL_SMTP_AUTH = "mail.smtp.auth";
    private final String MAIL_SMTP_STARTTLS_ENABLE = "mail.smtp.starttls.enable";
    private final String MAIL_USER = "mail.user";
    private final String MAIL_PASSWORD = "mail.password";
    private final String MAIL_SENDER = "mail.sender";
    private final String MAIL_TEMPLATE = "mail.template";
    private static final Logger logger = LoggerFactory.getLogger(EmailProperties.class);

    /**
     * Loads the email.properties file.
     * @param clz the class whose classloader will be used to load the email properties file
     */
    public EmailProperties(Class clz) {
        String config_file = "email.properties";
        InputStream input = clz.getClassLoader().getResourceAsStream(config_file);
        logger.info("Loading configuration file - {}", config_file);
        try {
            load(input);
        } catch (IOException ex) {
            logger.info("failed to load configuration file - see error log");
            logger.error("error loading email config file:", ex);
        }
    }
    
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
     * Gets the default email template.
     * @return the email template
     */
    public String getMailTemplate() {
        return getProperty(MAIL_TEMPLATE);
    }
}
