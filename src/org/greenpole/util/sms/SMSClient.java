/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.greenpole.util.sms;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import org.greenpole.entity.sms.Authentication;
import org.greenpole.entity.sms.Gsm;
import org.greenpole.entity.sms.Message;
import org.greenpole.entity.sms.Recipients;
import org.greenpole.entity.sms.Results;
import org.greenpole.entity.sms.Sms;

/**
 *
 * @author Akinwale Agbaje
 * Utilised for sending text messages.
 */
public class SMSClient {
    private final WebTarget webTarget;
    private final Client client;
    private final String username;
    private final String password;
    private static final String BASE_URI = "http://api.infobip.com/api/v3/sendsms";

    /**
     * Sets up the username and password for the sms api.
     * @param username the api username.
     * @param password the api password.
     */
    public SMSClient(String username, String password) {
        this.username = username;
        this.password = password;
        client = javax.ws.rs.client.ClientBuilder.newClient();
        webTarget = client.target(BASE_URI).path("xml");
    }
    
    /**
     * Processes and sends an sms to a single recipient.
     * @param sender the sender
     * @param text the text message
     * @param phoneNumber the recipient phone number
     * @param id the message id
     * @param isFlash if the text message should be a flash message
     * @return the response from the sms server
     * @throws ClientErrorException if REST client encounters error while trying to connect to api
     * @throws JAXBException if jaxb encounters error while transforming sms object to xml file
     */
    public Results processSendTextSingle(String sender, String text, String phoneNumber, String id, boolean isFlash) throws ClientErrorException, JAXBException {
        Authentication auth = new Authentication();
        Message msg = new Message();
        Recipients recipient = new Recipients();
        Gsm gsm = new Gsm();
        Sms sms = new Sms();
        List<Gsm> gsms = new ArrayList<>();
        
        //set contents for gsm recipient
        gsm.setContent(phoneNumber);
        gsm.setMessageId(id);
        gsms.add(gsm);
        recipient.setGsm(gsms);
        
        //sent content for text and sender
        msg.setSender(sender);
        msg.setText(text);
        msg.setRecipients(recipient);
        
        //if message is a flash message
        if (isFlash)
            msg.setFlash("1");
        
        //set authentication content
        auth.setUsername(username);
        auth.setPassword(password);
        
        //set sms content
        sms.setAuthentication(auth);
        sms.setMessage(msg);
        
        return sendText(sms);
    }
    
    /**
     * Processes and sends an sms to multiple recipients.
     * @param sender the sender
     * @param text the text message
     * @param numbersAndIds the recipients' phone numbers and message ids
     * @param isFlash if the text message should be a flash message
     * @return the response from the sms server
     * @throws ClientErrorException if REST client encounters error while trying to connect to api
     * @throws JAXBException if jaxb encounters error while transforming sms object to xml file 
     */
    public Results processSendTextBulk(String sender, String text, Map<String, String> numbersAndIds, boolean isFlash) throws ClientErrorException, JAXBException {
        Authentication auth = new Authentication();
        Message msg = new Message();
        Recipients recipient = new Recipients();
        Sms sms = new Sms();
        List<Gsm> gsms = new ArrayList<>();
        
        for (Map.Entry pairs : numbersAndIds.entrySet()) {
            Gsm gsm = new Gsm();
            
            String phoneNumber = (String) pairs.getKey();
            String id = (String) pairs.getValue();
            
            gsm.setContent(phoneNumber);
            gsm.setMessageId(id);
            
            gsms.add(gsm);
        }
        
        //set contents for gsm recipient
        recipient.setGsm(gsms);
        
        //sent content for text and sender
        msg.setSender(sender);
        msg.setText(text);
        msg.setRecipients(recipient);
        
        //if message is a flash message
        if (isFlash)
            msg.setFlash("1");
        
        //set authentication content
        auth.setUsername(username);
        auth.setPassword(password);
        
        //set sms content
        sms.setAuthentication(auth);
        sms.setMessage(msg);
        
        return sendText(sms);
    }
    
    private Results sendText(Sms sms) throws ClientErrorException, PropertyException, JAXBException {
        StringWriter sw = new StringWriter();
        
        //format sms class to extra xml information to be sent
        JAXBContext jaxbContext = JAXBContext.newInstance(Sms.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        jaxbMarshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
        jaxbMarshaller.marshal(sms, sw);
        
        String toSend = sw.toString();
        
        return webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_XML)
                .post(javax.ws.rs.client.Entity.entity(toSend, javax.ws.rs.core.MediaType.APPLICATION_XML), Results.class);
    }
    
    /**
     * close client.
     */
    public void close() {
        client.close();
    }
}
