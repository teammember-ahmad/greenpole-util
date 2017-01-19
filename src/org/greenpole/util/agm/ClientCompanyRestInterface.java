/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.greenpole.util.agm;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.client.WebTarget;
import org.greenpole.entity.model.clientcompany.ClientCompany;
import org.greenpole.entity.model.clientcompany.GeneralMeeting;
import org.greenpole.entity.model.clientcompany.VotingProcess;
import org.greenpole.entity.response.Response;
import org.greenpole.entity.security.Login;

/**
 *
 * @author Akinwale.Agbaje
 */
public class ClientCompanyRestInterface {
    private final BaseUrlInterface baseUrl;
    private final ObjectMapper mapper;    

    public ClientCompanyRestInterface() {
        baseUrl = new BaseUrlInterface();
        mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
    
    public Response queryAllClientCompanies_Request(Login login) throws IOException {
        baseUrl.loadClientCompanyQueryV1Path();
        WebTarget webTarget = baseUrl.getWebTarget();
        
        String json_resp = webTarget.path("queryallclientcompanies")
                .queryParam("userId", login.getUserId()).queryParam("password", login.getPassword())
                .request(javax.ws.rs.core.MediaType.APPLICATION_JSON)
                .get(String.class);
        
        Response resp = mapper.readValue(json_resp, Response.class);
        
        List<ClientCompany> cclist = new ArrayList<>();
        if (!resp.getRestBody().isEmpty()) {
            String json_list = mapper.writeValueAsString(resp.getRestBody());
            cclist = mapper.readValue(json_list, new TypeReference<List<ClientCompany>>(){});
        }
        resp.setRestBody(cclist);
        
        return resp;
    }
    
    public Response queryOpenAGMforClientCompany_Request(Login login, int clientCompanyId) throws IOException {
        baseUrl.loadClientCompanyQueryV1Path();
        WebTarget webTarget = baseUrl.getWebTarget();
        
        String json_resp = webTarget.path("queryopenagmforclientcompany")
                .queryParam("userId", login.getUserId()).queryParam("password", login.getPassword())
                .queryParam("clientCompanyId", clientCompanyId)
                .request(javax.ws.rs.core.MediaType.APPLICATION_JSON)
                .get(String.class);
        
        Response resp = mapper.readValue(json_resp, Response.class);
        
        List<GeneralMeeting> toSend = new ArrayList<>();
        if (!resp.getRestBody().isEmpty()) {
            String json_list = mapper.writeValueAsString(resp.getRestBody());
            toSend = mapper.readValue(json_list, new TypeReference<List<GeneralMeeting>>(){});
        }
        resp.setRestBody(toSend);
        
        return resp;
    }
    
    public Response queryOpenVotingProcessForAGM_Request(Login login, int gmId) throws IOException {
        baseUrl.loadClientCompanyQueryV1Path();
        WebTarget webTarget = baseUrl.getWebTarget();
        
        String json_resp = webTarget.path("queryopenvotingprocessforagm")
                .queryParam("userId", login.getUserId()).queryParam("password", login.getPassword())
                .queryParam("gmId", gmId)
                .request(javax.ws.rs.core.MediaType.APPLICATION_JSON)
                .get(String.class);
        
        Response resp = mapper.readValue(json_resp, Response.class);
        
        List<VotingProcess> toSend = new ArrayList<>();
        if (!resp.getRestBody().isEmpty()) {
            String json_list = mapper.writeValueAsString(resp.getRestBody());
            toSend = mapper.readValue(json_list, new TypeReference<List<VotingProcess>>(){});
        }
        resp.setRestBody(toSend);
        
        return resp;
    }
    
    public Response setupVotingProcess_Request(Login login, VotingProcess vp) throws IOException {
        baseUrl.loadClientCompanyQueryV1Path();
        WebTarget webTarget = baseUrl.getWebTarget();
        
        String json_resp = webTarget.path("setupvotingprocess")
                .queryParam("userId", login.getUserId()).queryParam("password", login.getPassword())
                .request(javax.ws.rs.core.MediaType.APPLICATION_JSON)
                .post(javax.ws.rs.client.Entity.entity(vp, javax.ws.rs.core.MediaType.APPLICATION_JSON), String.class);
        
        Response resp = mapper.readValue(json_resp, Response.class);
        
        return resp;
    }
    
    public Response closeVotingProcess_Request(Login login, int vpId) throws IOException {
        baseUrl.loadClientCompanyQueryV1Path();
        WebTarget webTarget = baseUrl.getWebTarget();
        
        String json_resp = webTarget.path("closevotingprocess")
                .queryParam("userId", login.getUserId()).queryParam("password", login.getPassword())
                .queryParam("vpId", vpId)
                .request(javax.ws.rs.core.MediaType.APPLICATION_JSON)
                .get(String.class);
        
        Response resp = mapper.readValue(json_resp, Response.class);
        
        return resp;
    }
}
