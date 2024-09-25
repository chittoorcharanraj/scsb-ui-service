package org.recap.controller;

import org.apache.shiro.authc.UsernamePasswordToken;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.recap.BaseTestCase;
import org.recap.ScsbConstants;
import org.recap.model.usermanagement.UserForm;
import org.recap.util.UserAuthUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class AuthenticationControllerUT  {
    @Mock
    HttpSession session;

    @Mock
    private UserAuthUtil userAuthUtil;

    @Mock
    HttpServletRequest request;


    @Test
    public void isAuthenticated() throws Exception{
        when(request.getSession()).thenReturn(session);
        UserForm userForm = new UserForm();
        userForm.setUsername("kholi");
        userForm.setInstitution("3");
        userForm.setPassword("12345");
        UsernamePasswordToken token=new UsernamePasswordToken(userForm.getUsername()+ ScsbConstants.TOKEN_SPLITER +userForm.getInstitution(),userForm.getPassword(),true);
        when(session.getAttribute(ScsbConstants.USER_TOKEN)).thenReturn(token);
        String roleURL = "http://localhost:9091/role/search";
        AuthenticationController authenticationController = new AuthenticationController();

       // Mockito.when(authenticationController.getUserAuthUtil().authorizedUser(roleURL, (UsernamePasswordToken) session.getAttribute(RecapConstants.USER_TOKEN))).thenReturn(true);
        //authenticationController.isAuthenticated(session,roleURL);
    }
}
