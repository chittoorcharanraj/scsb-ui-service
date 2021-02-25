package org.recap.util;

import org.apache.shiro.authc.UsernamePasswordToken;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.RecapConstants;
import org.recap.model.usermanagement.UserDetailsForm;
import org.recap.service.RestHeaderService;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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

    @Mock
    UsernamePasswordToken token;

    @Mock
    HttpSession session;

    @Mock
    HttpServletRequest httpServletRequest;

    @Mock
    HttpSession httpSession;


    @Test
    public void testdoAuthentication() {
        Mockito.when(session.getAttribute(Mockito.anyString())).thenReturn(true);
        Mockito.when(session.getAttribute(RecapConstants.USER_INSTITUTION)).thenReturn(1);
        UserDetailsForm userDetailsForm=userAuthUtil.getUserDetails(session,"");
        assertTrue(userDetailsForm.isRecapUser());
    }

    @Test
    public void testLoginAndAuthorizationExceptionLogOut() {
        boolean authorized = userAuthUtil.authorizedUser(RecapConstants.LOGOUT, token);
        assertFalse(authorized);
    }

    @Test
    public void testLoginAndAuthorException() {
        Mockito.when(httpServletRequest.getSession(false)).thenReturn(httpSession);
        Mockito.when(httpSession.getAttribute(RecapConstants.USER_TOKEN)).thenReturn(token);
        boolean authorized = userAuthUtil.isAuthenticated(httpServletRequest,RecapConstants.SCSB_SHIRO_COLLECTION_URL);
        assertFalse(authorized);
    }


}
