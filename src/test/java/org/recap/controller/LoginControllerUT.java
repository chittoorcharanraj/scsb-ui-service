package org.recap.controller;

import org.apache.shiro.authc.UsernamePasswordToken;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.recap.BaseTestCaseUT;
import org.recap.RecapConstants;
import org.recap.model.usermanagement.UserForm;
import org.recap.security.UserInstitutionCache;
import org.recap.util.PropertyUtil;
import org.recap.util.UserAuthUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.token.TokenStore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SecurityContextHolder.class)
public class LoginControllerUT extends BaseTestCaseUT{

    @InjectMocks
    LoginController loginController;

    @Mock
    UserAuthUtil userAuthUtil;

    @Mock
    UserInstitutionCache userInstitutionCache;

    @Mock
    PropertyUtil propertyUtil;

    @Mock
    TokenStore tokenStore;

    @Mock
    OAuth2AccessToken oAuth2AccessToken;

    @Mock
    OAuth2AuthenticationDetails oAuth2AuthenticationDetails;

    @Mock
    OAuth2Request oAuth2Request;

    @Mock
    HttpServletRequest request;

    @Mock
    HttpSession session;

    @Mock
    HttpServletResponse response;

    @Mock
    SecurityContext securityContext;

    @Test
    public void setup(){}

