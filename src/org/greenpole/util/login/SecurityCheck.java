/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.greenpole.util.login;

import java.security.PrivateKey;
import org.greenpole.entity.response.Response;
import org.greenpole.entity.security.Login;
import org.greenpole.hibernate.query.GeneralComponentQuery;
import org.greenpole.hibernate.query.factory.ComponentQueryFactory;
import org.greenpole.util.BytesConverter;
import org.greenpole.util.Notification;
import org.greenpole.util.properties.GreenpoleProperties;
import org.greenpole.util.properties.NotificationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Akinwale Agbaje
 * Checks the notification and logged in user against certain security rules.
 * Should be used in web service layer, under authorisation
 */
public class SecurityCheck {

    /**
     * Checks the notification and logged in user against certain security rules.
     * @param login the logged in user
     * @param notificationCode the notification code
     * @param resp response to the check
     * @return true, if notification fails. Otherwise, false
     */
    public static boolean authorisationSecurityFailChecker(Login login, String notificationCode, Response resp) {
        Logger logger = LoggerFactory.getLogger(SecurityCheck.class);
        GeneralComponentQuery gq = ComponentQueryFactory.getGeneralComponentQuery();
        GreenpoleProperties greenProp = GreenpoleProperties.getInstance();
        PrivateKeyReader prKreader = PrivateKeyReader.getInstance();
        NotificationProperties notificationProp = NotificationProperties.getInstance();
        Notification notification = new Notification();
        
        try {
            //login security rules:
            if (!gq.checkUserAccess(login.getUserId())) {//user must exist in system
                resp.setRetn(100);
                resp.setDesc("The account " + login.getUserId() + " is not a registered user in the system");
                logger.info("This account {} is not a registered user in the system - [{}]", login.getUserId(), login.getUserId());
                return true;
            } else {
                org.greenpole.hibernate.entity.UserAccess access_hib = gq.getUserAccess(login.getUserId());
                if (access_hib.getExpired()) {//existing user must not have expired password
                    resp.setRetn(100);
                    resp.setDesc("Your password has expired. Please, contact Administrator");
                    logger.info("Your password has expired - [{}]", login.getUserId());
                    return true;
                }
                
                if (access_hib.getLocked()) {//existing user must not have locked account
                    resp.setRetn(100);
                    resp.setDesc("Your account has been locked. Please, contact Administrator");
                    logger.info("Your account has been locked - [{}]", login.getUserId());
                    return true;
                }
                
                if (access_hib.getSuspended()) {//existing user must not have suspended account
                    resp.setRetn(100);
                    resp.setDesc("Your account has been suspended. Please, contact Administrator");
                    logger.info("Your account has been suspended - [{}]", login.getUserId());
                    return true;
                }
                
                if (access_hib.getFirstTime()) {//existing user must not be first time user
                    resp.setRetn(100);
                    resp.setDesc("Please reset your password before you can use the system");
                    logger.info("Please reset your password before you can use the system - [{}]", login.getUserId());
                    return true;
                }
                
                {//existing user must be using correct password
                    Decipher decipher = new Decipher("RSA");
                    BytesConverter converter = new BytesConverter();
                    
                    byte[] storedPassword = access_hib.getPassword();
                    byte[] storedSalt = access_hib.getPassSupport();
                    
                    //decrypt encrypted password from front-end
                    PrivateKey privateKey = prKreader.getPrivateKey();
                    
                    byte[] encryptedSentPassword = converter.decodeToBytes(login.getPassword());
                    String sentPassword = decipher.decryptEncryptedText(encryptedSentPassword, privateKey);
                    
                    boolean same = Password.authenticate(sentPassword, storedPassword, storedSalt);
                    if (!same) {
                        resp.setRetn(100);
                        resp.setDesc("Your password is invalid. You are not authorised to use this service");
                        logger.info("Your password is invalid. You are not authorised to use this service - [{}]", login.getUserId());
                        return true;
                    }
                }
            }
            
            //notification code must exist
            if (!gq.checkNotification(notificationCode)) {
                resp.setRetn(900);
                resp.setDesc("Illegal request. This notification code does not exist.");
                logger.info("Illegal request. This notification code does not exist - [{}]", login.getUserId());
                return true;
            }

            //notification code must have xml file and db record
            if (!notification.checkFile(notificationProp.getNotificationLocation(), notificationCode)) {
                if (gq.checkNotification(notificationCode)) {
                    notification.writeOffNotification(notificationCode);
                    resp.setRetn(900);
                    resp.setDesc("The notification file has been tampered with. System will write off notification. Send a new request.");
                    logger.info("The notification file has been tampered with. System will write off notification. Send a new request - [{}]", login.getUserId());
                    return true;
                }
                resp.setRetn(900);
                resp.setDesc("Illegal notification code sent.");
                logger.info("Illegal notification code sent - [{}]", login.getUserId());
                return true;
            }

            //notification code must be tied to logged in user
            if (!gq.checkNotificationAgainstUser(login.getUserId(), notificationCode)) {
                resp.setRetn(900);
                resp.setDesc("Notification code does not belong to logged in user.");
                logger.info("Notification code does not belong to logged in user - [{}]", login.getUserId());
                return true;
            }

            //notification code must not be tied to both the sender and receiver
            if (gq.checkFromToSame(login.getUserId(), notificationCode)) {
                resp.setRetn(900);
                resp.setDesc("Illegal entry. Notification code cannot have its sender and receiver as the same user.");
                logger.info("Illegal entry. Notification code cannot have its sender and receiver as the same user - [{}]", login.getUserId());
                return true;
            }
            return false;
        } catch (Exception ex) {
            logger.info("error in security check. See error log - [{}]", login.getUserId());
            logger.error("error in security check - [" + login.getUserId() + "]", ex);
            
            resp.setRetn(99);
            resp.setDesc("General error. Unable to carry out security check. Contact system administrator."
                    + "\nMessage: " + ex.getMessage());
            return true;
        }
    }
    
