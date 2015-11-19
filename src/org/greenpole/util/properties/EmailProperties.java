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
 * @author Akinwale.Agbaje Properties loaded from the email.properties file.
 */
public class EmailProperties extends Properties {

    private InputStream instream;
    private static EmailProperties INSTANCE;
    private final String MAIL_TRANSPORT_PROTOCOL = "mail.transport.protocol";
    private final String MAIL_HOST = "mail.host";
    private final String MAIL_SMTP_PORT = "mail.smtp.port";
    private final String MAIL_SMTP_AUTH = "mail.smtp.auth";
    private final String MAIL_SMTP_STARTTLS_ENABLE = "mail.smtp.starttls.enable";
    private final String MAIL_USER = "mail.user";
    private final String MAIL_PASSWORD = "mail.password";
    private final String INTERNAL_MAIL_USER = "internal.mail.user";
    private final String INTERNAL_MAIL_PASSWORD = "internal.mail.password";
    private final String EXTERNAL_MAIL_USER = "external.mail.user";
    private final String EXTERNAL_MAIL_PASSWORD = "external.mail.password";
    private final String AUTHORISATION_MAIL_TEMPLATE = "authorisation.mail.template";
    private final String WARNING_MAIL_TEMPLATE = "warning.mail.template";
    private final String INFORMATION_MAIL_TEMPLATE = "information.mail.template";
    private final String EMAIL_MERGE = "email.merge";
    private final String EMAIL_CHANGE_ADDRESS = "email.change.address";
    private final String EMAIL_CHANGE_NAME = "email.change.name";
    private final String EMAIL_CHANGE_CHN = "email.change.chn";
    private final String EMAIL_MERGE_SEND = "email.merge.send";
    private final String EMAIL_CHANGE_ADDRESS_SEND = "email.change.address.send";
    private final String EMAIL_CHANGE_NAME_SEND = "email.change.name.send";
    private final String EMAIL_CHANGE_CHN_SEND = "email.change.chn.send";
    private final String EMAIL_IPO_PROCESSING = "email.ipo.processing";
    private final String EMAIL_IPO_PAYMENT_SUCCESS = "email.ipo.payment.success";
    private final String EMAIL_IPO_PAYMENT_FAILURE = "email.ipo.payment.failure";
    private final String EMAIL_PLACEMENT_PROCESSING = "email.placement.processing";
    private final String EMAIL_PLACEMENT_PAYMENT_SUCCESS = "email.placement.payment.success";
    private final String EMAIL_PLACEMENT_PAYMENT_FAILURE = "email.placement.payment.failure";
    private final String EMAIL_RIGHTS_PROCESSING = "email.rights.processing";
    private final String EMAIL_RIGHTS_PAYMENT_SUCCESS = "email.rights.payment.success";
    private final String EMAIL_RIGHTS_PAYMENT_FAILURE = "email.rights.payment.failure";
    private final String EMAIL_IPO_SEND = "email.ipo.send";
    private final String EMAIL_RIGHTS_SEND = "email.rights.send";
    private final String EMAIL_PLACEMENT_SEND = "email.placement.send";
    private final String EMAIL_IPO_CANCEL_PROCESSING = "email.ipo.cancel.processing";
    private final String EMAIL_IPO_CANCEL_CONFIRM = "email.ipo.cancel.confirm";
    private final String EMAIL_PLACEMENT_CANCEL_PROCESSING = "email.placement.cancel.processing";
    private final String EMAIL_PLACEMENT_CANCEL_CONFIRM = "email.placement.cancel.confirm";
    private final String EMAIL_RIGHTS_CANCEL_PROCESSING = "email.rights.cancel.processing";
    private final String EMAIL_RIGHTS_CANCEL_CONFIRM = "email.rights.cancel.confirm";
    private final String EMAIL_CERTIFICATES_LODGEMENT_SUCCESS = "email.certificates.lodgement.success";

    private static final Logger logger = LoggerFactory.getLogger(EmailProperties.class);
    private final GeneralComponentQuery gq = ComponentQueryFactory.getGeneralComponentQuery();

    private EmailProperties() {
        load();
    }