    @Test
    public void loginScreenTest(){
        String response = loginController.loginScreen(request);
        assertNotNull(response);
        assertEquals(response,"forward:/index.html");
    }
    @Test
    public void home(){
        String response = loginController.home(request);
        assertNotNull(response);
        assertEquals(response,"forward:/index.html");
    }
    @Test
    public void logOutTest() throws Exception {
        UserForm userForm = new UserForm();
        userForm.setUsername("SuperAdmin");
        userForm.setInstitution("1");
        userForm.setPassword("12345");
        UsernamePasswordToken token=new UsernamePasswordToken(userForm.getUsername()+ RecapConstants.TOKEN_SPLITER +userForm.getInstitution(),userForm.getPassword(),true);
        Mockito.when(request.getSession(false)).thenReturn(session);
        Mockito.when(session.getAttribute(RecapConstants.USER_TOKEN)).thenReturn(token);
        Mockito.when( userAuthUtil.authorizedUser(RecapConstants.SCSB_SHIRO_LOGOUT_URL, (UsernamePasswordToken) session.getAttribute(RecapConstants.USER_TOKEN))).thenReturn(Boolean.TRUE);
        String response = loginController.logoutUser(request);
        assertNotNull(response);
    }
    @Test
    public void testLogin(){

        Authentication auth = getAuthentication();
        Map<String, Object> additionalInformation = new HashMap<>();
        additionalInformation.put("sub","testName");
        Mockito.when(request.getSession()).thenReturn(session);
        Mockito.when(request.getSession(false)).thenReturn(session);
        Mockito.when(request.getSession(true)).thenReturn(session);
        Mockito.when(session.getId()).thenReturn("66");
        Mockito.when(oAuth2AuthenticationDetails.getTokenValue()).thenReturn("token");
        Mockito.when(request.getParameter("institution")).thenReturn("PUL");
        PowerMockito.mockStatic(SecurityContextHolder.class);
        PowerMockito.when(SecurityContextHolder.getContext()).thenReturn(securityContext);
        Mockito.when(securityContext.getAuthentication()).thenReturn(auth);
        Mockito.when(userInstitutionCache.getInstitutionForRequestSessionId(any())).thenReturn("PUL");
        Mockito.doNothing().when(userInstitutionCache).removeSessionId(any());
        Mockito.doNothing().when(userInstitutionCache).addRequestSessionId(any(),any());
        Mockito.when(propertyUtil.getPropertyByInstitutionAndKey(anyString(), anyString())).thenReturn(RecapConstants.AUTH_TYPE_OAUTH);
        Mockito.when(tokenStore.readAccessToken(any())).thenReturn(oAuth2AccessToken);
        Mockito.when(oAuth2AccessToken.getAdditionalInformation()).thenReturn(additionalInformation);
        String responseStr = loginController.login(request,response);
        assertNotNull(responseStr);
    }
    @Test
    public void testLoginWithDiffAuthType() throws Exception {

        Authentication auth = getAuthentication();
        Map<String, Object> additionalInformation = new HashMap<>();
        additionalInformation.put("sub","testName");
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put(RecapConstants.IS_USER_AUTHENTICATED,Boolean.FALSE);
        resultMap.put(RecapConstants.USER_AUTH_ERRORMSG,"AuthenticationFailed");
        Mockito.when(request.getSession()).thenReturn(session);
        Mockito.when(request.getSession(false)).thenReturn(session);
        Mockito.when(request.getSession(true)).thenReturn(session);
        Mockito.when(session.getId()).thenReturn("66");
        Mockito.when(oAuth2AuthenticationDetails.getTokenValue()).thenReturn("token");
        Mockito.when(request.getParameter("institution")).thenReturn("PUL");
        PowerMockito.mockStatic(SecurityContextHolder.class);
        PowerMockito.when(SecurityContextHolder.getContext()).thenReturn(securityContext);
        Mockito.when(securityContext.getAuthentication()).thenReturn(auth);
        Mockito.when(userInstitutionCache.getInstitutionForRequestSessionId(any())).thenReturn("PUL");
        Mockito.doNothing().when(userInstitutionCache).removeSessionId(any());
        Mockito.doNothing().when(userInstitutionCache).addRequestSessionId(any(),any());
        Mockito.when(propertyUtil.getPropertyByInstitutionAndKey(anyString(), anyString())).thenReturn(RecapConstants.AUTH);
        Mockito.when(tokenStore.readAccessToken(any())).thenReturn(oAuth2AccessToken);
        Mockito.when(oAuth2AccessToken.getAdditionalInformation()).thenReturn(additionalInformation);
        Mockito.when(userAuthUtil.doAuthentication(any())).thenReturn(resultMap);
        String responseStr = loginController.login(request,response);
        assertNotNull(responseStr);
    }
    @Test
    public void testLoginWithDiffAuthTypeAndWithSuperAdmin() throws Exception {

        Authentication auth = getAuthentication();
        Map<String, Object> additionalInformation = new HashMap<>();
        additionalInformation.put("sub","testName");
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put(RecapConstants.IS_USER_AUTHENTICATED,Boolean.TRUE);
        resultMap.put(RecapConstants.USER_AUTH_ERRORMSG,"AuthenticationFailed");
        Mockito.when(request.getSession()).thenReturn(session);
        Mockito.when(request.getSession(false)).thenReturn(session);
        Mockito.when(request.getSession(true)).thenReturn(session);
        Mockito.when(session.getId()).thenReturn("66");
        Mockito.when(session.getAttribute(RecapConstants.SUPER_ADMIN_USER)).thenReturn(Boolean.TRUE);
        Mockito.when(oAuth2AuthenticationDetails.getTokenValue()).thenReturn("token");
        Mockito.when(request.getParameter("institution")).thenReturn("PUL");
        PowerMockito.mockStatic(SecurityContextHolder.class);
        PowerMockito.when(SecurityContextHolder.getContext()).thenReturn(securityContext);
        Mockito.when(securityContext.getAuthentication()).thenReturn(auth);
        Mockito.when(userInstitutionCache.getInstitutionForRequestSessionId(any())).thenReturn("PUL");
        Mockito.doNothing().when(userInstitutionCache).removeSessionId(any());
        Mockito.doNothing().when(userInstitutionCache).addRequestSessionId(any(),any());
        Mockito.when(propertyUtil.getPropertyByInstitutionAndKey(anyString(), anyString())).thenReturn(RecapConstants.AUTH);
        Mockito.when(tokenStore.readAccessToken(any())).thenReturn(oAuth2AccessToken);
        Mockito.when(oAuth2AccessToken.getAdditionalInformation()).thenReturn(additionalInformation);
        Mockito.when(userAuthUtil.doAuthentication(any())).thenReturn(resultMap);
        String responseStr = loginController.login(request,response);
        assertNotNull(responseStr);
    }
    @Test
    public void testLoginWithDiffAuthTypeAndWithoutSuperAdmin() throws Exception {

        Authentication auth = getAuthentication();
        Map<String, Object> additionalInformation = new HashMap<>();
        additionalInformation.put("sub","testName");
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put(RecapConstants.IS_USER_AUTHENTICATED,Boolean.TRUE);
        resultMap.put(RecapConstants.USER_AUTH_ERRORMSG,"AuthenticationFailed");
        Mockito.when(request.getSession()).thenReturn(session);
        Mockito.when(request.getSession(false)).thenReturn(session);
        Mockito.when(request.getSession(true)).thenReturn(session);
        Mockito.when(session.getId()).thenReturn("66");
        Mockito.when(session.getAttribute(RecapConstants.SUPER_ADMIN_USER)).thenReturn(Boolean.FALSE);
        Mockito.when(oAuth2AuthenticationDetails.getTokenValue()).thenReturn("token");
        Mockito.when(request.getParameter("institution")).thenReturn("PUL");
        PowerMockito.mockStatic(SecurityContextHolder.class);
        PowerMockito.when(SecurityContextHolder.getContext()).thenReturn(securityContext);
        Mockito.when(securityContext.getAuthentication()).thenReturn(auth);
        Mockito.when(userInstitutionCache.getInstitutionForRequestSessionId(any())).thenReturn("PUL");
        Mockito.doNothing().when(userInstitutionCache).removeSessionId(any());
        Mockito.doNothing().when(userInstitutionCache).addRequestSessionId(any(),any());
        Mockito.when(propertyUtil.getPropertyByInstitutionAndKey(anyString(), anyString())).thenReturn(RecapConstants.AUTH);
        Mockito.when(tokenStore.readAccessToken(any())).thenReturn(oAuth2AccessToken);
        Mockito.when(oAuth2AccessToken.getAdditionalInformation()).thenReturn(additionalInformation);
        Mockito.when(userAuthUtil.doAuthentication(any())).thenReturn(resultMap);
        String responseStr = loginController.login(request,response);
        assertNotNull(responseStr);
    }
    private Authentication getAuthentication(){

        Authentication auth = new Authentication() {


            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return null;
            }

            @Override
            public Object getCredentials() {
                return null;
            }

            @Override
            public Object getDetails() {
                return oAuth2AuthenticationDetails;
            }

            @Override
            public Object getPrincipal() {
                return null;
            }

            @Override
            public boolean isAuthenticated() {
                return false;
            }

            @Override
            public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

            }

            @Override
            public String getName() {
                return "test";
            }
        };
        OAuth2Authentication oauth = new OAuth2Authentication(oAuth2Request,auth);
        return oauth;
    }
}
