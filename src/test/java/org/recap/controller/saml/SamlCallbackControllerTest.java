package org.recap.controller.saml;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.recap.ScsbConstants;
import org.recap.model.jpa.UsersEntity;
import org.recap.model.saml.SamlConfig;
import org.recap.repository.jpa.UserDetailsRepository;
import org.recap.security.UserInstitutionCache;
import org.recap.security.saml.SamlAuthHandler;
import org.recap.util.PropertyUtil;
import org.recap.util.UserAuthUtil;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link SamlCallbackController}.
 * Groups:
 * 1. initiateSaml          ? GET /auth/saml
 * 2. samlAcs               ? POST /saml/acs
 * 3. spMetadataWithCode    ? GET /saml/metadata/{institutionCode}
 * 4. spMetadataFromSession ? GET /saml/metadata
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class SamlCallbackControllerTest {

    @InjectMocks
    private SamlCallbackController controller;
    @Mock
    private SamlAuthHandler samlAuthHandler;
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
    private HttpSession session;

    private static final String INST = "TEST_INST";
    private static final String IDP_SSO_URL = "https://idp.example.com/sso";
    private static final String ACS_URL = "https://sp.example.com/saml/acs";
    private static final String SP_ENTITY_ID = "https://sp.example.com/saml/metadata";
    private static final String IDP_CERT = "MIIC...dummyCert";
    private static final String SP_CERT = "MIIC...dummySpCert";
    private static final String SAML_RESP_B64 = "PHNhbWxwOlJlc3BvbnNlLz4="; // placeholder

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        // Inject UserAuthUtil into the inherited AbstractController field
        ReflectionTestUtils.setField(controller, "userAuthUtil", userAuthUtil);
        ReflectionTestUtils.setField(controller, "userDetailsRepository", userDetailsRepository);

        // Default HTTP request/session stubs
        when(request.getSession(false)).thenReturn(session);
        when(request.getSession(true)).thenReturn(session);
        when(session.getId()).thenReturn("mock-session-id");

        // Clear Spring Security context before each test
        SecurityContextHolder.clearContext();
    }


    /**
     * Stubs PropertyUtil so that every key for the given institution returns
     * the corresponding test value. Call this before any controller method
     * that triggers buildSamlConfig().
     */
    private void stubProperties(boolean withAcs) {
        lenient().when(propertyUtil.getPropertyByInstitutionAndKey(INST, ScsbConstants.IDP_SSO_URL))
                .thenReturn(IDP_SSO_URL);
        lenient().when(propertyUtil.getPropertyByInstitutionAndKey(INST, ScsbConstants.SP_ENTITY_ID))
                .thenReturn(SP_ENTITY_ID);
        lenient().when(propertyUtil.getPropertyByInstitutionAndKey(INST, ScsbConstants.IDP_CERTIFICATE))
                .thenReturn(IDP_CERT);
        lenient().when(propertyUtil.getPropertyByInstitutionAndKey(INST, ScsbConstants.SP_CERTIFICATE))
                .thenReturn(SP_CERT);
        lenient().when(propertyUtil.getPropertyByInstitutionAndKey(INST, ScsbConstants.ACS_URL))
                .thenReturn(withAcs ? ACS_URL : null);
        lenient().when(propertyUtil.getPropertyByInstitutionAndKey(INST, ScsbConstants.SP_PRIVATE_KEY))
                .thenReturn(null);
    }

    /**
     * Builds a minimal SamlUserInfo (uses the public constructor of the inner class).
     */
    private SamlAuthHandler.SamlUserInfo buildUserInfo(String userId) {
        return new SamlAuthHandler.SamlUserInfo(userId, "Display Name", userId + "@example.com", userId, INST);
    }

    /**
     * Creates a minimal resultMap that represents a successful Shiro authentication.
     */
    private Map<String, Object> successAuthMap(String userId) {
        Map<String, Object> map = new HashMap<>();
        map.put(ScsbConstants.IS_USER_AUTHENTICATED, Boolean.TRUE);
        map.put(ScsbConstants.USER_NAME, userId);
        map.put(ScsbConstants.USER_ID, 1);
        map.put(ScsbConstants.USER_INSTITUTION, INST);
        map.put(ScsbConstants.SUPER_ADMIN_USER, false);
        map.put(ScsbConstants.USER_ADMINISTRATOR, false);
        map.put(ScsbConstants.REPOSITORY, false);
        map.put(ScsbConstants.REQUEST_PRIVILEGE, false);
        map.put(ScsbConstants.COLLECTION_PRIVILEGE, false);
        map.put(ScsbConstants.REPORTS_PRIVILEGE, false);
        map.put(ScsbConstants.SEARCH_PRIVILEGE, true);
        map.put(ScsbConstants.USER_ROLE_PRIVILEGE, false);
        map.put(ScsbConstants.REQUEST_ALL_PRIVILEGE, false);
        map.put(ScsbConstants.REQUEST_ITEM_PRIVILEGE, false);
        map.put(ScsbConstants.BARCODE_RESTRICTED_PRIVILEGE, false);
        map.put(ScsbConstants.DEACCESSION_PRIVILEGE, false);
        map.put("bulkRequestPrivilege", false);
        map.put("resubmitRequestPrivilege", false);
        map.put(ScsbConstants.MONITORING, false);
        map.put(ScsbConstants.LOGGING, false);
        map.put(ScsbConstants.REQUESTLOG, false);
        map.put(ScsbConstants.DATA_EXPORT, false);
        return map;
    }


    @Test
    public void initiateSaml_whenConfigured_shouldRedirectToIdp() {
        stubProperties(true);
        when(samlAuthHandler.buildAuthnRequestUrl(any(SamlConfig.class), anyString(), anyString()))
                .thenReturn(IDP_SSO_URL + "?SAMLRequest=abc123&RelayState=inst%3DTEST_INST");

        String result = controller.initiateSaml(INST, request);

        assertTrue("Should redirect", result.startsWith("redirect:"));
        assertTrue("Should redirect to IdP", result.contains(IDP_SSO_URL));
    }

    @Test
    public void initiateSaml_whenIdpSsoUrlMissing_shouldRedirectToErrorPage() {
        // idp.sso.url returns null ? config.idpSsoUrl is blank ? error redirect
        lenient().when(propertyUtil.getPropertyByInstitutionAndKey(INST, ScsbConstants.IDP_SSO_URL))
                .thenReturn(null);
        lenient().when(propertyUtil.getPropertyByInstitutionAndKey(anyString(), anyString()))
                .thenReturn(null);

        String result = controller.initiateSaml(INST, request);

        assertEquals("redirect:/?error=saml_config_missing", result);
    }

    @Test
    public void initiateSaml_shouldStoreInstitutionCodeInSession() {
        stubProperties(true);
        when(samlAuthHandler.buildAuthnRequestUrl(any(), anyString(), anyString()))
                .thenReturn(IDP_SSO_URL + "?SAMLRequest=x");

        controller.initiateSaml(INST, request);

        verify(session).setAttribute(eq(ScsbConstants.SAML_INSTITUTION_CODE), eq(INST));
    }

    @Test
    public void initiateSaml_shouldRegisterSessionInUserInstitutionCache() {
        stubProperties(true);
        when(samlAuthHandler.buildAuthnRequestUrl(any(), anyString(), anyString()))
                .thenReturn(IDP_SSO_URL + "?SAMLRequest=x");

        controller.initiateSaml(INST, request);

        verify(userInstitutionCache).addRequestSessionId(eq("mock-session-id"), eq(INST));
    }

    @Test
    public void initiateSaml_withAcsUrlFromConfig_shouldPassConfiguredAcsUrlToHandler() {
        stubProperties(true);
        ArgumentCaptor<SamlConfig> configCaptor = ArgumentCaptor.forClass(SamlConfig.class);
        when(samlAuthHandler.buildAuthnRequestUrl(configCaptor.capture(), anyString(), anyString()))
                .thenReturn(IDP_SSO_URL + "?SAMLRequest=x");

        controller.initiateSaml(INST, request);

        assertEquals(ACS_URL, configCaptor.getValue().getAcsUrl());
    }

    @Test
    public void initiateSaml_whenPropertyUtilThrows_shouldRedirectToErrorPage() {
        when(propertyUtil.getPropertyByInstitutionAndKey(INST, ScsbConstants.IDP_SSO_URL))
                .thenThrow(new RuntimeException("DB error"));

        String result = controller.initiateSaml(INST, request);

        assertEquals("redirect:/?error=saml_config_missing", result);
    }


    @Test
    public void samlAcs_successPath_shouldRedirectToSearch() throws Exception {
        stubProperties(true);
        String userId = "testuser";
        SamlAuthHandler.SamlUserInfo userInfo = buildUserInfo(userId);
        UsersEntity usersEntity = new UsersEntity();
        usersEntity.setLoginId(userId);
        usersEntity.setUserDescription("Test User");

        when(session.getAttribute(ScsbConstants.SAML_INSTITUTION_CODE)).thenReturn(INST);
        when(samlAuthHandler.processSamlResponse(anyString(), any(SamlConfig.class))).thenReturn(userInfo);
        when(userDetailsRepository.findByLoginId(userId)).thenReturn(usersEntity);
        when(userAuthUtil.doAuthentication(any())).thenReturn(successAuthMap(userId));

        String result = controller.samlAcs(SAML_RESP_B64, "inst%3DTEST_INST", request);

        assertEquals("redirect:/search", result);
    }

    @Test
    public void samlAcs_whenInstitutionCodeNotInSessionOrRelayState_shouldRedirectToSessionExpired() throws Exception {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute(ScsbConstants.SAML_INSTITUTION_CODE)).thenReturn(null);

        // relayState is null and session has no institution
        String result = controller.samlAcs(SAML_RESP_B64, null, request);

        assertEquals("redirect:/?error=saml_session_expired", result);
    }

    @Test
    public void samlAcs_whenInstitutionFromRelayState_shouldProceed() throws Exception {
        stubProperties(true);
        // Session doesn't have the institution; it comes from RelayState
        HttpSession newSession = mock(HttpSession.class);
        when(newSession.getId()).thenReturn("new-session-id");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute(ScsbConstants.SAML_INSTITUTION_CODE)).thenReturn(null);
        when(request.getSession(true)).thenReturn(newSession);

        String userId = "relayuser";
        SamlAuthHandler.SamlUserInfo userInfo = buildUserInfo(userId);
        UsersEntity usersEntity = new UsersEntity();
        usersEntity.setLoginId(userId);
        usersEntity.setUserDescription("Relay User");

        when(samlAuthHandler.processSamlResponse(anyString(), any(SamlConfig.class))).thenReturn(userInfo);
        when(userDetailsRepository.findByLoginId(userId)).thenReturn(usersEntity);
        when(userAuthUtil.doAuthentication(any())).thenReturn(successAuthMap(userId));

        String result = controller.samlAcs(SAML_RESP_B64, "inst%3DTEST_INST", request);

        assertEquals("redirect:/search", result);
    }

    @Test
    public void samlAcs_whenIdpSsoUrlMissing_shouldRedirectToConfigMissing() throws Exception {
        when(session.getAttribute(ScsbConstants.SAML_INSTITUTION_CODE)).thenReturn(INST);
        // All properties return null ? config has no idpSsoUrl
        lenient().when(propertyUtil.getPropertyByInstitutionAndKey(anyString(), anyString())).thenReturn(null);

        String result = controller.samlAcs(SAML_RESP_B64, null, request);

        assertEquals("redirect:/?error=saml_config_missing", result);
    }

    @Test
    public void samlAcs_whenIdpCertificateMissing_shouldRedirectToConfigMissing() throws Exception {
        when(session.getAttribute(ScsbConstants.SAML_INSTITUTION_CODE)).thenReturn(INST);
        when(propertyUtil.getPropertyByInstitutionAndKey(INST, ScsbConstants.IDP_SSO_URL)).thenReturn(IDP_SSO_URL);
        when(propertyUtil.getPropertyByInstitutionAndKey(INST, ScsbConstants.IDP_CERTIFICATE)).thenReturn(null);
        lenient().when(propertyUtil.getPropertyByInstitutionAndKey(anyString(), anyString())).thenReturn(null);

        String result = controller.samlAcs(SAML_RESP_B64, null, request);

        assertEquals("redirect:/?error=saml_config_missing", result);
    }

    @Test
    public void samlAcs_whenProcessSamlResponseReturnsNull_shouldRedirectToAuthFailed() throws Exception {
        stubProperties(true);
        when(session.getAttribute(ScsbConstants.SAML_INSTITUTION_CODE)).thenReturn(INST);
        when(samlAuthHandler.processSamlResponse(anyString(), any(SamlConfig.class))).thenReturn(null);

        String result = controller.samlAcs(SAML_RESP_B64, null, request);

        assertEquals("redirect:/home?error=saml_auth_failed", result);
    }

    @Test
    public void samlAcs_whenUserNotInDatabase_shouldRedirectToUserNotFound() throws Exception {
        stubProperties(true);
        String userId = "unknownuser";
        when(session.getAttribute(ScsbConstants.SAML_INSTITUTION_CODE)).thenReturn(INST);
        when(samlAuthHandler.processSamlResponse(anyString(), any(SamlConfig.class)))
                .thenReturn(buildUserInfo(userId));
        when(userDetailsRepository.findByLoginId(userId)).thenReturn(null);

        String result = controller.samlAcs(SAML_RESP_B64, null, request);

        assertEquals("redirect:/home?error=saml_user_not_found", result);
    }

    @Test
    public void samlAcs_whenShiroDoAuthenticationThrows_shouldRedirectToAuthFailed() throws Exception {
        stubProperties(true);
        String userId = "throwuser";
        UsersEntity usersEntity = new UsersEntity();
        usersEntity.setLoginId(userId);

        when(session.getAttribute(ScsbConstants.SAML_INSTITUTION_CODE)).thenReturn(INST);
        when(samlAuthHandler.processSamlResponse(anyString(), any(SamlConfig.class)))
                .thenReturn(buildUserInfo(userId));
        when(userDetailsRepository.findByLoginId(userId)).thenReturn(usersEntity);
        when(userAuthUtil.doAuthentication(any())).thenThrow(new RuntimeException("Shiro boom"));

        String result = controller.samlAcs(SAML_RESP_B64, null, request);

        assertEquals("redirect:/home?error=saml_auth_failed", result);
    }

    @Test
    public void samlAcs_whenShiroReturnsNullMap_shouldRedirectToAuthFailed() throws Exception {
        stubProperties(true);
        String userId = "nullmapuser";
        UsersEntity usersEntity = new UsersEntity();
        usersEntity.setLoginId(userId);

        when(session.getAttribute(ScsbConstants.SAML_INSTITUTION_CODE)).thenReturn(INST);
        when(samlAuthHandler.processSamlResponse(anyString(), any(SamlConfig.class)))
                .thenReturn(buildUserInfo(userId));
        when(userDetailsRepository.findByLoginId(userId)).thenReturn(usersEntity);
        when(userAuthUtil.doAuthentication(any())).thenReturn(null);

        String result = controller.samlAcs(SAML_RESP_B64, null, request);

        assertEquals("redirect:/home?error=saml_auth_failed", result);
    }

    @Test
    public void samlAcs_whenShiroReturnsFalseAuthenticated_shouldRedirectToAuthFailed() throws Exception {
        stubProperties(true);
        String userId = "denieduser";
        UsersEntity usersEntity = new UsersEntity();
        usersEntity.setLoginId(userId);

        Map<String, Object> failMap = new HashMap<>();
        failMap.put(ScsbConstants.IS_USER_AUTHENTICATED, Boolean.FALSE);
        failMap.put(ScsbConstants.USER_AUTH_ERRORMSG, "Account locked");

        when(session.getAttribute(ScsbConstants.SAML_INSTITUTION_CODE)).thenReturn(INST);
        when(samlAuthHandler.processSamlResponse(anyString(), any(SamlConfig.class)))
                .thenReturn(buildUserInfo(userId));
        when(userDetailsRepository.findByLoginId(userId)).thenReturn(usersEntity);
        when(userAuthUtil.doAuthentication(any())).thenReturn(failMap);

        String result = controller.samlAcs(SAML_RESP_B64, null, request);

        assertEquals("redirect:/home?error=saml_auth_failed", result);
    }

    @Test
    public void samlAcs_successPath_shouldSetSamlAuthenticatedInSession() throws Exception {
        stubProperties(true);
        String userId = "checkuser";
        UsersEntity usersEntity = new UsersEntity();
        usersEntity.setLoginId(userId);
        usersEntity.setUserDescription("Check User");

        when(session.getAttribute(ScsbConstants.SAML_INSTITUTION_CODE)).thenReturn(INST);
        when(samlAuthHandler.processSamlResponse(anyString(), any(SamlConfig.class)))
                .thenReturn(buildUserInfo(userId));
        when(userDetailsRepository.findByLoginId(userId)).thenReturn(usersEntity);
        when(userAuthUtil.doAuthentication(any())).thenReturn(successAuthMap(userId));

        controller.samlAcs(SAML_RESP_B64, null, request);

        verify(session, atLeastOnce()).setAttribute(eq(ScsbConstants.SAML_AUTHENTICATED), eq(Boolean.TRUE));
    }

    @Test
    public void samlAcs_successPath_shouldSetLoggedInInstitutionInSession() throws Exception {
        stubProperties(true);
        String userId = "instuser";
        UsersEntity usersEntity = new UsersEntity();
        usersEntity.setLoginId(userId);
        usersEntity.setUserDescription("Inst User");

        when(session.getAttribute(ScsbConstants.SAML_INSTITUTION_CODE)).thenReturn(INST);
        when(samlAuthHandler.processSamlResponse(anyString(), any(SamlConfig.class)))
                .thenReturn(buildUserInfo(userId));
        when(userDetailsRepository.findByLoginId(userId)).thenReturn(usersEntity);
        when(userAuthUtil.doAuthentication(any())).thenReturn(successAuthMap(userId));

        controller.samlAcs(SAML_RESP_B64, null, request);

        verify(session, atLeastOnce()).setAttribute(eq(ScsbConstants.LOGGED_IN_INSTITUTION), eq(INST));
    }

    @Test
    public void samlAcs_successPath_shouldInvalidateOldSessionForSessionFixation() throws Exception {
        stubProperties(true);
        String userId = "fixuser";
        HttpSession freshSession = mock(HttpSession.class);
        when(freshSession.getId()).thenReturn("fresh-session-id");

        when(session.getAttribute(ScsbConstants.SAML_INSTITUTION_CODE)).thenReturn(INST);
        when(request.getSession(false)).thenReturn(session);
        when(request.getSession(true)).thenReturn(freshSession);
        when(userInstitutionCache.getInstitutionForRequestSessionId("mock-session-id")).thenReturn(INST);

        UsersEntity usersEntity = new UsersEntity();
        usersEntity.setLoginId(userId);
        usersEntity.setUserDescription("Fix User");

        when(samlAuthHandler.processSamlResponse(anyString(), any(SamlConfig.class)))
                .thenReturn(buildUserInfo(userId));
        when(userDetailsRepository.findByLoginId(userId)).thenReturn(usersEntity);
        when(userAuthUtil.doAuthentication(any())).thenReturn(successAuthMap(userId));

        controller.samlAcs(SAML_RESP_B64, null, request);

        verify(session).invalidate();
    }

    @Test
    public void samlAcs_whenNoExistingSession_shouldCreateNewSession() throws Exception {
        stubProperties(true);
        String userId = "nosessionuser";
        HttpSession newSession = mock(HttpSession.class);
        when(newSession.getId()).thenReturn("new-session-id");

        // First call (getSession(false) in resolveInstitution) returns null
        when(request.getSession(false)).thenReturn(null);
        // RelayState provides institution
        when(request.getSession(true)).thenReturn(newSession);
        when(newSession.getAttribute(ScsbConstants.SAML_INSTITUTION_CODE)).thenReturn(null);

        UsersEntity usersEntity = new UsersEntity();
        usersEntity.setLoginId(userId);
        usersEntity.setUserDescription("No Session User");

        when(samlAuthHandler.processSamlResponse(anyString(), any(SamlConfig.class)))
                .thenReturn(buildUserInfo(userId));
        when(userDetailsRepository.findByLoginId(userId)).thenReturn(usersEntity);
        when(userAuthUtil.doAuthentication(any())).thenReturn(successAuthMap(userId));

        String result = controller.samlAcs(SAML_RESP_B64, "inst%3DTEST_INST", request);

        assertEquals("redirect:/search", result);
        verify(userInstitutionCache, atLeastOnce()).addRequestSessionId(anyString(), eq(INST));
    }

    @Test
    public void spMetadataWithCode_whenConfigured_shouldReturnXml() {
        stubProperties(true);
        String expectedXml = "<md:EntityDescriptor entityID=\"" + SP_ENTITY_ID + "\"/>";
        when(samlAuthHandler.generateSpMetadata(any(SamlConfig.class))).thenReturn(expectedXml);

        String result = controller.spMetadataWithCode(INST);

        assertEquals(expectedXml, result);
    }

    @Test
    public void spMetadataWithCode_whenIdpSsoUrlMissing_shouldReturnCommentXml() {
        lenient().when(propertyUtil.getPropertyByInstitutionAndKey(anyString(), anyString())).thenReturn(null);

        String result = controller.spMetadataWithCode(INST);

        assertTrue("Expected an XML comment indicating no configuration",
                result.startsWith("<!-- No SAML configuration found"));
        assertTrue(result.contains(INST));
    }

    @Test
    public void spMetadataWithCode_shouldInvokeGenerateSpMetadataWithCorrectInstitution() {
        stubProperties(true);
        ArgumentCaptor<SamlConfig> configCaptor = ArgumentCaptor.forClass(SamlConfig.class);
        when(samlAuthHandler.generateSpMetadata(configCaptor.capture()))
                .thenReturn("<md:EntityDescriptor/>");

        controller.spMetadataWithCode(INST);

        assertEquals(INST, configCaptor.getValue().getInstitutionCode());
    }


    @Test
    public void spMetadataFromSession_withInstitutionInSession_shouldReturnXml() {
        stubProperties(true);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute(ScsbConstants.SAML_INSTITUTION_CODE)).thenReturn(INST);
        String expectedXml = "<md:EntityDescriptor/>";
        when(samlAuthHandler.generateSpMetadata(any(SamlConfig.class))).thenReturn(expectedXml);

        String result = controller.spMetadataFromSession(request);

        assertEquals(expectedXml, result);
    }

    @Test
    public void spMetadataFromSession_whenNoSession_shouldReturnErrorComment() {
        when(request.getSession(false)).thenReturn(null);

        String result = controller.spMetadataFromSession(request);

        assertTrue("Expected an error comment when no session exists",
                result.contains("ERROR"));
        assertTrue(result.contains("institutionCode"));
    }

    @Test
    public void spMetadataFromSession_whenSessionHasNoInstitutionCode_shouldReturnErrorComment() {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute(ScsbConstants.SAML_INSTITUTION_CODE)).thenReturn(null);

        String result = controller.spMetadataFromSession(request);

        assertTrue("Expected error comment when institution code is absent",
                result.contains("ERROR"));
    }

    @Test
    public void spMetadataFromSession_whenSessionHasBlankInstitutionCode_shouldReturnErrorComment() {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute(ScsbConstants.SAML_INSTITUTION_CODE)).thenReturn("   ");

        String result = controller.spMetadataFromSession(request);

        assertTrue("Expected error comment for blank institution code",
                result.contains("ERROR"));
    }
}