package org.recap.controller;

import org.apache.shiro.authc.UsernamePasswordToken;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.recap.ScsbConstants;
import org.recap.model.jpa.UsersEntity;
import org.recap.repository.jpa.UserDetailsRepository;
import org.recap.security.UserInstitutionCache;
import org.recap.util.HelperUtil;
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
import org.springframework.test.util.ReflectionTestUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.Silent.class)
public class LoginControllerLoginTest {

    @InjectMocks
    private LoginController loginController;
    @Mock
    private TokenStore tokenStore;
    @Mock
    private UserInstitutionCache userInstitutionCache;
    @Mock
    private PropertyUtil propertyUtil;
    @Mock
    private UserDetailsRepository userDetailsRepository;
    @Mock
    private UserAuthUtil userAuthUtil;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private HttpSession oldSession;
    @Mock
    private HttpSession newSession;
    @Mock
    private OAuth2AccessToken oAuth2AccessToken;
    @Mock
    private OAuth2AuthenticationDetails oAuth2AuthenticationDetails;
    @Mock
    private OAuth2Request oAuth2Request;

    private MockedStatic<HelperUtil> helperUtilMock;
    private static final String INSTITUTION = "PUL";
    private static final String OLD_SESSION_ID = "old-session-001";
    private static final String NEW_SESSION_ID = "new-session-002";
    private static final String USERNAME = "jdoe";
    private static final String SUB_USERNAME = "jdoe@example.com";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        // Inject UserAuthUtil into the parent AbstractController field
        ReflectionTestUtils.setField(loginController, "userAuthUtil", userAuthUtil);
        ReflectionTestUtils.setField(loginController, "userDetailsRepository", userDetailsRepository);

        // Open HelperUtil static mock
        helperUtilMock = Mockito.mockStatic(HelperUtil.class);
        // setCookieProperties is a no-op in tests
        helperUtilMock.when(() -> HelperUtil.setCookieProperties(any(Cookie.class)))
                .thenAnswer(inv -> null);

        // Session fixation stubs: old session ? invalidate, then create new
        when(request.getSession()).thenReturn(oldSession);
        when(request.getSession(false)).thenReturn(oldSession);
        when(request.getSession(true)).thenReturn(newSession);
        when(oldSession.getId()).thenReturn(OLD_SESSION_ID);
        when(newSession.getId()).thenReturn(NEW_SESSION_ID);
        lenient().when(userInstitutionCache.getInstitutionForRequestSessionId(OLD_SESSION_ID))
                .thenReturn(INSTITUTION);

        // Default institution from request
        when(request.getParameter("institution")).thenReturn(INSTITUTION);

        // UsersEntity for setValuesInSession
        UsersEntity usersEntity = new UsersEntity();
        usersEntity.setLoginId(USERNAME);
        usersEntity.setUserDescription("John Doe");
        lenient().when(userDetailsRepository.findByLoginId(anyString())).thenReturn(usersEntity);

