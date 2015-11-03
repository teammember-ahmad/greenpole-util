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
import javax.ws.rs.core.MediaType;
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
import org.greenpole.entity.sms.TextSend;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Akinwale Agbaje
 * Utilised for sending text messages.
 */
public class SMSClient {
    private final WebTarget webTarget;
    private final WebTarget creditTarget;
    private final Client client;
    private final Client creditClient;
    private final String username;
    private final String password;
    private static final String BASE_URI = "http://api.infobip.com/api/v3/sendsms";
    private static final String CREDIT_URI = "http://api.infobip.com/api/command";
    private static final Logger logger = LoggerFactory.getLogger(SMSClient.class);

    /**
     * Sets up the username and password for the sms api.
     * @param username the api username.
     * @param password the api password.
     */
    public SMSClient(String username, String password) {
        logger.info("entering sms client!!!!");
        this.username = username;
        this.password = password;
        
        logger.info("username: {}, password: {}", username, password);
        
        client = javax.ws.rs.client.ClientBuilder.newClient();
        creditClient = javax.ws.rs.client.ClientBuilder.newClient();
        
        webTarget = client.target(BASE_URI).path("xml");
        creditTarget = creditClient.target(CREDIT_URI);
        
        logger.info("webTarge:: {}", webTarget.toString());
        logger.info("creditTarget:: {}", creditTarget.toString());
    }
    
    /**
     * Processes and sends an sms to a single recipient.
     * @param toSend the object containing the text message to send
     * @return the response from the sms server
     * @throws ClientErrorException if REST client encounters error while trying to connect to api
     * @throws JAXBException if jaxb encounters error while transforming sms object to xml file
     */
    public Results processSendTextSingle(TextSend toSend) throws ClientErrorException, JAXBException {
        Authentication auth = new Authentication();
        Message msg = new Message();
        Recipients recipient = new Recipients();
        Gsm gsm = new Gsm();
        Sms sms = new Sms();
        List<Gsm> gsms = new ArrayList<>();
        
        //set contents for gsm recipient
        gsm.setContent(toSend.getPhoneNumber());
        gsm.setMessageId(toSend.getMessage_id());
        gsms.add(gsm);
        recipient.setGsm(gsms);
        
        //sent content for text and sender
        msg.setSender(toSend.getSender());
        msg.setText(toSend.getText());
        msg.setRecipients(recipient);
        
        //if message is a flash message
        if (toSend.isIsFlash())
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
     * @param toSend the object containing the text message to send
     * @return the response from the sms server
     * @throws ClientErrorException if REST client encounters error while trying to connect to api
     * @throws JAXBException if jaxb encounters error while transforming sms object to xml file 
     */
    public Results processSendTextBulk(TextSend toSend) throws ClientErrorException, JAXBException {
        Authentication auth = new Authentication();
        Message msg = new Message();
        Recipients recipient = new Recipients();
        Sms sms = new Sms();
        List<Gsm> gsms = new ArrayList<>();
        
        for (Map.Entry pairs : toSend.getNumbersAndIds().entrySet()) {
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
        msg.setSender(toSend.getSender());
        msg.setText(toSend.getText());
        msg.setRecipients(recipient);
        
        //if message is a flash message
        if (toSend.isIsFlash())
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
     * Gets the account's credit balance.
     * @return the account's credit balance
     */
    public String getCreditBalance() {
        logger.info("checking credit now!!");
        WebTarget resource = creditTarget;
        resource = resource.queryParam("username", username);
        logger.info("query param for username");
        resource = resource.queryParam("password", password);
        logger.info("query param for password");
        resource = resource.queryParam("cmd", "credits");
        logger.info("query param for credit");
        try {
            logger.info("credit check from api:: {}", resource.request(MediaType.TEXT_PLAIN).get(String.class));
        } catch (Exception ex) {
            logger.error("egbami!!!", ex);
        }
        return resource.request(MediaType.TEXT_PLAIN).get(String.class);
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
