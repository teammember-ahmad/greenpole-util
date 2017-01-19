/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.greenpole.util.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import org.greenpole.entity.model.user.UserAccess;
import org.greenpole.entity.response.Response;

/**
 *
 * @author Akinwale.Agbaje
 */
public class RestCheckTester {
    public static void main(String[] args) throws IOException {
        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
        RestCheck check = new RestCheck();
        System.out.println("started running...");
        String rtn = check.doPost();
        System.out.println(rtn);
        System.out.println("##################################################");
        System.out.println("##################################################");
        Response resp = mapper.readValue(rtn, Response.class);
        //Response resp2 = mapper.convertValue(rtn, Response.class);
        
        
        System.out.println(mapper.writeValueAsString(resp));
        
        List<UserAccess> ulist = (List<UserAccess>) resp.getRestBody();
        for (UserAccess a : ulist) {
            System.out.println("email: " + a.getEmail());
        }
        
        /*String json = mapper.writeValueAsString(resp.getRestBody());
        List<UserAccess> ulist = mapper.readValue(json, new TypeReference<List<UserAccess>>(){});
        for (UserAccess a : ulist) {
            System.out.println("email: " + a.getEmail());
        }*/
        System.out.println("done running!");
    }
}
