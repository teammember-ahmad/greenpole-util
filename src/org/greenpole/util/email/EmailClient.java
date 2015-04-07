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
    
    public EmailClient(String from, String to, String template) {
        initialiseProperties();
    }
    
    private void initialiseProperties() {
        mailSession = Session.getInstance(prop, new SMTPAuthenticator());
        mailSession.setDebug(false);
        msg = new MimeMessage(mailSession);
    }

    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private void sendMail(String from, String to, String subject, String template) {
        try {
            msg.setFrom(new InternetAddress(from));
            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            msg.setSubject(subject);
        } catch (AddressException ex) {
            Logger.getLogger(EmailClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MessagingException ex) {
            Logger.getLogger(EmailClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    private String getTemplateContent(String template) {
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