    public static EmailProperties getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new EmailProperties();
        }
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
                List<PropertyEmail> all = gq.getAllEmailProperty();
                for (PropertyEmail e : all) {
                    boolean found = false;
                    for (Map.Entry pairs : entrySet()) {
                        String key = (String) pairs.getKey();
                        String value = (String) pairs.getValue();
                        if (e.getPropertyName().equals(key) && e.getPropertyValue().equals(value)) {
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
            String config_file = "email.properties";
            EnvironmentalVariables ev = gq.getVariable("property.path");
            String prop_path = ev.getPath();
            logger.info("Reloading configuration file - {}", config_file);

            File propFile = new File(prop_path + config_file);
            propFile.delete();
            //if property file does not exist in designated location, create file using default file within system classpath
            if (!propFile.exists()) {
                propFile.getParentFile().mkdirs();

                instream = EmailProperties.class.getClassLoader().getResourceAsStream(config_file);
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
            setProperty(INTERNAL_MAIL_USER, gq.getEmailProperty(INTERNAL_MAIL_USER).getPropertyValue());
            setProperty(INTERNAL_MAIL_PASSWORD, gq.getEmailProperty(INTERNAL_MAIL_PASSWORD).getPropertyValue());
            setProperty(EXTERNAL_MAIL_USER, gq.getEmailProperty(EXTERNAL_MAIL_USER).getPropertyValue());
            setProperty(EXTERNAL_MAIL_PASSWORD, gq.getEmailProperty(EXTERNAL_MAIL_PASSWORD).getPropertyValue());
            setProperty(AUTHORISATION_MAIL_TEMPLATE, gq.getEmailProperty(AUTHORISATION_MAIL_TEMPLATE).getPropertyValue());
            setProperty(WARNING_MAIL_TEMPLATE, gq.getEmailProperty(WARNING_MAIL_TEMPLATE).getPropertyValue());
            setProperty(INFORMATION_MAIL_TEMPLATE, gq.getEmailProperty(INFORMATION_MAIL_TEMPLATE).getPropertyValue());
            setProperty(EMAIL_MERGE, gq.getEmailProperty(EMAIL_MERGE).getPropertyValue());
            setProperty(EMAIL_CHANGE_ADDRESS, gq.getEmailProperty(EMAIL_CHANGE_ADDRESS).getPropertyValue());
            setProperty(EMAIL_CHANGE_NAME, gq.getEmailProperty(EMAIL_CHANGE_NAME).getPropertyValue());
            setProperty(EMAIL_CHANGE_CHN, gq.getEmailProperty(EMAIL_CHANGE_CHN).getPropertyValue());
            setProperty(EMAIL_MERGE_SEND, gq.getEmailProperty(EMAIL_MERGE_SEND).getPropertyValue());
            setProperty(EMAIL_CHANGE_ADDRESS_SEND, gq.getEmailProperty(EMAIL_CHANGE_ADDRESS_SEND).getPropertyValue());
            setProperty(EMAIL_CHANGE_NAME_SEND, gq.getEmailProperty(EMAIL_CHANGE_NAME_SEND).getPropertyValue());
            setProperty(EMAIL_CHANGE_CHN_SEND, gq.getEmailProperty(EMAIL_CHANGE_CHN_SEND).getPropertyValue());
            setProperty(EMAIL_IPO_PROCESSING, gq.getEmailProperty(EMAIL_IPO_PROCESSING).getPropertyValue());
            setProperty(EMAIL_IPO_PAYMENT_SUCCESS, gq.getEmailProperty(EMAIL_IPO_PAYMENT_SUCCESS).getPropertyValue());
            setProperty(EMAIL_IPO_PAYMENT_FAILURE, gq.getEmailProperty(EMAIL_IPO_PAYMENT_FAILURE).getPropertyValue());
            setProperty(EMAIL_PLACEMENT_PROCESSING, gq.getEmailProperty(EMAIL_PLACEMENT_PROCESSING).getPropertyValue());
            setProperty(EMAIL_PLACEMENT_PAYMENT_SUCCESS, gq.getEmailProperty(EMAIL_PLACEMENT_PAYMENT_SUCCESS).getPropertyValue());
            setProperty(EMAIL_PLACEMENT_PAYMENT_FAILURE, gq.getEmailProperty(EMAIL_PLACEMENT_PAYMENT_FAILURE).getPropertyValue());
            setProperty(EMAIL_RIGHTS_PROCESSING, gq.getEmailProperty(EMAIL_RIGHTS_PROCESSING).getPropertyValue());
            setProperty(EMAIL_RIGHTS_PAYMENT_SUCCESS, gq.getEmailProperty(EMAIL_RIGHTS_PAYMENT_SUCCESS).getPropertyValue());
            setProperty(EMAIL_RIGHTS_PAYMENT_FAILURE, gq.getEmailProperty(EMAIL_RIGHTS_PAYMENT_FAILURE).getPropertyValue());
            setProperty(EMAIL_IPO_SEND, gq.getEmailProperty(EMAIL_IPO_SEND).getPropertyValue());
            setProperty(EMAIL_PLACEMENT_SEND, gq.getEmailProperty(EMAIL_PLACEMENT_SEND).getPropertyValue());
            setProperty(EMAIL_RIGHTS_SEND, gq.getEmailProperty(EMAIL_RIGHTS_SEND).getPropertyValue());
            setProperty(EMAIL_IPO_CANCEL_PROCESSING, gq.getEmailProperty(EMAIL_IPO_CANCEL_PROCESSING).getPropertyValue());
            setProperty(EMAIL_IPO_CANCEL_CONFIRM, gq.getEmailProperty(EMAIL_IPO_CANCEL_CONFIRM).getPropertyValue());
            setProperty(EMAIL_PLACEMENT_CANCEL_PROCESSING, gq.getEmailProperty(EMAIL_PLACEMENT_CANCEL_PROCESSING).getPropertyValue());
            setProperty(EMAIL_PLACEMENT_CANCEL_CONFIRM, gq.getEmailProperty(EMAIL_PLACEMENT_CANCEL_CONFIRM).getPropertyValue());
            setProperty(EMAIL_RIGHTS_CANCEL_PROCESSING, gq.getEmailProperty(EMAIL_RIGHTS_CANCEL_PROCESSING).getPropertyValue());
            setProperty(EMAIL_RIGHTS_CANCEL_CONFIRM, gq.getEmailProperty(EMAIL_RIGHTS_CANCEL_CONFIRM).getPropertyValue());
            setProperty(EMAIL_CERTIFICATES_LODGEMENT_SUCCESS, gq.getEmailProperty(EMAIL_CERTIFICATES_LODGEMENT_SUCCESS).getPropertyValue());

            store(changestream, null);
            changestream.close();
        } catch (Exception ex) {
            logger.info("failed to reload configuration file - see error log");
            logger.error("error reloading email config file:", ex);
        }
    }

    /**
     * Loads the email.properties file.
     *
     * @param clz the class whose classloader will be used to load the email
     * properties file
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
     *
     * @return the mail transport protocol
     */
    public String getMailTransportProtocol() {
        return getProperty(MAIL_TRANSPORT_PROTOCOL);
    }

    /**
     * Gets the mail host.
     *
     * @return the mail host
     */
    public String getMailHost() {
        return getProperty(MAIL_HOST);
    }

    /**
     * Gets the mail smtp port.
     *
     * @return the mail smtp port
     */
    public String getMailSmtpPort() {
        return getProperty(MAIL_SMTP_PORT);
    }

    /**
     * Gets the mail smtp authentication enable value.
     *
     * @return the mail smtp authentication enable value
     */
    public String getMailSmtpAuth() {
        return getProperty(MAIL_SMTP_AUTH);
    }

    /**
     * Gets the mail smtp start-tls enable value.
     *
     * @return the mail smtp start-tls enable value
     */
    public String getMailSmtpStarttlsEnable() {
        return getProperty(MAIL_SMTP_STARTTLS_ENABLE);
    }

    /**
     * Gets the username of the mail sender.
     *
     * @return the mail user
     */
    public String getMailUser() {
        return getProperty(MAIL_USER);
    }

    /**
     * Gets the password of the mail sender.
     *
     * @return the mail password
     */
    public String getMailPassword() {
        return getProperty(MAIL_PASSWORD);
    }

    /**
     * Gets the default internal sender's email address.
     *
     * @return the internal sender's email address
     */
    public String getInternalMailUser() {
        return getProperty(INTERNAL_MAIL_USER);
    }

    /**
     * Gets the default internal sender's email address password.
     *
     * @return the internal sender's email address password
     */
    public String getInternalMailPassword() {
        return getProperty(INTERNAL_MAIL_PASSWORD);
    }

    /**
     * Gets the default external sender's email address.
     *
     * @return the external sender's email address
     */
    public String getExternalMailUser() {
        return getProperty(EXTERNAL_MAIL_USER);
    }

    /**
     * Gets the default external sender's email address password.
     *
     * @return the external sender's email address password
     */
    public String getExternalMailPassword() {
        return getProperty(EXTERNAL_MAIL_PASSWORD);
    }

    /**
     * Gets the default authorisation email template location.
     *
     * @return the email authorisation template location
     */
    public String getAuthorisationMailTemplate() {
        return getProperty(AUTHORISATION_MAIL_TEMPLATE);
    }

    /**
     * Gets the default warning email template location.
     *
     * @return the email warning template location
     */
    public String getWarningMailTemplate() {
        return getProperty(WARNING_MAIL_TEMPLATE);
    }

    /**
     * Gets the default information email template location.
     *
     * @return the information email template location
     */
    public String getInformationMailTemplate() {
        return getProperty(INFORMATION_MAIL_TEMPLATE);
    }

    public String getEmailMerge() {
        return getProperty(EMAIL_MERGE);
    }

    public String getEmailChangeAddress() {
        return getProperty(EMAIL_CHANGE_ADDRESS);
    }

    public String getEmailChangeName() {
        return getProperty(EMAIL_CHANGE_NAME);
    }

    public String getEmailChangeChn() {
        return getProperty(EMAIL_CHANGE_CHN);
    }

    public String getEmailMergeSend() {
        return getProperty(EMAIL_MERGE_SEND);
    }

    public String getEmailChangeAddressSend() {
        return getProperty(EMAIL_CHANGE_ADDRESS_SEND);
    }

    public String getEmailChangeNameSend() {
        return getProperty(EMAIL_CHANGE_NAME_SEND);
    }

    public String getEmailChangeChnSend() {
        return getProperty(EMAIL_CHANGE_CHN_SEND);
    }

    public String getEmailIpoProcessing() {
        return getProperty(EMAIL_IPO_PROCESSING);
    }

    public String getEmailIpoPaymentSuccess() {
        return getProperty(EMAIL_IPO_PAYMENT_SUCCESS);
    }

    public String getEmailIpoPaymentFailure() {
        return getProperty(EMAIL_IPO_PAYMENT_FAILURE);
    }

    public String getEmailPlacementProcessing() {
        return getProperty(EMAIL_PLACEMENT_PROCESSING);
    }

    public String getEmailPlacementPaymentSuccess() {
        return getProperty(EMAIL_PLACEMENT_PAYMENT_SUCCESS);
    }

    public String getEmailPlacementPaymentFailure() {
        return getProperty(EMAIL_PLACEMENT_PAYMENT_FAILURE);
    }

    public String getEmailRightsProcessing() {
        return getProperty(EMAIL_RIGHTS_PROCESSING);
    }

    public String getEmailRightsPaymentSuccess() {
        return getProperty(EMAIL_RIGHTS_PAYMENT_SUCCESS);
    }

    public String getEmailRightsPaymentFailure() {
        return getProperty(EMAIL_RIGHTS_PAYMENT_FAILURE);
    }

    public String getEmailIpoSend() {
        return getProperty(EMAIL_IPO_SEND);
    }

    public String getEmailRightsSend() {
        return getProperty(EMAIL_RIGHTS_SEND);
    }

    public String getEmailPlacementSend() {
        return getProperty(EMAIL_PLACEMENT_SEND);
    }

    public String getEmailIpoCancelProcessing() {
        return getProperty(EMAIL_IPO_CANCEL_PROCESSING);
    }

    public String getEmailIpoCancelConfirm() {
        return getProperty(EMAIL_IPO_CANCEL_CONFIRM);
    }

    public String getEmailPlacementCancelProcessing() {
        return getProperty(EMAIL_PLACEMENT_CANCEL_PROCESSING);
    }

    public String getEmailPlacementCancelConfirm() {
        return getProperty(EMAIL_PLACEMENT_CANCEL_CONFIRM);
    }

    public String getEmailRightsCancelProcessing() {
        return getProperty(EMAIL_RIGHTS_CANCEL_PROCESSING);
    }

    public String getEmailRightsCancelConfirm() {
        return getProperty(EMAIL_RIGHTS_CANCEL_CONFIRM);
    }

    public String getEmailCertificatesLodgementSuccess() {
        return getProperty(EMAIL_CERTIFICATES_LODGEMENT_SUCCESS);
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
