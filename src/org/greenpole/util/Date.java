/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.greenpole.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 *
 * @author Akinwale Agbaje
 * Contains date functions for greenpole.
 */
public class Date {
    /**
     * Gets the current date and time in year, month, day, hour, minute, second and miliseconds.
     * @return the current date in the format stated in the description above
     */
    public String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        Calendar cal = Calendar.getInstance();
        return dateFormat.format(cal.getTime());
    }
}
