package org.recap.util;

import org.apache.shiro.authc.UsernamePasswordToken;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCase;
import org.recap.BaseTestCaseUT;
import org.recap.RecapConstants;
import org.recap.model.usermanagement.UserForm;
import org.recap.service.RestHeaderService;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;

/**
 * Created by dharmendrag on 7/2/17.
 */
public class UserAuthUtilUT extends BaseTestCaseUT {

    @InjectMocks
    UserAuthUtil userAuthUtil;

    @Mock
    RestHeaderService restHeaderService;

    @Mock
    RestTemplate restTemplate;

    @Test
    public void testLoginAndAuthorization()throws Exception{
        boolean authorized=false;
        UserForm userForm=new UserForm();
        userForm.setUsername("john");
        userForm.setInstitution("CUL");
        userForm.setPassword("123");

        Map<String,Object> map = new HashMap<>();
        map.put("isAuthenticated",true);
        map.put("userName","john");
        map.put(RecapConstants.USER_ID,"CUL");
        UsernamePasswordToken token=new UsernamePasswordToken(userForm.getUsername()+ RecapConstants.TOKEN_SPLITER +"CUL",userForm.getPassword(),true);
        try {
            Map<String, Object> resultmap = (Map<String, Object>) userAuthUtil.doAuthentication(token);
        }catch (Exception e){}
        authorized=userAuthUtil.authorizedUser(RecapConstants.SCSB_SHIRO_COLLECTION_URL,token);

    }

}
