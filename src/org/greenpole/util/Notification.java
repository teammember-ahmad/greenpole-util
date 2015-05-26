/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.greenpole.util;

import java.io.File;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.greenpole.entity.notification.NotificationWrapper;
import org.greenpole.entity.security.Login;
import org.greenpole.hibernate.query.GeneralComponentQuery;
import org.greenpole.hibernate.query.factory.ComponentQueryFactory;

/**
 *
 * @author Akinwale.Agbaje
 * Responsible for common notification functions.
 */
public class Notification {
    private final GeneralComponentQuery gq = ComponentQueryFactory.getGeneralComponentQuery();
    
    /**
     * Creates a notification code from the user's login details.
     * @param login the user's login details
     * @return the notification code
     */
    public String createCode(Login login) {
        Date date = new Date();
        Manipulator manipulate = new Manipulator();
        String[] names = manipulate.separateNameFromEmail(login.getUserId());
        return names[0] + "_" + names[1] + date.getDateTime();
    }
    
    /**
     * Loads the notification file according to the notification code.
     * This method will only load notification xml files that can be mapped 
     * to the {@link NotificationWrapper} class.
     * @param folderPath the location of the notification file
     * @param notificationCode the notification code
     * @return the notification wrapper containing contents of the xml file
     * @throws JAXBException if xml file cannot be found, or file does not map to
     * {@link NotificationWrapper}
     */
    public NotificationWrapper loadNotificationFile(String folderPath, String notificationCode) throws JAXBException {
        File file = new File(folderPath + notificationCode + ".xml");
        JAXBContext jaxbContext = JAXBContext.newInstance(NotificationWrapper.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        NotificationWrapper wrapper = (NotificationWrapper) jaxbUnmarshaller.unmarshal(file);
        return wrapper;
    }
    
    /**
     * Updates the notification file according to the notification code.
     * This method will only update notification xml files that can be mapped 
     * to the {@link NotificationWrapper} class.
     * @param folderPath the location of the notification file
     * @param notificationCode the notification code
     * @param wrapper the notification object containing updated notification information
     * @throws JAXBException if xml file cannot be found, or file does not map to
     * {@link NotificationWrapper}
     * @deprecated no need to persist notification at this point in development. Ignore method.
     */
    public void persistNotificationFile(String folderPath, String notificationCode, NotificationWrapper wrapper) throws JAXBException {
        File file = new File(folderPath + notificationCode + ".xml");
        JAXBContext jaxbContext = JAXBContext.newInstance(NotificationWrapper.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        jaxbMarshaller.marshal(wrapper, file);
    }
    
    /**
     * Writes off a notification in the database that has no corresponding xml file.
     * @param notificationCode the notification code
     */
    public void writeOffNotification(String notificationCode) {
        org.greenpole.hibernate.entity.Notification notification = gq.getNotification(notificationCode);
        notification.setWriteOff(true);
        gq.createUpdateNotification(notification);
    }
    
    /**
     * Marks a notification as attended to on the database.
     * @param notificationCode the notification code
     */
    public void markAttended(String notificationCode) {
        org.greenpole.hibernate.entity.Notification notification = gq.getNotification(notificationCode);
        notification.setAttendedTo(true);
        gq.createUpdateNotification(notification);
    }
    
    /**
     * Checks if notification file exists.
     * @param folderPath the notification's folder path
     * @param notificationCode the notification code
     * @return true, if the notification file exists
     */
    public boolean checkFile(String folderPath, String notificationCode) {
        File file = new File(folderPath + notificationCode + ".xml");
        return file.isFile();
    }
}
