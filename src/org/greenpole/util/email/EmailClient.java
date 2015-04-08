/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.greenpole.util.email;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.greenpole.util.properties.EmailProperties;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Akinwale Agbaje
 */
public class EmailClient implements Runnable {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(EmailClient.class);
    private final EmailProperties prop = new EmailProperties(EmailClient.class);
    private Session mailSession;
    private MimeMessage msg;
    private final String from;
    private final String to;
    private final String subject;
    private final String template;
    
    /**
     * Initialises all necessary components before email is sent via thread.
     * @param from the address the email will be sent to
     * @param to the address the email is coming from
     * @param subject the subject of the email
     * @param template the template which will serve as the email body, typically a html file
     */
    public EmailClient(String from, String to, String subject, String template) {
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.template = template;
        
        initialiseProperties();
    }
    
    private void initialiseProperties() {
        mailSession = Session.getInstance(prop, new SMTPAuthenticator());
        mailSession.setDebug(false);
        msg = new MimeMessage(mailSession);
    }

    @Override
    public void run() {
        sendMail();
    }
    
    private void sendMail() {
        try {
            msg.setFrom(new InternetAddress(from));
            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            msg.setSubject(subject);
            msg.setContent(getTemplateContent(template), "text/html");
            Transport.send(msg);
            logger.info("email sent");
        } catch (AddressException ex) {
            logger.info("failed to recognise email address - see error log");
            logger.error("error parsing email address:", ex);
        } catch (MessagingException ex) {
            logger.info("failed to send email - see error log");
            logger.error("error sending email:", ex);
        }
        
    }
    
    private String getTemplateContent(String template) {
        //read template into string format, java 8 style
        StringBuilder sb = new StringBuilder();
        logger.info("loading email template - [{}]", template);
        try {
            List<String> lines = Files.readAllLines(Paths.get(template), StandardCharsets.UTF_8);
            lines.stream().forEach((line) -> {
                sb.append(line);
            });
        } catch (IOException ex) {
            logger.info("failed to load email template - see error log");
            logger.error("error loading email template:", ex);
        }
        return sb.toString();
    }
    
    private class SMTPAuthenticator extends Authenticator {

        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(prop.getMailUser(), prop.getMailPassword());
        }
    }
}
