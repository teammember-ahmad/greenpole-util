/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.greenpole.util;

import org.greenpole.entity.security.Login;

/**
 *
 * @author Akinwale Agbaje
 * Creates notification codes for the notification wrapper.
 */
public class NotificationCodeCreator {

    /**
     * Creates a notification code from the user's login details.
     * @param login the user's login details
     * @return the notification code
     */
    public static String createNotificationCode(Login login) {
        Date date = new Date();
        Manipulator manipulate = new Manipulator();
        String[] names = manipulate.separateNameFromEmail(login.getUserId());
        return names[0] + "_" + names[1] + date.getDateTime();
    }
    
}
