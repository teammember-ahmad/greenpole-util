/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.greenpole.util.rest;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import org.greenpole.entity.response.Response;
import org.greenpole.entity.security.Login;

/**
 *
 * @author Akinwale.Agbaje
 */
public class RestCheck {
    private final Client client;
    private final WebTarget webTarget;
    private static final String BASE_URI = "http://192.168.10.219:7002/greenpole-engine-general/loginquery/v1";

    public RestCheck() {
        this.client = javax.ws.rs.client.ClientBuilder.newClient();
        this.webTarget = client.target(BASE_URI).path("queryuserlist");
    }
    
    public String doPost() {
        Login login = new Login();
        login.setUserId("akinwale.agbaje@africaprudentialregistrars.com");
        login.setPassword("ZOLHvWZMDck91zeog8SWTIdx2zjvK/Wry+N4BGZjRyYwCinsMrgM0OAj2trXcoVwfyeREE52lq5dWIPWcvEiVjevFdxSqRjFMgdXFJ6gJzCImjWtIF3A8VKLIbScsHlPY2ZdcPZSw8mt9V55PbeViRb/CbvU6RPQXJZzrid3ewE=");
        return webTarget.queryParam("pagenumber", 1).queryParam("pagesize", 10)
                .request(javax.ws.rs.core.MediaType.APPLICATION_JSON)
                .post(javax.ws.rs.client.Entity.entity(login, javax.ws.rs.core.MediaType.APPLICATION_JSON), String.class);
    }
}
