/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.greenpole.util.agm;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.greenpole.entity.model.holder.Holder;
import org.greenpole.entity.model.holder.HolderCompanyAccount;
import org.greenpole.entity.model.holder.QueryHolder;
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
        ClientCompanyRestInterface rest2 = new ClientCompanyRestInterface();
        
        Login login = new Login();
        login.setUserId("akinwale.agbaje@africaprudentialregistrars.com");
        login.setPassword("ZOLHvWZMDck91zeog8SWTIdx2zjvK/Wry+N4BGZjRyYwCinsMrgM0OAj2trXcoVwfyeREE52lq5dWIPWcvEiVjevFdxSqRjFMgdXFJ6gJzCImjWtIF3A8VKLIbScsHlPY2ZdcPZSw8mt9V55PbeViRb/CbvU6RPQXJZzrid3ewE=");
        
        //for holder query
        Holder h = new Holder();
        HolderCompanyAccount hca = new HolderCompanyAccount();
        hca.setId(49);
        
        List<HolderCompanyAccount> hcalist = new ArrayList<>();
        hcalist.add(hca);
        
        h.setCompanyAccounts(hcalist);
        QueryHolder queryParams = new QueryHolder();
        queryParams.setDescriptor("holder:main;units:none;totalHoldings:none;any:none");
        queryParams.setHolder(h);
        
        //Response resp = rest.queryHolderAccreditationList_Request(login, 1);
        //Response resp = rest.queryHolderPagination_Request(login, queryParams, 1, 1);
        Response resp = rest2.queryAllClientCompanies_Request(login);
        System.out.println(mapper.writeValueAsString(resp));
    }
}
