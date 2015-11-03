/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.greenpole.util.sms;

/**
 *
 * @author Akinwale Agbaje
 */
public class SMSClientTester {
    public static void main(String[] args) {
        SMSClient client = new SMSClient("africaprudentiasl", "afriprud2015");
        System.out.println("about to print that result!");
        System.out.println(client.getCreditBalance());
    }
}
