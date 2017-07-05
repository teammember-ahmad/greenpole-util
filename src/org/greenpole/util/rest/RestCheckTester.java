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
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.greenpole.entity.model.profiler.UserProfile;
import org.greenpole.entity.model.profiler.ViewGroup;
import org.greenpole.entity.model.user.UserAccess;
import org.greenpole.entity.response.Response;

/**
 *
 * @author Akinwale.Agbaje
 */
public class RestCheckTester {
    public static void main(String[] args) throws IOException, JAXBException {
        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        
        RestCheck check = new RestCheck();
        System.out.println("started running...");
        Response rtn = check.doPost();
        
        JAXBContext context = JAXBContext.newInstance(Response.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(rtn, System.out);
        
        Map<String, ViewGroup> views = ((UserProfile)rtn.getRestBody().get(0)).getViews();
        for (Map.Entry pairs : views.entrySet()) {
            String key = (String) pairs.getKey();
            ViewGroup group = (ViewGroup) pairs.getValue();
            
            System.out.println("key: " + key);
            System.out.println("group: " + group.getGroupName());
        }
        
        System.out.println(rtn);
        System.out.println("##################################################");
        System.out.println("##################################################");
        
        System.exit(0);
        Response resp = mapper.readValue("", Response.class);
        //Response resp2 = mapper.convertValue(rtn, Response.class);
        
        
        System.out.println(mapper.writeValueAsString(resp));
        System.out.println("retn: " + resp.getRetn());
        
        /*List<UserAccess> ulist = (List<UserAccess>) resp.getRestBody();
        for (UserAccess a : ulist) {
            System.out.println("email: " + a.getEmail());
        }*/
        
        String json = mapper.writeValueAsString(resp.getRestBody());
        System.out.println("############################################################");
        System.out.println("############################################################");
        System.out.println("############################################################");
        System.out.println("############################################################");
        System.out.println(json);
        List<UserProfile> uprofile_list = mapper.readValue(json, new TypeReference<List<UserProfile>>(){});
        System.out.println("############################################################");
        System.out.println("############################################################");
        System.out.println("############################################################");
        System.out.println("############################################################");
        System.out.println(mapper.writeValueAsString(uprofile_list));
        
        System.out.println("############################################################");
        System.out.println("############################################################");
        System.out.println("########################-- LIST OF USER PROFILE --####################################");
        for (Object up : uprofile_list) {
            String obj_json = mapper.writeValueAsString(up);
            System.out.println(obj_json);
            UserProfile profile = mapper.readValue(obj_json, UserProfile.class);
        }
        
        /*List<UserAccess> ulist = mapper.readValue(json, new TypeReference<List<UserAccess>>(){});
        for (UserAccess a : ulist) {
            System.out.println("email: " + a.getEmail());
        }*/
        System.out.println("done running!");
    }
}
