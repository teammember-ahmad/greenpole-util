/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.greenpole.util.email;

import com.sun.mail.smtp.SMTPTransport;
import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.Callable;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Akinwale Agbaje
 * Sends emails to any legit email address.
 */
public class EmailClient implements Runnable, Callable<String> {
    private static final Logger logger = LoggerFactory.getLogger(EmailClient.class);
    private final EmailProperties prop = EmailProperties.getInstance();
    private Session mailSession;
    private MimeMessage msg;
    private final String from;
    private final String to;
    private final String subject;
    private final String to_person;
    private final String body_main;
    private final String template;
    private final List<String> cc;
    
    /**
     * Initialises all necessary components before email is sent via thread.
     * @param from the address the email will be sent from
     * @param password the password of the from address (user address)
     * @param to the address the email is going to
     * @param subject the subject of the email
     * @param to_person the name that should come under "dear ..." in the email template (not necessary in all templates)
     * @param body_main the main body of the email that should be inserted into the template
     * @param template the template which will serve as the email body, typically a html file
     */
    public EmailClient(String from, String password, String to, String subject, String to_person, String body_main, String template) {
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.template = template;
        this.to_person = to_person;
        this.body_main = body_main;
        this.cc = null;
        initialiseProperties(from, password);
    }

    /**
     * Initialises all necessary components before email is sent via thread.
     * Used to send emails to multiple recipients.
     * @param from the address the email will be sent from
     * @param password the password of the from address (user address)
     * @param to the address the email is going to
     * @param subject the subject of the email
     * @param to_person the name that should come under "dear ..." in the email template (not necessary in all templates)
     * @param body_main the main body of the email that should be inserted into the template
     * @param template the template which will serve as the email body, typically a html file
     * @param cc email addresses to be attached to cc
     */
    public EmailClient(String from, String password, String to, String subject, String to_person, String body_main, String template, List<String> cc) {
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.to_person = to_person;
        this.body_main = body_main;
        this.template = template;
        this.cc = cc;
        initialiseProperties(from, password);
    }
    
    private void initialiseProperties(String user, String password) {
        prop.setProperty("mail.user", user);
        prop.setProperty("mail.password", password);
        mailSession = Session.getInstance(prop, new SMTPAuthenticator());
        mailSession.setDebug(false);
        msg = new MimeMessage(mailSession);
    }

    @Override
    public void run() {
        sendMail_NoResponse();
    }
    
    @Override
    public String call() throws Exception {
        return sendMail_WithResponse();
    }
    
    private String sendMail_WithResponse() {
        String response = "";
        try {
            msg.setFrom(new InternetAddress(from));
            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            msg.setSubject(subject);
            msg.setHeader("X-Priority", "1");
            
            if (cc != null && !cc.isEmpty()) {
                InternetAddress[] addys = new InternetAddress[cc.size()];
                int index = 0;
                for (String email : cc) {
                    addys[index] = new InternetAddress(email);
                    ++index;
                }
                msg.setRecipients(Message.RecipientType.CC, addys);
            }
            
            MessageFormat mf = new MessageFormat(template);
            String mail_content = mf.format(new Object[]{to_person,body_main});
            msg.setContent(mail_content, "text/html");
            
            SMTPTransport smtpTransport = (SMTPTransport) mailSession.getTransport();
            smtpTransport.sendMessage(msg, msg.getAllRecipients());
            response = smtpTransport.getLastServerResponse();
            smtpTransport.close();
            logger.info("email sent");
        } catch (AddressException ex) {
            logger.info("failed to recognise email address - see error log");
            logger.error("error parsing email address:", ex);
        } catch (MessagingException ex) {
            logger.info("failed to send email - see error log");
            logger.error("error sending email:", ex);
        }
        return response;
    }
    
    private void sendMail_NoResponse() {
        try {
            msg.setFrom(new InternetAddress(from));
            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            msg.setSubject(subject);
            msg.setHeader("X-Priority", "1");
            
            if (cc != null && !cc.isEmpty()) {
                InternetAddress[] addys = new InternetAddress[cc.size()];
                int index = 0;
                for (String email : cc) {
                    addys[index] = new InternetAddress(email);
                    ++index;
                }
                msg.setRecipients(Message.RecipientType.CC, addys);
            }
            
            MessageFormat mf = new MessageFormat(template);
            String mail_content = mf.format(new Object[]{to_person,body_main});
            msg.setContent(mail_content, "text/html");
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
    
    private class SMTPAuthenticator extends Authenticator {

        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(prop.getMailUser(), prop.getMailPassword());
        }
    }
}