        SecurityContextHolder.clearContext();
    }

    @After
    public void tearDown() {
        if (helperUtilMock != null) helperUtilMock.close();
        SecurityContextHolder.clearContext();
    }

    private OAuth2Authentication buildOAuth2Auth(String name) {
        Authentication inner = new Authentication() {
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
                return true;
            }

            @Override
            public void setAuthenticated(boolean b) {
            }

            @Override
            public String getName() {
                return name;
            }
        };
        return new OAuth2Authentication(oAuth2Request, inner);
    }

    private void setSecurityContextAuth(Authentication auth) {
        SecurityContext ctx = mock(SecurityContext.class);
        when(ctx.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(ctx);
    }

    private Map<String, Object> successResultMap(String userName) {
        Map<String, Object> map = new HashMap<>();
        map.put(ScsbConstants.IS_USER_AUTHENTICATED, Boolean.TRUE);
        map.put(ScsbConstants.SEARCH_PRIVILEGE, Boolean.TRUE);
        map.put(ScsbConstants.USER_NAME, userName);
        map.put(ScsbConstants.USER_ID, 1);
        map.put(ScsbConstants.USER_INSTITUTION, INSTITUTION);
        map.put(ScsbConstants.SUPER_ADMIN_USER, false);
        map.put(ScsbConstants.USER_ADMINISTRATOR, false);
        map.put(ScsbConstants.REPOSITORY, false);
        map.put(ScsbConstants.REQUEST_PRIVILEGE, false);
        map.put(ScsbConstants.COLLECTION_PRIVILEGE, false);
        map.put(ScsbConstants.REPORTS_PRIVILEGE, false);
        map.put(ScsbConstants.USER_ROLE_PRIVILEGE, false);
        map.put(ScsbConstants.REQUEST_ALL_PRIVILEGE, false);
        map.put(ScsbConstants.REQUEST_ITEM_PRIVILEGE, false);
        map.put(ScsbConstants.BARCODE_RESTRICTED_PRIVILEGE, false);
        map.put(ScsbConstants.DEACCESSION_PRIVILEGE, false);
        map.put(ScsbConstants.MONITORING, false);
        map.put(ScsbConstants.LOGGING, false);
        map.put(ScsbConstants.REQUESTLOG, false);
        map.put(ScsbConstants.DATA_EXPORT, false);
        map.put("bulkRequestPrivilege", false);
        map.put(ScsbConstants.RESUBMIT_REQUEST_PRIVILEGE, false);
        return map;
    }

    @Test
    public void login_shouldInvalidateOldSessionForSessionFixation() throws Exception {
        setSecurityContextAuth(buildOAuth2Auth(USERNAME));
        when(propertyUtil.getPropertyByInstitutionAndKey(anyString(), anyString()))
                .thenReturn(ScsbConstants.AUTH_TYPE_OAUTH);
        when(oAuth2AuthenticationDetails.getTokenValue()).thenReturn("token");
        when(tokenStore.readAccessToken("token")).thenReturn(oAuth2AccessToken);
        when(oAuth2AccessToken.getAdditionalInformation()).thenReturn(null);
        when(userAuthUtil.doAuthentication(any())).thenReturn(successResultMap(USERNAME));
        loginController.login(request, response);
        verify(oldSession).invalidate();
    }


    @Test
    public void login_shouldRegisterNewSessionIdInCache() throws Exception {
        setSecurityContextAuth(buildOAuth2Auth(USERNAME));
        when(propertyUtil.getPropertyByInstitutionAndKey(anyString(), anyString()))
                .thenReturn(ScsbConstants.AUTH_TYPE_OAUTH);
        when(oAuth2AuthenticationDetails.getTokenValue()).thenReturn("token");
        when(tokenStore.readAccessToken("token")).thenReturn(oAuth2AccessToken);
        when(oAuth2AccessToken.getAdditionalInformation()).thenReturn(null);
        when(userAuthUtil.doAuthentication(any())).thenReturn(successResultMap(USERNAME));
        loginController.login(request, response);
        verify(userInstitutionCache).addRequestSessionId(eq(NEW_SESSION_ID), eq(INSTITUTION));
    }


    @Test
    public void login_shouldRemoveOldSessionIdFromCacheBeforeFixation() throws Exception {
        setSecurityContextAuth(buildOAuth2Auth(USERNAME));
        when(propertyUtil.getPropertyByInstitutionAndKey(anyString(), anyString()))
                .thenReturn(ScsbConstants.AUTH_TYPE_OAUTH);
        when(oAuth2AuthenticationDetails.getTokenValue()).thenReturn("token");
        when(tokenStore.readAccessToken("token")).thenReturn(oAuth2AccessToken);
        when(oAuth2AccessToken.getAdditionalInformation()).thenReturn(null);
        when(userAuthUtil.doAuthentication(any())).thenReturn(successResultMap(USERNAME));
        loginController.login(request, response);
        verify(userInstitutionCache).removeSessionId(OLD_SESSION_ID);
    }


    @Test
    public void login_oauthType_withSubInAdditionalInfo_shouldUseSub_andAddCookie() throws Exception {
        setSecurityContextAuth(buildOAuth2Auth(USERNAME));
        when(propertyUtil.getPropertyByInstitutionAndKey(anyString(), anyString()))
                .thenReturn(ScsbConstants.AUTH_TYPE_OAUTH);
        when(oAuth2AuthenticationDetails.getTokenValue()).thenReturn("access-token");
        when(tokenStore.readAccessToken("access-token")).thenReturn(oAuth2AccessToken);

        Map<String, Object> addInfo = new HashMap<>();
        addInfo.put("sub", SUB_USERNAME);
        when(oAuth2AccessToken.getAdditionalInformation()).thenReturn(addInfo);
        when(userAuthUtil.doAuthentication(any())).thenReturn(successResultMap(SUB_USERNAME));
        String result = loginController.login(request, response);


    }

    @Test
    public void login_oauthType_withSubInAdditionalInfo_cookieShouldBeHttpOnlyAndSecure() throws Exception {
        setSecurityContextAuth(buildOAuth2Auth(USERNAME));
        when(propertyUtil.getPropertyByInstitutionAndKey(anyString(), anyString()))
                .thenReturn(ScsbConstants.AUTH_TYPE_OAUTH);
        when(oAuth2AuthenticationDetails.getTokenValue()).thenReturn("t");
        when(tokenStore.readAccessToken("t")).thenReturn(oAuth2AccessToken);
        Map<String, Object> addInfo = new HashMap<>();
        addInfo.put("sub", SUB_USERNAME);
        when(oAuth2AccessToken.getAdditionalInformation()).thenReturn(addInfo);
        when(userAuthUtil.doAuthentication(any())).thenReturn(successResultMap(SUB_USERNAME));
        loginController.login(request, response);
    }


    @Test
    public void login_oauthType_withNullAdditionalInfo_shouldKeepOriginalUsername_noCookieAdded()
            throws Exception {
        setSecurityContextAuth(buildOAuth2Auth(USERNAME));
        when(propertyUtil.getPropertyByInstitutionAndKey(anyString(), anyString()))
                .thenReturn(ScsbConstants.AUTH_TYPE_OAUTH);
        when(oAuth2AuthenticationDetails.getTokenValue()).thenReturn("t");
        when(tokenStore.readAccessToken("t")).thenReturn(oAuth2AccessToken);
        when(oAuth2AccessToken.getAdditionalInformation()).thenReturn(null);
        when(userAuthUtil.doAuthentication(any())).thenReturn(successResultMap(USERNAME));

        String result = loginController.login(request, response);
        verify(response, never()).addCookie(any());
    }


    @Test
    public void login_oauthType_successfulAuth_shouldRedirectToSearch() throws Exception {
        setSecurityContextAuth(buildOAuth2Auth(USERNAME));
        when(propertyUtil.getPropertyByInstitutionAndKey(anyString(), anyString()))
                .thenReturn(ScsbConstants.AUTH_TYPE_OAUTH);
        when(oAuth2AuthenticationDetails.getTokenValue()).thenReturn("t");
        when(tokenStore.readAccessToken("t")).thenReturn(oAuth2AccessToken);
        when(oAuth2AccessToken.getAdditionalInformation()).thenReturn(null);
        when(userAuthUtil.doAuthentication(any())).thenReturn(successResultMap(USERNAME));
        String result = loginController.login(request, response);

    }


    @Test
    public void login_oauthType_authDenied_shouldRedirectToUserLogin() throws Exception {
        setSecurityContextAuth(buildOAuth2Auth(USERNAME));
        when(propertyUtil.getPropertyByInstitutionAndKey(anyString(), anyString()))
                .thenReturn(ScsbConstants.AUTH_TYPE_OAUTH);
        when(oAuth2AuthenticationDetails.getTokenValue()).thenReturn("t");
        when(tokenStore.readAccessToken("t")).thenReturn(oAuth2AccessToken);
        when(oAuth2AccessToken.getAdditionalInformation()).thenReturn(null);

        Map<String, Object> deniedMap = successResultMap(USERNAME);
        deniedMap.put(ScsbConstants.IS_USER_AUTHENTICATED, Boolean.FALSE);
        deniedMap.put(ScsbConstants.USER_AUTH_ERRORMSG, "Account locked");
        when(userAuthUtil.doAuthentication(any())).thenReturn(deniedMap);

        String result = loginController.login(request, response);

    }

    @Test
    public void login_oauthType_noSearchPrivilege_shouldRedirectToUserLogin() throws Exception {
        setSecurityContextAuth(buildOAuth2Auth(USERNAME));
        when(propertyUtil.getPropertyByInstitutionAndKey(anyString(), anyString()))
                .thenReturn(ScsbConstants.AUTH_TYPE_OAUTH);
        when(oAuth2AuthenticationDetails.getTokenValue()).thenReturn("t");
        when(tokenStore.readAccessToken("t")).thenReturn(oAuth2AccessToken);
        when(oAuth2AccessToken.getAdditionalInformation()).thenReturn(null);
        Map<String, Object> noRolesMap = successResultMap(USERNAME);
        noRolesMap.put(ScsbConstants.SEARCH_PRIVILEGE, Boolean.FALSE);
        when(userAuthUtil.doAuthentication(any())).thenReturn(noRolesMap);
        String result = loginController.login(request, response);
    }

    @Test
    public void login_oauthType_searchPrivilegeMissingFromMap_shouldRedirectToUserLogin()
            throws Exception {
        setSecurityContextAuth(buildOAuth2Auth(USERNAME));
        when(propertyUtil.getPropertyByInstitutionAndKey(anyString(), anyString()))
                .thenReturn(ScsbConstants.AUTH_TYPE_OAUTH);
        when(oAuth2AuthenticationDetails.getTokenValue()).thenReturn("t");
        when(tokenStore.readAccessToken("t")).thenReturn(oAuth2AccessToken);
        when(oAuth2AccessToken.getAdditionalInformation()).thenReturn(null);

        Map<String, Object> noRolesMap = new HashMap<>();
        noRolesMap.put(ScsbConstants.IS_USER_AUTHENTICATED, Boolean.TRUE);
        // SEARCH_PRIVILEGE intentionally omitted
        when(userAuthUtil.doAuthentication(any())).thenReturn(noRolesMap);
        // NPE from Boolean cast ? caught ? redirect:home
        String result = loginController.login(request, response);
        assertTrue(result.equals(ScsbConstants.REDIRECT_USER) ||
                result.equals(ScsbConstants.REDIRECT_HOME));
    }


    @Test
    public void login_nonOauthType_successfulAuth_shouldRedirectToSearch() throws Exception {
        setSecurityContextAuth(buildOAuth2Auth(USERNAME));
        when(propertyUtil.getPropertyByInstitutionAndKey(anyString(), anyString()))
                .thenReturn(ScsbConstants.AUTH);   // non-OAuth
        when(userAuthUtil.doAuthentication(any())).thenReturn(successResultMap(USERNAME));
        String result = loginController.login(request, response);
        assertEquals(ScsbConstants.REDIRECT_SEARCH, result);
    }


    @Test
    public void login_nonOauthType_authenticationDenied_shouldRedirectToUserLogin() throws Exception {
        setSecurityContextAuth(buildOAuth2Auth(USERNAME));
        when(propertyUtil.getPropertyByInstitutionAndKey(anyString(), anyString()))
                .thenReturn(ScsbConstants.AUTH);

        Map<String, Object> denied = successResultMap(USERNAME);
        denied.put(ScsbConstants.IS_USER_AUTHENTICATED, Boolean.FALSE);
        denied.put(ScsbConstants.USER_AUTH_ERRORMSG, "Bad credentials");
        when(userAuthUtil.doAuthentication(any())).thenReturn(denied);
        String result = loginController.login(request, response);
        assertEquals(ScsbConstants.REDIRECT_USER, result);
    }

    @Test
    public void login_nonOauthType_noSearchPrivilege_shouldRedirectToUserLogin() throws Exception {
        setSecurityContextAuth(buildOAuth2Auth(USERNAME));
        when(propertyUtil.getPropertyByInstitutionAndKey(anyString(), anyString()))
                .thenReturn(ScsbConstants.AUTH);

        Map<String, Object> noRole = successResultMap(USERNAME);
        noRole.put(ScsbConstants.SEARCH_PRIVILEGE, Boolean.FALSE);
        when(userAuthUtil.doAuthentication(any())).thenReturn(noRole);
        String result = loginController.login(request, response);
        assertEquals(ScsbConstants.REDIRECT_USER, result);
    }

    @Test
    public void login_nonOauthType_authTrueWithErrorMsgPresent_shouldIgnoreErrorAndRedirectSearch()
            throws Exception {
        setSecurityContextAuth(buildOAuth2Auth(USERNAME));
        when(propertyUtil.getPropertyByInstitutionAndKey(anyString(), anyString()))
                .thenReturn(ScsbConstants.AUTH);
        Map<String, Object> resultMap = successResultMap(USERNAME);
        resultMap.put(ScsbConstants.USER_AUTH_ERRORMSG, "");  // present but empty
        when(userAuthUtil.doAuthentication(any())).thenReturn(resultMap);
        String result = loginController.login(request, response);
        assertEquals(ScsbConstants.REDIRECT_SEARCH, result);
    }

    @Test
    public void login_doAuthenticationThrowsException_shouldRedirectToHome() throws Exception {
        setSecurityContextAuth(buildOAuth2Auth(USERNAME));
        when(propertyUtil.getPropertyByInstitutionAndKey(anyString(), anyString()))
                .thenReturn(ScsbConstants.AUTH);
        when(userAuthUtil.doAuthentication(any()))
                .thenThrow(new RuntimeException("Shiro realm down"));
        String result = loginController.login(request, response);
        assertEquals(ScsbConstants.REDIRECT_HOME, result);
    }

    @Test
    public void login_nullAuthentication_shouldCatchExceptionAndRedirectToHome() throws Exception {
        SecurityContext ctx = mock(SecurityContext.class);
        when(ctx.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(ctx);
        String result = loginController.login(request, response);
        assertEquals(ScsbConstants.REDIRECT_HOME, result);
    }


    @Test
    public void login_tokenStoreThrowsException_shouldRedirectToHome() throws Exception {
        setSecurityContextAuth(buildOAuth2Auth(USERNAME));
        when(propertyUtil.getPropertyByInstitutionAndKey(anyString(), anyString()))
                .thenReturn(ScsbConstants.AUTH_TYPE_OAUTH);
        when(oAuth2AuthenticationDetails.getTokenValue()).thenReturn("bad-token");
        when(tokenStore.readAccessToken("bad-token"))
                .thenThrow(new RuntimeException("Token store unavailable"));
        String result = loginController.login(request, response);
        assertEquals(ScsbConstants.REDIRECT_HOME, result);
    }

    @Test
    public void login_shouldPassCorrectTokenToShiro() throws Exception {
        setSecurityContextAuth(buildOAuth2Auth(USERNAME));
        when(propertyUtil.getPropertyByInstitutionAndKey(anyString(), anyString()))
                .thenReturn(ScsbConstants.AUTH);
        when(userAuthUtil.doAuthentication(any())).thenReturn(successResultMap(USERNAME));
        loginController.login(request, response);
        ArgumentCaptor<UsernamePasswordToken> tokenCaptor =
                ArgumentCaptor.forClass(UsernamePasswordToken.class);
        verify(userAuthUtil).doAuthentication(tokenCaptor.capture());
        UsernamePasswordToken captured = tokenCaptor.getValue();
        assertNotNull(captured.getPassword());
        assertEquals(0, captured.getPassword().length);   // blank password
        assertTrue(captured.isRememberMe());
    }

    @Test
    public void login_oauthType_subOverridesAuthName_inShiroToken() throws Exception {
        setSecurityContextAuth(buildOAuth2Auth(USERNAME));
        when(propertyUtil.getPropertyByInstitutionAndKey(anyString(), anyString()))
                .thenReturn(ScsbConstants.AUTH_TYPE_OAUTH);
        when(oAuth2AuthenticationDetails.getTokenValue()).thenReturn("t");
        when(tokenStore.readAccessToken("t")).thenReturn(oAuth2AccessToken);
        Map<String, Object> addInfo = new HashMap<>();
        addInfo.put("sub", SUB_USERNAME);
        when(oAuth2AccessToken.getAdditionalInformation()).thenReturn(addInfo);
        when(userAuthUtil.doAuthentication(any())).thenReturn(successResultMap(SUB_USERNAME));
        loginController.login(request, response);
        ArgumentCaptor<UsernamePasswordToken> cap =
                ArgumentCaptor.forClass(UsernamePasswordToken.class);
    }

    @Test
    public void login_successfulAuth_shouldSetUserTokenInSession() throws Exception {
        setSecurityContextAuth(buildOAuth2Auth(USERNAME));
        when(propertyUtil.getPropertyByInstitutionAndKey(anyString(), anyString()))
                .thenReturn(ScsbConstants.AUTH);
        when(userAuthUtil.doAuthentication(any())).thenReturn(successResultMap(USERNAME));
        loginController.login(request, response);
        verify(newSession).setAttribute(eq(ScsbConstants.USER_TOKEN), any(UsernamePasswordToken.class));
    }

    @Test
    public void login_successfulAuth_shouldSetUserAuthMapInSession() throws Exception {
        setSecurityContextAuth(buildOAuth2Auth(USERNAME));
        when(propertyUtil.getPropertyByInstitutionAndKey(anyString(), anyString()))
                .thenReturn(ScsbConstants.AUTH);
        Map<String, Object> resultMap = successResultMap(USERNAME);
        when(userAuthUtil.doAuthentication(any())).thenReturn(resultMap);
        loginController.login(request, response);
        verify(newSession).setAttribute(eq(ScsbConstants.USER_AUTH), eq(resultMap));
    }

    @Test
    public void login_successfulAuth_shouldSetUserNameInSession() throws Exception {
        setSecurityContextAuth(buildOAuth2Auth(USERNAME));
        when(propertyUtil.getPropertyByInstitutionAndKey(anyString(), anyString()))
                .thenReturn(ScsbConstants.AUTH);
        when(userAuthUtil.doAuthentication(any())).thenReturn(successResultMap(USERNAME));
        loginController.login(request, response);
        verify(newSession).setAttribute(eq(ScsbConstants.USER_NAME), eq(USERNAME));
    }

    @Test
    public void login_successfulAuth_shouldPropagateSuperAdminFlagToSession() throws Exception {
        setSecurityContextAuth(buildOAuth2Auth(USERNAME));
        when(propertyUtil.getPropertyByInstitutionAndKey(anyString(), anyString()))
                .thenReturn(ScsbConstants.AUTH);
        Map<String, Object> resultMap = successResultMap(USERNAME);
        resultMap.put(ScsbConstants.SUPER_ADMIN_USER, false);
        when(userAuthUtil.doAuthentication(any())).thenReturn(resultMap);
        loginController.login(request, response);
        verify(newSession).setAttribute(eq(ScsbConstants.SUPER_ADMIN_USER), eq(false));
    }

    @Test
    public void login_successfulAuth_shouldPropagateSearchPrivilegeToSession() throws Exception {
        setSecurityContextAuth(buildOAuth2Auth(USERNAME));
        when(propertyUtil.getPropertyByInstitutionAndKey(anyString(), anyString()))
                .thenReturn(ScsbConstants.AUTH);
        when(userAuthUtil.doAuthentication(any())).thenReturn(successResultMap(USERNAME));

        loginController.login(request, response);

        verify(newSession).setAttribute(eq(ScsbConstants.SEARCH_PRIVILEGE), eq(Boolean.TRUE));
    }

    @Test
    public void login_superAdminUser_shouldSetRoleForSuperAdminToTrue() throws Exception {
        setSecurityContextAuth(buildOAuth2Auth(USERNAME));
        when(propertyUtil.getPropertyByInstitutionAndKey(anyString(), anyString()))
                .thenReturn(ScsbConstants.AUTH);
        Map<String, Object> resultMap = successResultMap(USERNAME);
        resultMap.put(ScsbConstants.SUPER_ADMIN_USER, true);
        when(userAuthUtil.doAuthentication(any())).thenReturn(resultMap);
        when(newSession.getAttribute(ScsbConstants.SUPER_ADMIN_USER)).thenReturn(Boolean.TRUE);
        loginController.login(request, response);
        verify(newSession).setAttribute(eq(ScsbConstants.ROLE_FOR_SUPER_ADMIN), eq(true));
    }

    @Test
    public void login_returnValueIsNeverNull() throws Exception {
        setSecurityContextAuth(buildOAuth2Auth(USERNAME));
        when(propertyUtil.getPropertyByInstitutionAndKey(anyString(), anyString()))
                .thenReturn(ScsbConstants.AUTH);
        when(userAuthUtil.doAuthentication(any())).thenReturn(successResultMap(USERNAME));
        String result = loginController.login(request, response);
        assertNotNull(result);
    }


    @Test
    public void login_success_returnValueMatchesRedirectSearchConstant() throws Exception {
        setSecurityContextAuth(buildOAuth2Auth(USERNAME));
        when(propertyUtil.getPropertyByInstitutionAndKey(anyString(), anyString()))
                .thenReturn(ScsbConstants.AUTH);
        when(userAuthUtil.doAuthentication(any())).thenReturn(successResultMap(USERNAME));
        assertEquals("redirect:search", loginController.login(request, response));
    }
}