    /**
     * Checks the logged in user against certain security rules.
     * @param login the logged in user
     * @param resp response to the check
     * @return true, if notification fails. Otherwise, false
     */
    public static boolean requestSecurityFailChecker(Login login, Response resp) {
        Logger logger = LoggerFactory.getLogger(SecurityCheck.class);
        GeneralComponentQuery gq = ComponentQueryFactory.getGeneralComponentQuery();
        PrivateKeyReader prKreader = PrivateKeyReader.getInstance();
        
        try {
            //login security rules:
            if (!gq.checkUserAccess(login.getUserId())) {//user must exist in system
                resp.setRetn(100);
                resp.setDesc("The account " + login.getUserId() + " is not a registered user in the system");
                logger.info("This account {} is not a registered user in the system - [{}]", login.getUserId(), login.getUserId());
                return true;
            } else {
                org.greenpole.hibernate.entity.UserAccess access_hib = gq.getUserAccess(login.getUserId());
                if (access_hib.getExpired()) {//existing user must not have expired password
                    resp.setRetn(100);
                    resp.setDesc("Your password has expired. Please, contact Administrator");
                    logger.info("Your password has expired - [{}]", login.getUserId());
                    return true;
                }
                
                if (access_hib.getLocked()) {//existing user must not have locked account
                    resp.setRetn(100);
                    resp.setDesc("Your account has been locked. Please, contact Administrator");
                    logger.info("Your account has been locked - [{}]", login.getUserId());
                    return true;
                }
                
                if (access_hib.getSuspended()) {//existing user must not have suspended account
                    resp.setRetn(100);
                    resp.setDesc("Your account has been suspended. Please, contact Administrator");
                    logger.info("Your account has been suspended - [{}]", login.getUserId());
                    return true;
                }
                
                if (access_hib.getFirstTime()) {//existing user must not be first time user
                    resp.setRetn(100);
                    resp.setDesc("Please reset your password before you can use the system");
                    logger.info("Please reset your password before you can use the system - [{}]", login.getUserId());
                    return true;
                }
                
                {//existing user must be using correct password
                    Decipher decipher = new Decipher("RSA");
                    BytesConverter converter = new BytesConverter();
                    
                    byte[] storedPassword = access_hib.getPassword();
                    byte[] storedSalt = access_hib.getPassSupport();
                    
                    //decrypt encrypted password from front-end
                    PrivateKey privateKey = prKreader.getPrivateKey();
                    
                    byte[] encryptedSentPassword = converter.decodeToBytes(login.getPassword());
                    String sentPassword = decipher.decryptEncryptedText(encryptedSentPassword, privateKey);
                    
                    boolean same = Password.authenticate(sentPassword, storedPassword, storedSalt);
                    if (!same) {
                        resp.setRetn(100);
                        resp.setDesc("Your password is invalid. You are not authorised to use this service");
                        logger.info("Your password is invalid. You are not authorised to use this service - [{}]", login.getUserId());
                        return true;
                    }
                }
            }
            return false;
        } catch (Exception ex) {
            logger.info("error in security check. See error log - [{}]", login.getUserId());
            logger.error("error in security check - [" + login.getUserId() + "]", ex);
            
            resp.setRetn(99);
            resp.setDesc("General error. Unable to carry out security check. Contact system administrator."
                    + "\nMessage: " + ex.getMessage());
            return true;
        }
    }
    
