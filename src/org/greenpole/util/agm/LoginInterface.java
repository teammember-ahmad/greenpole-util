/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.greenpole.util.agm;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import javax.ws.rs.client.WebTarget;
import org.greenpole.entity.response.Response;
import org.greenpole.entity.security.Login;

/**
 *
 * @author Akinwale.Agbaje
 */
public class LoginInterface {
    private final BaseUrlInterface baseUrl;
    private final ObjectMapper mapper;

    public LoginInterface() {
        baseUrl = new BaseUrlInterface();
        mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
    }
    
    public Response authenticateUserAccount_Request(Login login) throws IOException {
        baseUrl.loadLoginRequestV1Path();
        WebTarget webTarget = baseUrl.getWebTarget();
        
        String json_resp = webTarget.path("authenticateaccount")
                .queryParam("userId", login.getUserId()).queryParam("password", login.getPassword())
                .request(javax.ws.rs.core.MediaType.APPLICATION_JSON)
                .get(String.class);
        
        Response resp = mapper.readValue(json_resp, Response.class);
        
        return resp;
    }
}
