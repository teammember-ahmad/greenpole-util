/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.greenpole.util.agm;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import org.greenpole.util.properties.GeneralMeetingProperties;

/**
 *
 * @author Akinwale.Agbaje
 */
public class BaseUrlInterface {
    //private final GeneralMeetingProperties prop = GeneralMeetingProperties.getInstance();
    private final Client client;
    private WebTarget webTarget;
    private final String BASE_URI = "http://192.168.10.219:7002/greenpole-engine-agm";
    //private final String BASE_URI = "http://"+prop.getGmWebserviceHost()+":7002/greenpole-engine-agm";
    //private static final String BASE_URI = "http://greensvr.africaprudentialregistrars.com:7002/greenpole-engine-agm";

    public BaseUrlInterface() {
        this.client = javax.ws.rs.client.ClientBuilder.newClient();
    }
    
    public void loadHolderRequestV1Path() {
        this.webTarget = client.target(BASE_URI).path("holderrequest/v1");
    }
    
    public void loadHolderQueryV1Path() {
        this.webTarget = client.target(BASE_URI).path("holderquery/v1");
    }
    
    public void loadClientCompanyRequestV1Path() {
        this.webTarget = client.target(BASE_URI).path("clientcompanyrequest/v1");
    }
    
    public void loadClientCompanyQueryV1Path() {
        this.webTarget = client.target(BASE_URI).path("clientcompanyquery/v1");
    }
    
    public WebTarget getWebTarget() {
        return webTarget;
    }
}