    /**
     * Checks the logged in user against certain security rules.
     * @param login the logged in user
     * @param resp response to the check
     * @return true, if notification fails. Otherwise, false
     */
    public static boolean querySecurityFailChecker(Login login, Response resp) {
        Logger logger = LoggerFactory.getLogger(SecurityCheck.class);
        GeneralComponentQuery gq = ComponentQueryFactory.getGeneralComponentQuery();
        PrivateKeyReader prKreader = PrivateKeyReader.getInstance();
        
        try {
            //login security rules:
            if (!gq.checkUserAccess(login.getUserId())) {//user must exist in system
                resp.setRetn(100);
                resp.setDesc("The account " + login.getUserId() + " is not a registered user in the system");
                logger.info("This account {} is not a registered user in the system - [{}]", login.getUserId(), login.getUserId());
                return true;
            } else {
                org.greenpole.hibernate.entity.UserAccess access_hib = gq.getUserAccess(login.getUserId());
                if (access_hib.getExpired()) {//existing user must not have expired password
                    resp.setRetn(100);
                    resp.setDesc("Your password has expired. Please, contact Administrator");
                    logger.info("Your password has expired - [{}]", login.getUserId());
                    return true;
                }
                
                if (access_hib.getLocked()) {//existing user must not have locked account
                    resp.setRetn(100);
                    resp.setDesc("Your account has been locked. Please, contact Administrator");
                    logger.info("Your account has been locked - [{}]", login.getUserId());
                    return true;
                }
                
                if (access_hib.getSuspended()) {//existing user must not have suspended account
                    resp.setRetn(100);
                    resp.setDesc("Your account has been suspended. Please, contact Administrator");
                    logger.info("Your account has been suspended - [{}]", login.getUserId());
                    return true;
                }
                
                if (access_hib.getFirstTime()) {//existing user must not be first time user
                    resp.setRetn(100);
                    resp.setDesc("Please reset your password before you can use the system");
                    logger.info("Please reset your password before you can use the system - [{}]", login.getUserId());
                    return true;
                }
                
                {//existing user must be using correct password
                    Decipher decipher = new Decipher("RSA");
                    BytesConverter converter = new BytesConverter();
                    
                    byte[] storedPassword = access_hib.getPassword();
                    byte[] storedSalt = access_hib.getPassSupport();
                    
                    //decrypt encrypted password from front-end
                    PrivateKey privateKey = prKreader.getPrivateKey();
                    
                    byte[] encryptedSentPassword = converter.decodeToBytes(login.getPassword());
                    String sentPassword = decipher.decryptEncryptedText(encryptedSentPassword, privateKey);
                    
                    boolean same = Password.authenticate(sentPassword, storedPassword, storedSalt);
                    if (!same) {
                        resp.setRetn(100);
                        resp.setDesc("Your password is invalid. You are not authorised to use this service");
                        logger.info("Your password is invalid. You are not authorised to use this service - [{}]", login.getUserId());
                        return true;
                    }
                }
            }
            return false;
        } catch (Exception ex) {
            logger.info("error in security check. See error log - [{}]", login.getUserId());
            logger.error("error in security check - [" + login.getUserId() + "]", ex);
            
            resp.setRetn(99);
            resp.setDesc("General error. Unable to carry out security check. Contact system administrator."
                    + "\nMessage: " + ex.getMessage());
            return true;
        }
    }
    
}
