/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.greenpole.util.agm;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.greenpole.entity.response.Response;
import org.greenpole.entity.security.Login;

/**
 *
 * @author Akinwale.Agbaje
 */
public class RestTest {
    public static void main(String[] args) throws IOException {
        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        HolderRestInterface rest = new HolderRestInterface();
        
        Login login = new Login();
        login.setUserId("akinwale.agbaje@africaprudentialregistrars.com");
        login.setPassword("ZOLHvWZMDck91zeog8SWTIdx2zjvK/Wry+N4BGZjRyYwCinsMrgM0OAj2trXcoVwfyeREE52lq5dWIPWcvEiVjevFdxSqRjFMgdXFJ6gJzCImjWtIF3A8VKLIbScsHlPY2ZdcPZSw8mt9V55PbeViRb/CbvU6RPQXJZzrid3ewE=");
        
        Response resp = rest.queryHolderAccreditationList_Request(login, 1);
        System.out.println(mapper.writeValueAsString(resp));
    }
}
