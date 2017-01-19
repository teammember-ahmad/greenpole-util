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
import org.greenpole.entity.model.holder.HolderAccreditation;
import org.greenpole.entity.model.holder.HolderVoting;
import org.greenpole.entity.model.holder.QueryHolder;
import org.greenpole.entity.response.Response;
import org.greenpole.entity.rest.CarrierWrapper;
import org.greenpole.entity.security.Login;

/**
 *
 * @author Akinwale.Agbaje
 */
public class HolderRestInterface {
    private final BaseUrlInterface baseUrl;
    private final ObjectMapper mapper;

    public HolderRestInterface() {
        baseUrl = new BaseUrlInterface();
        mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
    
    public Response queryHolderAccreditationList_Request(Login login, int gmId) throws IOException {
        baseUrl.loadHolderQueryV1Path();
        WebTarget webTarget = baseUrl.getWebTarget();
        
        String json_resp = webTarget.path("queryholderaccreditation")
                .queryParam("userId", login.getUserId()).queryParam("password", login.getPassword()).queryParam("gmId", gmId)
                .request(javax.ws.rs.core.MediaType.APPLICATION_JSON)
                .get(String.class);
        
        Response resp = mapper.readValue(json_resp, Response.class);
        
        List<HolderAccreditation> accreditation_result = new ArrayList<>();
        if (resp.getRestBody() != null && !resp.getRestBody().isEmpty()) {
            String json_list = mapper.writeValueAsString(resp.getRestBody());
            accreditation_result = mapper.readValue(json_list, new TypeReference<List<HolderAccreditation>>(){});
        }
        resp.setRestBody(accreditation_result);
        
        return resp;
    }
    
    public Response saveVoteResult_Request(Login login, List<HolderVoting> holderVotingList) throws IOException {
        baseUrl.loadHolderRequestV1Path();
        WebTarget webTarget = baseUrl.getWebTarget();
        
        CarrierWrapper carrier = new CarrierWrapper();
        carrier.setHolderVotingList(holderVotingList);
        
        String json_resp = webTarget.path("savevoteresult")
                .queryParam("userId", login.getUserId()).queryParam("password", login.getPassword())
                .request(javax.ws.rs.core.MediaType.APPLICATION_JSON)
                .post(javax.ws.rs.client.Entity.entity(carrier, javax.ws.rs.core.MediaType.APPLICATION_JSON), String.class);
        
        Response resp = mapper.readValue(json_resp, Response.class);
        
        return resp;
    }
    
    /*public Response queryHolderPagination_Request(Login login, QueryHolder queryParams, int pageNumber, int pageSize) {
        baseUrl.loadHolderQueryV1Path();
        WebTarget webTarget = baseUrl.getWebTarget();
        
        String json_resp = webTarget.path("queryholderpagination")
                .queryParam("userId", login.getUserId()).queryParam("password", login.getPassword())
                .request(javax.ws.rs.core.MediaType.APPLICATION_JSON)
                .post(javax.ws.rs.client.Entity.entity(carrier, javax.ws.rs.core.MediaType.APPLICATION_JSON), String.class);
        
        Response resp = mapper.readValue(json_resp, Response.class);
        
        return resp;
    }*/
}
