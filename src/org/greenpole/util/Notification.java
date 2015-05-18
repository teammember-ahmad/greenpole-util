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

/**
 *
 * @author Akinwale.Agbaje
 * Responsible for common notification functions.
 */
public class Notification {

    /**
     * Creates a notification code from the user's login details.
     * @param login the user's login details
     * @return the notification code
     */
    public static String createCode(Login login) {
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
    public static NotificationWrapper loadNotificationFile(String folderPath, String notificationCode) throws JAXBException {
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
     */
    public static void persistNotificationFile(String folderPath, String notificationCode, NotificationWrapper wrapper) throws JAXBException {
        File file = new File(folderPath + notificationCode + ".xml");
        JAXBContext jaxbContext = JAXBContext.newInstance(NotificationWrapper.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        jaxbMarshaller.marshal(wrapper, file);
    }
}
